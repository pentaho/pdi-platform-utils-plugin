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

package org.pentaho.di.baserver.utils.widgets.callEndpointTabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.baserver.utils.CallEndpointMeta;
import org.pentaho.di.ui.core.PropsUI;

public abstract class Tab extends Composite {
  protected static Class<?> PKG = CallEndpointMeta.class;
  protected static final int LEFT_PLACEMENT = 0;
  protected static final int RIGHT_PLACEMENT = 100;

  public Tab( CTabFolder tabFolder, String label, PropsUI props ) {
    super( tabFolder, SWT.NONE );

    setLayout( new FormLayout() );

    props.setLook( this );

    final FormData formData = new FormData();
    formData.top = new FormAttachment( 0 );
    formData.bottom = new FormAttachment( 100 );
    formData.left = new FormAttachment( 0 );
    formData.right = new FormAttachment( 100 );
    setLayoutData( formData );

    CTabItem tabItem = new CTabItem( tabFolder, SWT.NONE );
    tabItem.setText( label );
    tabItem.setControl( this );
  }

  public abstract void loadData( CallEndpointMeta meta );

  public void refresh() {}

  public abstract void saveData( CallEndpointMeta meta );
}
