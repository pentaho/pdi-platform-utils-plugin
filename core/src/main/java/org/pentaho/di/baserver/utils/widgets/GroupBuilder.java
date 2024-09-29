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
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.pentaho.di.baserver.utils.BAServerCommonDialog;
import org.pentaho.di.ui.core.PropsUI;

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
    layout.marginWidth = BAServerCommonDialog.MEDIUM_MARGIN;
    layout.marginHeight = BAServerCommonDialog.LARGE_MARGIN;
    group.setLayout( layout );

    return group;
  }
}
