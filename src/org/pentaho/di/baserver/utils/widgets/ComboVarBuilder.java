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
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.ComboVar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Marco Vala
 */
public final class ComboVarBuilder extends WidgetBuilder<ComboVar> {

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
    ComboVar comboVar = new ComboVar( this.variableSpace, this.parent, SWT.BORDER );
    String[] itemsArray = this.items.toArray( new String[ items.size() ] );
    comboVar.setItems( itemsArray );
    return comboVar;
  }
}
