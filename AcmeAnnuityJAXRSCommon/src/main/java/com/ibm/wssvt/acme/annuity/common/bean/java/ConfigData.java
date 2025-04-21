//***********************************************************
// The ConfigData object used for testing 
// @Singleton Mbean support in EJB 3.1.
//
// Randy Erickson 07/02/2009
// Amirhossein Kiani 09/28/2009 - Added the ConfigData Map store configuration values
//                               (tests concurrency).
//***********************************************************

package com.ibm.wssvt.acme.annuity.common.bean.java;


import java.util.HashMap;
import java.util.Map;

import com.ibm.wssvt.acme.annuity.common.bean.IConfigData;

public class ConfigData extends AnnuityPersistebleObject implements IConfigData {

    private static final long serialVersionUID = -7113607908478311087L;
    private Map <String, String> configMap = new HashMap <String, String> ();

    public Map<String, String> getConfigMap() {
        return this.configMap;
    }

    public void setConfigMap(Map<String, String> cMap) {
        this.configMap= cMap;       
    }

}
