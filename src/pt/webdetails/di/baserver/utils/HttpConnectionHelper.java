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

package pt.webdetails.di.baserver.utils;

import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;

/**
 * @author Marco Vala
 */
public final class HttpConnectionHelper {

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
