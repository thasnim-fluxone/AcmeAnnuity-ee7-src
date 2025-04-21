package com.ibm.wssvt.acme.common.exception;

public interface IExceptionHandlerResult {
	public String getMessage();
	public void setMessage(String message);
	public ExceptionAction getAction();
	public void setAction(ExceptionAction exceptionAction);
	
	
}
