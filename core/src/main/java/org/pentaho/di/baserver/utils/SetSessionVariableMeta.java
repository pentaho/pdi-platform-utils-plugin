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
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
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

@Step( id = "SetSessionVariableStep",
  name = "SetSessionVariableMeta.Name",
  image = "icons/setsessionvariable.svg",
  description = "SetSessionVariableMeta.Description",
  i18nPackageName = "pt.webdetails.di.baserverutils",
  categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.BAServer",
  isSeparateClassLoaderNeeded = true,
  documentationUrl = "https://pentaho-community.atlassian.net/wiki/display/EAI/Set+Session+Variables" )
public class SetSessionVariableMeta extends BaseStepMeta implements StepMetaInterface {
  private static Class<?> PKG = SetSessionVariableMeta.class; // for i18n purposes, needed by Translator2!!

  private String[] fieldName;
  private String[] variableName;
  private String[] defaultValue;
  private boolean useFormatting;


  public SetSessionVariableMeta() {
    super(); // allocate BaseStepMeta
  }

  public StepInterface getStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr,
                                TransMeta transMeta, Trans trans ) {
    return new SetSessionVariableStep( stepMeta, stepDataInterface, cnr, transMeta, trans );
  }

  public StepDataInterface getStepData() {
    return new SetSessionVariableData();
  }

  public StepDialogInterface getDialog( Shell shell, StepMetaInterface meta, TransMeta transMeta, String name ) {
    return new SetSessionVariableDialog( shell, meta, transMeta, name );
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

  public void setVariableName( String[] fieldValue ) {
    this.variableName = fieldValue;
  }

  public String[] getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue( String[] defaultValue ) {
    this.defaultValue = defaultValue;
  }

  public boolean isUsingFormatting() {
    return this.useFormatting;
  }

  public void setUseFormatting( boolean useFormatting ) {
    this.useFormatting = useFormatting;
  }


  public void allocate( int count ) {
    this.fieldName = new String[ count ];
    this.variableName = new String[ count ];
    this.defaultValue = new String[ count ];
  }

  public void setDefault() {
    allocate( 0 );
    this.useFormatting = true;
  }

  public Object clone() {
    SetSessionVariableMeta clone = (SetSessionVariableMeta) super.clone();
    int count = this.fieldName.length;
    clone.allocate( count );
    for ( int i = 0; i < count; i++ ) {
      clone.fieldName[ i ] = this.fieldName[ i ];
      clone.variableName[ i ] = this.variableName[ i ];
      clone.defaultValue[ i ] = this.defaultValue[ i ];
    }
    clone.useFormatting = this.useFormatting;
    return clone;
  }

  public String getXML() {
    StringBuffer xml = new StringBuffer( 150 );
    xml.append( "    <fields>" ).append( Const.CR );
    for ( int i = 0; i < fieldName.length; i++ ) {
      xml.append( "      <field>" ).append( Const.CR );
      xml.append( "        " ).append( XMLHandler.addTagValue( "name", fieldName[ i ] ) );
      xml.append( "        " ).append( XMLHandler.addTagValue( "variable", variableName[ i ] ) );
      xml.append( "        " ).append( XMLHandler.addTagValue( "default_value", defaultValue[ i ] ) );
      xml.append( "        </field>" ).append( Const.CR );
    }
    xml.append( "      </fields>" ).append( Const.CR );
    xml.append( "    " ).append( XMLHandler.addTagValue( "use_formatting", useFormatting ) );
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
        defaultValue[ i ] = XMLHandler.getTagValue( fieldNode, "default_value" );
      }
      useFormatting = "Y".equalsIgnoreCase( XMLHandler.getTagValue( stepNode, "use_formatting" ) );
    } catch ( Exception e ) {
      throw new KettleXMLException(
        BaseMessages.getString( PKG, "SetSessionVariable.RuntimeError.UnableToReadXML" ), e );
    }
  }

  public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases )
    throws KettleException {
    try {
      int count = rep.countNrStepAttributes( id_step, "field_name" );
      allocate( count );
      for ( int i = 0; i < count; i++ ) {
        fieldName[ i ] = rep.getStepAttributeString( id_step, i, "field_name" );
        variableName[ i ] = rep.getStepAttributeString( id_step, i, "field_variable_name" );
        defaultValue[ i ] = rep.getStepAttributeString( id_step, i, "field_default_value" );
      }
      useFormatting = rep.getStepAttributeBoolean( id_step, 0, "use_formatting", false );
    } catch ( Exception e ) {
      throw new KettleException( BaseMessages.getString(
        PKG, "SetSessionVariable.RuntimeError.UnableToReadRepository" ), e );
    }
  }

  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step )
    throws KettleException {
    try {
      for ( int i = 0; i < fieldName.length; i++ ) {
        rep.saveStepAttribute( id_transformation, id_step, i, "field_name",
          Const.isEmpty( fieldName[ i ] ) ? "" : fieldName[ i ] );
        rep.saveStepAttribute( id_transformation, id_step, i, "field_variable_name", variableName[ i ] );
        rep.saveStepAttribute( id_transformation, id_step, i, "field_default_value", defaultValue[ i ] );
      }
      rep.saveStepAttribute( id_transformation, id_step, 0, "use_formatting", useFormatting );
    } catch ( Exception e ) {
      throw new KettleException( BaseMessages.getString(
        PKG, "SetSessionVariable.RuntimeError.UnableToSaveRepository", "" + id_step ), e );
    }
  }

  public void check( List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta,
                     RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info, VariableSpace space,
                     Repository repository, IMetaStore metaStore ) {

    // see if we have fields from setTop steps
    if ( prev == null || prev.size() == 0 ) {
      remarks.add( new CheckResult( CheckResultInterface.TYPE_RESULT_WARNING,
        BaseMessages.getString( PKG, "SetSessionVariable.CheckResult.NotReceivingFieldsFromPreviousSteps" ),
        stepMeta ) );
    } else {
      remarks.add( new CheckResult( CheckResultInterface.TYPE_RESULT_OK, BaseMessages
        .getString( PKG, "SetSessionVariable.CheckResult.ReceivingFieldsFromPreviousSteps", "" + prev.size() ),
        stepMeta ) );
    }

    // see if we have input streams leading to this step
    if ( input.length > 0 ) {
      remarks.add( new CheckResult( CheckResultInterface.TYPE_RESULT_OK,
        BaseMessages.getString( PKG, "SetSessionVariable.CheckResult.ReceivingInfoFromOtherSteps" ), stepMeta ) );
    } else {
      remarks.add( new CheckResult( CheckResultInterface.TYPE_RESULT_ERROR,
        BaseMessages.getString( PKG, "SetSessionVariable.CheckResult.NotReceivingInfoFromOtherSteps" ),
        stepMeta ) );
    }
  }
}
