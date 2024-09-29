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


package org.pentaho.di.baserver.utils.widgets.fields;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.baserver.utils.BAServerCommonDialog;
import org.pentaho.di.baserver.utils.widgets.LabelBuilder;
import org.pentaho.di.baserver.utils.widgets.WidgetBuilder;
import org.pentaho.di.ui.core.PropsUI;

import java.util.ArrayList;
import java.util.List;

public abstract class FieldBuilder<T extends Control> extends WidgetBuilder<Field<T>> {
  protected String label;
  protected List<ModifyListener> listeners = new ArrayList<ModifyListener>();

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

  protected void prepareControl( Field<T> field, WidgetBuilder<T> controlBuilder ) {
    Label l = new LabelBuilder( field, props )
        .setText( label + ":" )
        .setLeftPlacement( 0 )
        .build();
    field.setLabel( l );

    field.setControl(
        controlBuilder
            .setTop( l )
            .setTopMargin( BAServerCommonDialog.SMALL_MARGIN )
            .setLeftPlacement( 0 )
            .setRightPlacement( 100 )
            .build()
    );
  }
}
