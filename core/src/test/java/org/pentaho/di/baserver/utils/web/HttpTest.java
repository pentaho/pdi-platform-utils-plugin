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


package org.pentaho.di.baserver.utils.web;

import org.junit.Test;


import static org.junit.Assert.assertEquals;

public class HttpTest {

  @Test
  public void testGetHttpMethod() {
    assertEquals( Http.POST, Http.getHttpMethod( "POST" ) );
    assertEquals( Http.POST, Http.getHttpMethod( "post" ) );
    assertEquals( Http.PUT, Http.getHttpMethod( "PUT" ) );
    assertEquals( Http.PUT, Http.getHttpMethod( "put" ) );
    assertEquals( Http.GET, Http.getHttpMethod( "GET" ) );
    assertEquals( Http.GET, Http.getHttpMethod( "get" ) );
    assertEquals( Http.DELETE, Http.getHttpMethod( "DELETE" ) );
    assertEquals( Http.DELETE, Http.getHttpMethod( "delete" ) );
    assertEquals( Http.HEAD, Http.getHttpMethod( "HEAD" ) );
    assertEquals( Http.HEAD, Http.getHttpMethod( "head" ) );
    assertEquals( Http.OPTIONS, Http.getHttpMethod( "OPTIONS" ) );
    assertEquals( Http.OPTIONS, Http.getHttpMethod( "options" ) );

    assertEquals( Http.GET, Http.getHttpMethod( "NOT SUPPORTED" ) );
  }
}
