package com.ibm.wssvt.acme.annuity.common.client.executionunit.specialunit.quickreturn;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.AbastractAnnuityExecutionUnit;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.BasicExecutionUnitLibrarry;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.ExecutionUnitVerificationHelper;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityType;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class CRUDAnnuityQuickReturnSEU extends AbastractAnnuityExecutionUnit {	

	private static final long serialVersionUID = 6056146343000986463L;

	public void execute() {
		try{
			AcmeLogger logger = getLogger(getClass().getName());
			boolean verifyIdOnly = true;
			try{
				verifyIdOnly = getParameterValueBoolean("verifyIdOnly");
			}catch (InvalidExecutionUnitParameterException ignoreMe){}			
			
			IAnnuity original = BasicExecutionUnitLibrarry.getAnnuity(getAnnuityBeansFactory(), getRandomEnum(AnnuityType.class));
			
			IPayor payor = BasicExecutionUnitLibrarry.getPayor(getAnnuityBeansFactory());
			original.getPayors().add(payor);
			IPayor payor2 = BasicExecutionUnitLibrarry.getPayor(getAnnuityBeansFactory());
			original.getPayors().add(payor2);			
			IPayor payor3 = BasicExecutionUnitLibrarry.getPayor(getAnnuityBeansFactory());
			original.getPayors().add(payor3);			
			
			IPayout payout = BasicExecutionUnitLibrarry.getPayout(getAnnuityBeansFactory());
			original.getPayouts().add(payout);
			IPayout payout2 = BasicExecutionUnitLibrarry.getPayout(getAnnuityBeansFactory());
			original.getPayouts().add(payout2);
			IPayout payout3 = BasicExecutionUnitLibrarry.getPayout(getAnnuityBeansFactory());
			original.getPayouts().add(payout3);
			
			IRider rider = BasicExecutionUnitLibrarry.getRider(getAnnuityBeansFactory());
			original.getRiders().add(rider);			
			IRider rider2 = BasicExecutionUnitLibrarry.getRider(getAnnuityBeansFactory());
			original.getRiders().add(rider2);
			IRider rider3 = BasicExecutionUnitLibrarry.getRider(getAnnuityBeansFactory());
			original.getRiders().add(rider3);
			
			original.setConfiguration(getConfiguration());
			original.getConfiguration().addParameter("specialServiceName", "QuickReturnService");
			
			logger.fine("creating Annuity - quick return");
			IAnnuity result = getServerAdapter().createAnnuity(original);
			ExecutionUnitVerificationHelper.assertEqual(this, original, result,
					"Annuity from Client is not equal to DB value", "Mismacth was found.");			
			
			result = getServerAdapter().findAnnuityById(original);			
			if (verifyIdOnly){
				if (!(original.getId().equals(result.getId()))){
					throw new ExecutionUnitVerificationException("id did not match");
				}
			}else{				
				ExecutionUnitVerificationHelper.assertEqual(this, original, result,
						"Annuity from Client is not equal to DB value", "Mismacth was found.");
			}
			
			result = getServerAdapter().updateAnnuity(original);						
			ExecutionUnitVerificationHelper.assertEqual(this, original, result,
					"Annuity from Client is not equal to DB value", "Mismacth was found.");
			
			getServerAdapter().deleteAnnuityById(original);
			
		}catch (Exception e) {			
			getExecutionUnitEvent().addException(e);
		}		
	}	
}
