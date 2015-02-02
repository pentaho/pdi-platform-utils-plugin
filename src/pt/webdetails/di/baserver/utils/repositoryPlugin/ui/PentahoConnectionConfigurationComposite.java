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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.TextVar;
import pt.webdetails.di.baserver.utils.repositoryPlugin.IPentahoConnectionConfiguration;
import pt.webdetails.di.baserver.utils.repositoryPlugin.PentahoConnectionConfiguration;
import pt.webdetails.di.baserver.utils.repositoryPlugin.RepositoryPlugin;


public class PentahoConnectionConfigurationComposite extends Composite {

  // region Inner Definitions
  private interface Callback {
    public void invoke( String newValue );
  }
  // endregion

  // for message resolution
  private static Class<?> PKG = RepositoryPlugin.class;

  // region Properties
  public PentahoConnectionConfiguration getPentahoConnectionConfiguration() {
    return this.pentahoConnectionConfiguration;
  }
  private PentahoConnectionConfiguration pentahoConnectionConfiguration;

  public VariableSpace getVariableSpace() {
    return this.variableSpace;
  }
  public PentahoConnectionConfigurationComposite setVariableSpace( VariableSpace variableSpace ) {
    this.variableSpace = variableSpace;
    return this;
  }
  private VariableSpace variableSpace;

  protected TextVar configName;
  protected TextVar serverUrl;
  protected TextVar userName;
  protected TextVar password;

  // endregion

  // region Constructors
  public PentahoConnectionConfigurationComposite( Composite parent,
                                                  PropsUI propsUI,
                                                  VariableSpace variableSpace,
                                                  PentahoConnectionConfiguration pentahoConnectionConfiguration ) {
    super( parent, SWT.NONE );

    propsUI.setLook( this );

    this.setVariableSpace( variableSpace );

    if ( pentahoConnectionConfiguration == null ) {
      // TODO get default configuration? not accept null?
    }
    this.pentahoConnectionConfiguration = pentahoConnectionConfiguration;

    // view
    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;
    this.setLayout( formLayout );

    this.createPanel();
  }
  // endregion


  // region create View
  private void createPanel( ) {
    Composite group = this.buildGroup( this.getParent(), 2 );

    final IPentahoConnectionConfiguration configuration = this.getPentahoConnectionConfiguration();

    this.configName = this.buildTextInput( group, "PentahoSolutionVfsFileChooserPanel.ConnectionName.Label",
      new Callback() {
        @Override public void invoke( String newValue ) {
          configuration.setName( newValue );
        }
      }
    );

    this.serverUrl = this.buildTextInput( group, "PentahoSolutionVfsFileChooserPanel.ServerUrl.Label",
      new Callback() {
        @Override public void invoke( String newValue ) {
          configuration.setServerUrl( newValue );
        }
      }
    );
    this.serverUrl.setText( configuration.getServerUrl() );

    this.userName = this.buildTextInput( group, "PentahoSolutionVfsFileChooserPanel.UserName.Label",
      new Callback() {
        @Override public void invoke( String newValue ) {
          configuration.setUserName( newValue );
        }
      } );
    this.userName.setText( configuration.getUserName() );

    this.password = this.buildTextInput( group, "PentahoSolutionVfsFileChooserPanel.Password.Label",
      new Callback() {
        @Override public void invoke( String newValue ) {
          configuration.setPassword( newValue );
        }
      }, true );
    this.password.setText( configuration.getPassword() );

    this.buildEmptyWidget( group );
  }

  private Composite buildGroup( Composite parent, int numberOfColumns ) {
    // The Connection group
    Group connectionGroup = new Group( parent, SWT.NONE ); //SWT.SHADOW_ETCHED_IN );
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

  private TextVar buildTextInput( Composite parent, String i18nLabelKey, final Callback onTextChangedCallback, boolean isPassword ) {
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
    final TextVar textVarInput = new TextVar( this.getVariableSpace(), parent, flags );
    GridData inputGridData = new GridData();
    inputGridData.widthHint = 250;
    inputGridData.grabExcessHorizontalSpace = true;
    inputGridData.horizontalAlignment = SWT.FILL;
    textVarInput.setLayoutData( inputGridData );

    if ( onTextChangedCallback != null ) {
      textVarInput.addKeyListener( new KeyListener() {
        @Override public void keyPressed( KeyEvent keyEvent ) {
          onTextChangedCallback.invoke( textVarInput.getText() );
        }

        @Override public void keyReleased( KeyEvent keyEvent ) { }
      } );
    }
    return textVarInput;
  }

  private TextVar buildTextInput( Composite parent, String i18nLabelKey, final Callback onTextChangedCallBack ) {
    return this.buildTextInput( parent, i18nLabelKey, onTextChangedCallBack, false );
  }

  // endregion



}
