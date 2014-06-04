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

package pt.webdetails.di.baserver.utils.inspector;

import org.apache.commons.httpclient.HttpStatus;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.pentaho.di.core.exception.KettleStepException;
import pt.webdetails.di.baserver.utils.web.HttpConnectionHelper;
import pt.webdetails.di.baserver.utils.web.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * @author Marco Vala
 */
public final class Inspector {

  // region Inner Definitions

  // loaded on the first execution of Inspector.getInstance(), not before
  private static class InspectorHolder {
    public static final Inspector INSTANCE = new Inspector();
  }

  // singleton
  public static Inspector getInstance() {
    return InspectorHolder.INSTANCE;
  }

  // endregion

  // region Fields

  private String serverUrl;
  private String userName;
  private String password;
  private LinkedList<String> modules = new LinkedList<String>();
  private TreeMap<String, LinkedList<Endpoint>> endpointsMap = new TreeMap<String, LinkedList<Endpoint>>();

  // endregion


  public void setServer( final String serverUrl, final String userName, final String password ) {
    this.serverUrl = serverUrl;
    this.userName = userName;
    this.password = password;
  }


  public String getBaseUrl( final String serverUrl ) {
    if ( serverUrl.endsWith( "/" ) ) {
      return serverUrl + "api";
    } else {
      return serverUrl + "/api";
    }
  }

  public String getBaseUrl( final String serverUrl, final String pluginId ) {
    if ( serverUrl.endsWith( "/" ) ) {
      return serverUrl + "plugin/" + pluginId + "/api";
    } else {
      return serverUrl + "/plugin/" + pluginId + "/api";
    }
  }



  private Collection<String> getModules() {

    String endpointUrl = getBaseUrl( this.serverUrl ) + "/plugin-manager/ids";

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
      ArrayList<String> plugins = new ArrayList<String>();
      for ( String moduleName : moduleNames ) {
        plugins.add( moduleName.substring( 1, moduleName.length() - 1 ) );
      }
      Collections.sort( plugins );
      return plugins;
    }

    return Collections.emptySet();
  }

  public void updateModules() {
    modules.clear();
    modules.addAll( getModules() );
  }

  private Collection<Endpoint> getEndpoints( final String pluginId ) {
    String endpointUrl;

    if ( pluginId == null ) {
      endpointUrl = getBaseUrl( this.serverUrl ) + "/application.wadl";
    } else {
      endpointUrl = getBaseUrl( this.serverUrl, pluginId ) + "/application.wadl";
    }

    Response response = null;
    try {
      response =
        HttpConnectionHelper.callHttp( endpointUrl, this.userName, this.password );
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
        return parser.getEndpoints( doc );
      } catch ( DocumentException e ) {
        // do nothing
      }
    }

    return Collections.emptySet();
  }



  public void updateEndpoints( final String pluginId ) {
    endpointsMap.clear();
    Collection<Endpoint> endpoints = getEndpoints( pluginId );
    for ( Endpoint endpoint : endpoints ) {
      final String path = endpoint.getPath();
      if ( endpointsMap.get( path ) == null ) {
        endpointsMap.put( path, new LinkedList<Endpoint>( ) );
      }
      endpointsMap.get( path ).add( endpoint );
    }
  }







  public Iterable<String> getModuleNames() {
    return this.modules;
  }

  public Iterable<String> getEndpointPaths() {
    return this.endpointsMap.keySet();
  }

  public Iterable<Endpoint> getEndpointsWithPath( String path ) {
    if ( !path.equals( "" ) ) {
      Iterable<Endpoint> endpoints = this.endpointsMap.get( path );
      if ( endpoints != null ) {
        return endpoints;
      }
    }
    return Collections.emptyList();
  }

  public Endpoint getEndpoint( String path, String type ) {
    if ( !path.equals( "" ) ) {
      for ( Endpoint item : this.endpointsMap.get( path ) ) {
        if ( item.getHttpMethod().name().equals( type ) ) {
          return item;
        }
      }
    }
    return null;
  }
}
