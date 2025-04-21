package com.ibm.wssvt.acme.common.executionunit;

import java.util.List;

public interface IExecutionUnitEvent {

	public void addException(Exception o);

	public boolean removeException(Exception o);

	public List<Exception> getExceptions();

	public void setExecutionUnit(IExecutionUnit executionUnit);

	public IExecutionUnit getExecutionUnit();

	public void clearAll();

}