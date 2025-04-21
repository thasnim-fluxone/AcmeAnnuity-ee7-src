package com.ibm.wssvt.acme.common.client.ui.gui.model;

import com.ibm.wssvt.acme.common.envconfig.AdapterConfiguration;
import com.ibm.wssvt.acme.common.envconfig.EnvConfigStringParameterizable;

public class AdapterConfigurationDecorator extends AdapterConfiguration implements Cloneable{		
	private static final long serialVersionUID = 383064329260277952L;
	
	public AdapterConfigurationDecorator(AdapterConfiguration parent) {
		super.setClassName(parent.getClassName());
		super.setConfiguration(parent.getConfiguration());
		super.setId(parent.getId());		
	}
	
	private String getShortName(String className){
		if (className ==null || className.trim().length() == 0) {
			return null;
		}
		
		int idx = className.lastIndexOf(".");
		if (idx > 0) {
			return className.substring(idx+1);
		}
		
		return null;		
	}
	
	public String toString(){
		String friendlyName = getConfiguration().getParameterValue("DOC.friendlyName");
		if (friendlyName != null && friendlyName.trim().length() >0) {
			return friendlyName;
		}else{
			return getShortName(super.getClassName());	
		}
		
	}
	
	@Override
	public AdapterConfigurationDecorator clone(){
		AdapterConfigurationDecorator copy = new AdapterConfigurationDecorator(this);		
		copy.setConfiguration(ParametarizableCopier.getCopy(getConfiguration(), new EnvConfigStringParameterizable()));
		return copy;
	}
	
}
