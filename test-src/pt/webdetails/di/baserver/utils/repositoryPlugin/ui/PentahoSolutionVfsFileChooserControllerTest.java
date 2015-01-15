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
import pt.webdetails.di.baserver.utils.repositoryPlugin.Constants;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import static org.mockito.Mockito.*;

public class PentahoSolutionVfsFileChooserControllerTest {

  protected PentahoSolutionVfsFileChooserPanel createPanelMock ( String serverUrl, String username, String password )
    throws MalformedURLException {
    PentahoSolutionVfsFileChooserPanel panelMock = mock( PentahoSolutionVfsFileChooserPanel.class );
    when( panelMock.getServerUrl() ).thenReturn( new URL( serverUrl ) );
    when( panelMock.getUserName() ).thenReturn( username );
    when( panelMock.getPassword() ).thenReturn( password );
    return panelMock;
  }

  @Test
  public void testGetPentahoConnectionStringSimpleInputOk() throws MalformedURLException {
    String vfsScheme = Constants.getInstance().getVfsScheme();
    String serverUrlPath = "myserver.com:8080/pentaho";
    String serverUrlScheme = "http";
    String serverUrl = serverUrlScheme + "://" + serverUrlPath;
    String userName = "JohnSmith";
    String password = "myUncrackablePassword";

    PentahoSolutionVfsFileChooserController controller = new PentahoSolutionVfsFileChooserController();

    String actualConnectionString = controller.getPentahoConnectionString( vfsScheme, new URL( serverUrl ), userName, password );
    String expectedConnectionString = vfsScheme  + ":" + serverUrlScheme + "://" + userName + ":" + password + "@" + serverUrlPath;

    assertThat( actualConnectionString, is( equalTo( expectedConnectionString )) );
  }

}
