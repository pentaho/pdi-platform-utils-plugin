/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License, version 2 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/gpl-2.0.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 *
 * Copyright 2006 - 2015 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.di.baserver.utils;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class CallEndpointMetaTest {
  private CallEndpointMeta callEndpointMeta;
  private CallEndpointMeta callEndpointMetaSpy;

  @Before
  public void setUp() throws Exception {
    callEndpointMeta = new CallEndpointMeta();
    callEndpointMetaSpy = spy( callEndpointMeta );
  }

  @Test
  public void testGetStep() {
    final StepMeta stepMeta = mock( StepMeta.class );
    doReturn( "name" ).when( stepMeta ).getName();
    final StepDataInterface stepDataInterface = mock( StepDataInterface.class );
    final int cnr = 0;
    final TransMeta transMeta = mock( TransMeta.class );
    doReturn( stepMeta ).when( transMeta ).findStep( anyString() );
    final Trans trans = mock( Trans.class );
    final StepInterface step = callEndpointMeta.getStep( stepMeta, stepDataInterface, cnr, transMeta, trans );
    assertNotNull( step );
  }

  @Test
  public void testSetServerURL() throws Exception {
    assertEquals( "http://localhost:8080/pentaho", callEndpointMeta.getServerURL() );
      callEndpointMeta.setServerURL( "http://127.0.0.1:80/pentaho" );
    assertEquals( "http://127.0.0.1:80/pentaho", callEndpointMeta.getServerURL() );
  }

  @Test
  public void testSetUserName() throws Exception {
    assertEquals( "admin", callEndpointMeta.getUserName() );
    callEndpointMeta.setUserName( "username" );
    assertEquals( "username", callEndpointMeta.getUserName() );
  }

  @Test
  public void testSetPassword() throws Exception {
    assertEquals( "password", callEndpointMeta.getPassword() );
    callEndpointMeta.setPassword( "newPassword" );
    assertEquals( "newPassword", callEndpointMeta.getPassword() );
  }

  @Test
  public void testSetBypassingAuthentication() throws Exception {
    assertFalse( callEndpointMeta.isBypassingAuthentication() );
    callEndpointMeta.setBypassingAuthentication( true );
    assertTrue( callEndpointMeta.isBypassingAuthentication() );
  }

  @Test
  public void testSetModuleName() throws Exception {
    assertEquals( "", callEndpointMeta.getModuleName() );
    callEndpointMeta.setModuleName( "moduleName" );
    assertEquals( "moduleName", callEndpointMeta.getModuleName() );
  }

  @Test
  public void testSetModuleFromField() throws Exception {
    assertFalse( callEndpointMeta.isModuleFromField() );
    callEndpointMeta.setModuleFromField( true );
    assertTrue( callEndpointMeta.isModuleFromField() );
  }

  @Test
  public void testSetEndpointPath() throws Exception {
    assertEquals( "", callEndpointMeta.getEndpointPath() );
    callEndpointMeta.setEndpointPath( "endpointPath" );
    assertEquals( "endpointPath", callEndpointMeta.getEndpointPath() );
  }

  @Test
  public void testSetHttpMethod() throws Exception {
    assertEquals( "", callEndpointMeta.getHttpMethod() );
    callEndpointMeta.setHttpMethod( "GET" );
    assertEquals( "GET", callEndpointMeta.getHttpMethod() );
  }

  @Test
  public void testSetEndpointFromField() throws Exception {
    assertFalse( callEndpointMeta.isEndpointFromField() );
    callEndpointMeta.setEndpointFromField( true );
    assertTrue( callEndpointMeta.isEndpointFromField() );
  }

  @Test
  public void testSetResultField() throws Exception {
    assertEquals( "result", callEndpointMeta.getResultField() );
    callEndpointMeta.setResultField( "resultField" );
    assertEquals( "resultField", callEndpointMeta.getResultField() );
  }

  @Test
  public void testSetFieldName() throws Exception {
    assertEquals( 0, callEndpointMeta.getFieldName().length );
    String[] fieldName = new String[] { "fieldName" };
    callEndpointMeta.setFieldName( fieldName );
    assertEquals( fieldName, callEndpointMeta.getFieldName() );
  }

  @Test
  public void testSetParameter() throws Exception {
    assertEquals( 0, callEndpointMeta.getParameter().length );
    String[] parameter = new String[] { "parameter" };
    callEndpointMeta.setParameter( parameter );
    assertEquals( parameter, callEndpointMeta.getParameter() );
  }

  @Test
  public void testSetDefaultValue() throws Exception {
    assertEquals( 0, callEndpointMeta.getDefaultValue().length );
    String[] defaultValue = new String[] { "defaultValue" };
    callEndpointMeta.setDefaultValue( defaultValue );
    assertEquals( defaultValue, callEndpointMeta.getDefaultValue() );
  }

  @Test
  public void testClone() throws Exception {
    callEndpointMeta.setServerURL( "url" );
    callEndpointMeta.setUserName( "username" );
    callEndpointMeta.setPassword( "password" );
    callEndpointMeta.setBypassingAuthentication( true );
    callEndpointMeta.setModuleName( "moduleName" );
    callEndpointMeta.setModuleFromField( true );
    callEndpointMeta.setEndpointPath( "endpointPath" );
    callEndpointMeta.setHttpMethod( "GET");
    callEndpointMeta.setEndpointFromField( true );
    callEndpointMeta.setResultField( "resultField" );
    callEndpointMeta.setStatusCodeField( "statusCodeField" );
    callEndpointMeta.setResponseTimeField( "responseTimeField" );
    callEndpointMeta.setFieldName( new String[] { "fieldName" } );
    callEndpointMeta.setParameter( new String[] { "parameter" } );
    callEndpointMeta.setDefaultValue( new String[] { "defaultValue" } );

    CallEndpointMeta result = (CallEndpointMeta) callEndpointMeta.clone();

    assertEquals( "url" , result.getServerURL() );
    assertEquals( "username" , result.getUserName() );
    assertEquals( "password" , result.getPassword() );
    assertTrue( result.isBypassingAuthentication() );
    assertEquals( "moduleName" , result.getModuleName() );
    assertTrue( result.isModuleFromField() );
    assertEquals( "endpointPath" , result.getEndpointPath() );
    assertEquals( "GET" , result.getHttpMethod() );
    assertTrue( result.isEndpointFromField() );
    assertEquals( "resultField" , result.getResultField() );
    assertEquals( "statusCodeField" , result.getStatusCodeField() );
    assertEquals( "responseTimeField" , result.getResponseTimeField() );
    assertEquals( "fieldName", result.getFieldName()[0] );
    assertEquals( "parameter", result.getParameter()[0] );
    assertEquals( "defaultValue", result.getDefaultValue()[0] );
  }

  @Test
  public void testGetStepData() {
    final StepDataInterface stepData = callEndpointMeta.getStepData();
    assertNotNull( stepData );
  }

  @Test
  public void testAllocate() {
    final int size = 100;
    callEndpointMeta.allocate( size );
    assertNotNull( callEndpointMeta.getFieldName() );
    assertEquals( size, callEndpointMeta.getFieldName().length );
    assertNotNull( callEndpointMeta.getParameter() );
    assertEquals( size, callEndpointMeta.getParameter().length );
    assertNotNull( callEndpointMeta.getDefaultValue() );
    assertEquals( size, callEndpointMeta.getDefaultValue().length );
  }

  @Test
  public void setDefault() {
    callEndpointMetaSpy.setDefault();

    assertEquals( "http://localhost:8080/pentaho", callEndpointMetaSpy.getServerURL() );
    assertEquals( "admin", callEndpointMetaSpy.getUserName() );
    assertEquals( "password", callEndpointMetaSpy.getPassword() );
    assertEquals( false, callEndpointMetaSpy.isBypassingAuthentication() );
    assertEquals( "", callEndpointMetaSpy.getModuleName() );
    assertEquals( "", callEndpointMetaSpy.getEndpointPath() );
    assertEquals( "", callEndpointMetaSpy.getHttpMethod() );
    assertEquals( false, callEndpointMetaSpy.isModuleFromField() );
    assertEquals( false, callEndpointMetaSpy.isEndpointFromField() );
    assertEquals( "result", callEndpointMetaSpy.getResultField() );
    assertEquals( "", callEndpointMetaSpy.getStatusCodeField() );
    assertEquals( "", callEndpointMetaSpy.getResponseTimeField() );

    verify( callEndpointMetaSpy, times( 1 ) ).allocate( 0 );
  }

  @Test
  public void testGetXML() {
    final String xml = callEndpointMeta.getXML();
    assertNotNull( xml );
    assertTrue( xml.contains( "serverUrl" ) );
    assertTrue( xml.contains( "userName" ) );
    assertTrue( xml.contains( "password" ) );
    assertTrue( xml.contains( "isBypassingAuthentication" ) );
    assertTrue( xml.contains( "moduleName" ) );
    assertTrue( xml.contains( "isModuleFromField" ) );
    assertTrue( xml.contains( "endpointPath" ) );
    assertTrue( xml.contains( "httpMethod" ) );
    assertTrue( xml.contains( "isEndpointFromField" ) );
    assertTrue( xml.contains( "resultField" ) );
    assertTrue( xml.contains( "statusCodeField" ) );
    assertTrue( xml.contains( "responseTimeField" ) );
    assertTrue( xml.contains( "fields" ) );
  }

  @Test
  public void testLoadXML() throws Exception {
    final Node stepNode = mock( Node.class );
    final NodeList childrenStepNode = mock( NodeList.class );
    doReturn( 0 ).when( childrenStepNode ).getLength();
    doReturn( childrenStepNode ).when( stepNode).getChildNodes();
    final List databases = mock( List.class );
    final IMetaStore metaStore = mock( IMetaStore.class );
    callEndpointMetaSpy.loadXML( stepNode, databases, metaStore );

    verify( callEndpointMetaSpy ).allocate( anyInt() );
  }

  @Test
  public void testReadRep() throws Exception {
    final Repository rep = mock( Repository.class );
    final IMetaStore metaStore = mock( IMetaStore.class );
    final ObjectId id_step = mock( ObjectId.class );
    final List databases = mock( List.class );

    doReturn( 1 ).when( rep ).countNrStepAttributes( eq( id_step ), anyString() );
    callEndpointMetaSpy.readRep( rep, metaStore, id_step, databases );

    verify( rep, times( 9 ) ).getStepAttributeString( eq( id_step ), anyString() );
    verify( rep, times( 3 ) ).getStepAttributeString( eq( id_step ), anyInt(), anyString() );
    verify( rep, times( 3 ) ).getStepAttributeBoolean( eq( id_step ), anyInt(), anyString(), anyBoolean() );
    verify( callEndpointMetaSpy ).allocate( anyInt() );
  }

  @Test
  public void testSaveRep() throws Exception {
    final Repository rep = mock( Repository.class );
    final IMetaStore metaStore = mock( IMetaStore.class );
    final ObjectId id_step = mock( ObjectId.class );
    final ObjectId id_transformation = mock( ObjectId.class );

    callEndpointMeta.allocate( 1 );
    callEndpointMeta.saveRep( rep, metaStore, id_transformation, id_step );

    verify( rep, times( 9 ) ).saveStepAttribute( eq( id_transformation ), eq( id_step ), anyString(), anyString() );
    verify( rep, times( 3 ) ).saveStepAttribute( eq( id_transformation ), eq( id_step ), anyInt(), anyString(), anyBoolean() );
    verify( rep, times( 3 ) ).saveStepAttribute( eq( id_transformation ), eq( id_step ), anyInt(), anyString(), anyString() );
  }

  @Test
  public void testCheck() {
    List remarks = mock( List.class );
    TransMeta transMeta = mock( TransMeta.class );
    StepMeta stepMeta = mock( StepMeta.class );
    RowMetaInterface prev = mock( RowMetaInterface.class );
    String[] input = new String[1];
    String[] output = new String[1];
    RowMetaInterface info = mock( RowMetaInterface.class );
    VariableSpace space = mock( VariableSpace.class );
    Repository repository = mock( Repository.class );
    IMetaStore metaStore = mock( IMetaStore.class );

    callEndpointMeta.check( remarks, transMeta, stepMeta, prev, input, output, info, space, repository, metaStore );
    verify( remarks, times( 2 ) ).add( any( CheckResultInterface.class ) );
  }

  @Test
  public void testGetFields() throws Exception {
    RowMetaInterface inputRowMeta = mock( RowMetaInterface.class );
    String name = "name";
    RowMetaInterface[] info = new RowMetaInterface[1];
    StepMeta nextStep = mock( StepMeta.class );
    VariableSpace space = mock( VariableSpace.class );
    Repository repository = mock( Repository.class );
    IMetaStore metaStore = mock( IMetaStore.class );

    callEndpointMeta.setResultField( "rf" );
    callEndpointMeta.setStatusCodeField( "scf" );
    callEndpointMeta.setResponseTimeField( "rtf" );
    callEndpointMeta.getFields( inputRowMeta, name, info, nextStep, space, repository, metaStore );

    verify( inputRowMeta, times( 3 ) ).addValueMeta( any( ValueMetaInterface.class ) );
  }
}
