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


package org.pentaho.di.baserver.utils.widgets.fields;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.baserver.utils.widgets.ComboVarBuilder;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.ComboVar;

import java.util.ArrayList;
import java.util.List;

public class ComboVarFieldBuilder extends FieldBuilder<ComboVar> {
  private VariableSpace variableSpace;
  private List<SelectionAdapter> selectionListeners = new ArrayList<SelectionAdapter>();

  public ComboVarFieldBuilder( Composite parent, PropsUI props ) {
    super( parent, props );
  }

  public ComboVarFieldBuilder setVariableSpace( VariableSpace variableSpace ) {
    this.variableSpace = variableSpace;
    return this;
  }

  public ComboVarFieldBuilder addSelectionListener( SelectionAdapter selectionListener ) {
    selectionListeners.add( selectionListener );
    return this;
  }

  @Override protected Field<ComboVar> createWidget( Composite parent ) {
    final Field<ComboVar> field = new Field<ComboVar>( parent, SWT.NONE );
    prepareControl( field, new ComboVarBuilder( field, props, variableSpace ) );

    for ( ModifyListener ml : listeners ) {
      field.getControl().addModifyListener( ml );
    }
    for ( SelectionAdapter selectionListener : selectionListeners ) {
      field.getControl().addSelectionListener( selectionListener );
    }

    return field;
  }
}
