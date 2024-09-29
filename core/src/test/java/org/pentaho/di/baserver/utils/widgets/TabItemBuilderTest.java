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

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.ui.core.PropsUI;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


public class TabItemBuilderTest {
  TabItemBuilder tabItemBuilder, tabItemBuilderSpy;
  CTabFolder parent = mock( CTabFolder.class );
  PropsUI propsUI = mock( PropsUI.class );

  @Before
  public void setUp() throws Exception {
    tabItemBuilder = new TabItemBuilder( parent, propsUI );
    tabItemBuilderSpy = spy( tabItemBuilder );
  }

  @Test
  public void testCreateWidget() throws Exception {
    String text = "tabitem-text"; //$NON-NLS-1$
    Composite compositeMock = mock( Composite.class );
    doReturn( compositeMock ).when( tabItemBuilderSpy ).createServerTabItemControl( Mockito.<Composite>any(), anyInt() );
    CTabItem cTabItemMock = mock( CTabItem.class );
    doReturn( cTabItemMock ).when( tabItemBuilderSpy ).createCTabItem( Mockito.<CTabFolder>any(), anyInt() );

    when( tabItemBuilderSpy.setTopPlacement( anyInt() ) ).thenCallRealMethod();

    tabItemBuilderSpy.setText( text );
    Composite composite = tabItemBuilderSpy.createWidget( parent );

    assertNotNull( composite );
    assertEquals( 0, tabItemBuilderSpy.getTopPlacement() );
    assertEquals( 100, tabItemBuilderSpy.getBottomPlacement() );
    assertEquals( 0, tabItemBuilderSpy.getLeftPlacement() );
    assertEquals( 100, tabItemBuilderSpy.getRightPlacement() );

  }
}
