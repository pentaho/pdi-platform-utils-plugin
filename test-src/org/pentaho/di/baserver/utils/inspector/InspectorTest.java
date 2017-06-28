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
 * Copyright 2006 - 2017 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.di.baserver.utils.inspector;

import org.apache.http.HttpStatus;
import org.dom4j.Document;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.baserver.utils.web.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
    doReturn( null ).when( inspectorSpy ).callHttp( anyString() );
    assertEquals( -1, inspectorSpy.checkServerStatus( SERVER_URL, USERNAME, PASSWORD ) );

    Response response = mock( Response.class );
    doReturn( 200 ).when( response ).getStatusCode();
    doReturn( response ).when( inspectorSpy ).callHttp( anyString() );

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
    doReturn( null ).when( inspectorSpy ).callHttp( anyString() );
    assertFalse( inspectorSpy.inspectEndpoints( moduleName ) );

    Response response = mock( Response.class );
    doReturn( HttpStatus.SC_INTERNAL_SERVER_ERROR ).when( response ).getStatusCode();
    doReturn( response ).when( inspectorSpy ).callHttp( anyString() );
    assertFalse( inspectorSpy.inspectEndpoints( moduleName ) );

    doReturn( HttpStatus.SC_OK ).when( response ).getStatusCode();
    doReturn( null ).when( inspectorSpy ).getDocument( anyString() );
    assertFalse( inspectorSpy.inspectEndpoints( moduleName ) );

    Document doc = mock( Document.class );
    doReturn( doc ).when( inspectorSpy ).getDocument( anyString() );
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
}
