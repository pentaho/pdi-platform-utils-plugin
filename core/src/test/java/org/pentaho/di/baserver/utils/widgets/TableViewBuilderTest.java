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

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;


public class TableViewBuilderTest {
  TableViewBuilder tableViewBuilder, tableViewBuilderSDpy;
  Composite parent = mock( Composite.class );
  PropsUI propsUI = mock( PropsUI.class );
  VariableSpace variableSpace = mock( VariableSpace.class );
  ModifyListener modifyListener = mock( ModifyListener.class );

  @Before
  public void setUp() throws Exception {
    tableViewBuilder = new TableViewBuilder( propsUI, parent, variableSpace );
    tableViewBuilderSDpy = spy( tableViewBuilder );
  }

  @Test
  public void testCreateWidget() throws Exception {
    TableView tableViewMock = mock( TableView.class );
    doReturn( tableViewMock ).when( tableViewBuilderSDpy ).createTableView( Mockito.<VariableSpace>any(),
        Mockito.<Composite>any(), anyInt(), any( ColumnInfo[].class ), anyInt(), Mockito.<ModifyListener>any(),
        Mockito.<PropsUI>any() );
    Table tableMock = mock( Table.class );
    doReturn( tableMock ).when( tableViewMock ).getTable();

    TableColumn[] tableColumnMock = new TableColumn[ 0 ]; ;
    doReturn( tableColumnMock ).when( tableMock ).getColumns();

    tableViewBuilderSDpy.addColumnInfo( new ColumnInfo( "col", 0 ) );
    tableViewBuilderSDpy.setRowsCount( 0 );
    tableViewBuilderSDpy.setModifyListener( modifyListener );
    TableView tableView = tableViewBuilderSDpy.createWidget( parent );
    assertNotNull( tableView );
  }
}
