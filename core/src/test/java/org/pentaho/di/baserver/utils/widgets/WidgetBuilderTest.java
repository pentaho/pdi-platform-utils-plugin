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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.ui.core.PropsUI;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;

public class WidgetBuilderTest {
  WidgetBuilderMock builder;
  Composite parent = mock( Composite.class );
  PropsUI propsUI = mock( PropsUI.class );

  @Before
  public void setUp() throws Exception {
    builder = new WidgetBuilderMock( parent, propsUI );
  }

  @Test
  public void testSetWidget() throws Exception {
    assertNull( builder.getWidget() );
    Label widget = mock( Label.class );
    builder.setWidget( widget );
    assertEquals( widget, builder.getWidget() );
  }

  @Test
  public void testSetParent() throws Exception {
    Composite composite = mock( Composite.class );
    builder.setParent( composite );
    assertEquals( composite, builder.getParent() );
  }

  @Test
  public void testSetProps() throws Exception {
    PropsUI props = mock( PropsUI.class );
    builder.setProps( props );
    assertEquals( props, builder.getProps() );
  }

  @Test
  public void testSetTop() throws Exception {
    assertNull( builder.getTop() );
    Control control = mock( Control.class );
    builder.setTop( control );
    assertEquals( control, builder.getTop() );
  }

  @Test
  public void testSetLeft() throws Exception {
    assertNull( builder.getLeft() );
    Control control = mock( Control.class );
    builder.setLeft( control );
    assertEquals( control, builder.getLeft() );
  }

  @Test
  public void testSetWidth() throws Exception {
    assertEquals( 0, builder.getWidth() );
    builder.setWidth( 100 );
    assertEquals( 100, builder.getWidth() );
  }

  @Test
  public void testSetHeight() throws Exception {
    assertEquals( 0, builder.getHeight() );
    builder.setHeight( 100 );
    assertEquals( 100, builder.getHeight() );
  }

  @Test
  public void testSetTopPlacement() throws Exception {
    assertEquals( -1, builder.getTopPlacement() );
    builder.setTopPlacement( 100 );
    assertEquals( 100, builder.getTopPlacement() );
  }

  @Test
  public void testSetLeftPlacement() throws Exception {
    assertEquals( -1, builder.getLeftPlacement() );
    builder.setLeftPlacement( 100 );
    assertEquals( 100, builder.getLeftPlacement() );
  }

  @Test
  public void testSetRightPlacement() throws Exception {
    assertEquals( -1, builder.getRightPlacement() );
    builder.setRightPlacement( 100 );
    assertEquals( 100, builder.getRightPlacement() );
  }

  @Test
  public void testSetBottomPlacement() throws Exception {
    assertEquals( -1, builder.getBottomPlacement() );
    builder.setBottomPlacement( 100 );
    assertEquals( 100, builder.getBottomPlacement() );
  }

  @Test
  public void testSetTopMargin() throws Exception {
    assertEquals( 0, builder.getTopMargin() );
    builder.setTopMargin( 100 );
    assertEquals( 100, builder.getTopMargin() );
  }

  @Test
  public void testSetLeftMargin() throws Exception {
    assertEquals( 0, builder.getLeftMargin() );
    builder.setLeftMargin( 100 );
    assertEquals( 100, builder.getLeftMargin() );
  }

  @Test
  public void testSetRightMargin() throws Exception {
    assertEquals( 0, builder.getRightMargin() );
    builder.setRightMargin( 100 );
    assertEquals( 100, builder.getRightMargin() );
  }

  @Test
  public void testSetBottomMargin() throws Exception {
    assertEquals( 0, builder.getBottomMargin() );
    builder.setBottomMargin( 100 );
    assertEquals( 100, builder.getBottomMargin() );
  }

  @Test
  public void testSetEnabled() throws Exception {
    assertTrue( builder.isEnabled() );
    builder.setEnabled( false );
    assertFalse( builder.isEnabled() );
  }
}
