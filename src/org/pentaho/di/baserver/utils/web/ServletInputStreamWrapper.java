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

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ServletInputStreamWrapper extends ServletInputStream {

  private final InputStream inputStream;

  public InputStream getInputStream() {
    return this.inputStream;
  }

  public ServletInputStreamWrapper( InputStream inputStream ) {
    this.inputStream = inputStream;
  }

  @Override
  public int read() throws IOException {
    return this.inputStream.read();
  }

  @Override
  public void close() throws IOException {
    super.close();
    this.inputStream.close();
  }

  @Override public boolean isFinished() { return false; }

  @Override public boolean isReady() { return false; }

  @Override public void setReadListener(ReadListener readListener) { }
}
