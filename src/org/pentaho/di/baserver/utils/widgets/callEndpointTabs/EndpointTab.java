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

package org.pentaho.di.baserver.utils.widgets.callEndpointTabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.baserver.utils.CallEndpointMeta;
import org.pentaho.di.baserver.utils.inspector.Endpoint;
import org.pentaho.di.baserver.utils.inspector.Inspector;
import org.pentaho.di.baserver.utils.widgets.ButtonBuilder;
import org.pentaho.di.baserver.utils.widgets.GroupBuilder;
import org.pentaho.di.baserver.utils.widgets.LabelBuilder;
import org.pentaho.di.baserver.utils.widgets.RadioBuilder;
import org.pentaho.di.baserver.utils.widgets.fields.ComboVarFieldBuilder;
import org.pentaho.di.baserver.utils.widgets.fields.Field;
import org.pentaho.di.baserver.utils.widgets.fields.TextAreaFieldBuilder;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.ComboVar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EndpointTab extends Tab {
  private final TransMeta transMeta;
  private final String stepName;
  private final LogChannel log;
  private final ServerTab serverTab;
  private ComboVar serverModule, resourcePath, httpMethod;
  private final Button fromServerRadio;

  public EndpointTab( CTabFolder tabFolder, PropsUI props, TransMeta transMeta, ModifyListener modifyListener,
      SelectionListener selectionListener, String stepName, LogChannel log, ServerTab serverTab ) {
    super( tabFolder, BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.Title" ), props );
    this.transMeta = transMeta;
    this.stepName = stepName;
    this.log = log;
    this.serverTab = serverTab;

    Group endpointLocationGroup = new GroupBuilder( this, props )
        .setText( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.EndpointLocation" ) )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
    fromServerRadio = new RadioBuilder( endpointLocationGroup, props )
        .setText( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.FromServer" ) )
        .addSelectionListener( selectionListener )
        .setLeftPlacement( LEFT_PLACEMENT )
        .build();
    final Button getEndpointButton = new ButtonBuilder( endpointLocationGroup, props )
        .setLabelText( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.GetEndpoint" ) )
        .setLeft( fromServerRadio )
        .build();
    getEndpointButton.addSelectionListener( new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        super.widgetSelected( selectionEvent );
        populateEndpoints();
      }
    } );
    final Button fieldsUpstreamRadio = new RadioBuilder( endpointLocationGroup, props )
        .setText( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.FieldsUpstream" ) )
        .addSelectionListener( selectionListener )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setTop( fromServerRadio )
        .build();
    final SelectionAdapter switchEndpointLocation = new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        super.widgetSelected( selectionEvent );
        serverModule.removeAll();
        resourcePath.removeAll();
        httpMethod.removeAll();
      }
    };
    fromServerRadio.addSelectionListener( switchEndpointLocation );
    fieldsUpstreamRadio.addSelectionListener( switchEndpointLocation );
    fromServerRadio.setSelection( true );

    Group wsEndpointGroup = new GroupBuilder( this, props )
        .setText( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.WebServiceEndpoint" ) )
        .setTop( endpointLocationGroup )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( 50 )
        .build();

    final Field<ComboVar> serverModuleField = new ComboVarFieldBuilder( wsEndpointGroup, props )
        .setVariableSpace( transMeta )
        .addSelectionListener( new SelectionAdapter() {
          @Override public void widgetSelected( SelectionEvent selectionEvent ) {
            super.widgetSelected( selectionEvent );
            updateEndpointPathsComboBox();
            setDefaultEndpointPath();
            updateHttpMethodsComboBox();
            setDefaultHttpMethod();
          }
        } )
        .addModifyListener( modifyListener )
        .setLabel( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.BAServerModule" ) )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
    serverModule = serverModuleField.getControl();
    final Field<ComboVar> resourcePathField = new ComboVarFieldBuilder( wsEndpointGroup, props )
        .setVariableSpace( transMeta )
        .addSelectionListener( new SelectionAdapter() {
          @Override public void widgetSelected( SelectionEvent selectionEvent ) {
            super.widgetSelected( selectionEvent );
            updateHttpMethodsComboBox();
            setDefaultHttpMethod();
          }
        } )
        .addModifyListener( modifyListener )
        .setLabel( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.ResourcePath" ) )
        .setTop( serverModuleField )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
    resourcePath = resourcePathField.getControl();
    final Field<ComboVar> httpMethodField = new ComboVarFieldBuilder( wsEndpointGroup, props )
        .setVariableSpace( transMeta )
        .addModifyListener( modifyListener )
        .setLabel( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.HTTPMethod" ) )
        .setTop( resourcePathField )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
    httpMethod = httpMethodField.getControl();
    final Field resourcePathDetailsField = new TextAreaFieldBuilder( this, props )
        .setLabel( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.ResourcePathDetails" ) )
        .addModifyListener( modifyListener )
        .setTop( endpointLocationGroup )
        .setLeft( wsEndpointGroup )
        .setRightPlacement( RIGHT_PLACEMENT )
        .setBottomPlacement( 100 )
        .build();
  }

  @Override public void loadData( CallEndpointMeta meta ) {
    fromServerRadio.setSelection( !meta.isEndpointFromField() );
    serverModule.setText( meta.getModuleName() );
    resourcePath.setText( meta.getEndpointPath() );
    httpMethod.setText( meta.getHttpMethod() );

  }

  public void populateEndpoints() {
    if ( fromServerRadio.getSelection() ) {
        String serverUrl = serverTab.getServerUrl();
        String userName = serverTab.getUserName();
        String password = serverTab.getPassword();
        if ( serverTab.testConnection( false ) && Inspector.getInstance().inspectServer( serverUrl, userName, password ) ) {
            updateModuleNamesComboBox();
            updateEndpointPathsComboBox();
            updateHttpMethodsComboBox();
        }
    } else {
      updateModuleNamesComboBox();
      updateEndpointPathsComboBox();
      updateHttpMethodsComboBox();
    }
    // TODO: update resourcePathDetailsField when WADL requests for endpoint description will be ready
  }

  @Override public void saveData( CallEndpointMeta meta ) {
    meta.setEndpointFromField( !fromServerRadio.getSelection() );
    meta.setModuleName( serverModule.getText() );
    meta.setEndpointPath( resourcePath.getText() );
    meta.setHttpMethod( httpMethod.getText() );
  }

  @Override public boolean isValid() {
    return !Const.isEmpty( serverModule.getText() ) && !Const.isEmpty( resourcePath.getText() )
        && !Const.isEmpty( httpMethod.getText() );
  }

  private void updateModuleNamesComboBox() {
    String moduleName = this.transMeta.environmentSubstitute( serverModule.getText() );
    serverModule.removeAll();
    if ( fromServerRadio.getSelection() ) {
      serverModule.removeAll();
      for ( String item : Inspector.getInstance().getModuleNames() ) {
        serverModule.add( item );
      }
      if ( moduleName.equals( "" ) ) {
        moduleName = Inspector.getInstance().getDefaultModuleName();
      }
      serverModule.setText( moduleName );
    } else {
      serverModule.setItems( getFieldNames() );
    }
  }

  private void updateEndpointPathsComboBox() {
    String endpointPath = transMeta.environmentSubstitute( resourcePath.getText() );
    resourcePath.removeAll();
    if ( fromServerRadio.getSelection() ) {
      String moduleName = transMeta.environmentSubstitute( serverModule.getText() );
      for ( String path : Inspector.getInstance().getEndpointPaths( moduleName ) ) {
        resourcePath.add( path );
      }
      if ( endpointPath.equals( "" )  ) {
        endpointPath = Inspector.getInstance().getDefaultEndpointPath( moduleName );
      }
      resourcePath.setText( endpointPath );
    } else {
      resourcePath.setItems( getFieldNames() );
    }
  }

  private void setDefaultEndpointPath() {
    if ( fromServerRadio.getSelection() ) {
      String moduleName = transMeta.environmentSubstitute( serverModule.getText() );
      resourcePath.setText( Inspector.getInstance().getDefaultEndpointPath( moduleName ) );
    }
  }

  private void updateHttpMethodsComboBox() {
    String httpMethod = transMeta.environmentSubstitute( this.httpMethod.getText() );
    this.httpMethod.removeAll();
    if ( fromServerRadio.getSelection() ) {
      String moduleName = transMeta.environmentSubstitute( serverModule.getText() );
      String endpointPath = transMeta.environmentSubstitute( resourcePath.getText() );
      Iterable<Endpoint> endpoints = Inspector.getInstance().getEndpoints( moduleName, endpointPath );
      for ( Endpoint endpoint : endpoints ) {
        this.httpMethod.add( endpoint.getHttpMethod().name() );
      }
      if ( httpMethod.equals( "" ) ) {
        Endpoint endpoint = Inspector.getInstance().getDefaultEndpoint( moduleName, endpointPath );
        if ( endpoint != null ) {
          httpMethod = endpoint.getHttpMethod().name();
        }
      }
      this.httpMethod.setText( httpMethod );
    } else {
      this.httpMethod.setItems( getFieldNames() );
    }
  }

  private void setDefaultHttpMethod() {
    if ( fromServerRadio.getSelection() ) {
      String moduleName = transMeta.environmentSubstitute( serverModule.getText() );
      String path = transMeta.environmentSubstitute( resourcePath.getText() );
      Endpoint endpoint = Inspector.getInstance().getDefaultEndpoint( moduleName, path );
      if ( endpoint != null ) {
        this.httpMethod.setText( endpoint.getHttpMethod().name() );
      } else {
        this.httpMethod.setText( "" );
      }
    }
  }

  private String[] getFieldNames() {
    StepMeta stepMeta = transMeta.findStep( stepName );
    List<String> entries = null;
    if ( stepMeta != null ) {
      try {
        // get field names from previous steps
        RowMetaInterface row = transMeta.getPrevStepFields( stepMeta );
        entries = new ArrayList<String>();
        for ( int i = 0; i < row.size(); i++ ) {
          entries.add( row.getValueMeta( i ).getName() );
        }
        Collections.sort( entries );

        //fieldNames = entries.toArray( new String[ entries.size() ] );
        //Const.sortStrings( fieldNames );
      } catch ( KettleException e ) {
        log.logError( BaseMessages.getString( PKG, "System.Dialog.GetFieldsFailed.Message" ) );
      }
    }
    return entries == null ? new String[0] : entries.toArray( new String[entries.size()] );
  }
}
