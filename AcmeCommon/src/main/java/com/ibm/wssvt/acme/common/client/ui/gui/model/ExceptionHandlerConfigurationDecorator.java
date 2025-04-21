package com.ibm.wssvt.acme.common.client.ui.gui.model;

import com.ibm.wssvt.acme.common.envconfig.EnvConfigStringParameterizable;
import com.ibm.wssvt.acme.common.envconfig.ExceptionHandlerConfiguration;

public class ExceptionHandlerConfigurationDecorator extends ExceptionHandlerConfiguration implements Cloneable{
		
	private static final long serialVersionUID = -4962720928417471219L;
	
	public ExceptionHandlerConfigurationDecorator(){
		
	}
	public ExceptionHandlerConfigurationDecorator(ExceptionHandlerConfiguration parent){
		super.setExceptionClassName(parent.getExceptionClassName());
		super.setConfiguration(parent.getConfiguration());
		super.setHandlerClassName(parent.getHandlerClassName());
	}
	public String getShortName(String handlerClassName){
		if (handlerClassName ==null || handlerClassName.trim().length() == 0) {
			return null;
		}
		
		int idx = handlerClassName.lastIndexOf(".");
		if (idx > 0) {
			return handlerClassName.substring(idx+1);
		}
		
		return null;		
	}
	
	
	public String toString(){
		String friendlyName = getConfiguration().getParameterValue("DOC.friendlyName");
		if (friendlyName != null && friendlyName.trim().length() >0) {
			return friendlyName;
		}else{
			return getShortName(super.getHandlerClassName());	
		}		
	}
	
	@Override
	public ExceptionHandlerConfigurationDecorator clone(){
		ExceptionHandlerConfigurationDecorator copy = new ExceptionHandlerConfigurationDecorator(this);			
		copy.setConfiguration(ParametarizableCopier.getCopy(getConfiguration(), new EnvConfigStringParameterizable()));

		return copy;
	}
		
}
