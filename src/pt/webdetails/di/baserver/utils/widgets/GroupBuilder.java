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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.pentaho.di.core.Const;

/**
 * @author Marco Vala
 */
public final class GroupBuilder {

  private Composite parent;
  private String labelText = "";
  private Control top = null;

  public GroupBuilder( Composite parent ) {
    this.parent = parent;
  }

  public Group build() {

    // create group
    Group group = new Group( this.parent, SWT.NONE );
    group.setText( this.labelText );

    // create a new form layout for the group
    FormLayout layout = new FormLayout();
    layout.marginWidth = Const.FORM_MARGIN;
    layout.marginHeight = Const.FORM_MARGIN;
    group.setLayout( layout );

    // place group below the previous control
    FormData data = new FormData();
    if ( this.top != null ) {
      data.top = new FormAttachment( this.top, Const.MARGIN );
    }
    group.setLayoutData( data );

    return group;
  }

  public GroupBuilder setLabelText( String labelText ) {
    this.labelText = labelText;
    return this;
  }

  public GroupBuilder setTop( Control top ) {
    this.top = top;
    return this;
  }
}
