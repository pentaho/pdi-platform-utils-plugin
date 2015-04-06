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

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.cluster.SlaveConnectionManager;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.web.servlet.JAXRSPluginServlet;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class HttpConnectionHelper {

  private static final Log logger = LogFactory.getLog( HttpConnectionHelper.class );

  private static HttpConnectionHelper _instance = new HttpConnectionHelper();

  public static HttpConnectionHelper getInstance() {
    return _instance;
  }

  public Response invokeEndpoint( final String serverUrl, final String userName, final String password,
      final String moduleName, final String endpointPath, final Map<String, String> queryParameters ) {

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

    String queryString = "";
    boolean first = true;
    for ( String parameterName : queryParameters.keySet() ) {
      if ( first ) {
        queryString = queryString + "?";
        first = false;
      } else {
        queryString = queryString + "&";
      }
      queryString = queryString + parameterName + "=" + queryParameters.get( parameterName );
    }
    requestUrl = requestUrl + queryString;

    logger.info( "requestUrl = " + requestUrl );

    try {
      response = callHttp( requestUrl, userName, password );
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
      servletRequest.setAttribute( "org.apache.catalina.core.DISPATCHER_TYPE", 2 ); //FORWARD = 2

      insertParameters( httpMethod, queryParameters, servletRequest );

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
      InternalHttpServletRequest servletRequest ) {
    if ( !httpMethod.equals( "GET" ) ) {
      StringBuilder s = new StringBuilder();
      boolean first = true;
      for ( Map.Entry<String, String> entry : queryParameters.entrySet() ) {
        if ( !first ) {
          s.append( "&" );
        }
        s.append( entry.getKey() ).append( "=" ).append( entry.getValue() );
        first = false;
      }
      servletRequest.setContentType( "application/x-www-form-urlencoded" );
      servletRequest.setContent( s.toString().getBytes( ) );
    } else {
      for ( Map.Entry<String, String> entry : queryParameters.entrySet() ) {
        servletRequest.setParameter( entry.getKey(), entry.getValue() );
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

    insertParameters( httpMethod, queryParameters, servletRequest );

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

  public Response callHttp( String url, String user, String password ) throws IOException, KettleStepException {

    // used for calculating the responseTime
    long startTime = System.currentTimeMillis();

    HttpClient httpclient = getHttpClient();
    HttpMethod method = getHttpMethod( url );
    httpclient.getParams().setAuthenticationPreemptive( true );
    Credentials credentials = getCredentials( user, password );
    httpclient.getState().setCredentials( AuthScope.ANY, credentials );
    HostConfiguration hostConfiguration = new HostConfiguration();

    int status;
    try {
      status = httpclient.executeMethod( hostConfiguration, method );
    } catch ( IllegalArgumentException ex ) {
      status = -1;
    }

    Response response = new Response();
    if ( status != -1 ) {
      String body;
      String encoding = "";
      Header contentTypeHeader = method.getResponseHeader( "Content-Type" );
      if ( contentTypeHeader != null ) {
        String contentType = contentTypeHeader.getValue();
        if ( contentType != null && contentType.contains( "charset" ) ) {
          encoding = contentType.replaceFirst( "^.*;\\s*charset\\s*=\\s*", "" ).replace( "\"", "" ).trim();
        }
      }

      // get the response
      body = getContent( method, encoding );
      // Get response time
      long responseTime = System.currentTimeMillis() - startTime;

      response.setStatusCode( status );
      response.setResult( body );
      response.setResponseTime( responseTime );
    }
    return response;
  }

  protected String getContent( HttpMethod method, String encoding ) throws IOException {
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
  }

  protected Credentials getCredentials( String user, String password ) {
    return new UsernamePasswordCredentials( user, password );
  }

  protected HttpMethod getHttpMethod( String url ) {
    return new GetMethod( url );
  }

  protected HttpClient getHttpClient() {
    return SlaveConnectionManager.getInstance().createHttpClient();
  }
}
