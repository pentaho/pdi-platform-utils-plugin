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

package pt.webdetails.di.baserver.utils.web;

/**
 * @author Marco Vala
 */
public class Response {

  private long responseTime;
  private int statusCode;
  private String result;

  public long getResponseTime() {
    return this.responseTime;
  }

  public Response setResponseTime( long responseTime ) {
    this.responseTime = responseTime;
    return this;
  }

  public int getStatusCode() {
    return this.statusCode;
  }

  public Response setStatusCode( int statusCode ) {
    this.statusCode = statusCode;
    return this;
  }

  public String getResult() {
    return this.result;
  }

  public Response setResult( String result ) {
    this.result = result;
    return this;
  }

  public Response() {
    this.responseTime = -1;
    this.statusCode = -1;
    this.result = "";
  }
}
