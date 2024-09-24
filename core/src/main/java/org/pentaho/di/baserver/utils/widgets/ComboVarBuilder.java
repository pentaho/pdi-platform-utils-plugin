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
 * Copyright 2006 - 2017 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.di.baserver.utils.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.ComboVar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ComboVarBuilder extends WidgetBuilder<ComboVar> {

  private VariableSpace variableSpace;
  private List<String> items = new ArrayList<String>();

  public ComboVarBuilder addItem( String item ) {
    if ( item != null ) {
      this.items.add( item );
      Collections.sort( this.items );
    }
    return this;
  }

  public ComboVarBuilder addAllItems( Collection<String> items ) {
    this.items.addAll( items );
    Collections.sort( this.items );
    return this;
  }

  public ComboVarBuilder( Composite parent, PropsUI props, VariableSpace variableSpace ) {
    super( parent, props );
    this.variableSpace = variableSpace;
  }

  @Override
  protected ComboVar createWidget( Composite parent ) {
    ComboVar comboVar = createComboVar( this.variableSpace, parent, SWT.BORDER );
    String[] itemsArray = this.items.toArray( new String[ items.size() ] );
    comboVar.setItems( itemsArray );
    return comboVar;
  }

  protected ComboVar createComboVar( VariableSpace variableSpace, Composite parent, int i ) {
    return new ComboVar( variableSpace, parent, i );
  }
}
