package com.ibm.wssvt.acme.annuity.struts.actions;

import com.ibm.wssvt.acme.annuity.common.util.AnnuityServerConfig;


public class ServerConfiguration extends AnnuitySupport{

	private static final long serialVersionUID = 1L;
	private String configFile;
	
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	public String getConfigFile() {
		return configFile;
	}
	
	public String input(){
		return INPUT;
	}
	
	public String init(){
		try{
			AnnuityServerConfig.init(getConfigFile());		
			AnnuityServerConfig.getInstance().getConfigs().put("configFile", getConfigFile());
			return SUCCESS;
		}catch (Exception e){
			super.addActionError("Failed to initialize the server configuration.  Error is: " + e.getMessage());
			return ERROR;
		}
	}
		
}