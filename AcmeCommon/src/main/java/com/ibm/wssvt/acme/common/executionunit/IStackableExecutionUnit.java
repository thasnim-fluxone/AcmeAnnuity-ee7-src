package com.ibm.wssvt.acme.common.executionunit;

import java.util.Map;

public interface IStackableExecutionUnit extends IExecutionUnit{
	public Map<String, Object> getStackMap();
	public void setStackMap(Map<String, Object> stackMap);

}
