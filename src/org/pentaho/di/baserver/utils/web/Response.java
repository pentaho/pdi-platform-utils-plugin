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

package org.pentaho.di.baserver.utils.web;

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
