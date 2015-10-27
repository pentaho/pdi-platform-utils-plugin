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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.ui.core.PropsUI;

import java.util.ArrayList;
import java.util.List;

public class CheckBoxBuilder extends WidgetBuilder<Button> {

  private String text;
  private List<SelectionListener> selectionListeners = new ArrayList<SelectionListener>();

  public CheckBoxBuilder( Composite parent, PropsUI props ) {
    super( parent, props );
  }

  public CheckBoxBuilder setText( String text ) {
    this.text = text;
    return this;
  }

  public CheckBoxBuilder addSelectionListener( SelectionListener selectionListener ) {
    selectionListeners.add( selectionListener );
    return this;
  }

  @Override
  protected Button createWidget( Composite parent ) {
    Button checkBox = createButton( parent, SWT.CHECK );
    checkBox.setText( text );
    for ( SelectionListener selectionListener : selectionListeners ) {
      checkBox.addSelectionListener( selectionListener );
    }
    return checkBox;
  }

  protected Button createButton( Composite parent, int i ) {
    return new Button( parent, i );
  }
}
