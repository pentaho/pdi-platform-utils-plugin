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
