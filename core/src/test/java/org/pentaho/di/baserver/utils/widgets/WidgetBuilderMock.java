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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.ui.core.PropsUI;

public class WidgetBuilderMock extends WidgetBuilder {
  protected WidgetBuilderMock( Composite parent, PropsUI props ) {
    super( parent, props );
  }

  @Override protected Control createWidget( Composite parent ) {

    Display display = Display.getDefault();
    Shell shell = new Shell( display, SWT.SHELL_TRIM );
    Composite panel = new Composite( shell, SWT.NONE );
    return new Label( panel, 0 );
  }
}
