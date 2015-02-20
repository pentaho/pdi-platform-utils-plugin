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
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.TextVar;

public final class TextVarBuilder extends WidgetBuilder<TextVar> {

  private VariableSpace variableSpace;
  private String defaultText = "";

  public String getDefaultText( final String defaultText ) {
    return this.defaultText;
  }

  public TextVarBuilder setDefaultText( final String defaultText ) {
    this.defaultText = defaultText;
    return this;
  }

  public TextVarBuilder( Composite parent, PropsUI props, VariableSpace variableSpace ) {
    super( parent, props );
    this.variableSpace = variableSpace;
  }

  @Override
  protected TextVar createWidget( Composite parent ) {
    TextVar textVar = new TextVar( this.variableSpace, this.parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    textVar.setText( this.defaultText );
    return textVar;
  }
}
