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

package pt.webdetails.di.baserver.utils.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.pentaho.di.core.Const;
import org.pentaho.di.ui.core.PropsUI;

/**
 * @author Marco Vala
 */
public class ButtonBuilder extends WidgetBuilder {

  private String labelText = "";
  private Control top = null;
  private Control left = null;
  private SelectionListener onButtonPressed = null;

  public ButtonBuilder( PropsUI props, Composite parent ) {
    super( props, parent );
  }

  public Button build() {
    // create button
    Button button = new Button( this.parent, SWT.PUSH );
    button.setText( this.labelText );
    this.props.setLook( button );

    // place button
    FormData data;
    data = new FormData();
    data.top = new FormAttachment( this.top, Const.MARGIN );
    data.left = new FormAttachment( this.left, Const.MARGIN );
    button.setLayoutData( data );

    // add listener
    if ( this.onButtonPressed != null ) {
      button.addSelectionListener( this.onButtonPressed );
    }

    return button;
  }

  public ButtonBuilder setLabelText( String labelText ) {
    this.labelText = labelText;
    return this;
  }

  public ButtonBuilder setTop( Control top ) {
    this.top = top;
    return this;
  }

  public ButtonBuilder setLeft( Control left ) {
    this.left = left;
    return this;
  }

  public ButtonBuilder onButtonPressed( SelectionListener selectionListener ) {
    this.onButtonPressed = selectionListener;
    return this;
  }
}
