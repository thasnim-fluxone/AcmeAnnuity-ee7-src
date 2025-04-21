package com.ibm.wssvt.acme.common.client.ui.gui.model;

import java.util.Set;

import com.ibm.wssvt.acme.common.bean.Parameterizable;

public class ParametarizableCopier {
		
	public static Parameterizable<String, String> getCopy(Parameterizable<String, String> in, Parameterizable<String, String> copy){	
		if (in == null || in.getParameters() == null){
			return null;
		}
		if (copy == null || copy.getParameters() == null){
			return null;
		}
		
		Set<String> keys = in.getParameters().keySet();
		for (String key : keys) {
			copy.addParameter(key, in.getParameterValue(key));
		}
		return copy;		
	}
	
	
}
