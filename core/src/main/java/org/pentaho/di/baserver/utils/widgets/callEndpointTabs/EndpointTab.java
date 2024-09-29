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


package org.pentaho.di.baserver.utils.widgets.callEndpointTabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.baserver.utils.BAServerCommonDialog;
import org.pentaho.di.baserver.utils.CallEndpointMeta;
import org.pentaho.di.baserver.utils.inspector.Endpoint;
import org.pentaho.di.baserver.utils.web.Http;
import org.pentaho.di.baserver.utils.inspector.Inspector;
import org.pentaho.di.baserver.utils.widgets.BrowserBuilder;
import org.pentaho.di.baserver.utils.widgets.ButtonBuilder;
import org.pentaho.di.baserver.utils.widgets.GroupBuilder;
import org.pentaho.di.baserver.utils.widgets.LabelBuilder;
import org.pentaho.di.baserver.utils.widgets.RadioBuilder;
import org.pentaho.di.baserver.utils.widgets.fields.ComboVarFieldBuilder;
import org.pentaho.di.baserver.utils.widgets.fields.Field;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.ComboVar;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EndpointTab extends Tab {
  public static final int FIELD_WIDTH = 250;
  public static final int BOTTOM_PLACEMENT = 100;
  private static final String HTML_DOC = "<html>\n" + "<head>\n"
      + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />"
      + "<style>{0}</style>"
      + "</head>\n<body>{1}</body>\n</html>";


  private final TransMeta transMeta;
  private final String stepName;
  private final LogChannel log;
  private final ServerTab serverTab;
  private final Button getEndpointButton;
  private ComboVar serverModule, resourcePath, httpMethod;
  private Browser resourcePathDetailsField;
  private final Button fromServerRadio, fieldsUpstreamRadio;
  private boolean showNonSupportedEndpoints;

  public EndpointTab( CTabFolder tabFolder, PropsUI props, TransMeta transMeta, ModifyListener modifyListener,
                      SelectionListener selectionListener, String stepName, LogChannel log, ServerTab serverTab ) {
    super( tabFolder, BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.Title" ), props );
    this.transMeta = transMeta;
    this.stepName = stepName;
    this.log = log;
    this.serverTab = serverTab;
    this.showNonSupportedEndpoints = isShowingNonSupportedEndpoints();

    Group endpointLocationGroup = new GroupBuilder( this, props )
        .setText( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.EndpointLocation" ) )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT )
        .build();
    fromServerRadio = new RadioBuilder( endpointLocationGroup, props )
        .setText( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.FromServer" ) )
        .addSelectionListener( selectionListener )
        .setLeftPlacement( LEFT_PLACEMENT )
        .build();
    getEndpointButton = new ButtonBuilder( endpointLocationGroup, props )
        .setLabelText( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.GetEndpoint" ) )
        .setLeft( fromServerRadio )
        .build();
    ( (FormData) getEndpointButton.getLayoutData() ).top = new FormAttachment( fromServerRadio, 0, SWT.CENTER );
    getEndpointButton.addSelectionListener( new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        super.widgetSelected( selectionEvent );
        refreshEndpoints();
      }
    } );
    fieldsUpstreamRadio = new RadioBuilder( endpointLocationGroup, props )
        .setText( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.FieldsUpstream" ) )
        .addSelectionListener( selectionListener )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setTop( fromServerRadio )
        .setTopMargin( BAServerCommonDialog.MEDIUM_MARGIN )
        .build();
    final SelectionAdapter switchEndpointLocation = new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        super.widgetSelected( selectionEvent );
        if ( !fromServerRadio.getSelection() ) {
          getEndpointButton.setEnabled( false );
        } else {
          getEndpointButton.setEnabled( true );
        }
        updateModuleNamesComboBox();
        updateEndpointPathsComboBox();
        updateHttpMethodsComboBox();
        updateEndpointPathsDetailsField();
      }
    };
    fromServerRadio.addSelectionListener( switchEndpointLocation );
    fieldsUpstreamRadio.addSelectionListener( switchEndpointLocation );
    fromServerRadio.setSelection( true );

    Group wsEndpointGroup = new GroupBuilder( this, props )
        .setText( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.WebServiceEndpoint" ) )
        .setTop( endpointLocationGroup )
        .setTopMargin( BAServerCommonDialog.LARGE_MARGIN )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setRightPlacement( RIGHT_PLACEMENT / 2 )
        .setBottomPlacement( RIGHT_PLACEMENT )
        .build();

    final Field<ComboVar> serverModuleField = new ComboVarFieldBuilder( wsEndpointGroup, props )
        .setVariableSpace( transMeta )
        .addSelectionListener( new SelectionAdapter() {
          @Override public void widgetSelected( SelectionEvent selectionEvent ) {
            super.widgetSelected( selectionEvent );
            if ( fromServerRadio.getSelection() ) {
              updateEndpointPathsComboBox();
              updateHttpMethodsComboBox();
              updateEndpointPathsDetailsField();
            }
          }
        } )
        .addModifyListener( modifyListener )
        .setLabel( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.BAServerModule" ) )
        .setTopMargin( BAServerCommonDialog.MEDIUM_MARGIN )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setWidth( FIELD_WIDTH )
        .build();
    serverModule = serverModuleField.getControl();
    final Field<ComboVar> resourcePathField = new ComboVarFieldBuilder( wsEndpointGroup, props )
        .setVariableSpace( transMeta )
        .addSelectionListener( new SelectionAdapter() {
          @Override public void widgetSelected( SelectionEvent selectionEvent ) {
            super.widgetSelected( selectionEvent );
            if ( fromServerRadio.getSelection() ) {
              updateHttpMethodsComboBox();
              updateEndpointPathsDetailsField();
            }
          }
        } )
        .addModifyListener( modifyListener )
        .setLabel( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.ResourcePath" ) )
        .setTop( serverModuleField )
        .setTopMargin( BAServerCommonDialog.MEDIUM_MARGIN )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setWidth( FIELD_WIDTH )
        .build();
    resourcePath = resourcePathField.getControl();
    final Field<ComboVar> httpMethodField = new ComboVarFieldBuilder( wsEndpointGroup, props )
        .setVariableSpace( transMeta )
        .addSelectionListener( new SelectionAdapter() {
          @Override public void widgetSelected( SelectionEvent selectionEvent ) {
            super.widgetSelected( selectionEvent );
            if ( fromServerRadio.getSelection() ) {
              updateEndpointPathsDetailsField();
            }
          }
        } )
        .addModifyListener( modifyListener )
        .setLabel( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.HTTPMethod" ) )
        .setTop( resourcePathField )
        .setTopMargin( BAServerCommonDialog.MEDIUM_MARGIN )
        .setLeftPlacement( LEFT_PLACEMENT )
        .setWidth( FIELD_WIDTH )
        .build();
    httpMethod = httpMethodField.getControl();

    Label lab = new LabelBuilder( this, props )
        .setText( BaseMessages.getString( PKG, "CallEndpointDialog.TabItem.Endpoint.ResourcePathDetails" ) )
        .setTop( endpointLocationGroup )
        .setTopMargin( BAServerCommonDialog.LARGE_MARGIN )
        .setLeft( wsEndpointGroup )
        .setLeftMargin( BAServerCommonDialog.LARGE_MARGIN )
        .setRightMargin( LEFT_PLACEMENT )
        .build();

    resourcePathDetailsField = new BrowserBuilder( this, props )
        .setTop( lab )
        .setTopMargin( BAServerCommonDialog.SMALL_MARGIN )
        .setLeft( wsEndpointGroup )
        .setLeftMargin( BAServerCommonDialog.LARGE_MARGIN )
        .setRightPlacement( RIGHT_PLACEMENT )
        .setBottomPlacement( BOTTOM_PLACEMENT )
        .build();
  }

  @Override public void loadData( CallEndpointMeta meta ) {
    fromServerRadio.setSelection( !meta.isEndpointFromField() );
    getEndpointButton.setEnabled( !meta.isEndpointFromField() );
    fieldsUpstreamRadio.setSelection( meta.isEndpointFromField() );
    serverModule.setText( meta.getModuleName() );
    resourcePath.setText( meta.getEndpointPath() );
    httpMethod.setText( meta.getHttpMethod() );
  }

  public void refreshEndpoints() {
    String serverUrl = serverTab.getServerUrl();
    String userName = serverTab.getUserName();
    String password = serverTab.getPassword();
    if ( serverTab.testConnection( false )
        && Inspector.getInstance().inspectServer( serverUrl, userName, password ) ) {
      updateModuleNamesComboBox();
      updateEndpointPathsComboBox();
      updateHttpMethodsComboBox();
      updateEndpointPathsDetailsField();
    }
  }

  @Override public void saveData( CallEndpointMeta meta ) {
    meta.setEndpointFromField( !fromServerRadio.getSelection() );
    meta.setModuleName( serverModule.getText() );
    meta.setEndpointPath( resourcePath.getText() );
    meta.setHttpMethod( httpMethod.getText() );
  }

  @Override public boolean isValid() {
    return !Const.isEmpty( serverModule.getText() ) && !Const.isEmpty( resourcePath.getText() )
        && !Const.isEmpty( httpMethod.getText() );
  }

  private void updateModuleNamesComboBox() {
    String moduleName = this.transMeta.environmentSubstitute( serverModule.getText() );
    serverModule.removeAll();
    if ( fromServerRadio.getSelection() ) {
      serverModule.removeAll();
      for ( String item : Inspector.getInstance().getModuleNames() ) {
        serverModule.add( item );
      }
      if ( moduleName.isEmpty() ) {
        moduleName = Inspector.getInstance().getDefaultModuleName();
      }
      serverModule.setText( moduleName );
    } else {
      serverModule.setItems( getFieldNames() );
    }
  }

  private void updateEndpointPathsComboBox() {
    String endpointPath = transMeta.environmentSubstitute( resourcePath.getText() );
    resourcePath.removeAll();
    Inspector inspector = Inspector.getInstance();
    if ( fromServerRadio.getSelection() ) {
      String moduleName = transMeta.environmentSubstitute( serverModule.getText() );
      for ( String path : inspector.getEndpointPaths( moduleName ) ) {
        Iterable<Endpoint> endpoints = inspector.getEndpoints( moduleName, path );
        boolean add = false;
        for ( Endpoint endpoint : endpoints ) {
          if ( endpoint.isSupported() || this.showNonSupportedEndpoints ) {
            add = true;
            if ( endpointPath.isEmpty() ) {
              endpointPath = path;
            }
          }
        }
        if ( !add && endpointPath.equals( path ) ) {
          endpointPath = "";
        } else if ( add ) {
          resourcePath.add( path );
        }
      }

      List<String> endpointPaths = new ArrayList<String>( Arrays.asList( resourcePath.getItems() ) );
      if ( endpointPaths.size() == 0 ) {
        endpointPath = "";
      } else if ( endpointPaths.indexOf( endpointPath ) < 0 ) {
        endpointPath = endpointPaths.get( 0 );
      }

      resourcePath.setText( endpointPath );
    } else {
      resourcePath.setItems( getFieldNames() );
    }
  }

  private void updateHttpMethodsComboBox() {
    String httpMethod = transMeta.environmentSubstitute( this.httpMethod.getText() );
    this.httpMethod.removeAll();
    if ( fromServerRadio.getSelection() ) {
      String moduleName = transMeta.environmentSubstitute( serverModule.getText() );
      String endpointPath = transMeta.environmentSubstitute( resourcePath.getText() );
      Iterable<Endpoint> endpoints = Inspector.getInstance().getEndpoints( moduleName, endpointPath );
      for ( Endpoint endpoint : endpoints ) {
        if ( endpoint.isSupported() || this.showNonSupportedEndpoints ) {
          this.httpMethod.add( endpoint.getHttpMethod().name() );
          if ( httpMethod.isEmpty() ) {
            httpMethod = endpoint.getHttpMethod().name();
          }
        }
      }

      List<String> httpMethods = new ArrayList<String>( Arrays.asList( this.httpMethod.getItems() ) );
      if ( httpMethods.size() == 0 ) {
        httpMethod = "";
      } else if ( httpMethods.indexOf( endpointPath ) < 0 ) {
        httpMethod = httpMethods.get( 0 );
      }

      this.httpMethod.setText( httpMethod );
    } else {
      this.httpMethod.setItems( getFieldNames() );
    }
  }

  private boolean isShowingNonSupportedEndpoints() {
    String value = Variables.getADefaultVariableSpace().environmentSubstitute( "${ShowNonSupportedEndpoints}" );
    if ( value == null ) {
      return false;
    }

    return new Boolean( value );
  }

  private void updateEndpointPathsDetailsField() {
    String newValue = "";
    if ( fromServerRadio.getSelection() ) {
      String moduleName = transMeta.environmentSubstitute( serverModule.getText() );
      String endpointPath = transMeta.environmentSubstitute( resourcePath.getText() );
      String method = transMeta.environmentSubstitute( httpMethod.getText() );
      Iterable<Endpoint> endpoints = Inspector.getInstance().getEndpoints( moduleName, endpointPath );
      for ( Endpoint endpoint : endpoints ) {
        if ( method.isEmpty() || endpoint.getHttpMethod().equals( Http.valueOf( method ) ) ) {
          if ( endpoint.isDeprecated() ) {
            newValue = BaseMessages.getString( CallEndpointMeta.class, "WadlParser.endpoint.deprecated" ) + "<br/>";
          }

          if ( endpoint.isSupported() ) {
            String documentation = endpoint.getDocumentation();
            newValue += documentation != null ? documentation : "";
          } else if ( this.showNonSupportedEndpoints ) {
            newValue += BaseMessages.getString( CallEndpointMeta.class, "WadlParser.endpoint.not.supported" );
          }
          break;
        }
      }
    }

    if ( newValue.isEmpty() ) {
      newValue = BaseMessages.getString( CallEndpointMeta.class, "WadlParser.endpoint.no.details" );
    }

    newValue = MessageFormat.format( HTML_DOC, BaseMessages.getString( CallEndpointMeta.class,
        "WadlParser.endpoint.style" ), newValue );

    this.resourcePathDetailsField.setText( newValue );
  }

  private String[] getFieldNames() {
    StepMeta stepMeta = transMeta.findStep( stepName );
    List<String> entries = null;
    if ( stepMeta != null ) {
      try {
        // get field names from previous steps
        RowMetaInterface row = transMeta.getPrevStepFields( stepMeta );
        entries = new ArrayList<String>();
        for ( int i = 0; i < row.size(); i++ ) {
          entries.add( row.getValueMeta( i ).getName() );
        }
        Collections.sort( entries );
      } catch ( KettleException e ) {
        log.logError( BaseMessages.getString( PKG, "System.Dialog.GetFieldsFailed.Message" ) );
      }
    }
    return entries == null ? new String[ 0 ] : entries.toArray( new String[ entries.size() ] );
  }
}
