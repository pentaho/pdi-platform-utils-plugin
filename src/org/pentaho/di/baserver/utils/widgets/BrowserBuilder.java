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

package org.pentaho.di.baserver.utils.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.ui.core.PropsUI;

public class BrowserBuilder extends WidgetBuilder<Browser> {

  private String labelText = "";

  public String getLabelText() {
    return this.labelText;
  }

  public BrowserBuilder setLabelText( String labelText ) {
    this.labelText = labelText;
    return this;
  }

  public BrowserBuilder( Composite parent, PropsUI props ) {
    super( parent, props );
  }

  @Override
  protected Browser createWidget( Composite parent ) {
    // create button
    Browser browser = createBrowser( parent, SWT.BORDER );
    browser.setText( this.labelText );
    FontData f = props.getDefaultFont();
    Font font = createFont( parent, f );
    browser.setFont( font );

    return browser;
  }

  protected Browser createBrowser( Composite parent, int i ) {
    return new Browser( parent, i );
  }

  protected Font createFont( Composite parent, FontData fontdata ) {
    return new Font( parent.getDisplay(), fontdata.getName(), fontdata.getHeight(), fontdata.getStyle() );
  }
}
