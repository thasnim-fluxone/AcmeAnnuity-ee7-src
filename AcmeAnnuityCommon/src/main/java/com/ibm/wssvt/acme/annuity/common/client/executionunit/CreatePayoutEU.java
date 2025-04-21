package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class CreatePayoutEU extends AbastractAnnuityExecutionUnit {
	
	private static final long serialVersionUID = 7642267245351999878L;

	public void execute() {				
		AcmeLogger logger = getLogger(getClass().getName());
		try{
			IAnnuity ann = BasicExecutionUnitLibrarry.getBasicAnnuity(getAnnuityBeansFactory());
			ann.setConfiguration(getConfiguration());
			getServerAdapter().createAnnuity(ann);
			
			IPayout p = BasicExecutionUnitLibrarry.getPayout(getAnnuityBeansFactory());
			p.setConfiguration(getConfiguration());
			p.setAnnuity(ann);
			logger.fine("End Date before send to server: " + p.getEndDate());
			getServerAdapter().createPayout(p);
			
			IPayout p2 = BasicExecutionUnitLibrarry.getPayout(getAnnuityBeansFactory());
			p2.setId(p.getId());
			p2.setConfiguration(getConfiguration());
			p2 = getServerAdapter().findPayoutById(p2);
			logger.fine("End Date before After it read from server: " + p2.getEndDate());
			ExecutionUnitVerificationHelper.assertEqual(this, p, p2, 
					"Payout from client did not match from server", "Mismatch was found");
		}catch (Exception e) {
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
		}		
	}
	
	

}
