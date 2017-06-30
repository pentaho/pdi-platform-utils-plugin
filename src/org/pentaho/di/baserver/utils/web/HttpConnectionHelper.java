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
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
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

import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HttpConnectionHelper {

  public static final String UTF_8 = "UTF-8";
  public static final String APPLICATION_FORM_ENCODED = "application/x-www-form-urlencoded";
  public static final String CONTENT_TYPE = "Content-Type";

  private static final Log logger = LogFactory.getLog( HttpConnectionHelper.class );

  private static HttpConnectionHelper _instance = new HttpConnectionHelper();

  public static HttpConnectionHelper getInstance() {
    return _instance;
  }

  public Response invokeEndpoint( String serverUrl, String userName, String password,
                                  String moduleName, String endpointPath, String httpMethod, List<HttpParameter> httpParameters ) {

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
      response = callHttp( requestUrl, httpParameters, httpMethod, userName, password );
    } catch ( IOException ex ) {
      logger.error( "Failed ", ex );
    } catch ( KettleStepException ex ) {
      logger.error( "Failed ", ex );
    }

    return response;
  }

  public Response invokeEndpoint( String moduleName, String endpointPath, String httpMethod,
                                  List<HttpParameter> httpParameters ) {

    if ( moduleName.equals( "platform" ) ) {
      return invokePlatformEndpoint( endpointPath, httpMethod, httpParameters );
    } else {
      return invokePluginEndpoint( moduleName, endpointPath, httpMethod, httpParameters );
    }
  }

  protected Response invokePlatformEndpoint( String endpointPath, String httpMethod,
                                             List<HttpParameter> httpParameters ) {

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
        insertParameters( httpMethod, httpParameters, servletRequest );
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

  protected void insertParameters( String httpMethod, List<HttpParameter> httpParameters,
                                   InternalHttpServletRequest servletRequest ) throws UnsupportedEncodingException {
    Http method = Http.getHttpMethod( httpMethod );

    switch ( method ) {
      case POST:
      case PUT:
      case DELETE: {
        List<HttpParameter> queryParameters = httpParameters.stream().filter( param -> param.getParamType() == HttpParameter.ParamType.QUERY )
          .collect( Collectors.toList() );
        for ( HttpParameter parameter : queryParameters ) {
          String value = parameter.getValue() != null ? URLEncoder.encode( parameter.getValue(), UTF_8 ) : null;
          String name = parameter.getName() != null ? URLEncoder.encode( parameter.getName(), UTF_8 ) : null;
          servletRequest.setParameter( name, value );
        }
        servletRequest.setContentType( APPLICATION_FORM_ENCODED );
        servletRequest.putHeader( CONTENT_TYPE, APPLICATION_FORM_ENCODED );
        String bodyQuery = constructQueryString( httpParameters, true, HttpParameter.ParamType.BODY, HttpParameter.ParamType.NONE );
        servletRequest.setContent( bodyQuery.getBytes( UTF_8 ) );
        break;
      }
      case OPTIONS:
      case GET:
      case HEAD:
      default: {
        for ( HttpParameter parameter : httpParameters ) {
          String value = parameter.getValue() != null ? URLEncoder.encode( parameter.getValue(), UTF_8 ) : null;
          String name = parameter.getName() != null ? URLEncoder.encode( parameter.getName(), UTF_8 ) : null;
          servletRequest.setParameter( name, value );
        }
        break;
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

  protected Response invokePluginEndpoint( String pluginName, String endpointPath, String httpMethod,
                                           List<HttpParameter> httpParameters ) {

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
      insertParameters( httpMethod, httpParameters, servletRequest );
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

  public Response callHttp( String url, List<HttpParameter> httpParameters,
                            String httpMethod, String user,
                            String password )
    throws IOException, KettleStepException {

    // used for calculating the responseTime
    long startTime = System.currentTimeMillis();

    HttpClient httpclient = getHttpClient();
    HttpMethod method = getHttpMethod( url, httpParameters, httpMethod );
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

  protected HttpMethod getHttpMethod( String url, List<HttpParameter> httpParameters, String httpMethod ) {

    Http method = Http.getHttpMethod( httpMethod );

    switch ( method ) {
      case GET:
        return new GetMethod( url + constructQueryString( httpParameters, false ) );
      case POST:
        PostMethod postMethod = new PostMethod( url + constructQueryString( httpParameters, false, HttpParameter.ParamType.QUERY ) );
        setRequestEntity( postMethod, httpParameters, HttpParameter.ParamType.BODY, HttpParameter.ParamType.NONE );
        return postMethod;
      case PUT:
        PutMethod putMethod = new PutMethod( url + constructQueryString( httpParameters, false, HttpParameter.ParamType.QUERY ) );
        setRequestEntity( putMethod, httpParameters, HttpParameter.ParamType.BODY, HttpParameter.ParamType.NONE );
        return putMethod;
      case DELETE:
        return new DeleteMethod( url + constructQueryString( httpParameters, false ) );
      case HEAD:
        return new HeadMethod( url + constructQueryString( httpParameters, false ) );
      case OPTIONS:
        return new OptionsMethod( url + constructQueryString( httpParameters, false ) );
      default:
        return new GetMethod( url + constructQueryString( httpParameters, false ) );
    }
  }

  private void setRequestEntity( EntityEnclosingMethod method, List<HttpParameter> httpParameters,
                                 HttpParameter.ParamType... paramTypes ) {
    try {
      // TODO: this supports only FormParameters, need to support MultiPart messages with files,
      // simple string values with JSON and XML, plain text, both body and query parameters for PUT
      String queryString = constructQueryString( httpParameters, true, paramTypes );
      method.setRequestEntity( new StringRequestEntity( queryString, APPLICATION_FORM_ENCODED, UTF_8 ) );
    } catch ( UnsupportedEncodingException e ) {
      logger.error( "Failed", e );
    }
  }

  private String constructQueryString( List<HttpParameter> httpParameters, boolean escapeFirstCh,
                                       HttpParameter.ParamType... paramTypes ) {

    StringBuilder queryString = new StringBuilder();

    if ( httpParameters != null && !httpParameters.isEmpty() ) {

      List<HttpParameter.ParamType> acceptableParamTypes = Arrays.asList( paramTypes );

      if ( !acceptableParamTypes.isEmpty() ) {
        httpParameters = httpParameters.stream()
          .filter( param -> acceptableParamTypes.contains( param.getParamType() ) ).collect( Collectors.toList() );
      }

      if ( !httpParameters.isEmpty() ) {
        try {
          boolean first = true;
          for ( HttpParameter param : httpParameters ) {
            if ( first ) {
              queryString.append( "?" );
              first = false;
            } else {
              queryString.append( "&" );
            }
            String name = param.getName() != null ? param.getName() : "";
            String value = param.getValue();

            if ( value != null ) {
              queryString.append( URLEncoder.encode( name, UTF_8 ) )
                .append( "=" ).append( URLEncoder.encode( value, UTF_8 ) );
            } else {
              queryString.append( URLEncoder.encode( name, UTF_8 ) );
            }
          }
        } catch ( UnsupportedEncodingException e ) {
          logger.error( "Failed ", e );
        }
      }
    }

    String query = queryString.toString();

    if ( escapeFirstCh ) {
      if ( !query.isEmpty() ) {
        query = query.substring( 1 );
      }
    }

    return query;
  }

  protected HttpClient getHttpClient() {
    return SlaveConnectionManager.getInstance().createHttpClient();
  }
}
