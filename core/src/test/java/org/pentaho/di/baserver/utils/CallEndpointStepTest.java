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
 * Copyright 2006 - 2021 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.di.baserver.utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.Matchers;
import org.pentaho.di.baserver.utils.inspector.Endpoint;
import org.pentaho.di.baserver.utils.inspector.Inspector;
import org.pentaho.di.baserver.utils.web.Http;
import org.pentaho.di.baserver.utils.web.HttpConnectionHelper;
import org.pentaho.di.baserver.utils.web.HttpParameter;
import org.pentaho.di.baserver.utils.web.Response;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.job.Job;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;


@RunWith( PowerMockRunner.class )
@PowerMockIgnore( "jdk.internal.reflect.*" )
@PrepareForTest( { Inspector.class, HttpConnectionHelper.class, PentahoSessionHolder.class } )
public class CallEndpointStepTest {

  @Mock
  StepMeta stepMeta;
  @Mock
  TransMeta transMeta;
  @Mock
  Trans trans;
  @Mock
  Trans parentTrans;
  @Mock
  Job job;
  @Mock
  CallEndpointData callEndpointData;
  @Mock
  CallEndpointMeta callEndpointMeta;
  @Mock
  Inspector inspector;
  @Mock
  HttpConnectionHelper httpConnectionHelper;
  @Mock
  RowMetaInterface rowMetaInterface;
  @Mock
  Response response;
  @Mock
  Endpoint endpoint;
  @Mock
  IPentahoSession session;
  @Spy
  CallEndpointMeta smiSpy = new CallEndpointMeta();
  @Captor
  ArgumentCaptor<BaseStepData.StepExecutionStatus> captor;
  @Captor
  ArgumentCaptor<List<HttpParameter>> paramListCaptor;
  CallEndpointStep callEndpointStepSpy;

  String moduleName = "moduleName";
  String endpointPath = "/test/path";
  String testMethod = "POST";
  String serverUrl = "serverUrl";
  String username = "username";
  String password = "password";

  String[] fieldNames = { "field1", "field2", "field3", "field4" };
  String[] parameterNames = { "param1", "param2", "param3", "param4" };
  String[] parameterValues = { "value1", "value2", "value3", "value4" };

  @Before
  public void setUpMocks() throws Exception {
    when( stepMeta.getName() ).thenReturn( "someName" );
    when( transMeta.findStep( "someName" ) ).thenReturn( stepMeta );
    doReturn( parentTrans ).when( trans ).getParentTrans();
    when( trans.getParentJob() ).thenReturn( job );
    callEndpointStepSpy = spy( new CallEndpointStep( stepMeta, callEndpointData, 0, transMeta, trans ) );
    doReturn( rowMetaInterface ).when( callEndpointStepSpy ).getInputRowMeta();
    doReturn( rowMetaInterface ).when( rowMetaInterface ).clone();
    doNothing().when( (CallEndpointMeta) smiSpy ).getFields(
      any( RowMetaInterface.class ), anyString(), Matchers.<RowMetaInterface[]>any(),
      any( StepMeta.class ), any( VariableSpace.class ), any( Repository.class ), any( IMetaStore.class ) );
    doNothing().when( callEndpointStepSpy ).putRow( any( RowMetaInterface.class ), any( Object[].class ) );
    doNothing().when( callEndpointStepSpy ).setOutputDone();
    for ( int i = 0; i < fieldNames.length; i++ ) {
      doReturn( parameterValues[i] ).when( callEndpointStepSpy ).getRowValue( any(), eq( i ) );
    }

  }

  @Before
  public void setUpPowerMocks() {
    PowerMockito.mockStatic( Inspector.class );
    PowerMockito.mockStatic( HttpConnectionHelper.class );
    PowerMockito.mockStatic( PentahoSessionHolder.class );
    PowerMockito.when( Inspector.getInstance() ).thenReturn( inspector );
    PowerMockito.when( HttpConnectionHelper.getInstance() ).thenReturn( httpConnectionHelper );
    PowerMockito.when( PentahoSessionHolder.getSession() ).thenReturn( session );
  }

  @Before
  public void setUpMeta() {
    smiSpy.setModuleName( moduleName );
    smiSpy.setEndpointPath( endpointPath );
    smiSpy.setServerURL( serverUrl );
    smiSpy.setUserName( username );
    smiSpy.setPassword( password );
    smiSpy.setFieldName( fieldNames );
    smiSpy.setParameter( parameterNames );
    smiSpy.setHttpMethod( testMethod );
  }

  @Test
  public void testProcessRow1() throws Exception {

    assertTrue( callEndpointStepSpy.init( callEndpointMeta, callEndpointData ) );

    smiSpy.setEndpointFromField( false );
    smiSpy.setBypassingAuthentication( false );

    when( inspector.getEndpoint( eq( moduleName ), eq( endpointPath ), eq( Http.POST ) ) ).thenReturn( endpoint );

    when( endpoint.getParameterType( eq( parameterNames[0] ) ) ).thenReturn( HttpParameter.ParamType.QUERY );
    when( endpoint.getParameterType( eq( parameterNames[1] ) ) ).thenReturn( HttpParameter.ParamType.BODY );
    when( endpoint.getParameterType( eq( parameterNames[2] ) ) ).thenReturn( HttpParameter.ParamType.QUERY );
    when( endpoint.getParameterType( eq( parameterNames[3] ) ) ).thenReturn( HttpParameter.ParamType.BODY );

    when( httpConnectionHelper.invokeEndpoint( eq( serverUrl ), eq( username ), eq( password ),
      eq( moduleName ), eq( endpointPath ), eq( testMethod ), anyList() ) ).thenReturn( response );

    doReturn( new Object[] { "" } ).when( callEndpointStepSpy ).getRow();

    assertTrue( callEndpointStepSpy.processRow( smiSpy, callEndpointData ) );

    verify( inspector ).getEndpoint( eq( moduleName ), eq( endpointPath ), eq( Http.POST ) );
    verify( httpConnectionHelper ).invokeEndpoint( eq( serverUrl ), eq( username ), eq( password ),
      eq( moduleName ), eq( endpointPath ), eq( testMethod ), paramListCaptor.capture() );

    List<HttpParameter> httpParameters = paramListCaptor.getValue();
    assertEquals( 4, httpParameters.size() );
    for ( int i = 0; i < httpParameters.size(); i++ ) {
      HttpParameter httpParameter = httpParameters.get( i );
      assertEquals( parameterNames[i], httpParameter.getName() );
      assertEquals( parameterValues[i], httpParameter.getValue() );
    }
    assertEquals( HttpParameter.ParamType.QUERY, httpParameters.get( 0 ).getParamType() );
    assertEquals( HttpParameter.ParamType.BODY, httpParameters.get( 1 ).getParamType() );
    assertEquals( HttpParameter.ParamType.QUERY, httpParameters.get( 2 ).getParamType() );
    assertEquals( HttpParameter.ParamType.BODY, httpParameters.get( 3 ).getParamType() );
  }

  @Test
  public void testProcessRow2() throws Exception {

    assertTrue( callEndpointStepSpy.init( callEndpointMeta, callEndpointData ) );

    smiSpy.setEndpointFromField( true );
    smiSpy.setBypassingAuthentication( false );

    doReturn( moduleName ).when( callEndpointStepSpy ).getRowValue( any(), eq( moduleName ), eq( "" ) );
    doReturn( endpointPath ).when( callEndpointStepSpy ).getRowValue( any(), eq( endpointPath ), eq( "" ) );
    doReturn( testMethod ).when( callEndpointStepSpy ).getRowValue( any(), eq( testMethod ), eq( "" ) );

    when( inspector.getEndpoint( eq( moduleName ), eq( endpointPath ), eq( Http.POST ) ) ).thenReturn( endpoint );

    when( endpoint.getParameterType( eq( parameterNames[0] ) ) ).thenReturn( HttpParameter.ParamType.QUERY );
    when( endpoint.getParameterType( eq( parameterNames[1] ) ) ).thenReturn( HttpParameter.ParamType.BODY );
    when( endpoint.getParameterType( eq( parameterNames[2] ) ) ).thenReturn( HttpParameter.ParamType.QUERY );
    when( endpoint.getParameterType( eq( parameterNames[3] ) ) ).thenReturn( HttpParameter.ParamType.BODY );

    when( httpConnectionHelper.invokeEndpoint( eq( serverUrl ), eq( username ), eq( password ),
      eq( moduleName ), eq( endpointPath ), eq( testMethod ), anyList() ) ).thenReturn( response );

    doReturn( new Object[] { "" } ).when( callEndpointStepSpy ).getRow();

    assertTrue( callEndpointStepSpy.processRow( smiSpy, callEndpointData ) );

    verify( inspector ).getEndpoint( eq( moduleName ), eq( endpointPath ), eq( Http.POST ) );
    verify( httpConnectionHelper ).invokeEndpoint( eq( serverUrl ), eq( username ), eq( password ),
      eq( moduleName ), eq( endpointPath ), eq( testMethod ), paramListCaptor.capture() );

    List<HttpParameter> httpParameters = paramListCaptor.getValue();
    assertEquals( 4, httpParameters.size() );
    for ( int i = 0; i < httpParameters.size(); i++ ) {
      HttpParameter httpParameter = httpParameters.get( i );
      assertEquals( parameterNames[i], httpParameter.getName() );
      assertEquals( parameterValues[i], httpParameter.getValue() );
    }
    assertEquals( HttpParameter.ParamType.QUERY, httpParameters.get( 0 ).getParamType() );
    assertEquals( HttpParameter.ParamType.BODY, httpParameters.get( 1 ).getParamType() );
    assertEquals( HttpParameter.ParamType.QUERY, httpParameters.get( 2 ).getParamType() );
    assertEquals( HttpParameter.ParamType.BODY, httpParameters.get( 3 ).getParamType() );
  }

  @Test
  public void testProcessRowBypassingAuthentication() throws Exception {

    assertTrue( callEndpointStepSpy.init( callEndpointMeta, callEndpointData ) );

    smiSpy.setEndpointFromField( false );
    smiSpy.setBypassingAuthentication( true );

    when( inspector.getEndpoint( eq( moduleName ), eq( endpointPath ), eq( Http.POST ) ) ).thenReturn( endpoint );

    when( endpoint.getParameterType( eq( parameterNames[0] ) ) ).thenReturn( HttpParameter.ParamType.QUERY );
    when( endpoint.getParameterType( eq( parameterNames[1] ) ) ).thenReturn( HttpParameter.ParamType.BODY );
    when( endpoint.getParameterType( eq( parameterNames[2] ) ) ).thenReturn( HttpParameter.ParamType.QUERY );
    when( endpoint.getParameterType( eq( parameterNames[3] ) ) ).thenReturn( HttpParameter.ParamType.BODY );

    when( httpConnectionHelper.invokeEndpoint( eq( moduleName ), eq( endpointPath ), eq( testMethod ), anyList() ) ).thenReturn( response );

    doReturn( new Object[] { "" } ).when( callEndpointStepSpy ).getRow();

    assertTrue( callEndpointStepSpy.processRow( smiSpy, callEndpointData ) );

    verify( inspector ).getEndpoint( eq( moduleName ), eq( endpointPath ), eq( Http.POST ) );
    verify( httpConnectionHelper ).invokeEndpoint( eq( moduleName ), eq( endpointPath ), eq( testMethod ), paramListCaptor.capture() );

    List<HttpParameter> httpParameters = paramListCaptor.getValue();
    assertEquals( 4, httpParameters.size() );
    for ( int i = 0; i < httpParameters.size(); i++ ) {
      HttpParameter httpParameter = httpParameters.get( i );
      assertEquals( parameterNames[i], httpParameter.getName() );
      assertEquals( parameterValues[i], httpParameter.getValue() );
    }
    assertEquals( HttpParameter.ParamType.QUERY, httpParameters.get( 0 ).getParamType() );
    assertEquals( HttpParameter.ParamType.BODY, httpParameters.get( 1 ).getParamType() );
    assertEquals( HttpParameter.ParamType.QUERY, httpParameters.get( 2 ).getParamType() );
    assertEquals( HttpParameter.ParamType.BODY, httpParameters.get( 3 ).getParamType() );
  }

  @Test
  public void testProcessRowEndpointNull() throws Exception {

    assertTrue( callEndpointStepSpy.init( callEndpointMeta, callEndpointData ) );

    smiSpy.setEndpointFromField( false );
    smiSpy.setBypassingAuthentication( false );

    when( inspector.getEndpoint( eq( moduleName ), eq( endpointPath ), eq( Http.POST ) ) ).thenReturn( null );
    when( httpConnectionHelper.invokeEndpoint( eq( serverUrl ), eq( username ), eq( password ),
      eq( moduleName ), eq( endpointPath ), eq( testMethod ), anyList() ) ).thenReturn( response );

    doReturn( new Object[] { "" } ).when( callEndpointStepSpy ).getRow();

    assertTrue( callEndpointStepSpy.processRow( smiSpy, callEndpointData ) );

    verify( inspector ).getEndpoint( eq( moduleName ), eq( endpointPath ), eq( Http.POST ) );
    verify( httpConnectionHelper ).invokeEndpoint( eq( serverUrl ), eq( username ), eq( password ),
      eq( moduleName ), eq( endpointPath ), eq( testMethod ), paramListCaptor.capture() );

    List<HttpParameter> httpParameters = paramListCaptor.getValue();
    assertNotNull( httpParameters );
    assertEquals( 4, httpParameters.size() );
    for ( int i = 0; i < httpParameters.size(); i++ ) {
      HttpParameter httpParameter = httpParameters.get( i );
      assertEquals( parameterNames[i], httpParameter.getName() );
      assertEquals( parameterValues[i], httpParameter.getValue() );
      assertEquals( HttpParameter.ParamType.NONE, httpParameter.getParamType() );
    }
  }

  @Test
  public void testProcessRowNull() throws Exception {
    doReturn( null ).when( callEndpointStepSpy ).getRow();
    assertFalse( callEndpointStepSpy.processRow( smiSpy, callEndpointData ) );
    verify( callEndpointStepSpy ).setOutputDone();
  }


  @Test
  public void testInit() throws Exception {
    assertTrue( callEndpointStepSpy.init( smiSpy, callEndpointData ) );
    PowerMockito.verifyStatic();
    Inspector.getInstance();
    PowerMockito.verifyStatic();
    HttpConnectionHelper.getInstance();
    verify( inspector ).refreshSettings( eq( serverUrl ), eq( username ), eq( password ) );
  }

  @Test
  public void testDispose() throws Exception {
    callEndpointStepSpy.dispose( callEndpointMeta, callEndpointData );
    verify( callEndpointData, times( 1 ) ).setStatus( any( BaseStepData.StepExecutionStatus.class ) );
    verify( callEndpointData ).setStatus( captor.capture() );
    assertEquals( BaseStepData.StepExecutionStatus.STATUS_DISPOSED.toString(),
      captor.getValue().toString() );
  }
}
