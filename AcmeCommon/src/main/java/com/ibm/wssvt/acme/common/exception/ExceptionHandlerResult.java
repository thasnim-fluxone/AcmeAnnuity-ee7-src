package com.ibm.wssvt.acme.common.exception;


public class ExceptionHandlerResult implements IExceptionHandlerResult {
	private ExceptionAction action;
	private String message;
	
	public ExceptionAction getAction() {
		return action;
	}
	public void setAction(ExceptionAction action) {
		this.action = action;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	

}
