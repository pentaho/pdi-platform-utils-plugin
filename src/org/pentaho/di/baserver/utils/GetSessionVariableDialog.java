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

package org.pentaho.di.baserver.utils;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.baserver.utils.widgets.TableViewBuilder;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;

public class GetSessionVariableDialog extends BAServerCommonDialog<GetSessionVariableMeta> {
  private TableView wFields;

  public GetSessionVariableDialog( Shell parent, Object in, TransMeta transMeta, String name ) {
    super( parent, (GetSessionVariableMeta) in, transMeta, name );
  }

  @Override
  protected String getTitleKey() {
    return "GetSessionVariableDialog.DialogTitle";
  }

  @Override
  protected void buildContent( Composite parent ) {
    ColumnInfo fieldColumn = new ColumnInfo(
        BaseMessages.getString(PKG, "GetSessionVariableDialog.Column.FieldName"),
        ColumnInfo.COLUMN_TYPE_TEXT, false);
    ColumnInfo variableColumn = new ColumnInfo(
        BaseMessages.getString(PKG, "GetSessionVariableDialog.Column.VariableName"),
        ColumnInfo.COLUMN_TYPE_TEXT, false);
    variableColumn.setUsingVariables( true );
    ColumnInfo defaultValueColumn = new ColumnInfo(
        BaseMessages.getString(PKG, "GetSessionVariableDialog.Column.DefaultValue"),
        ColumnInfo.COLUMN_TYPE_TEXT, false);
    defaultValueColumn.setUsingVariables( true );
    defaultValueColumn.setToolTip( BaseMessages.getString( PKG, "GetSessionVariableDialog.Column.DefaultValue.Tooltip" ) );
    wFields = new TableViewBuilder( props, parent, variables )
        .addColumnInfo(fieldColumn)
        .addColumnInfo(variableColumn)
        .addColumnInfo(defaultValueColumn)
        .setTopPlacement( 0 )
        .setBottomPlacement( 100 )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
  }

  protected void loadData( GetSessionVariableMeta meta ) {
    super.loadData( meta );

    for ( int i = 0; i < meta.getFieldName().length; i++ ) {
      TableItem item = wFields.table.getItem( i );
      int index = 0;
      item.setText( ++index, Const.NVL( meta.getFieldName()[ i ], "" ) );
      item.setText( ++index, Const.NVL( meta.getVariableName()[ i ], "" ) );
      item.setText( ++index, ValueMeta.getTypeDesc( meta.getFieldType()[ i ] ) );
      item.setText( ++index, Const.NVL( meta.getFieldFormat()[ i ], "" ) );
      item.setText( ++index, meta.getFieldLength()[ i ] < 0 ? "" : ( "" + meta.getFieldLength()[ i ] ) );
      item.setText( ++index, meta.getFieldPrecision()[ i ] < 0 ? "" : ( "" + meta.getFieldPrecision()[ i ] ) );
      item.setText( ++index, Const.NVL( meta.getCurrency()[ i ], "" ) );
      item.setText( ++index, Const.NVL( meta.getDecimal()[ i ], "" ) );
      item.setText( ++index, Const.NVL( meta.getGroup()[ i ], "" ) );
      item.setText( ++index, ValueMeta.getTrimTypeDesc( meta.getTrimType()[ i ] ) );
      item.setText( ++index, Const.NVL( meta.getDefaultValue()[ i ], "" ) );
    }
    wFields.setRowNums();
    wFields.optWidth( true );
  }

  protected void saveData( GetSessionVariableMeta meta ) {
    super.saveData( meta );

    int count = wFields.nrNonEmpty();
    meta.allocate( count );
    for ( int i = 0; i < count; i++ ) {
      TableItem item = wFields.getNonEmpty( i );
      int index = 0;
      meta.getFieldName()[ i ] = item.getText( ++index );
      meta.getVariableName()[ i ] = item.getText( ++index );
      meta.getFieldType()[ i ] = ValueMeta.getType( item.getText( ++index ) );
      meta.getFieldFormat()[ i ] = item.getText( ++index );
      meta.getFieldLength()[ i ] = Const.toInt( item.getText( ++index ), -1 );
      meta.getFieldPrecision()[ i ] = Const.toInt( item.getText( ++index ), -1 );
      meta.getCurrency()[ i ] = item.getText( ++index );
      meta.getDecimal()[ i ] = item.getText( ++index );
      meta.getGroup()[ i ] = item.getText( ++index );
      meta.getTrimType()[ i ] = ValueMeta.getTrimTypeByDesc( item.getText( ++index ) );
      meta.getDefaultValue()[ i ] = item.getText( ++index );
    }
  }

  @Override protected int getMinimumHeight() {
    return 391;
  }

  @Override protected int getMinimumWidth() {
    return 505;
  }
}
