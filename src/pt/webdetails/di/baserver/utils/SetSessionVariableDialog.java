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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
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
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marco Vala
 */
public class SetSessionVariableDialog extends BaseStepDialog implements StepDialogInterface {
  private static Class<?> PKG = SetSessionVariableMeta.class; // for i18n purposes, needed by Translator2!!

  private SetSessionVariableMeta metaInfo;
  private Text wStepName;
  private Button wApplyFormatting;
  private TableView wFields;
  private ColumnInfo cFieldNames;

  public SetSessionVariableDialog( Shell parent, Object in, TransMeta transMeta, String name ) {
    super( parent, (BaseStepMeta) in, transMeta, name );
    metaInfo = (SetSessionVariableMeta) in;
  }

  public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    // shell
    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN );
    props.setLook( shell );
    setShellImage( shell, metaInfo );
    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;
    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "SetSessionVariableDialog.DialogTitle" ) );
    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    // listener to detect changes
    ModifyListener lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        updateOKButtonStatus();
        metaInfo.setChanged( changed );
      }
    };

    // step name text box
    Label wlStepName = new Label( shell, SWT.RIGHT );
    wlStepName.setText( BaseMessages.getString( PKG, "System.Label.StepName" ) );
    props.setLook( wlStepName );
    FormData fdlStepName = new FormData();
    fdlStepName.left = new FormAttachment( 0, 0 );
    fdlStepName.right = new FormAttachment( middle, -margin );
    fdlStepName.top = new FormAttachment( 0, margin );
    wlStepName.setLayoutData( fdlStepName );
    wStepName = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wStepName );
    FormData fdStepName = new FormData();
    fdStepName.left = new FormAttachment( middle, 0 );
    fdStepName.top = new FormAttachment( 0, margin );
    fdStepName.right = new FormAttachment( 100, 0 );
    wStepName.setLayoutData( fdStepName );
    wStepName.addModifyListener( lsMod );

    // apply formatting checkbox
    Label wlFormat = new Label( shell, SWT.RIGHT );
    wlFormat.setText( BaseMessages.getString( PKG, "SetSessionVariableDialog.Label.ApplyFormatting" ) );
    wlFormat.setToolTipText( BaseMessages.getString( PKG, "SetSessionVariableDialog.Label.ApplyFormatting.Tooltip" ) );
    props.setLook( wlFormat );
    FormData fdlFormat = new FormData();
    fdlFormat.left = new FormAttachment( 0, 0 );
    fdlFormat.right = new FormAttachment( middle, -margin );
    fdlFormat.top = new FormAttachment( wStepName, margin );
    wlFormat.setLayoutData( fdlFormat );
    wApplyFormatting = new Button( shell, SWT.CHECK );
    wApplyFormatting
      .setToolTipText( BaseMessages.getString( PKG, "SetSessionVariableDialog.Label.ApplyFormatting.Tooltip" ) );
    props.setLook( wApplyFormatting );
    FormData fdFormat = new FormData();
    fdFormat.left = new FormAttachment( middle, 0 );
    fdFormat.top = new FormAttachment( wStepName, margin );
    wApplyFormatting.setLayoutData( fdFormat );

    // fields
    Label wlFields = new Label( shell, SWT.NONE );
    wlFields.setText( BaseMessages.getString( PKG, "SetSessionVariableDialog.Label.Fields" ) );
    props.setLook( wlFields );
    FormData fdlFields = new FormData();
    fdlFields.left = new FormAttachment( 0, 0 );
    fdlFields.top = new FormAttachment( wApplyFormatting, margin );
    wlFields.setLayoutData( fdlFields );
    ColumnInfo[] columns = new ColumnInfo[] {
      // field name
      new ColumnInfo(
        BaseMessages.getString( PKG, "SetSessionVariableDialog.Column.FieldName" ),
        ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, false ),
      // variable name
      new ColumnInfo(
        BaseMessages.getString( PKG, "SetSessionVariableDialog.Column.VariableName" ),
        ColumnInfo.COLUMN_TYPE_TEXT, false ),
      // default value
      new ColumnInfo(
        BaseMessages.getString( PKG, "SetSessionVariableDialog.Column.DefaultValue" ),
        ColumnInfo.COLUMN_TYPE_TEXT, false )
    };
    cFieldNames = columns[ 0 ];
    columns[ 1 ].setUsingVariables( true );
    columns[ 1 ].setToolTip( BaseMessages.getString( PKG, "SetSessionVariableDialog.Column.VariableName.Tooltip" ) );
    columns[ 2 ].setUsingVariables( true );
    columns[ 2 ].setToolTip( BaseMessages.getString( PKG, "SetSessionVariableDialog.Column.DefaultValue.Tooltip" ) );
    wFields = new TableView( transMeta, shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, columns,
      metaInfo.getFieldName().length, lsMod, props );
    FormData fdFields = new FormData();
    fdFields.left = new FormAttachment( 0, 0 );
    fdFields.top = new FormAttachment( wlFields, margin );
    fdFields.right = new FormAttachment( 100, 0 );
    fdFields.bottom = new FormAttachment( 100, -50 );
    wFields.setLayoutData( fdFields );

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
    setButtonPositions( new Button[] { wOK, wCancel }, margin, wFields );

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
        cFieldNames.setComboValues( fieldNames );
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

  private void loadData( SetSessionVariableMeta meta ) {
    // load step name
    wStepName.setText( stepname );

    // load if is using formatting or not
    wApplyFormatting.setSelection( meta.isUsingFormatting() );

    // load fields
    for ( int i = 0; i < meta.getFieldName().length; i++ ) {
      TableItem item = wFields.table.getItem( i );
      int index = 0;
      item.setText( ++index, Const.NVL( meta.getFieldName()[ i ], "" ) );
      item.setText( ++index, Const.NVL( meta.getVariableName()[ i ], "" ) );
      item.setText( ++index, Const.NVL( meta.getDefaultValue()[ i ], "" ) );
    }
    wFields.setRowNums();
    wFields.optWidth( true );
  }

  private void saveData( SetSessionVariableMeta meta ) {
    // save step name
    stepname = wStepName.getText();

    // save if is using formatting or not
    meta.setUseFormatting( wApplyFormatting.getSelection() );

    // save fields
    int count = wFields.nrNonEmpty();
    meta.allocate( count );
    for ( int i = 0; i < count; i++ ) {
      TableItem item = wFields.getNonEmpty( i );
      int index = 0;
      meta.getFieldName()[ i ] = item.getText( ++index );
      meta.getVariableName()[ i ] = item.getText( ++index );
      meta.getDefaultValue()[ i ] = item.getText( ++index );
    }
  }
}
