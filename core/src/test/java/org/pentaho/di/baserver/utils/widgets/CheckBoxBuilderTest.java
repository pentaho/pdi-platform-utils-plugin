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

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.ui.core.PropsUI;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class CheckBoxBuilderTest {
  CheckBoxBuilder checkBoxBuilder, checkBoxBuilderSpy;
  Composite parent = mock( Composite.class );
  PropsUI propsUI = mock( PropsUI.class );

  @Before
  public void setUp() throws Exception {
    checkBoxBuilder = new CheckBoxBuilder( parent, propsUI );
    checkBoxBuilderSpy = spy( checkBoxBuilder );
  }

  @Test
  public void testCreateWidget() throws Exception {
    String text = "checkbox-text"; //$NON-NLS-1$
    Button buttonMock = mock( Button.class );
    doReturn( buttonMock ).when( checkBoxBuilderSpy ).createButton( any( Composite.class ), anyInt() );
    doReturn( text ).when( buttonMock ).getText();
    when( buttonMock.getListeners( anyInt() ) ).thenCallRealMethod();

    checkBoxBuilderSpy.setText( text );
    checkBoxBuilderSpy.addSelectionListener( mock( SelectionListener.class ) );
    Button button = checkBoxBuilderSpy.createWidget( parent );
    assertEquals( text, button.getText() );
    verify( button, times( 1 ) ).setText( text );
    verify( button, times( 1 ) ).addSelectionListener( any( SelectionListener.class ) );
  }
}
