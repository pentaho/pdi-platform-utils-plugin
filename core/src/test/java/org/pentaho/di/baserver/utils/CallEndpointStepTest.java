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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
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

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.StrictStubs.class )
public class CallEndpointStepTest {

  @Mock
  StepMeta stepMeta;
  @Mock
  TransMeta transMeta;
  @Mock
  Trans trans;
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

  private MockedStatic<Inspector> inspectorMockedStatic;
  private MockedStatic<HttpConnectionHelper> httpConnectionHelperMockedStatic;
  private MockedStatic<PentahoSessionHolder> sessionMockedStatic;

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
    callEndpointStepSpy = spy( new CallEndpointStep( stepMeta, callEndpointData, 0, transMeta, trans ) );
    doReturn( rowMetaInterface ).when( callEndpointStepSpy ).getInputRowMeta();
    doReturn( rowMetaInterface ).when( rowMetaInterface ).clone();
    doNothing().when( (CallEndpointMeta) smiSpy ).getFields(
      Mockito.<RowMetaInterface>any(), any(), ArgumentMatchers.<RowMetaInterface[]>any(),
      Mockito.<StepMeta>any(), Mockito.<VariableSpace>any(), Mockito.<Repository>any(), Mockito.<IMetaStore>any() );
    doNothing().when( callEndpointStepSpy ).putRow( Mockito.<RowMetaInterface>any(), Mockito.<Object[]>any() );
    doNothing().when( callEndpointStepSpy ).setOutputDone();
    for ( int i = 0; i < fieldNames.length; i++ ) {
      doReturn( parameterValues[i] ).when( callEndpointStepSpy ).getRowValue( any(), eq( i ) );
    }

  }

  @Before
  public void setUpPowerMocks() {
    inspectorMockedStatic = Mockito.mockStatic( Inspector.class );
    when( Inspector.getInstance() ).thenReturn( inspector );
    httpConnectionHelperMockedStatic = Mockito.mockStatic( HttpConnectionHelper.class );
    when( HttpConnectionHelper.getInstance() ).thenReturn( httpConnectionHelper );
    sessionMockedStatic = Mockito.mockStatic( PentahoSessionHolder.class );
    when( PentahoSessionHolder.getSession() ).thenReturn( session );
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

  @After
  public void afterEach(){
    inspectorMockedStatic.close();
    httpConnectionHelperMockedStatic.close();
    sessionMockedStatic.close();
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
    inspectorMockedStatic.verify( Inspector::getInstance, times( 1 ) );
    httpConnectionHelperMockedStatic.verify( HttpConnectionHelper::getInstance, times( 1 ) );
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
