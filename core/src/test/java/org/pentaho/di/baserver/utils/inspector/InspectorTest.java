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

import org.apache.http.HttpStatus;
import org.dom4j.Document;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.baserver.utils.web.Http;
import org.pentaho.di.baserver.utils.web.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.eq;

public class InspectorTest {

  private static final String DEFAULT_PLATFORM_NAME = "platform";

  private Inspector inspector, inspectorSpy;

  private String SERVER_URL = "http://localhost:8080/pentaho",
    USERNAME = "admin",
    PASSWORD = "password";

  @Before
  public void setUp() throws Exception {
    inspector = Inspector.getInstance();
    inspector.getEndpointsTree().clear();
    inspectorSpy = spy( inspector );
  }

  @Test
  public void testInspectServer() throws Exception {
    doReturn( true ).when( inspectorSpy ).inspectModuleNames();
    assertTrue( inspectorSpy.inspectServer( SERVER_URL, USERNAME, PASSWORD ) );
    doReturn( false ).when( inspectorSpy ).inspectModuleNames();
    assertFalse( inspectorSpy.inspectServer( SERVER_URL, USERNAME, PASSWORD ) );
    verify( inspectorSpy, times( 2 ) ).inspectModuleNames();
    assertEquals( SERVER_URL, inspectorSpy.getServerUrl() );
    assertEquals( USERNAME, inspectorSpy.getUserName() );
    assertEquals( PASSWORD, inspectorSpy.getPassword() );
  }

  @Test
  public void testCheckServerStatus() throws Exception {
    doReturn( null ).when( inspectorSpy ).callHttp( any() );
    assertEquals( -1, inspectorSpy.checkServerStatus( SERVER_URL, USERNAME, PASSWORD ) );

    Response response = mock( Response.class );
    doReturn( 200 ).when( response ).getStatusCode();
    doReturn( response ).when( inspectorSpy ).callHttp( any() );

    assertEquals( 200, inspectorSpy.checkServerStatus( SERVER_URL, USERNAME, PASSWORD ) );
  }

  @Test
  public void testGetModuleNames() throws Exception {
    TreeMap<String, TreeMap<String, LinkedList<Endpoint>>> endpoints = null;
    doReturn( endpoints ).when( inspectorSpy ).getEndpointsTree();
    assertEquals( inspectorSpy.getModuleNames(), Collections.emptyList() );

    endpoints = new TreeMap<String, TreeMap<String, LinkedList<Endpoint>>>();
    doReturn( endpoints ).when( inspectorSpy ).getEndpointsTree();
    assertEquals( inspectorSpy.getModuleNames(), endpoints.keySet() );
  }

  @Test
  public void testGetDefaultModuleName() throws Exception {
    TreeMap<String, TreeMap<String, LinkedList<Endpoint>>> endpoints = null;
    doReturn( endpoints ).when( inspectorSpy ).getEndpointsTree();
    assertEquals( inspectorSpy.getDefaultModuleName(), "" );

    endpoints = mock( TreeMap.class );
    doReturn( endpoints ).when( inspectorSpy ).getEndpointsTree();
    assertEquals( inspectorSpy.getDefaultModuleName(), DEFAULT_PLATFORM_NAME );
  }

  @Test
  public void testGetEndpointPaths() throws Exception {
    String moduleName = "myModule";

    Map<String, LinkedList<Endpoint>> moduleEndpoints = new HashMap<String, LinkedList<Endpoint>>();
    doReturn( moduleEndpoints ).when( inspectorSpy ).getEndpointMap( moduleName );
    assertEquals( moduleEndpoints.keySet(), inspectorSpy.getEndpointPaths( moduleName ) );
    verify( inspectorSpy, times( 1 ) ).getEndpointMap( moduleName );
  }

  @Test
  public void testGetDefaultEndpointPath() throws Exception {
    String moduleName = "myModule";

    doReturn( null ).when( inspectorSpy ).getEndpointPaths( moduleName );
    assertEquals( inspectorSpy.getDefaultEndpointPath( moduleName ), "" );

    Map<String, LinkedList<Endpoint>> moduleEndpoints = new HashMap<String, LinkedList<Endpoint>>();
    doReturn( moduleEndpoints.keySet() ).when( inspectorSpy ).getEndpointPaths( moduleName );
    assertEquals( inspectorSpy.getDefaultEndpointPath( moduleName ), "" );

    moduleEndpoints.put( "aa", new LinkedList<Endpoint>() );
    assertEquals( "aa", inspectorSpy.getDefaultEndpointPath( moduleName ) );
  }

  @Test
  public void testGetEndpoints() throws Exception {
    String moduleName = "myModule",
      path = "myPath";

    Map<String, LinkedList<Endpoint>> moduleEndpoints = mock( Map.class );
    doReturn( null ).when( moduleEndpoints ).get( path );
    doReturn( moduleEndpoints ).when( inspectorSpy ).getEndpointMap( moduleName );
    assertEquals( inspectorSpy.getEndpoints( moduleName, path ), Collections.emptyList() );

    doReturn( new ArrayList() ).when( moduleEndpoints ).get( path );
    assertEquals( inspectorSpy.getEndpoints( moduleName, path ), new ArrayList() );
  }

  @Test
  public void testGetDefaultEndpoint() throws Exception {
    String moduleName = "myModule",
      path = "myPath",
      endpointId = "myEndpoint";

    doReturn( null ).when( inspectorSpy ).getEndpoints( moduleName, path );
    assertNull( inspectorSpy.getDefaultEndpoint( moduleName, path ) );

    List<Endpoint> moduleEndpoints = new LinkedList<Endpoint>();
    doReturn( moduleEndpoints ).when( inspectorSpy ).getEndpoints( moduleName, path );
    assertNull( inspectorSpy.getDefaultEndpoint( moduleName, path ) );

    Endpoint e = new Endpoint();
    e.setId( endpointId );
    moduleEndpoints.add( e );
    assertEquals( inspectorSpy.getDefaultEndpoint( moduleName, path ), e );
  }

  @Test
  public void testGetEndpointMap() throws Exception {
    String moduleName = "myModule";

    doReturn( null ).when( inspectorSpy ).getModuleEndpoints( moduleName );
    doReturn( false ).when( inspectorSpy ).inspectEndpoints( moduleName );
    assertEquals( inspectorSpy.getEndpointMap( moduleName ), Collections.emptyMap() );
    verify( inspectorSpy, times( 1 ) ).inspectEndpoints( moduleName );
    verify( inspectorSpy, times( 1 ) ).getModuleEndpoints( moduleName );

    doReturn( null ).when( inspectorSpy ).getModuleEndpoints( moduleName );
    doReturn( true ).when( inspectorSpy ).inspectEndpoints( moduleName );
    assertEquals( inspectorSpy.getEndpointMap( moduleName ), null );
    verify( inspectorSpy, times( 2 ) ).inspectEndpoints( moduleName );
    verify( inspectorSpy, times( 3 ) ).getModuleEndpoints( moduleName );

    Map<String, LinkedList<Endpoint>> endpointMap = new TreeMap<String, LinkedList<Endpoint>>();
    doReturn( endpointMap ).when( inspectorSpy ).getModuleEndpoints( moduleName );
    doReturn( true ).when( inspectorSpy ).inspectEndpoints( moduleName );
    inspectorSpy.getEndpointMap( moduleName );
    verify( inspectorSpy, times( 2 ) ).inspectEndpoints( moduleName );
    verify( inspectorSpy, times( 4 ) ).getModuleEndpoints( moduleName );
  }

  @Test
  public void testInspectModuleNames() throws Exception {
    doReturn( SERVER_URL ).when( inspectorSpy ).getServerUrl();

    doReturn( null ).when( inspectorSpy ).callHttp( SERVER_URL + "/api/plugin-manager/ids" );
    assertFalse( inspectorSpy.inspectModuleNames() );

    Response response = mock( Response.class );
    doReturn( HttpStatus.SC_INTERNAL_SERVER_ERROR ).when( response ).getStatusCode();
    doReturn( response ).when( inspectorSpy ).callHttp( SERVER_URL + "/api/plugin-manager/ids" );
    assertFalse( inspectorSpy.inspectModuleNames() );

    doReturn( HttpStatus.SC_OK ).when( response ).getStatusCode();
    doReturn( "{ strings: ['url','data-access','cgg','marketplace','xaction','jpivot']}" ).when( response ).getResult();
    assertTrue( inspectorSpy.inspectModuleNames() );
    assertEquals( inspectorSpy.getEndpointsTree().size(), 7 );
  }

  @Test
  public void testInspectEndpoints() throws Exception {
    String moduleName = "myModule",
      applicationWadlEndpoint = SERVER_URL + moduleName + "/application.wadl";

    doReturn( "" ).when( inspectorSpy ).getApplicationWadlEndpoint( moduleName );
    doReturn( null ).when( inspectorSpy ).callHttp( any() );
    assertFalse( inspectorSpy.inspectEndpoints( moduleName ) );

    Response response = mock( Response.class );
    doReturn( HttpStatus.SC_INTERNAL_SERVER_ERROR ).when( response ).getStatusCode();
    doReturn( response ).when( inspectorSpy ).callHttp( any() );
    assertFalse( inspectorSpy.inspectEndpoints( moduleName ) );

    doReturn( HttpStatus.SC_OK ).when( response ).getStatusCode();
    doReturn( null ).when( inspectorSpy ).getDocument( any() );
    assertFalse( inspectorSpy.inspectEndpoints( moduleName ) );

    Document doc = mock( Document.class );
    doReturn( doc ).when( inspectorSpy ).getDocument( any() );
    WadlParser parser = mock( WadlParser.class );

    List<Endpoint> endpointList = new ArrayList<Endpoint>();
    Endpoint endpoint1 = new Endpoint(),
      endpoint2 = new Endpoint(),
      endpoint3 = new Endpoint();

    endpoint1.setPath( "path" );
    endpoint2.setPath( "path" );
    endpoint3.setPath( "" );

    endpointList.add( endpoint1 );
    endpointList.add( endpoint2 );
    endpointList.add( endpoint3 );

    doReturn( endpointList ).when( parser ).getEndpoints( doc );
    doReturn( parser ).when( inspectorSpy ).getParser();
    assertTrue( inspectorSpy.inspectEndpoints( moduleName ) );
  }

  @Test
  public void testGetDocument() throws Exception {
    String result = "<application xmlns=\"http://wadl.dev.java.net/2009/02\">\n"
      + "  <resources base=\"http://localhost:8080/pentaho/api/\">\n" + "    <resource path=\"\">\n"
      + "      <method id=\"doGetRoot\" name=\"GET\">\n" + "        <response>\n"
      + "          <representation mediaType=\"*/*\"/>\n" + "        </response>\n" + "      </method>\n"
      + "      <resource path=\"docs\">\n" + "        <method id=\"doGetDocs\" name=\"GET\">\n"
      + "          <response>\n" + "            <representation mediaType=\"*/*\"/>\n" + "          </response>\n"
      + "        </method>\n" + "      </resource>\n" + "    </resource>\n" + "  </resources>\n" + "</application>\n";

    assertNotNull( inspectorSpy.getDocument( result ) );
    assertNull( inspectorSpy.getDocument( "" ) );
  }

  @Test
  public void testGetApplicationWadlEndpoint() throws Exception {
    String moduleName = "myModule";

    doReturn( SERVER_URL ).when( inspectorSpy ).getServerUrl();
    assertEquals( inspectorSpy.getApplicationWadlEndpoint( moduleName ),
      SERVER_URL + "/plugin/" + moduleName + "/api/application.wadl" );
    assertEquals( inspectorSpy.getApplicationWadlEndpoint( DEFAULT_PLATFORM_NAME ),
      SERVER_URL + "/api/application.wadl" );

    doReturn( SERVER_URL + "/" ).when( inspectorSpy ).getServerUrl();
    assertEquals( inspectorSpy.getApplicationWadlEndpoint( moduleName ),
      SERVER_URL + "/plugin/" + moduleName + "/api/application.wadl" );
    assertEquals( inspectorSpy.getApplicationWadlEndpoint( DEFAULT_PLATFORM_NAME ),
      SERVER_URL + "/api/application.wadl" );
  }

  @Test
  public void testCallHttp() {
    inspectorSpy.callHttp( "test" );

    verify( inspectorSpy ).getUserName();
    verify( inspectorSpy ).getPassword();
  }

  @Test
  public void testRefreshSettings() {
    inspectorSpy.refreshSettings( null, null, null );
    assertNull( inspectorSpy.getServerUrl() );
    assertNull( inspectorSpy.getUserName() );
    assertNull( inspectorSpy.getPassword() );
    inspectorSpy.refreshSettings( "serverUrl", "username", "password" );
    assertEquals( "serverUrl", inspectorSpy.getServerUrl() );
    assertEquals( "username", inspectorSpy.getUserName() );
    assertEquals( "password", inspectorSpy.getPassword() );
  }

  @Test
  public void testGetEndpoint() throws Exception {
    Map<String, LinkedList<Endpoint>> endpointMap = initializeEndpointMap();
    doReturn( endpointMap ).when( inspectorSpy ).getEndpointMap( eq( "moduleName" ) );
    Endpoint endpoint = inspectorSpy.getEndpoint( "moduleName", "path1", Http.PUT );
    assertNotNull( endpoint );
    assertEquals( Http.PUT, endpoint.getHttpMethod() );
    endpoint = inspectorSpy.getEndpoint( "moduleName", "path1", Http.GET );
    assertNotNull( endpoint );
    assertEquals( Http.GET, endpoint.getHttpMethod() );
    endpoint = inspectorSpy.getEndpoint( "moduleName", "path1", Http.DELETE );
    assertNull( endpoint );
    endpoint = inspectorSpy.getEndpoint( "moduleName", "path5", Http.GET );
    assertNotNull( endpoint );
    assertEquals( Http.GET, endpoint.getHttpMethod() );
    endpoint = inspectorSpy.getEndpoint( "moduleName", "path3", Http.POST );
    assertNotNull( endpoint );
    assertEquals( Http.POST, endpoint.getHttpMethod() );
    assertNull( inspectorSpy.getEndpoint( "moduleName", "path not exist", Http.POST ) );
  }

  private Map<String, LinkedList<Endpoint>> initializeEndpointMap() {
    Map<String, LinkedList<Endpoint>> endpointMap = new TreeMap<String, LinkedList<Endpoint>>();
    LinkedList<Endpoint> endpoints = new LinkedList<>();
    endpoints.add( createEndpoint( Http.POST ) );
    endpoints.add( createEndpoint( Http.GET ) );
    endpoints.add( createEndpoint( Http.PUT ) );
    endpointMap.put( "path1", endpoints );
    endpoints = new LinkedList<>();
    endpoints.add( createEndpoint( Http.DELETE ) );
    endpointMap.put( "path2", endpoints );
    endpoints = new LinkedList<>();
    endpoints.add( createEndpoint( Http.POST ) );
    endpoints.add( createEndpoint( Http.PUT ) );
    endpointMap.put( "path3", endpoints );
    endpoints = new LinkedList<>();
    endpoints.add( createEndpoint( Http.HEAD ) );
    endpointMap.put( "path4", endpoints );
    endpoints = new LinkedList<>();
    endpoints.add( createEndpoint( Http.POST ) );
    endpoints.add( createEndpoint( Http.GET ) );
    endpointMap.put( "path5", endpoints );
    return endpointMap;
  }

  private Endpoint createEndpoint( Http httpMethod ) {
    Endpoint endpoint = new Endpoint();
    endpoint.setHttpMethod( httpMethod );
    return endpoint;
  }

}
