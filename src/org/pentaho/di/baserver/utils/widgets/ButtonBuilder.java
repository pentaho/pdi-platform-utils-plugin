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

package org.pentaho.di.baserver.utils.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.ui.core.PropsUI;

/**
 * @author Marco Vala
 */
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
    Button button = new Button( this.parent, SWT.PUSH );
    button.setText( this.labelText );

    return button;
  }
}
