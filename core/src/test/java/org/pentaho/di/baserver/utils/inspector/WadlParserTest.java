/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.di.baserver.utils.inspector;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.baserver.utils.CallEndpointMeta;
import org.pentaho.di.baserver.utils.web.Http;
import org.pentaho.di.baserver.utils.web.HttpParameter;
import org.pentaho.di.i18n.BaseMessages;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Collection;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.any;

public class WadlParserTest {

  private WadlParser wadlParser, wadlParserSpy, testableWadlParser;
  private Document doc;

  private static String DEPRECATED = BaseMessages.getString( CallEndpointMeta.class, "WadlParser.endpoint.deprecated" );
  private static String PRIVATE = BaseMessages.getString( CallEndpointMeta.class, "WadlParser.endpoint.private" );

  private static String TEST_DATA_DEPRECATED =
      "<visibility>Public</visibility><deprecated>true</deprecated><documentation>Retrieve the all the job(s) visible"
          + " to the current users.</documentation>";
  private static String TEST_DATA_RESULT_DEPRECATED = DEPRECATED
      + "Retrieve the all the job(s) visible to the current users.";

  private static String TEST_DATA =
      "<visibility>Public</visibility><documentation>Retrieve the all the job(s) visible to the current users"
          + ".</documentation>";
  private static String TEST_DATA_RESULT = "Retrieve the all the job(s) visible to the current users.";

  private static String TEST_DATA_PRIVATE =
      "<visibility>Private</visibility><deprecated>true</deprecated><documentation>Retrieve the all the job(s) "
          + "visible to the current users.</documentation>";

  private static String TEST_DATA_MULTILINE =
      "<visibility>Public</visibility><deprecated>true</deprecated><documentation>Return a list of the permission "
          + "roles in the platform.\r\n"
          + "\r\n"
          + " <p><b>Example Request:</b><br />\r\n"
          + "    GET pentaho/api/userrolelist/permission-roles\r\n"
          + " </p></documentation>";

  private static String TEST_DATA_MULTILINE_RESULT = DEPRECATED
      + "Return a list of the permission roles in the platform.\r\n" + "\r\n" + " <p><b>Example Request:</b><br />\r\n"
      + "    GET pentaho/api/userrolelist/permission-roles\r\n" + " </p>";

  private static class TestableWadlParser extends WadlParser {

    @Override
    public boolean isSupported( String in ) {
      return super.isSupported( in );
    }

    @Override
    public boolean isDeprecated( String in ) {
      return super.isDeprecated( in );
    }

    @Override
    public String extractComment( String in ) {
      return super.extractComment( in );
    }
  }

  @Before
  public void setUp() throws Exception {
    wadlParser = new WadlParser();
    wadlParserSpy = spy( wadlParser );
    testableWadlParser = new TestableWadlParser();

    File wadlFile = new File( this.getClass().getResource("/wadl.xml").getFile() );
    InputStream is = new FileInputStream( wadlFile );
    SAXReader reader = new SAXReader();
    doc = reader.read( is );
  }

  @Test
  public void testGetEndpoints() throws Exception {
    Collection<Endpoint> endpointList = wadlParserSpy.getEndpoints( doc );
    Node resources = doc.selectSingleNode( "/application/child::*[local-name() = 'resources' ]" );

    verify( wadlParserSpy, times( 1 ) ).parseResources( eq( resources ), any() );
    assertEquals( endpointList.size(), 142 );

    Endpoint endpoint = (Endpoint) endpointList.toArray()[ 0 ];
    assertEquals( endpoint.getHttpMethod(), Http.POST );
    assertEquals( endpoint.getId(), "addBlockout" );
    assertEquals( endpoint.getPath(), "/scheduler/blockout/add" );
    assertEquals( endpoint.getParamDefinitions().size(), 0 );
    assertEquals( endpoint.isDeprecated(), false );
    assertEquals( endpoint.isSupported(), true );
    assertEquals( endpoint.getDocumentation().isEmpty(), false );

    endpoint = (Endpoint) endpointList.toArray()[ 1 ];
    assertEquals( endpoint.getHttpMethod(), Http.PUT );
    assertEquals( endpoint.getId(), "assignAllRolesToUser" );
    assertEquals( endpoint.getPath(), "/userroledao/assignAllRolesToUser" );
    Set<ParamDefinition> paramDefinitions = endpoint.getParamDefinitions();
    assertEquals( paramDefinitions.size(), 2 );
    Object[] definitions = paramDefinitions.toArray();
    Arrays.sort( definitions );
    assertEquals( ( (ParamDefinition) definitions[ 0 ] ).getName(), "tenant" );
    assertEquals( ( (ParamDefinition) definitions[ 0 ] ).getContentType(), "xs:string" );
    assertEquals( ( (ParamDefinition) definitions[ 1 ] ).getName(), "userName" );
    assertEquals( ( (ParamDefinition) definitions[ 1 ] ).getContentType(), "xs:string" );
    assertEquals( endpoint.isDeprecated(), false );
    assertEquals( endpoint.isSupported(), false );
    assertEquals( endpoint.getDocumentation().isEmpty(), false );

    endpoint = (Endpoint) endpointList.toArray()[ 69 ];
    assertEquals( endpoint.getHttpMethod(), Http.GET );
    assertEquals( endpoint.getId(), "getAllRoles" );
    assertEquals( endpoint.getPath(), "/userrolelist/allRoles" );
    assertEquals( endpoint.getParamDefinitions().size(), 0 );
    assertEquals( endpoint.isDeprecated(), false );
    assertEquals( endpoint.isSupported(), true );
    assertEquals( endpoint.getDocumentation().isEmpty(), false );

    assertEquals( wadlParserSpy.getEndpoints( mock( Document.class ) ).size(), 0 );
  }

  @Test
  public void testParseResources() throws Exception {
    final Node resourceNode = mock( Node.class );
    final String parentPath = "parentPath";

    when( resourceNode.valueOf( "@path" ) ).thenReturn( "" );

    final String id = "id";
    final Http httpMethod = Http.GET;
    final Node mockNode = createMockNode( id, httpMethod );
    when( resourceNode.selectNodes( any() ) ).thenReturn( new ArrayList() {
      {
        add( mockNode );
      }
    }, new ArrayList() );
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
    final Http httpMethod = Http.GET;
    final String path = "path";
    Node methodNode = createMockNode( id, httpMethod );
    Node requestNode = mock( Node.class );
    List<Node> queryParams = new ArrayList<>();
    queryParams.add( createParamMockNode( "queryParam1", "xs:string" ) );
    queryParams.add( createParamMockNode( "queryParam2", "xs:string" ) );
    when( methodNode.selectSingleNode( eq( "*[local-name() = 'request']" ) ) ).thenReturn( requestNode );
    when( requestNode.selectNodes( eq( "*[local-name() = 'param']" ) ) ).thenReturn( queryParams );
    Node representationNode = mock( Node.class );
    when( requestNode.selectSingleNode( eq( "*[local-name() = 'representation' and @mediaType='application/x-www-form-urlencoded']" ) ) )
            .thenReturn( representationNode );
    List<Node> bodyParams = new ArrayList<>();
    bodyParams.add( createParamMockNode( "bodyParam1", "xs:string" ) );
    bodyParams.add( createParamMockNode( "bodyParam2", "xs:string" ) );
    when( representationNode.selectNodes( "*[local-name() = 'param']" ) ).thenReturn( bodyParams );
    final Endpoint endpoint = wadlParserSpy.parseMethod( methodNode, path );
    assertNotNull( endpoint );
    assertEquals( endpoint.getId(), id );
    assertEquals( endpoint.getHttpMethod(), httpMethod );
    assertEquals( endpoint.getPath(), path );
    Set<ParamDefinition> paramDefinitions = endpoint.getParamDefinitions();
    assertEquals( 4, paramDefinitions.size() );
    ParamDefinition paramDefinition = endpoint.getParameterDefinition( "queryParam1" );
    assertNotNull( paramDefinition );
    assertEquals( "queryParam1", paramDefinition.getName() );
    assertEquals( HttpParameter.ParamType.QUERY, paramDefinition.getParamType() );
    paramDefinition = endpoint.getParameterDefinition( "bodyParam2" );
    assertNotNull( paramDefinition );
    assertEquals( "bodyParam2", paramDefinition.getName() );
    assertEquals( HttpParameter.ParamType.BODY, paramDefinition.getParamType() );
  }

  @Test
  public void testParseParameter() {
    final String id = "id";
    final Http httpMethod = Http.GET;
    final Node mockNode = createMockNode( id, httpMethod );

    final String name = "name";
    final String type = "type";
    doReturn( name ).when( mockNode ).valueOf( "@name" );
    doReturn( type ).when( mockNode ).valueOf( "@type" );

    ParamDefinition paramDefinition = wadlParserSpy.parseParam( mockNode, HttpParameter.ParamType.QUERY );
    assertNotNull( paramDefinition );
    assertEquals( paramDefinition.getName(), name );
    assertEquals( paramDefinition.getContentType(), type );
    assertEquals( HttpParameter.ParamType.QUERY, paramDefinition.getParamType() );

    paramDefinition = wadlParserSpy.parseParam( mockNode, HttpParameter.ParamType.BODY );
    assertNotNull( paramDefinition );
    assertEquals( paramDefinition.getName(), name );
    assertEquals( paramDefinition.getContentType(), type );
    assertEquals( HttpParameter.ParamType.BODY, paramDefinition.getParamType() );
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
    assertEquals( path, shortPath );

    String apiPath = "somePath/api" + path;
    shortPath = wadlParserSpy.shortPath( apiPath );
    assertEquals( path, shortPath );
  }

  private Node createMockNode( String id, Http httpMethod ) {
    final Node node = mock( Node.class );
    doReturn( id ).when( node ).valueOf( "@id" );
    doReturn( httpMethod.toString() ).when( node ).valueOf( "@name" );

    return node;
  }

  private Node createParamMockNode( String name, String type ) {
    Node param = mock( Node.class );
    when( param.valueOf( "@name" ) ).thenReturn( name );
    when( param.valueOf( "@type" ) ).thenReturn( type );
    return param;
  }

  @Test
  public void testParseDocDeprecated() throws Exception {
    TestableWadlParser testableWadlParser = new TestableWadlParser();

    Assert.assertEquals( true, testableWadlParser.isDeprecated( "<deprecated>true</deprecated>" ) );
    Assert.assertEquals( true, testableWadlParser.isDeprecated( "<deprecated>TRUE</deprecated>" ) );
    Assert.assertEquals( true, testableWadlParser.isDeprecated( "<deprecated>True</deprecated>" ) );
    Assert.assertEquals( false, testableWadlParser.isDeprecated( "<deprecated>TrUe</deprecated>" ) );
    Assert.assertEquals( false, testableWadlParser.isDeprecated( "<deprecated>false</deprecated>" ) );
    Assert.assertEquals( false, testableWadlParser.isDeprecated( "" ) );
    Assert.assertEquals( true, testableWadlParser.isDeprecated( TEST_DATA_MULTILINE ) );
  }

}
