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
 * Copyright 2006 - 2024 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.di.baserver.utils.web;

import org.junit.Before;
import org.junit.Test;

import java.io.OutputStream;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class ServletOutputStreamWrapperTest {
  ServletOutputStreamWrapper wrapper;
  OutputStream stream;

  @Before
  public void setUp() {
    stream = mock( OutputStream.class );
    wrapper = new ServletOutputStreamWrapper( stream );
  }

  @Test
  public void testSetOutputStream() throws Exception {
    assertEquals( stream, wrapper.getOutputStream() );
  }

  @Test
  public void testWrite() throws Exception {
    doNothing().when( stream ).write( anyInt() );
    wrapper.write( 3 );
    verify( stream, times( 1 ) ).write( anyInt() );
  }

  @Test
  public void testFlush() throws Exception {
    doNothing().when( stream ).flush();
    wrapper.flush();
    verify( stream, times( 1 ) ).flush();
  }

  @Test
  public void testClose() throws Exception {
    doNothing().when( stream ).close();
    wrapper.close();
    verify( stream, times( 1 ) ).close();
  }
}
