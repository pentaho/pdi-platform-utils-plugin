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

package org.pentaho.di.baserver.utils.web;

public class HttpParameter {

  private String name;
  private String value;
  private ParamType paramType;

  public enum ParamType {
    QUERY, BODY, NONE
  }

  public HttpParameter() {
    this.paramType = ParamType.NONE;
  }

  public HttpParameter( String name, String value, ParamType paramType ) {
    this.name = name;
    this.value = value;
    this.paramType = paramType;
  }

  public HttpParameter( String name, String value ) {
    this.name = name;
    this.value = value;
    this.paramType = ParamType.NONE;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue( String value ) {
    this.value = value;
  }

  public ParamType getParamType() {
    return paramType;
  }

  public void setParamType( ParamType paramType ) {
    this.paramType = paramType;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    HttpParameter that = (HttpParameter) o;

    if ( !name.equals( that.name ) ) {
      return false;
    }
    if ( !value.equals( that.value ) ) {
      return false;
    }
    return paramType == that.paramType;

  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + value.hashCode();
    result = 31 * result + paramType.hashCode();
    return result;
  }
}
