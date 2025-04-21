package com.ibm.wssvt.acme.annuity.common.util;

import java.util.logging.Logger;

import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
import com.ibm.wssvt.acme.common.log.AcmeLoggerFactory;

public class AnnuityLoggerFactory {
	
	public synchronized static AcmeLogger getAcmeServerLogger(Configrable<String, String> configrable, String name) {
		setServerNameInConfigrable(configrable, name);
		return AcmeLoggerFactory.getAcmeServerLogger(configrable, name);
		
	}
	
	public synchronized static Logger getServerLogger(Configrable<String, String> configrable, String name) {
		setServerNameInConfigrable(configrable, name);
		return AcmeLoggerFactory.getServerLogger(configrable, name);
	}
	
	public synchronized static AcmeLogger getAcmeClientLogger(Logger root, Configrable<String, String> configrable, String name) {
		return AcmeLoggerFactory.getAcmeClientLogger(root, configrable, name);
	}

	public synchronized static Logger getClientLogger(Logger root,
			Configrable<String, String> configrable, String name) {
		return AcmeLoggerFactory.getClientLogger(root, configrable, name);		
	}	
	
	private static void setServerNameInConfigrable(Configrable<String, String> configrable, String name) {
		if (configrable != null) {
			String nodeAndProcess = RunningServerInfo.getInstance().getNodeName() + "_" + RunningServerInfo.getInstance().getProcessName();
			configrable.getConfiguration().addParameter("internal.serverName", nodeAndProcess);
		}
	}
}
