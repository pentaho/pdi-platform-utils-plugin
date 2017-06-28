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

package org.pentaho.di.baserver.utils.inspector;

import org.apache.http.HttpStatus;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.pentaho.di.baserver.utils.web.HttpConnectionHelper;
import org.pentaho.di.baserver.utils.web.Response;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.xml.XMLParserFactoryProducer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class Inspector {

  private static final String DEFAULT_PLATFORM_NAME = "platform";

  private String serverUrl = "";
  private String userName = "";
  private String password = "";
  private TreeMap<String, TreeMap<String, LinkedList<Endpoint>>> endpoints = new TreeMap<String, TreeMap<String, LinkedList<Endpoint>>>();
  private WadlParser parser = new WadlParser();

  // loaded on the first execution of Inspector.getInstance(), not before
  private static class InspectorHolder {
    public static final Inspector INSTANCE = new Inspector();
  }

  // singleton
  public static Inspector getInstance() {
    return InspectorHolder.INSTANCE;
  }

  protected String getServerUrl() {
    return serverUrl;
  }

  protected String getUserName() {
    return userName;
  }

  protected String getPassword() {
    return password;
  }

  protected TreeMap<String, TreeMap<String, LinkedList<Endpoint>>> getEndpointsTree() {
    return endpoints;
  }

  protected TreeMap<String, LinkedList<Endpoint>> getModuleEndpoints( String module ) {
    return getEndpointsTree().get( module );
  }

  protected WadlParser getParser() {
    return parser;
  }

  /**
   * Inspects the BA server *
   *
   * @param serverUrl
   * @param userName
   * @param password
   * @return
   */
  public boolean inspectServer( final String serverUrl, final String userName, final String password ) {
    this.serverUrl = serverUrl;
    this.userName = userName;
    this.password = password;
    return inspectModuleNames();
  }

  /**
   * Gets connection status to the BA server *
   *
   * @param serverUrl
   * @param userName
   * @param password
   * @return
   */
  public int checkServerStatus( final String serverUrl, final String userName, final String password ) {
    this.serverUrl = serverUrl;
    this.userName = userName;
    this.password = password;
    return getConnectionStatus();
  }

  /**
   * *
   *
   * @return
   */
  public Iterable<String> getModuleNames() {
    if ( getEndpointsTree() != null ) {
      return getEndpointsTree().keySet();
    }
    return Collections.emptyList();
  }

  /**
   * *
   *
   * @return
   */
  public String getDefaultModuleName() {
    if ( getEndpointsTree() != null ) {
      return DEFAULT_PLATFORM_NAME;
    }
    return "";
  }

  /**
   * *
   *
   * @param moduleName
   * @return
   */
  public Iterable<String> getEndpointPaths( String moduleName ) {
    Map<String, LinkedList<Endpoint>> moduleEndpoints = getEndpointMap( moduleName );
    return moduleEndpoints.keySet();
  }

  /**
   * *
   *
   * @param moduleName
   * @return
   */
  public String getDefaultEndpointPath( String moduleName ) {
    Iterable<String> endpointPaths = getEndpointPaths( moduleName );
    if ( endpointPaths != null && endpointPaths.iterator().hasNext() ) {
      // return first path
      return endpointPaths.iterator().next();
    }
    return "";
  }

  /**
   * *
   *
   * @param moduleName
   * @param path
   * @return
   */
  public Iterable<Endpoint> getEndpoints( String moduleName, String path ) {
    Map<String, LinkedList<Endpoint>> moduleEndpoints = getEndpointMap( moduleName );
    Iterable<Endpoint> endpoints = moduleEndpoints.get( path );
    if ( endpoints != null ) {
      return endpoints;
    }
    return Collections.emptyList();
  }

  /**
   * *
   *
   * @param moduleName
   * @param path
   * @return
   */
  public Endpoint getDefaultEndpoint( String moduleName, String path ) {
    Iterable<Endpoint> endpoints = getEndpoints( moduleName, path );
    if ( endpoints != null && endpoints.iterator().hasNext() ) {
      // return first endpoint
      return endpoints.iterator().next();
    }
    return null;
  }

  protected Map<String, LinkedList<Endpoint>> getEndpointMap( String moduleName ) {
    Map<String, LinkedList<Endpoint>> endpointMap = getModuleEndpoints( moduleName );

    if ( endpointMap != null ) {
      return endpointMap;
    } else if ( inspectEndpoints( moduleName ) ) {
      return getModuleEndpoints( moduleName );
    }

    return Collections.emptyMap();
  }

  protected boolean inspectModuleNames() {
    String endpointUrl = getBaseUrl( this.getServerUrl() ) + "/plugin-manager/ids";
    Response response = callHttp( endpointUrl );

    if ( response != null && response.getStatusCode() == HttpStatus.SC_OK ) {
      String[] moduleNames = getModuleNames( response.getResult() );
      for ( String moduleName : moduleNames ) {
        final String id = moduleName.substring( 1, moduleName.length() - 1 );
        this.getEndpointsTree().put( id, null );
      }
      this.getEndpointsTree().put( DEFAULT_PLATFORM_NAME, null );
      return true;
    }

    return false;
  }

  protected int getConnectionStatus() {
    String endpointUrl = getBaseUrl( this.getServerUrl() ) + "/plugin-manager/ids";
    Response response = callHttp( endpointUrl );

    return response == null ? -1 : response.getStatusCode();
  }

  private String[] getModuleNames( String result ) {
    return result.substring( 12, result.length() - 2 ).split( "," );
  }

  protected boolean inspectEndpoints( final String moduleName ) {
    URI uri = null;
    try {
      uri = new URI( getApplicationWadlEndpoint( moduleName ) );
    } catch ( URISyntaxException e ) {
      // do nothing
    }

    if ( uri != null ) {
      Response response = callHttp( uri.toASCIIString() );

      if ( response != null && response.getStatusCode() == HttpStatus.SC_OK ) {
        Document doc = getDocument( response.getResult() );

        if ( doc != null ) {
          TreeMap<String, LinkedList<Endpoint>> endpointMap = new TreeMap<String, LinkedList<Endpoint>>();

          for ( Endpoint endpoint : getParser().getEndpoints( doc ) ) {
            final String path = endpoint.getPath();
            if ( !endpointMap.containsKey( path ) ) {
              endpointMap.put( path, new LinkedList<Endpoint>() );
            }
            endpointMap.get( path ).add( endpoint );
          }

          getEndpointsTree().put( moduleName, endpointMap );

          return true;
        }
      }
    }

    return false;
  }

  protected Document getDocument( String result ) {
    SAXReader reader = XMLParserFactoryProducer.getSAXReader( null );
    InputStream inputStream = new ByteArrayInputStream( result.getBytes() );

    try {
      return reader.read( inputStream );
    } catch ( DocumentException e ) {
      // do nothing
    }

    return null;
  }

  protected String getApplicationWadlEndpoint( String moduleName ) {
    if ( moduleName.equals( DEFAULT_PLATFORM_NAME ) ) {
      return getBaseUrl( this.getServerUrl() ) + "/application.wadl";
    } else {
      return getBaseUrl( this.getServerUrl(), moduleName ) + "/application.wadl";
    }
  }

  protected Response callHttp( String endpointUrl ) {
    return callHttp( endpointUrl, null, null );
  }

  protected Response callHttp( String endpointUrl, Map<String, String> queryParameters, String httpMethod ) {
    Response response = null;
    try {
      response = HttpConnectionHelper.getInstance()
          .callHttp( endpointUrl, queryParameters, httpMethod, this.getUserName(), this.getPassword() );
    } catch ( IOException e ) {
      // do nothing
    } catch ( KettleStepException e ) {
      // do nothing
    }

    return response;
  }


  private String getBaseUrl( final String serverUrl ) {
    if ( serverUrl.endsWith( "/" ) ) {
      return serverUrl + "api";
    } else {
      return serverUrl + "/api";
    }
  }

  private String getBaseUrl( final String serverUrl, final String pluginId ) {
    if ( serverUrl.endsWith( "/" ) ) {
      return serverUrl + "plugin/" + pluginId + "/api";
    } else {
      return serverUrl + "/plugin/" + pluginId + "/api";
    }
  }
}
