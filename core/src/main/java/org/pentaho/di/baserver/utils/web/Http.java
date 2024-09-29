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
