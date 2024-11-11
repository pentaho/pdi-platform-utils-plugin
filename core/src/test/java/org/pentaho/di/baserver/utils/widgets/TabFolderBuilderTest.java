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
import org.eclipse.swt.widgets.Composite;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.ui.core.PropsUI;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;


public class TabFolderBuilderTest {
  TabFolderBuilder tabFolderBuilder, tabFolderBuilderSpy;
  Composite parent = mock( Composite.class );
  PropsUI propsUI = mock( PropsUI.class );

  @Before
  public void setUp() throws Exception {
    tabFolderBuilder = new TabFolderBuilder( parent, propsUI );
    tabFolderBuilderSpy = spy( tabFolderBuilder );
  }

  @Test
  public void testCreateWidget() throws Exception {
    CTabFolder cTabFolderMock = mock( CTabFolder.class );
    doReturn( cTabFolderMock ).when( tabFolderBuilderSpy ).createCTabFolder( Mockito.<Composite>any(), anyInt() );

    CTabFolder label = tabFolderBuilderSpy.createWidget( parent );
    assertNotNull( label );
  }
}
