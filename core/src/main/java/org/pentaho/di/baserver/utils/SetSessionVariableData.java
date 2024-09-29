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


package org.pentaho.di.baserver.utils;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.platform.engine.core.system.PentahoSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetSessionVariableData extends BaseStepData implements StepDataInterface {
  public RowMetaInterface outputRowMeta;
  private List<String> blackList;

  public SetSessionVariableData() {
    super();
    setBlackList();
  }

  protected void setBlackList() {
    try {
      if ( Class.forName( "org.pentaho.platform.engine.core.system.PentahoSystem" ) != null ) {
        String variablesBlackList = PentahoSystem.getSystemSetting( "kettle/plugins/platform-utils-plugin/settings.xml",
            "session-variables-blacklist", "" );
        blackList = Arrays.asList( variablesBlackList.split( "," ) );
      }
    } catch ( ClassNotFoundException e ) {
      blackList = new ArrayList<String>(  );
    } catch ( NoClassDefFoundError e ) {
      blackList = new ArrayList<String>(  );
    }
  }

  public List<String> getBlackList() {
    return this.blackList;
  }
}
