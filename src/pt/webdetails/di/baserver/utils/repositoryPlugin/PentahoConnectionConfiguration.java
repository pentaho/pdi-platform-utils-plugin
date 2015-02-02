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

package pt.webdetails.di.baserver.utils.repositoryPlugin;

import org.pentaho.metastore.persist.MetaStoreAttribute;
import org.pentaho.metastore.persist.MetaStoreElementType;

@MetaStoreElementType( name = "PentahoConnectionConfiguration", description = "A Pentaho Connection Configuration" )
public final class PentahoConnectionConfiguration implements IPentahoConnectionConfiguration {

  // region Properties
  @Override
  public String getName() {
    return this.name;
  }
  public PentahoConnectionConfiguration setName( String name ) {
    this.name = name;
    return this;
  }
  private String name;

  @Override
  public String getServerUrl() {
    return this.serverUrl;
  }
  @Override
  public PentahoConnectionConfiguration setServerUrl( String serverUrl ) {
    this.serverUrl = serverUrl;
    return this;
  }
  @MetaStoreAttribute()
  private String serverUrl;

  @Override
  public String getUserName() {
    return this.userName;
  }
  @Override
  public PentahoConnectionConfiguration setUserName( String userName ) {
    this.userName = userName;
    return this;
  }
  @MetaStoreAttribute()
  private String userName;

  @Override
  public String getPassword() {
    return this.password;
  }
  @Override
  public PentahoConnectionConfiguration setPassword( String password ) {
    this.password = password;
    return this;
  }
  @MetaStoreAttribute( password = true )
  private String password;
  // endregion

  // region Constructor
  public PentahoConnectionConfiguration() {
    this.setName( "" );
    this.setServerUrl( "" );
    this.setUserName( "" );
    this.setPassword( "" );
  }
  // endregion

  @Override
  public PentahoConnectionConfiguration clone() {
    return new PentahoConnectionConfiguration()
      .setName( this.getName() )
      .setServerUrl( this.getServerUrl() )
      .setUserName( this.getUserName() )
      .setPassword( this.getPassword() );
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    PentahoConnectionConfiguration that = (PentahoConnectionConfiguration) o;

    if ( name != null ? !name.equals( that.name ) : that.name != null ) {
      return false;
    }
    if ( password != null ? !password.equals( that.password ) : that.password != null ) {
      return false;
    }
    if ( serverUrl != null ? !serverUrl.equals( that.serverUrl ) : that.serverUrl != null ) {
      return false;
    }
    if ( userName != null ? !userName.equals( that.userName ) : that.userName != null ) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }
}
