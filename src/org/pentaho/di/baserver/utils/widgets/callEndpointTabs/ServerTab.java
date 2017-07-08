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
 * Copyright 2006 - 2017 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.di.baserver.utils.widgets.callEndpointTabs;

import org.apache.http.HttpStatus;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.baserver.utils.BAServerCommonDialog;
import org.pentaho.di.baserver.utils.CallEndpointMeta;
import org.pentaho.di.baserver.utils.inspector.Inspector;
import org.pentaho.di.baserver.utils.widgets.ButtonBuilder;
import org.pentaho.di.baserver.utils.widgets.CheckBoxBuilder;
import org.pentaho.di.baserver.utils.widgets.fields.Field;
import org.pentaho.di.baserver.utils.widgets.fields.TextVarFieldBuilder;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.TextVar;

public class ServerTab extends Tab {

  public static final int FIELD_WIDTH = 350;
  private final TextVar urlText;
  private final TextVar userNameText;
  private final TextVar passwordText;
  private final Button useSessionCB;
  private final TransMeta transMeta;

  public ServerTab( CTabFolder tabFolder, PropsUI props, TransMeta transMeta, ModifyListener modifyListener,
                    SelectionListener selectionListener ) {
    super( tabFolder, BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Server.Title" ), props );
    this.transMeta = transMeta;

    final Field<TextVar> urlField = new TextVarFieldBuilder( this, props )
        .setVariableSpace( transMeta )
        .addModifyListener( modifyListener )
        .setLabel( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Server.URL" ) )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setWidth( FIELD_WIDTH )
        .build();
    urlText = urlField.getControl();

    final Field<TextVar> userNameField = new TextVarFieldBuilder( this, props )
        .setVariableSpace( transMeta )
        .addModifyListener( modifyListener )
        .setLabel( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Server.UserName" ) )
        .setTop( urlField )
        .setTopMargin( BAServerCommonDialog.MEDIUM_MARGIN )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setWidth( FIELD_WIDTH )
        .build();
    userNameText = userNameField.getControl();
    final Field<TextVar> passwordField = new TextVarFieldBuilder( this, props )
        .setVariableSpace( transMeta )
        .addModifyListener( modifyListener )
        .addModifyListener( new ModifyListener() {
          @Override public void modifyText( ModifyEvent modifyEvent ) {
            Text text = (Text) modifyEvent.getSource();
            if ( text.getText().startsWith( "${" ) ) {
              text.setEchoChar( '\0' );
            } else {
              text.setEchoChar( '*' );
            }
          }
        } )
        .setLabel( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Server.Password" ) )
        .setTop( userNameField )
        .setTopMargin( BAServerCommonDialog.MEDIUM_MARGIN )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setWidth( FIELD_WIDTH )
        .build();
    passwordText = passwordField.getControl();

    final Button testConnectionButton = new ButtonBuilder( this, props )
        .setLabelText( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Server.TestConnection" ) )
        .setTop( passwordField )
        .setTopMargin( BAServerCommonDialog.MEDIUM_MARGIN )
        .setLeftPlacement( LEFT_PLACEMENT )
        .build();
    testConnectionButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent selectionEvent ) {
        super.widgetSelected( selectionEvent );
        testConnection( true );
      }
    } );

    useSessionCB = new CheckBoxBuilder( this, props )
        .setText( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Server.UseSession" ) )
        .addSelectionListener( selectionListener )
        .setTop( testConnectionButton )
        .setTopMargin( BAServerCommonDialog.MEDIUM_MARGIN )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
  }

  public boolean testConnection( boolean showDialogOnSuccess ) {
    String serverUrl = getServerUrl();
    String userName = getUserName();
    String password = getPassword();
    int serverStatus = Inspector.getInstance().checkServerStatus( serverUrl, userName, password );
    MessageBox messageBox = new MessageBox( getShell() );
    switch ( serverStatus ) {
      case HttpStatus.SC_OK:
        if ( !showDialogOnSuccess ) {
          return true;
        }
        messageBox.setText( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Server.Test.Success.Header" ) );
        messageBox
            .setMessage( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Server.Test.Success.Message" ) );
        break;
      case HttpStatus.SC_UNAUTHORIZED:
        messageBox
            .setText( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Server.Test.UnableLogin.Header" ) );
        messageBox
            .setMessage( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Server.Test.UnableLogin.Message" ) );
        break;
      default:
        messageBox
            .setText( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Server.Test.UnableConnect.Header" ) );
        messageBox.setMessage(
            BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Server.Test.UnableConnect.Message" ) );
        break;
    }
    messageBox.open();
    return serverStatus == HttpStatus.SC_OK;
  }

  @Override public void loadData( CallEndpointMeta meta ) {
    urlText.setText( meta.getServerURL() );
    userNameText.setText( meta.getUserName() );
    passwordText.setText( meta.getPassword() );

    final boolean bypassingAuthentication = meta.isBypassingAuthentication();
    useSessionCB.setSelection( bypassingAuthentication );
  }

  @Override public void saveData( CallEndpointMeta meta ) {
    meta.setServerURL( urlText.getText() );
    meta.setUserName( userNameText.getText() );
    meta.setPassword( passwordText.getText() );
    meta.setBypassingAuthentication( useSessionCB.getSelection() );
  }

  public String getServerUrl() {
    return transMeta.environmentSubstitute( urlText.getText() );
  }

  public String getUserName() {
    return transMeta.environmentSubstitute( userNameText.getText() );
  }

  public String getPassword() {
    return transMeta.environmentSubstitute( passwordText.getText() );
  }
}

