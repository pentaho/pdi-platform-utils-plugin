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

package pt.webdetails.di.baserver.utils.repositoryPlugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;

import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.SpoonLifecycleListener;
import org.pentaho.di.ui.spoon.SpoonPerspective;
import org.pentaho.di.ui.spoon.SpoonPluginCategories;
import org.pentaho.di.ui.spoon.SpoonPluginInterface;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.pensol.LibPensolBoot;
import org.pentaho.reporting.libraries.pensol.PentahoSolutionFileProvider;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.vfs.ui.VfsFileChooserDialog;
import pt.webdetails.di.baserver.utils.repositoryPlugin.ui.PentahoSolutionVfsFileChooserController;
import pt.webdetails.di.baserver.utils.repositoryPlugin.ui.PentahoSolutionVfsFileChooserPanel;

@org.pentaho.di.ui.spoon.SpoonPlugin( id = "RepositoryPlugin", image = "" )
@SpoonPluginCategories( { "spoon" } )
public class RepositoryPlugin implements SpoonPluginInterface {

  // region constants
  // endregion

  // region properties
  private XulDomContainer container;

  private Log getLogger() {
    return logger;
  }
  private static final Log logger = LogFactory.getLog( RepositoryPlugin.class );

  private Spoon getSpoon() {
    return Spoon.getInstance();
  }

  private Constants getConstants() {
    return Constants.getInstance();
  }

  /***
   * Gets the file system manager the plugin uses to add the JCR VFS file provider
   * @return
   */
  protected DefaultFileSystemManager getFileSystemManager() {
    return this.fileSystemManager;
  }
  protected RepositoryPlugin setFileSystemManager( DefaultFileSystemManager fileSystemManager ) {
    this.fileSystemManager = fileSystemManager;
    return this;
  }
  private DefaultFileSystemManager fileSystemManager;

  /**
   * Gets the file system manager used by the KettleVFS singleton
   * @return
   */
  private DefaultFileSystemManager getKettleVFSFileSystemManager( ) {
    // If no file system manager is set, try to set it to the one given by KettleVFS
    FileSystemManager fileSystemManager = KettleVFS.getInstance().getFileSystemManager();
    DefaultFileSystemManager defaultFileSystemManager = null;
    if ( fileSystemManager instanceof DefaultFileSystemManager ) {
      defaultFileSystemManager = (DefaultFileSystemManager) fileSystemManager;
    }
    return defaultFileSystemManager;
  }

  protected ToolbarEventHandler getToolbarEventHandler() {
    return this.toolbarEventHandler;
  }
  protected RepositoryPlugin setToolbarEventHandler( ToolbarEventHandler toolbarEventHandler ) {
    this.toolbarEventHandler = toolbarEventHandler;
    return this;
  }
  private ToolbarEventHandler toolbarEventHandler;
  // endregion

  // region Constructors
  public RepositoryPlugin( DefaultFileSystemManager fileSystemManager ) {
    this.forceLibPensolPropertiesLoad();

    // if no file system manager is specified used the one from Kettle VFS
    if ( fileSystemManager == null ) {
      fileSystemManager = this.getKettleVFSFileSystemManager();
    }
    this.setFileSystemManager( fileSystemManager );

    String vfsScheme = this.getConstants().getVfsScheme();
    if ( fileSystemManager != null && !fileSystemManager.hasProvider( vfsScheme ) ) {
      try {
        fileSystemManager.addProvider( vfsScheme, new PentahoSolutionFileProvider() );
      } catch ( FileSystemException e ) {
        this.getLogger().error( "Error trying to add Pentaho Solution File provider.", e );
      }
    }

    this.registerJCRFileChooserDialog();

    this.setToolbarEventHandler( new ToolbarEventHandler() );

    final RepositoryPlugin repositoryPlugin = this;
    this.lifecycleListener = new SpoonLifecycleListener() {
      @Override public void onEvent( SpoonLifeCycleEvent evt ) {
        repositoryPlugin.getToolbarEventHandler().getSpoonLifeCycleListener().onEvent( evt );
      }
    };

  }

  public RepositoryPlugin() {
    this( null );
  }

  // endregion

  // region Methods
  @Override
  public void applyToContainer( String category, final XulDomContainer container ) throws XulException {
    this.container = container;
    container.registerClassLoader( getClass().getClassLoader() );
    if ( category.equals( "spoon" ) ) {
      container.loadOverlay( "pt/webdetails/di/baserver/utils/repositoryPlugin/ui/spoon_overlays.xul" );
      container.addEventHandler( this.getToolbarEventHandler() );
    }
  }

  @Override
  public SpoonLifecycleListener getLifecycleListener() {
    return this.lifecycleListener;
  }
  private SpoonLifecycleListener lifecycleListener;

  @Override
  public SpoonPerspective getPerspective() {
    return null;
  }

  /***
   * This method changes the context class loader to the LibPensol classloader and
   * triggers the initialization of the LibPensolBoot properties (configuration).
   * This is required because when not switching the classloader, LibPensol fails to
   * find the org/pentaho/reporting/libraries/pensol/libpensol.properties resource.
   */
  private void forceLibPensolPropertiesLoad() {
    ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader( LibPensolBoot.class.getClassLoader() );
      LibPensolBoot libPensolBoot = LibPensolBoot.getInstance();
      Configuration configuration = libPensolBoot.getGlobalConfig();
    } catch ( Exception e ) {
      this.getLogger().error( "Failed to force LibPensol configuration initialization." );
    } finally {
      Thread.currentThread().setContextClassLoader( origClassLoader );
    }
  }

  private void registerJCRFileChooserDialog() {
    Spoon spoon = this.getSpoon();
    final VfsFileChooserDialog vfsFileChooserDialog = spoon.getVfsFileChooserDialog( null, null );
    final PentahoSolutionVfsFileChooserPanel vfsPanel = new PentahoSolutionVfsFileChooserPanel( vfsFileChooserDialog );
    vfsFileChooserDialog.addVFSUIPanel( vfsPanel );

    PentahoSolutionVfsFileChooserController controller = new PentahoSolutionVfsFileChooserController( vfsPanel );
  }
  // endregion

}
