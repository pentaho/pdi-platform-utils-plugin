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
