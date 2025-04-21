/**
 * 
 */
package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;


/**
 * @author schader
 *
 */
public class ThinkTimeEU extends AbastractAnnuityExecutionUnit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3225215206026217874L;
	private static final String THINK_TIME="thinkTime";
	/* (non-Javadoc)
	 * @see com.ibm.wssvt.acme.annuity.client.scenario.Scenario#execute()
	 */
	public void execute() {
		AcmeLogger logger = getLogger(getClass().getName());
		// Is there any sleep time between runs?
		int thinkTime = 1;
		
		try {
			thinkTime = getParameterValueInt(THINK_TIME);
			logger.finest("ThinkTimeEU="+thinkTime);
			Thread.sleep(thinkTime);
		} catch ( InvalidExecutionUnitParameterException e){
			logger.severe("Invalid scenario parameter");
			getExecutionUnitEvent().addException(e);
		} catch (InterruptedException e) {			
			logger.severe("Thread Sleep has been interrupted"+ e.toString());
			getExecutionUnitEvent().addException(e);
		}

	}

}
