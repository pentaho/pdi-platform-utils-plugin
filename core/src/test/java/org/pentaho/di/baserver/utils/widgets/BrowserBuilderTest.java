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


package org.pentaho.di.baserver.utils.widgets;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.ui.core.PropsUI;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class BrowserBuilderTest {
  BrowserBuilder browserBuilder, browserBuilderSpy;
  Composite parent = mock( Composite.class );
  PropsUI propsUI = mock( PropsUI.class );

  @Before
  public void setUp() throws Exception {
    browserBuilder = new BrowserBuilder( parent, propsUI );
    browserBuilderSpy = spy( browserBuilder );
  }

  @Test
  public void testSetLabelText() throws Exception {
    assertEquals( "", browserBuilder.getLabelText() ); //$NON-NLS-1$
    String labelText = "new-label-text"; //$NON-NLS-1$
    browserBuilder.setLabelText( labelText );
    assertEquals( labelText, browserBuilder.getLabelText() );
  }

  @Test
  public void testCreateWidget() throws Exception {
    String text = "browser-text"; //$NON-NLS-1$

    Browser browserMock = mock( Browser.class );
    doReturn( browserMock ).when( browserBuilderSpy ).createBrowser( Mockito.<Composite>any(), anyInt() );
    doReturn( text ).when( browserMock ).getText();
    doReturn( null ).when( browserBuilderSpy ).createFont( Mockito.<Composite>any(), Mockito.<FontData>any() );

    browserBuilderSpy.setLabelText( text );
    Browser browser = browserBuilderSpy.createWidget( parent );

    assertEquals( text, browser.getText() );
    verify( browser, times( 1 ) ).setText( text );
    verify( browser, times( 1 ) ).setFont( Mockito.<Font>any() );
  }
}
