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
 * Copyright 2017 - 2017 Hitachi Vantara.  All rights reserved.
 */

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
