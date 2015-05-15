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

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.baserver.utils.widgets.CheckBoxBuilder;
import org.pentaho.di.baserver.utils.widgets.TableViewBuilder;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.ConstUI;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.util.SwtSvgImageUtil;

public class SetSessionVariableDialog extends BAServerCommonDialog<SetSessionVariableMeta> {
  private Button wApplyFormatting;
  private TableView wFields;

  public SetSessionVariableDialog( Shell parent, Object in, TransMeta transMeta, String name ) {
    super( parent, (SetSessionVariableMeta) in, transMeta, name );
  }

  @Override
  protected String getTitleKey() {
    return "SetSessionVariableDialog.DialogTitle";
  }

  @Override
  protected void buildContent( Composite parent ) {
    wApplyFormatting = new CheckBoxBuilder( parent, props )
        .setText( BaseMessages.getString( PKG, "SetSessionVariableDialog.Label.ApplyFormatting" ) )
        .setToolTipText( BaseMessages.getString( PKG, "SetSessionVariableDialog.Label.ApplyFormatting.Tooltip" ) )
        .setTopPlacement( 0 )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
    wApplyFormatting.addSelectionListener( new SelectionListener() {
      @Override
      public void widgetSelected( SelectionEvent selectionEvent ) {
        Event e = new Event();
        e.widget = wApplyFormatting;
        changeListener.modifyText( new ModifyEvent( e ) );
      }

      @Override
      public void widgetDefaultSelected( SelectionEvent selectionEvent ) {
      }
    } );

    ColumnInfo cFieldNames = new ColumnInfo(
        BaseMessages.getString( PKG, "SetSessionVariableDialog.Column.FieldName" ),
        ColumnInfo.COLUMN_TYPE_TEXT, false );
    cFieldNames.setComboValues( getFieldNames() );
    ColumnInfo variableColumn = new ColumnInfo(
        BaseMessages.getString( PKG, "SetSessionVariableDialog.Column.VariableName" ),
        ColumnInfo.COLUMN_TYPE_TEXT, false );
    variableColumn.setUsingVariables( true );
    variableColumn.setToolTip( BaseMessages.getString( PKG, "SetSessionVariableDialog.Column.VariableName.Tooltip" ) );
    ColumnInfo defaultValueColumn = new ColumnInfo(
        BaseMessages.getString( PKG, "SetSessionVariableDialog.Column.DefaultValue" ),
        ColumnInfo.COLUMN_TYPE_TEXT, false );
    defaultValueColumn.setUsingVariables( true );
    defaultValueColumn.setToolTip(
        BaseMessages.getString( PKG, "SetSessionVariableDialog.Column.DefaultValue.Tooltip" ) );
    wFields = new TableViewBuilder( props, parent, variables )
        .addColumnInfo( cFieldNames )
        .addColumnInfo( variableColumn )
        .addColumnInfo( defaultValueColumn )
        .setTop( wApplyFormatting )
        .setTopMargin( MEDIUM_MARGIN )
        .setBottomPlacement( 100 )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
    wFields.addModifyListener( changeListener );
  }

  @Override
  protected Image getImage() {
    return SwtSvgImageUtil
        .getImage( shell.getDisplay(), getClass().getClassLoader(), "icons/setsessionvariable.svg", ConstUI.ICON_SIZE,
            ConstUI.ICON_SIZE );
  }

  protected void loadData( SetSessionVariableMeta meta ) {
    super.loadData( meta );

    wApplyFormatting.setSelection( meta.isUsingFormatting() );

    // load fields
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

  protected void saveData( SetSessionVariableMeta meta ) {
    super.saveData( meta );

    meta.setUseFormatting( wApplyFormatting.getSelection() );

    // save fields
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
    return 414;
  }

  @Override protected int getMinimumWidth() {
    return 505;
  }
}
