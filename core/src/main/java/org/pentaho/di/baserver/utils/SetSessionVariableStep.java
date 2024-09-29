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

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class SetSessionVariableStep extends BaseStep implements StepInterface {
  private static Class<?> PKG = SetSessionVariableMeta.class; // for i18n purposes, needed by Translator2!!
  private SetSessionVariableMeta meta;
  private SetSessionVariableData data;

  public SetSessionVariableStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
                                 TransMeta transMeta,
                                 Trans trans ) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }

  public boolean init( StepMetaInterface smi, StepDataInterface sdi ) {
    meta = (SetSessionVariableMeta) smi;
    data = (SetSessionVariableData) sdi;
    return super.init( smi, sdi );
  }

  @Override
  public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {
    meta = (SetSessionVariableMeta) smi;
    data = (SetSessionVariableData) sdi;

    // process row
    Object[] rowData = getRow();
    if ( first ) {
      first = false;
      if ( rowData != null ) {
        // set session variables with the data from the first row
        data.outputRowMeta = getInputRowMeta().clone();
        for ( int i = 0; i < meta.getFieldName().length; i++ ) {
          setValue( meta.getVariableName()[ i ], getRowValue( rowData, i ) );
        }
        putRow( data.outputRowMeta, rowData );

        // continue
        return true;

        // did not get any input row
      } else {
        // use default values for session variables
        logBasic( BaseMessages.getString( PKG, "SetSessionVariable.Log.NoInputRowUseDefaults" ) );
        for ( int i = 0; i < meta.getFieldName().length; i++ ) {
          setValue( meta.getVariableName()[ i ], getRowDefaultValue( i ) );
        }

        // end processing
        setOutputDone();
        return false;
      }

    } else {
      if ( rowData == null ) {
        // end processing
        setOutputDone();
        return false;
      }

      // should not happen, received more than one row
      throw new KettleStepException(
          BaseMessages.getString( PKG, "SetSessionVariable.RuntimeError.MoreThanOneRowReceived" ) );
    }
  }

  protected void setValue( String varName, String value ) throws KettleException {
    // should have a non-empty variable name
    if ( Const.isEmpty( varName ) ) {
      throw new KettleException(
          BaseMessages.getString( PKG, "SetSessionVariable.RuntimeError.EmptyVariableName", value ) );
    } else if ( this.getData().getBlackList().contains( varName ) ) {
      throw new KettleException( BaseMessages.getString( PKG, "SetSessionVariable.RuntimeError.VariableInBlacklist",
          varName ) );
    }

    // set session variable
    String sessionVarName = environmentSubstitute( varName );
    try {
      setSessionVariable( value, sessionVarName );
      // no session inside Spoon
    } catch ( NoClassDefFoundError e ) {

      // set simulated session variable
      sessionVarName = SessionHelper.SIMULATED_SESSION_PREFIX + sessionVarName;
      setVariable( sessionVarName, value );
      Trans trans = getTrans();
      trans.setVariable( sessionVarName, value );

      // propagate to parent transformations
      while ( trans.getParentTrans() != null ) {
        trans = trans.getParentTrans();
        trans.setVariable( sessionVarName, value );
      }
    }
    logBasic( BaseMessages.getString( PKG, "SetSessionVariable.Log.SetVariable", sessionVarName, value ) );
  }

  protected void setSessionVariable( String value, String sessionVarName ) {
    SessionHelper.setSessionVariable( sessionVarName, value );
  }

  protected String getRowValue( Object[] rowData, int i ) throws KettleException {
    // find a matching field
    String fieldName = getMeta().getFieldName()[ i ];
    int index = getData().outputRowMeta.indexOfValue( fieldName );
    if ( index >= 0 ) {
      ValueMetaInterface valueMeta = getData().outputRowMeta.getValueMeta( index );
      Object valueData = rowData[ index ];
      return getMeta().isUsingFormatting() ? valueMeta.getString( valueData )
          : valueMeta.getCompatibleString( valueData );
    }

    // otherwise, return default value
    logBasic( BaseMessages
        .getString( PKG, "SetSessionVariable.Log.UnableToFindFieldUsingDefault", fieldName, getRowDefaultValue( i ) ) );
    return getRowDefaultValue( i );
  }

  private String getRowDefaultValue( int i ) {
    return environmentSubstitute( meta.getDefaultValue()[ i ] );
  }

  public void dispose( StepMetaInterface smi, StepDataInterface sdi ) {
    meta = (SetSessionVariableMeta) smi;
    data = (SetSessionVariableData) sdi;

    super.dispose( smi, sdi );
  }

  protected SetSessionVariableMeta getMeta() {
    return meta;
  }

  protected void setMeta( SetSessionVariableMeta meta ) {
    this.meta = meta;
  }

  protected SetSessionVariableData getData() {
    return data;
  }

  protected void setData( SetSessionVariableData data ) {
    this.data = data;
  }
}
