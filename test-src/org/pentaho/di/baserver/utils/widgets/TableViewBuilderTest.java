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

package org.pentaho.di.baserver.utils.widgets;

import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

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
    doReturn( tableViewMock ).when( tableViewBuilderSDpy ).createTableView( any( VariableSpace.class ),
        any( Composite.class ), anyInt(), any( ColumnInfo[].class), anyInt(), any( ModifyListener.class ),
        any( PropsUI.class ) );
    Table tableMock = mock( Table.class );
    doReturn( tableMock ).when( tableViewMock ).getTable();

    TableColumn[] tableColumnMock = new TableColumn[0];    ;
    doReturn( tableColumnMock ).when( tableMock ).getColumns();

    tableViewBuilderSDpy.addColumnInfo( new ColumnInfo( "col", 0) );
    tableViewBuilderSDpy.setRowsCount( 0 );
    tableViewBuilderSDpy.setModifyListener( modifyListener );
    TableView tableView = tableViewBuilderSDpy.createWidget( parent );
    assertNotNull( tableView );
  }
}
