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
package org.pentaho.di.baserver.utils.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.ui.core.PropsUI;

public final class TabFolderBuilder extends WidgetBuilder<CTabFolder> {

  private String text = "";

  public String getText() {
    return this.text;
  }

  public TabFolderBuilder setText( String text ) {
    this.text = text;
    return this;
  }

  public TabFolderBuilder( Composite parent, PropsUI props ) {
    super( parent, props );
  }

  @Override
  protected CTabFolder createWidget( Composite parent ) {

    // create tab folder
    CTabFolder tabFolder = new CTabFolder( this.parent, SWT.BORDER );

    for ( int i = 0; i < 6; i++ ) {
      CTabItem item = new CTabItem( tabFolder, SWT.NULL );
      item.setText ( "Tab Item " + i );
      //Text text = new Text( tabFolder, SWT.BORDER | SWT.MULTI );
      //text.setText( "Content for Item " + i );
      //item.setControl( text );
    }

    /*
    this.tabFolder = new TabFolderBuilder( shell, props )
      .setTop( this.stepName )
      .setLeftPlacement( 0 )
      .setRightPlacement( 100 )
      .setBottomPlacement( 100 )
      .setBottomMargin( 50 )
      .build();

    this.tabFolder.setSimple( false );

    this.tabComp = new Composite( this.tabFolder, SWT.NONE );
    this.props.setLook( this.tabComp );
    FormLayout fileLayout = new FormLayout();
    fileLayout.marginWidth = 3;
    fileLayout.marginHeight = 3;
    this.tabComp.setLayout( fileLayout );

    this.tabFolder.getItem( 0 ).setControl( this.tabComp );
    */

    // create a new form layout for the group
//    FormLayout layout = new FormLayout();
//    layout.marginWidth = Const.FORM_MARGIN;
//    layout.marginHeight = Const.FORM_MARGIN;
//    group.setLayout( layout );

    return tabFolder;
  }
}
