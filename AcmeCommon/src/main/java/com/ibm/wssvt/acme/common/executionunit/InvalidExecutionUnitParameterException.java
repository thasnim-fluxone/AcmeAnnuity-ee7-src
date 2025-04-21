package com.ibm.wssvt.acme.common.executionunit;


import com.ibm.wssvt.acme.common.exception.AcmeClientException;
/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class InvalidExecutionUnitParameterException extends
		AcmeClientException {

	private static final long serialVersionUID = -4613601817416421035L;

	public InvalidExecutionUnitParameterException() {
		// TODO Auto-generated constructor stub
	}

	public InvalidExecutionUnitParameterException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidExecutionUnitParameterException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InvalidExecutionUnitParameterException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
