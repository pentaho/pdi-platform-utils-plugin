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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SetSessionVariableMetaTest {
  private SetSessionVariableMeta setSessionVariableMeta;
  private SetSessionVariableMeta setSessionVariableMetaSpy;

  @Before
  public void setUp() throws Exception {
    setSessionVariableMeta = new SetSessionVariableMeta();
    setSessionVariableMetaSpy = spy( setSessionVariableMeta );
  }

  @Test
  public void testSetFieldName() throws Exception {
    assertNull( setSessionVariableMeta.getFieldName() );
    String[] fieldName = new String[] { "value1", "value2" };
    setSessionVariableMeta.setFieldName( fieldName );
    assertEquals( fieldName, setSessionVariableMeta.getFieldName() );
  }

  @Test
  public void testSetVariableName() throws Exception {
    assertNull( setSessionVariableMeta.getVariableName() );
    String[] variableName = new String[] { "value1", "value2" };
    setSessionVariableMeta.setVariableName( variableName );
    assertEquals( variableName, setSessionVariableMeta.getVariableName() );
  }

  @Test
  public void testSetDefaultValue() throws Exception {
    assertNull( setSessionVariableMeta.getDefaultValue() );
    String[] defaultValue = new String[] { "value1", "value2" };
    setSessionVariableMeta.setDefaultValue( defaultValue );
    assertEquals( defaultValue, setSessionVariableMeta.getDefaultValue() );
  }

  @Test
  public void testSetUseFormatting() throws Exception {
    assertFalse( setSessionVariableMeta.isUsingFormatting() );
    setSessionVariableMeta.setUseFormatting( true );
    assertTrue( setSessionVariableMeta.isUsingFormatting() );
  }

  @Test
  public void testGetStep() {
    final StepMeta stepMeta = mock( StepMeta.class );
    doReturn( "name" ).when( stepMeta ).getName();
    final StepDataInterface stepDataInterface = mock( StepDataInterface.class );
    final int cnr = 0;
    final TransMeta transMeta = mock( TransMeta.class );
    doReturn( stepMeta ).when( transMeta ).findStep( any() );
    final Trans trans = mock( Trans.class );

    setSessionVariableMeta.getStep( stepMeta, stepDataInterface, cnr, transMeta, trans );
  }

  @Test
  public void testGetStepData() {
    final StepDataInterface stepData = setSessionVariableMeta.getStepData();
    assertNotNull( stepData );
  }

  @Test
  public void testAllocate() {
    final int size = 100;
    setSessionVariableMeta.allocate( size );
    assertAllocate( size, setSessionVariableMeta.getFieldName() );
    assertAllocate( size, setSessionVariableMeta.getVariableName() );
    assertAllocate( size, setSessionVariableMeta.getDefaultValue() );
  }

  private void assertAllocate( int size, String[] array ) {
    assertNotNull( array );
    assertEquals( size, array.length );
  }

  @Test
  public void testSetDefault() {
    setSessionVariableMetaSpy.setDefault();
    verify( setSessionVariableMetaSpy, times( 1 ) ).allocate( 0 );
    assertTrue( setSessionVariableMetaSpy.isUsingFormatting() );
  }

  @Test
  public void testClone() throws Exception {
    setSessionVariableMetaSpy.setFieldName( new String[] { "1" } );
    setSessionVariableMetaSpy.setVariableName( new String[] { "2" } );
    setSessionVariableMetaSpy.setDefaultValue( new String[] { "3" } );

    SetSessionVariableMeta clone = (SetSessionVariableMeta) setSessionVariableMetaSpy.clone();
    assertArrayEquals( clone.getFieldName(), setSessionVariableMetaSpy.getFieldName() );
    assertArrayEquals( clone.getVariableName(), setSessionVariableMetaSpy.getVariableName() );
    assertArrayEquals( clone.getDefaultValue(), setSessionVariableMetaSpy.getDefaultValue() );
  }

  @Test
  public void testGetXML() {
    setSessionVariableMeta.allocate( 1 );
    setSessionVariableMeta.setFieldName( new String[] { "fn" } );
    final String xml = setSessionVariableMeta.getXML();
    assertNotNull( xml );
    assertTrue( xml.contains( "fields" ) );
    assertTrue( xml.contains( "field" ) );
    assertTrue( xml.contains( "name" ) );
    assertTrue( xml.contains( "variable" ) );
    assertTrue( xml.contains( "default_value" ) );
    assertTrue( xml.contains( "use_formatting" ) );
  }

  @Test
  public void testLoadXML() throws Exception {
    final Node stepNode = mock( Node.class );
    final NodeList childrenStepNode = mock( NodeList.class );
    doReturn( 0 ).when( childrenStepNode ).getLength();
    doReturn( childrenStepNode ).when( stepNode ).getChildNodes();
    final List databases = mock( List.class );
    final IMetaStore metaStore = mock( IMetaStore.class );
    setSessionVariableMetaSpy.loadXML( stepNode, databases, metaStore );

    verify( setSessionVariableMetaSpy ).allocate( anyInt() );
  }

  @Test
  public void testReadRep() throws Exception {
    final Repository rep = mock( Repository.class );
    final IMetaStore metaStore = mock( IMetaStore.class );
    final ObjectId id_step = mock( ObjectId.class );
    final List databases = mock( List.class );

    doReturn( 1 ).when( rep ).countNrStepAttributes( eq( id_step ), any() );
    setSessionVariableMetaSpy.readRep( rep, metaStore, id_step, databases );

    verify( rep, times( 3 ) ).getStepAttributeString( eq( id_step ), anyInt(), any() );
    verify( rep ).getStepAttributeBoolean( eq( id_step ), anyInt(), any(), anyBoolean() );
    verify( setSessionVariableMetaSpy ).allocate( anyInt() );
  }

  @Test
  public void testSaveRep() throws Exception {
    final Repository rep = mock( Repository.class );
    final IMetaStore metaStore = mock( IMetaStore.class );
    final ObjectId id_step = mock( ObjectId.class );
    final ObjectId id_transformation = mock( ObjectId.class );

    setSessionVariableMeta.allocate( 1 );
    setSessionVariableMeta.setFieldName( new String[] { "fn" } );
    setSessionVariableMeta.saveRep( rep, metaStore, id_transformation, id_step );

    verify( rep, times( 3 ) )
        .saveStepAttribute( eq( id_transformation ), eq( id_step ), anyInt(), any(), any() );
    verify( rep ).saveStepAttribute( eq( id_transformation ), eq( id_step ), anyInt(), any(), anyBoolean() );
  }

  @Test
  public void testCheck() {
    List remarks = mock( List.class );
    TransMeta transMeta = mock( TransMeta.class );
    StepMeta stepMeta = mock( StepMeta.class );
    RowMetaInterface prev = mock( RowMetaInterface.class );
    String[] input = new String[ 1 ];
    String[] output = new String[ 1 ];
    RowMetaInterface info = mock( RowMetaInterface.class );
    VariableSpace space = mock( VariableSpace.class );
    Repository repository = mock( Repository.class );
    IMetaStore metaStore = mock( IMetaStore.class );

    setSessionVariableMeta.allocate( 1 );
    setSessionVariableMeta
        .check( remarks, transMeta, stepMeta, prev, input, output, info, space, repository, metaStore );
    verify( remarks, times( 2 ) ).add( Mockito.<CheckResultInterface>any() );
  }
}
