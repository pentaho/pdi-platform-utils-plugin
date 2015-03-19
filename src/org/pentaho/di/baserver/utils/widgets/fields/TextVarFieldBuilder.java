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

package org.pentaho.di.baserver.utils.widgets.fields;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.baserver.utils.widgets.LabelBuilder;
import org.pentaho.di.baserver.utils.widgets.TextVarBuilder;
import org.pentaho.di.baserver.utils.widgets.WidgetBuilder;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.TextVar;

public class TextVarFieldBuilder extends FieldBuilder<TextVar> {
  private VariableSpace variableSpace;

  public TextVarFieldBuilder( Composite parent, PropsUI props ) {
    super( parent, props );
    setFieldRightPlacement( 50 );
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
