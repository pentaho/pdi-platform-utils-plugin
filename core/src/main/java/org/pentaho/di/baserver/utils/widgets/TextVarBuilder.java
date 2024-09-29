/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
