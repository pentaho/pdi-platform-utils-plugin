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

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
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

/**
 * @author Marco Vala
 */
@Step( id = "CallEndpointStep",
  name = "CallEndpointMeta.Name",
  image = "icons/callendpoint.png",
  description = "CallEndpointMeta.Description",
  i18nPackageName = "pt.webdetails.di.baserverutils",
  categoryDescription = "BAServerUtils.Category",
  isSeparateClassLoaderNeeded = true )
public class CallEndpointMeta extends BaseStepMeta implements StepMetaInterface {
  private static Class<?> PKG = CallEndpointMeta.class; // for i18n purposes, needed by Translator2!!

  // region Fields

  private String serverURL;
  private String userName;
  private String password;
  private boolean isBypassingAuthentication;
  private String moduleName;
  private boolean isModuleFromField;
  private String moduleField;
  private String serviceName;
  private boolean isServiceFromField;
  private String serviceField;
  private String resultField;
  private String statusCodeField;
  private String responseTimeField;
  private String[] fieldName;
  private String[] parameter;
  private String[] defaultValue;

  // endregion

  // region Getters / Setters

  public String getServerURL() {
    return this.serverURL;
  }

  public void setServerURL( String serverURL ) {
    this.serverURL = serverURL;
  }

  public String getUserName() {
    return this.userName;
  }

  public void setUserName( String userName ) {
    this.userName = userName;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword( String password ) {
    this.password = password;
  }

  public boolean isBypassingAuthentication() {
    return this.isBypassingAuthentication;
  }

  public void setBypassingAuthentication( boolean bypassingAuthentication ) {
    this.isBypassingAuthentication = bypassingAuthentication;
  }

  public String getModuleName() {
    return this.moduleName;
  }

  public void setModuleName( String moduleName ) {
    this.moduleName = moduleName;
  }

  public boolean isModuleFromField() {
    return this.isModuleFromField;
  }

  public void setModuleFromField( boolean isModuleFromField ) {
    this.isModuleFromField = isModuleFromField;
  }

  public String getModuleField() {
    return this.moduleField;
  }

  public void setModuleField( String moduleField ) {
    this.moduleField = moduleField;
  }

  public String getServiceName() {
    return this.serviceName;
  }

  public void setServiceName( String serviceName ) {
    this.serviceName = serviceName;
  }

  public boolean isServiceFromField() {
    return this.isServiceFromField;
  }

  public void setServiceFromField( boolean isServiceFromField ) {
    this.isServiceFromField = isServiceFromField;
  }

  public String getServiceField() {
    return this.serviceField;
  }

  public void setServiceField( String serviceField ) {
    this.serviceField = serviceField;
  }

  public String getResultField() {
    return this.resultField;
  }

  public void setResultField( String resultField ) {
    this.resultField = resultField;
  }

  public String getStatusCodeField() {
    return this.statusCodeField;
  }

  public void setStatusCodeField( String statusCodeField ) {
    this.statusCodeField = statusCodeField;
  }

  public String getResponseTimeField() {
    return this.responseTimeField;
  }

  public void setResponseTimeField( String responseTimeField ) {
    this.responseTimeField = responseTimeField;
  }

  public String[] getFieldName() {
    return this.fieldName;
  }

  public void setFieldName( String[] fieldName ) {
    this.fieldName = fieldName;
  }

  public String[] getParameter() {
    return this.parameter;
  }

  public void setParameter( String[] fieldValue ) {
    this.parameter = fieldValue;
  }

  public String[] getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue( String[] defaultValue ) {
    this.defaultValue = defaultValue;
  }

  // endregion

  // region Constructors

  public CallEndpointMeta() {
    super(); // allocate BaseStepMeta
    setDefault();
  }

  // endregion

  @Override
  public StepInterface getStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr,
                                TransMeta transMeta, Trans trans ) {
    return new CallEndpointStep( stepMeta, stepDataInterface, cnr, transMeta, trans );
  }

  @Override
  public StepDataInterface getStepData() {
    return new CallEndpointData();
  }

  public StepDialogInterface getDialog( Shell shell, StepMetaInterface meta, TransMeta transMeta, String name ) {
    return new CallEndpointDialog( shell, meta, transMeta, name );
  }

  public void allocate( int count ) {
    this.fieldName = new String[ count ];
    this.parameter = new String[ count ];
    this.defaultValue = new String[ count ];
  }

  public void setDefault() {
    this.serverURL = "";
    this.userName = "";
    this.password = "";
    this.isBypassingAuthentication = false;
    this.moduleName = "";
    this.isModuleFromField = false;
    this.moduleField = "";
    this.serviceName = "";
    this.isServiceFromField = false;
    this.serviceField = "";
    this.resultField = "Result";
    this.statusCodeField = "Status code";
    this.responseTimeField = "Response time";
    allocate( 0 );
  }

  public Object clone() {
    CallEndpointMeta clone = (CallEndpointMeta) super.clone();
    clone.serverURL = this.serverURL;
    clone.userName = this.userName;
    clone.password = this.password;
    clone.isBypassingAuthentication = this.isBypassingAuthentication;
    clone.moduleName = this.moduleName;
    clone.isModuleFromField = this.isModuleFromField;
    clone.moduleField = this.moduleField;
    clone.serviceName = this.serviceName;
    clone.isServiceFromField = this.isServiceFromField;
    clone.serviceField = this.serviceField;
    clone.resultField = this.resultField;
    clone.statusCodeField = this.statusCodeField;
    clone.responseTimeField = this.responseTimeField;
    int count = this.fieldName.length;
    clone.allocate( count );
    for ( int i = 0; i < count; i++ ) {
      clone.fieldName[ i ] = this.fieldName[ i ];
      clone.parameter[ i ] = this.parameter[ i ];
      clone.defaultValue[ i ] = this.defaultValue[ i ];
    }
    return clone;
  }

  public String getXML() {
    StringBuffer xml = new StringBuffer( 300 );
    xml.append( "    " ).append( XMLHandler.addTagValue( "serverUrl", this.serverURL ) );
    xml.append( "    " ).append( XMLHandler.addTagValue( "userName", this.userName ) );
    xml.append( "    " ).append( XMLHandler.addTagValue( "password", this.password ) );
    xml.append( "    " ).append( XMLHandler.addTagValue( "isBypassingAuthentication", this.isBypassingAuthentication ) );
    xml.append( "    " ).append( XMLHandler.addTagValue( "moduleName", this.moduleName ) );
    xml.append( "    " ).append( XMLHandler.addTagValue( "isModuleFromField", this.isModuleFromField ) );
    xml.append( "    " ).append( XMLHandler.addTagValue( "moduleField", this.moduleField ) );
    xml.append( "    " ).append( XMLHandler.addTagValue( "serviceName", this.serviceName ) );
    xml.append( "    " ).append( XMLHandler.addTagValue( "isServiceFromField", this.isServiceFromField ) );
    xml.append( "    " ).append( XMLHandler.addTagValue( "serviceField", this.serviceField ) );
    xml.append( "    " ).append( XMLHandler.addTagValue( "resultField", this.resultField ) );
    xml.append( "    " ).append( XMLHandler.addTagValue( "statusCodeField", this.statusCodeField ) );
    xml.append( "    " ).append( XMLHandler.addTagValue( "responseTimeField", this.responseTimeField ) );
    xml.append( "    <fields>" ).append( Const.CR );
    for ( int i = 0; i < this.fieldName.length; i++ ) {
      xml.append( "      <field>" ).append( Const.CR );
      xml.append( "        " ).append( XMLHandler.addTagValue( "fieldName", this.fieldName[ i ] ) );
      xml.append( "        " ).append( XMLHandler.addTagValue( "parameter", this.parameter[ i ] ) );
      xml.append( "        " ).append( XMLHandler.addTagValue( "defaultValue", this.defaultValue[ i ] ) );
      xml.append( "        </field>" ).append( Const.CR );
    }
    xml.append( "    </fields>" ).append( Const.CR );
    return xml.toString();
  }

  public void loadXML( Node stepNode, List<DatabaseMeta> databases, IMetaStore metaStore ) throws KettleXMLException {
    try {
      this.serverURL = XMLHandler.getTagValue( stepNode, "serverUrl" );
      if ( this.serverURL == null ) {
        this.serverURL = "";
      }
      this.userName = XMLHandler.getTagValue( stepNode, "userName" );
      if ( this.userName == null ) {
        this.userName = "";
      }
      this.password = XMLHandler.getTagValue( stepNode, "password" );
      if ( this.password == null ) {
        this.password = "";
      }
      this.isBypassingAuthentication = "Y".equalsIgnoreCase( XMLHandler.getTagValue( stepNode, "isBypassingAuthentication" ) );
      this.moduleName = XMLHandler.getTagValue( stepNode, "moduleName" );
      if ( this.moduleName == null ) {
        this.moduleName = "";
      }
      this.isModuleFromField = "Y".equalsIgnoreCase( XMLHandler.getTagValue( stepNode, "isModuleFromField" ) );
      this.moduleField = XMLHandler.getTagValue( stepNode, "moduleField" );
      if ( this.moduleField == null ) {
        this.moduleField = "";
      }
      this.serviceName = XMLHandler.getTagValue( stepNode, "serviceName" );
      if ( this.serviceName == null ) {
        this.serviceName = "";
      }
      this.isServiceFromField = "Y".equalsIgnoreCase( XMLHandler.getTagValue( stepNode, "isServiceFromField" ) );
      this.serviceField = XMLHandler.getTagValue( stepNode, "serviceField" );
      if ( this.serviceField == null ) {
        this.serviceField = "";
      }
      this.resultField = XMLHandler.getTagValue( stepNode, "resultField" );
      if ( this.resultField == null ) {
        this.resultField = "";
      }
      this.statusCodeField = XMLHandler.getTagValue( stepNode, "statusCodeField" );
      if ( this.statusCodeField == null ) {
        this.statusCodeField = "";
      }
      this.responseTimeField = XMLHandler.getTagValue( stepNode, "responseTimeField" );
      if ( this.responseTimeField == null ) {
        this.responseTimeField = "";
      }
      Node fields = XMLHandler.getSubNode( stepNode, "fields" );
      int count = XMLHandler.countNodes( fields, "field" );
      allocate( count );
      for ( int i = 0; i < count; i++ ) {
        Node fieldNode = XMLHandler.getSubNodeByNr( fields, "field", i );
        this.fieldName[ i ] = XMLHandler.getTagValue( fieldNode, "fieldName" );
        this.parameter[ i ] = XMLHandler.getTagValue( fieldNode, "parameter" );
        this.defaultValue[ i ] = XMLHandler.getTagValue( fieldNode, "defaultValue" );
      }
    } catch ( Exception e ) {
      throw new KettleXMLException(
        BaseMessages.getString( PKG, "BAServerUtils.RuntimeError.UnableToReadXML" ), e );
    }
  }

  public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases )
    throws KettleException {
    try {
      this.serverURL = rep.getStepAttributeString( id_step, "serverUrl" );
      this.userName = rep.getStepAttributeString( id_step, "userName" );
      this.password = rep.getStepAttributeString( id_step, "password" );
      this.isBypassingAuthentication = rep.getStepAttributeBoolean( id_step, 0, "isBypassingAuthentication", false );
      this.moduleName = rep.getStepAttributeString( id_step, "moduleName" );
      this.isModuleFromField = rep.getStepAttributeBoolean( id_step, 0, "isModuleFromField", false );
      this.moduleField = rep.getStepAttributeString( id_step, "moduleField" );
      this.serviceName = rep.getStepAttributeString( id_step, "serviceName" );
      this.isServiceFromField = rep.getStepAttributeBoolean( id_step, 0, "isServiceFromField", false );
      this.serviceField = rep.getStepAttributeString( id_step, "serviceField" );
      this.resultField = rep.getStepAttributeString( id_step, "resultField" );
      this.statusCodeField = rep.getStepAttributeString( id_step, "statusCodeField" );
      this.responseTimeField = rep.getStepAttributeString( id_step, "responseTimeField" );
      int count = rep.countNrStepAttributes( id_step, "queryParameters_fieldName" );
      allocate( count );
      for ( int i = 0; i < count; i++ ) {
        this.fieldName[ i ] = rep.getStepAttributeString( id_step, i, "queryParameters_fieldName" );
        this.parameter[ i ] = rep.getStepAttributeString( id_step, i, "queryParameters_parameter" );
        this.defaultValue[ i ] = rep.getStepAttributeString( id_step, i, "queryParameters_defaultValue" );
      }
    } catch ( Exception e ) {
      throw new KettleException( BaseMessages.getString(
        PKG, "BAServerUtils.RuntimeError.UnableToReadRepository" ), e );
    }
  }

  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step )
    throws KettleException {
    try {
      rep.saveStepAttribute( id_transformation, id_step, "serverUrl", this.serverURL );
      rep.saveStepAttribute( id_transformation, id_step, "userName", this.userName );
      rep.saveStepAttribute( id_transformation, id_step, "password", this.password );
      rep.saveStepAttribute( id_transformation, id_step, 0, "isBypassingAuthentication", this.isBypassingAuthentication );
      rep.saveStepAttribute( id_transformation, id_step, "moduleName", this.moduleName );
      rep.saveStepAttribute( id_transformation, id_step, 0, "isModuleFromField", this.isModuleFromField );
      rep.saveStepAttribute( id_transformation, id_step, "moduleField", this.moduleField );
      rep.saveStepAttribute( id_transformation, id_step, "serviceName", this.serviceName );
      rep.saveStepAttribute( id_transformation, id_step, 0, "isServiceFromField", this.isServiceFromField );
      rep.saveStepAttribute( id_transformation, id_step, "serviceField", this.serviceField );
      rep.saveStepAttribute( id_transformation, id_step, "result_field", this.resultField );
      rep.saveStepAttribute( id_transformation, id_step, "status_code_field", this.statusCodeField );
      rep.saveStepAttribute( id_transformation, id_step, "response_time_field", this.responseTimeField );
      for ( int i = 0; i < fieldName.length; i++ ) {
        rep.saveStepAttribute( id_transformation, id_step, i, "queryParameters_fieldName",
          Const.isEmpty( fieldName[ i ] ) ? "" : fieldName[ i ] );
        rep.saveStepAttribute( id_transformation, id_step, i, "queryParameters_parameter", parameter[ i ] );
        rep.saveStepAttribute( id_transformation, id_step, i, "queryParameters_defaultValue", defaultValue[ i ] );
      }
    } catch ( Exception e ) {
      throw new KettleException( BaseMessages.getString(
        PKG, "BAServerUtils.RuntimeError.UnableToSaveRepository", "" + id_step ), e );
    }
  }

  public void check( List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta,
                     RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info, VariableSpace space,
                     Repository repository, IMetaStore metaStore ) {

    // see if we have fields from previous steps
    if ( prev == null || prev.size() == 0 ) {
      remarks.add( new CheckResult( CheckResultInterface.TYPE_RESULT_WARNING,
        BaseMessages.getString( PKG, "BAServerUtils.CheckResult.NotReceivingFieldsFromPreviousSteps" ),
        stepMeta ) );
    } else {
      remarks.add( new CheckResult( CheckResultInterface.TYPE_RESULT_OK, BaseMessages
        .getString( PKG, "BAServerUtils.CheckResult.ReceivingFieldsFromPreviousSteps", "" + prev.size() ),
        stepMeta ) );
    }

    // see if we have input streams leading to this step
    if ( input.length > 0 ) {
      remarks.add( new CheckResult( CheckResultInterface.TYPE_RESULT_OK,
        BaseMessages.getString( PKG, "BAServerUtils.CheckResult.ReceivingInfoFromOtherSteps" ), stepMeta ) );
    } else {
      remarks.add( new CheckResult( CheckResultInterface.TYPE_RESULT_ERROR,
        BaseMessages.getString( PKG, "BAServerUtils.CheckResult.NotReceivingInfoFromOtherSteps" ),
        stepMeta ) );
    }
  }

  @Override
  public void getFields( RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
                         VariableSpace space, Repository repository, IMetaStore metaStore ) throws KettleStepException {
    ValueMetaInterface vmi;

    if ( !Const.isEmpty( this.resultField ) ) {
      vmi = new ValueMeta( space.environmentSubstitute( this.resultField ), ValueMeta.TYPE_STRING );
      vmi.setOrigin( name );
      inputRowMeta.addValueMeta( vmi );
    }

    if ( !Const.isEmpty( this.statusCodeField ) ) {
      vmi = new ValueMeta( space.environmentSubstitute( this.statusCodeField ), ValueMeta.TYPE_INTEGER );
      vmi.setOrigin( name );
      inputRowMeta.addValueMeta( vmi );
    }

    if ( !Const.isEmpty( this.responseTimeField ) ) {
      vmi = new ValueMeta( space.environmentSubstitute( this.responseTimeField ), ValueMeta.TYPE_INTEGER );
      vmi.setOrigin( name );
      inputRowMeta.addValueMeta( vmi );
    }
  }
}
