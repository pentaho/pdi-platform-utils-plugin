/*!
* Copyright 2002 - 2014 Webdetails, a Pentaho company.  All rights reserved.
*
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package org.pentaho.di.baserver.utils.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.ui.core.PropsUI;

/**
 * @author Marco Vala
 */
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
