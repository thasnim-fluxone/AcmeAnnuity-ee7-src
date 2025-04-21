package com.ibm.wssvt.acme.common.client.ui.gui.model;

import com.ibm.wssvt.acme.common.envconfig.EnvConfigStringParameterizable;
import com.ibm.wssvt.acme.common.envconfig.ExecutionUnitConfiguration;

public class ExecutionUnitConfigurationDecorator extends ExecutionUnitConfiguration implements Cloneable{
	
	private static final long serialVersionUID = -1281799933190438576L;
	
	public ExecutionUnitConfigurationDecorator(ExecutionUnitConfiguration parent) {
		super.setAdapterId(parent.getAdapterId());
		super.setBeansFactoryClass(parent.getBeansFactoryClass());
		super.setClassName(parent.getClassName());
		super.setDescription(parent.getDescription());
		super.setRepeat(parent.getRepeat());
		super.setConfiguration(parent.getConfiguration());			
	}
	
	public String getShortName(String className){
		if (className ==null || className.trim().length() == 0) {
			return null;
		}
		
		int idx = className.lastIndexOf(".");
		if (idx > 0) {
			return className.substring(idx+1);
		}
		
		return null;		
	}
	@Override
	public String toString(){
		String friendlyName = getConfiguration().getParameterValue("DOC.friendlyName");
		if (friendlyName != null && friendlyName.trim().length() >0) {
			return friendlyName;
		}else{
			return getShortName(super.getClassName());	
		}						
	}
	@Override
	public ExecutionUnitConfigurationDecorator clone(){
		ExecutionUnitConfigurationDecorator copy = new ExecutionUnitConfigurationDecorator(this);
		copy.setConfiguration(ParametarizableCopier.getCopy(getConfiguration(), new EnvConfigStringParameterizable()));
		return copy;
	}
		
}
