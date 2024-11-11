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
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.baserver.utils.widgets.TextVarBuilder;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.TextVar;

public class TextVarFieldBuilder extends FieldBuilder<TextVar> {
  private VariableSpace variableSpace;

  public TextVarFieldBuilder( Composite parent, PropsUI props ) {
    super( parent, props );
  }

  public TextVarFieldBuilder setVariableSpace( VariableSpace variableSpace ) {
    this.variableSpace = variableSpace;
    return this;
  }

  @Override protected Field<TextVar> createWidget( Composite parent ) {
    final Field<TextVar> field = new Field<TextVar>( parent, SWT.NONE );
    prepareControl( field, new TextVarBuilder( field, props, variableSpace ) );
    for ( ModifyListener ml : listeners ) {
      field.getControl().addModifyListener( ml );
    }

    return field;
  }
}
