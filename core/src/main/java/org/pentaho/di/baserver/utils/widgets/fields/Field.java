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

import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class Field<T extends Control> extends Composite {
  private Label label;
  private T control;

  public Field( Composite composite, int i ) {
    super( composite, i );
    setLayout( new FormLayout() );
  }

  public Label getLabel() {
    return label;
  }

  public T getControl() {
    return control;
  }

  public void setLabel( Label label ) {
    this.label = label;
  }

  public void setControl( T control ) {
    this.control = control;
  }
}
