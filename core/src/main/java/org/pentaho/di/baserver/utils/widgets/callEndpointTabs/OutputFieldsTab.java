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


package org.pentaho.di.baserver.utils.widgets.callEndpointTabs;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.ModifyListener;
import org.pentaho.di.baserver.utils.BAServerCommonDialog;
import org.pentaho.di.baserver.utils.CallEndpointMeta;
import org.pentaho.di.baserver.utils.widgets.fields.Field;
import org.pentaho.di.baserver.utils.widgets.fields.TextVarFieldBuilder;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.TextVar;

public class OutputFieldsTab extends Tab {

  public static final int FIELD_WIDTH = 350;
  private final TextVar resultNameText;
  private final TextVar statusCodeNameText;
  private final TextVar responseTimeNameText;

  public OutputFieldsTab( CTabFolder tabFolder, PropsUI props, VariableSpace transMeta,
                          ModifyListener modifyListener ) {
    super( tabFolder, BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.OutputFields.Title" ), props );

    final Field<TextVar> resultNameField = new TextVarFieldBuilder( this, props )
        .setVariableSpace( transMeta )
        .addModifyListener( modifyListener )
        .setLabel( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.OutputFields.ResultName" ) )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setWidth( FIELD_WIDTH )
        .build();
    resultNameText = resultNameField.getControl();
    final Field<TextVar> statusCodeNameField = new TextVarFieldBuilder( this, props )
        .setVariableSpace( transMeta )
        .addModifyListener( modifyListener )
        .setLabel( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.OutputFields.StatusCodeName" ) )
        .setTop( resultNameField )
        .setTopMargin( BAServerCommonDialog.MEDIUM_MARGIN )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setWidth( FIELD_WIDTH )
        .build();
    statusCodeNameText = statusCodeNameField.getControl();
    final Field<TextVar> responseTimeNameField = new TextVarFieldBuilder( this, props )
        .setVariableSpace( transMeta )
        .addModifyListener( modifyListener )
        .setLabel( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.OutputFields.ResponseTimeName" ) )
        .setTop( statusCodeNameField )
        .setTopMargin( BAServerCommonDialog.MEDIUM_MARGIN )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setWidth( FIELD_WIDTH )
        .build();
    responseTimeNameText = responseTimeNameField.getControl();
  }

  @Override public void loadData( CallEndpointMeta meta ) {
    resultNameText.setText( meta.getResultField() );
    statusCodeNameText.setText( meta.getStatusCodeField() );
    responseTimeNameText.setText( meta.getResponseTimeField() );
  }

  @Override public void saveData( CallEndpointMeta meta ) {
    meta.setResultField( resultNameText.getText() );
    meta.setStatusCodeField( statusCodeNameText.getText() );
    meta.setResponseTimeField( responseTimeNameText.getText() );
  }

  @Override public boolean isValid() {
    return !Const.isEmpty( resultNameText.getText() ) || !Const.isEmpty( statusCodeNameText.getText() )
        || !Const.isEmpty( responseTimeNameText.getText() );
  }
}
