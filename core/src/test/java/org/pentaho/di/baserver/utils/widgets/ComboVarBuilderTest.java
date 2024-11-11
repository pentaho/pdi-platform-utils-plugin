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
import org.mockito.Mockito;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.ComboVar;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


public class ComboVarBuilderTest {
  ComboVarBuilder comboVarBuilder, comboVarBuilderSpy;
  Composite parent = mock( Composite.class );
  PropsUI propsUI = mock( PropsUI.class );
  VariableSpace variableSpace = mock( VariableSpace.class );

  @Before
  public void setUp() throws Exception {
    comboVarBuilder = new ComboVarBuilder( parent, propsUI, variableSpace );
    comboVarBuilderSpy = spy( comboVarBuilder );
  }

  @Test
  public void testCreateWidget() throws Exception {
    ComboVar comboVarMock = mock( ComboVar.class );
    doReturn( comboVarMock ).when( comboVarBuilderSpy )
      .createComboVar( Mockito.<VariableSpace>any(), Mockito.<Composite>any(), anyInt() );
    when( comboVarBuilderSpy.addItem( any() ) ).thenCallRealMethod();

    ComboVar comboVar = comboVarBuilderSpy.createWidget( parent );
    assertNotNull( comboVar );
    assertEquals( 0, comboVar.getItemCount() );
  }
}
