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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
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
    assertEquals( "Result", callEndpointMetaSpy.getResultField() );
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
