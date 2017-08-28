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

package org.pentaho.di.baserver.utils.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum Http {

  GET,
  POST,
  PUT,
  DELETE,
  HEAD,
  OPTIONS;

  private static final Log logger = LogFactory.getLog( Http.class );

  public static Http getHttpMethod( String httpMethod ) {
    Http method;
    if ( httpMethod == null ) {
      httpMethod = "";
    }
    try {
      httpMethod = httpMethod.toUpperCase();
      method = Http.valueOf( httpMethod );
    } catch ( IllegalArgumentException e ) {
      logger.warn( "Method '" + httpMethod + "' is not supported - using 'GET'" );
      method = Http.GET;
    }
    return method;
  }

}
