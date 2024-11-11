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

import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.baserver.utils.web.HttpParameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.pentaho.di.core.util.Assert.assertTrue;

public class ParamDefinitionTest {
  private ParamDefinition paramDefinition;


  @Before
  public void setup() {
    paramDefinition = new ParamDefinition();
  }

  @Test
  public void testGet() {
    String name = "paramName";
    paramDefinition.setName( name );
    assertEquals( paramDefinition.getName(), name );

    String type = "String";
    paramDefinition.setContentType( type );
    assertEquals( paramDefinition.getContentType(), type );

    HttpParameter.ParamType paramType = HttpParameter.ParamType.QUERY;
    paramDefinition.setParamType( paramType );
    assertEquals( paramType, paramDefinition.getParamType() );
  }

  @Test
  public void testCompareTo() {
    String name = "paramName",
      name2 = "paramName1234";
    paramDefinition.setName( name );

    assertEquals( paramDefinition.compareTo( paramDefinition ), name.compareTo( name ) );

    ParamDefinition paramDefinition2 = new ParamDefinition();
    paramDefinition2.setName( name2 );

    assertEquals( paramDefinition.compareTo( paramDefinition2 ), name.compareTo( name2 ) );
  }

  @Test
  public void testEquals() {
    String name = "paramName";
    paramDefinition.setName( name );

    assertTrue( paramDefinition.equals( paramDefinition ) );

    ParamDefinition paramDefinition2 = new ParamDefinition();
    paramDefinition2.setName( name );
    assertTrue( paramDefinition.equals( paramDefinition2 ) );

    assertFalse( paramDefinition.equals( null ) );
    assertFalse( paramDefinition.equals( new Endpoint() ) );

    paramDefinition2.setName( "12345" );
    assertFalse( paramDefinition.equals( paramDefinition2 ) );
  }

  @Test
  public void testHashCode() {
    String name = "paramName";
    paramDefinition.setName( name );

    assertEquals( name.hashCode(), paramDefinition.hashCode() );
  }

}
