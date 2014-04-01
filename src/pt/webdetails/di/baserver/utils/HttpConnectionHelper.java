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

package pt.webdetails.di.baserver.utils;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.pentaho.di.cluster.SlaveConnectionManager;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleStepException;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Marco Vala
 */
public final class HttpConnectionHelper {

  public static String callHttp( String url, String user, String password ) throws IOException, KettleStepException {
    HttpClient httpclient = SlaveConnectionManager.getInstance().createHttpClient();
    HttpMethod method = new GetMethod( url );
    httpclient.getParams().setAuthenticationPreemptive( true );
    Credentials credentials = new UsernamePasswordCredentials( user, password );
    httpclient.getState().setCredentials( AuthScope.ANY, credentials );
    HostConfiguration hostConfiguration = new HostConfiguration();
    int status = httpclient.executeMethod( hostConfiguration, method );

    if ( status == 401 ) {
      throw new KettleStepException( "Authentication Error" );
    }

    String body = null;
    if ( status != -1 ) {
      String encoding = "";
      String contentType = method.getResponseHeader( "Content-Type" ).getValue();
      if ( contentType != null && contentType.contains( "charset" ) ) {
        encoding = contentType.replaceFirst( "^.*;\\s*charset\\s*=\\s*", "" ).replace( "\"", "" ).trim();
      }

      // get the response
      InputStreamReader inputStreamReader = null;
      if ( !Const.isEmpty( encoding ) ) {
        inputStreamReader = new InputStreamReader( method.getResponseBodyAsStream(), encoding );
      } else {
        inputStreamReader = new InputStreamReader( method.getResponseBodyAsStream() );
      }
      StringBuilder bodyBuffer = new StringBuilder();
      int c;
      while ( ( c = inputStreamReader.read() ) != -1 ) {
        bodyBuffer.append( (char) c );
      }
      inputStreamReader.close();
      body = bodyBuffer.toString();
    }
    return body;
  }
}
