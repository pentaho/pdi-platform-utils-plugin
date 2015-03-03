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
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class WadlParserTest {
  
  private WadlParser wadlParser, wadlParserSpy;
  private Document doc;

  @Before 
  public void setUp() throws Exception {
    wadlParser = new WadlParser();
    wadlParserSpy = spy( wadlParser );
    
    InputStream is = new FileInputStream( new File( "test-res/wadl.xml" ) );
    SAXReader reader = new SAXReader();
    doc = reader.read( is );
  }

  @Test 
  public void testGetEndpoints() throws Exception {
    Collection<Endpoint> endpointList = wadlParserSpy.getEndpoints( doc );
    Node resources = doc.selectSingleNode( "/application/child::*[local-name() = 'resources' ]" );
    
    verify( wadlParserSpy, times( 1 ) ).parseResources( eq( resources ), anyString() );
    assertEquals( endpointList.size(), 142 );
   
    Endpoint endpoint = ( Endpoint ) endpointList.toArray()[0];
    assertEquals( endpoint.getHttpMethod(), HttpMethod.POST );
    assertEquals( endpoint.getId(), "addBlockout" );
    assertEquals( endpoint.getPath(), "/scheduler/blockout/add" );
    assertEquals( endpoint.getQueryParams().size(), 0 );

    endpoint = ( Endpoint ) endpointList.toArray()[1];
    assertEquals( endpoint.getHttpMethod(), HttpMethod.PUT );
    assertEquals( endpoint.getId(), "assignAllRolesToUser" );
    assertEquals( endpoint.getPath(), "/userroledao/assignAllRolesToUser" );
    Collection<QueryParam> queryParamList = endpoint.getQueryParams();
    assertEquals( queryParamList.size(), 2 );
    assertEquals( ( ( QueryParam ) queryParamList.toArray()[0] ).getName(), "tenant" );
    assertEquals( ( ( QueryParam ) queryParamList.toArray()[0] ).getType(), "xs:string" );
    assertEquals( ( ( QueryParam ) queryParamList.toArray()[1] ).getName(), "userName" );
    assertEquals( ( ( QueryParam ) queryParamList.toArray()[1] ).getType(), "xs:string" );

    endpoint = ( Endpoint ) endpointList.toArray()[69];
    assertEquals( endpoint.getHttpMethod(), HttpMethod.GET );
    assertEquals( endpoint.getId(), "getAllRoles" );
    assertEquals( endpoint.getPath(), "/userrolelist/allRoles" );
    assertEquals( endpoint.getQueryParams().size(), 0 );

    assertEquals( wadlParserSpy.getEndpoints( mock( Document.class ) ).size(), 0 );
  }

  @Test
  public void testParseResources() throws Exception {
    final Node resourceNode = mock( Node.class );
    final String parentPath = "parentPath";

    when( resourceNode.valueOf( "@path" ) ).thenReturn( "" );

    final String id = "id";
    final HttpMethod httpMethod = HttpMethod.GET;
    final Node mockNode = createMockNode( id, httpMethod );
    when( resourceNode.selectNodes( anyString() ) ).thenReturn(
        new ArrayList() {{
          add( mockNode );
        }}, new ArrayList()
    );
    final Collection<Endpoint> endpoints = wadlParserSpy.parseResources( resourceNode, parentPath );

    verify( wadlParserSpy, times( 1 ) ).parseMethod( any( Node.class ), eq( parentPath ) );
    assertNotNull( endpoints );
    assertEquals( endpoints.size(), 1 );
    final Endpoint endpoint = endpoints.iterator().next();
    assertEquals( endpoint.getId(), id );
    assertEquals( endpoint.getHttpMethod(), httpMethod );
    assertEquals( endpoint.getPath(), parentPath );
  }

  @Test
  public void testParseMethod() {
    final String id = "id";
    final HttpMethod httpMethod = HttpMethod.GET;
    final String path = "path";

    final Endpoint endpoint = wadlParserSpy.parseMethod( createMockNode( id, httpMethod ), path );
    assertNotNull( endpoint );
    assertEquals( endpoint.getId(), id );
    assertEquals( endpoint.getHttpMethod(), httpMethod );
    assertEquals( endpoint.getPath(), path );
  }

  @Test
  public void testParseQueryParam() {
    final String id = "id";
    final HttpMethod httpMethod = HttpMethod.GET;
    final Node mockNode = createMockNode( id, httpMethod );

    final String name = "name";
    final String type = "type";
    doReturn( name ).when( mockNode ).valueOf( "@name" );
    doReturn( type ).when( mockNode ).valueOf( "@type" );

    final QueryParam queryParam = wadlParserSpy.parseQueryParam( mockNode );
    assertNotNull( queryParam );
    assertEquals( queryParam.getName(), name );
    assertEquals( queryParam.getType(), type );
  }

  @Test
  public void testSanitizePath() {
    final String path = "path";
    String sanitizePath = wadlParserSpy.sanitizePath( path );
    assertEquals( path, sanitizePath );

    final String path1 = "/path";
    sanitizePath = wadlParserSpy.sanitizePath( path1 );
    assertEquals( path, sanitizePath );

    final String path2 = "path/";
    sanitizePath = wadlParserSpy.sanitizePath( path2 );
    assertEquals( path, sanitizePath );

    final String path3 = "/path/";
    sanitizePath = wadlParserSpy.sanitizePath( path3 );
    assertEquals( path, sanitizePath );
  }

  @Test
  public void testShortPath() {
    final String path = "/path";
    String shortPath = wadlParserSpy.shortPath( path );
    assertEquals( path, shortPath);

    String apiPath = "somePath/api" + path;
    shortPath = wadlParserSpy.shortPath( apiPath );
    assertEquals( path, shortPath);
  }

  private Node createMockNode( String id, HttpMethod httpMethod ) {
    final Node node = mock( Node.class );
    doReturn( id ).when( node ).valueOf( "@id" );
    doReturn( httpMethod.toString() ).when( node ).valueOf( "@name" );

    return node;
  }
}

