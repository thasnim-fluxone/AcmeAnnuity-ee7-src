package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.util.Map;

import com.ibm.wssvt.acme.common.executionunit.IStackableExecutionUnit;

public abstract class AbstractAnnuityStackableExecutionUnit 
	extends AbastractAnnuityExecutionUnit implements IStackableExecutionUnit{

	private static final long serialVersionUID = -2189205383198025767L;
	private Map<String, Object> stackMap;
	
	@Override
	public Map<String, Object> getStackMap() {
		return this.stackMap;
	}

	@Override
	public void setStackMap(Map<String, Object> stackMap) {
		this.stackMap = stackMap;
		
	}
	

}
