/*!
* Copyright 2002 - 2014 Webdetails, a Pentaho company.  All rights reserved.
*
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package pt.webdetails.di.baserver.utils.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;

import java.util.ArrayList;

/**
 * @author Marco Vala
 */
public final class TableViewBuilder extends WidgetBuilder {

  private VariableSpace variableSpace;
  private ArrayList<ColumnInfo> columns = new ArrayList<ColumnInfo>();
  private int rowsCount = 0;
  private int width = 0;
  private String labelText = "";
  private int labelWidth = 0;
  private Control top = null;
  private ModifyListener modifyListener = null;

  public TableViewBuilder( PropsUI props, Composite parent ) {
    super( props, parent );
  }

  public TableView build() {
    FormData data;

    // create label for table view
    Label label = new Label( this.parent, SWT.NONE );
    label.setText( this.labelText );

    // place label below the top control
    data = new FormData();
    if ( this.labelWidth > 0 ) {
      data.width = this.labelWidth;
    }
    data.top = new FormAttachment( this.top, Const.MARGIN );
    label.setLayoutData( data );

    // create table view
    ColumnInfo[] columnsArray = columns.toArray( new ColumnInfo[ columns.size() ] );
    TableView tableView = new TableView( this.variableSpace, this.parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI,
      columnsArray, this.rowsCount, this.modifyListener, this.props );

    // attach table view to label
    data = new FormData();
    if ( width > 0 ) {
      data.width = width;
    }
    data.top = new FormAttachment( label, Const.MARGIN );
    data.left = new FormAttachment( 0, Const.MARGIN );
    data.right = new FormAttachment( 100, -Const.MARGIN );
    data.bottom = new FormAttachment( 100, -50 );
    tableView.setLayoutData( data );

    return tableView;
  }

  public TableViewBuilder setWidth( int width ) {
    this.width = width;
    return this;
  }

  public TableViewBuilder setLabelText( String labelText ) {
    this.labelText = labelText;
    return this;
  }

  public TableViewBuilder setLabelWidth( int labelWidth ) {
    this.labelWidth = labelWidth;
    return this;
  }

  public TableViewBuilder setVariableSpace( VariableSpace variableSpace ) {
    this.variableSpace = variableSpace;
    return this;
  }

  public TableViewBuilder addColumnInfo( ColumnInfo columnInfo ) {
    this.columns.add( columnInfo );
    return this;
  }

  public TableViewBuilder setRowsCount( int rowsCount ) {
    this.rowsCount = rowsCount;
    return this;
  }

  public TableViewBuilder setTop( Control top ) {
    this.top = top;
    return this;
  }

  public TableViewBuilder setModifyListener( ModifyListener modifyListener ) {
    this.modifyListener = modifyListener;
    return this;
  }
}
