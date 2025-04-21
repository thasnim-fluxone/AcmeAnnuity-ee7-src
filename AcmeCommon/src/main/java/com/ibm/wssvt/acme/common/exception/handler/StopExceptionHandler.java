package com.ibm.wssvt.acme.common.exception.handler;

import java.util.List;

import com.ibm.wssvt.acme.common.envconfig.ExceptionHandlerConfiguration;
import com.ibm.wssvt.acme.common.exception.ExceptionAction;
import com.ibm.wssvt.acme.common.exception.ExceptionHandlerResult;
import com.ibm.wssvt.acme.common.exception.IExceptionHandler;
import com.ibm.wssvt.acme.common.exception.IExceptionHandlerResult;

public class StopExceptionHandler implements IExceptionHandler {

	public IExceptionHandlerResult handleException(
			ExceptionHandlerConfiguration exceptionHandlerConfiguration, Exception exception) {
		
		IExceptionHandlerResult result = new ExceptionHandlerResult();
				
		if (checkOverride(exceptionHandlerConfiguration.getOverrides(), exception)) {
			result.setAction(ExceptionAction.REPORT_AND_COUNT);
			result.setMessage("Exception:  " + exception.getClass().getName() + ", matching pattern: " + exceptionHandlerConfiguration.getExceptionClassName() 
					+ " was encountered with STOP Handler -- HOWEVER, it was OVERRIDEN. Full message is: " + exception.getMessage());			
		} else{
			result.setAction(ExceptionAction.STOP);
			result.setMessage("Exception:  " + exception.getClass().getName() + ", matching pattern: " + exceptionHandlerConfiguration.getExceptionClassName() 
					+ " was encountered with STOP Handler.  Full message is: " + exception.getMessage() );			
		}
		return result;
	}

	private static boolean checkOverride(List<String> msgs, Exception ex) {
		for (String msg : msgs) {
			if (ex.getMessage().contains(msg)){
				return true;
			}
		}
		return false;
	}

}
