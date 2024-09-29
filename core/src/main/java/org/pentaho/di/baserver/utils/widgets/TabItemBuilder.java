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
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.ui.core.PropsUI;

public class TabItemBuilder extends WidgetBuilder<Composite> {
  private String text;

  public TabItemBuilder setText( String text ) {
    this.text = text;
    return this;
  }

  public TabItemBuilder( CTabFolder parent, PropsUI props ) {
    super( parent, props );
  }

  @Override
  protected Composite createWidget( Composite parent ) {
    setTopPlacement( 0 );
    setBottomPlacement( 100 );
    setLeftPlacement( 0 );
    setRightPlacement( 100 );
    Composite serverTabItemControl = createServerTabItemControl( parent, SWT.NONE );
    serverTabItemControl.setLayout( new FormLayout() );
    // create group
    CTabItem tabItem = createCTabItem( (CTabFolder) parent, SWT.NONE );
    tabItem.setText( this.text );
    tabItem.setControl( serverTabItemControl );

    return serverTabItemControl;
  }

  protected Composite createServerTabItemControl( Composite parent, int i ) {
    return new Composite( parent, i );
  }

  protected CTabItem createCTabItem( CTabFolder parent, int i ) {
    return new CTabItem( parent, i );
  }
}
