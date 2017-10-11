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
 * Copyright 2017 - 2017 Hitachi Vantara.  All rights reserved.
 */
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
