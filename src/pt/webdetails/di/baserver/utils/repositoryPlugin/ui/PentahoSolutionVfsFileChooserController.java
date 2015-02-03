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
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.vfs.ui.VfsFileChooserDialog;
import pt.webdetails.di.baserver.utils.repositoryPlugin.PentahoConnectionConfiguration;

import java.net.MalformedURLException;
import java.net.URL;

public class PentahoSolutionVfsFileChooserController {

  // region Properties
  public PentahoSolutionVfsFileChooserPanel getView() {
    return this.view;
  }
  protected PentahoSolutionVfsFileChooserController setView( PentahoSolutionVfsFileChooserPanel view ) {
    this.view = view;
    if ( this.view != null ) {
      final PentahoSolutionVfsFileChooserController controller = this;
      this.view.getConnectionButton().addSelectionListener( new SelectionListener() {

        @Override public void widgetSelected( SelectionEvent selectionEvent ) {
          controller.connect();
        }

        @Override public void widgetDefaultSelected( SelectionEvent selectionEvent ) {
        }
      } );

      this.view.getNewConnectionButton().addSelectionListener( new SelectionListener() {
        @Override public void widgetSelected( SelectionEvent selectionEvent ) {
          controller.newConnection();
        }

        @Override public void widgetDefaultSelected( SelectionEvent selectionEvent ) {

        }
      } );
    }
    return this;
  }
  private PentahoSolutionVfsFileChooserPanel view;
  // endregion

  public PentahoSolutionVfsFileChooserController( PentahoSolutionVfsFileChooserPanel view ) {
    this.setView( view );
  }

  // constructor for unit tests
  protected PentahoSolutionVfsFileChooserController() { }

  // region Methods
  private FileObject getFileObject( String vfsFileUri ) throws KettleFileException {
    FileObject file;
    file = KettleVFS.getFileObject( vfsFileUri );
    return file;
  }

  /***
   *
   * @return the file URI constructed from the dialog input
   */
  protected String getPentahoConnectionString( String vfsScheme, URL serverUrl, String username, String password ) {
    StringBuilder urlString = new StringBuilder( vfsScheme );
    urlString.append( ":" );

    urlString.append( serverUrl.getProtocol() );
    urlString.append( "://" );

    if ( !nullOrEmpty( username ) ) {
      urlString.append( username );
      urlString.append( ":" );
      urlString.append( password );
      urlString.append( "@" );
    }

    urlString.append( serverUrl.getHost() );
    int port = serverUrl.getPort();
    if ( port != -1 ) { // if port is specified
      urlString.append( ":" );
      urlString.append( port );
    }

    urlString.append( serverUrl.getPath() );

    return urlString.toString();
  }

  /***
   * Shows a message box to the user
   * @param message
   * @param shell
   */
  private void showMessage( String message, Shell shell ) {
    MessageBox box = new MessageBox( shell );
    box.setText( "BOX TEXT" ); //$NON-NLS-1$
    box.setMessage( message );
    box.open();
  }

  /***
   * Connects to the Pentaho repository specified by the information
   * in the view associated with the controller
   */
  protected void connect() {
    try {
      URL serverUrl = this.getView().getServerUrl();
      String userName = this.getView().getUserName();
      String password = this.getView().getPassword();
      String vfsScheme = this.getView().getVfsScheme();
      String connectionString = this.getPentahoConnectionString( vfsScheme, serverUrl, userName, password );

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

  protected void newConnection() {
    PentahoConnectionConfiguration configuration = new PentahoConnectionConfiguration()
      .setName( "amazing" );

    Shell openUrlShell = this.getView().getVfsFileChooserDialog().dialog;
    PentahoConnectionConfigurationDialog dialog = new PentahoConnectionConfigurationDialog(  openUrlShell , configuration );
    PentahoConnectionConfigurationDialogController controller = new PentahoConnectionConfigurationDialogController( dialog );

    dialog.open();
  }

  // region aux
  private static boolean nullOrEmpty( String string ) {
    return string == null || string.isEmpty();
  }
  // endregion
  // endregion


}
