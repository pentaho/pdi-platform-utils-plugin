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

import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.baserver.utils.web.Http;
import org.pentaho.di.baserver.utils.web.HttpParameter;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.pentaho.di.core.util.Assert.assertNotNull;
import static org.pentaho.di.core.util.Assert.assertNull;
import static org.pentaho.di.core.util.Assert.assertTrue;

public class EndpointTest {
  Endpoint endpoint;

  @Before
  public void setup() {
    endpoint = new Endpoint();
  }

  @Test
  public void testConstructor() {
    assertEquals( endpoint.getParamDefinitions().size(), 0 );
  }

  @Test
  public void testGet() {
    String id = "1234";
    endpoint.setId( id );
    assertEquals( endpoint.getId(), id );

    String path = "/path";
    endpoint.setPath( path );
    assertEquals( endpoint.getPath(), path );

    Http httpMethod = Http.GET;
    endpoint.setHttpMethod( httpMethod );
    assertEquals( endpoint.getHttpMethod(), httpMethod );

    ParamDefinition paramDefinition1 = new ParamDefinition( "paramName1" );
    ParamDefinition paramDefinition2 = new ParamDefinition( "paramName2" );
    endpoint.getParamDefinitions().add( paramDefinition1 );
    endpoint.getParamDefinitions().add( paramDefinition2 );
    assertTrue( endpoint.getParamDefinitions().contains( paramDefinition1 ) );
    assertTrue( endpoint.getParamDefinitions().contains( paramDefinition2 ) );

  }

  @Test
  public void testCompareTo() {
    String id = "1234",
      id2 = "12345678";
    endpoint.setId( id );

    Endpoint endpoint2 = new Endpoint();
    endpoint2.setId( id2 );

    assertEquals( endpoint.compareTo( endpoint2 ), id.compareTo( id2 ) );
  }

  @Test
  public void testEquals() {
    String id = "1234";
    endpoint.setId( id );

    Endpoint endpoint2 = new Endpoint();
    endpoint2.setId( id );
    assertTrue( endpoint.equals( endpoint2 ) );
    assertFalse( endpoint.equals( null ) );
    assertFalse( endpoint.equals( new ParamDefinition() ) );

    endpoint2.setId( "12345" );
    assertFalse( endpoint.equals( endpoint2 ) );
  }

  @Test
  public void testHashCode() {
    String id = "1234";
    endpoint.setId( id );

    assertEquals( id.hashCode(), endpoint.hashCode() );
  }

  @Test
  public void testGetParameterDefinition() {
    endpoint.getParamDefinitions().addAll( initializeTestSet() );
    assertNull( endpoint.getParameterDefinition( null ) );
    assertNotNull( endpoint.getParameterDefinition( "param1" ) );
    assertNotNull( endpoint.getParameterDefinition( "param2" ) );
    assertNotNull( endpoint.getParameterDefinition( "param3" ) );
    assertNotNull( endpoint.getParameterDefinition( "param4" ) );
    assertNull( endpoint.getParameterDefinition( "param not exist" ) );
  }

  @Test
  public void testGetParameterType() {
    endpoint.getParamDefinitions().addAll( initializeTestSet() );
    assertNull( endpoint.getParameterType( null ) );
    assertNull( endpoint.getParameterType( "param not exist" ) );
    assertEquals( HttpParameter.ParamType.QUERY, endpoint.getParameterType( "param1" ) );
    assertEquals( HttpParameter.ParamType.BODY, endpoint.getParameterType( "param2" ) );
    assertEquals( HttpParameter.ParamType.QUERY, endpoint.getParameterType( "param3" ) );
    assertEquals( HttpParameter.ParamType.QUERY, endpoint.getParameterType( "param4" ) );
    assertEquals( HttpParameter.ParamType.BODY, endpoint.getParameterType( "param5" ) );
  }

  Set<ParamDefinition> initializeTestSet() {
    Set<ParamDefinition> paramDefinitions = new HashSet<>();
    paramDefinitions.add( new ParamDefinition( "param1", HttpParameter.ParamType.QUERY ) );
    paramDefinitions.add( new ParamDefinition( "param2", HttpParameter.ParamType.BODY ) );
    paramDefinitions.add( new ParamDefinition( "param3", HttpParameter.ParamType.QUERY ) );
    paramDefinitions.add( new ParamDefinition( "param4", HttpParameter.ParamType.QUERY ) );
    paramDefinitions.add( new ParamDefinition( "param5", HttpParameter.ParamType.BODY ) );
    return paramDefinitions;
  }
}
