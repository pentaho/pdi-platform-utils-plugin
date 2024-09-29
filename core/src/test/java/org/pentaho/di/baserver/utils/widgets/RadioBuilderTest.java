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

import org.eclipse.swt.events.SelectionListener;
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
import static org.mockito.Mockito.when;


public class RadioBuilderTest {
  RadioBuilder radioBuilder, radioBuilderSpy;
  Composite parent = mock( Composite.class );
  PropsUI propsUI = mock( PropsUI.class );

  @Before
  public void setUp() throws Exception {
    radioBuilder = new RadioBuilder( parent, propsUI );
    radioBuilderSpy = spy( radioBuilder );
  }

  @Test
  public void testCreateWidget() throws Exception {
    String text = "radio-text"; //$NON-NLS-1$
    Button buttonMock = mock( Button.class );
    doReturn( buttonMock ).when( radioBuilderSpy ).createButton( Mockito.<Composite>any(), anyInt() );
    doReturn( text ).when( buttonMock ).getText();
    when( buttonMock.getListeners( anyInt() ) ).thenCallRealMethod();

    radioBuilderSpy.setText( text );
    radioBuilderSpy.addSelectionListener( mock( SelectionListener.class ) );
    Button button = radioBuilderSpy.createWidget( parent );
    assertEquals( text, button.getText() );
    verify( button, times( 1 ) ).setText( text );
    verify( button, times( 1 ) ).addSelectionListener( Mockito.<SelectionListener>any() );
  }
}
