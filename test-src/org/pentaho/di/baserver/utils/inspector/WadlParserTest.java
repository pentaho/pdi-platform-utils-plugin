package org.pentaho.di.baserver.utils.inspector;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.core.IsAnything.any;
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
}
