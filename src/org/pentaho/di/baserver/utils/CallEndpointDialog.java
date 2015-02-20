/*!
* Copyright 2002 - 2014 Webdetails, a Pentaho company.  All rights reserved.
*
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package org.pentaho.di.baserver.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.di.baserver.utils.widgets.CheckBoxBuilder;
import org.pentaho.di.baserver.utils.widgets.LabelBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Marco Vala
 */
public class CallEndpointDialog extends BaseStepDialog implements StepDialogInterface {
  private static Class<?> PKG = CallEndpointMeta.class; // for i18n purposes, needed by Translator2!!

  public static final int LEFT_PLACEMENT = 0;
  public static final int RIGHT_PLACEMENT = 100;

  private CallEndpointMeta metaInfo;
  private Inspector inspector;
  private boolean connected;

  private Text stepName;

  private Group serverGroup;
  private TextVar serverUrl;
  private TextVar userName;
  private TextVar password;
  private Label connectionStatus;

  private Group endpointGroup;
  private ComboVar moduleName;
  private ComboVar endpointPath;
  private ComboVar httpMethod;
  private Button endpointFromFields;
  private ComboVar moduleNameField;
  private ComboVar endpointPathField;
  private ComboVar httpMethodField;
  private Button isBypassingAuthentication;

  private Group outputFieldsGroup;
  private TextVar resultField;
  private TextVar statusCodeField;
  private TextVar responseTimeField;

  private TableView queryParameters;
  private ColumnInfo cFieldName;
  private ColumnInfo cParameter;


  public CallEndpointDialog( Shell parent, Object in, TransMeta transMeta, String name ) {
    super( parent, (BaseStepMeta) in, transMeta, name );
    this.metaInfo = (CallEndpointMeta) in;
    this.inspector = Inspector.getInstance();
    this.connected = false;
  }


  private void refresh() {
    String serverUrl = this.transMeta.environmentSubstitute( this.serverUrl.getText() );
    String userName = this.transMeta.environmentSubstitute( this.userName.getText() );
    String password = this.transMeta.environmentSubstitute( this.password.getText() );
    if ( this.inspector.inspectServer( serverUrl, userName, password ) ) {
      if ( !this.connected ) {
        this.connected = true;
        this.connectionStatus.setText( "Connected!" );
        this.connectionStatus.setForeground( getParent().getDisplay().getSystemColor( SWT.COLOR_DARK_GREEN ) );
        updateModuleNamesComboBox();
        updateEndpointPathsComboBox();
        updateHttpMethodsComboBox();
      }
    } else {
      this.connected = false;
      this.connectionStatus.setText( "Could not connect to server." );
      this.connectionStatus.setForeground( getParent().getDisplay().getSystemColor( SWT.COLOR_RED ) );
    }
  }

  private void updateModuleNamesComboBox() {
    String moduleName = this.transMeta.environmentSubstitute( this.moduleName.getText() );
    this.moduleName.removeAll();
    for ( String item : this.inspector.getModuleNames() ) {
      this.moduleName.add( item );
    }
    if ( moduleName.equals( "" ) ) {
      moduleName = this.inspector.getDefaultModuleName();
    }
    this.moduleName.setText( moduleName );
  }

  private void updateEndpointPathsComboBox() {
    String moduleName = this.transMeta.environmentSubstitute( this.moduleName.getText() );
    String endpointPath = this.transMeta.environmentSubstitute( this.endpointPath.getText() );
    this.endpointPath.removeAll();
    for ( String path : this.inspector.getEndpointPaths( moduleName ) ) {
      this.endpointPath.add( path );
    }
    if ( endpointPath.equals( "" )  ) {
      endpointPath = this.inspector.getDefaultEndpointPath( moduleName );
    }
    this.endpointPath.setText( endpointPath );
  }

  private void updateHttpMethodsComboBox() {
    String moduleName = this.transMeta.environmentSubstitute( this.moduleName.getText() );
    String endpointPath = this.transMeta.environmentSubstitute( this.endpointPath.getText() );
    String httpMethod = this.transMeta.environmentSubstitute( this.httpMethod.getText() );
    Iterable<Endpoint> endpoints = this.inspector.getEndpoints( moduleName, endpointPath );
    this.httpMethod.removeAll();
    for ( Endpoint endpoint : endpoints ) {
      this.httpMethod.add( endpoint.getHttpMethod().name() );
    }
    if ( endpointPath.equals( "" ) ) {
      Endpoint endpoint = this.inspector.getDefaultEndpoint( moduleName, endpointPath );
      if ( endpoint != null ) {
        httpMethod = endpoint.getHttpMethod().name();
      }
    }
    this.httpMethod.setText( httpMethod );
  }

  private void setDefaultEndpointPath() {
    String moduleName = this.transMeta.environmentSubstitute( this.moduleName.getText() );
    this.endpointPath.setText( this.inspector.getDefaultEndpointPath( moduleName ) );
  }

  private void setDefaultHttpMethod() {
    String moduleName = this.transMeta.environmentSubstitute( this.moduleName.getText() );
    String path = this.transMeta.environmentSubstitute( this.endpointPath.getText() );
    Endpoint endpoint = this.inspector.getDefaultEndpoint( moduleName, path );
    if ( endpoint != null ) {
      this.httpMethod.setText( endpoint.getHttpMethod().name() );
    }
    else {
      this.httpMethod.setText( "" );
    }
  }

  private Collection<String> getFieldNames() {
    StepMeta stepMeta = this.transMeta.findStep( this.stepname );
    if ( stepMeta != null ) {
      try {
        // get field names from previous steps
        RowMetaInterface row = this.transMeta.getPrevStepFields( stepMeta );
        List<String> entries = new ArrayList<String>();
        for ( int i = 0; i < row.size(); i++ ) {
          entries.add( row.getValueMeta( i ).getName() );
        }
        return entries;

        //fieldNames = entries.toArray( new String[ entries.size() ] );
        //Const.sortStrings( fieldNames );
      } catch ( KettleException e ) {
        logError( BaseMessages.getString( PKG, "System.Dialog.GetFieldsFailed.Message" ) );
      }
    }
    return Collections.emptySet();
  }

  private void updateFieldNamesComboBox() {
    StepMeta stepMeta = transMeta.findStep( stepname );
    if ( stepMeta != null ) {
      try {
        // get field names from previous steps
        RowMetaInterface row = transMeta.getPrevStepFields( stepMeta );
        List<String> entries = new ArrayList<String>();
        for ( int i = 0; i < row.size(); i++ ) {
          entries.add( row.getValueMeta( i ).getName() );
        }
        String[] fieldNames = entries.toArray( new String[ entries.size() ] );

        // sort field names and add them to the combo box
        Const.sortStrings( fieldNames );
        cFieldName.setComboValues( fieldNames );
      } catch ( KettleException e ) {
        logError( BaseMessages.getString( PKG, "System.Dialog.GetFieldsFailed.Message" ) );
      }
    }
  }

  private void processInputChange() {
    this.metaInfo.setChanged( this.changed );
    wOK.setEnabled( !Const.isEmpty( stepName.getText() ) );
  }

  private void ok() {
    // keep information for next time
    saveData( metaInfo );
    dispose();
  }

  private void cancel() {
    // fill return value
    stepname = null;
    dispose();
  }


  //region Step Name

  private void buildStepNameInput( Composite parent ) {

    // label
    Label stepNameLabel = new LabelBuilder( parent, this.props )
      .setText( "Step name" )
      .setRightPlacement( this.props.getMiddlePct() )
      .build();

    // step name input
    this.stepName = new TextBoxBuilder( parent, this.props )
      .setLeft( stepNameLabel )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    this.stepName.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );
  }

  //endregion

  // region Server Group

  private void buildServerGroup( Composite parent ) {

    // group
    this.serverGroup = new GroupBuilder( parent, this.props )
      .setText( "BA Server" )
      .setTop( this.stepName )
      .setLeftPlacement( LEFT_PLACEMENT )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    // widgets
    buildServerUrlInput( this.serverGroup );
    buildUserNameInput( this.serverGroup );
    buildPasswordInput( this.serverGroup );
    buildRefreshEndpointListButton( this.serverGroup );
  }

  private void buildServerUrlInput( Composite parent ) {
    // label
    Label serverUrlLabel = new LabelBuilder( parent, this.props )
      .setText( "Server URL" )
      .setRightPlacement( this.props.getMiddlePct() )
      .build();

    // server url input box
    this.serverUrl = new TextVarBuilder( parent, this.props, this.transMeta )
      .setLeft( serverUrlLabel )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    this.serverUrl.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );
  }

  private void buildUserNameInput( Composite parent ) {
    // label
    Label userNameLabel = new LabelBuilder( parent, this.props )
      .setText( "User name" )
      .setTop( this.serverUrl )
      .setRightPlacement( this.props.getMiddlePct() )
      .build();

    // user name input box
    this.userName = new TextVarBuilder( parent, this.props, this.transMeta )
      .setTop( this.serverUrl )
      .setLeft( userNameLabel )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    this.userName.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );
  }

  private void buildPasswordInput( Composite parent ) {
    // label
    Label passwordLabel = new LabelBuilder( parent, this.props )
      .setText( "Password" )
      .setTop( this.userName )
      .setRightPlacement( this.props.getMiddlePct() )
      .build();

    // password input box
    this.password = new TextVarBuilder( parent, this.props, this.transMeta )
      .setTop( this.userName )
      .setLeft( passwordLabel )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    this.password.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent modifyEvent ) {
        protectPasswordWithEchoChar();
        processInputChange();
      }
    } );
  }

  private void protectPasswordWithEchoChar() {
    if ( this.password.getText().startsWith( "${" ) ) {
      this.password.setEchoChar( '\0' );
    } else {
      this.password.setEchoChar( '*' );
    }
  }

  private void buildRefreshEndpointListButton( Composite parent ) {

    Button button = new ButtonBuilder( parent, this.props )
      .setLabelText( "Connect to server and get available endpoints" )
      .setTop( this.password )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    Label statusSeparator = new SeparatorBuilder( parent, this.props )
      .setTop( button )
      .setLeftPlacement( LEFT_PLACEMENT )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    this.connectionStatus = new LabelBuilder( parent, this.props )
      .setText( "                                              " )
      .setTop( statusSeparator )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    button.addSelectionListener( new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        super.widgetSelected( selectionEvent );
        refresh();
      }
    } );
  }

  // endregion

  //region Endpoint Group

  private void buildEnpointGroup( Composite parent ) {
    // group
    this.endpointGroup = new GroupBuilder( parent, this.props )
      .setText( "Endpoint" )
      .setTop( this.serverGroup )
      .setLeftPlacement( LEFT_PLACEMENT )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    // widgets
    buildModuleNameInput( this.endpointGroup );
    buildEndpointPathInput( this.endpointGroup );
    buildHttpMethodInput( this.endpointGroup );
    buildEndpointFromFieldInput( this.endpointGroup );
    buildBypassAuthenticationCheck( this.endpointGroup );
  }

  private void buildModuleNameInput( Composite parent ) {

    Label moduleNameLabel = new LabelBuilder( parent, this.props )
      .setText( "Module name" )
      .setRightPlacement( this.props.getMiddlePct() )
      .build();

    this.moduleName = new ComboVarBuilder( parent, this.props, this.transMeta )
      .setLeft( moduleNameLabel )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    this.moduleName.addSelectionListener( new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        super.widgetSelected( selectionEvent );
        updateEndpointPathsComboBox();
        setDefaultEndpointPath();
        updateHttpMethodsComboBox();
        setDefaultHttpMethod();
      }
    } );

    this.moduleName.addModifyListener( new ModifyListener() {
      @Override public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );
  }

  private void buildEndpointPathInput( Composite parent ) {

    Label endpointPathLabel = new LabelBuilder( parent, this.props )
      .setText( "Endpoint path" )
      .setTop( this.moduleName )
      .setRightPlacement( this.props.getMiddlePct() )
      .build();

    this.endpointPath = new ComboVarBuilder( parent, this.props, this.transMeta )
      .setTop( this.moduleName )
      .setLeft( endpointPathLabel )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    this.endpointPath.addModifyListener( new ModifyListener() {
      @Override public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );

    this.endpointPath.addSelectionListener( new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        super.widgetSelected( selectionEvent );
        updateHttpMethodsComboBox();
        setDefaultHttpMethod();
      }
    } );
  }

  private void buildHttpMethodInput( Composite parent ) {

    Label httpMethodLabel = new LabelBuilder( parent, this.props )
      .setText( "HTTP method" )
      .setTop( this.endpointPath )
      .setRightPlacement( this.props.getMiddlePct() )
      .build();

    this.httpMethod = new ComboVarBuilder( parent, this.props, this.transMeta )
      .setTop( this.endpointPath )
      .setLeft( httpMethodLabel )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    this.httpMethod.addModifyListener( new ModifyListener() {
      @Override public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );
  }


  private void buildEndpointFromFieldInput( Composite parent ) {

    Label endpointIsFieldSeparator = new SeparatorBuilder( parent, this.props )
      .setTop( this.httpMethod )
      .setLeftPlacement( LEFT_PLACEMENT )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    Label endpointIsFieldLabel = new LabelBuilder( parent, this.props )
      .setText( "Endpoint from fields" )
      .setTop( endpointIsFieldSeparator )
      .setRightPlacement( this.props.getMiddlePct() )
      .build();

    this.endpointFromFields = new CheckBoxBuilder( parent, this.props )
      .setTop( endpointIsFieldSeparator )
      .setLeft( endpointIsFieldLabel )
      .build();

    this.endpointFromFields.addSelectionListener( new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        super.widgetSelected( selectionEvent );
        toggleIsEndpointFromField();
      }
    } );

    // module name field
    Label moduleNameFieldLabel = new LabelBuilder( parent, props )
      .setText( "Module field name" )
      .setTop( this.endpointFromFields )
      .setRightPlacement( this.props.getMiddlePct() )
      .build();

    this.moduleNameField = new ComboVarBuilder( parent, props, transMeta )
      .addAllItems( getFieldNames() )
      .setTop( this.endpointFromFields )
      .setLeft( moduleNameFieldLabel )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    this.moduleNameField.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );

    Label endpointFieldLabel = new LabelBuilder( parent, this.props )
      .setText( "Endpoint path field name" )
      .setTop( this.moduleNameField )
      .setRightPlacement( this.props.getMiddlePct() )
      .build();

    this.endpointPathField = new ComboVarBuilder( parent, this.props, this.transMeta )
      .addAllItems( getFieldNames() )
      .setTop( this.moduleNameField )
      .setLeft( endpointFieldLabel )
      .setRightPlacement( RIGHT_PLACEMENT )
      .setEnabled( false )
      .build();

    this.endpointPathField.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );

    Label endpointTypeFieldLabel = new LabelBuilder( parent, this.props )
      .setText( "Http method field name" )
      .setTop( this.endpointPathField )
      .setRightPlacement( this.props.getMiddlePct() )
      .build();

    this.httpMethodField = new ComboVarBuilder( parent, this.props, this.transMeta )
      .addAllItems( getFieldNames() )
      .setTop( this.endpointPathField )
      .setLeft( endpointTypeFieldLabel )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    this.httpMethodField.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );
  }

  private boolean toggleIsEndpointFromField() {
    boolean isEnabled = endpointFromFields.getSelection();
    this.moduleName.setEnabled( !isEnabled );
    this.endpointPath.setEnabled( !isEnabled );
    this.httpMethod.setEnabled( !isEnabled );
    this.moduleNameField.setEnabled( isEnabled );
    this.endpointPathField.setEnabled( isEnabled );
    this.httpMethodField.setEnabled( isEnabled );
    return isEnabled;
  }

  private void buildBypassAuthenticationCheck( Composite parent ) {

    Label bypassAuthenticationCheckSeparator = new SeparatorBuilder( parent, this.props )
      .setTop( this.httpMethodField )
      .setLeftPlacement( LEFT_PLACEMENT )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    // label
    Label bypassAuthenticationCheckLabel = new LabelBuilder( parent, this.props )
      .setText( "Bypass authentication" )
      .setTop( bypassAuthenticationCheckSeparator )
      .setRightPlacement( this.props.getMiddlePct() )
      .build();

    // is bypassing authentication check box
    this.isBypassingAuthentication = new CheckBoxBuilder( parent, this.props )
      .setTop( bypassAuthenticationCheckSeparator )
      .setLeft( bypassAuthenticationCheckLabel )
      .build();

    this.isBypassingAuthentication.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent selectionEvent ) {
        super.widgetSelected( selectionEvent );
        processInputChange();
      }
    } );
  }

  //endregion

  //region Output Fields Group

  private void buildOutputFieldsGroup( Composite parent ) {

    // group
    this.outputFieldsGroup = new GroupBuilder( parent, this.props )
      .setText( "Output Fields" )
      .setTop( this.endpointGroup )
      .setLeftPlacement( LEFT_PLACEMENT )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    // widgets
    buildResultFieldInput( this.outputFieldsGroup );
    buildStatusCodeFieldInput( this.outputFieldsGroup );
    buildResponseTimeFieldInput( this.outputFieldsGroup );
  }

  private void buildResultFieldInput( Composite parent ) {

    // label
    Label resultFieldLabel = new LabelBuilder( parent, this.props )
      .setText( "Result field name" )
      .setRightPlacement( this.props.getMiddlePct() )
      .build();

    // result field name input
    this.resultField = new TextVarBuilder( parent, this.props, this.transMeta )
      .setLeft( resultFieldLabel )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    this.resultField.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );
  }

  private void buildStatusCodeFieldInput( Composite parent ) {

    // label
    Label statusCodeLabel = new LabelBuilder( parent, this.props )
      .setText( "Status code field name" )
      .setTop( this.resultField )
      .setRightPlacement( this.props.getMiddlePct() )
      .build();

    // status code field name input
    this.statusCodeField = new TextVarBuilder( parent, this.props, this.transMeta )
      .setTop( this.resultField )
      .setLeft( statusCodeLabel )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    this.statusCodeField.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );
  }

  private void buildResponseTimeFieldInput( Composite parent ) {

    // label
    Label responseTimeLabel = new LabelBuilder( parent, this.props )
      .setText( "Response time field name" )
      .setTop( this.statusCodeField )
      .setRightPlacement( this.props.getMiddlePct() )
      .build();

    // response time field name input
    this.responseTimeField = new TextVarBuilder( parent, this.props, this.transMeta )
      .setTop( this.statusCodeField )
      .setLeft( responseTimeLabel )
      .setRightPlacement( RIGHT_PLACEMENT )
      .build();

    this.responseTimeField.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );
  }

  //endregion Output Fields Group

  @Override
  public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    // create shell
    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN );
    shell.setText( BaseMessages.getString( PKG, "CallEndpointDialog.DialogTitle" ) );

    // create form layout
    FormLayout formLayout = new FormLayout();
    formLayout.marginHeight = Const.FORM_MARGIN;
    formLayout.marginWidth = Const.FORM_MARGIN;
    shell.setLayout( formLayout );

    props.setLook( shell );
    setShellImage( shell, metaInfo );

    // widgets
    buildStepNameInput( this.shell );
    buildServerGroup( this.shell );
    buildEnpointGroup( this.shell );
    buildOutputFieldsGroup( this.shell );

    this.cFieldName = new ColumnInfo( BaseMessages.getString( PKG, "CallEndpointDialog.Column.FieldName" ),
      ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, false );

    this.cParameter = new ColumnInfo( BaseMessages.getString( PKG, "CallEndpointDialog.Column.Parameter" ),
      ColumnInfo.COLUMN_TYPE_TEXT, false );
    this.cParameter.setUsingVariables( true );

    ColumnInfo cDefaultValue = new ColumnInfo( BaseMessages.getString( PKG, "CallEndpointDialog.Column.DefaultValue" ),
      ColumnInfo.COLUMN_TYPE_TEXT, false );
    cDefaultValue.setUsingVariables( true );
    cDefaultValue.setToolTip( BaseMessages.getString( PKG, "SetSessionVariableDialog.Column.DefaultValue.Tooltip" ) );

    Label lParameters = new LabelBuilder( shell, props )
      .setText( "Query parameters:" )
      .setTop( outputFieldsGroup )
      .build();
    this.queryParameters = new TableViewBuilder( props, shell, transMeta )
      .addColumnInfo( cFieldName )
      .addColumnInfo( cParameter )
      .addColumnInfo( cDefaultValue )
      .setRowsCount( metaInfo.getFieldName().length )
      .setModifyListener( new ModifyListener() {
        @Override
        public void modifyText( ModifyEvent modifyEvent ) {
          processInputChange();
        }
      } )
      .setTop( lParameters )
      .setLeftPlacement( 0 )
      .setRightPlacement( 100 )
      .setBottomPlacement( 100 )
      .setBottomMargin( 50 )
      .build();

    // background thread that updates field name combo box
    final Runnable runnable = new Runnable() {
      public void run() {
        updateFieldNamesComboBox();
      }
    };
    new Thread( runnable ).start();

    // buttons
    wOK = new Button( shell, SWT.PUSH );
    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    lsOK = new Listener() {
      public void handleEvent( Event e ) {
        ok();
      }
    };
    wOK.addListener( SWT.Selection, lsOK );
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );
    lsCancel = new Listener() {
      public void handleEvent( Event e ) {
        cancel();
      }
    };
    wCancel.addListener( SWT.Selection, lsCancel );
    setButtonPositions( new Button[] { wOK, wCancel }, Const.MARGIN, this.queryParameters );

    // listener to add default action
    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };
    stepName.addSelectionListener( lsDef );

    // listener to detect X or something that kills this window
    ShellListener lsShell = new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    };
    shell.addShellListener( lsShell );

    // load information (based on previous usage)
    loadData( metaInfo );

    // set the shell size (based on previous usage)
    setSize();

    // set focus on step name
    stepName.selectAll();
    stepName.setFocus();

    // open shell
    shell.open();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return stepname;
  }

  private void loadData( CallEndpointMeta meta ) {
    // load step name
    stepName.setText( stepname );

    serverUrl.setText( meta.getServerURL() );
    userName.setText( meta.getUserName() );
    password.setText( meta.getPassword() );

    endpointFromFields.setSelection( meta.isEndpointFromField() );
    if ( toggleIsEndpointFromField() ) {
      moduleNameField.setText( meta.getModuleName() );
      endpointPathField.setText( meta.getEndpointPath() );
      httpMethodField.setText( meta.getHttpMethod() );
    } else {
      moduleName.setText( meta.getModuleName() );
      endpointPath.setText( meta.getEndpointPath() );
      httpMethod.setText( meta.getHttpMethod() );
    }
    isBypassingAuthentication.setSelection( meta.isBypassingAuthentication() );

    resultField.setText( meta.getResultField() );
    statusCodeField.setText( meta.getStatusCodeField() );
    responseTimeField.setText( meta.getResponseTimeField() );

    // load fields
    for ( int i = 0; i < meta.getFieldName().length; i++ ) {
      TableItem item = queryParameters.table.getItem( i );
      int index = 0;
      item.setText( ++index, Const.NVL( meta.getFieldName()[ i ], "" ) );
      item.setText( ++index, Const.NVL( meta.getParameter()[ i ], "" ) );
      item.setText( ++index, Const.NVL( meta.getDefaultValue()[ i ], "" ) );
    }
    queryParameters.setRowNums();
    queryParameters.optWidth( true );
  }

  private void saveData( CallEndpointMeta meta ) {
    // save step name
    stepname = stepName.getText();

    meta.setServerURL( serverUrl.getText() );
    meta.setUserName( userName.getText() );
    meta.setPassword( password.getText() );

    meta.setEndpointFromField( endpointFromFields.getSelection() );
    if ( endpointFromFields.getSelection() ) {
      meta.setModuleName( moduleNameField.getText() );
      meta.setEndpointPath( endpointPathField.getText() );
      meta.setHttpMethod( httpMethodField.getText() );
    } else {
      meta.setModuleName( moduleName.getText() );
      meta.setEndpointPath( endpointPath.getText() );
      meta.setHttpMethod( httpMethod.getText() );
    }
    meta.setBypassingAuthentication( isBypassingAuthentication.getSelection() );

    meta.setResultField( resultField.getText() );
    meta.setStatusCodeField( statusCodeField.getText() );
    meta.setResponseTimeField( responseTimeField.getText() );

    // save fields
    int count = queryParameters.nrNonEmpty();
    meta.allocate( count );
    for ( int i = 0; i < count; i++ ) {
      TableItem item = queryParameters.getNonEmpty( i );
      int index = 0;
      meta.getFieldName()[ i ] = item.getText( ++index );
      meta.getParameter()[ i ] = item.getText( ++index );
      meta.getDefaultValue()[ i ] = item.getText( ++index );
    }
  }
}
