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

package org.pentaho.di.baserver.utils.inspector;

import java.util.ArrayList;
import java.util.Collection;

public class Endpoint implements Comparable<Endpoint> {
  public enum Visibility { PUBLIC, PRIVATE }
  // region Fields

  private String id;
  private String path;
  private HttpMethod httpMethod;
  private ArrayList<QueryParam> queryParams;
  private boolean deprecated;
  private Visibility visibility;
  private String documentation;

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

  public boolean isDeprecated() {
    return deprecated;
  }

  public void setDeprecated( boolean deprecated ) {
    this.deprecated = deprecated;
  }

  public Visibility getVisibility() {
    return visibility;
  }

  public void setVisibility( Visibility visiblity ) {
    this.visibility = visiblity;
  }

  public String getDocumentation() {
    return documentation;
  }

  public void setDocumentation( String doc ) {
    this.documentation = doc;
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
