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

/**
 * @author Marco Vala
 */
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

  private void setValue( String varName, String value ) throws KettleException {
    // should have a non-empty variable name
    if ( Const.isEmpty( varName ) ) {
      throw new KettleException(
        BaseMessages.getString( PKG, "SetSessionVariable.RuntimeError.EmptyVariableName", value ) );
    }

    // set session variable
    String sessionVarName = environmentSubstitute( varName );
    try {
      SessionHelper.setSessionVariable( sessionVarName, value );

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

  private String getRowValue( Object[] rowData, int i ) throws KettleException {
    // find a matching field
    String fieldName = meta.getFieldName()[ i ];
    int index = data.outputRowMeta.indexOfValue( fieldName );
    if ( index >= 0 ) {
      ValueMetaInterface valueMeta = data.outputRowMeta.getValueMeta( index );
      Object valueData = rowData[ index ];
      return meta.isUsingFormatting() ? valueMeta.getString( valueData ) : valueMeta.getCompatibleString( valueData );
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
}
