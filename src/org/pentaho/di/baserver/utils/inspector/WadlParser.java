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
 * Copyright 2006 - 2017 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.di.baserver.utils.inspector;

import org.dom4j.Document;
import org.dom4j.Node;
import org.pentaho.di.baserver.utils.web.Http;
import org.pentaho.di.baserver.utils.web.HttpParameter;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WadlParser {

  private static final String REPRESENTATION = "*[local-name() = 'representation' and @mediaType='application/x-www-form-urlencoded']";
  private static final String RESOURCES = "/application/child::*[local-name() = 'resources' ]";
  private static final String RESOURCE = "*[local-name() = 'resource']";
  private static final String REQUEST = "*[local-name() = 'request']";
  private static final String PARAM = "*[local-name() = 'param']";
  private static final String METHOD = "*[local-name() = 'method']";
  private static final String DOC = "*[local-name() = 'doc']";
  private static final String PATH = "@path";
  private static final String BASE = "@base";
  private static final String NAME = "@name";
  private static final String TYPE = "@type";
  private static final String ID = "@id";


  private static final Pattern SUPPORTED = Pattern.compile( "<supported>(true||TRUE||True)<\\/supported>.*", Pattern.DOTALL );
  private static final Pattern DEPRECATED = Pattern.compile( ".*<deprecated>(true||TRUE||True)<\\/deprecated>.*", Pattern.DOTALL );
  private static final Pattern DOCUMENTATION = Pattern.compile( ".*<documentation>(.*)<\\/documentation>.*", Pattern.DOTALL );

  public WadlParser() {
  }

  public Collection<Endpoint> getEndpoints( Document doc ) {
    Node resources = doc.selectSingleNode( RESOURCES );
    if ( resources != null ) {
      return parseResources( resources, sanitizePath( resources.valueOf( BASE ) ) );
    }
    return Collections.emptySet();
  }

  protected Collection<Endpoint> parseResources( Node resourceNode, final String parentPath ) {

    String path = resourceNode.valueOf( PATH );
    if ( path.isEmpty() ) {
      path = parentPath;
    } else {
      path = parentPath + "/" + sanitizePath( path );
    }

    TreeSet<Endpoint> endpoints = new TreeSet<Endpoint>();

    for ( Object methodNode : resourceNode.selectNodes( METHOD ) ) {
      endpoints.add( parseMethod( (Node) methodNode, path ) );
    }

    for ( Object innerResourceNode : resourceNode.selectNodes( RESOURCE ) ) {
      endpoints.addAll( parseResources( (Node) innerResourceNode, path ) );
    }

    return endpoints;
  }

  protected Endpoint parseMethod( Node methodNode, final String path ) {
    Endpoint endpoint = new Endpoint();
    endpoint.setId( methodNode.valueOf( ID ) );
    endpoint.setHttpMethod( Http.valueOf( methodNode.valueOf( NAME ) ) );
    endpoint.setPath( shortPath( path ) );

    Node requestNode = methodNode.selectSingleNode( REQUEST );
    if ( requestNode != null ) {
      for ( Object queryParamNode : requestNode.selectNodes( PARAM ) ) {
        endpoint.getParamDefinitions().add( parseParam( (Node) queryParamNode, HttpParameter.ParamType.QUERY ) );
      }

      Node representationNode = requestNode.selectSingleNode( REPRESENTATION );

      if ( representationNode != null ) {
        for ( Object bodyParamNode : representationNode.selectNodes( PARAM ) ) {
          endpoint.getParamDefinitions().add( parseParam( (Node) bodyParamNode, HttpParameter.ParamType.BODY ) );
        }
      }
    }

    Node nodeDoc = methodNode.selectSingleNode( DOC );
    if ( nodeDoc != null ) {
      endpoint.setDeprecated( isDeprecated( nodeDoc.getText() ) );
      endpoint.setDocumentation( extractComment( nodeDoc.getText() ) );
      endpoint.setSupported( isSupported( nodeDoc.getText() ) );
    }
    return endpoint;
  }

  protected ParamDefinition parseParam( Node paramNode, HttpParameter.ParamType paramType ) {
    return new ParamDefinition( paramNode.valueOf( NAME ), paramNode.valueOf( TYPE ), paramType );
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
    Matcher matcher = SUPPORTED.matcher( in );
    return matcher.matches();
  }

  protected boolean isDeprecated( String in ) {
    Matcher matcher = DEPRECATED.matcher( in );
    return matcher.matches();
  }

  protected String extractComment( String in ) {
    Matcher matcher = DOCUMENTATION.matcher( in );
    if ( matcher.matches() ) {
      return matcher.group( 1 );
    }
    return "";
  }
}
