package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class RandomThinkTimeEU extends AbastractAnnuityExecutionUnit {


	private static final long serialVersionUID = 3717944532474853110L;
	private static final String THINK_TIME="maxThinkTime";
	/* (non-Javadoc)
	 * @see com.ibm.wssvt.acme.annuity.client.scenario.Scenario#execute()
	 */
	public void execute() {
		AcmeLogger logger = getLogger(getClass().getName());
		// Is there any sleep time between runs?
		int thinkTime = 1;
		int maxThinkTime = 0;

		try {
			maxThinkTime = getParameterValueInt(THINK_TIME);
			thinkTime = (int)Math.round( Math.random() * maxThinkTime);
		    logger.info("putting the thread to sleep for: " + thinkTime + " Max allowed is: " + maxThinkTime);
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
