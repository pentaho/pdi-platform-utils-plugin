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

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import java.util.List;

@Step( id = "GetSessionVariableStep",
  name = "GetSessionVariableMeta.Name",
  image = "icons/getsessionvariable.svg",
  description = "GetSessionVariableMeta.Description",
  i18nPackageName = "pt.webdetails.di.baserverutils",
  categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.BAServer",
  isSeparateClassLoaderNeeded = true,
  documentationUrl = "https://pentaho-community.atlassian.net/wiki/display/EAI/Get+Session+Variables" )
public class GetSessionVariableMeta extends BaseStepMeta implements StepMetaInterface {
  private static Class<?> PKG = GetSessionVariableMeta.class; // for i18n purposes, needed by Translator2!!

  private String[] fieldName;
  private String[] variableName;
  private int[] fieldType;
  private String[] fieldFormat;
  private int[] fieldLength;
  private int[] fieldPrecision;
  private String[] currency;
  private String[] decimal;
  private String[] group;
  private int[] trimType;
  private String[] defaultValue;

  public GetSessionVariableMeta() {
    super(); // allocate BaseStepMeta
  }

  public StepInterface getStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr,
                                TransMeta transMeta, Trans trans ) {
    return new GetSessionVariableStep( stepMeta, stepDataInterface, cnr, transMeta, trans );
  }

  public StepDataInterface getStepData() {
    return new GetSessionVariableData();
  }

  public StepDialogInterface getDialog( Shell shell, StepMetaInterface meta, TransMeta transMeta, String name ) {
    return new GetSessionVariableDialog( shell, meta, transMeta, name );
  }


  public String[] getFieldName() {
    return this.fieldName;
  }

  public void setFieldName( String[] fieldName ) {
    this.fieldName = fieldName;
  }

  public String[] getVariableName() {
    return this.variableName;
  }

  public void setVariableName( String[] variableName ) {
    this.variableName = variableName;
  }

  public int[] getFieldType() {
    return this.fieldType;
  }

  public void setFieldType( int[] fieldType ) {
    this.fieldType = fieldType;
  }

  public String[] getFieldFormat() {
    return this.fieldFormat;
  }

  public void setFieldFormat( String[] fieldFormat ) {
    this.fieldFormat = fieldFormat;
  }

  public int[] getFieldLength() {
    return this.fieldLength;
  }

  public void setFieldLength( int[] fieldLength ) {
    this.fieldLength = fieldLength;
  }

  public int[] getFieldPrecision() {
    return this.fieldPrecision;
  }

  public void setFieldPrecision( int[] fieldPrecision ) {
    this.fieldPrecision = fieldPrecision;
  }

  public String[] getCurrency() {
    return this.currency;
  }

  public void setCurrency( String[] currency ) {
    this.currency = currency;
  }

  public String[] getDecimal() {
    return this.decimal;
  }

  public void setDecimal( String[] decimal ) {
    this.decimal = decimal;
  }

  public String[] getGroup() {
    return this.group;
  }

  public void setGroup( String[] group ) {
    this.group = group;
  }

  public int[] getTrimType() {
    return this.trimType;
  }

  public void setTrimType( int[] trimType ) {
    this.trimType = trimType;
  }

  public String[] getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue( String[] defaultValue ) {
    this.defaultValue = defaultValue;
  }


  public void allocate( int count ) {
    this.fieldName = new String[ count ];
    this.variableName = new String[ count ];
    this.fieldType = new int[ count ];
    this.fieldFormat = new String[ count ];
    this.fieldLength = new int[ count ];
    this.fieldPrecision = new int[ count ];
    this.currency = new String[ count ];
    this.decimal = new String[ count ];
    this.group = new String[ count ];
    this.trimType = new int[ count ];
    this.defaultValue = new String[ count ];
  }

  public void setDefault() {
    allocate( 0 );
  }

  public Object clone() {
    GetSessionVariableMeta clone = (GetSessionVariableMeta) super.clone();
    int count = fieldName.length;
    clone.allocate( count );
    for ( int i = 0; i < count; i++ ) {
      clone.fieldName[ i ] = fieldName[ i ];
      clone.variableName[ i ] = variableName[ i ];
      clone.fieldType[ i ] = fieldType[ i ];
      clone.fieldFormat[ i ] = fieldFormat[ i ];
      clone.currency[ i ] = currency[ i ];
      clone.decimal[ i ] = decimal[ i ];
      clone.group[ i ] = group[ i ];
      clone.fieldLength[ i ] = fieldLength[ i ];
      clone.fieldPrecision[ i ] = fieldPrecision[ i ];
      clone.trimType[ i ] = trimType[ i ];
      clone.defaultValue[ i ] = defaultValue[ i ];
    }
    return clone;
  }

  public void getFields( RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
                         VariableSpace space, Repository repository, IMetaStore metaStore ) throws KettleStepException {
    // determine the maximum length
    int length = -1;
    for ( int i = 0; i < fieldName.length; i++ ) {
      if ( variableName[ i ] != null ) {
        String string = space.environmentSubstitute( variableName[ i ] );
        if ( string.length() > length ) {
          length = string.length();
        }
      }
    }

    RowMetaInterface row = new RowMeta();
    for ( int i = 0; i < fieldName.length; i++ ) {
      ValueMetaInterface valueMeta = new ValueMeta( fieldName[ i ], fieldType[ i ] );
      if ( fieldLength[ i ] < 0 ) {
        valueMeta.setLength( length );
      } else {
        valueMeta.setLength( fieldLength[ i ] );
      }
      if ( fieldPrecision[ i ] >= 0 ) {
        valueMeta.setPrecision( fieldPrecision[ i ] );
      }
      valueMeta.setConversionMask( fieldFormat[ i ] );
      valueMeta.setGroupingSymbol( group[ i ] );
      valueMeta.setDecimalSymbol( decimal[ i ] );
      valueMeta.setCurrencySymbol( currency[ i ] );
      valueMeta.setTrimType( trimType[ i ] );
      valueMeta.setOrigin( name );
      row.addValueMeta( valueMeta );
    }
    inputRowMeta.mergeRowMeta( row );
  }

  public String getXML() {
    StringBuffer xml = new StringBuffer( 300 );
    xml.append( "    <fields>" ).append( Const.CR );
    for ( int i = 0; i < fieldName.length; i++ ) {
      if ( fieldName[ i ] != null && fieldName[ i ].length() != 0 ) {
        xml.append( "      <field>" ).append( Const.CR );
        xml.append( "        " ).append( XMLHandler.addTagValue( "name", fieldName[ i ] ) );
        xml.append( "        " ).append( XMLHandler.addTagValue( "variable", variableName[ i ] ) );
        xml.append( "        " ).append( XMLHandler.addTagValue( "type", ValueMeta.getTypeDesc( fieldType[ i ] ) ) );
        xml.append( "        " ).append( XMLHandler.addTagValue( "format", fieldFormat[ i ] ) );
        xml.append( "        " ).append( XMLHandler.addTagValue( "currency", currency[ i ] ) );
        xml.append( "        " ).append( XMLHandler.addTagValue( "decimal", decimal[ i ] ) );
        xml.append( "        " ).append( XMLHandler.addTagValue( "group", group[ i ] ) );
        xml.append( "        " ).append( XMLHandler.addTagValue( "length", fieldLength[ i ] ) );
        xml.append( "        " ).append( XMLHandler.addTagValue( "precision", fieldPrecision[ i ] ) );
        xml.append( "        " ).append( XMLHandler.addTagValue( "trim_type", ValueMeta.getTrimTypeCode( trimType[ i ] ) ) );
        xml.append( "        " ).append( XMLHandler.addTagValue( "default_value", defaultValue[ i ] ) );
        xml.append( "      </field>" ).append( Const.CR );
      }
    }
    xml.append( "    </fields>" ).append( Const.CR );
    return xml.toString();
  }

  public void loadXML( Node stepNode, List<DatabaseMeta> databases, IMetaStore metaStore ) throws KettleXMLException {
    try {
      Node fields = XMLHandler.getSubNode( stepNode, "fields" );
      int count = XMLHandler.countNodes( fields, "field" );
      allocate( count );

      for ( int i = 0; i < count; i++ ) {
        Node fieldNode = XMLHandler.getSubNodeByNr( fields, "field", i );
        fieldName[ i ] = XMLHandler.getTagValue( fieldNode, "name" );
        variableName[ i ] = XMLHandler.getTagValue( fieldNode, "variable" );
        fieldType[ i ] = ValueMeta.getType( XMLHandler.getTagValue( fieldNode, "type" ) );
        fieldFormat[ i ] = XMLHandler.getTagValue( fieldNode, "format" );
        currency[ i ] = XMLHandler.getTagValue( fieldNode, "currency" );
        decimal[ i ] = XMLHandler.getTagValue( fieldNode, "decimal" );
        group[ i ] = XMLHandler.getTagValue( fieldNode, "group" );
        fieldLength[ i ] = Const.toInt( XMLHandler.getTagValue( fieldNode, "length" ), -1 );
        fieldPrecision[ i ] = Const.toInt( XMLHandler.getTagValue( fieldNode, "precision" ), -1 );
        trimType[ i ] = ValueMeta.getTrimTypeByCode( XMLHandler.getTagValue( fieldNode, "trim_type" ) );
        defaultValue[ i ] = XMLHandler.getTagValue( fieldNode, "default_value" );
        // backward compatibility
        if ( fieldType[ i ] == ValueMetaInterface.TYPE_NONE ) {
          fieldType[ i ] = ValueMetaInterface.TYPE_STRING;
        }
      }
    } catch ( Exception e ) {
      throw new KettleXMLException(
        BaseMessages.getString( PKG, "GetSessionVariable.RuntimeError.UnableToReadXML" ), e );
    }
  }

  public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases )
    throws KettleException {
    try {
      int count = rep.countNrStepAttributes( id_step, "field_name" );
      allocate( count );
      for ( int i = 0; i < count; i++ ) {
        fieldName[ i ] = rep.getStepAttributeString( id_step, i, "field_name" );
        variableName[ i ] = rep.getStepAttributeString( id_step, i, "field_variable" );
        fieldType[ i ] = ValueMeta.getType( rep.getStepAttributeString( id_step, i, "field_type" ) );
        fieldFormat[ i ] = rep.getStepAttributeString( id_step, i, "field_format" );
        currency[ i ] = rep.getStepAttributeString( id_step, i, "field_currency" );
        decimal[ i ] = rep.getStepAttributeString( id_step, i, "field_decimal" );
        group[ i ] = rep.getStepAttributeString( id_step, i, "field_group" );
        fieldLength[ i ] = (int) rep.getStepAttributeInteger( id_step, i, "field_length" );
        fieldPrecision[ i ] = (int) rep.getStepAttributeInteger( id_step, i, "field_precision" );
        trimType[ i ] = ValueMeta.getTrimTypeByCode( rep.getStepAttributeString( id_step, i, "field_trim_type" ) );
        defaultValue[ i ] = rep.getStepAttributeString( id_step, i, "field_default_value" );
        // backward compatibility
        if ( fieldType[ i ] == ValueMetaInterface.TYPE_NONE ) {
          fieldType[ i ] = ValueMetaInterface.TYPE_STRING;
        }
      }
    } catch ( Exception e ) {
      throw new KettleException( BaseMessages.getString(
        PKG, "GetSessionVariable.RuntimeError.UnableToReadRepository" ), e );
    }
  }

  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step )
    throws KettleException {
    try {
      for ( int i = 0; i < fieldName.length; i++ ) {
        if ( fieldName[ i ] != null && fieldName[ i ].length() != 0 ) {
          rep.saveStepAttribute( id_transformation, id_step, i, "field_name", fieldName[ i ] );
          rep.saveStepAttribute( id_transformation, id_step, i, "field_variable", variableName[ i ] );
          rep.saveStepAttribute( id_transformation, id_step, i, "field_type", ValueMeta.getTypeDesc( fieldType[ i ] ) );
          rep.saveStepAttribute( id_transformation, id_step, i, "field_format", fieldFormat[ i ] );
          rep.saveStepAttribute( id_transformation, id_step, i, "field_currency", currency[ i ] );
          rep.saveStepAttribute( id_transformation, id_step, i, "field_decimal", decimal[ i ] );
          rep.saveStepAttribute( id_transformation, id_step, i, "field_group", group[ i ] );
          rep.saveStepAttribute( id_transformation, id_step, i, "field_length", fieldLength[ i ] );
          rep.saveStepAttribute( id_transformation, id_step, i, "field_precision", fieldPrecision[ i ] );
          rep.saveStepAttribute( id_transformation, id_step, i, "field_trim_type",
            ValueMeta.getTrimTypeCode( trimType[ i ] ) );
          rep.saveStepAttribute( id_transformation, id_step, i, "field_default_value", defaultValue[ i ] );
        }
      }
    } catch ( Exception e ) {
      throw new KettleException( BaseMessages.getString(
        PKG, "GetSessionVariable.RuntimeError.UnableToSaveRepository", "" + id_step ), e );
    }
  }

  public void check( List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta,
                     RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info, VariableSpace space,
                     Repository repository, IMetaStore metaStore ) {

    // see if we have all variables specified
    int nrRemarks = remarks.size();
    for ( int i = 0; i < fieldName.length; i++ ) {
      if ( Const.isEmpty( variableName[ i ] ) ) {
        remarks.add( new CheckResult( CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString( PKG, "GetSessionVariable.CheckResult.VariableNotSpecified", fieldName[ i ] ), stepMeta ) );
      }
    }
    if ( remarks.size() == nrRemarks ) {
      remarks.add( new CheckResult( CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString( PKG, "GetSessionVariable.CheckResult.AllVariablesSpecified" ), stepMeta ) );
    }
  }
}
