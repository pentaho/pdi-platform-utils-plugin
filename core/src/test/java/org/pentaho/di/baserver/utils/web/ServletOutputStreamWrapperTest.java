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
