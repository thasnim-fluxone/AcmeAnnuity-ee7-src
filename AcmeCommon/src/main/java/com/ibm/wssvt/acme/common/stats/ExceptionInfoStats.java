package com.ibm.wssvt.acme.common.stats;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExceptionInfoStats implements Serializable {
	private static final long serialVersionUID = -9181009739048456265L;
	
	private String name;
	private long count;		
	private Map<Long, String> exceptionData =Collections.synchronizedMap(new HashMap<Long, String>()); 
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}		
	public Map<Long, String> getExceptionData() {
		return exceptionData;
	}
	
	public void addExceptionData(long count, int threadId, Date date, String euDesc, String exMessage, int maxMsgSize, int exceptionMaxCount){
		if (maxMsgSize <0) maxMsgSize =0;
		if (exceptionMaxCount != 0) {
			if (exceptionMaxCount > 0 && count - exceptionMaxCount > 0) {
				synchronized(exceptionData) {
					exceptionData.remove(count - exceptionMaxCount);
				}
			}
			exceptionData.put(count, buildExceptionData(threadId, date, euDesc, exMessage, maxMsgSize));
		}
	}
		
	private String buildExceptionData(int threadId, Date date, String euDesc, String exMessage, int maxMsgSize){
		StringBuffer sb = new StringBuffer("Thread id: ")
			.append(threadId);		
		
		String dateInfo = (date == null)? ".  Date: unknown" : ".  Date: " + date.toString();
		sb.append(dateInfo);
		
		sb.append(".  Execution Unit Desc: ").append(euDesc);
		
		String msg = ".  Message: exception has no message ";		
		if (exMessage != null && exMessage.length()> maxMsgSize){
			msg = ".  Message: (truncated to: " +maxMsgSize +" chars): " + exMessage.substring(0, maxMsgSize);
		}else if (exMessage != null){
			msg = ".  Message: " + exMessage;
		}else{
			msg = "(framework note: the exception message provided was null, framework replaced it by this text)";
		}
		sb.append(msg);
		
		return sb.toString();
		
	}
}
