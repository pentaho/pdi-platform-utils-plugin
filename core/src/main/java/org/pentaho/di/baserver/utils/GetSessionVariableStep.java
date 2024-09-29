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

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import java.util.List;

public class GetSessionVariableStep extends BaseStep implements StepInterface {
  private static Class<?> PKG = GetSessionVariableMeta.class; // for i18n purposes, needed by Translator2!!
  private GetSessionVariableMeta meta;
  private GetSessionVariableData data;

  public GetSessionVariableStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
                                 TransMeta transMeta,
                                 Trans trans ) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }

  public boolean init( StepMetaInterface smi, StepDataInterface sdi ) {
    meta = (GetSessionVariableMeta) smi;
    data = (GetSessionVariableData) sdi;

    if ( super.init( smi, sdi ) ) {
      data.readsRows = getStepMeta().getRemoteInputSteps().size() > 0;
      List<StepMeta> previous = getTransMeta().findPreviousSteps( getStepMeta() );
      if ( previous != null && previous.size() > 0 ) {
        data.readsRows = true;
      }
      return true;
    }
    return false;
  }

  public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {
    meta = (GetSessionVariableMeta) smi;
    data = (GetSessionVariableData) sdi;

    Object[] rowData;

    // no row data input from previous steps
    if ( !data.readsRows ) {
      // create a new row
      rowData = RowDataUtil.allocateRowData( 0 );
      incrementLinesRead();
      data.inputRowMeta = new RowMeta();

      // get session variables data
      getSessionVariablesData();

      // add session variables data to row
      rowData = RowDataUtil.addRowData( rowData, data.inputRowMeta.size(), data.extraData );
      putRow( data.outputRowMeta, rowData );

      // end processing
      setOutputDone();
      return false;
    }

    // process row
    rowData = getRow();
    if ( rowData != null ) {
      // get session variables data (once)
      if ( first ) {
        first = false;
        data.inputRowMeta = getInputRowMeta();
        getSessionVariablesData();
      }

      // add session variables data to row
      rowData = RowDataUtil.addRowData( rowData, data.inputRowMeta.size(), data.extraData );
      putRow( data.outputRowMeta, rowData );

      // continue
      return true;

    } else {
      // end processing
      setOutputDone();
      return false;
    }
  }

  private void getSessionVariablesData() throws KettleException {
    data.outputRowMeta = data.inputRowMeta.clone();
    meta.getFields( data.outputRowMeta, getStepname(), null, null, this, repository, metaStore );

    // convert the data to the desired data type
    data.conversionMeta = data.outputRowMeta.cloneToType( ValueMetaInterface.TYPE_STRING );
    data.extraData = new Object[ meta.getFieldName().length ];
    for ( int i = 0; i < meta.getFieldName().length; i++ ) {
      ValueMetaInterface targetMeta = data.outputRowMeta.getValueMeta( data.inputRowMeta.size() + i );
      ValueMetaInterface sourceMeta = data.conversionMeta.getValueMeta( data.inputRowMeta.size() + i );
      data.extraData[ i ] =
        targetMeta.convertData( sourceMeta, getValue( meta.getVariableName()[ i ], getRowDefaultValue( i ) ) );
    }
  }

  private Object getValue( String varName, Object defaultValue ) {
    String sessionVarName = environmentSubstitute( varName );
    Object value;
    try {
      // get session variable
      value = SessionHelper.getSessionVariable( sessionVarName );

    // did not find a session variable
    } catch ( NoClassDefFoundError e ) {
      // get the PDI internal variable which simulates the session variable
      String pdiVarName = "${" + SessionHelper.SIMULATED_SESSION_PREFIX + sessionVarName + "}";
      value = environmentSubstitute( pdiVarName );

      // did not find any internal variable, use default value
      if ( value.equals( pdiVarName ) ) {
        value = defaultValue;
      }
    }

    logBasic( BaseMessages.getString( PKG, "GetSessionVariable.Log.GetVariable", sessionVarName, value ) );
    return value;
  }

  private Object getRowDefaultValue( int i ) {
    return environmentSubstitute( meta.getDefaultValue()[ i ] );
  }

  public void dispose( StepMetaInterface smi, StepDataInterface sdi ) {
    super.dispose( smi, sdi );
  }
}
