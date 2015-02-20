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

package org.pentaho.di.baserver.utils.web;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Marco Vala
 */
public class ServletOutputStreamWrapper extends ServletOutputStream {

  private final OutputStream outputStream;

  public OutputStream getOutputStream() {
    return outputStream;
  }

  public ServletOutputStreamWrapper( OutputStream outputStream ) {
    this.outputStream = outputStream;
  }

  @Override
  public void write( int b ) throws IOException {
    this.outputStream.write( b );
  }

  @Override
  public void flush() throws IOException {
    super.flush();
    this.outputStream.flush();
  }

  @Override
  public void close() throws IOException {
    super.close();
    this.outputStream.close();
  }
}
