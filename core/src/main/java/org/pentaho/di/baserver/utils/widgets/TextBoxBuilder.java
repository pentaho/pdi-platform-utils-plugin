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
 * Copyright 2006 - 2017 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.di.baserver.utils.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.ui.core.PropsUI;

public final class TextBoxBuilder extends WidgetBuilder<Text> {

  private String defaultText = "";

  public String getDefaultText() {
    return this.defaultText;
  }

  public TextBoxBuilder setDefaultText( final String defaultText ) {
    this.defaultText = defaultText;
    return this;
  }

  public TextBoxBuilder( Composite parent, PropsUI props ) {
    super( parent, props );
  }

  @Override
  protected Text createWidget( Composite parent ) {
    Text textBox = new Text( parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    textBox.setText( this.defaultText );
    return textBox;
  }
}
