package com.ibm.wssvt.acme.common.executionunit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class ExecutionUnitEvent implements Serializable, IExecutionUnitEvent{

	private static final long serialVersionUID = 1978445384864558975L;
	private IExecutionUnit executionUnit;
	private List<Exception> exceptions = new ArrayList<Exception>();
	
	public void addException(Exception o) {
		this.exceptions.add(o);
	}
	public boolean removeException(Exception o) {
		return this.exceptions.remove(o);
	}
	
	public List<Exception> getExceptions() {
		return exceptions;
	}
		
	public void setExecutionUnit(IExecutionUnit executionUnit){
		this.executionUnit = executionUnit;
	}
	public IExecutionUnit getExecutionUnit(){
		return this.executionUnit;
	}
		
	public void clearAll(){
		this.exceptions.clear();		
	}
}
