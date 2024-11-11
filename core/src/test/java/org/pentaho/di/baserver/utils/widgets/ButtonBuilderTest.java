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

import org.eclipse.swt.widgets.Button;
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


public class ButtonBuilderTest {
  ButtonBuilder buttonBuilder, buttonBuilderSpy;
  Composite parent = mock( Composite.class );
  PropsUI propsUI = mock( PropsUI.class );

  @Before
  public void setUp() throws Exception {
    buttonBuilder = new ButtonBuilder( parent, propsUI );
    buttonBuilderSpy = spy( buttonBuilder );
  }

  @Test
  public void testSetLabelText() throws Exception {
    assertEquals( "", buttonBuilder.getLabelText() ); //$NON-NLS-1$
    String labelText = "new-label-text"; //$NON-NLS-1$
    buttonBuilder.setLabelText( labelText );
    assertEquals( labelText, buttonBuilder.getLabelText() );
  }

  @Test
  public void testCreateWidget() throws Exception {
    String text = "button-text"; //$NON-NLS-1$
    Button buttonMock = mock( Button.class );

    doReturn( buttonMock ).when( buttonBuilderSpy ).createButton( Mockito.<Composite>any(), anyInt() );
    doReturn( text ).when( buttonMock ).getText();

    buttonBuilderSpy.setLabelText( text );
    Button button = buttonBuilderSpy.createWidget( parent );

    assertEquals( text, button.getText() );
    verify( button, times( 1 ) ).setText( text );
  }
}
