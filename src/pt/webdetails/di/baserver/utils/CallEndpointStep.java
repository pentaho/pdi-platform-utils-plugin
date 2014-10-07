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

package pt.webdetails.di.baserver.utils;


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
import pt.webdetails.di.baserver.utils.web.HttpConnectionHelper;
import pt.webdetails.di.baserver.utils.web.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Marco Vala
 */
public class CallEndpointStep extends BaseStep implements StepInterface {
  private static Class<?> PKG = CallEndpointMeta.class; // for i18n purposes, needed by Translator2!!
  private CallEndpointMeta meta;
  private CallEndpointData data;

  public CallEndpointStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
                           TransMeta transMeta,
                           Trans trans ) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }

  public boolean init( StepMetaInterface smi, StepDataInterface sdi ) {
    meta = (CallEndpointMeta) smi;
    data = (CallEndpointData) sdi;

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

    Map<String, String> queryParameters = new HashMap<String, String>();
    for ( int i = 0; i < meta.getFieldName().length; i++ ) {
      queryParameters.put( meta.getParameter()[ i ], getRowValue( rowData, i ) );
    }

    Response response = null;

    if ( meta.isBypassingAuthentication() ) {
      try {
        IPentahoSession session = PentahoSessionHolder.getSession();
        if ( session != null ) {
          response = HttpConnectionHelper.invokeEndpoint( moduleName, endpointPath, httpMethod, queryParameters );
        }
      } catch ( NoClassDefFoundError ex ) {
        logBasic( "No valid session. Falling back to normal authentication mode." );
      }
    }

    if ( response == null ) {

      String serverUrl = environmentSubstitute( meta.getServerURL() );
      String username = environmentSubstitute( meta.getUserName() );
      String password = environmentSubstitute( meta.getPassword() );

      response =
        HttpConnectionHelper
          .invokeEndpoint( serverUrl, username, password, moduleName, endpointPath, httpMethod, queryParameters );
    }

    int index = getInputRowMeta().size();
    rowData = RowDataUtil.addValueData( rowData, index++, response.getResult() );
    rowData = RowDataUtil.addValueData( rowData, index++, (long) response.getStatusCode() );
    rowData = RowDataUtil.addValueData( rowData, index, response.getResponseTime() );
    putRow( data.outputRowMeta, rowData );

    // continue processing
    return true;
  }

  private String getRowValue( Object[] rowData, String fieldName, String defaultValue ) throws KettleException {

    // find a matching field
    int index = getInputRowMeta().indexOfValue( fieldName );
    if ( index >= 0 ) {
      ValueMetaInterface valueMeta = getInputRowMeta().getValueMeta( index );
      return valueMeta.getCompatibleString( rowData[ index ] );
    }

    // otherwise, return default value
    return defaultValue;
  }

  private String getRowValue( Object[] rowData, int i ) throws KettleException {

    // find a matching field
    String fieldName = meta.getFieldName()[ i ];
    int index = getInputRowMeta().indexOfValue( fieldName );
    if ( index >= 0 ) {
      ValueMetaInterface valueMeta = getInputRowMeta().getValueMeta( index );
      Object valueData = rowData[ index ];
      return valueMeta.getCompatibleString( valueData );
    }

    // otherwise, return default value
    logBasic( BaseMessages
      .getString( PKG, "CallEndpoint.Log.UnableToFindFieldUsingDefault", fieldName, getRowDefaultValue( i ) ) );
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
