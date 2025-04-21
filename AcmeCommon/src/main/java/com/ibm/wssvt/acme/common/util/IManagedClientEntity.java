package com.ibm.wssvt.acme.common.util;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public interface IManagedClientEntity {

	void setClientContext(ClientContext clientContext);
	ClientContext getClientContext();
}
