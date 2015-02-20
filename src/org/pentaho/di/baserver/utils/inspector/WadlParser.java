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

package org.pentaho.di.baserver.utils.inspector;

import org.dom4j.Document;
import org.dom4j.Node;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

public class WadlParser {

  public WadlParser() {
  }

  public Collection<Endpoint> getEndpoints( Document doc ) {
    Node resources = doc.selectSingleNode( "/application/child::*[local-name() = 'resources' ]" );
    if ( resources != null ) {
      return parseResources( resources, sanitizePath( resources.valueOf( "@base" ) ) );
    }
    return Collections.emptySet();
  }


  private Collection<Endpoint> parseResources( Node resourceNode, final String parentPath ) {

    String path = resourceNode.valueOf( "@path" );
    if ( path.isEmpty() ) {
      path = parentPath;
    } else {
      path = parentPath + "/" + sanitizePath( path );
    }

    TreeSet<Endpoint> endpoints = new TreeSet<Endpoint>();

    for ( Object methodNode : resourceNode.selectNodes( "*[name() = 'method']" ) ) {
      endpoints.add( parseMethod( (Node) methodNode, path ) );
    }

    /*
    Node methodNode = resourceNode.selectSingleNode( "*[name() = 'method']" );
    if ( methodNode != null ) {
    }
    */

    for ( Object innerResourceNode : resourceNode.selectNodes( "*[name() = 'resource']" ) ) {
      endpoints.addAll( parseResources( (Node) innerResourceNode, path ) );
    }

    return endpoints;
  }

  private Endpoint parseMethod( Node methodNode, final String path ) {
    Endpoint endpoint = new Endpoint();
    endpoint.setId( methodNode.valueOf( "@id" ) );
    endpoint.setHttpMethod( HttpMethod.valueOf( methodNode.valueOf( "@name" ) ) );
    endpoint.setPath( shortPath( path ) );

    Node requestNode = methodNode.selectSingleNode( "*[name() = 'request']" );
    if ( requestNode != null ) {
      for ( Object queryParamNode : requestNode.selectNodes( "*[name() = 'param']" ) ) {
        endpoint.getQueryParams().add( parseQueryParam( (Node) queryParamNode ) );
      }
    }

    return endpoint;
  }

  private QueryParam parseQueryParam( Node queryParamNode ) {
    QueryParam queryParam = new QueryParam();
    queryParam.setName( queryParamNode.valueOf( "@name" ) );
    queryParam.setType( queryParamNode.valueOf( "@type" ) );
    return queryParam;
  }

  private String sanitizePath( String path ) {
    // trim off leading and trailing slashes
    path = ( path.startsWith( "/" )) ? path.substring( 1 ) : path;
    path = ( path.endsWith( "/" )) ? path.substring( 0, path.length() - 1 ) : path;
    return path;
  }

  private String shortPath( String path ) {
    return path.substring( path.indexOf( "api" ) + 3 );
  }
}
