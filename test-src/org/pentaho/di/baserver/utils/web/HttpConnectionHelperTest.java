/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License, version 2 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/gpl-2.0.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 *
 * Copyright 2006 - 2015 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.di.baserver.utils.web;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.junit.Before;
import org.junit.Test;

import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.web.servlet.JAXRSPluginServlet;
import org.springframework.beans.factory.ListableBeanFactory;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HostConfiguration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class HttpConnectionHelperTest {
  HttpConnectionHelper httpConnectionHelper, httpConnectionHelperSpy;

  @Before 
  public void setUp() throws Exception {
    httpConnectionHelper = HttpConnectionHelper.getInstance();
    httpConnectionHelperSpy = spy( httpConnectionHelper );
  }

  @Test 
  public void testInvokeEndpoint() throws Exception {
    String serverUrl = "http://localhost:8080/pentaho", userName = "admin", password = "password", moduleName = "platform",
        endpointPath = "myEndpoint", httpMethod = "GET";
    Map<String, String> queryParameters = new HashMap<String, String>();
    queryParameters.put( "param1", "value1" );
    queryParameters.put( "param2", "value2" );
    queryParameters.put( "param3", "value3" );
    
    Response r = mock( Response.class );
    
    doReturn( r ).when( httpConnectionHelperSpy ).callHttp( anyString(), anyString(), anyString() );
    httpConnectionHelperSpy.invokeEndpoint( serverUrl, userName, password, moduleName, endpointPath,
        queryParameters );

    serverUrl = "http://localhost:8080/pentaho/";
    endpointPath = "/myEndpoint";
    httpConnectionHelperSpy.invokeEndpoint( serverUrl, userName, password, moduleName, endpointPath,
        queryParameters );
    verify( httpConnectionHelperSpy, times( 2 ) ).callHttp( "http://localhost:8080/pentaho/api/myEndpoint?param1=value1&param2=value2&param3=value3", userName, password );
    
    moduleName = "data-access";
    httpConnectionHelperSpy.invokeEndpoint( serverUrl, userName, password, moduleName, endpointPath,
        queryParameters );
    verify( httpConnectionHelperSpy ).callHttp(
        "http://localhost:8080/pentaho/plugin/data-access/api/myEndpoint?param1=value1&param2=value2&param3=value3",
        userName, password );
    
  }

  @Test 
  public void testInvokeEndpoint1() throws Exception {
    String moduleName = "platform", endpointPath = "myEndpoint", httpMethod = "GET";
    Map<String, String> queryParameters = new HashMap<String, String>();
    queryParameters.put( "param1", "value1" );
    queryParameters.put( "param2", "value2" );
    queryParameters.put( "param3", "value3" );
    
    Response r = mock( Response.class );
    
    doReturn( r ).when( httpConnectionHelperSpy ).invokePlatformEndpoint( anyString(),anyString(), any( Map.class) );
    doReturn( r ).when( httpConnectionHelperSpy ).invokePluginEndpoint( anyString(), anyString(), anyString(),
        any( Map.class ) );
    
    httpConnectionHelperSpy.invokeEndpoint( moduleName, endpointPath, httpMethod, queryParameters );
    verify( httpConnectionHelperSpy, times( 1 ) ).invokePlatformEndpoint( endpointPath, httpMethod, queryParameters );
    verify( httpConnectionHelperSpy, times( 0 ) ).invokePluginEndpoint( moduleName, endpointPath, httpMethod, queryParameters );

    moduleName = "myModule";
    httpConnectionHelperSpy.invokeEndpoint( moduleName, endpointPath, httpMethod, queryParameters );
    verify( httpConnectionHelperSpy, times( 1 ) ).invokePlatformEndpoint( endpointPath, httpMethod, queryParameters );
    verify( httpConnectionHelperSpy, times( 1 ) ).invokePluginEndpoint( moduleName, endpointPath, httpMethod,
        queryParameters );
  }

  @Test 
  public void testInvokePlatformEndpoint() throws Exception {
    Response r;
    
    String endpointPath = "myEndpoint", httpMethod = "GET";
    Map<String, String> queryParameters = new HashMap<String, String>();
    queryParameters.put( "param1", "value1" );
    queryParameters.put( "param2", "value2" );
    queryParameters.put( "param3", "value3" );

    RequestDispatcher requestDispatcher = mock( RequestDispatcher.class );
    ServletContext context = mock( ServletContext.class );
    doThrow( new NoClassDefFoundError(  ) ).when( httpConnectionHelperSpy ).getContext();
    r = httpConnectionHelperSpy.invokePlatformEndpoint( endpointPath, httpMethod, queryParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    doReturn( context ).when( httpConnectionHelperSpy ).getContext();
    doReturn( requestDispatcher ).when( context ).getRequestDispatcher( "/api" + endpointPath );
    doThrow( new MalformedURLException() ).when( httpConnectionHelperSpy ).getUrl();
    r = httpConnectionHelperSpy.invokePlatformEndpoint( endpointPath, httpMethod, queryParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    String serverUrl = "http://localhost:8080/pentaho";
    URL url = new URL( serverUrl );
    doReturn( url ).when( httpConnectionHelperSpy ).getUrl();
    doThrow( new ServletException() ).when( requestDispatcher ).forward( any( InternalHttpServletRequest.class ),
        any( InternalHttpServletResponse.class ) );
    r = httpConnectionHelperSpy.invokePlatformEndpoint( endpointPath, httpMethod, queryParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    doThrow( new IOException() ).when( requestDispatcher ).forward( any( InternalHttpServletRequest.class ),
        any( InternalHttpServletResponse.class ) );
    r = httpConnectionHelperSpy.invokePlatformEndpoint( endpointPath, httpMethod, queryParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    doNothing().when( requestDispatcher ).forward( any( InternalHttpServletRequest.class ),
        any( InternalHttpServletResponse.class ) );
    r = httpConnectionHelperSpy.invokePlatformEndpoint( endpointPath, httpMethod, queryParameters );
    assertEquals( r.getStatusCode(), 204 );
    
  }
  
  @Test
  public void teatInsertParameters() throws Exception {
    String httpMethod = "GET";
    Map<String, String> queryParameters = new HashMap<String, String>();
    queryParameters.put( "param1", "value1|" );
    queryParameters.put( "param2", "value2\\/" );
    queryParameters.put( "param3", "value3{}" );
    
    InternalHttpServletRequest request = new InternalHttpServletRequest( "", "" );
    
    httpConnectionHelperSpy.insertParameters( httpMethod, queryParameters, request );
    assertEquals( request.getParameterMap().size(), 3 );
    assertEquals( URLDecoder.decode( request.getParameter( "param1" ), HttpConnectionHelper.UTF_8 ),
        queryParameters.get( "param1" ) );
    assertEquals( URLDecoder.decode( request.getParameter( "param2" ), HttpConnectionHelper.UTF_8 ),
        queryParameters.get( "param2" ) );
    assertEquals( URLDecoder.decode( request.getParameter( "param3" ), HttpConnectionHelper.UTF_8 ),
        queryParameters.get( "param3" ) );
    
    httpMethod = "PUT";
    request = new InternalHttpServletRequest( "", "" );
    httpConnectionHelperSpy.insertParameters( httpMethod, queryParameters, request );
    assertEquals( request.getContentType(), "application/x-www-form-urlencoded" );
    assertEquals( new String( request.getContent() ), "param1=value1%7C&param2=value2%5C%2F&param3=value3%7B%7D" );

    httpMethod = "POST";
    request = new InternalHttpServletRequest( "", "" );
    httpConnectionHelperSpy.insertParameters( httpMethod, queryParameters, request );
    assertEquals( request.getContentType(), "application/x-www-form-urlencoded" );
    assertEquals( new String( request.getContent() ), "param1=value1%7C&param2=value2%5C%2F&param3=value3%7B%7D" );

    httpMethod = "DELETE";
    request = new InternalHttpServletRequest( "", "" );
    httpConnectionHelperSpy.insertParameters( httpMethod, queryParameters, request );
    assertEquals( request.getContentType(), "application/x-www-form-urlencoded" );
    assertEquals( new String( request.getContent() ), "param1=value1%7C&param2=value2%5C%2F&param3=value3%7B%7D" );
  }

  @Test 
  public void testInvokePluginEndpoint() throws Exception {
    Response r;
    
    String pluginName = "platform", endpointPath = "myEndpoint", httpMethod = "GET";
    Map<String, String> queryParameters = new HashMap<String, String>();
    queryParameters.put( "param1", "value1" );
    queryParameters.put( "param2", "value2" );
    queryParameters.put( "param3", "value3" );
    
    
    doReturn( null ).when( httpConnectionHelperSpy ).getPluginManager();
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, queryParameters );
    assertEquals( r.getResult(), ( new Response() ).getResult() );

    IPluginManager pluginManager = mock( IPluginManager.class );
    doReturn( pluginManager ).when( httpConnectionHelperSpy ).getPluginManager();
    doReturn( null ).when( httpConnectionHelperSpy ).getPluginClassLoader( pluginName, pluginManager );
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, queryParameters );
    assertEquals( r.getResult(), ( new Response() ).getResult() );

    ClassLoader pluginClassLoader = mock( ClassLoader.class );
    doReturn( pluginClassLoader ).when( httpConnectionHelperSpy ).getPluginClassLoader( pluginName, pluginManager );
    doReturn( null ).when( httpConnectionHelperSpy ).getListableBeanFactory( pluginName, pluginManager );
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, queryParameters );
    assertEquals( r.getResult(), ( new Response() ).getResult() );

    ListableBeanFactory beanFactory = mock( ListableBeanFactory.class );
    doReturn( beanFactory ).when( httpConnectionHelperSpy ).getListableBeanFactory( pluginName, pluginManager );
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, queryParameters );
    assertEquals( r.getResult(), ( new Response() ).getResult() );

    JAXRSPluginServlet pluginServlet = mock( JAXRSPluginServlet.class );
    doReturn( pluginServlet ).when( httpConnectionHelperSpy ).getJAXRSPluginServlet( beanFactory );
    doThrow( new MalformedURLException() ).when( httpConnectionHelperSpy ).getUrl();
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, queryParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );
    
    String serverUrl = "http://localhost:8080/pentaho";
    URL url = new URL( serverUrl );
    doReturn( url ).when( httpConnectionHelperSpy ).getUrl();
    doThrow( new ServletException() ).when( pluginServlet ).service( any( InternalHttpServletRequest.class ),
        any( InternalHttpServletResponse.class ) );
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, queryParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    doThrow( new IOException() ).when( pluginServlet ).service( any( InternalHttpServletRequest.class ),
        any( InternalHttpServletResponse.class ) );
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, queryParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    doNothing().when( pluginServlet ).service( any( InternalHttpServletRequest.class ),
        any( InternalHttpServletResponse.class ) );
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, queryParameters );
    assertEquals( r.getStatusCode(), new Response().getStatusCode() );
  }

  @Test 
  public void testCallHttp() throws Exception {
    String url = "http://localhost:8080/pentaho",
        user = "admin",
        password = "password";

    Response r;

    HttpClient httpClient = mock( HttpClient.class );
    HttpClientParams httpClientParams = mock( HttpClientParams.class );
    doNothing().when( httpClientParams ).setAuthenticationPreemptive( true );
    doReturn( httpClientParams ).when( httpClient ).getParams();
    HttpState httpState = mock( HttpState.class );
    Credentials credentials = mock( Credentials.class );
    doReturn( credentials ).when( httpConnectionHelperSpy ).getCredentials( user, password );
    doNothing().when( httpState ).setCredentials( AuthScope.ANY, credentials );
    doReturn( httpState ).when( httpClient ).getState();

    doReturn( httpClient ).when( httpConnectionHelperSpy ).getHttpClient();
    HttpMethod httpMethod = mock( HttpMethod.class );
    doReturn( httpMethod ).when( httpConnectionHelperSpy ).getHttpMethod( url );
    
    doThrow( new IllegalArgumentException() ).when( httpClient ).executeMethod( any( HostConfiguration.class ),
        eq( httpMethod ) );
    r = httpConnectionHelperSpy.callHttp( url, user, password );
    assertEquals( r.getStatusCode(), new Response().getStatusCode() );

    doReturn( 1 ).when( httpClient ).executeMethod( any( HostConfiguration.class ), eq( httpMethod ) );
    doReturn( null ).when( httpMethod ).getResponseHeaders( "Content-Type" );
    doReturn( "content" ).when( httpConnectionHelperSpy ).getContent( eq( httpMethod ), anyString() );
    r = httpConnectionHelperSpy.callHttp( url, user, password );
    assertEquals( r.getResult(), "content" );

    Header header = mock( Header.class );
    doReturn( null ).when( header ).getValue();
    r = httpConnectionHelperSpy.callHttp( url, user, password );
    assertEquals( r.getResult(), "content" );

    doReturn( "test" ).when( header ).getValue();
    r = httpConnectionHelperSpy.callHttp( url, user, password );
    assertEquals( r.getResult(), "content" );

    doReturn( "charset=utf8" ).when( header ).getValue();
    r = httpConnectionHelperSpy.callHttp( url, user, password );
    assertEquals( r.getResult(), "content" );
  }
}
