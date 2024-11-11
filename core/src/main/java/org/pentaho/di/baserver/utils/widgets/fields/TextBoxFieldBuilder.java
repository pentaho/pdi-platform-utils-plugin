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
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.baserver.utils.widgets.TextBoxBuilder;
import org.pentaho.di.ui.core.PropsUI;

public class TextBoxFieldBuilder extends FieldBuilder<Text> {
  public TextBoxFieldBuilder( Composite parent, PropsUI props ) {
    super( parent, props );
  }

  @Override protected Field<Text> createWidget( Composite parent ) {
    final Field<Text> field = new Field<Text>( parent, SWT.NONE );
    prepareControl( field, new TextBoxBuilder( field, props ) );
    for ( ModifyListener ml : listeners ) {
      field.getControl().addModifyListener( ml );
    }

    return field;
  }
}
