package com.ibm.wssvt.acme.common.executionunit;

import java.io.Serializable;
/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public interface IExecutionUnitEventListener extends Serializable {

	public void processExecutionUnitEvent(IExecutionUnitEvent executionUnitEvent);
}
