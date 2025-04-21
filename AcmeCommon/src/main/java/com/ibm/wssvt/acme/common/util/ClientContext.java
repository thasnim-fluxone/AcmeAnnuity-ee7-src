package com.ibm.wssvt.acme.common.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class ClientContext implements Serializable {
	private static final long serialVersionUID = -2141654010322997536L;
	private String clientId;
	private int threadId;
	private String rootLoggerName;	
	private Logger rootLogger;
	private Map<String, Object> injectedObjects = new HashMap<String, Object>();
	private ClientType clientType;

	public Logger getRootLogger() {
		return rootLogger;
	}
	public void setRootLogger(Logger logger) {
		this.rootLogger = logger;
	}
			
	public ClientContext(String clientId, int threadId){
		this.clientId = clientId;
		this.threadId = threadId;
	}
	public int getThreadId() {
		return threadId;
	}

	public String getClientId() {
		return clientId;
	}

	public String getRootLoggerName() {		
		return this.rootLoggerName;
	}
	
	public String getPrefixedRootLoggerName() {
		return  getLoggerPrefix() + rootLoggerName;
	}

	public void setRootLoggerName(String parentLoggerName) {		
		this.rootLoggerName = parentLoggerName;
	}
	
	public String getLoggerPrefix(){
		StringBuffer sb = new StringBuffer("C_").append(clientId).append("_T_").append(threadId).append("_");
		return sb.toString();
	}
	
	public Map<String, Object> getInjectedObjects() {
		return injectedObjects;
	}
	public void setInjectedObjects(Map<String, Object> injectedObjects) {
		this.injectedObjects = injectedObjects;		
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}
	public ClientType getClientType() {
		return clientType;
	}
	public void setClientType(ClientType clientType) {
		this.clientType = clientType;
	}
	
	
	
}
