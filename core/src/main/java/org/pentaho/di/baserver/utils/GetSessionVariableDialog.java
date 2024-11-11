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


package org.pentaho.di.baserver.utils;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.baserver.utils.widgets.TableViewBuilder;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.ConstUI;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.util.SwtSvgImageUtil;

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
        BaseMessages.getString(PKG, "GetSessionVariableDialog.Column.FieldName" ),
        ColumnInfo.COLUMN_TYPE_TEXT, false );
    ColumnInfo variableColumn = new ColumnInfo(
        BaseMessages.getString(PKG, "GetSessionVariableDialog.Column.VariableName" ),
        ColumnInfo.COLUMN_TYPE_TEXT, false );
    variableColumn.setUsingVariables( true );
    ColumnInfo defaultValueColumn = new ColumnInfo(
        BaseMessages.getString(PKG, "GetSessionVariableDialog.Column.DefaultValue" ),
        ColumnInfo.COLUMN_TYPE_TEXT, false );
    defaultValueColumn.setUsingVariables( true );
    defaultValueColumn.setToolTip( BaseMessages.getString( PKG, "GetSessionVariableDialog.Column.DefaultValue.Tooltip" ) );
    wFields = new TableViewBuilder( props, parent, variables )
        .addColumnInfo(fieldColumn )
        .addColumnInfo(variableColumn )
        .addColumnInfo(defaultValueColumn )
        .setTopPlacement( 0 )
        .setBottomPlacement( 100 )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
    wFields.addModifyListener( changeListener );
  }

  @Override
  protected Image getImage() {
    return SwtSvgImageUtil
        .getImage( shell.getDisplay(), getClass().getClassLoader(), "icons/getsessionvariable.svg", ConstUI.ICON_SIZE,
            ConstUI.ICON_SIZE );
  }

  protected void loadData( GetSessionVariableMeta meta ) {
    super.loadData( meta );

    int metaFieldsLength = meta.getFieldName().length;

    wFields.table.removeAll();
    wFields.table.setItemCount( metaFieldsLength == 0 ? 1 : metaFieldsLength );
    for ( int i = 0; i < metaFieldsLength; i++ ) {
      TableItem item = wFields.table.getItem( i );
      int index = 0;
      item.setText( ++index, Const.NVL( meta.getFieldName()[ i ], "" ) );
      item.setText( ++index, Const.NVL( meta.getVariableName()[ i ], "" ) );
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
