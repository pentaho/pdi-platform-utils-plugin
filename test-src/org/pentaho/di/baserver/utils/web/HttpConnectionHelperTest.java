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
 * Copyright 2006 - 2017 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.di.baserver.utils.web;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.web.servlet.JAXRSPluginServlet;
import org.springframework.beans.factory.ListableBeanFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.anyList;

public class HttpConnectionHelperTest {
  HttpConnectionHelper httpConnectionHelper, httpConnectionHelperSpy;

  @Before
  public void setUp() throws Exception {
    httpConnectionHelper = HttpConnectionHelper.getInstance();
    httpConnectionHelperSpy = spy( httpConnectionHelper );
  }

  @Test
  public void testInvokeEndpoint() throws Exception {
    String serverUrl = "http://localhost:8080/pentaho", userName = "admin", password = "password",
        moduleName = "platform", endpointPath = "myEndpoint", httpMethod = "GET";
    List<HttpParameter> httpParameters = new ArrayList<>();
    httpParameters.add( new HttpParameter( "param1", "value1" ) );
    httpParameters.add( new HttpParameter( "param2", "value2" ) );
    httpParameters.add( new HttpParameter( "param3", "value3" ) );

    Response r = mock( Response.class );

    doReturn( r ).when( httpConnectionHelperSpy ).callHttp( anyString(), anyList(), anyString(),
        anyString(), anyString() );
    httpConnectionHelperSpy.invokeEndpoint( serverUrl, userName, password, moduleName, endpointPath, httpMethod,
        httpParameters );

    serverUrl = "http://localhost:8080/pentaho/";
    endpointPath = "/myEndpoint";
    httpConnectionHelperSpy.invokeEndpoint( serverUrl, userName, password, moduleName, endpointPath, httpMethod,
        httpParameters );
    verify( httpConnectionHelperSpy, times( 2 ) ).callHttp(
        "http://localhost:8080/pentaho/api/myEndpoint", httpParameters, httpMethod, userName, password );

    moduleName = "data-access";
    httpConnectionHelperSpy.invokeEndpoint( serverUrl, userName, password, moduleName, endpointPath, httpMethod,
        httpParameters );
    verify( httpConnectionHelperSpy ).callHttp(
        "http://localhost:8080/pentaho/plugin/data-access/api/myEndpoint", httpParameters, httpMethod, userName,
        password );

  }

  @Test
  public void testInvokeEndpoint1() throws Exception {
    String moduleName = "platform", endpointPath = "myEndpoint", httpMethod = "GET";
    List<HttpParameter> httpParameters = new ArrayList<>();
    httpParameters.add( new HttpParameter( "param1", "value1" ) );
    httpParameters.add( new HttpParameter( "param2", "value2" ) );
    httpParameters.add( new HttpParameter( "param3", "value3" ) );

    Response r = mock( Response.class );

    doReturn( r ).when( httpConnectionHelperSpy ).invokePlatformEndpoint( anyString(), anyString(), any( List.class ) );
    doReturn( r ).when( httpConnectionHelperSpy ).invokePluginEndpoint( anyString(), anyString(), anyString(),
        any( List.class ) );

    httpConnectionHelperSpy.invokeEndpoint( moduleName, endpointPath, httpMethod, httpParameters );
    verify( httpConnectionHelperSpy, times( 1 ) ).invokePlatformEndpoint( endpointPath, httpMethod, httpParameters );
    verify( httpConnectionHelperSpy, times( 0 ) )
        .invokePluginEndpoint( moduleName, endpointPath, httpMethod, httpParameters );

    moduleName = "myModule";
    httpConnectionHelperSpy.invokeEndpoint( moduleName, endpointPath, httpMethod, httpParameters );
    verify( httpConnectionHelperSpy, times( 1 ) ).invokePlatformEndpoint( endpointPath, httpMethod, httpParameters );
    verify( httpConnectionHelperSpy, times( 1 ) ).invokePluginEndpoint( moduleName, endpointPath, httpMethod,
        httpParameters );
  }

  @Test
  public void testInvokePlatformEndpoint() throws Exception {
    Response r;

    String endpointPath = "myEndpoint", httpMethod = "GET";
    List<HttpParameter> httpParameters = new ArrayList<>();
    httpParameters.add( new HttpParameter( "param1", "value1" ) );
    httpParameters.add( new HttpParameter( "param2", "value2" ) );
    httpParameters.add( new HttpParameter( "param3", "value3" ) );

    RequestDispatcher requestDispatcher = mock( RequestDispatcher.class );
    ServletContext context = mock( ServletContext.class );
    doThrow( new NoClassDefFoundError() ).when( httpConnectionHelperSpy ).getContext();
    r = httpConnectionHelperSpy.invokePlatformEndpoint( endpointPath, httpMethod, httpParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    doReturn( context ).when( httpConnectionHelperSpy ).getContext();
    doReturn( requestDispatcher ).when( context ).getRequestDispatcher( "/api" + endpointPath );
    doThrow( new MalformedURLException() ).when( httpConnectionHelperSpy ).getUrl();
    r = httpConnectionHelperSpy.invokePlatformEndpoint( endpointPath, httpMethod, httpParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    String serverUrl = "http://localhost:8080/pentaho";
    URL url = new URL( serverUrl );
    doReturn( url ).when( httpConnectionHelperSpy ).getUrl();
    doThrow( new ServletException() ).when( requestDispatcher ).forward( any( InternalHttpServletRequest.class ),
        any( InternalHttpServletResponse.class ) );
    r = httpConnectionHelperSpy.invokePlatformEndpoint( endpointPath, httpMethod, httpParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    doThrow( new IOException() ).when( requestDispatcher ).forward( any( InternalHttpServletRequest.class ),
        any( InternalHttpServletResponse.class ) );
    r = httpConnectionHelperSpy.invokePlatformEndpoint( endpointPath, httpMethod, httpParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    doNothing().when( requestDispatcher ).forward( any( InternalHttpServletRequest.class ),
        any( InternalHttpServletResponse.class ) );
    r = httpConnectionHelperSpy.invokePlatformEndpoint( endpointPath, httpMethod, httpParameters );
    assertEquals( r.getStatusCode(), 204 );

  }

  @Test
  public void teatInsertDefaultParameters() throws Exception {
    String httpMethod = "GET";
    List<HttpParameter> httpParameters = new ArrayList<>();
    httpParameters.add( new HttpParameter( "param1",  "value1|" ) );
    httpParameters.add( new HttpParameter( "param2", "value2\\/" ) );
    httpParameters.add( new HttpParameter( "param3", "value3{}" ) );

    InternalHttpServletRequest request = new InternalHttpServletRequest( "", "" );

    httpConnectionHelperSpy.insertParameters( httpMethod, httpParameters, request );
    assertEquals( request.getParameterMap().size(), 3 );
    assertEquals( URLDecoder.decode( request.getParameter( "param1" ), HttpConnectionHelper.UTF_8 ),
        httpParameters.get( 0 ).getValue() );
    assertEquals( URLDecoder.decode( request.getParameter( "param2" ), HttpConnectionHelper.UTF_8 ),
            httpParameters.get( 1 ).getValue() );
    assertEquals( URLDecoder.decode( request.getParameter( "param3" ), HttpConnectionHelper.UTF_8 ),
            httpParameters.get( 2 ).getValue() );

    httpMethod = "PUT";
    request = new InternalHttpServletRequest( "", "" );
    httpConnectionHelperSpy.insertParameters( httpMethod, httpParameters, request );
    assertEquals( request.getContentType(), "application/x-www-form-urlencoded" );
    assertEquals( new String( request.getContent() ), "param1=value1%7C&param2=value2%5C%2F&param3=value3%7B%7D" );

    httpMethod = "POST";
    request = new InternalHttpServletRequest( "", "" );
    httpConnectionHelperSpy.insertParameters( httpMethod, httpParameters, request );
    assertEquals( request.getContentType(), "application/x-www-form-urlencoded" );
    assertEquals( new String( request.getContent() ), "param1=value1%7C&param2=value2%5C%2F&param3=value3%7B%7D" );

    httpMethod = "DELETE";
    request = new InternalHttpServletRequest( "", "" );
    httpConnectionHelperSpy.insertParameters( httpMethod, httpParameters, request );
    assertEquals( request.getContentType(), "application/x-www-form-urlencoded" );
    assertEquals( new String( request.getContent() ), "param1=value1%7C&param2=value2%5C%2F&param3=value3%7B%7D" );
  }

  @Test
  public void testInsertParametersWadlAvailable() throws Exception {
    String httpMethod = "GET";
    List<HttpParameter> httpParametersWadl = new ArrayList<>();
    httpParametersWadl.add( new HttpParameter( "param1",  "value1|", HttpParameter.ParamType.QUERY ) );
    httpParametersWadl.add( new HttpParameter( "param2", "value2\\/", HttpParameter.ParamType.QUERY ) );
    httpParametersWadl.add( new HttpParameter( "param3", "value3{}", HttpParameter.ParamType.BODY ) );
    httpParametersWadl.add( new HttpParameter( "param4", "value4", HttpParameter.ParamType.BODY ) );
    httpParametersWadl.add( new HttpParameter( "param5", "value5", HttpParameter.ParamType.NONE ) );

    InternalHttpServletRequest request = new InternalHttpServletRequest( "", "" );

    httpConnectionHelperSpy.insertParameters( httpMethod, httpParametersWadl, request );
    assertEquals( request.getParameterMap().size(), 5 );
    assertEquals( URLDecoder.decode( request.getParameter( "param1" ), HttpConnectionHelper.UTF_8 ),
      httpParametersWadl.get( 0 ).getValue() );
    assertEquals( URLDecoder.decode( request.getParameter( "param2" ), HttpConnectionHelper.UTF_8 ),
      httpParametersWadl.get( 1 ).getValue() );
    assertEquals( URLDecoder.decode( request.getParameter( "param3" ), HttpConnectionHelper.UTF_8 ),
      httpParametersWadl.get( 2 ).getValue() );
    assertEquals( URLDecoder.decode( request.getParameter( "param4" ), HttpConnectionHelper.UTF_8 ),
      httpParametersWadl.get( 3 ).getValue() );
    assertEquals( URLDecoder.decode( request.getParameter( "param5" ), HttpConnectionHelper.UTF_8 ),
      httpParametersWadl.get( 4 ).getValue() );

    httpMethod = "PUT";
    request = new InternalHttpServletRequest( "", "" );
    httpConnectionHelperSpy.insertParameters( httpMethod, httpParametersWadl, request );
    assertEquals( request.getContentType(), "application/x-www-form-urlencoded" );
    assertEquals( "&param1=value1%7C&param2=value2%5C%2F", request.getQueryString() );
    assertEquals( new String( request.getContent() ), "param3=value3%7B%7D&param4=value4&param5=value5" );

    httpMethod = "POST";
    request = new InternalHttpServletRequest( "", "" );
    httpConnectionHelperSpy.insertParameters( httpMethod, httpParametersWadl, request );
    assertEquals( request.getContentType(), "application/x-www-form-urlencoded" );
    assertEquals( "&param1=value1%7C&param2=value2%5C%2F", request.getQueryString() );
    assertEquals( new String( request.getContent() ), "param3=value3%7B%7D&param4=value4&param5=value5" );

    httpMethod = "DELETE";
    request = new InternalHttpServletRequest( "", "" );
    httpConnectionHelperSpy.insertParameters( httpMethod, httpParametersWadl, request );
    assertEquals( request.getContentType(), "application/x-www-form-urlencoded" );
    assertEquals( "&param1=value1%7C&param2=value2%5C%2F", request.getQueryString() );
    assertEquals( new String( request.getContent() ), "param3=value3%7B%7D&param4=value4&param5=value5" );
  }

  @Test
  public void testInvokePluginEndpoint() throws Exception {
    Response r;

    String pluginName = "platform", endpointPath = "myEndpoint", httpMethod = "GET";
    List<HttpParameter> httpParameters = new ArrayList<>();
    httpParameters.add( new HttpParameter( "param1",  "value1|" ) );
    httpParameters.add( new HttpParameter( "param2", "value2\\/" ) );
    httpParameters.add( new HttpParameter( "param3", "value3{}" ) );


    doReturn( null ).when( httpConnectionHelperSpy ).getPluginManager();
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, httpParameters );
    assertEquals( r.getResult(), ( new Response() ).getResult() );

    IPluginManager pluginManager = mock( IPluginManager.class );
    doReturn( pluginManager ).when( httpConnectionHelperSpy ).getPluginManager();
    doReturn( null ).when( httpConnectionHelperSpy ).getPluginClassLoader( pluginName, pluginManager );
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, httpParameters );
    assertEquals( r.getResult(), ( new Response() ).getResult() );

    ClassLoader pluginClassLoader = mock( ClassLoader.class );
    doReturn( pluginClassLoader ).when( httpConnectionHelperSpy ).getPluginClassLoader( pluginName, pluginManager );
    doReturn( null ).when( httpConnectionHelperSpy ).getListableBeanFactory( pluginName, pluginManager );
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, httpParameters );
    assertEquals( r.getResult(), ( new Response() ).getResult() );

    ListableBeanFactory beanFactory = mock( ListableBeanFactory.class );
    doReturn( beanFactory ).when( httpConnectionHelperSpy ).getListableBeanFactory( pluginName, pluginManager );
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, httpParameters );
    assertEquals( r.getResult(), ( new Response() ).getResult() );

    JAXRSPluginServlet pluginServlet = mock( JAXRSPluginServlet.class );
    doReturn( pluginServlet ).when( httpConnectionHelperSpy ).getJAXRSPluginServlet( beanFactory );
    doThrow( new MalformedURLException() ).when( httpConnectionHelperSpy ).getUrl();
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, httpParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    String serverUrl = "http://localhost:8080/pentaho";
    URL url = new URL( serverUrl );
    doReturn( url ).when( httpConnectionHelperSpy ).getUrl();
    doThrow( new ServletException() ).when( pluginServlet ).service( any( InternalHttpServletRequest.class ),
        any( InternalHttpServletResponse.class ) );
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, httpParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    doThrow( new IOException() ).when( pluginServlet ).service( any( InternalHttpServletRequest.class ),
        any( InternalHttpServletResponse.class ) );
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, httpParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    doNothing().when( pluginServlet ).service( any( InternalHttpServletRequest.class ),
        any( InternalHttpServletResponse.class ) );
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, httpParameters );
    assertEquals( r.getStatusCode(), 404 );
    assertEquals( r.getResponseTime(), 0 );
  }

  @Test
  public void testCallHttp() throws Exception {
    String url = "http://localhost:8080/pentaho",
        user = "admin",
        password = "password",
        method = "GET";
    List<HttpParameter> parameters = null;

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
    doReturn( httpMethod ).when( httpConnectionHelperSpy ).getHttpMethod( url, parameters, method );

    doThrow( new IllegalArgumentException() ).when( httpClient ).executeMethod( any( HostConfiguration.class ),
        eq( httpMethod ) );
    r = httpConnectionHelperSpy.callHttp( url, parameters, method, user, password );
    assertEquals( r.getStatusCode(), new Response().getStatusCode() );

    doReturn( 1 ).when( httpClient ).executeMethod( any( HostConfiguration.class ), eq( httpMethod ) );
    doReturn( null ).when( httpMethod ).getResponseHeaders( "Content-Type" );
    doReturn( "content" ).when( httpConnectionHelperSpy ).getContent( eq( httpMethod ), anyString() );
    r = httpConnectionHelperSpy.callHttp( url, parameters, method, user, password );
    assertEquals( r.getResult(), "content" );

    Header header = mock( Header.class );
    doReturn( null ).when( header ).getValue();
    r = httpConnectionHelperSpy.callHttp( url, parameters, method, user, password );
    assertEquals( r.getResult(), "content" );

    doReturn( "test" ).when( header ).getValue();
    r = httpConnectionHelperSpy.callHttp( url, parameters, method, user, password );
    assertEquals( r.getResult(), "content" );

    doReturn( "charset=utf8" ).when( header ).getValue();
    r = httpConnectionHelperSpy.callHttp( url, parameters, method, user, password );
    assertEquals( r.getResult(), "content" );
  }

  @Test
  public void testGetHttpMethodDefaultParams() throws Exception {
    List<HttpParameter> httpParameters = new ArrayList<>();
    httpParameters.add( new HttpParameter( "param1",  "value1" ) );
    httpParameters.add( new HttpParameter( "param2", "value2" ) );
    httpParameters.add( new HttpParameter( "param3", "value3" ) );
    String url = "http://localhost:8080/pentaho";

    HttpMethod method = httpConnectionHelperSpy.getHttpMethod( url, httpParameters, "GET" );
    assertEquals( method.getClass(), GetMethod.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    assertNotNull( method.getQueryString() );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParameters, "PUT" );
    assertEquals( method.getClass(), PutMethod.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    RequestEntity requestEntity = ( (PutMethod) method ).getRequestEntity();
    assertNotNull( requestEntity );
    assertEquals( requestEntity.getContentType(), "application/x-www-form-urlencoded; charset=UTF-8" );
    assertNull( method.getQueryString() );
    assertEquals( requestEntity.getClass(), StringRequestEntity.class );
    assertNotNull( ( (StringRequestEntity) requestEntity ).getContent() );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParameters, "POST" );
    assertEquals( method.getClass(), PostMethod.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    requestEntity = ( (PostMethod) method ).getRequestEntity();
    assertNotNull( requestEntity );
    assertEquals( requestEntity.getContentType(), "application/x-www-form-urlencoded; charset=UTF-8" );
    assertNull( method.getQueryString() );
    assertEquals( requestEntity.getClass(), StringRequestEntity.class );
    assertNotNull( ( (StringRequestEntity) requestEntity ).getContent() );

    // POST without parameters
    method = httpConnectionHelperSpy.getHttpMethod( url, null, "POST" );
    assertEquals( method.getClass(), PostMethod.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    requestEntity = ( (PostMethod) method ).getRequestEntity();
    assertNotNull( requestEntity );
    assertEquals( requestEntity.getContentType(), "application/x-www-form-urlencoded; charset=UTF-8" );
    assertNull( method.getQueryString() );
    assertEquals( requestEntity.getClass(), StringRequestEntity.class );
    assertNotNull( ( (StringRequestEntity) requestEntity ).getContent() );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParameters, "DELETE" );
    assertEquals( method.getClass(), DeleteMethod.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    assertNotNull( method.getQueryString() );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParameters, "HEAD" );
    assertEquals( method.getClass(), HeadMethod.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    assertNotNull( method.getQueryString() );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParameters, "OPTIONS" );
    assertEquals( method.getClass(), OptionsMethod.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    assertNotNull( method.getQueryString() );
  }

  @Test
  public void testGetHttpMethodWadlAvailable() throws Exception {
    List<HttpParameter> httpParametersWadl = new ArrayList<>();
    httpParametersWadl.add( new HttpParameter( "param1",  "value1|", HttpParameter.ParamType.QUERY ) );
    httpParametersWadl.add( new HttpParameter( "param2", "value2\\/", HttpParameter.ParamType.QUERY ) );
    httpParametersWadl.add( new HttpParameter( "param3", "value3{}", HttpParameter.ParamType.BODY ) );
    httpParametersWadl.add( new HttpParameter( "param4", "value4", HttpParameter.ParamType.BODY ) );
    httpParametersWadl.add( new HttpParameter( "param5", "value5", HttpParameter.ParamType.NONE ) );
    String url = "http://localhost:8080/pentaho";

    HttpMethod method = httpConnectionHelperSpy.getHttpMethod( url, httpParametersWadl, "GET" );
    assertEquals( method.getClass(), GetMethod.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    assertNotNull( method.getQueryString() );
    assertEquals( "param1=value1%7C&param2=value2%5C%2F&param3=value3%7B%7D&param4=value4&param5=value5", method.getQueryString() );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParametersWadl, "PUT" );
    assertEquals( method.getClass(), PutMethod.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    RequestEntity requestEntity = ( (PutMethod) method ).getRequestEntity();
    assertNotNull( requestEntity );
    assertEquals( requestEntity.getContentType(), "application/x-www-form-urlencoded; charset=UTF-8" );
    assertEquals( "param1=value1%7C&param2=value2%5C%2F", method.getQueryString() );
    assertEquals( requestEntity.getClass(), StringRequestEntity.class );
    assertNotNull( ( (StringRequestEntity) requestEntity ).getContent() );
    assertEquals( "param3=value3%7B%7D&param4=value4&param5=value5", ( (StringRequestEntity) requestEntity ).getContent() );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParametersWadl, "POST" );
    assertEquals( method.getClass(), PostMethod.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    requestEntity = ( (PostMethod) method ).getRequestEntity();
    assertNotNull( requestEntity );
    assertEquals( requestEntity.getContentType(), "application/x-www-form-urlencoded; charset=UTF-8" );
    assertEquals( "param1=value1%7C&param2=value2%5C%2F", method.getQueryString() );
    assertEquals( requestEntity.getClass(), StringRequestEntity.class );
    assertNotNull( ( (StringRequestEntity) requestEntity ).getContent() );
    assertEquals( "param3=value3%7B%7D&param4=value4&param5=value5", ( (StringRequestEntity) requestEntity ).getContent() );

    // POST without parameters
    method = httpConnectionHelperSpy.getHttpMethod( url, null, "POST" );
    assertEquals( method.getClass(), PostMethod.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    requestEntity = ( (PostMethod) method ).getRequestEntity();
    assertNotNull( requestEntity );
    assertEquals( requestEntity.getContentType(), "application/x-www-form-urlencoded; charset=UTF-8" );
    assertNull( method.getQueryString() );
    assertEquals( requestEntity.getClass(), StringRequestEntity.class );
    assertEquals( "", ( (StringRequestEntity) requestEntity ).getContent() );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParametersWadl, "DELETE" );
    assertEquals( method.getClass(), DeleteMethod.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    assertNotNull( method.getQueryString() );
    assertEquals( "param1=value1%7C&param2=value2%5C%2F&param3=value3%7B%7D&param4=value4&param5=value5", method.getQueryString() );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParametersWadl, "HEAD" );
    assertEquals( method.getClass(), HeadMethod.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    assertNotNull( method.getQueryString() );
    assertEquals( "param1=value1%7C&param2=value2%5C%2F&param3=value3%7B%7D&param4=value4&param5=value5", method.getQueryString() );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParametersWadl, "OPTIONS" );
    assertEquals( method.getClass(), OptionsMethod.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    assertNotNull( method.getQueryString() );
    assertEquals( "param1=value1%7C&param2=value2%5C%2F&param3=value3%7B%7D&param4=value4&param5=value5", method.getQueryString() );
  }
}
