package com.ibm.wssvt.acme.common.exception;

import com.ibm.wssvt.acme.common.envconfig.ExceptionHandlerConfiguration;

public interface IExceptionHandler {

	public  IExceptionHandlerResult handleException(ExceptionHandlerConfiguration exceptionHandlerConfiguration, Exception exception);
}
