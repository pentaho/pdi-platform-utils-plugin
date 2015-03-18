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

package org.pentaho.di.baserver.utils.widgets.fields;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.baserver.utils.widgets.LabelBuilder;
import org.pentaho.di.baserver.utils.widgets.WidgetBuilder;
import org.pentaho.di.ui.core.PropsUI;

import java.util.ArrayList;
import java.util.List;

public abstract class FieldBuilder<T extends Control> extends WidgetBuilder<Field<T>> {
  protected String label;
  protected List<ModifyListener> listeners = new ArrayList<ModifyListener>();
  protected int fieldRightPlacement = 100;

  protected FieldBuilder( Composite parent, PropsUI props ) {
    super( parent, props );
  }

  public FieldBuilder<T> setLabel( String label ) {
    this.label = label;
    return this;
  }

  public FieldBuilder<T> addModifyListener( ModifyListener listener ) {
    listeners.add( listener );
    return this;
  }

  public FieldBuilder<T> setFieldRightPlacement( int fieldRightPlacement ) {
    this.fieldRightPlacement = fieldRightPlacement;
    return this;
  }

  protected void prepareControl( Field<T> field, WidgetBuilder<T> controlBuilder ) {
    Label l = new LabelBuilder( field, props )
        .setText( label + ":" )
        .setLeftPlacement( 0 )
        .build();
    field.setLabel( l );

    field.setControl(
        controlBuilder
            .setTop( l )
            .setLeftPlacement( 0 )
            .setRightPlacement( fieldRightPlacement )
            .build()
    );
  }
}
