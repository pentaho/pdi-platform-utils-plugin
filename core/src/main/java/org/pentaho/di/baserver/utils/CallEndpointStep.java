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


import org.pentaho.di.baserver.utils.inspector.Endpoint;
import org.pentaho.di.baserver.utils.inspector.Inspector;
import org.pentaho.di.baserver.utils.web.Http;
import org.pentaho.di.baserver.utils.web.HttpConnectionHelper;
import org.pentaho.di.baserver.utils.web.HttpParameter;
import org.pentaho.di.baserver.utils.web.Response;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;

import java.util.List;
import java.util.ArrayList;

public class CallEndpointStep extends BaseStep implements StepInterface {
  private static Class<?> PKG = CallEndpointMeta.class; // for i18n purposes, needed by Translator2!!
  private CallEndpointMeta meta;
  private CallEndpointData data;
  private Inspector inspector;
  private HttpConnectionHelper connectionHelper;

  public CallEndpointStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
                           TransMeta transMeta,
                           Trans trans ) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }

  public boolean init( StepMetaInterface smi, StepDataInterface sdi ) {
    meta = (CallEndpointMeta) smi;
    data = (CallEndpointData) sdi;
    inspector = Inspector.getInstance();
    connectionHelper = HttpConnectionHelper.getInstance();
    inspector.refreshSettings( meta.getServerURL(), meta.getUserName(), meta.getPassword() );
    return super.init( smi, sdi );
  }

  public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {
    meta = (CallEndpointMeta) smi;
    data = (CallEndpointData) sdi;

    // get next row
    Object[] rowData = getRow();

    if ( rowData == null ) {
      // no more input expected => end processing
      setOutputDone();
      return false;
    }

    if ( first ) {
      data.outputRowMeta = getInputRowMeta().clone();
      meta.getFields( data.outputRowMeta, getStepname(), null, null, this, repository, metaStore );
      first = false;
    }

    String moduleName;
    String endpointPath;
    String httpMethod;
    if ( meta.isEndpointFromField() ) {
      moduleName = getRowValue( rowData, environmentSubstitute( meta.getModuleName() ), "" );
      endpointPath = getRowValue( rowData, environmentSubstitute( meta.getEndpointPath() ), "" );
      httpMethod = getRowValue( rowData, environmentSubstitute( meta.getHttpMethod() ), "" );
    } else {
      moduleName = environmentSubstitute( meta.getModuleName() );
      endpointPath = environmentSubstitute( meta.getEndpointPath() );
      httpMethod = environmentSubstitute( meta.getHttpMethod() );
    }

    if ( moduleName == null || endpointPath == null ) {
      log.logError( "Module name or endpoint path is not specified" );
      throw new KettleException( "Module name or endpoint path is not specified" );
    }

    List<HttpParameter> httpParameters = new ArrayList<>();
    for ( int i = 0; i < meta.getFieldName().length; i++ ) {
      HttpParameter httpParameter = new HttpParameter();
      httpParameter.setName( meta.getParameter()[ i ] );
      httpParameter.setValue( getRowValue( rowData, i ) );
      httpParameters.add( httpParameter );
    }

    Http method = Http.getHttpMethod( httpMethod );

    Endpoint endpoint = inspector.getEndpoint( moduleName, endpointPath, method );

    if ( endpoint != null ) {
      for ( HttpParameter httpParameter : httpParameters ) {
        HttpParameter.ParamType paramType = endpoint.getParameterType( httpParameter.getName() );
        if ( paramType != null ) {
          httpParameter.setParamType( paramType );
        }
      }
    }


    Response response = null;

    if ( meta.isBypassingAuthentication() ) {
      try {
        IPentahoSession session = PentahoSessionHolder.getSession();
        if ( session != null ) {
          response = connectionHelper.invokeEndpoint( moduleName, endpointPath, httpMethod, httpParameters );
        }
      } catch ( NoClassDefFoundError ex ) {
        logBasic( "No valid session. Falling back to normal authentication mode." );
      }
    }

    if ( response == null ) {

      String serverUrl = environmentSubstitute( meta.getServerURL() );
      String username = environmentSubstitute( meta.getUserName() );
      String password = environmentSubstitute( meta.getPassword() );

      response = connectionHelper.invokeEndpoint( serverUrl, username, password, moduleName, endpointPath, httpMethod,
        httpParameters );
    }

    int index = getInputRowMeta().size();
    rowData = RowDataUtil.addValueData( rowData, index++, response.getResult() );
    rowData = RowDataUtil.addValueData( rowData, index++, (long) response.getStatusCode() );
    rowData = RowDataUtil.addValueData( rowData, index, response.getResponseTime() );
    putRow( data.outputRowMeta, rowData );

    // continue processing
    return true;
  }

  protected String getRowValue( Object[] rowData, String fieldName, String defaultValue ) throws KettleException {

    // find a matching field
    int index = getInputRowMeta().indexOfValue( fieldName );
    if ( index >= 0 ) {
      ValueMetaInterface valueMeta = getInputRowMeta().getValueMeta( index );
      return valueMeta.getCompatibleString( rowData[ index ] );
    }

    // otherwise, return default value
    return defaultValue;
  }

  String getRowValue( Object[] rowData, int i ) throws KettleException {

    // find a matching field
    String fieldName = meta.getFieldName()[ i ];
    if ( !Const.isEmpty( fieldName ) ) {
      int index = getInputRowMeta().indexOfValue( fieldName );
      if ( index >= 0 ) {
        ValueMetaInterface valueMeta = getInputRowMeta().getValueMeta( index );
        Object valueData = rowData[ index ];
        return valueMeta.getCompatibleString( valueData );
      }

      // otherwise, return default value
      logBasic( BaseMessages
        .getString( PKG, "CallEndpoint.Log.UnableToFindFieldUsingDefault", fieldName, getRowDefaultValue( i ) ) );
    }
    return getRowDefaultValue( i );
  }

  private String getRowDefaultValue( int i ) {
    return environmentSubstitute( meta.getDefaultValue()[ i ] );
  }

  public void dispose( StepMetaInterface smi, StepDataInterface sdi ) {
    meta = (CallEndpointMeta) smi;
    data = (CallEndpointData) sdi;

    super.dispose( smi, sdi );
  }

}
