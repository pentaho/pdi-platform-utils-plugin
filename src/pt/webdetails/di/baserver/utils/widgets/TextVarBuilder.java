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
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.TextVar;

/**
 * @author Marco Vala
 */
public final class TextVarBuilder extends WidgetBuilder {

  private int width = 0;
  private String labelText = "";
  private int labelWidth = 0;
  private String defaultText = "";
  private char echoChar = 0;
  private Control top = null;
  private Control left = null;
  private ModifyListener modifyListener = null;
  private VariableSpace variableSpace;

  public TextVarBuilder( PropsUI props, Composite parent, VariableSpace variableSpace ) {
    super( props, parent );
    this.variableSpace = variableSpace;
  }

  public TextVar build() {
    // create label for text box
    Label label = new Label( parent, SWT.RIGHT );
    label.setText( this.labelText );
    super.props.setLook( label );

    // create text box
    TextVar textVar = new TextVar( this.variableSpace, this.parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    textVar.setText( this.defaultText );
    if ( this.echoChar != 0 ) {
      textVar.setEchoChar( this.echoChar );
    }
    this.props.setLook( textVar );

    // place label
    FormData data;
    data = new FormData();
    if ( this.labelWidth > 0 ) {
      data.width = this.labelWidth;
    }
    data.top = new FormAttachment( this.top, Const.MARGIN );
    data.left = new FormAttachment( this.left, Const.MARGIN );
    label.setLayoutData( data );

    // place text box next to label
    data = new FormData();
    if ( this.width > 0 ) {
      data.width = this.width;
    }
    data.top = new FormAttachment( this.top, Const.MARGIN );
    data.left = new FormAttachment( label, Const.MARGIN );
    textVar.setLayoutData( data );

    // add listener
    if ( this.modifyListener != null ) {
      textVar.addModifyListener( this.modifyListener );
    }

    return textVar;
  }

  public TextVarBuilder setWidth( int width ) {
    this.width = width;
    return this;
  }

  public TextVarBuilder setLabelText( String labelText ) {
    this.labelText = labelText;
    return this;
  }

  public TextVarBuilder setLabelWidth( int labelWidth ) {
    this.labelWidth = labelWidth;
    return this;
  }

  public TextVarBuilder setDefaultText( String defaultText ) {
    this.defaultText = defaultText;
    return this;
  }

  public TextVarBuilder setEchoChar( char echoChar ) {
    this.echoChar = echoChar;
    return this;
  }

  public TextVarBuilder setTop( Control top ) {
    this.top = top;
    return this;
  }

  public TextVarBuilder setLeft( Control left ) {
    this.left = left;
    return this;
  }

  public TextVarBuilder setModifyListener( ModifyListener modifyListener ) {
    this.modifyListener = modifyListener;
    return this;
  }
}
