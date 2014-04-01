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



import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;


import org.pentaho.di.cluster.SlaveConnectionManager;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

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

    String urlParams = "";
    for ( int i = 0; i < meta.getFieldName().length; i++ ) {
      if ( i == 0 ) {
        urlParams = urlParams + "?";
      } else {
        urlParams = urlParams + "&";
      }
      urlParams = urlParams + meta.getParameter()[ i ] + "=" + getRowValue( rowData, i );
    }

    logBasic( "PARAMS: " + urlParams );
    String response = callHttp( urlParams );
    logBasic( "RESPONSE: " + response );

    int index = getInputRowMeta().size();
    logBasic( "INDEX: " + index );

    rowData = RowDataUtil.addValueData( rowData, index, response );
    putRow( data.outputRowMeta, rowData );

    // continue processing
    return true;
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

  private String callHttp( String params ) throws KettleStepException {
    try {
      String module = meta.getModule();
      if ( module.equals( "platform" ) ) {
        module = "";
      } else {
        module = "/plugin/" + module;
      }

      String url = meta.getServerURL() + "/pentaho" + module + "/api/" + meta.getService() + params;
      logBasic( "CALL: " + url );
      return HttpConnectionHelper.callHttp( url, meta.getUsername(), meta.getPassword() );

    } catch ( IOException ex ) {
      logError( ex.toString() );
    }
    return null;
  }




  private void processResult( int status ) {

  }

  public void dispose( StepMetaInterface smi, StepDataInterface sdi ) {
    meta = (CallEndpointMeta) smi;
    data = (CallEndpointData) sdi;

    super.dispose( smi, sdi );
  }
}
