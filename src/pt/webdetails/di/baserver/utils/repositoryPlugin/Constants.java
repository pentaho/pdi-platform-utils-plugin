/*
 *  Copyright 2002 - 2014 Webdetails, a Pentaho company.  All rights reserved.
 *
 *  This software was developed by Webdetails and is provided under the terms
 *  of the Mozilla Public License, Version 2.0, or any later version. You may not use
 *  this file except in compliance with the license. If you need a copy of the license,
 *  please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 *  Software distributed under the Mozilla Public License is distributed on an "AS IS"
 *  basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 *  the license for the specific language governing your rights and limitations.
 */

package pt.webdetails.di.baserver.utils.repositoryPlugin;

public final class Constants {

  // region constants
  private static final Constants instance = new Constants();
  // endregion

  // region Properties
  public static Constants getInstance() {
    return Constants.instance;
  }

  public String getVfsScheme() {
    return "jcr-solution";
  }

  public String getDefaultServerUrl() {
    return "http://127.0.0.1:8080/pentaho";
  }

  public String getDefaultUser() {
    return "admin";
  }

  public String getDefaultPassword() {
    return "password";
  }
  // endregion

  private Constants() {}


}
