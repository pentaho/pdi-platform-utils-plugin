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
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.ui.core.PropsUI;

public class BrowserBuilder extends WidgetBuilder<Browser> {

  private String labelText = "";

  public String getLabelText() {
    return this.labelText;
  }

  public BrowserBuilder setLabelText( String labelText ) {
    this.labelText = labelText;
    return this;
  }

  public BrowserBuilder( Composite parent, PropsUI props ) {
    super( parent, props );
  }

  @Override
  protected Browser createWidget( Composite parent ) {
    // create button
    Browser browser = createBrowser( parent, SWT.BORDER );
    browser.setText( this.labelText );
    FontData f = props.getDefaultFont();
    Font font = createFont( parent, f );
    browser.setFont( font );

    return browser;
  }

  protected Browser createBrowser( Composite parent, int i ) {
    return new Browser( parent, i );
  }

  protected Font createFont( Composite parent, FontData fontdata ) {
    return new Font( parent.getDisplay(), fontdata.getName(), fontdata.getHeight(), fontdata.getStyle() );
  }
}
