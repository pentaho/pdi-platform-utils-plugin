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
