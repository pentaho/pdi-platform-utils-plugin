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

package org.pentaho.di.baserver.utils.web;

import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ServletInputStreamWrapperTest {
  ServletInputStreamWrapper wrapper;
  InputStream stream;

  @Before
  public void setUp() {
    stream = mock( InputStream.class );
    wrapper = new ServletInputStreamWrapper( stream );
  }

  @Test
  public void testSetOutputStream() throws Exception {
    assertEquals( stream, wrapper.getInputStream() );
  }

  @Test
  public void testRead() throws Exception {
    doReturn( 1 ).when( stream ).read();
    wrapper.read();
    verify( stream, times( 1 ) ).read();
  }

  @Test
  public void testClose() throws Exception {
    doNothing().when( stream ).close();
    wrapper.close();
    verify( stream, times( 1 ) ).close();
  }
}
