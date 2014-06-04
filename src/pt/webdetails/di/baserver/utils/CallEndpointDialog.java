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

package pt.webdetails.di.baserver.utils;

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
import org.eclipse.swt.widgets.Control;
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
import pt.webdetails.di.baserver.utils.inspector.Endpoint;
import pt.webdetails.di.baserver.utils.inspector.Inspector;
import pt.webdetails.di.baserver.utils.widgets.ButtonBuilder;
import pt.webdetails.di.baserver.utils.widgets.CheckBoxBuilder;
import pt.webdetails.di.baserver.utils.widgets.ComboVarBuilder;
import pt.webdetails.di.baserver.utils.widgets.GroupBuilder;
import pt.webdetails.di.baserver.utils.widgets.LabelBuilder;
import pt.webdetails.di.baserver.utils.widgets.TableViewBuilder;
import pt.webdetails.di.baserver.utils.widgets.TextBoxBuilder;
import pt.webdetails.di.baserver.utils.widgets.TextVarBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Marco Vala
 */
public class CallEndpointDialog extends BaseStepDialog implements StepDialogInterface {
  private static Class<?> PKG = CallEndpointMeta.class; // for i18n purposes, needed by Translator2!!

  private CallEndpointMeta metaInfo;
  private Inspector inspector;

  private Text stepName;

  private Group serverGroup;
  private TextVar serverUrl;
  private TextVar userName;
  private TextVar password;
  private Button isBypassingAuthentication;

  private Group moduleGroup;
  private ComboVar moduleName;
  private Button isModuleFromField;
  private ComboVar moduleNameField;

  private Group endpointGroup;
  private ComboVar endpointPath;
  private ComboVar httpMethod;
  private Button isEndpointFromField;
  private ComboVar endpointPathField;
  private ComboVar httpMethodField;

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

  private void updateModuleNames() {
    this.inspector.updateModules();
    this.moduleName.removeAll();
    this.moduleName.add( "platform" );
    for ( String item : this.inspector.getModuleNames() ) {
      this.moduleName.add( item );
    }
  }

  private void updateEndpointNames() {
    String moduleName = this.transMeta.environmentSubstitute( this.moduleName.getText() );
    if ( moduleName.equals( "platform" ) ) {
      this.inspector.updateEndpoints( null );
    } else {
      this.inspector.updateEndpoints( moduleName );
    }
    boolean first = true;
    this.endpointPath.removeAll();
    for ( String path : this.inspector.getEndpointPaths() ) {
      this.endpointPath.add( path );
      if ( first ) {
        this.endpointPath.setText( path );
        first = false;
      }
    }
  }

  private void updateHttpMethods() {
    String path = this.endpointPath.getText();
    Iterable<Endpoint> endpoints = this.inspector.getEndpointsWithPath( path );

    boolean first = true;
    this.httpMethod.removeAll();
    for ( Endpoint item : endpoints ) {
      this.httpMethod.add( item.getHttpMethod().name() );
      if ( first ) {
        this.httpMethod.setText( item.getHttpMethod().name() );
        first = false;
      }
    }
  }

  private void updateEndpointParams() {
    String path = this.endpointPath.getText();
    String type = this.httpMethod.getText();

    Endpoint endpoint = this.inspector.getEndpoint( path, type );

    if ( endpoint != null ) {
      // TODO add parameters to table
    }
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


  private void initInspector() {
    String serverUrl = this.transMeta.environmentSubstitute( this.serverUrl.getText() );
    String userName = this.transMeta.environmentSubstitute( this.userName.getText() );
    String password = this.transMeta.environmentSubstitute( this.password.getText() );

    this.inspector.setServer( serverUrl, userName, password );
  }



  //region Step Name

  private void buildStepNameInput( Composite parent ) {

    // label
    Label stepNameLabel = new LabelBuilder( parent, this.props )
      .setText( "Step name" )
      .build();

    // step name input
    this.stepName = new TextBoxBuilder( parent, this.props )
      .setWidth( 200 )
      .setLeft( stepNameLabel )
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
      .build();

    // widgets
    buildServerUrlInput( this.serverGroup );
    buildUserNameInput( this.serverGroup );
    buildPasswordInput( this.serverGroup );
    buildIsBypassingAuthenticationCheck( this.serverGroup );

    Button bt = new ButtonBuilder( this.serverGroup, this.props )
      .setLabelText( "Refresh endpoint list" )
      .setTop( this.isBypassingAuthentication )
      .build();

    bt.addSelectionListener( new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        super.widgetSelected( selectionEvent );
        initInspector();
        updateModuleNames();
      }
    } );
  }

  private void buildServerUrlInput( Composite parent ) {

    // label
    Label serverUrlLabel = new LabelBuilder( parent, this.props )
      .setText( "Server URL" )
      .setWidth( 170 )
      .build();

    // server url input box
    this.serverUrl = new TextVarBuilder( parent, this.props, this.transMeta )
      .setLeft( serverUrlLabel )
      .setWidth( 350 )
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
      .setWidth( 170 )
      .build();

    // user name input box
    this.userName = new TextVarBuilder( parent, this.props, this.transMeta )
      .setTop( this.serverUrl )
      .setLeft( userNameLabel )
      .setWidth( 200 )
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
      .setWidth( 170 )
      .build();

    // password input box
    this.password = new TextVarBuilder( parent, this.props, this.transMeta )
      .setTop( this.userName )
      .setLeft( passwordLabel )
      .setWidth( 200 )
      .build();

    this.password.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent modifyEvent ) {
        protectPasswordWithEchoChar();
        processInputChange();
      }
    } );
  }

  private void buildIsBypassingAuthenticationCheck( Composite parent ) {

    // label
    Label bypassAuthenticationCheckLabel = new LabelBuilder( parent, this.props )
      .setText( "Bypass authentication on local execution?" )
      .setTop( this.password )
      .setWidth( 340 )
      .build();

    // is bypassing authentication check box
    this.isBypassingAuthentication = new CheckBoxBuilder( parent, this.props )
      .setTop( this.password )
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

  private void protectPasswordWithEchoChar() {
    if ( this.password.getText().startsWith( "${" ) ) {
      this.password.setEchoChar( '\0' );
    } else {
      this.password.setEchoChar( '*' );
    }
  }

  // endregion

  // region Module Group

  /*
  private void buildModuleGroup( Composite parent ) {
    // group
    this.moduleGroup = new GroupBuilder( parent, this.props )
      .setText( "Module" )
      .setTop( this.serverGroup )
      .build();

    // widgets
    //buildModuleNameInput( this.moduleGroup );
    //buildModuleFromFieldInput( this.moduleGroup );
  }
  */






  // endregion

  //region Endpoint Group

  private void buildEnpointGroup( Composite parent ) {

    // group
    this.endpointGroup = new GroupBuilder( parent, this.props )
      .setText( "Endpoint" )
      .setTop( this.serverGroup )
      .build();

    // widgets
    buildModuleNameInput( this.endpointGroup );
    buildEndpointInput( this.endpointGroup );
    new Label( parent, SWT.SEPARATOR | SWT.HORIZONTAL );
    buildModuleFromFieldInput( this.endpointGroup );
    buildEndpointFromFieldInput( this.endpointGroup );
  }

  private void buildModuleNameInput( Composite parent ) {
    // label
    Label moduleNameLabel = new LabelBuilder( parent, this.props )
      .setText( "Module name" )
      .setWidth( 170 )
      .build();

    // input box
    this.moduleName = new ComboVarBuilder( parent, this.props, this.transMeta )
      .setLeft( moduleNameLabel )
      .setWidth( 350 )
      .build();

    this.moduleName.addSelectionListener( new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        super.widgetSelected( selectionEvent );
        updateEndpointNames();
      }
    } );

    this.moduleName.addModifyListener( new ModifyListener() {
      @Override public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );
  }

  private void buildEndpointInput( Composite parent ) {

    Control top = this.moduleName;

    // endpoint path
    Label endpointNameLabel = new LabelBuilder( parent, this.props )
      .setText( "Endpoint path" )
      .setTop( top )
      .setWidth( 170 )
      .build();
    this.endpointPath = new ComboVarBuilder( parent, this.props, this.transMeta )
      .setTop( top )
      .setLeft( endpointNameLabel )
      .setWidth( 350 )
      .build();
    this.endpointPath.addModifyListener( new ModifyListener() {
      @Override public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );
    this.endpointPath.addSelectionListener( new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        super.widgetSelected( selectionEvent );
        updateHttpMethods();
      }
    } );

    // endpoint type
    Label endpointTypeLabel = new LabelBuilder( parent, this.props )
      .setText( "Http method" )
      .setTop( this.endpointPath )
      .setWidth( 170 )
      .build();
    this.httpMethod = new ComboVarBuilder( parent, this.props, this.transMeta )
      .setTop( this.endpointPath )
      .setLeft( endpointTypeLabel )
      .setWidth( 150 )
      .build();
    this.httpMethod.addModifyListener( new ModifyListener() {
      @Override public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );
  }


  private void buildModuleFromFieldInput( Composite parent ) {

    Control top = this.isEndpointFromField;

    /*
    // module is defined in a field
    Label isModuleFromFieldLabel = new LabelBuilder( parent, this.props )
      .setText( "Module is defined in a field?" )
      .setTop( top )
      .setWidth( 170 )
      .build();

    this.isModuleFromField = new CheckBoxBuilder( parent, this.props )
      .setTop( top )
      .setLeft( isModuleFromFieldLabel )
      .build();

    this.isModuleFromField.addSelectionListener( new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        super.widgetSelected( selectionEvent );
        toggleIsModuleFromField();
      }
    } );
    */


  }

  private void buildEndpointFromFieldInput( Composite parent ) {

    Label endpointIsFieldLabel = new LabelBuilder( parent, this.props )
      .setText( "Endpoint is defined in a field?" )
      .setTop( this.httpMethod )
      .setWidth( 170 )
      .build();

    this.isEndpointFromField = new CheckBoxBuilder( parent, this.props )
      .setTop( this.httpMethod )
      .setLeft( endpointIsFieldLabel )
      .build();

    this.isEndpointFromField.addSelectionListener( new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        super.widgetSelected( selectionEvent );
        toggleIsEndpointFromField();
      }
    } );

    // module name field
    Label moduleNameFieldLabel = new LabelBuilder( parent, props )
      .setText( "Module name from field" )
      .setTop( this.isEndpointFromField )
      .setWidth( 170 )
      .build();

    this.moduleNameField = new ComboVarBuilder( parent, props, transMeta )
      .addAllItems( getFieldNames() )
      .setTop( this.isEndpointFromField )
      .setLeft( moduleNameFieldLabel )
      .setWidth( 200 )
      .build();

    this.moduleNameField.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );

    Label endpointFieldLabel = new LabelBuilder( parent, this.props )
      .setText( "Endpoint path from field" )
      .setTop( this.moduleNameField )
      .setWidth( 170 )
      .build();

    this.endpointPathField = new ComboVarBuilder( parent, this.props, this.transMeta )
      .addAllItems( getFieldNames() )
      .setTop( this.moduleNameField )
      .setLeft( endpointFieldLabel )
      .setWidth( 200 )
      .setEnabled( false )
      .build();

    this.endpointPathField.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );

    Label endpointTypeFieldLabel = new LabelBuilder( parent, this.props )
      .setText( "Http method from field" )
      .setTop( this.endpointPathField )
      .setWidth( 170 )
      .build();

    this.httpMethodField = new ComboVarBuilder( parent, this.props, this.transMeta )
      .addAllItems( getFieldNames() )
      .setTop( this.endpointPathField )
      .setLeft( endpointTypeFieldLabel )
      .setWidth( 200 )
      .build();

    this.httpMethodField.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );
  }

  /*
  private void toggleIsModuleFromField() {
    boolean isEnabled = isModuleFromField.getSelection();
  }
  */

  private void toggleIsEndpointFromField() {
    boolean isEnabled = isEndpointFromField.getSelection();
    this.moduleName.setEnabled( !isEnabled );
    this.endpointPath.setEnabled( !isEnabled );
    this.httpMethod.setEnabled( !isEnabled );
    this.moduleNameField.setEnabled( isEnabled );
    this.endpointPathField.setEnabled( isEnabled );
    this.httpMethodField.setEnabled( isEnabled );
  }

  //endregion

  //region Output Fields Group

  private void buildOutputFieldGroup( Composite parent ) {

    // group
    this.outputFieldsGroup = new GroupBuilder( parent, this.props )
      .setText( "Output Fields" )
      .setTop( this.endpointGroup )
      .build();

    // widgets
    buildResultFieldInput( this.outputFieldsGroup );
    buildStatusCodeFieldInput( this.outputFieldsGroup );
    buildResponseTimeFieldInput( this.outputFieldsGroup );
  }

  private void buildResultFieldInput( Composite parent ) {

    // label
    Label resultFieldLabel = new LabelBuilder( parent, this.props )
      .setText( "Result into field" )
      .setWidth( 170 )
      .build();

    // result field input
    this.resultField = new TextVarBuilder( parent, this.props, this.transMeta )
      .setLeft( resultFieldLabel )
      .setWidth( 200 )
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
      .setText( "Status code into field" )
      .setTop( this.resultField )
      .setWidth( 170 )
      .build();

    // status code field input
    this.statusCodeField = new TextVarBuilder( parent, this.props, this.transMeta )
      .setTop( this.resultField )
      .setLeft( statusCodeLabel )
      .setWidth( 200 )
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
      .setText( "Response time into field" )
      .setTop( this.statusCodeField )
      .setWidth( 170 )
      .build();

    // response time field input
    this.responseTimeField = new TextVarBuilder( parent, this.props, this.transMeta )
      .setTop( this.statusCodeField )
      .setLeft( responseTimeLabel )
      .setWidth( 200 )
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
    //buildModuleGroup( this.shell );
    buildEnpointGroup( this.shell );
    buildOutputFieldGroup( this.shell );

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

    toggleIsEndpointFromField();

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
    isBypassingAuthentication.setSelection( meta.isBypassingAuthentication() );

    /*
    isModuleFromField.setSelection( meta.isModuleFromField() );
    if ( isModuleFromField.getSelection() ) {
    } else {
    }
    */

    isEndpointFromField.setSelection( meta.isEndpointFromField() );
    if ( isEndpointFromField.getSelection() ) {
      moduleName.setText( "" );
      endpointPath.setText( "" );
      httpMethod.setText( "" );
      moduleNameField.setText( meta.getModuleName() );
      endpointPathField.setText( meta.getEndpointPath() );
      httpMethodField.setText( meta.getHttpMethod() );
    } else {
      moduleName.setText( meta.getModuleName() );
      endpointPath.setText( meta.getEndpointPath() );
      httpMethod.setText( meta.getHttpMethod() );
      moduleNameField.setText( "" );
      endpointPathField.setText( "" );
      httpMethodField.setText( "" );
    }

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
    meta.setBypassingAuthentication( isBypassingAuthentication.getSelection() );

    meta.setEndpointFromField( isEndpointFromField.getSelection() );
    if ( isEndpointFromField.getSelection() ) {
      meta.setModuleName( moduleNameField.getText() );
      meta.setEndpointPath( endpointPathField.getText() );
      meta.setHttpMethod( httpMethodField.getText() );
    } else {
      meta.setModuleName( moduleName.getText() );
      meta.setEndpointPath( endpointPath.getText() );
      meta.setHttpMethod( httpMethod.getText() );
    }

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
