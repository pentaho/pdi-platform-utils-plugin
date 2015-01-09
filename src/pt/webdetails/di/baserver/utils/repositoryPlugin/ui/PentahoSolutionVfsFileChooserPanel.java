/*
 *  Copyright 2002 - 2014 Webdetails, a Pentaho company.  All rights reserved.
 *
 *  This software was developed by Webdetails and is provided under the terms
 *  of the Mozilla Public License, Version 2.0, or any later version. You may not use
 *  this file except in compliance with the license. If you need a copy of the license,
 *  please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 *  Software distributed under the Mozilla Public License is distributed on an "AS IS"
 *  basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 *  the license for the specific language governing your rights and limitations.
 */

package pt.webdetails.di.baserver.utils.repositoryPlugin.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.vfs.ui.CustomVfsUiPanel;
import org.pentaho.vfs.ui.VfsFileChooserDialog;
import pt.webdetails.di.baserver.utils.repositoryPlugin.Constants;
import pt.webdetails.di.baserver.utils.repositoryPlugin.RepositoryPlugin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class PentahoSolutionVfsFileChooserPanel extends CustomVfsUiPanel {

  // for message resolution
  private static Class<?> PKG = RepositoryPlugin.class;

  public PentahoSolutionVfsFileChooserPanel( VfsFileChooserDialog vfsFileChooserDialog ) {
    super( "" , "", vfsFileChooserDialog, SWT.NONE );

    this.setVfsScheme( this.getConstants().getVfsScheme() );
    String vfsSchemeDisplayText = BaseMessages.getString( PKG,
        "PentahoSolutionVfsFileChooserPanel.VfsDropdownOption.Text" );
    this.setVfsSchemeDisplayText( vfsSchemeDisplayText );

    this.createPanel();
  }

  // region Properties
  private Button connectionButton;
  public Button getConnectionButton() {
    return this.connectionButton;
  }

  private TextVar serverUrl;
  private URL getServerUrl() throws MalformedURLException {
    String resolvedUrl = this.resolveVariable( this.serverUrl.getText() );
    URL url = new URL( resolvedUrl );
    return url;
  }

  private TextVar userName;
  private String getUserName() {
    String resolvedUserName = this.resolveVariable( this.userName.getText() );
    return resolvedUserName;
  }

  private TextVar password;
  private String getPassword() {
    String resolvedPassword = this.resolveVariable( this.password.getText() );
    return resolvedPassword;
  }

  private Constants getConstants() {
    return Constants.getInstance();
  }

  /***
   *
   * @return the file URI constructed from the dialog input
   * @throws MalformedURLException when the specified Server URL is invalid
   */
  public String getPentahoConnectionString() throws MalformedURLException {
    StringBuffer urlString = new StringBuffer( this.getConstants().getVfsScheme() );
    urlString.append( ":" );

    URL serverUrl = this.getServerUrl();
    urlString.append( serverUrl.getProtocol() );
    urlString.append( "://" );

    String username = this.getUserName();
    if ( !nullOrEmpty( username ) ) {
      urlString.append( this.getUserName() );
      urlString.append( ":" );
      urlString.append( this.getPassword() );
      urlString.append( "@" );
    }

    urlString.append( serverUrl.getHost() );
    int port = serverUrl.getPort();
    if ( port != -1 ) { // if port is specified
      urlString.append( ":" );
      urlString.append( port );
    }

    urlString.append( serverUrl.getPath() );

    return urlString.toString();
  }


  private Spoon getSpoon() {
    return Spoon.getInstance();
  }
  // endregion

  // region Methods

  // region aux
  private static boolean nullOrEmpty( String string ) {
    return string == null || string.isEmpty();
  }
  // endregion

  private VariableSpace getVariableSpace() {
    VariableSpace environmentVariables = this.getEnvironmentVariables();
    VariableSpace kettlePropertiesVariables = this.getKettlePropertiesVariables();

    kettlePropertiesVariables.copyVariablesFrom( environmentVariables );
    return kettlePropertiesVariables;
  }

  private VariableSpace getEnvironmentVariables() {
    Spoon spoon = this.getSpoon();
    VariableSpace variables = new Variables();

    RowMetaAndData envVariables = spoon.variables;
    for ( int i = 0; i < envVariables.size(); i++ ) {
      try {
        String name = envVariables.getValueMeta( i ).getName();
        String value = envVariables.getString( i, "" );
        variables.setVariable( name, value );

      } catch ( KettleValueException e ) {
        // Just eat the exception. getString() should never give an
        // exception.
        e.printStackTrace();
      }
    }

    return variables;
  }

  private VariableSpace getKettlePropertiesVariables() {
    VariableSpace variables = new Variables();
    //vars.initializeVariablesFrom( null );
    try {
      Map<?, ?> kettleProperties = EnvUtil.readProperties( Const.KETTLE_PROPERTIES );
      for ( Object key : kettleProperties.keySet() ) {
        String variable = (String) key;
        String value = variables.environmentSubstitute( (String) kettleProperties.get( key ) );
        variables.setVariable( variable, value );
      }
    } catch ( KettleException e ) {
      e.printStackTrace();
    }

    return variables;
  }

  private String resolveVariable( String variable ) {
    return this.getVariableSpace().environmentSubstitute( variable );
  }

  public void activate( ) {
    VariableSpace variableSpace = this.getVariableSpace();
    this.serverUrl.setVariables( variableSpace );
    this.userName.setVariables( variableSpace );
    this.password.setVariables( variableSpace );
    //this.webAppName.setVariables( variableSpace );
  }

  // region create View
  private void createPanel() {
    Composite group = this.buildGroup( this, 2 );

    this.serverUrl = this.buildTextInput( group, "PentahoSolutionVfsFileChooserPanel.ServerUrl.Label" );
    this.serverUrl.setText( this.getConstants().getDefaultServerUrl() );

    this.userName = this.buildTextInput( group, "PentahoSolutionVfsFileChooserPanel.UserName.Label" );
    this.userName.setText( this.getConstants().getDefaultUser() );

    this.password = this.buildTextInput( group, "PentahoSolutionVfsFileChooserPanel.Password.Label", true );
    this.password.setText( this.getConstants().getDefaultPassword() );

    this.buildEmptyWidget( group );
    this.connectionButton = this.buildButton( group, "PentahoSolutionVfsFileChooserPanel.ConnectButton.Text" );
  }

  private Composite buildGroup( Composite parent, int numberOfColumns ) {
    // The Connection group
    Group connectionGroup = new Group( parent, SWT.SHADOW_ETCHED_IN );
    String connectionGroupLabel = BaseMessages.getString( PKG,
        "PentahoSolutionVfsFileChooserPanel.ConnectionGroup.Label" );
    connectionGroup.setText( connectionGroupLabel );

    GridLayout connectionGroupLayout = new GridLayout();
    connectionGroupLayout.marginWidth = 5;
    connectionGroupLayout.marginHeight = 5;
    connectionGroupLayout.verticalSpacing = 5;
    connectionGroupLayout.horizontalSpacing = 5;

    GridData gData = new GridData( SWT.FILL, SWT.FILL, true, false );
    connectionGroup.setLayoutData( gData );
    connectionGroup.setLayout( connectionGroupLayout );

    // The composite we need in the group
    Composite fieldPanel = new Composite( connectionGroup, SWT.NONE );
    GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, false );
    fieldPanel.setLayoutData( gridData );
    fieldPanel.setLayout( new GridLayout( numberOfColumns, false ) );

    return fieldPanel;
  }

  private Widget buildEmptyWidget( Composite parent ) {
    Label label = new Label( parent, SWT.NONE );
    GridData labelGridData = new GridData();
    label.setLayoutData( labelGridData );
    return label;
  }

  private TextVar buildTextInput( Composite parent, String i18nLabelKey, boolean isPassword ) {
    Label label = new Label( parent, SWT.RIGHT );
    String labelText = BaseMessages.getString( PKG, i18nLabelKey );
    label.setText( labelText );

    GridData labelGridData = new GridData();
    labelGridData.widthHint = 100;
    label.setLayoutData( labelGridData );

    int flags = SWT.SINGLE | SWT.LEFT | SWT.BORDER;
    if ( isPassword ) {
      flags = flags | SWT.PASSWORD;
    }
    TextVar textVarInput = new TextVar( this.getVariableSpace(), parent, flags );
    GridData inputGridData = new GridData();
    inputGridData.widthHint = 250;
    inputGridData.grabExcessHorizontalSpace = true;
    inputGridData.horizontalAlignment = SWT.FILL;
    textVarInput.setLayoutData( inputGridData );

    return textVarInput;
  }

  private TextVar buildTextInput( Composite parent, String i18nLabelKey ) {
    return this.buildTextInput( parent, i18nLabelKey, false );
  }

  private Button buildButton( Composite parent, String i18nTextKey ) {
    Button button = new Button( parent, SWT.PUSH | SWT.CENTER );

    GridData buttonGridData = new GridData( SWT.RIGHT, SWT.CENTER, true, true, 1, 1 );
    buttonGridData.widthHint = 100;
    button.setLayoutData( buttonGridData );

    String buttonText = BaseMessages.getString( PKG, i18nTextKey );
    button.setText( buttonText );

    return button;
  }


  // endregion
  // endregion


}
