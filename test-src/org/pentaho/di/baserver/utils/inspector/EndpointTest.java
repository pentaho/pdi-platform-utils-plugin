package org.pentaho.di.baserver.utils.inspector;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.util.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.pentaho.di.core.util.Assert.assertTrue;

public class EndpointTest {
  Endpoint endpoint;
  
  @Before
  public void setup() {
    endpoint = new Endpoint();
  }

  @Test
  public void testConstructor() {
    assertEquals( endpoint.getQueryParams().size(), 0 );
  }

  @Test
  public void testGet() {
    String id = "1234";
    endpoint.setId( id );
    assertEquals( endpoint.getId(), id);

    String path = "/path";
    endpoint.setPath( path );
    assertEquals( endpoint.getPath(), path);
    
    HttpMethod httpMethod = HttpMethod.GET;
    endpoint.setHttpMethod( httpMethod );
    assertEquals( endpoint.getHttpMethod(), httpMethod );
  }
  
  @Test
  public void testCompareTo() {
    String id = "1234",
      id2 = "12345678";
    endpoint.setId( id );
    
    Endpoint endpoint2 = new Endpoint();
    endpoint2.setId( id2 );
    
    assertEquals( endpoint.compareTo( endpoint2 ), id.compareTo( id2 ) );
  }
  
  @Test 
  public void testEquals() {
    String id = "1234";
    endpoint.setId( id );
    
    endpoint.equals( endpoint );

    Endpoint endpoint2 = new Endpoint();
    endpoint2.setId( id );
    assertTrue( endpoint.equals( endpoint2 ) );
    assertFalse( endpoint.equals( null ) );
    assertFalse( endpoint.equals( new QueryParam() ) );

    endpoint2.setId( "12345" );
    assertFalse( endpoint.equals( endpoint2 ) );
  }
  
  @Test
  public void testHashCode() {
    String id = "1234";
    endpoint.setId( id );
    
    assertEquals( id.hashCode(), endpoint.hashCode() );
  }
}
