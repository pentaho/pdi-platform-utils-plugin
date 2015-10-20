package org.pentaho.di.baserver.utils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.job.Job;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class GetSessionVariableStepTest {
  GetSessionVariableStep getSessionVariableStep, getSessionVariableStepSpy;
  GetSessionVariableData getSessionVariableData;

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

    getSessionVariableData = mock( GetSessionVariableData.class );

    getSessionVariableStep = new GetSessionVariableStep( stepMeta, getSessionVariableData, 0, transMeta, trans );
    getSessionVariableStepSpy = spy( getSessionVariableStep );

    doReturn( new Object[] {} ).when( getSessionVariableStepSpy ).getRow();
  }

  @Test
  public void testInit() throws Exception {
    StepMetaInterface smi = new GetSessionVariableMeta();
    GetSessionVariableData sdi = mock( GetSessionVariableData.class );

    assertTrue( getSessionVariableStepSpy.init( smi, sdi ) );
  }

  @Test
  public void testProcessRow() throws Exception {
    StepMetaInterface smi = new GetSessionVariableMeta(),
      smiSpy = spy( smi );

    doNothing().when( (GetSessionVariableMeta) smiSpy ).getFields(
      any( RowMetaInterface.class ), anyString(), Matchers.<RowMetaInterface[]>any(),
      any( StepMeta.class ), any( VariableSpace.class ), any( Repository.class ), any( IMetaStore.class ) );
    doReturn( new String[] {} ).when( (GetSessionVariableMeta) smiSpy ).getFieldName();

    doNothing().when( getSessionVariableStepSpy ).putRow( any( RowMetaInterface.class ), any( Object[].class ) );
    doNothing().when( getSessionVariableStepSpy ).setOutputDone();

    assertFalse( getSessionVariableStepSpy.processRow( smiSpy, getSessionVariableData ) );
    verify( getSessionVariableStepSpy, times(1) ).setOutputDone();

    getSessionVariableData.readsRows = true;

    RowMetaInterface rowMetaInterface = mock( RowMetaInterface.class );
    RowMetaInterface rowMetaInterfaceSpy = mock( RowMetaInterface.class );
    doReturn( rowMetaInterfaceSpy ).when( rowMetaInterface ).clone();
    doReturn( rowMetaInterface ).when( getSessionVariableStepSpy ).getInputRowMeta();

    assertTrue( getSessionVariableStepSpy.processRow( smiSpy, getSessionVariableData ) );

    doReturn( null ).when(getSessionVariableStepSpy).getRow();

    assertFalse( getSessionVariableStepSpy.processRow( smiSpy, getSessionVariableData ) );
    verify( getSessionVariableStepSpy, times(2) ).setOutputDone();
  }
}
