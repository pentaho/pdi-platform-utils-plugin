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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
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
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import pt.webdetails.di.baserver.utils.widgets.CheckBoxBuilder;
import pt.webdetails.di.baserver.utils.widgets.GroupBuilder;
import pt.webdetails.di.baserver.utils.widgets.TableViewBuilder;
import pt.webdetails.di.baserver.utils.widgets.TextBoxBuilder;
import pt.webdetails.di.baserver.utils.widgets.TextVarBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marco Vala
 */
public class CallEndpointDialog extends BaseStepDialog implements StepDialogInterface {
  private static Class<?> PKG = CallEndpointMeta.class; // for i18n purposes, needed by Translator2!!

  private CallEndpointMeta metaInfo;
  private Text wStepName;
  private TextVar wServerUrl;
  private TextVar wUsername;
  private TextVar wPassword;
  private Button wBypassAuthCheck;

  private TextVar wModule;
  private Button wModuleIsField;
  private TextVar wService;
  private Button wServiceIsField;

  private TextVar wResultField;
  private TextVar wStatusCodeField;
  private TextVar wResponseTimeField;

  private TableView wParameters;
  private ColumnInfo cFieldName;


  public CallEndpointDialog( Shell parent, Object in, TransMeta transMeta, String name ) {
    super( parent, (BaseStepMeta) in, transMeta, name );
    metaInfo = (CallEndpointMeta) in;
  }


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


    // listener to detect changes
    ModifyListener lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        updateOKButtonStatus();
        metaInfo.setChanged( changed );
      }
    };


    /*
    final Text wResultField = new TextBoxBuilder( props, shell )
      .setWidth( 200 )
      .setLabelText( "STATUS" )
      .setLabelWidth( 80 )
      .build();

    SelectionListener lsTest = new SelectionListener() {

      @Override
      public void widgetSelected( SelectionEvent selectionEvent ) {
        // TEST
        wResultField.setText( "" );
        try {
          // http://localhost:8080/pentaho/plugin/repositorySynchronizer/api/version

          String module = wModule.getText();
          if ( module.equals( "platform" ) ) {
            module = "";
          } else {
            module = "/plugin/" + module;
          }

          String call = wServerUrl.getText() + "/pentaho" + module + "/api/" + wService.getText();
          int status = HttpConnectionHelper.callHttp( call, wUsername.getText(), wPassword.getText() );

          wResultField.setText( String.valueOf( status ) );
        } catch ( IOException ex ) {
          logError( ex.toString() );
        }
      }

      @Override
      public void widgetDefaultSelected( SelectionEvent selectionEvent ) {
      }
    };

    final Button wTest = new ButtonBuilder( props, shell )
      .setLabelText( "TEST" )
      .setLeft( wResultField )
      .onButtonPressed( lsTest )
      .build();
    */


    // WIDGETS

    // step name
    this.wStepName = new TextBoxBuilder( props, shell )
      .setWidth( 200 )
      .setLabelText( "Step name" )
      .setLabelWidth( 80 )
      .setModifyListener( lsMod )
      .build();

    // BA server info group
    Group serverInfo = new GroupBuilder( props, shell )
      .setLabelText( "BA Server" )
      .setTop( this.wStepName )
      .build();

    // server url
    this.wServerUrl = new TextVarBuilder( props, serverInfo, transMeta )
      .setWidth( 400 )
      .setLabelText( "Server URL" )
      .setLabelWidth( 80 )
      .setModifyListener( lsMod )
      .build();

    // username
    this.wUsername = new TextVarBuilder( props, serverInfo, transMeta )
      .setWidth( 200 )
      .setLabelText( "Username" )
      .setLabelWidth( 80 )
      .setTop( this.wServerUrl )
      .setModifyListener( lsMod )
      .build();

    // password
    this.wPassword = new TextVarBuilder( props, serverInfo, transMeta )
      .setWidth( 200 )
      .setLabelText( "Password" )
      .setLabelWidth( 80 )
      .setEchoChar( '*' )
      .setTop( this.wUsername )
      .setModifyListener( lsMod )
      .build();

    // bypass authentication
    this.wBypassAuthCheck = new CheckBoxBuilder( props, serverInfo )
      .setLabelText( "Bypass authentication on local execution " )
      .setLabelWidth( 260 )
      .setTop( this.wPassword )
      .build();

    // endpoint info group
    Group endpointInfo = new GroupBuilder( props, shell )
      .setLabelText( "Endpoint" )
      .setTop( serverInfo )
      .build();

    // module
    this.wModule = new TextVarBuilder( props, endpointInfo, transMeta )
      .setWidth( 200 )
      .setLabelText( "Module" )
      .setLabelWidth( 80 )
      .setModifyListener( lsMod )
      .build();

    // module is field
    this.wModuleIsField = new CheckBoxBuilder( props, endpointInfo )
      .setLabelText( "Module is a field" )
      .setLabelWidth( 140 )
      .setLeft( this.wModule )
      .build();

    // service
    this.wService = new TextVarBuilder( props, endpointInfo, transMeta )
      .setWidth( 200 )
      .setLabelText( "Service" )
      .setLabelWidth( 80 )
      .setTop( this.wModule )
      .setModifyListener( lsMod )
      .build();

    // service is field
    this.wServiceIsField = new CheckBoxBuilder( props, endpointInfo )
      .setLabelText( "Service is a field" )
      .setLabelWidth( 140 )
      .setTop( this.wModule )
      .setLeft( this.wService )
      .build();

    // output fields group
    Group outputFields = new GroupBuilder( props, shell )
      .setLabelText( "Output Fields" )
      .setTop( endpointInfo )
      .build();

    // wResultField
    this.wResultField = new TextVarBuilder( props, outputFields, transMeta )
      .setWidth( 200 )
      .setLabelText( "Result field" )
      .setLabelWidth( 160 )
      .setModifyListener( lsMod )
      .build();

    // status code
    this.wStatusCodeField = new TextVarBuilder( props, outputFields, transMeta )
      .setWidth( 200 )
      .setLabelText( "Status code field" )
      .setLabelWidth( 160 )
      .setTop( this.wResultField )
      .setModifyListener( lsMod )
      .build();

    // response time
    this.wResponseTimeField = new TextVarBuilder( props, outputFields, transMeta )
      .setWidth( 200 )
      .setLabelText( "Response time (ms) field" )
      .setLabelWidth( 160 )
      .setTop( this.wStatusCodeField )
      .setModifyListener( lsMod )
      .build();

    this.cFieldName = new ColumnInfo( BaseMessages.getString( PKG, "CallEndpointDialog.Column.FieldName" ),
      ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, false );

    ColumnInfo cParameter = new ColumnInfo( BaseMessages.getString( PKG, "CallEndpointDialog.Column.Parameter" ),
      ColumnInfo.COLUMN_TYPE_TEXT, false );
    cParameter.setUsingVariables( true );

    ColumnInfo cDefaultValue = new ColumnInfo( BaseMessages.getString( PKG, "CallEndpointDialog.Column.DefaultValue" ),
      ColumnInfo.COLUMN_TYPE_TEXT, false );
    cDefaultValue.setUsingVariables( true );
    cDefaultValue.setToolTip( BaseMessages.getString( PKG, "SetSessionVariableDialog.Column.DefaultValue.Tooltip" ) );

    this.wParameters = new TableViewBuilder( props, shell )
      .setTop( outputFields )
      .setLabelText( "Parameters:" )
      .setModifyListener( lsMod )
      .setVariableSpace( transMeta )
      .addColumnInfo( cFieldName )
      .addColumnInfo( cParameter )
      .addColumnInfo( cDefaultValue )
      .setRowsCount( metaInfo.getFieldName().length )
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
    setButtonPositions( new Button[] { wOK, wCancel }, Const.MARGIN, this.wParameters );

    // listener to add default action
    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };
    wStepName.addSelectionListener( lsDef );

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
    wStepName.selectAll();
    wStepName.setFocus();

    // open shell
    shell.open();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return stepname;
  }

  private void updateOKButtonStatus() {
    wOK.setEnabled( !Const.isEmpty( wStepName.getText() ) );
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

  private void loadData( CallEndpointMeta meta ) {
    // load step name
    wStepName.setText( stepname );

    wServerUrl.setText( meta.getServerURL() );
    wUsername.setText( meta.getUsername() );
    wPassword.setText( meta.getPassword() );
    wBypassAuthCheck.setSelection( meta.isBypassingAuthCheck() );
    wModule.setText( meta.getModule() );
    wModuleIsField.setSelection( meta.isModuleField() );
    wService.setText( meta.getService() );
    wServiceIsField.setSelection( meta.isServiceField() );
    wResultField.setText( meta.getResultField() );
    wStatusCodeField.setText( meta.getStatusCodeField() );
    wResponseTimeField.setText( meta.getResponseTimeField() );

    // load fields
    for ( int i = 0; i < meta.getFieldName().length; i++ ) {
      TableItem item = wParameters.table.getItem( i );
      int index = 0;
      item.setText( ++index, Const.NVL( meta.getFieldName()[ i ], "" ) );
      item.setText( ++index, Const.NVL( meta.getParameter()[ i ], "" ) );
      item.setText( ++index, Const.NVL( meta.getDefaultValue()[ i ], "" ) );
    }
    wParameters.setRowNums();
    wParameters.optWidth( true );
  }

  private void saveData( CallEndpointMeta meta ) {
    // save step name
    stepname = wStepName.getText();

    meta.setServerURL( wServerUrl.getText() );
    meta.setUsername( wUsername.getText() );
    meta.setPassword( wPassword.getText() );
    meta.setBypassAuthCheck( wBypassAuthCheck.getSelection() );
    meta.setModule( wModule.getText() );
    meta.setIsModuleField( wModuleIsField.getSelection() );
    meta.setService( wService.getText() );
    meta.setIsServiceField( wServiceIsField.getSelection() );
    meta.setResultField( wResultField.getText() );
    meta.setStatusCodeField( wStatusCodeField.getText() );
    meta.setResponseTimeField( wResponseTimeField.getText() );

    // save fields
    int count = wParameters.nrNonEmpty();
    meta.allocate( count );
    for ( int i = 0; i < count; i++ ) {
      TableItem item = wParameters.getNonEmpty( i );
      int index = 0;
      meta.getFieldName()[ i ] = item.getText( ++index );
      meta.getParameter()[ i ] = item.getText( ++index );
      meta.getDefaultValue()[ i ] = item.getText( ++index );
    }
  }
}
