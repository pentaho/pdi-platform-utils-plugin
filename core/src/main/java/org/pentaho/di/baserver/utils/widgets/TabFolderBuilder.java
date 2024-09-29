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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.ui.core.PropsUI;

public class TabFolderBuilder extends WidgetBuilder<CTabFolder> {

  public TabFolderBuilder( Composite parent, PropsUI props ) {
    super( parent, props );
  }

  @Override
  protected CTabFolder createWidget( Composite parent ) {
    // create tab folder
    CTabFolder tabFolder = createCTabFolder( this.parent, SWT.BORDER );
    return tabFolder;
  }

  protected CTabFolder createCTabFolder( Composite parent, int i ) {
    return new CTabFolder( parent, i );
  }
}
