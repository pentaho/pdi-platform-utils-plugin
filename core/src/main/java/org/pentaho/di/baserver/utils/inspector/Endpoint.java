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


package org.pentaho.di.baserver.utils.inspector;

import org.pentaho.di.baserver.utils.web.Http;
import org.pentaho.di.baserver.utils.web.HttpParameter;

import java.util.HashSet;
import java.util.Set;

public class Endpoint implements Comparable<Endpoint> {
  // region Fields

  private String id;
  private String path;
  private Http httpMethod;
  private Set<ParamDefinition> paramDefinitions;
  private boolean deprecated;
  private boolean supported;
  private String documentation;

  // endregion

  // region Getters and Setters

  public String getId() {
    return this.id;
  }

  public void setId( String id ) {
    this.id = id;
  }

  public String getPath() {
    return this.path;
  }

  public void setPath( String path ) {
    this.path = path;
  }

  public Http getHttpMethod() {
    return this.httpMethod;
  }

  public void setHttpMethod( Http httpMethod ) {
    this.httpMethod = httpMethod;
  }

  public Set<ParamDefinition> getParamDefinitions() {
    return this.paramDefinitions;
  }

  public ParamDefinition getParameterDefinition( String paramName ) {
    if ( paramName == null ) {
      return null;
    }
    return paramDefinitions.stream().filter( description -> paramName.equals( description.getName() ) ).findAny().orElse( null );
  }

  public HttpParameter.ParamType getParameterType( String paramName ) {
    ParamDefinition paramDefinition = getParameterDefinition( paramName );
    return paramDefinition != null ? paramDefinition.getParamType() : null;
  }

  public boolean isDeprecated() {
    return deprecated;
  }

  public void setDeprecated( boolean deprecated ) {
    this.deprecated = deprecated;
  }

  public boolean isSupported() {
    return supported;
  }

  public void setSupported( boolean supported ) {
    this.supported = supported;
  }

  public String getDocumentation() {
    return documentation;
  }

  public void setDocumentation( String doc ) {
    this.documentation = doc;
  }

  // endregion

  // region Constructors

  public Endpoint() {
    this.paramDefinitions = new HashSet<ParamDefinition>();
  }

  // endregion

  // region Methods

  @Override
  public int compareTo( Endpoint endpoint ) {
    if ( this == endpoint ) {
      return 0;
    }
    return this.id.compareTo( endpoint.id );
  }

  @Override
  public boolean equals( Object obj ) {
    if ( obj == this ) {
      return true;
    }
    if ( obj == null || !( obj instanceof Endpoint ) ) {
      return false;
    }

    Endpoint other = (Endpoint) obj;
    return this.id.equals( other.id );
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }

  // endregion
}
