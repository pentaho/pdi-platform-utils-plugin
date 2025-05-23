/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
