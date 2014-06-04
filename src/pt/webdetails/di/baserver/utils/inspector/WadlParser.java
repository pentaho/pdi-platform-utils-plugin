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

import org.dom4j.Document;
import org.dom4j.Node;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

/**
 * @author Marco Vala
 */
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
