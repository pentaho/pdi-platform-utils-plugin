/*!
* Copyright 2002 - 2014 Webdetails, a Pentaho company.  All rights reserved.
*
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package pt.webdetails.di.baserver.utils.web;

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
import java.util.Map;

/**
 * @author Marco Vala
 */
public final class HttpConnectionHelper {

  private static final Log logger = LogFactory.getLog( HttpConnectionHelper.class );


  public static Response invokeEndpoint( final String serverUrl,
                                         final String userName,
                                         final String password,
                                         final String moduleName,
                                         final String endpointPath,
                                         final String httpMethod,
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


  public static Response invokeEndpoint( final String moduleName,
                                         final String endpointPath,
                                         final String httpMethod,
                                         final Map<String, String> queryParameters ) {

    if ( moduleName.equals( "platform" ) ) {
      return invokePlatformEndpoint( endpointPath, httpMethod, queryParameters );
    } else {
      return invokePluginEndpoint( moduleName, endpointPath, httpMethod, queryParameters );
    }
  }


  protected static Response invokePlatformEndpoint( final String endpointPath,
                                                    final String httpMethod,
                                                    final Map<String, String> queryParameters ) {

    Response response = new Response();

    // get servlet context and request dispatcher
    ServletContext servletContext = null;
    RequestDispatcher requestDispatcher = null;
    try {
      Object context = PentahoSystem.getApplicationContext().getContext();
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
      final InternalHttpServletRequest servletRequest =
        new InternalHttpServletRequest( httpMethod, "/pentaho", "/api", endpointPath );
      servletRequest.setAttribute( "org.apache.catalina.core.DISPATCHER_TYPE", 2 ); // FORWARD = 2

      for ( Map.Entry<String, String> entry : queryParameters.entrySet() ) {
        servletRequest.setParameter( entry.getKey(), entry.getValue() );
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

  protected static Response invokePluginEndpoint( final String pluginName,
                                                  final String endpointPath,
                                                  final String httpMethod,
                                                  final Map<String, String> queryParameters ) {

    Response response = new Response();

    IPluginManager pluginManager = PentahoSystem.get( IPluginManager.class );
    if ( pluginManager == null ) {
      logger.error( "Failed to get plugin manager" );
      return response;
    }

    ClassLoader classLoader = pluginManager.getClassLoader( pluginName );
    if ( classLoader == null ) {
      logger.error( "No such plugin: " + pluginName );
      return response;
    }

    ListableBeanFactory beanFactory = pluginManager.getBeanFactory( pluginName );

    if ( beanFactory == null || !beanFactory.containsBean( "api" ) ) {
      logger.error( "Bean not found for plugin: " + pluginName );
      return response;
    }

    JAXRSPluginServlet pluginServlet = (JAXRSPluginServlet) beanFactory.getBean( "api", JAXRSPluginServlet.class );

    // create servlet request
    final InternalHttpServletRequest servletRequest =
      new InternalHttpServletRequest( httpMethod, "", "/plugin", pluginName + "/api" + endpointPath );

    for ( Map.Entry<String, String> entry : queryParameters.entrySet() ) {
      servletRequest.setParameter( entry.getKey(), entry.getValue() );
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


  public static Response callHttp( String url, String user, String password ) throws IOException, KettleStepException {

    // used for calculating the responseTime
    long startTime = System.currentTimeMillis();

    HttpClient httpclient = SlaveConnectionManager.getInstance().createHttpClient();
    HttpMethod method = new GetMethod( url );
    httpclient.getParams().setAuthenticationPreemptive( true );
    Credentials credentials = new UsernamePasswordCredentials( user, password );
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
      String body = null;
      String encoding = "";
      Header contentTypeHeader = method.getResponseHeader( "Content-Type" );
      if ( contentTypeHeader != null ) {
        String contentType = contentTypeHeader.getValue();
        if ( contentType != null && contentType.contains( "charset" ) ) {
          encoding = contentType.replaceFirst( "^.*;\\s*charset\\s*=\\s*", "" ).replace( "\"", "" ).trim();
        }
      }

      // get the response
      InputStreamReader inputStreamReader = null;
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

      // Get response time
      long responseTime = System.currentTimeMillis() - startTime;

      response.setStatusCode( status );
      response.setResult( body );
      response.setResponseTime( responseTime );
    }
    return response;
  }
}
