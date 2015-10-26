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
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.job.Job;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class CallEndpointStepTest {
  CallEndpointStep callEndpointStep, callEndpointStepSpy;
  CallEndpointData callEndpointData;

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

    callEndpointData = mock( CallEndpointData.class );

    callEndpointStep = new CallEndpointStep( stepMeta, callEndpointData, 0, transMeta, trans );
    callEndpointStepSpy = spy( callEndpointStep );
  }

  @Test
  public void testInit() throws Exception {
    StepMetaInterface smi = new CallEndpointMeta();
    CallEndpointData sdi = mock( CallEndpointData.class );

    assertTrue( callEndpointStepSpy.init( smi, sdi ) );
  }

  @Test
   public void testProcessRow() throws Exception {
    CallEndpointMeta smi = new CallEndpointMeta(),
      smiSpy = spy( smi );

    doNothing().when( (CallEndpointMeta) smiSpy ).getFields(
      any( RowMetaInterface.class ), anyString(), Matchers.<RowMetaInterface[]>any(),
      any( StepMeta.class ), any( VariableSpace.class ), any( Repository.class ), any( IMetaStore.class ) );
    doReturn( new String[] {} ).when( (CallEndpointMeta) smiSpy ).getFieldName();

    doNothing().when( callEndpointStepSpy ).putRow( any( RowMetaInterface.class ), any( Object[].class ) );
    doNothing().when( callEndpointStepSpy ).setOutputDone();

    RowMetaInterface rowMetaInterface = mock( RowMetaInterface.class );
    RowMetaInterface rowMetaInterfaceSpy = mock( RowMetaInterface.class );
    doReturn( rowMetaInterfaceSpy ).when( rowMetaInterface ).clone();

    ValueMetaInterface vmi = mock( ValueMetaInterface.class );
    doReturn( vmi ).when ( rowMetaInterface ).getValueMeta( anyInt() );
    doReturn( rowMetaInterface ).when( callEndpointStepSpy ).getInputRowMeta();

    doReturn( null ).when( callEndpointStepSpy ).getRow();

    assertFalse( callEndpointStepSpy.processRow( smiSpy, callEndpointData ) );

    smiSpy.setEndpointFromField( true );
    Object[] rowData = new Object[1];
    rowData[0] = "";
    doReturn( rowData ).when( callEndpointStepSpy ).getRow();
    doReturn( "bar" ).when( callEndpointStepSpy ).getRowValue( any( Object[].class ), anyString(), anyString() );

    assertTrue( callEndpointStepSpy.processRow( smiSpy, callEndpointData ) );

    smiSpy.setEndpointFromField( false );
    assertTrue( callEndpointStepSpy.processRow( smiSpy, callEndpointData ) );

    smiSpy.setBypassingAuthentication( true );
    assertTrue( callEndpointStepSpy.processRow( smiSpy, callEndpointData ) );
  }

  @Test
  public void testDispose() throws Exception {
    StepMetaInterface smi = new CallEndpointMeta();
    StepDataInterface sdi = mock( CallEndpointData.class );

    ArgumentCaptor< BaseStepData.StepExecutionStatus > argument =
      ArgumentCaptor.forClass( BaseStepData.StepExecutionStatus.class );

    callEndpointStepSpy.dispose( smi, sdi );
    verify( sdi, times( 1 ) ).setStatus( any( BaseStepData.StepExecutionStatus.class ) );

    verify( sdi ).setStatus( argument.capture() );
    assertEquals( BaseStepData.StepExecutionStatus.STATUS_DISPOSED.toString(),
        argument.getValue().toString() );
  }
}
