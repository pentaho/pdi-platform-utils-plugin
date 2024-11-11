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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.ui.core.PropsUI;

import java.util.ArrayList;
import java.util.List;

public class RadioBuilder extends WidgetBuilder<Button> {

  private String text;
  private List<SelectionListener> selectionListeners = new ArrayList<SelectionListener>();

  public RadioBuilder( Composite parent, PropsUI props ) {
    super( parent, props );
  }

  public RadioBuilder setText( String text ) {
    this.text = text;
    return this;
  }

  public RadioBuilder addSelectionListener( SelectionListener selectionListener ) {
    selectionListeners.add( selectionListener );
    return this;
  }

  @Override
  protected Button createWidget( Composite parent ) {
    Button radio = createButton( parent, SWT.RADIO );
    radio.setText( text );
    for ( SelectionListener selectionListener : selectionListeners ) {
      radio.addSelectionListener( selectionListener );
    }
    return radio;
  }

  protected Button createButton( Composite parent, int i ) {
    return new Button( parent, i );
  }
}
