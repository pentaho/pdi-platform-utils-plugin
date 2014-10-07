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
import org.pentaho.di.ui.core.PropsUI;

/**
 * @author Marco Vala
 */
public final class GroupBuilder extends WidgetBuilder<Group> {

  private String text = "";

  public String getText() {
    return this.text;
  }

  public GroupBuilder setText( String text ) {
    this.text = text;
    return this;
  }

  public GroupBuilder( Composite parent, PropsUI props ) {
    super( parent, props );
  }

  @Override
  protected Group createWidget( Composite parent ) {
    // create group
    Group group = new Group( this.parent, SWT.NONE );
    group.setText( this.text );

    // create a new form layout for the group
    FormLayout layout = new FormLayout();
    layout.marginWidth = Const.FORM_MARGIN;
    layout.marginHeight = Const.FORM_MARGIN;
    group.setLayout( layout );

    return group;
  }
}
