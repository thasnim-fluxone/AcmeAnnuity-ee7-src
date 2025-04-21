package com.ibm.wssvt.acme.common.exception;
/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public abstract class AcmeException extends Exception {

	private static final long serialVersionUID = 1094567224425879198L;

	public AcmeException() {
		// TODO Auto-generated constructor stub
	}

	public AcmeException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public AcmeException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public AcmeException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
