/*
 *  Copyright 2002 - 2015 Webdetails, a Pentaho company.  All rights reserved.
 *
 *  This software was developed by Webdetails and is provided under the terms
 *  of the Mozilla Public License, Version 2.0, or any later version. You may not use
 *  this file except in compliance with the license. If you need a copy of the license,
 *  please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 *  Software distributed under the Mozilla Public License is distributed on an "AS IS"
 *  basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 *  the license for the specific language governing your rights and limitations.
 */

package pt.webdetails.di.baserver.utils.repositoryPlugin.ui;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ToolbarControllerTest {

  @Test
  public void testAddDefaultExtensionIfMissingWithMissingExtension()  {
    String fileNameWithoutExtension = "myName";
    String[] extensions = { ".ktr", ".kjb" };
    String defaultExtension = "ktr";
    ToolbarController controller = new ToolbarController();

    String expectedFileName = fileNameWithoutExtension + "." + defaultExtension;
    String actualFileName = controller.addDefaultExtensionIfMissing( fileNameWithoutExtension, Arrays.asList( extensions ), defaultExtension  );

    assertThat( actualFileName, is( equalTo( expectedFileName )) );
  }

  @Test
  public void testAddDefaultExtensionIfMissingWithUnknownExtension()  {
    String fileNameWithUnknownExtension = "myName.xml";
    String[] extensions = { ".ktr", ".kjb" };
    String defaultExtension = "ktr";
    ToolbarController controller = new ToolbarController();

    String expectedFileName = fileNameWithUnknownExtension + "." + defaultExtension;
    String actualFileName = controller.addDefaultExtensionIfMissing( fileNameWithUnknownExtension, Arrays.asList( extensions ), defaultExtension  );

    assertThat( actualFileName, is( equalTo( expectedFileName )) );
  }

  @Test
  public void testAddDefaultExtensionIfMissingWithKnownExtensionDifferentFromDefault()  {
    String fileNameWithKnownExtension = "myName.kjb";
    String[] extensions = { ".ktr", ".kjb" };
    String defaultExtension = "ktr";
    ToolbarController controller = new ToolbarController();

    String expectedFileName = fileNameWithKnownExtension;
    String actualFileName = controller.addDefaultExtensionIfMissing( fileNameWithKnownExtension, Arrays.asList( extensions ), defaultExtension  );

    assertThat( actualFileName, is( equalTo( expectedFileName )) );
  }

}
