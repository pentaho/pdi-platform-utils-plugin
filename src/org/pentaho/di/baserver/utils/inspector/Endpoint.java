/*!
* Copyright 2002 - 2014 Webdetails, a Pentaho company.  All rights reserved.
*
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package org.pentaho.di.baserver.utils.inspector;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Marco Vala
 */
public class Endpoint implements Comparable<Endpoint> {

  // region Fields

  private String id;
  private String path;
  private HttpMethod httpMethod;
  private ArrayList<QueryParam> queryParams;

  // endregion

  // region Getters and Setters

  public String getId() {
    return this.id;
  }

  public void setId( String id ) {
    this.id = id;
  }

  public String getPath() {
    return this.path;
  }

  public void setPath( String path ) {
    this.path = path;
  }

  public HttpMethod getHttpMethod() {
    return this.httpMethod;
  }

  public void setHttpMethod( HttpMethod httpMethod ) {
    this.httpMethod = httpMethod;
  }

  public Collection<QueryParam> getQueryParams() {
    return this.queryParams;
  }

  // endregion

  // region Constructors

  public Endpoint() {
    this.queryParams = new ArrayList<QueryParam>();
  }

  // endregion

  // region Methods

  @Override
  public int compareTo( Endpoint endpoint ) {
    if ( this == endpoint ) {
      return 0;
    }
    return this.id.compareTo( endpoint.id );
  }

  @Override
  public boolean equals( Object obj ) {
    if ( obj == this ) {
      return true;
    }
    if ( obj == null || !( obj instanceof Endpoint ) ) {
      return false;
    }

    Endpoint other = (Endpoint) obj;
    return this.id.equals( other.id );
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }

  // endregion
}
