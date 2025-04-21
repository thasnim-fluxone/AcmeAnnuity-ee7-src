package com.ibm.wssvt.acme.annuity.common.client.executionunit.stackunit;

import java.util.Properties;

import com.ibm.wssvt.acme.annuity.common.client.executionunit.AbstractAnnuityStackableExecutionUnit;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
/**
 * $Rev: 476 $
 * $Date: 2007-07-02 17:58:29 -0500 (Mon, 02 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class TestEU extends AbstractAnnuityStackableExecutionUnit {

	private static final long serialVersionUID = 5751982103363852382L;
	
	public void execute() {
		try {
			AcmeLogger logger = getLogger(getClass().getName());
			logger.fine("fine");
			logger.info("info");
			logger.warning("war");
			logger.severe("severe");
			Properties p = System.getProperties();
			System.out.println(p);
			logger.info("Current stackMap is: " + getStackMap());
			logger.info("ABC value from stackMap: " + getStackMap().get("ABC"));
			logger.info("Add value to ABC in stackMap: " + getStackMap());
			getStackMap().put("ABC", "Hello World!");
			logger.info("ABC value from stackMap: " + getStackMap().get("ABC"));			
		} catch (Exception e) {
			getExecutionUnitEvent().addException(e);			
		}

	}

}
