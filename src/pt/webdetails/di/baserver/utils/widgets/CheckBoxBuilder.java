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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.core.Const;
import org.pentaho.di.ui.core.PropsUI;

/**
 * @author Marco Vala
 */
public final class CheckBoxBuilder extends WidgetBuilder {

  private String labelText = "";
  private int labelWidth = 0;
  private Control top = null;

  public CheckBoxBuilder( PropsUI props, Composite parent ) {
    super( props, parent );
  }

  public Button build() {
    // create label for check box
    Label label = new Label( parent, SWT.NONE );
    label.setText( labelText );
    this.props.setLook( label );

    // create check box
    Button checkBox = new Button( parent, SWT.CHECK );
    this.props.setLook( checkBox );

    // place label
    FormData data;
    data = new FormData();
    if ( labelWidth > 0 ) {
      data.width = labelWidth;
    }
    data.top = new FormAttachment( top, Const.MARGIN );
    label.setLayoutData( data );

    // place check box next to label
    data = new FormData();
    data.top = new FormAttachment( top, Const.MARGIN );
    data.left = new FormAttachment( label, Const.MARGIN );
    checkBox.setLayoutData( data );

    return checkBox;
  }

  public CheckBoxBuilder setLabelText( String labelText ) {
    this.labelText = labelText;
    return this;
  }

  public CheckBoxBuilder setLabelWidth( int labelWidth ) {
    this.labelWidth = labelWidth;
    return this;
  }

  public CheckBoxBuilder setTop( Control top ) {
    this.top = top;
    return this;
  }
}
