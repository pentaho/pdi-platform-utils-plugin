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

import org.apache.commons.vfs.FileObject;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.vfs.ui.VfsFileChooserDialog;

import java.net.MalformedURLException;

public class PentahoSolutionVfsFileChooserController {

  // region Properties
  public PentahoSolutionVfsFileChooserPanel getView() {
    return this.view;
  }
  private PentahoSolutionVfsFileChooserPanel view;
  // endregion


  public PentahoSolutionVfsFileChooserController( PentahoSolutionVfsFileChooserPanel view ) {
    this.view = view;

    this.registerEventHandlers();
  }

  // region Methods
  private void registerEventHandlers() {
    final PentahoSolutionVfsFileChooserController controller = this;

    this.getView().getConnectionButton().addSelectionListener( new SelectionListener() {

      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        controller.connect();
      }

      @Override public void widgetDefaultSelected( SelectionEvent selectionEvent ) {}
    } );

  }

  private FileObject getFileObject( String vfsFileUri ) throws KettleFileException {
    FileObject file;
    file = KettleVFS.getFileObject( vfsFileUri );
    return file;
  }

  /***
   * Shows a message box to the user
   * @param message
   * @param shell
   */
  private void showMessage( String message, Shell shell) {
    MessageBox box = new MessageBox( shell );
    box.setText( "BOX TEXT" ); //$NON-NLS-1$
    box.setMessage( message );
    box.open();
    return;
  }

  /***
   * Connects to the Pentaho repository specified by the information of the vfs panel
   */
  public void connect() {
    try {
      String connectionString = this.getView().getPentahoConnectionString();
      FileObject file = this.getFileObject( connectionString );
      VfsFileChooserDialog vfsFileChooserDialog = this.getView().getVfsFileChooserDialog();
      vfsFileChooserDialog.setSelectedFile( file );
      vfsFileChooserDialog.setRootFile( file );

    } catch ( MalformedURLException e ) {
      showMessage( "ERROR URL", this.getView().getShell() );
    } catch ( KettleFileException e ) {
      showMessage( "ERROR FILE", this.getView().getShell() );
    }
  }

  // endregion


}
