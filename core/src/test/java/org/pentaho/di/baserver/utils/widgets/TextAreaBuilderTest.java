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
import org.pentaho.di.ui.core.PropsUI;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class TextAreaBuilderTest {
  TextAreaBuilder textAreaBuilder;
  Composite parent = mock( Composite.class );
  PropsUI propsUI = mock( PropsUI.class );

  @Before
  public void setUp() throws Exception {
    textAreaBuilder = new TextAreaBuilder( parent, propsUI );
  }

  @Test
  public void testSetDefaultTextText() throws Exception {
    assertEquals( "", textAreaBuilder.getDefaultText() ); //$NON-NLS-1$
    String labelText = "new-textarea-text"; //$NON-NLS-1$
    textAreaBuilder.setDefaultText( labelText );
    assertEquals( labelText, textAreaBuilder.getDefaultText() );
  }
}
