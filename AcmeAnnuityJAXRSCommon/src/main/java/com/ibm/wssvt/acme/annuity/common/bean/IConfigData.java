//***********************************************************
// The IConfigData interface is for the 
// ConfigData object used for testing 
// @Singleton Mbean support in EJB 3.1.
//
// Randy Erickson 07/02/2009
// Amirhossein Kiani 09/28/2009 - Added the ConfigData Map store configuration values
//                               (tests concurrency).
//***********************************************************

package com.ibm.wssvt.acme.annuity.common.bean;

import java.util.Map;

public interface IConfigData extends IAnnuityObject{

    public Map<String, String> getConfigMap() ;

    public void setConfigMap(Map<String, String> cMap);
	
}