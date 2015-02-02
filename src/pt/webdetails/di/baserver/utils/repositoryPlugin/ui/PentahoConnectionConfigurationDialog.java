/*
 *  Copyright 2002 - 2015 Webdetails, a Pentaho company.  All rights reserved.
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
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import pt.webdetails.di.baserver.utils.repositoryPlugin.PentahoConnectionConfiguration;
import pt.webdetails.di.baserver.utils.repositoryPlugin.RepositoryPlugin;

import java.util.Map;

public class PentahoConnectionConfigurationDialog extends Dialog {

  // for message resolution
  private static Class<?> PKG = RepositoryPlugin.class;

  // region Properties

  public PentahoConnectionConfigurationComposite getConfigurationComposite() {
    return this.configurationComposite;
  }
  private PentahoConnectionConfigurationComposite configurationComposite;

  public Button getOkButton() {
    return this.okButton;
  }
  private Button okButton;

  public Button getCancelButton() {
    return this.cancelButton;
  }
  private Button cancelButton;

  public Shell getShell() { return this.shell; }
  private PentahoConnectionConfigurationDialog setShell( Shell shell ) {
    this.shell = shell;
    return this;
  }
  private Shell shell;

  public PentahoConnectionConfiguration getEditedConfiguration() {
    return this.getConfigurationComposite().getPentahoConnectionConfiguration();
  }

  public PentahoConnectionConfiguration getOriginalConfiguration() {
    return this.originalConfiguration;
  }
  private PentahoConnectionConfiguration originalConfiguration;

  protected PropsUI getPropsUI() {
    return this.propsUI;
  }
  protected PentahoConnectionConfigurationDialog setPropsUI( PropsUI propsUI ) {
    this.propsUI = propsUI;
    return this;
  }
  private PropsUI propsUI;

  // endregion

  // region Constructors
  public PentahoConnectionConfigurationDialog( Shell parent,
                                               PentahoConnectionConfiguration pentahoConnectionConfiguration ) {
    super( parent );

    if ( pentahoConnectionConfiguration == null ) {
      throw new ArgumentNullException( "pentahoConnectionConfiguration" );
    }

    this.setPropsUI( PropsUI.getInstance() );
    this.originalConfiguration = pentahoConnectionConfiguration;

    this.createDialogElements();
  }
  // endregion


  // region Methods
  public void dispose() {
    Shell shell = this.getShell();
    this.getPropsUI().setScreen( new WindowProperty( shell ) );
    shell.dispose();
  }

  public void open() {
    Shell myShell = this.getShell();

    Display display = this.getParent().getDisplay();

    myShell.open();

    while ( !myShell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }

  }
  // endregion

  // region View
  private void createDialogElements() {
    Shell parent = this.getParent();
    Shell shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.CLOSE | SWT.MAX | SWT.MIN | SWT.ICON );
    this.setShell( shell );

    shell.setSize( 480, 550 );
    this.getPropsUI().setLook( shell );

    //FormLayout layout = new FormLayout();
    GridLayout layout = new GridLayout( 2, false );
    layout.marginWidth = Const.FORM_MARGIN;
    layout.marginHeight = Const.FORM_MARGIN;

    //TODO: i18n
    shell.setText( "Pentaho Configs" );
    shell.setLayout( layout );

    Composite group = this.buildGroup( shell, 2 );


    //TODO: Change this to another place?
    // Set panel variable space to use by default the
    // environment and kettle properties file variables
    VariableSpace variableSpace = new Variables();
    variableSpace.copyVariablesFrom( this.getEnvironmentVariables( Spoon.getInstance() ) );
    variableSpace.copyVariablesFrom( this.getKettlePropertiesVariables() );
    PentahoConnectionConfiguration configurationToEdit = this.getOriginalConfiguration().clone();
    this.configurationComposite = new PentahoConnectionConfigurationComposite( group, this.getPropsUI(), variableSpace , configurationToEdit );


    this.okButton = this.buildButton( group, "System.Button.OK" );
    this.cancelButton = this.buildButton( group, "System.Button.Cancel" );
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

  // region Aux
  /***
   * Gets the Spoon environment variables space
   * @param spoon
   * @return
   */
  private VariableSpace getEnvironmentVariables( Spoon spoon ) {
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

  /***
   * Gets the kettle properties file variables space
   * @return
   */
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
  // endregion


}
