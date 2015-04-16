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

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.baserver.utils.widgets.TabFolderBuilder;
import org.pentaho.di.baserver.utils.widgets.callEndpointTabs.EndpointTab;
import org.pentaho.di.baserver.utils.widgets.callEndpointTabs.OutputFieldsTab;
import org.pentaho.di.baserver.utils.widgets.callEndpointTabs.ParametersTab;
import org.pentaho.di.baserver.utils.widgets.callEndpointTabs.ServerTab;
import org.pentaho.di.core.Props;
import org.pentaho.di.trans.TransMeta;

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
    return 555;
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
    tabFolder.setSelection(0);
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
