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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;

import java.util.ArrayList;

public class TableViewBuilder extends WidgetBuilder<TableView> {
  public static final int MINIMUM_LAST_COLUMN_WIDTH = 20;

  private VariableSpace variableSpace;
  private ArrayList<ColumnInfo> columns = new ArrayList<ColumnInfo>();
  private int rowsCount = 0;
  private ModifyListener modifyListener = null;

  public TableViewBuilder( PropsUI props, Composite parent, VariableSpace variableSpace ) {
    super( parent, props );
    this.variableSpace = variableSpace;
  }

  public TableViewBuilder addColumnInfo( ColumnInfo columnInfo ) {
    this.columns.add( columnInfo );
    return this;
  }

  public TableViewBuilder setRowsCount( int rowsCount ) {
    this.rowsCount = rowsCount;
    return this;
  }

  public TableViewBuilder setModifyListener( ModifyListener modifyListener ) {
    this.modifyListener = modifyListener;
    return this;
  }

  @Override
  protected TableView createWidget( Composite parent ) {
    // create table view
    ColumnInfo[] columnsArray = columns.toArray( new ColumnInfo[ columns.size() ] );
    final TableView tableView = createTableView( this.variableSpace, parent, SWT.BORDER | SWT.FULL_SELECTION
      | SWT.MULTI, columnsArray, this.rowsCount, this.modifyListener, this.props );
    if ( !Const.isRunningOnWebspoonMode() ) {
      final Table table = tableView.getTable();
      // resize last column to remove extra empty column
      ControlAdapter columnResizeListener = new ControlAdapter() {
        @Override public void controlResized( ControlEvent controlEvent ) {
          super.controlResized( controlEvent );
          TableColumn[] tableColumns = table.getColumns();
          int columnsWidth = 0;
          for ( int i = 0; i < tableColumns.length - 1; i++ ) {
            TableColumn column = table.getColumn( i );
            columnsWidth += column.getWidth();
          }
          int lastColumnWidth = table.getClientArea().width - columnsWidth;
          tableColumns[ tableColumns.length - 1 ].setWidth( lastColumnWidth < MINIMUM_LAST_COLUMN_WIDTH
            ? MINIMUM_LAST_COLUMN_WIDTH : lastColumnWidth );
        }
      };
      table.addControlListener( columnResizeListener );
      for ( TableColumn column : table.getColumns() ) {
        column.addControlListener( columnResizeListener );
      }
    }
    return tableView;
  }

  protected TableView createTableView( VariableSpace variableSpace, Composite parent, int flags,
       ColumnInfo[] columnsArray, int rowsCount, ModifyListener modifyListener, PropsUI props ) {
    return new TableView( variableSpace, parent, flags, columnsArray, rowsCount, modifyListener, props );
  }
}
