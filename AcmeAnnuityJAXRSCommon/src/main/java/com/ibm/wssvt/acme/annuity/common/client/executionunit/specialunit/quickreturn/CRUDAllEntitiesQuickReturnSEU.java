package com.ibm.wssvt.acme.annuity.common.client.executionunit.specialunit.quickreturn;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
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
 * 
 * @author malbedaiwi
 *
 */
public class CRUDAllEntitiesQuickReturnSEU extends AbastractAnnuityExecutionUnit {	

	private static final long serialVersionUID = 1092982436706516660L;

	public void execute() {
		try{
			AcmeLogger logger = getLogger(getClass().getName());
			boolean verifyIdOnly = true;
			try{
				verifyIdOnly = getParameterValueBoolean("verifyIdOnly");
			}catch (InvalidExecutionUnitParameterException ignoreMe){}		
			
			// Tests for AnnuityHolder and Contact Entities.
			IContact contact = BasicExecutionUnitLibrarry.getContact(getAnnuityBeansFactory());
			IAnnuityHolder originalHolder = BasicExecutionUnitLibrarry.getAnnuityHolder(getAnnuityBeansFactory());
			originalHolder.setContact(contact);
			originalHolder.setConfiguration(getConfiguration());
			originalHolder.getConfiguration().addParameter("specialServiceName", "QuickReturnService");			
		
			IAnnuityHolder resultHolder = getServerAdapter().createAnnuityHolder(originalHolder);
			ExecutionUnitVerificationHelper.assertEqual(this, originalHolder, resultHolder,
					"AnnuityHolder from Client is not equal to DB value", "Mismacth was found.");
							
			resultHolder = getServerAdapter().findHolderById(originalHolder);
			if (verifyIdOnly){
				if (!(originalHolder.getId().equals(resultHolder.getId()))){
					throw new ExecutionUnitVerificationException("id did not match");
				}
			}else{				
				ExecutionUnitVerificationHelper.assertEqual(this, originalHolder, resultHolder,
						"AnnuityHolder from Client is not equal to DB value", "Mismacth was found.");
			}
			
		
			resultHolder = getServerAdapter().updateAnnuityHolder(originalHolder);
			ExecutionUnitVerificationHelper.assertEqual(this, originalHolder, resultHolder,
					"AnnuityHolder from Client is not equal to DB value", "Mismacth was found.");

			getServerAdapter().deleteAnnuityHolder(originalHolder);
			
			
			// Tests for Annuity, Payor, Payout & Rider entities
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
