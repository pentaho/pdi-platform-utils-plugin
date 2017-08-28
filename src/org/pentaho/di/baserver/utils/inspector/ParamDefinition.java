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
 * Copyright 2006 - 2017 Pentaho Corporation.  All rights reserved.
 */
package org.pentaho.di.baserver.utils.inspector;

import org.pentaho.di.baserver.utils.web.HttpParameter;

public class ParamDefinition implements Comparable<ParamDefinition> {

  private String name;
  private String contentType;
  private HttpParameter.ParamType paramType;

  public ParamDefinition() {
    paramType = HttpParameter.ParamType.NONE;
  }

  public ParamDefinition( String name ) {
    this.name = name;
    paramType = HttpParameter.ParamType.NONE;
  }

  public ParamDefinition( String name, HttpParameter.ParamType paramType ) {
    this.name = name;
    this.paramType = paramType;
  }

  public ParamDefinition( String name, String contentType, HttpParameter.ParamType paramType ) {
    this.name = name;
    this.contentType = contentType;
    this.paramType = paramType;
  }

  //region Getters and Setters
  public String getName() {
    return this.name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType( String contentType ) {
    this.contentType = contentType;
  }

  public HttpParameter.ParamType getParamType() {
    return paramType;
  }

  public void setParamType( HttpParameter.ParamType paramType ) {
    this.paramType = paramType;
  }

  //endregion

  @Override
  public int compareTo( ParamDefinition other ) {
    if ( this == other ) {
      return 0;
    }
    return this.name.compareTo( other.name );
  }

  @Override
  public boolean equals( Object obj ) {
    if ( obj == this ) {
      return true;
    }
    if ( obj == null || !( obj instanceof ParamDefinition ) ) {
      return false;
    }

    ParamDefinition other = (ParamDefinition) obj;
    return this.name.equals( other.name );
  }

  @Override
  public int hashCode() {
    return this.name.hashCode();
  }
}
