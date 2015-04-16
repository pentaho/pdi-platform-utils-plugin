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

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Node;
import org.pentaho.di.baserver.utils.CallEndpointMeta;
import org.pentaho.di.i18n.BaseMessages;

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

  protected Collection<Endpoint> parseResources( Node resourceNode, final String parentPath ) {

    String path = resourceNode.valueOf( "@path" );
    if ( path.isEmpty() ) {
      path = parentPath;
    } else {
      path = parentPath + "/" + sanitizePath( path );
    }

    TreeSet<Endpoint> endpoints = new TreeSet<Endpoint>();

    for ( Object methodNode : resourceNode.selectNodes( "*[local-name() = 'method']" ) ) {
      endpoints.add( parseMethod( (Node) methodNode, path ) );
    }

    for ( Object innerResourceNode : resourceNode.selectNodes( "*[local-name() = 'resource']" ) ) {
      endpoints.addAll( parseResources( (Node) innerResourceNode, path ) );
    }

    return endpoints;
  }

  protected Endpoint parseMethod( Node methodNode, final String path ) {
    Endpoint endpoint = new Endpoint();
    endpoint.setId( methodNode.valueOf( "@id" ) );
    endpoint.setHttpMethod( HttpMethod.valueOf( methodNode.valueOf( "@name" ) ) );
    endpoint.setPath( shortPath( path ) );

    Node requestNode = methodNode.selectSingleNode( "*[local-name() = 'request']" );
    if ( requestNode != null ) {
      for ( Object queryParamNode : requestNode.selectNodes( "*[local-name() = 'param']" ) ) {
        endpoint.getQueryParams().add( parseQueryParam( (Node) queryParamNode ) );
      }
    }

    Node nodeDoc = methodNode.selectSingleNode( "*[local-name() = 'doc']" );
    if ( nodeDoc != null ) {
      endpoint.setDeprecated( isDeprecated( nodeDoc.getText() ) );
      endpoint.setDocumentation( extractComment( nodeDoc.getText() ) );
      endpoint.setSupported( isSupported( nodeDoc.getText() ) );
    }
    return endpoint;
  }

  protected QueryParam parseQueryParam( Node queryParamNode ) {
    QueryParam queryParam = new QueryParam();
    queryParam.setName( queryParamNode.valueOf( "@name" ) );
    queryParam.setType( queryParamNode.valueOf( "@type" ) );
    return queryParam;
  }

  protected String sanitizePath( String path ) {
    // trim off leading and trailing slashes
    path = ( path.startsWith( "/" ) ) ? path.substring( 1 ) : path;
    path = ( path.endsWith( "/" ) ) ? path.substring( 0, path.length() - 1 ) : path;
    return path;
  }

  protected String shortPath( String path ) {
    return path.contains( "/api/" ) ? path.substring( path.indexOf( "/api/" ) + 4 ) : path;
  }

  protected boolean isSupported( String in ) {
    Pattern patern = Pattern.compile( "<supported>(true||TRUE||True)<\\/supported>.*", Pattern.DOTALL );
    Matcher matcher = patern.matcher( in );
    return matcher.matches();
  }

  protected boolean isDeprecated( String in ) {
    Pattern patern = Pattern.compile( ".*<deprecated>(true||TRUE||True)<\\/deprecated>.*", Pattern.DOTALL );
    Matcher matcher = patern.matcher( in );
    return matcher.matches();
  }

  protected String extractComment( String in ) {
    Pattern patern = Pattern.compile( ".*<documentation>(.*)<\\/documentation>.*", Pattern.DOTALL );
    Matcher matcher = patern.matcher( in );
    if ( matcher.matches() ) {
      return matcher.group( 1 );
    }
    return "";
  }
}
