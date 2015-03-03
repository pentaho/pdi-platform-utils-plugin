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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.pentaho.di.core.util.Assert.assertTrue;

public class QueryParamTest {
  private QueryParam queryParam;
  
  
  @Before
  public void setup() {
    queryParam = new QueryParam();
  }

  @Test
  public void testGet() {
    String name = "paramName";
    queryParam.setName( name );
    assertEquals( queryParam.getName(), name);

    String type = "String";
    queryParam.setType( type );
    assertEquals( queryParam.getType(), type);
  }

  @Test
  public void testCompareTo() {
    String name = "paramName",
        name2 = "paramName1234";
    queryParam.setName( name );

    assertEquals( queryParam.compareTo( queryParam ), name.compareTo( name ) );
    
    QueryParam queryParam2 = new QueryParam();
    queryParam2.setName( name2 );

    assertEquals( queryParam.compareTo( queryParam2 ), name.compareTo( name2 ) );
  }

  @Test
  public void testEquals() {
    String name = "paramName";
    queryParam.setName( name );

    assertTrue( queryParam.equals( queryParam ) );

    QueryParam queryParam2 = new QueryParam();
    queryParam2.setName( name );
    assertTrue( queryParam.equals( queryParam2 ) );

    assertFalse( queryParam.equals( null ) );
    assertFalse( queryParam.equals( new Endpoint() ) );

    queryParam2.setName( "12345" );
    assertFalse( queryParam.equals( queryParam2 ) );
  }

  @Test
  public void testHashCode() {
    String name = "paramName";
    queryParam.setName( name );

    assertEquals( name.hashCode(), queryParam.hashCode() );
  }
  
}
