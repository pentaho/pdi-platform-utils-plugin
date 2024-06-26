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
 * Copyright 2006 - 2024 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.di.baserver.utils.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.ui.core.PropsUI;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;


public class SeparatorBuilderTest {
  SeparatorBuilder separatorBuilder, separatorBuilderSpy;
  Composite parent = mock( Composite.class );
  PropsUI propsUI = mock( PropsUI.class );

  @Before
  public void setUp() throws Exception {
    separatorBuilder = new SeparatorBuilder( parent, propsUI );
    separatorBuilderSpy = spy( separatorBuilder );
  }

  @Test
  public void testCreateWidget() throws Exception {
    Label labelMock = mock( Label.class );
    doReturn( labelMock ).when( separatorBuilderSpy ).createLabel( Mockito.<Composite>any(), anyInt() );

    Label label = separatorBuilderSpy.createWidget( parent );
    assertNotNull( label );
  }
}
