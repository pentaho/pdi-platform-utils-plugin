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

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
