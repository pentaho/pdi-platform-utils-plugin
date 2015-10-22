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
 * Copyright 2006 - 2015 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.di.baserver.utils.widgets.fields;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.*;

public class FieldTest {
  Field field;

  @Before
  public void setUp() {
    field = mock( Field.class, Mockito.CALLS_REAL_METHODS );
  }

  @Test
  public void testSetLabel() throws Exception {
    assertNull( field.getLabel() );
    Label label = mock( Label.class );
    field.setLabel( label );
    assertEquals( label, field.getLabel() );
  }

  @Test
  public void testSetControl() throws Exception {
    assertNull( field.getControl() );
    Control control = mock( Control.class );
    field.setControl( control );
    assertEquals( control, field.getControl() );
  }
}
