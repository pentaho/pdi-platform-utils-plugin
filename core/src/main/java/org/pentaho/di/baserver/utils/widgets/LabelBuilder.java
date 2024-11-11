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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.ui.core.PropsUI;

public class LabelBuilder extends WidgetBuilder<Label> {

  private String text = "";

  public String getText() {
    return this.text;
  }

  public LabelBuilder setText( final String text ) {
    this.text = text;
    return this;
  }

  public LabelBuilder( Composite parent, PropsUI props ) {
    super( parent, props );
  }

  @Override
  protected Label createWidget( Composite parent ) {
    Label label = createLabel( parent, SWT.RIGHT );
    label.setText( text );
    return label;
  }

  protected Label createLabel( Composite parent, int i ) {
    return new Label( parent, i );
  }
}
