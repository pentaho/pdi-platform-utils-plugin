package org.pentaho.di.baserver.utils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.job.Job;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SetSessionVariableStepTest {
  private static final String SIMULATED_SESSION_PREFIX = "_FAKE_SESSION_";

  SetSessionVariableStep setSessionVariableStep, setSessionVariableStepSpy;
  SetSessionVariableData setSessionVariableData;


  @Before
  public void setUp() throws Exception {
    StepMeta stepMeta = mock( StepMeta.class );
    TransMeta transMeta = mock( TransMeta.class );
    Trans trans = mock( Trans.class );
    when( stepMeta.getName() ).thenReturn( "someName" );
    when( transMeta.findStep( "someName" ) ).thenReturn( stepMeta );
    doReturn( mock( Trans.class ) ).when( trans ).getParentTrans();
    Job job = mock( Job.class );
    when( trans.getParentJob() ).thenReturn( job );

    setSessionVariableData = mock( SetSessionVariableData.class );

    setSessionVariableStep = new SetSessionVariableStep( stepMeta, setSessionVariableData, 0, transMeta, trans );
    setSessionVariableStepSpy = spy( setSessionVariableStep );

    doReturn( setSessionVariableData ).when( setSessionVariableStepSpy ).getData();
    doNothing().when( setSessionVariableStepSpy ).logBasic( anyString() );
  }

  @Test
  public void testProcessRow() throws Exception {
    StepMetaInterface smi = new SetSessionVariableMeta(),
      smiSpy = spy( smi );

    String[] fields = new String[1],
      variables = new String[1],
      defaultValues = new String[1];
    fields[0] = "fooField";
    variables[0] = "foo";
    defaultValues[0] = "bar";
    doReturn( fields ).when( (SetSessionVariableMeta) smiSpy ).getFieldName();
    doReturn( variables ).when( (SetSessionVariableMeta) smiSpy ).getVariableName();
    doReturn( defaultValues ).when( (SetSessionVariableMeta) smiSpy ).getDefaultValue();

    Object[] rowData = new Object[1];
    rowData[0] = "";
    doReturn( rowData ).when( setSessionVariableStepSpy ).getRow();

    RowMetaInterface rowMetaInterface = mock( RowMetaInterface.class );
    doReturn( rowMetaInterface ).when( rowMetaInterface ).clone();
    doReturn( rowMetaInterface ).when( setSessionVariableStepSpy ).getInputRowMeta();

    doNothing().when( setSessionVariableStepSpy ).putRow( any( RowMetaInterface.class ), any( Object[].class ) );
    doNothing().when( setSessionVariableStepSpy ).setValue( anyString(), anyString() );
    doReturn( "bar" ).when( setSessionVariableStepSpy ).getRowValue( rowData, 0 );

    assertTrue( setSessionVariableStepSpy.processRow( smiSpy, setSessionVariableData ) );
    verify( setSessionVariableStepSpy, times( 1 ) ).setValue( anyString(), anyString() );

    doReturn( null ).when( setSessionVariableStepSpy ).getRow();
    doReturn( defaultValues[0] ).when( setSessionVariableStepSpy ).environmentSubstitute( defaultValues[0] );
    doNothing().when( setSessionVariableStepSpy ).setOutputDone();

    setSessionVariableStepSpy.first = true;
    assertFalse( setSessionVariableStepSpy.processRow( smiSpy, setSessionVariableData ) );
    verify( setSessionVariableStepSpy, times( 2 ) ).setValue( anyString(), anyString() );

    setSessionVariableStepSpy.first = false;
    doReturn( rowData ).when( setSessionVariableStepSpy ).getRow();
    try {
      setSessionVariableStepSpy.processRow( smiSpy, setSessionVariableData );
      fail();
    } catch ( KettleStepException e ) {
      verify( setSessionVariableStepSpy, times( 2 ) ).setValue( anyString(), anyString() );
    }

    doReturn( null ).when( setSessionVariableStepSpy ).getRow();
    assertFalse( setSessionVariableStepSpy.processRow( smiSpy, setSessionVariableData ) );
    verify( setSessionVariableStepSpy, times( 2 ) ).setValue( anyString(), anyString() );
  }

  @Test
  public void testSetValue() throws Exception {
    String varName = "foo", value = "bar";

    try {
      setSessionVariableStepSpy.setValue( "", value );
      fail();
    } catch ( KettleException e ) {
    }

    List<String> blacklist = new ArrayList<String>();
    blacklist.add( varName );
    doReturn( blacklist ).when( setSessionVariableData ).getBlackList();

    try {
      setSessionVariableStepSpy.setValue( varName, value );
      fail();
    } catch ( KettleException e ) {
    }

    blacklist.remove( 0 );
    doReturn( blacklist ).when( setSessionVariableData ).getBlackList();
    doNothing().when( setSessionVariableStepSpy ).setSessionVariable( value, varName );
    setSessionVariableStepSpy.setValue( varName, value );
    verify( setSessionVariableStepSpy, times( 1 ) ).setSessionVariable( value, varName );

    doThrow( new NoClassDefFoundError() ).when( setSessionVariableStepSpy ).setSessionVariable( value, varName );
    setSessionVariableStepSpy.setValue( varName, value );
    verify( setSessionVariableStepSpy, times( 1 ) ).setVariable( SIMULATED_SESSION_PREFIX + varName, value );
    verify( setSessionVariableStepSpy.getTrans(), times( 1 ) ).setVariable( SIMULATED_SESSION_PREFIX + varName, value );
  }

  @Test
  public void testGetRowValue() throws Exception {
    Object[] rowData = new Object[1];
    rowData[0] = "bar";

    StepMetaInterface smi = new SetSessionVariableMeta(),
        smiSpy = spy( smi );
    doReturn( false ).when( (SetSessionVariableMeta) smiSpy ).isUsingFormatting();

    String[] fields = new String[1];
    fields[0] = "fooField";
    doReturn( fields ).when( (SetSessionVariableMeta) smiSpy ).getFieldName();

    StepDataInterface sdi = new SetSessionVariableData(),
        sdiSpy = spy( sdi );

    RowMetaInterface rowMetaInterface = mock( RowMetaInterface.class );
    ValueMetaInterface valueMetaInterface = mock ( ValueMetaInterface.class );
    doReturn( valueMetaInterface ).when( rowMetaInterface ).getValueMeta( 0 );
    doReturn( "bar1" ).when( valueMetaInterface ).getString( "bar" );
    doReturn( "bar" ).when( valueMetaInterface ).getCompatibleString( "bar" );

    ( (SetSessionVariableData) sdiSpy ).outputRowMeta = rowMetaInterface;
    doReturn( smiSpy ).when( setSessionVariableStepSpy ).getMeta();
    doReturn( sdiSpy ).when( setSessionVariableStepSpy ).getData();
    assertEquals( setSessionVariableStepSpy.getRowValue( rowData, 0 ), "bar" );

    doReturn( true ).when( ( SetSessionVariableMeta) smiSpy ).isUsingFormatting();
    assertEquals( setSessionVariableStepSpy.getRowValue( rowData, 0 ), "bar1" );
  }

  @Test
  public void testDispose() throws Exception {
    StepMetaInterface smi = new SetSessionVariableMeta();
    StepDataInterface sdi = mock( SetSessionVariableData.class );

    ArgumentCaptor< BaseStepData.StepExecutionStatus > argument =
        ArgumentCaptor.forClass( BaseStepData.StepExecutionStatus.class );

    setSessionVariableStepSpy.dispose( smi, sdi );
    verify( sdi, times( 1 ) ).setStatus( any( BaseStepData.StepExecutionStatus.class ) );

    verify( sdi ).setStatus( argument.capture() );
    assertEquals( BaseStepData.StepExecutionStatus.STATUS_DISPOSED.toString(),
        argument.getValue().toString() );
  }
}
