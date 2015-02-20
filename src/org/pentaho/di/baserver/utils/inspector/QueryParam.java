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

/**
 * @author Marco Vala
 */
public class QueryParam implements Comparable<QueryParam> {

  private String name;
  private String type;

  public QueryParam() {
  }

  //region Getters and Setters
  public String getName() {
    return this.name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getType() {
    return this.type;
  }

  public void setType( String type ) {
    this.type = type;
  }
  //endregion

  @Override
  public int compareTo( QueryParam other ) {
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
    if ( obj == null || !( obj instanceof QueryParam ) ) {
      return false;
    }

    QueryParam other = (QueryParam) obj;
    return this.name.equals( other.name );
  }

  @Override
  public int hashCode() {
    return this.name.hashCode();
  }
}
