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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.ui.core.PropsUI;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;


public class LabelBuilderTest {
  LabelBuilder labelBuilder, labelBuilderSpy;
  Composite parent = mock( Composite.class );
  PropsUI propsUI = mock( PropsUI.class );

  @Before
  public void setUp() throws Exception {
    labelBuilder = new LabelBuilder( parent, propsUI );
    labelBuilderSpy = spy( labelBuilder );
  }

  @Test
  public void testSetText() throws Exception {
    assertEquals( "", labelBuilder.getText() ); //$NON-NLS-1$
    String labelText = "new-label-text"; //$NON-NLS-1$
    labelBuilder.setText( labelText );
    assertEquals( labelText, labelBuilder.getText() );
  }

  @Test
  public void testCreateWidget() throws Exception {
    String text = "label-text"; //$NON-NLS-1$
    Label labelMock = mock( Label.class );
    doReturn( labelMock ).when( labelBuilderSpy ).createLabel( Mockito.<Composite>any(), anyInt() );
    doReturn( text ).when( labelMock ).getText();

    labelBuilderSpy.setText( text );
    Label label = labelBuilderSpy.createWidget( parent );

    assertEquals( text, label.getText() );
  }
}
