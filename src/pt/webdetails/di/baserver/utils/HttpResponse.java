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

/**
 * @author Marco Vala
 */
public class HttpResponse {

  private int statusCode;
  private String result;
  private long responseTime;

  public HttpResponse() {
    this.statusCode = 0;
    this.result = "";
    this.responseTime = 0;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode( int statusCode ) {
    this.statusCode = statusCode;
  }

  public String getResult() {
    return result;
  }

  public void setResult( String result ) {
    this.result = result;
  }

  public long getResponseTime() {
    return responseTime;
  }

  public void setResponseTime( long responseTime ) {
    this.responseTime = responseTime;
  }
}
