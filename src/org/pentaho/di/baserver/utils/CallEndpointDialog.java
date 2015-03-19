/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License, version 2 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/gpl-2.0.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 *
 * Copyright 2006 - 2015 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.di.baserver.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.baserver.utils.widgets.ImageBuilder;
import org.pentaho.di.baserver.utils.widgets.SeparatorBuilder;
import org.pentaho.di.baserver.utils.widgets.TabFolderBuilder;
import org.pentaho.di.baserver.utils.widgets.callEndpointTabs.EndpointTab;
import org.pentaho.di.baserver.utils.widgets.callEndpointTabs.OutputFieldsTab;
import org.pentaho.di.baserver.utils.widgets.callEndpointTabs.ParametersTab;
import org.pentaho.di.baserver.utils.widgets.callEndpointTabs.ServerTab;
import org.pentaho.di.baserver.utils.widgets.callEndpointTabs.Tab;
import org.pentaho.di.baserver.utils.widgets.fields.Field;
import org.pentaho.di.baserver.utils.widgets.fields.TextBoxFieldBuilder;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import java.io.InputStream;

public class CallEndpointDialog extends BaseStepDialog implements StepDialogInterface {
  private static Class<?> PKG = CallEndpointMeta.class; // for i18n purposes, needed by Translator2!!

  public static final int LEFT_PLACEMENT = 0;
  public static final int RIGHT_PLACEMENT = 100;

  private CallEndpointMeta metaInfo;

  private Text stepName;

  private CTabFolder tabFolder;

  private ModifyListener changeListener = new ModifyListener() {
    @Override public void modifyText( ModifyEvent modifyEvent ) {
      processInputChange();
    }
  };
  private final SelectionAdapter selectionListener = new SelectionAdapter() {
    @Override public void widgetSelected( SelectionEvent selectionEvent ) {
      super.widgetSelected( selectionEvent );
      processInputChange();
    }
  };
  private ServerTab serverTab;
  private EndpointTab endpointTab;
  private ParametersTab parametersTab;
  private OutputFieldsTab outputFieldsTab;

  public CallEndpointDialog( Shell parent, Object in, TransMeta transMeta, String name ) {
    super( parent, (BaseStepMeta) in, transMeta, name );
    this.metaInfo = (CallEndpointMeta) in;
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

  private Control buildStepNameInput( Composite parent ) {
    //label with textbox
    final Field<Text> field = new TextBoxFieldBuilder( parent, this.props )
        .setLabel( BaseMessages.getString( PKG, "CallEndpointDialog.StepName" ) )
        .addModifyListener( changeListener )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
    stepName = field.getControl();

    // icon

    try {
      final PluginInterface plugin = PluginRegistry.getInstance()
          .getPlugin( StepPluginType.class, CallEndpointMeta.class.getAnnotation( Step.class ).id() );

      ClassLoader classLoader = PluginRegistry.getInstance().getClassLoader( plugin );
      InputStream inputStream = classLoader.getResourceAsStream( plugin.getImageFile() );

      final Label icon = new ImageBuilder( parent, this.props )
          .setImage( new Image( parent.getDisplay(), inputStream ) )
          .setRightPlacement( RIGHT_PLACEMENT )
          .build();
      ( (FormData) field.getLayoutData() ).right = new FormAttachment( icon );
    } catch ( KettlePluginException e ) {
      // do nothing
    }

    // separator
    return new SeparatorBuilder( parent, this.props )
        .setTop( field )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
  }

  //endregion

  private void buildTabs( Composite parent, Control top ) {
    tabFolder = new TabFolderBuilder( parent, props )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .setTop( top )
        .setBottomPlacement( 100 )
        .setBottomMargin( 30 )
        .build();

    serverTab = new ServerTab(
        tabFolder,
        props,
        transMeta,
        changeListener,
        selectionListener
    );
    endpointTab = new EndpointTab(
        tabFolder,
        props,
        transMeta,
        changeListener,
        selectionListener,
        stepname,
        log,
        serverTab
    );
    parametersTab = new ParametersTab(
        tabFolder,
        props,
        transMeta,
        metaInfo,
        changeListener,
        stepname,
        log
    );
    outputFieldsTab = new OutputFieldsTab(
        tabFolder,
        props,
        transMeta,
        changeListener
    );
    tabFolder.addSelectionListener( new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        super.widgetSelected( selectionEvent );
        ((Tab) tabFolder.getSelection().getControl()).refresh();
      }
    } );
    tabFolder.setSelection( 0 );
  }

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
    final Control top = buildStepNameInput( this.shell );
    buildTabs( this.shell, top );

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
    setButtonPositions( new Button[] { wOK, wCancel }, Const.MARGIN, null );

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

    serverTab.loadData( meta );
    endpointTab.loadData( meta );
    parametersTab.loadData( meta );
    outputFieldsTab.loadData( meta );
  }

  private void saveData( CallEndpointMeta meta ) {
    // save step name
    stepname = stepName.getText();

    serverTab.saveData( meta );
    endpointTab.saveData( meta );
    parametersTab.saveData( meta );
    outputFieldsTab.saveData( meta );
  }
}
