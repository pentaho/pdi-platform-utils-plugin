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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.ui.core.PropsUI;

public class ButtonBuilder extends WidgetBuilder<Button> {

  private String labelText = "";

  public String getLabelText() {
    return this.labelText;
  }

  public ButtonBuilder setLabelText( String labelText ) {
    this.labelText = labelText;
    return this;
  }

  public ButtonBuilder( Composite parent, PropsUI props ) {
    super( parent, props );
  }

  @Override
  protected Button createWidget( Composite parent ) {
    // create button
    Button button = createButton( parent, SWT.PUSH );
    button.setText( this.labelText );

    return button;
  }

  protected Button createButton( Composite parent, int i ) {
    return new Button( parent, i );
  }
}
