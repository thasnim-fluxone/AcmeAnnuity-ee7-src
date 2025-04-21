package com.ibm.wssvt.acme.annuity.common.client.executionunit.specialunit.quickreturn;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.AbastractAnnuityExecutionUnit;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.BasicExecutionUnitLibrarry;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.ExecutionUnitVerificationHelper;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityType;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class CRUDPayoutQuickReturnSEU extends AbastractAnnuityExecutionUnit {	

	private static final long serialVersionUID = 6056146343000986463L;

	public void execute() {
		try{
			boolean verifyIdOnly = true;
			try{
				verifyIdOnly = getParameterValueBoolean("verifyIdOnly");
			}catch (InvalidExecutionUnitParameterException ignoreMe){}
			IPayout original = BasicExecutionUnitLibrarry.getPayout(getAnnuityBeansFactory());
			IAnnuity annuity = BasicExecutionUnitLibrarry.getAnnuity(getAnnuityBeansFactory(), getRandomEnum(AnnuityType.class));
			original.setAnnuity(annuity);
			original.setConfiguration(getConfiguration());
			original.getConfiguration().addParameter("specialServiceName", "QuickReturnService");			
		
			IPayout result = getServerAdapter().createPayout(original);
			ExecutionUnitVerificationHelper.assertEqual(this, original, result,
					"Payout from Client is not equal to DB value", "Mismacth was found.");
			
			result = null;			
			result = getServerAdapter().findPayoutById(original);
			if (verifyIdOnly){
				if (!(original.getId().equals(result.getId()))){
					throw new ExecutionUnitVerificationException("id did not match");
				}
			}else{				
				ExecutionUnitVerificationHelper.assertEqual(this, original, result,
					"Payout from Client is not equal to DB value", "Mismacth was found.");
			}


			
			result = null;
			result = getServerAdapter().updatePayout(original);
			ExecutionUnitVerificationHelper.assertEqual(this, original, result,
					"Payout from Client is not equal to DB value", "Mismacth was found.");

			getServerAdapter().deletePayout(original);
			
		}catch (Exception e) {			
			getExecutionUnitEvent().addException(e);
		}		
	}
	

}
