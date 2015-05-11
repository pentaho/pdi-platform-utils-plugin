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
