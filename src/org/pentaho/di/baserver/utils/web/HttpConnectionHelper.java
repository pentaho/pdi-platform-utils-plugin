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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.util.HttpClientManager;
import org.pentaho.di.core.util.HttpClientUtil;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.web.servlet.JAXRSPluginServlet;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

public class HttpConnectionHelper {

  public static final String UTF_8 = "UTF-8";
  private static final Log logger = LogFactory.getLog( HttpConnectionHelper.class );

  private static HttpConnectionHelper _instance = new HttpConnectionHelper();

  public static HttpConnectionHelper getInstance() {
    return _instance;
  }

  public Response invokeEndpoint( final String serverUrl, final String userName, final String password,
                                  final String moduleName, final String endpointPath, final String httpMethod,
                                  final Map<String, String> queryParameters ) {

    Response response = new Response();

    String requestUrl;
    if ( serverUrl.endsWith( "/" ) ) {
      requestUrl = serverUrl;
    } else {
      requestUrl = serverUrl + "/";
    }
    if ( moduleName.equals( "platform" ) ) {
      requestUrl = requestUrl + "api";
    } else {
      requestUrl = requestUrl + "plugin/" + moduleName + "/api";
    }
    if ( endpointPath.startsWith( "/" ) ) {
      requestUrl = requestUrl + endpointPath;
    } else {
      requestUrl = requestUrl + "/" + endpointPath;
    }

    logger.info( "requestUrl = " + requestUrl );

    try {
      response = callHttp( requestUrl, queryParameters, httpMethod, userName, password );
    } catch ( IOException ex ) {
      logger.error( "Failed ", ex );
    } catch ( KettleStepException ex ) {
      logger.error( "Failed ", ex );
    }

    return response;
  }

  public Response invokeEndpoint( final String moduleName, final String endpointPath, final String httpMethod,
                                  final Map<String, String> queryParameters ) {

    if ( moduleName.equals( "platform" ) ) {
      return invokePlatformEndpoint( endpointPath, httpMethod, queryParameters );
    } else {
      return invokePluginEndpoint( moduleName, endpointPath, httpMethod, queryParameters );
    }
  }

  protected Response invokePlatformEndpoint( final String endpointPath, final String httpMethod,
                                             final Map<String, String> queryParameters ) {

    Response response = new Response();

    // get servlet context and request dispatcher
    ServletContext servletContext = null;
    RequestDispatcher requestDispatcher = null;
    try {
      Object context = getContext();
      if ( context instanceof ServletContext ) {
        servletContext = (ServletContext) context;
        requestDispatcher = servletContext.getRequestDispatcher( "/api" + endpointPath );
      }
    } catch ( NoClassDefFoundError ex ) {
      logger.error( "Failed to get application servlet context", ex );
      return response;
    }

    if ( requestDispatcher != null ) {
      // create servlet request
      URL fullyQualifiedServerURL;
      try {
        fullyQualifiedServerURL = getUrl();
      } catch ( MalformedURLException e ) {
        logger.error( "FullyQualifiedServerURL is incorrect" );
        return response;
      }

      final InternalHttpServletRequest servletRequest = new InternalHttpServletRequest( httpMethod,
        fullyQualifiedServerURL, "/api", endpointPath );
      servletRequest.setAttribute( "org.apache.catalina.core.DISPATCHER_TYPE", DispatcherType.FORWARD ); //FORWARD = 2

      try {
        insertParameters( httpMethod, queryParameters, servletRequest );
      } catch ( UnsupportedEncodingException e ) {
        logger.error( "Can't encode parameters" );
        return response;
      }

      ServletRequestEvent servletRequestEvent = new ServletRequestEvent( servletContext, servletRequest );
      RequestContextListener requestContextListener = new RequestContextListener();
      requestContextListener.requestInitialized( servletRequestEvent );

      // create servlet response
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      final InternalHttpServletResponse servletResponse = new InternalHttpServletResponse( outputStream );

      try {
        // used for calculating the response time
        long startTime = System.currentTimeMillis();

        requestDispatcher.forward( servletRequest, servletResponse );

        // get response time
        long responseTime = System.currentTimeMillis() - startTime;

        response.setStatusCode( servletResponse.getStatus() );
        response.setResult( servletResponse.getContentAsString() );
        response.setResponseTime( responseTime );
      } catch ( ServletException ex ) {
        logger.error( "Failed ", ex );
        return response;
      } catch ( IOException ex ) {
        logger.error( "Failed ", ex );
        return response;
      } finally {
        requestContextListener.requestDestroyed( servletRequestEvent );
      }

    }

    return response;
  }

  protected void insertParameters( String httpMethod, Map<String, String> queryParameters,
                                   InternalHttpServletRequest servletRequest ) throws UnsupportedEncodingException {
    if ( !httpMethod.equals( "GET" ) ) {
      StringBuilder s = new StringBuilder();
      boolean first = true;
      for ( Map.Entry<String, String> entry : queryParameters.entrySet() ) {
        if ( !first ) {
          s.append( "&" );
        }
        s.append( entry.getKey() ).append( "=" ).append( URLEncoder.encode( entry.getValue(), UTF_8 ) );
        first = false;
      }
      servletRequest.setContentType( "application/x-www-form-urlencoded" );
      servletRequest.setContent( s.toString().getBytes() );
    } else {
      for ( Map.Entry<String, String> entry : queryParameters.entrySet() ) {
        String value = URLEncoder.encode( entry.getValue(), UTF_8 );
        servletRequest.setParameter( entry.getKey(), value );
      }
    }
  }

  protected URL getUrl() throws MalformedURLException {
    return new URL( getFullyQualifiedServerURL() );
  }

  protected String getFullyQualifiedServerURL() {
    return PentahoSystem.getApplicationContext().getFullyQualifiedServerURL();
  }

  protected Object getContext() {
    return PentahoSystem.getApplicationContext().getContext();
  }

  protected Response invokePluginEndpoint( final String pluginName, final String endpointPath, final String httpMethod,
                                           final Map<String, String> queryParameters ) {

    Response response = new Response();
    response.setStatusCode( 404 );
    response.setResponseTime( 0 );

    IPluginManager pluginManager = getPluginManager();
    if ( pluginManager == null ) {
      logger.error( "Failed to get plugin manager" );
      return response;
    }

    ClassLoader classLoader = getPluginClassLoader( pluginName, pluginManager );
    if ( classLoader == null ) {
      logger.error( "No such plugin: " + pluginName );
      return response;
    }

    ListableBeanFactory beanFactory = getListableBeanFactory( pluginName, pluginManager );

    if ( beanFactory == null || !beanFactory.containsBean( "api" ) ) {
      logger.error( "Bean not found for plugin: " + pluginName );
      return response;
    }

    JAXRSPluginServlet pluginServlet = getJAXRSPluginServlet( beanFactory );

    // create servlet request
    URL fullyQualifiedServerURL;
    try {
      fullyQualifiedServerURL = getUrl();
    } catch ( MalformedURLException e ) {
      logger.error( "FullyQualifiedServerURL is incorrect" );
      return response;
    }
    final InternalHttpServletRequest servletRequest = new InternalHttpServletRequest( httpMethod,
      fullyQualifiedServerURL, "/plugin", "/" + pluginName + "/api" + endpointPath );

    try {
      insertParameters( httpMethod, queryParameters, servletRequest );
    } catch ( UnsupportedEncodingException e ) {
      logger.error( "Can't encode parameters" );
      return response;
    }

    // create servlet response
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final InternalHttpServletResponse servletResponse = new InternalHttpServletResponse( outputStream );

    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    try {
      // used for calculating the response time
      long startTime = System.currentTimeMillis();

      Thread.currentThread().setContextClassLoader( classLoader );
      pluginServlet.service( servletRequest, servletResponse );
      long responseTime = System.currentTimeMillis() - startTime;

      response.setStatusCode( servletResponse.getStatus() );
      response.setResult( servletResponse.getContentAsString() );
      response.setResponseTime( responseTime );

    } catch ( ServletException ex ) {
      logger.error( "Failed ", ex );
      return response;
    } catch ( IOException ex ) {
      logger.error( "Failed ", ex );
      return response;
    } finally {
      Thread.currentThread().setContextClassLoader( contextClassLoader );
    }

    return response;
  }

  protected JAXRSPluginServlet getJAXRSPluginServlet( ListableBeanFactory beanFactory ) {
    return (JAXRSPluginServlet) beanFactory.getBean( "api", JAXRSPluginServlet.class );
  }

  protected ListableBeanFactory getListableBeanFactory( String pluginName, IPluginManager pluginManager ) {
    return pluginManager.getBeanFactory( pluginName );
  }

  protected ClassLoader getPluginClassLoader( String pluginName, IPluginManager pluginManager ) {
    return pluginManager.getClassLoader( pluginName );
  }

  protected IPluginManager getPluginManager() {
    return PentahoSystem.get( IPluginManager.class );
  }

  public Response callHttp( String url, Map<String, String> queryParameters, String httpMethod, String user,
                            String password )
    throws IOException, KettleStepException {

    // used for calculating the responseTime
    long startTime = System.currentTimeMillis();

    HttpClient httpclient = getHttpClient( user, password );

    int status;
    HttpRequestBase method = getHttpMethod( url, queryParameters, httpMethod );
    HttpResponse httpResponse = null;
    try {
      httpResponse = httpclient.execute( method );
      status = httpResponse.getStatusLine().getStatusCode();
    } catch ( IllegalArgumentException ex ) {
      status = -1;
    }

    Response response = new Response();
    if ( status != -1 ) {
      String body;
      String encoding = UTF_8;
      Header contentTypeHeader = method.getHeaders( "Content-Type" )[ 0 ];
      if ( contentTypeHeader != null ) {
        String contentType = contentTypeHeader.getValue();
        if ( contentType != null && contentType.contains( "charset" ) ) {
          encoding = contentType.replaceFirst( "^.*;\\s*charset\\s*=\\s*", "" ).replace( "\"", "" ).trim();
        }
      }
      // get the response
      body = HttpClientUtil.responseToString( httpResponse, Charset.forName( encoding ) );
      // Get response time
      long responseTime = System.currentTimeMillis() - startTime;

      response.setStatusCode( status );
      response.setResult( body );
      response.setResponseTime( responseTime );
    }
    return response;
  }

/*  protected String getContent( HttpRequestBase method, String encoding ) throws IOException {
    String body;
    InputStreamReader inputStreamReader;

    if ( !Const.isEmpty( encoding ) ) {
      inputStreamReader = new InputStreamReader( method.getResponseBodyAsStream(), encoding );
    } else {
      inputStreamReader = new InputStreamReader( method.getResponseBodyAsStream() );
    }
    StringBuilder bodyBuffer = new StringBuilder();
    int c;
    while ( ( c = inputStreamReader.read() ) != -1 ) {
      bodyBuffer.append( (char) c );
    }
    inputStreamReader.close();
    body = bodyBuffer.toString();
    return body;
  }*/

  protected Credentials getCredentials( String user, String password ) {
    return new UsernamePasswordCredentials( user, password );
  }

  protected HttpRequestBase getHttpMethod( String url, Map<String, String> queryParameters, String httpMethod ) {
    org.pentaho.di.baserver.utils.inspector.HttpMethod method;
    if ( httpMethod == null ) {
      httpMethod = "";
    }
    try {
      method = org.pentaho.di.baserver.utils.inspector.HttpMethod.valueOf( httpMethod );
    } catch ( IllegalArgumentException e ) {
      logger.warn( "Method '" + httpMethod + "' is not supported - using 'GET'" );
      method = org.pentaho.di.baserver.utils.inspector.HttpMethod.GET;
    }

    switch ( method ) {
      case GET:
        return new HttpGet( url + constructQueryString( queryParameters ) );
      case POST:
        HttpPost postMethod = new HttpPost( url );
        setRequestEntity( postMethod, queryParameters );
        return postMethod;
      case PUT:
        HttpPut putMethod = new HttpPut( url );
        setRequestEntity( putMethod, queryParameters );
        return putMethod;
      case DELETE:
        return new HttpDelete( url + constructQueryString( queryParameters ) );
      case HEAD:
        return new HttpHead( url + constructQueryString( queryParameters ) );
      case OPTIONS:
        return new HttpOptions( url + constructQueryString( queryParameters ) );
      default:
        return new HttpGet( url + constructQueryString( queryParameters ) );
    }
  }

  private void setRequestEntity( HttpRequestBase method, Map<String, String> queryParameters ) {
    try {
      // TODO: this supports only FormParameters, need to support MultiPart messages with files,
      // simple string values with JSON and XML, plain text, both body and query parameters for PUT
      String queryString = constructQueryString( queryParameters );
      if ( !queryString.isEmpty() ) {
        queryString = queryString.substring( 1 );
      }
      StringEntity requestEntity = new StringEntity(
        queryString,
        ContentType.APPLICATION_FORM_URLENCODED );
      if ( method instanceof HttpEntityEnclosingRequestBase ) {
        HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase = (HttpEntityEnclosingRequestBase) method;
        httpEntityEnclosingRequestBase.setEntity( requestEntity );
      }
    } catch ( Exception e ) {
      logger.error( "Failed", e );
    }
  }

  private String constructQueryString( Map<String, String> queryParameters ) {
    StringBuilder queryString = new StringBuilder();
    if ( queryParameters != null && !queryParameters.isEmpty() ) {
      try {
        boolean first = true;
        for ( String parameterName : queryParameters.keySet() ) {
          if ( first ) {
            queryString.append( "?" );
            first = false;
          } else {
            queryString.append( "&" );
          }
          queryString.append( parameterName ).append( "=" )
            .append( URLEncoder.encode( queryParameters.get( parameterName ), UTF_8 ) );
        }
      } catch ( UnsupportedEncodingException e ) {
        logger.error( "Failed ", e );
      }
    }
    return queryString.toString();
  }

  HttpClient getHttpClient( String user, String password ) {
    HttpClientManager.HttpClientBuilderFacade clientBuilder = HttpClientManager.getInstance().createBuilder();
    clientBuilder.setCredentials( user, password );
    return clientBuilder.build();
  }
}
