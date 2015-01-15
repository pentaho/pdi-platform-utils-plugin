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

package pt.webdetails.di.baserver.utils.repositoryPlugin.ui;

import org.apache.commons.vfs.FileObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.EngineMetaInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.SpoonLifecycleListener;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.components.XulButton;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;
import org.pentaho.vfs.ui.VfsFileChooserDialog;
import org.pentaho.xul.swt.tab.TabItem;
import org.pentaho.xul.swt.tab.TabListener;
import pt.webdetails.di.baserver.utils.repositoryPlugin.Constants;

import java.util.ArrayList;
import java.util.Collection;

public class ToolbarController extends AbstractXulEventHandler {

  // region inner definitions
  private static class VfsDialogFileInfo {

    private FileObject initialFile;
    public FileObject getInitialFile() {
      return this.initialFile;
    }
    public VfsDialogFileInfo setInitialFile( FileObject initialFile ) {
      this.initialFile = initialFile;
      return this;
    }

    private FileObject rootFile;
    public FileObject getRootFile() {
      return this.rootFile;
    }
    public VfsDialogFileInfo setRootFile( FileObject rootFile ) {
      this.rootFile = rootFile;
      return this;
    }
  }
  // endregion

  // region Constructors
  public ToolbarController() {
    final ToolbarController toolbarController = this;

    // lifecycle listener to add tab select listener when spoon starts up
    // it is necessary to do it only on startup to make sure that the tabs were created
    this.spoonLifecycleListener = new SpoonLifecycleListener() {
      @Override public void onEvent( SpoonLifeCycleEvent evt ) {
        if ( evt.equals( SpoonLifeCycleEvent.STARTUP ) ) {
          toolbarController.getSpoon().getTabSet().addListener( new TabListener() {
            @Override public void tabSelected( TabItem item ) {
              toolbarController.updateSaveVFSButtonEnableStatus();
            }

            @Override public void tabDeselected( TabItem item ) { }

            @Override public boolean tabClose( TabItem item ) {
              return false;
            }
          } );
        }
      }
    };
  }
  // endregion

  // region properties

  /**
   * @return the name of the controller to be used in the XUL views
   */
  public String getName() {
    return "toolbarController";
  }

  private Spoon getSpoon() {
    return Spoon.getInstance();
  }

  private Constants getConstants() {
    return Constants.getInstance();
  }

  public SpoonLifecycleListener getSpoonLifeCycleListener() {
    return this.spoonLifecycleListener;
  }
  private SpoonLifecycleListener spoonLifecycleListener;

  private XulButton getSaveVfsButton() {
    if ( this.saveVfsButton == null ) {
      XulComponent saveVfsButton = this
          .getXulDomContainer()
          .getDocumentRoot()
          .getElementById( "toolbar-file-save-url" );
      this.saveVfsButton = (XulButton) saveVfsButton;
    }
    return this.saveVfsButton;
  }
  private XulButton saveVfsButton;

  private VfsDialogFileInfo getLastOpenedFile() {
    VfsDialogFileInfo fileInfo = new VfsDialogFileInfo();
    Spoon spoon = this.getSpoon();
    try {
      fileInfo.setInitialFile( KettleVFS.getFileObject( spoon.getLastFileOpened() ) );
      fileInfo.setRootFile( fileInfo.getInitialFile().getFileSystem().getRoot() );
    } catch ( Exception e ) {
      String message = Const.getStackTracker( e );
      new ErrorDialog( spoon.getShell(), BaseMessages.getString( Spoon.class, "Spoon.Error" ), message, e );
    }

    return fileInfo;
  }
  // endregion

  // region event handlers
  public void saveFileToPentahoRepository() {
    this.saveXMLFileToVfs();
  }

  public void openFileFromPentahoRepository() {
    Spoon spoon = this.getSpoon();
    VfsDialogFileInfo fileInfo = this.getLastOpenedFile();

    VfsFileChooserDialog vfsFileChooserDialog = spoon.getVfsFileChooserDialog( fileInfo.getRootFile(), fileInfo.getInitialFile() );
    FileObject selectedFile = vfsFileChooserDialog.open(
        spoon.getShell(), null, this.getConstants().getVfsScheme(), true, null,
        Const.STRING_TRANS_AND_JOB_FILTER_EXT, Const.getTransformationAndJobFilterNames(), VfsFileChooserDialog.VFS_DIALOG_OPEN_FILE );

    if ( selectedFile != null ) {
      String uri = selectedFile.getName().getFriendlyURI();
      spoon.setLastFileOpened( uri );
      spoon.openFile( uri, false );
    }
  }
  // endregion

  // region Methods
  protected void updateSaveVFSButtonEnableStatus() {
    Spoon spoon = this.getSpoon();
    TransMeta activeTransformation = spoon.getActiveTransformation();
    JobMeta activeJob = spoon.getActiveJob();
    boolean buttonEnabled = activeTransformation != null || activeJob != null;

    // disable save file on VFS button if no transformation or job is active
    this.getSaveVfsButton().setDisabled( !buttonEnabled );
  }

  private boolean saveXMLFileToVfs() {
    Spoon spoon = this.getSpoon();

    TransMeta transMeta = spoon.getActiveTransformation();
    if ( transMeta != null ) {
      return saveXMLFileToVfs( transMeta );
    }

    JobMeta jobMeta = spoon.getActiveJob();
    if ( jobMeta != null ) {
      return saveXMLFileToVfs( jobMeta );
    }

    return false;
  }

  private boolean saveXMLFileToVfs( EngineMetaInterface meta ) {
    Spoon spoon = this.getSpoon();
    LogChannelInterface spoonLog = spoon.getLog();
    Shell spoonShell = spoon.getShell();

    if (  spoonLog.isBasic() ) {
      spoonLog.logBasic( "Save file as..." );
    }

    VfsDialogFileInfo fileInfo = this.getLastOpenedFile();
    FileObject rootFile = fileInfo.getRootFile();
    FileObject initialFile = fileInfo.getInitialFile();
    if ( rootFile == null || initialFile == null ) {
      return false;
    }

    String filename = null;
    FileObject selectedFile = spoon
        .getVfsFileChooserDialog( rootFile, initialFile )
        .open( spoonShell, null, this.getConstants().getVfsScheme(), true, "Untitled",
          Const.STRING_TRANS_AND_JOB_FILTER_EXT, Const.getTransformationAndJobFilterNames(),
          VfsFileChooserDialog.VFS_DIALOG_SAVEAS );

    if ( selectedFile != null ) {
      filename = selectedFile.getName().getFriendlyURI();
    }

    if ( filename != null ) {
      Collection<String> extensionMasks = new ArrayList<String>( );
      for ( String composedExtension : meta.getFilterExtensions() ) {
        // extension given by meta.getFilterExtensions may contain elements
        // with more than one extension mask separated by ';'.  E.g. "*.ktr;*.kjb"
        String[] extensions = composedExtension.split( ";" );
        for( String simpleExtension : extensions ) {
          extensionMasks.add( simpleExtension );
        }
      }

      filename = this.addDefaultExtensionIfMissing( filename, extensionMasks, meta.getDefaultExtension() );
      // See if the file already exists...
      boolean overrideFile = true;
      try {
        FileObject f = KettleVFS.getFileObject( filename );
        if ( f.exists() ) {
          overrideFile = this.promptShouldOverrideFile();
        }
      } catch ( Exception e ) {
        // TODO do we want to show an error dialog here? My first guess
        // is not, but we might.
      }
      if ( overrideFile ) {
        spoon.save( meta, filename, false );
      }
    }
    return false;
  }

  /**
   *
   * @param fileName the FileName
   * @param extensionMasks The allowed / known extension masks. E.g. [ "*.ktr", "*.kjb" ]
   * @param defaultExtension The default extension to append to the filename. E.g. "ktr"
   * @return The filename with the default extension if the filename originally did not have one of the allowed extensions
   */
  protected String addDefaultExtensionIfMissing( String fileName, Iterable<String> extensionMasks, String defaultExtension ) {
    String resultFileName = fileName;
    // Is the filename ending on .ktr, .xml?
    boolean ending = false;
    for ( String extension : extensionMasks ) {
      if ( fileName.endsWith( extension.substring( 1 ) ) ) {
        ending = true;
      }
    }
    if ( fileName.endsWith( defaultExtension ) ) {
      ending = true;
    }
    if ( !ending ) {
      resultFileName += '.' + defaultExtension;
    }

    return resultFileName;
  }

  /***
   * Prompts the user with a dialog asking if the file should be overriden
   * @return if the the file should be overriden
   */
  private boolean promptShouldOverrideFile() {
    MessageBox mb = new MessageBox( this.getSpoon().getShell(), SWT.NO | SWT.YES | SWT.ICON_WARNING );
    // "This file already exists.  Do you want to overwrite it?"
    mb.setMessage( BaseMessages.getString( Spoon.class, "Spoon.Dialog.PromptOverwriteFile.Message" ) );
    mb.setText( BaseMessages.getString( Spoon.class, "Spoon.Dialog.PromptOverwriteFile.Title" ) );
    int result = mb.open();
    return result == SWT.YES;
  }
  // end region

}
