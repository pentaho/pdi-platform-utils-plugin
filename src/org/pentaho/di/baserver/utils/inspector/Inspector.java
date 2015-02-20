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

package org.pentaho.di.baserver.utils.inspector;

import org.apache.commons.httpclient.HttpStatus;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.pentaho.di.core.exception.KettleStepException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Marco Vala
 */
public final class Inspector {

  // loaded on the first execution of Inspector.getInstance(), not before
  private static class InspectorHolder {
    public static final Inspector INSTANCE = new Inspector();
  }

  // singleton
  public static Inspector getInstance() {
    return InspectorHolder.INSTANCE;
  }


  private String serverUrl = "";
  private String userName = "";
  private String password = "";
  private TreeMap<String, TreeMap<String, LinkedList<Endpoint>>> endpoints;


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

  private boolean inspectModuleNames() {

    String endpointUrl = getBaseUrl( this.serverUrl ) + "/plugin-manager/ids";
    this.endpoints = new TreeMap<String, TreeMap<String, LinkedList<Endpoint>>>();

    Response response = null;
    try {
      response = HttpConnectionHelper.callHttp( endpointUrl, this.userName, this.password );
    } catch ( IOException e ) {
      // do nothing
    } catch ( KettleStepException e ) {
      // do nothing
    }

    if ( response != null && response.getStatusCode() == HttpStatus.SC_OK ) {
      String[] moduleNames = response.getResult().substring( 12, response.getResult().length() - 2 ).split( "," );
      LinkedList<String> modules = new LinkedList<String>();
      for ( String moduleName : moduleNames ) {
        final String id = moduleName.substring( 1, moduleName.length() - 1 );
        this.endpoints.put( id, null );
      }
      this.endpoints.put( "platform", null );
      return true;
    }

    return false;
  }

  private boolean inspectEndpoints( final String moduleName ) {
    String endpointUrl;

    if ( moduleName.equals( "platform" ) ) {
      endpointUrl = getBaseUrl( this.serverUrl ) + "/application.wadl";
    } else {
      endpointUrl = getBaseUrl( this.serverUrl, moduleName ) + "/application.wadl";
    }
    URI uri = null;
    try {
      uri = new URI( endpointUrl );
    } catch ( URISyntaxException e ) {
      // do nothing
    }

    if ( uri != null ) {
      Response response = null;
      try {
        response =
          HttpConnectionHelper.callHttp( uri.toASCIIString(), this.userName, this.password );
      } catch ( IOException e ) {
        // do nothing
      } catch ( KettleStepException e ) {
        // do nothing
      }

      if ( response != null && response.getStatusCode() == HttpStatus.SC_OK ) {
        SAXReader reader = new SAXReader();
        InputStream inputStream = new ByteArrayInputStream( response.getResult().getBytes() );
        WadlParser parser = new WadlParser();
        try {
          Document doc = reader.read( inputStream );

          TreeMap<String, LinkedList<Endpoint>> endpointMap = new TreeMap<String, LinkedList<Endpoint>>();

          for ( Endpoint endpoint : parser.getEndpoints( doc ) ) {
            final String path = endpoint.getPath();
            if ( !endpointMap.containsKey( path ) ) {
              endpointMap.put( path, new LinkedList<Endpoint>() );
            }
            endpointMap.get( path ).add( endpoint );
          }

          this.endpoints.put( moduleName, endpointMap );

          return true;


        } catch ( DocumentException e ) {
          // do nothing
        }
      }
    }

    return false;
  }

  private Map<String, LinkedList<Endpoint>> getEndpointMap( String moduleName ) {

    Map<String, LinkedList<Endpoint>> endpointMap = this.endpoints.get( moduleName );

    if ( endpointMap != null ) {
      return endpointMap;
    }

    if ( inspectEndpoints( moduleName ) ) {
      return this.endpoints.get( moduleName );
    }

    return Collections.emptyMap();
  }


  public boolean inspectServer( final String serverUrl, final String userName, final String password ) {
    this.serverUrl = serverUrl;
    this.userName = userName;
    this.password = password;
    return inspectModuleNames();
  }

  public Iterable<String> getModuleNames() {
    if ( this.endpoints != null ) {
      return this.endpoints.keySet();
    }
    return Collections.emptyList();
  }

  public String getDefaultModuleName() {
    if ( this.endpoints != null ) {
      return "platform";
    }
    return "";
  }

  public Iterable<String> getEndpointPaths( String moduleName ) {
    Map<String, LinkedList<Endpoint>> moduleEndpoints = getEndpointMap( moduleName );
    return moduleEndpoints.keySet();
  }

  public String getDefaultEndpointPath( String moduleName ) {
    Map<String, LinkedList<Endpoint>> moduleEndpoints = getEndpointMap( moduleName );
    if ( moduleEndpoints.size() > 0 ) {
      // return first path
      return moduleEndpoints.keySet().iterator().next();
    }
    return "";
  }

  public Iterable<Endpoint> getEndpoints( String moduleName, String path ) {
    Map<String, LinkedList<Endpoint>> moduleEndpoints = getEndpointMap( moduleName );
    Iterable<Endpoint> endpoints = moduleEndpoints.get( path );
    if ( endpoints != null ) {
      return endpoints;
    }
    return Collections.emptyList();
  }

  public Endpoint getDefaultEndpoint( String moduleName, String path ) {
    Map<String, LinkedList<Endpoint>> moduleEndpoints = getEndpointMap( moduleName );
    Iterable<Endpoint> endpoints = moduleEndpoints.get( path );
    if ( endpoints != null && endpoints.iterator().hasNext() ) {
      // return first endpoint
      return endpoints.iterator().next();
    }
    return null;
  }
}
