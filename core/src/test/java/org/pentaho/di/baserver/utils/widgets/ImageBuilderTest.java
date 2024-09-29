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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.ui.core.PropsUI;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class ImageBuilderTest {
  ImageBuilder imageBuilder, imageBuilderSpy;
  Composite parent = mock( Composite.class );
  PropsUI propsUI = mock( PropsUI.class );

  @Before
  public void setUp() throws Exception {
    imageBuilder = new ImageBuilder( parent, propsUI );
    imageBuilderSpy = spy( imageBuilder );
  }

  @Test
  public void testCreateWidget() throws Exception {
    Label labelMock = mock( Label.class );

    doReturn( labelMock ).when( imageBuilderSpy ).createLabel( Mockito.<Composite>any(), anyInt() );
    doReturn( null ).when( imageBuilderSpy ).setImage( Mockito.<Image>any() );

    ImageBuilder m = mock( ImageBuilder.class );

    Label label = imageBuilderSpy.createWidget( parent );
    verify( label, times( 1 ) ).setImage( Mockito.<Image>any() );
  }
}
