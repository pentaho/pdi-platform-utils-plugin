/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.di.baserver.utils;

import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;

public final class SessionHelper {

  public static final String SIMULATED_SESSION_PREFIX = "_FAKE_SESSION_";

  public static Object getSessionVariable( String varName ) {
    IPentahoSession session = PentahoSessionHolder.getSession();
    if ( session != null ) {
      return session.getAttribute( varName );
    }
    return null;
  }

  public static void setSessionVariable( String varName, Object varValue ) {
    IPentahoSession session = PentahoSessionHolder.getSession();
    if ( session != null ) {
      session.setAttribute( varName, varValue );
    }
  }
}
