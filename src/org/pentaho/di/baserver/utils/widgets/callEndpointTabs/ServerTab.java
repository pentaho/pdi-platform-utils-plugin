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

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.baserver.utils.CallEndpointMeta;
import org.pentaho.di.baserver.utils.widgets.CheckBoxBuilder;
import org.pentaho.di.baserver.utils.widgets.GroupBuilder;
import org.pentaho.di.baserver.utils.widgets.fields.Field;
import org.pentaho.di.baserver.utils.widgets.fields.TextVarFieldBuilder;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.TextVar;

public class ServerTab extends Tab {

  private final TextVar urlText;
  private final TextVar userNameText;
  private final TextVar passwordText;
  private final Button useSessionCB;
  private final TransMeta transMeta;

  public ServerTab( CTabFolder tabFolder, PropsUI props, TransMeta transMeta, ModifyListener modifyListener, SelectionListener selectionListener ) {
    super( tabFolder, BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Server.Title" ), props );
    this.transMeta = transMeta;

    final Field<TextVar> urlField = new TextVarFieldBuilder( this, props )
        .setVariableSpace( transMeta )
        .addModifyListener( modifyListener )
        .setLabel( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Server.URL" ) )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
    urlText = urlField.getControl();

    Group authenticationGroup = new GroupBuilder( this, props )
        .setText( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Server.Authentication" ) )
        .setTop( urlField )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
    useSessionCB = new CheckBoxBuilder( authenticationGroup, props )
        .setText( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Server.UseSession" ) )
        .addSelectionListener( selectionListener )
        .addSelectionListener( new SelectionAdapter() {
          @Override public void widgetSelected( SelectionEvent selectionEvent ) {
            super.widgetSelected( selectionEvent );
            final boolean useSessionCBSelection = useSessionCB.getSelection();
            userNameText.setEnabled( !useSessionCBSelection );
            passwordText.setEnabled( !useSessionCBSelection );
          }
        } )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
    final Field<TextVar> userNameField = new TextVarFieldBuilder( authenticationGroup, props )
        .setVariableSpace( transMeta )
        .addModifyListener( modifyListener )
        .setLabel( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Server.UserName" ) )
        .setTop( useSessionCB )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
    userNameText = userNameField.getControl();
    final Field<TextVar> passwordField = new TextVarFieldBuilder( authenticationGroup, props )
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
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
    passwordText = passwordField.getControl();
  }

  @Override public void loadData( CallEndpointMeta meta ) {
    urlText.setText( meta.getServerURL() );
    userNameText.setText( meta.getUserName() );
    passwordText.setText( meta.getPassword() );

    final boolean bypassingAuthentication = meta.isBypassingAuthentication();
    useSessionCB.setSelection( bypassingAuthentication );
    userNameText.setEnabled( !bypassingAuthentication );
    passwordText.setEnabled( !bypassingAuthentication );
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

