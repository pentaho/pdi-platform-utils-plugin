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
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.vfs.ui.CustomVfsUiPanel;
import org.pentaho.vfs.ui.VfsFileChooserDialog;
import pt.webdetails.di.baserver.utils.repositoryPlugin.Constants;
import pt.webdetails.di.baserver.utils.repositoryPlugin.RepositoryPlugin;

public class PentahoSolutionVfsFileChooserPanel extends CustomVfsUiPanel {

  private static String vfsSchemeDisplayText = "Pentaho";

  // for message resolution
  private static Class<?> PKG = RepositoryPlugin.class;

  public PentahoSolutionVfsFileChooserPanel( VfsFileChooserDialog vfsFileChooserDialog ) {
    super( Constants.getInstance().getVfsScheme() , vfsSchemeDisplayText, vfsFileChooserDialog, SWT.NONE );

    this.createPanel();
  }

  // region Properties

  private Button connectionButton;
  public Button getConnectionButton() {
    return this.connectionButton;
  }

  private TextVar serverUrl;
  public TextVar getServerUrl() {
    return this.serverUrl;
  }

  private TextVar webAppName;
  public TextVar getWebAppName() {
    return this.webAppName;
  }

  private TextVar userName;
  public TextVar getUserName() {
    return this.userName;
  }

  private TextVar password;
  public TextVar getPassword() {
    return this.password;
  }

  private Constants getConstants() {
    return Constants.getInstance();
  }

  public String getPentahoConnectionString() {
    return null;
    /*
    String connectionString = this.getConstants().getVfsScheme() + ":" +
      this.getServerUrl()
    ;
    */
  }

  /*
  try {
    FileObject root = rootFile;
    root = resolveFile( buildS3FileSystemUrlString() );
    vfsFileChooserDialog.setSelectedFile( root );
    vfsFileChooserDialog.setRootFile( root );
    rootFile = root;
  } catch ( FileSystemException e1 ) {
    MessageBox box = new MessageBox( getShell() );
    box.setText( BaseMessages.getString( PKG, "S3VfsFileChooserDialog.error" ) ); //$NON-NLS-1$
    box.setMessage( e1.getMessage() );
    log.logError( e1.getMessage(), e1 );
    box.open();
    return;
  }
  */



  private Spoon getSpoon() {
    return Spoon.getInstance();
  }
  // endregion

  // region Methods

  private VariableSpace getVariableSpace() {
    Spoon spoon = this.getSpoon();

    VariableSpace variableSpace = spoon.getActiveTransformation();
    if ( variableSpace != null ) {
      return variableSpace;
    }

    variableSpace = spoon.getActiveJob();
    if ( variableSpace != null ) {
      return variableSpace;
    }

    // TODO: check for environment variables
    return new Variables();
  }

  // region create View
  private void createPanel() {
    Composite group = this.buildGroup( this, 2 );

    this.serverUrl = this.buildTextInput( group, "PentahoSolutionVfsFileChooserPanel.ServerUrl.Label" );
    this.webAppName = this.buildTextInput( group, "PentahoSolutionVfsFileChooserPanel.WebAppName.Label" );

    this.userName = this.buildTextInput( group, "PentahoSolutionVfsFileChooserPanel.UserName.Label" );
    this.password = this.buildTextInput( group, "PentahoSolutionVfsFileChooserPanel.Password.Label", true );
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

  private TextVar buildTextInput( Composite parent, String i18nLabelKey, boolean isPassword ) {
    Label label = new Label( parent, SWT.RIGHT );
    String labelText = BaseMessages.getString( PKG, i18nLabelKey );
    label.setText( labelText );

    GridData labelGridData = new GridData();
    labelGridData.widthHint = 80;
    label.setLayoutData( labelGridData );

    int flags = SWT.SINGLE | SWT.LEFT | SWT.BORDER;
    if ( isPassword ) {
      flags = flags | SWT.PASSWORD;
    }
    TextVar textVarInput = new TextVar( this.getVariableSpace(), parent, flags );
    GridData inputGridData = new GridData();
    inputGridData.widthHint = 250;
    textVarInput.setLayoutData( inputGridData );

    return textVarInput;
  }

  private TextVar buildTextInput( Composite parent, String i18nLabelKey ) {
    return this.buildTextInput( parent, i18nLabelKey, false );
  }

  private Button buildButton( Composite parent, String i18nTextKey ) {
    Button button = new Button( parent, SWT.CENTER );

    GridData buttonGridData = new GridData();
    buttonGridData.widthHint = 100;
    button.setLayoutData( buttonGridData );

    String buttonText = BaseMessages.getString( PKG, i18nTextKey );
    button.setText( buttonText );

    return button;
  }


  // endregion
  // endregion


}
