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
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.ui.core.PropsUI;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class TextVarBuilderTest {
  TextVarBuilder textVarBuilder;
  Composite parent = mock( Composite.class );
  PropsUI propsUI = mock( PropsUI.class );
  VariableSpace variableSpace = mock( VariableSpace.class );

  @Before
  public void setUp() throws Exception {
    textVarBuilder = new TextVarBuilder( parent, propsUI, variableSpace );
  }

  @Test
  public void testSetDefaultTextText() {
    assertEquals( "", textVarBuilder.getDefaultText( "" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    String labelText = "new-label-text"; //$NON-NLS-1$
    textVarBuilder.setDefaultText( labelText );
    assertEquals( labelText, textVarBuilder.getDefaultText( "" ) ); //$NON-NLS-1$
  }
}
