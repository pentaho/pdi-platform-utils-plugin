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

import static junit.framework.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class GetSessionVariableMetaTest {
  private GetSessionVariableMeta getSessionVariableMeta;
  private GetSessionVariableMeta getSessionVariableMetaSpy;

  @Before
  public void setUp() throws Exception {
    getSessionVariableMeta = new GetSessionVariableMeta();
    getSessionVariableMetaSpy = spy( getSessionVariableMeta );
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

    getSessionVariableMeta.getStep( stepMeta, stepDataInterface, cnr, transMeta, trans );
  }

  @Test
  public void testGetStepData() {
    final StepDataInterface stepData = getSessionVariableMeta.getStepData();
    assertNotNull( stepData );
  }

  @Test
  public void testAllocate() {
    final int size = 100;
    getSessionVariableMeta.allocate( size );
    assertAllocate( size, getSessionVariableMeta.getFieldName() );
    assertAllocate( size, getSessionVariableMeta.getVariableName() );
    assertAllocate( size, getSessionVariableMeta.getFieldType() );
    assertAllocate( size, getSessionVariableMeta.getFieldFormat() );
    assertAllocate( size, getSessionVariableMeta.getFieldLength() );
    assertAllocate( size, getSessionVariableMeta.getFieldPrecision() );
    assertAllocate( size, getSessionVariableMeta.getCurrency() );
    assertAllocate( size, getSessionVariableMeta.getDecimal() );
    assertAllocate( size, getSessionVariableMeta.getGroup() );
    assertAllocate( size, getSessionVariableMeta.getTrimType() );
    assertAllocate( size, getSessionVariableMeta.getDefaultValue() );
  }

  private void assertAllocate( int size, String[] array ) {
    assertNotNull( array );
    assertEquals( size, array.length );
  }

  private void assertAllocate( int size, int[] array ) {
    assertNotNull( array );
    assertEquals( size, array.length );
  }

  @Test
  public void testSetDefault() {
    getSessionVariableMetaSpy.setDefault();
    verify( getSessionVariableMetaSpy, times( 1 ) ).allocate( 0 );
  }

  @Test
  public void testGetFields() throws Exception {
    RowMetaInterface inputRowMeta = mock( RowMetaInterface.class );
    String name = "name";
    RowMetaInterface[] info = new RowMetaInterface[ 1 ];
    StepMeta nextStep = mock( StepMeta.class );
    VariableSpace space = mock( VariableSpace.class );
    doReturn( "" ).when( space ).environmentSubstitute( Mockito.<String>any() );
    Repository repository = mock( Repository.class );
    IMetaStore metaStore = mock( IMetaStore.class );

    getSessionVariableMeta.allocate( 1 );
    getSessionVariableMeta.setVariableName( new String[] { "vn" } );
    getSessionVariableMeta.getFields( inputRowMeta, name, info, nextStep, space, repository, metaStore );

    verify( space ).environmentSubstitute( Mockito.<String>any() );
    verify( inputRowMeta ).mergeRowMeta( Mockito.<RowMetaInterface>any() );
  }

  @Test
  public void testGetXML() {
    getSessionVariableMeta.allocate( 1 );
    getSessionVariableMeta.setFieldName( new String[] { "fn" } );
    final String xml = getSessionVariableMeta.getXML();
    assertNotNull( xml );
    assertTrue( xml.contains( "fields" ) );
    assertTrue( xml.contains( "field" ) );
    assertTrue( xml.contains( "name" ) );
    assertTrue( xml.contains( "variable" ) );
    assertTrue( xml.contains( "type" ) );
    assertTrue( xml.contains( "format" ) );
    assertTrue( xml.contains( "currency" ) );
    assertTrue( xml.contains( "decimal" ) );
    assertTrue( xml.contains( "group" ) );
    assertTrue( xml.contains( "length" ) );
    assertTrue( xml.contains( "precision" ) );
    assertTrue( xml.contains( "trim_type" ) );
    assertTrue( xml.contains( "default_value" ) );
  }

  @Test
  public void testLoadXML() throws Exception {
    final Node stepNode = mock( Node.class );
    final NodeList childrenStepNode = mock( NodeList.class );
    doReturn( 0 ).when( childrenStepNode ).getLength();
    doReturn( childrenStepNode ).when( stepNode ).getChildNodes();
    final List databases = mock( List.class );
    final IMetaStore metaStore = mock( IMetaStore.class );
    getSessionVariableMetaSpy.loadXML( stepNode, databases, metaStore );

    verify( getSessionVariableMetaSpy ).allocate( anyInt() );
  }

  @Test
  public void testReadRep() throws Exception {
    final Repository rep = mock( Repository.class );
    final IMetaStore metaStore = mock( IMetaStore.class );
    final ObjectId id_step = mock( ObjectId.class );
    final List databases = mock( List.class );

    doReturn( 1 ).when( rep ).countNrStepAttributes( eq( id_step ), any() );
    getSessionVariableMetaSpy.readRep( rep, metaStore, id_step, databases );

    verify( rep, times( 9 ) ).getStepAttributeString( eq( id_step ), anyInt(), any() );
    verify( rep, times( 2 ) ).getStepAttributeInteger( eq( id_step ), anyInt(), any() );
    verify( getSessionVariableMetaSpy ).allocate( anyInt() );
  }

  @Test
  public void testSaveRep() throws Exception {
    final Repository rep = mock( Repository.class );
    final IMetaStore metaStore = mock( IMetaStore.class );
    final ObjectId id_step = mock( ObjectId.class );
    final ObjectId id_transformation = mock( ObjectId.class );

    getSessionVariableMeta.allocate( 1 );
    getSessionVariableMeta.setFieldName( new String[] { "fn" } );
    getSessionVariableMeta.saveRep( rep, metaStore, id_transformation, id_step );

    verify( rep, times( 9 ) ).saveStepAttribute( eq( id_transformation ), eq( id_step ), anyInt(), any(),
        any() );
    verify( rep, times( 2 ) ).saveStepAttribute( eq( id_transformation ), eq( id_step ), anyInt(), any(),
        anyLong() );
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

    getSessionVariableMeta.allocate( 1 );
    getSessionVariableMeta.check( remarks, transMeta, stepMeta, prev, input, output, info, space, repository,
        metaStore );
    verify( remarks, times( 2 ) ).add( Mockito.<CheckResultInterface>any() );
  }

  @Test
  public void testSetFieldName() throws Exception {
    assertNull( getSessionVariableMeta.getFieldName() );
    String[] fieldName = new String[] { "value1", "value2" };
    getSessionVariableMeta.setFieldName( fieldName );
    assertEquals( fieldName, getSessionVariableMeta.getFieldName() );
  }

  @Test
  public void testSetVariableName() throws Exception {
    assertNull( getSessionVariableMeta.getVariableName() );
    String[] variableName = new String[] { "value1", "value2" };
    getSessionVariableMeta.setVariableName( variableName );
    assertEquals( variableName, getSessionVariableMeta.getVariableName() );
  }

  @Test
  public void testSetFieldType() throws Exception {
    assertNull( getSessionVariableMeta.getFieldType() );
    int[] fieldType = new int[] { 1, 2 };
    getSessionVariableMeta.setFieldType( fieldType );
    assertEquals( fieldType, getSessionVariableMeta.getFieldType() );
  }

  @Test
  public void testSetFieldFormat() throws Exception {
    assertNull( getSessionVariableMeta.getFieldFormat() );
    String[] fieldFormat = new String[] { "value1", "value2" };
    getSessionVariableMeta.setFieldFormat( fieldFormat );
    assertEquals( fieldFormat, getSessionVariableMeta.getFieldFormat() );
  }

  @Test
  public void testSetFieldLength() throws Exception {
    assertNull( getSessionVariableMeta.getFieldLength() );
    int[] fieldLength = new int[] { 1, 2 };
    getSessionVariableMeta.setFieldLength( fieldLength );
    assertEquals( fieldLength, getSessionVariableMeta.getFieldLength() );
  }

  @Test
  public void testSetFieldPrecision() throws Exception {
    assertNull( getSessionVariableMeta.getFieldPrecision() );
    int[] fieldPrecision = new int[] { 1, 2 };
    getSessionVariableMeta.setFieldPrecision( fieldPrecision );
    assertEquals( fieldPrecision, getSessionVariableMeta.getFieldPrecision() );
  }

  @Test
  public void testSetCurrency() throws Exception {
    assertNull( getSessionVariableMeta.getCurrency() );
    String[] currency = new String[] { "value1", "value2" };
    getSessionVariableMeta.setCurrency( currency );
    assertEquals( currency, getSessionVariableMeta.getCurrency() );
  }

  @Test
  public void testSetDecimal() throws Exception {
    assertNull( getSessionVariableMeta.getDecimal() );
    String[] decimal = new String[] { "value1", "value2" };
    getSessionVariableMeta.setDecimal( decimal );
    assertEquals( decimal, getSessionVariableMeta.getDecimal() );
  }

  @Test
  public void testSetGroup() throws Exception {
    assertNull( getSessionVariableMeta.getGroup() );
    String[] group = new String[] { "value1", "value2" };
    getSessionVariableMeta.setGroup( group );
    assertEquals( group, getSessionVariableMeta.getGroup() );
  }

  @Test
  public void testSetTrimType() throws Exception {
    assertNull( getSessionVariableMeta.getTrimType() );
    int[] trimType = new int[] { 1, 2 };
    getSessionVariableMeta.setTrimType( trimType );
    assertEquals( trimType, getSessionVariableMeta.getTrimType() );
  }

  @Test
  public void testSetDefaultValue() throws Exception {
    assertNull( getSessionVariableMeta.getDefaultValue() );
    String[] defaultValue = new String[] { "value1", "value2" };
    getSessionVariableMeta.setDefaultValue( defaultValue );
    assertEquals( defaultValue, getSessionVariableMeta.getDefaultValue() );
  }
}
