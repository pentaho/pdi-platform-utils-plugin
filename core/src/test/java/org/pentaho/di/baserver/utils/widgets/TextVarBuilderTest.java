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
 * Copyright 2006 - 2017 Hitachi Vantara.  All rights reserved.
 */

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
