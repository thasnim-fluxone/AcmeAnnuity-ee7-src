package com.ibm.wssvt.acme.common.exception;
/**
 * $Rev: 745 $
 * $Date: 2007-09-07 09:51:25 -0500 (Fri, 07 Sep 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class ExceptionFormatter {
	
	public static String deepFormatToString(Throwable e){
		if (e == null) {
			return ExceptionFormatter.class.getName() 
				+ " deepFormatToString detected that the exception passed is null." 
				+ " Hence returning this message. There is no exception to format!" 
				+ " Please check the application logic.";
		}
		StringBuffer msg = readStackTrace(e);
		if(e.getCause() != null) {
			msg.append("\nCause:");
			msg.append(deepFormatToString(e.getCause()));
		}
		
		return msg.toString();
	}
	private static StringBuffer readStackTrace(Throwable e) {
		StringBuffer sb = new StringBuffer();
		sb.append("\nThrowable class: " + e.getClass().getName());
		sb.append("\nError Message: " + e.getMessage());		
		for (StackTraceElement element: e.getStackTrace()) {			
			//sb.append("\n.  StackTrace::Line Number: " + element.getLineNumber());
			//sb.append("\n.  StackTrace::Method: " + element.getMethodName());
			sb.append("\n.  StackTrace::Element: "+  element.toString());			
		}
		return sb;
	}
		
}
