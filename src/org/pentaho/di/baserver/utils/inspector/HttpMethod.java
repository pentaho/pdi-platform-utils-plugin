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

public enum HttpMethod {
  GET,
  POST,
  PUT,
  DELETE,
  HEAD,
  OPTIONS

  /*
  private static final String[] names = new String[values().length];

  static {
    HttpMethod[] types = values();
    for ( int i = 0; i < types.length; i++ ) {
      names[i] = types[i].name();
    }
  }

  public static String[] names() {
    return names;
  }

  public static HttpMethod getHttpMethod( String name ) {
    HttpMethod[] types = values();
    for ( int i = 0; i < types.length; i++ ) {
      if ( types[i].name().equals( name ) ) {
        return types[i];
      }
    }
    return null;
  }
  */
}
