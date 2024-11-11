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

import java.io.InputStream;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


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
