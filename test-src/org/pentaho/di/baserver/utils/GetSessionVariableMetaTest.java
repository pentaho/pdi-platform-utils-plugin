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
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

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
    doReturn( stepMeta ).when( transMeta ).findStep( anyString() );
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
    RowMetaInterface[] info = new RowMetaInterface[1];
    StepMeta nextStep = mock( StepMeta.class );
    VariableSpace space = mock( VariableSpace.class );
    doReturn( "" ).when( space ).environmentSubstitute( anyString() );
    Repository repository = mock( Repository.class );
    IMetaStore metaStore = mock( IMetaStore.class );

    getSessionVariableMeta.allocate( 1 );
    getSessionVariableMeta.setVariableName( new String[] { "vn" } );
    getSessionVariableMeta.getFields( inputRowMeta, name, info, nextStep, space, repository, metaStore );

    verify( space ).environmentSubstitute( anyString() );
    verify( inputRowMeta ).mergeRowMeta( any( RowMetaInterface.class ) );
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
    doReturn( childrenStepNode ).when( stepNode).getChildNodes();
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

    doReturn( 1 ).when( rep ).countNrStepAttributes( eq( id_step ), anyString() );
    getSessionVariableMetaSpy.readRep( rep, metaStore, id_step, databases );

    verify( rep, times( 9 ) ).getStepAttributeString( eq( id_step ), anyInt(), anyString() );
    verify( rep, times( 2 ) ).getStepAttributeInteger( eq( id_step ), anyInt(), anyString() );
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

    verify( rep, times( 9 ) ).saveStepAttribute( eq( id_transformation ), eq( id_step ), anyInt(), anyString(), anyString() );
    verify( rep, times( 2 ) ).saveStepAttribute( eq( id_transformation ), eq( id_step ), anyInt(), anyString(), anyInt() );
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

    getSessionVariableMeta.allocate( 1 );
    getSessionVariableMeta.check( remarks, transMeta, stepMeta, prev, input, output, info, space, repository, metaStore );
    verify( remarks, times( 2 ) ).add( any( CheckResultInterface.class ) );
  }
}
