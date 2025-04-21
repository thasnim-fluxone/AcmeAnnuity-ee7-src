package com.ibm.wssvt.acme.annuity.common.client.executionunit.specialunit.quickreturn;


import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.AbastractAnnuityExecutionUnit;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.BasicExecutionUnitLibrarry;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.ExecutionUnitVerificationHelper;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class CRUDContactQuickReturnSEU extends AbastractAnnuityExecutionUnit {	
	private static final long serialVersionUID = -235038475291112993L;

	public void execute() {
		try{			
			AcmeLogger logger = getLogger(getClass().getName());
			boolean verifyIdOnly = true;
			try{
				verifyIdOnly = getParameterValueBoolean("verifyIdOnly");
			}catch (InvalidExecutionUnitParameterException ignoreMe){}						
			
			IContact original = BasicExecutionUnitLibrarry.getContact(getAnnuityBeansFactory());
			original.setConfiguration(getConfiguration());
			original.getConfiguration().addParameter("specialServiceName", "QuickReturnService");
			logger.fine("creating contact in CRUDContactQuickReturn");
			IContact result = getServerAdapter().createContact(original);	

			if (verifyIdOnly){
				if (!(original.getId().equals(result.getId()))){
					throw new ExecutionUnitVerificationException("id did not match");
				}
			}else{
				ExecutionUnitVerificationHelper.assertEqual(this, original, result,
					"contact from Client is not equal to DB value", "Mismacth was found.");
			}
						
			logger.fine("findContactById in CRUDContactQuickReturn");
			result = getServerAdapter().findContactById(original);
			
			if (verifyIdOnly){
				if (!(original.getId().equals(result.getId()))){
					throw new ExecutionUnitVerificationException("id did not match");
				}
			}else{
				ExecutionUnitVerificationHelper.assertEqual(this, original, result,
					"contact from Client is not equal to DB value", "Mismacth was found.");
			}

			logger.fine("updateContact in CRUDContactQuickReturn");
			result = getServerAdapter().updateContact(original);
			if (verifyIdOnly){
				if (!(original.getId().equals(result.getId()))){
					throw new ExecutionUnitVerificationException("id did not match");
				}
			}else{
				ExecutionUnitVerificationHelper.assertEqual(this, original, result,
					"contact from Client is not equal to DB value", "Mismacth was found.");
			}
			logger.fine("finished updating Contact ");
			
			logger.fine("deleteContact in CRUDContactQuickReturn");
			getServerAdapter().deleteContact(original);
			
			logger.fine("finished deleting contact");
			
		}catch (Exception e) {			
			getExecutionUnitEvent().addException(e);
			System.out.print("From CRUDContactQuickReturnSEU ...");
			e.printStackTrace();
		}		
	}
	

}
