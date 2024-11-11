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


package org.pentaho.di.baserver.utils.widgets.fields;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;

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
