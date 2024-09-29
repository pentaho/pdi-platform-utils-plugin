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
