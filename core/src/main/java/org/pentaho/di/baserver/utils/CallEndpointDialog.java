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

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.baserver.utils.widgets.TabFolderBuilder;
import org.pentaho.di.baserver.utils.widgets.callEndpointTabs.EndpointTab;
import org.pentaho.di.baserver.utils.widgets.callEndpointTabs.OutputFieldsTab;
import org.pentaho.di.baserver.utils.widgets.callEndpointTabs.ParametersTab;
import org.pentaho.di.baserver.utils.widgets.callEndpointTabs.ServerTab;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.ConstUI;
import org.pentaho.di.ui.util.SwtSvgImageUtil;

public class CallEndpointDialog extends BAServerCommonDialog<CallEndpointMeta> {

  private ServerTab serverTab;
  private EndpointTab endpointTab;
  private ParametersTab parametersTab;
  private OutputFieldsTab outputFieldsTab;

  public CallEndpointDialog( Shell parent, Object in, TransMeta transMeta, String name ) {
    super( parent, (CallEndpointMeta) in, transMeta, name );
  }

  protected boolean isValid() {
    return super.isValid()
        && serverTab.isValid() && endpointTab.isValid() && parametersTab.isValid() && outputFieldsTab.isValid();
  }

  @Override protected int getMinimumHeight() {
    if ( Const.isLinux() ) {
      return 600;
    } else {
      return 555;
    }
  }

  @Override protected int getMinimumWidth() {
    return 664;
  }

  @Override
  protected String getTitleKey() {
    return "CallEndpointDialog.DialogTitle";
  }

  //endregion

  public void buildContent( Composite parent ) {
    final CTabFolder tabFolder = new TabFolderBuilder( parent, props )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .setTopPlacement( 0 )
        .setBottomPlacement( 100 )
        .build();
    props.setLook( tabFolder, Props.WIDGET_STYLE_TAB );

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
        getMetaInfo(),
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
    tabFolder.setSelection( 0 );
  }

  @Override
  protected Image getImage() {
    return SwtSvgImageUtil
        .getImage( shell.getDisplay(), getClass().getClassLoader(), "icons/callendpoint.svg", ConstUI.ICON_SIZE,
            ConstUI.ICON_SIZE );
  }

  protected void loadData( CallEndpointMeta meta ) {
    super.loadData( meta );

    serverTab.loadData( meta );
    endpointTab.loadData( meta );
    parametersTab.loadData( meta );
    outputFieldsTab.loadData( meta );
  }

  protected void saveData( CallEndpointMeta meta ) {
    // save step name
    super.saveData( meta );

    serverTab.saveData( meta );
    endpointTab.saveData( meta );
    parametersTab.saveData( meta );
    outputFieldsTab.saveData( meta );
  }
}
