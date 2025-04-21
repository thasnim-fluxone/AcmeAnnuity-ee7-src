package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import com.ibm.wssvt.acme.common.log.AcmeLogger;
import java.util.logging.Level;
/**
 * $Rev: 522 $
 * $Date: 2007-07-13 16:29:59 -0500 (Fri, 13 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class ClientLoggingTestEU extends AbastractAnnuityExecutionUnit {

	private static final long serialVersionUID = 5751982103363852382L;
	
	public void execute() {
		try {
			AcmeLogger logger = getLogger(getClass().getName());			
			logger.finest("Scenario logging at FINEST");
			logger.finer("Scenario logging at FINER");
			logger.fine("Scenario logging at FINE");
			logger.info("Scenario logging at INFO");
			logger.config("Scenario logging at CONFIG");			
			logger.warning("Scenario logging at WARNING");
			logger.severe("Scenario logging at SEVERE");
			logger.log(Level.ALL, "Scenario logging at ALL");
		} catch (Exception e) {
			getExecutionUnitEvent().addException(e);			
		}

	}

}

