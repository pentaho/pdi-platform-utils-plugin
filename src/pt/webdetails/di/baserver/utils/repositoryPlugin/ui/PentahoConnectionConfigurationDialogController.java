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

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.persist.MetaStoreFactory;
import org.pentaho.metastore.util.PentahoDefaults;
import pt.webdetails.di.baserver.utils.repositoryPlugin.PentahoConnectionConfiguration;

public class PentahoConnectionConfigurationDialogController {

  private static MetaStoreFactory<PentahoConnectionConfiguration> metaStoreFactory =
    new MetaStoreFactory<PentahoConnectionConfiguration>( PentahoConnectionConfiguration.class,
      Spoon.getInstance().getMetaStore(), PentahoDefaults.NAMESPACE );

  // region Properties
  private PentahoConnectionConfigurationDialog getDialog() {
    return this.dialog;
  }
  private PentahoConnectionConfigurationDialog dialog;
  // endregion

  public PentahoConnectionConfigurationDialogController( PentahoConnectionConfigurationDialog dialog ) {
    this.dialog = dialog;

    final PentahoConnectionConfigurationDialogController controller = this;
    dialog.getOkButton().addSelectionListener( new SelectionListener() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        controller.okClicked();
      }

      @Override public void widgetDefaultSelected( SelectionEvent selectionEvent ) {

      }
    } );
    dialog.getCancelButton().addSelectionListener( new SelectionListener() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        controller.cancelClicked();
      }

      @Override public void widgetDefaultSelected( SelectionEvent selectionEvent ) {

      }
    } );
  }

  public void okClicked() {
    PentahoConnectionConfiguration originalConfig = this.getDialog().getOriginalConfiguration();
    PentahoConnectionConfiguration editedConfig = this.getDialog().getEditedConfiguration();

    boolean newConfiguration = !this.configurationExists( editedConfig.getName() );
    // if nothing has changed and not a new configuration do nothing
    if ( editedConfig.equals( originalConfig ) && !newConfiguration ) {
      return;
    }

    this.updateConfiguration( editedConfig, originalConfig.getName() );

    this.getDialog().dispose();
  }

  public void cancelClicked() {
    this.getDialog().dispose();
  }

  private void updateConfiguration( PentahoConnectionConfiguration updatedConfiguration, String originalName ) {
    // remove original configuration if it exits
    try {
      this.metaStoreFactory.deleteElement( originalName );
    } catch ( MetaStoreException e ) { }

    try {
      this.metaStoreFactory.saveElement( updatedConfiguration );
    } catch ( MetaStoreException e ) {
      //TODO i18n
      ErrorDialog errorDialog = new ErrorDialog( this.getDialog().getParent(), "Error Pentaho Connection Save" , "Error saving Pentaho Connection", e );
    }
  }

  private boolean configurationExists( String configurationNameId ) {
    try {
      return this.metaStoreFactory.getElementNames().contains( configurationNameId );
    } catch ( MetaStoreException e ) {
      return false;
    }
  }
}
