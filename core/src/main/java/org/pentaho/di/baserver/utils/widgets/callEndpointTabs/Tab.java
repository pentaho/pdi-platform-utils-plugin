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


package org.pentaho.di.baserver.utils.widgets.callEndpointTabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.baserver.utils.BAServerCommonDialog;
import org.pentaho.di.baserver.utils.CallEndpointMeta;
import org.pentaho.di.ui.core.PropsUI;

public abstract class Tab extends Composite {
  protected static Class<?> PKG = CallEndpointMeta.class;
  protected static final int LEFT_PLACEMENT = 0;
  protected static final int RIGHT_PLACEMENT = 100;

  public Tab( CTabFolder tabFolder, String label, PropsUI props ) {
    super( tabFolder, SWT.NONE );

    FormLayout formLayout = new FormLayout();
    formLayout.marginLeft = BAServerCommonDialog.MEDIUM_MARGIN;
    formLayout.marginRight = BAServerCommonDialog.MEDIUM_MARGIN;
    formLayout.marginTop = BAServerCommonDialog.LARGE_MARGIN;
    formLayout.marginBottom = BAServerCommonDialog.LARGE_MARGIN;
    setLayout( formLayout );

    props.setLook( this );

    CTabItem tabItem = new CTabItem( tabFolder, SWT.NONE );
    tabItem.setText( label );
    tabItem.setControl( this );
  }

  public boolean isValid() {
    return true;
  }

  public abstract void loadData( CallEndpointMeta meta );

  public abstract void saveData( CallEndpointMeta meta );
}
