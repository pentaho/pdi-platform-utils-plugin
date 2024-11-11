/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.di.baserver.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.baserver.utils.widgets.ButtonBuilder;
import org.pentaho.di.baserver.utils.widgets.ImageBuilder;
import org.pentaho.di.baserver.utils.widgets.SeparatorBuilder;
import org.pentaho.di.baserver.utils.widgets.fields.Field;
import org.pentaho.di.baserver.utils.widgets.fields.TextBoxFieldBuilder;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import java.util.ArrayList;

public abstract class BAServerCommonDialog<T extends BaseStepMeta> extends BaseStepDialog
    implements StepDialogInterface {
  public static final int LEFT_PLACEMENT = 0;
  public static final int RIGHT_PLACEMENT = 100;
  public static final int LARGE_MARGIN = 15;
  public static final int MEDIUM_MARGIN = 10;
  public static final int SMALL_MARGIN = 5;
  public static final int FIELD_WIDTH = 350;
  public static final int BOTTOM_BUTTON_WIDTH = 80;
  protected static Class<?> PKG = BAServerCommonDialog.class; // for i18n purposes, needed by Translator2!!

  protected final ModifyListener changeListener = new ModifyListener() {
    @Override public void modifyText( ModifyEvent modifyEvent ) {
      processInputChange();
    }
  };
  protected final SelectionAdapter selectionListener = new SelectionAdapter() {
    @Override public void widgetSelected( SelectionEvent selectionEvent ) {
      super.widgetSelected( selectionEvent );
      processInputChange();
    }
  };

  private Text stepName;
  private T metaInfo;
  private Button wOK;

  public BAServerCommonDialog( Shell parent, T baseStepMeta, TransMeta transMeta, String stepname ) {
    super( parent, baseStepMeta, transMeta, stepname );

    this.metaInfo = baseStepMeta;
  }

  @Override
  public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    // create shell
    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN );
    shell.setText( BaseMessages.getString( PKG, getTitleKey() ) );

    changed = metaInfo.hasChanged();

    // create form layout
    FormLayout formLayout = new FormLayout();
    formLayout.marginHeight = LARGE_MARGIN;
    formLayout.marginWidth = LARGE_MARGIN;
    shell.setLayout( formLayout );
    shell.setMinimumSize( getMinimumWidth(), getMinimumHeight() );
    shell.setSize( getMinimumWidth(), getMinimumHeight() );

    props.setLook( shell );
    setShellImage( shell, (StepMetaInterface) metaInfo );

    final Control top = buildStepNameInput( this.shell );
    final Composite container = new Composite( shell, SWT.NONE );
    container.setLayout( new FormLayout() );
    props.setLook( container );

    buildContent( container );

    Composite buttons = createButtons();

    final Label bottomSeparator = new SeparatorBuilder( shell, props )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
    ( (FormData) bottomSeparator.getLayoutData() ).bottom = new FormAttachment( buttons, -LARGE_MARGIN );

    FormData containerLD = new FormData();
    containerLD.top = new FormAttachment( top, LARGE_MARGIN );
    containerLD.bottom = new FormAttachment( bottomSeparator, -LARGE_MARGIN );
    containerLD.left = new FormAttachment( LEFT_PLACEMENT );
    containerLD.right = new FormAttachment( RIGHT_PLACEMENT );
    container.setLayoutData( containerLD );

    stepName.addModifyListener( changeListener );

    // listener to detect X or something that kills this window
    ShellListener lsShell = new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    };
    shell.addShellListener( lsShell );

    // load information (based on previous usage)
    loadData( metaInfo );
    metaInfo.setChanged( changed );

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

  private Composite createButtons() {
    final Composite container = new Composite( shell, SWT.NONE );
    container.setLayout( new FormLayout() );
    FormData layoutData = new FormData();
    layoutData.bottom = new FormAttachment( 100 );
    layoutData.left = new FormAttachment( LEFT_PLACEMENT );
    layoutData.right = new FormAttachment( RIGHT_PLACEMENT );
    container.setLayoutData( layoutData );
    props.setLook( container );

    // buttons

    Button wCancel = new ButtonBuilder( container, props )
        .setLabelText( BaseMessages.getString( PKG, "System.Button.Cancel" ) )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
    wCancel.addSelectionListener( new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        super.widgetSelected( selectionEvent );
        cancel();
      }
    } );

    wOK = new ButtonBuilder( container, props )
        .setLabelText( BaseMessages.getString( PKG, "System.Button.OK" ) )
        .setWidth( BOTTOM_BUTTON_WIDTH )
        .build();
    ( (FormData) wOK.getLayoutData() ).right = new FormAttachment( wCancel, -Const.MARGIN, SWT.LEFT );
    ( (FormData) wCancel.getLayoutData() ).width = BOTTOM_BUTTON_WIDTH;
    wOK.addSelectionListener( new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        super.widgetSelected( selectionEvent );
        ok();
      }
    } );

    return container;
  }

  public T getMetaInfo() {
    return metaInfo;
  }

  protected String[] getFieldNames() {
    StepMeta stepMeta = transMeta.findStep( stepname );
    if ( stepMeta != null ) {
      try {
        // get field names from setTop steps
        RowMetaInterface row = transMeta.getPrevStepFields( stepMeta );
        java.util.List<String> entries = new ArrayList<String>();
        for ( int i = 0; i < row.size(); i++ ) {
          entries.add( row.getValueMeta( i ).getName() );
        }
        String[] fieldNames = entries.toArray( new String[ entries.size() ] );

        // sort field names and add them to the combo box
        Const.sortStrings( fieldNames );
        return fieldNames;
      } catch ( KettleException e ) {
        logError( BaseMessages.getString( PKG, "System.Dialog.GetFieldsFailed.Message" ) );
      }
    }
    return new String[ 0 ];
  }

  protected void loadData( T metaInfo ) {
    stepName.setText( stepname );
  }

  protected void saveData( T meta ) {
    // save step name
    stepname = stepName.getText();
  }

  private void processInputChange() {
    this.metaInfo.setChanged();

    wOK.setEnabled( isValid() );
  }

  protected boolean isValid() {
    return !Const.isEmpty( stepName.getText() );
  }

  private void ok() {
    // keep information for next time
    saveData( metaInfo );
    dispose();
  }

  private void cancel() {
    // fill return value
    stepname = null;
    metaInfo.setChanged( changed );
    dispose();
  }

  private Control buildStepNameInput( Composite parent ) {
    //label with textbox
    final Field<Text> field = new TextBoxFieldBuilder( parent, this.props )
        .setLabel( BaseMessages.getString( PKG, "BAServerUtils.Dialog.StepName" ) )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setWidth( FIELD_WIDTH )
        .build();
    stepName = field.getControl();
    stepName.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent modifyEvent ) {
        processInputChange();
      }
    } );

    // icon
    final Label icon = new ImageBuilder( parent, this.props )
        .setImage( getImage() )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
    ( (FormData) icon.getLayoutData() ).top = new FormAttachment( field, 0, SWT.CENTER );

    // separator
    return new SeparatorBuilder( parent, this.props )
        .setTop( field )
        .setTopMargin( LARGE_MARGIN )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
  }

  protected int getMinimumHeight() {
    return 500;
  }

  protected int getMinimumWidth() {
    return 600;
  }

  protected abstract String getTitleKey();

  protected abstract void buildContent( Composite parent );

  protected abstract Image getImage();
}
