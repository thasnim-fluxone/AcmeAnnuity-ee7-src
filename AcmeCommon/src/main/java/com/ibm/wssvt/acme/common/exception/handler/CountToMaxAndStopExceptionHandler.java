package com.ibm.wssvt.acme.common.exception.handler;

import com.ibm.wssvt.acme.common.envconfig.ExceptionHandlerConfiguration;
import com.ibm.wssvt.acme.common.exception.ExceptionAction;
import com.ibm.wssvt.acme.common.exception.ExceptionHandlerResult;
import com.ibm.wssvt.acme.common.exception.IExceptionHandler;
import com.ibm.wssvt.acme.common.exception.IExceptionHandlerResult;
import com.ibm.wssvt.acme.common.stats.ExceptionInfoStats;
import com.ibm.wssvt.acme.common.stats.ExecutionStats;

public class CountToMaxAndStopExceptionHandler implements IExceptionHandler {

	public IExceptionHandlerResult handleException(
			ExceptionHandlerConfiguration exceptionHandlerConfiguration, Exception exception) {
		
		IExceptionHandlerResult result = new ExceptionHandlerResult();
		ExceptionInfoStats exStats = ExecutionStats.getInstance().getExceptionInfoStats(exception);
		long count =0;
		if (exStats != null){
			count = exStats.getCount();
		}		
		count++; // for this instance of the exception
		long maxCount = exceptionHandlerConfiguration.getMaxCount();
		if (count >= maxCount){
			result.setAction(ExceptionAction.STOP);
			result.setMessage("Exception:  " + exception.getClass().getName() + ", matching pattern: " 
					+ exceptionHandlerConfiguration.getExceptionClassName() 
					+ " was counted and it reached maxCount of " + maxCount 
					+ ".  Complete error message: " + exception.getMessage());		
		}else {
			result.setAction(ExceptionAction.REPORT_AND_COUNT);
			result.setMessage("Exception:  " + exception.getClass().getName() + ", matching pattern: "
				+ exceptionHandlerConfiguration.getExceptionClassName() 
				+ " was ignored, but it was counted.  Current count: " + count + " maxCount is: " + maxCount);
		}
		return result;
	}

}
