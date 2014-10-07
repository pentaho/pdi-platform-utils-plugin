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
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.TransPreviewFactory;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.dialog.EnterTextDialog;
import org.pentaho.di.ui.core.dialog.PreviewRowsDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.dialog.TransPreviewProgressDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

/**
 * @author Marco Vala
 */
public class GetSessionVariableDialog extends BaseStepDialog implements StepDialogInterface {
  private static Class<?> PKG = GetSessionVariableMeta.class; // for i18n purposes, needed by Translator2!!

  private GetSessionVariableMeta metaInfo;
  private Text wStepName;
  private TableView wFields;

  public GetSessionVariableDialog( Shell parent, Object in, TransMeta transMeta, String name ) {
    super( parent, (BaseStepMeta) in, transMeta, name );
    metaInfo = (GetSessionVariableMeta) in;
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
    shell.setText( BaseMessages.getString( PKG, "GetSessionVariableDialog.DialogTitle" ) );
    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    // listener to detect changes
    ModifyListener lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        updateOKButtonStatus();
        metaInfo.setChanged();
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

    // fields
    Label wlFields = new Label( shell, SWT.NONE );
    wlFields.setText( BaseMessages.getString( PKG, "GetSessionVariableDialog.Label.Fields" ) );
    props.setLook( wlFields );
    FormData fdlFields = new FormData();
    fdlFields.left = new FormAttachment( 0, 0 );
    fdlFields.top = new FormAttachment( wStepName, margin );
    wlFields.setLayoutData( fdlFields );
    ColumnInfo[] columns = new ColumnInfo[] {
      // field name
      new ColumnInfo(
        BaseMessages.getString( PKG, "GetSessionVariableDialog.Column.FieldName" ),
        ColumnInfo.COLUMN_TYPE_TEXT, false ),
      // variable name
      new ColumnInfo(
        BaseMessages.getString( PKG, "GetSessionVariableDialog.Column.VariableName" ),
        ColumnInfo.COLUMN_TYPE_TEXT, false ),
      // type
      new ColumnInfo(
        BaseMessages.getString( PKG, "System.Column.Type" ),
        ColumnInfo.COLUMN_TYPE_CCOMBO, ValueMeta.getTypes() ),
      // format
      new ColumnInfo(
        BaseMessages.getString( PKG, "System.Column.Format" ), ColumnInfo.COLUMN_TYPE_FORMAT, 3 ),
      // length
      new ColumnInfo(
        BaseMessages.getString( PKG, "System.Column.Length" ), ColumnInfo.COLUMN_TYPE_TEXT, false ),
      // precision
      new ColumnInfo(
        BaseMessages.getString( PKG, "System.Column.Precision" ), ColumnInfo.COLUMN_TYPE_TEXT, false ),
      // currency
      new ColumnInfo(
        BaseMessages.getString( PKG, "System.Column.Currency" ), ColumnInfo.COLUMN_TYPE_TEXT, false ),
      // decimal
      new ColumnInfo(
        BaseMessages.getString( PKG, "System.Column.Decimal" ), ColumnInfo.COLUMN_TYPE_TEXT, false ),
      // group
      new ColumnInfo(
        BaseMessages.getString( PKG, "System.Column.Group" ), ColumnInfo.COLUMN_TYPE_TEXT, false ),
      // trim type
      new ColumnInfo(
        BaseMessages.getString( PKG, "GetSessionVariableDialog.Column.TrimType" ),
        ColumnInfo.COLUMN_TYPE_CCOMBO, ValueMeta.getTrimTypeDescriptions() ),
      // default value
      new ColumnInfo(
        BaseMessages.getString( PKG, "GetSessionVariableDialog.Column.DefaultValue" ),
        ColumnInfo.COLUMN_TYPE_TEXT, false )
    };
    columns[ 1 ].setUsingVariables( true );
    columns[ 10 ].setUsingVariables( true );
    columns[ 10 ].setToolTip( BaseMessages.getString( PKG, "GetSessionVariableDialog.Column.DefaultValue.Tooltip" ) );
    wFields = new TableView( transMeta, shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, columns,
      metaInfo.getFieldName().length, lsMod, props );
    FormData fdFields = new FormData();
    fdFields.left = new FormAttachment( 0, 0 );
    fdFields.top = new FormAttachment( wlFields, margin );
    fdFields.right = new FormAttachment( 100, 0 );
    fdFields.bottom = new FormAttachment( 100, -50 );
    wFields.setLayoutData( fdFields );

    // buttons
    wOK = new Button( shell, SWT.PUSH );
    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    lsOK = new Listener() {
      public void handleEvent( Event e ) {
        ok();
      }
    };
    wOK.addListener( SWT.Selection, lsOK );
    wPreview = new Button( this.shell, 8 );
    wPreview.setText( BaseMessages.getString( PKG, "System.Button.Preview" ) );
    boolean isReceivingInput = transMeta.findNrPrevSteps( stepMeta ) > 0;
    wPreview.setEnabled( !isReceivingInput );
    lsPreview = new Listener() {
      public void handleEvent( Event e ) {
        preview();
      }
    };
    wPreview.addListener( 13, this.lsPreview );
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );
    lsCancel = new Listener() {
      public void handleEvent( Event e ) {
        cancel();
      }
    };
    wCancel.addListener( SWT.Selection, lsCancel );
    setButtonPositions( new Button[] { wOK, wPreview, wCancel }, margin, wFields );

    // listener to add default action
    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };
    wStepName.addSelectionListener( lsDef );

    // listener to detect X or something that kills this window
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    // fill information (based on setTop time)
    loadData( metaInfo );

    // set the shell size (based on setTop time)
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

  private void ok() {
    // keep information for next time
    saveData( metaInfo );
    dispose();
  }

  private void preview() {
    String stepName = wStepName.getText();
    GetSessionVariableMeta meta = new GetSessionVariableMeta();
    saveData( meta );
    TransMeta previewMeta = TransPreviewFactory.generatePreviewTransformation( transMeta, meta, stepName );

    // open progress dialog and run transformation
    int previewSize = 1;
    TransPreviewProgressDialog progressDialog =
      new TransPreviewProgressDialog( shell, previewMeta, new String[] { stepName }, new int[] { previewSize } );
    progressDialog.open();

    if ( !progressDialog.isCancelled() ) {
      Trans trans = progressDialog.getTrans();
      String loggingText = progressDialog.getLoggingText();

      // we have errors, show log
      if ( trans.getResult() != null && trans.getResult().getNrErrors() > 0 ) {
        EnterTextDialog etd =
          new EnterTextDialog( shell, BaseMessages.getString( PKG, "System.Dialog.PreviewError.Title" ),
            BaseMessages.getString(
              PKG, "System.Dialog.PreviewError.Message" ), loggingText, true );
        etd.setReadOnly();
        etd.open();
      }

      // show preview results
      PreviewRowsDialog prd = new PreviewRowsDialog( shell, transMeta, SWT.NONE, wStepName.getText(),
      progressDialog.getPreviewRowsMeta( stepName ), progressDialog.getPreviewRows( stepName ), loggingText );
      prd.open();
    }
  }

  private void cancel() {
    // fill return value
    stepname = null;
    dispose();
  }

  private void loadData( GetSessionVariableMeta meta ) {
    // load step name
    wStepName.setText( stepname );

    // load fields
    for ( int i = 0; i < meta.getFieldName().length; i++ ) {
      TableItem item = wFields.table.getItem( i );
      int index = 0;
      item.setText( ++index, Const.NVL( meta.getFieldName()[ i ], "" ) );
      item.setText( ++index, Const.NVL( meta.getVariableName()[ i ], "" ) );
      item.setText( ++index, ValueMeta.getTypeDesc( meta.getFieldType()[ i ] ) );
      item.setText( ++index, Const.NVL( meta.getFieldFormat()[ i ], "" ) );
      item.setText( ++index, meta.getFieldLength()[ i ] < 0 ? "" : ( "" + meta.getFieldLength()[ i ] ) );
      item.setText( ++index, meta.getFieldPrecision()[ i ] < 0 ? "" : ( "" + meta.getFieldPrecision()[ i ] ) );
      item.setText( ++index, Const.NVL( meta.getCurrency()[ i ], "" ) );
      item.setText( ++index, Const.NVL( meta.getDecimal()[ i ], "" ) );
      item.setText( ++index, Const.NVL( meta.getGroup()[ i ], "" ) );
      item.setText( ++index, ValueMeta.getTrimTypeDesc( meta.getTrimType()[ i ] ) );
      item.setText( ++index, Const.NVL( meta.getDefaultValue()[ i ], "" ) );
    }
    wFields.setRowNums();
    wFields.optWidth( true );
  }

  private void saveData( GetSessionVariableMeta meta ) {
    // save step name
    stepname = wStepName.getText();

    // save fields
    int count = wFields.nrNonEmpty();
    meta.allocate( count );
    for ( int i = 0; i < count; i++ ) {
      TableItem item = wFields.getNonEmpty( i );
      int index = 0;
      meta.getFieldName()[ i ] = item.getText( ++index );
      meta.getVariableName()[ i ] = item.getText( ++index );
      meta.getFieldType()[ i ] = ValueMeta.getType( item.getText( ++index ) );
      meta.getFieldFormat()[ i ] = item.getText( ++index );
      meta.getFieldLength()[ i ] = Const.toInt( item.getText( ++index ), -1 );
      meta.getFieldPrecision()[ i ] = Const.toInt( item.getText( ++index ), -1 );
      meta.getCurrency()[ i ] = item.getText( ++index );
      meta.getDecimal()[ i ] = item.getText( ++index );
      meta.getGroup()[ i ] = item.getText( ++index );
      meta.getTrimType()[ i ] = ValueMeta.getTrimTypeByDesc( item.getText( ++index ) );
      meta.getDefaultValue()[ i ] = item.getText( ++index );
    }
  }
}
