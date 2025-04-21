package com.ibm.wssvt.acme.common.executionunit;

import java.util.List;

import com.ibm.wssvt.acme.common.bean.Configrable;

public interface IExecutionUnitStack extends Configrable<String, String> { 	
	public void setDescription(String description);
	public String getDescription();
	public void setStackExecutionUnits(List<IExecutionUnit> stackEUs);
	public List<IExecutionUnit> getStackExecutionUnits();

}
