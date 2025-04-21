package com.ibm.wssvt.acme.common.client;

import com.ibm.wssvt.acme.common.util.IManagedClientEntity;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public interface IExecutionUnitRunner extends IManagedClientEntity, Runnable {
	public int getRunnerId();
	public void stopRunner(String msg);
	public void reportMessage(String msg);
	public String getReportedMessage();
	
}
