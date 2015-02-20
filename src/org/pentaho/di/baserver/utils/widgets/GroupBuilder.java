/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License, version 2 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/gpl-2.0.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 *
 * Copyright 2006 - 2015 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.di.baserver.utils.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.pentaho.di.core.Const;
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
    layout.marginWidth = Const.FORM_MARGIN;
    layout.marginHeight = Const.FORM_MARGIN;
    group.setLayout( layout );

    return group;
  }
}
