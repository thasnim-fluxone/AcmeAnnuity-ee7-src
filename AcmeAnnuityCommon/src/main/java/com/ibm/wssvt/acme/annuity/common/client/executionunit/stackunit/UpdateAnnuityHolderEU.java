package com.ibm.wssvt.acme.annuity.common.client.executionunit.stackunit;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.AbstractAnnuityStackableExecutionUnit;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.BasicExecutionUnitLibrarry;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.ExecutionUnitVerificationHelper;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
/**
 * $Rev: 476 $
 * $Date: 2007-07-02 17:58:29 -0500 (Mon, 02 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class UpdateAnnuityHolderEU extends AbstractAnnuityStackableExecutionUnit {

	private static final long serialVersionUID = 5751982103363852382L;
	boolean enableVerify = true;
	AcmeLogger logger;
	public void execute() {
		logger = getLogger(getClass().getName());		
		try {
			setEUParams();
			IAnnuityHolder holder = BasicExecutionUnitLibrarry.getAnnuityHolder(getAnnuityBeansFactory());
			holder.setConfiguration(getConfiguration());
			String annuityHolderId = (String) getStackMap().get("annuityHolderId");
			if (annuityHolderId== null || annuityHolderId.trim().length()==0){
				throw new InvalidExecutionUnitParameterException("This EU expects to find the attribute contactId in the stack but found null or empty.  found: " + annuityHolderId
						+ ".  Make sure that you use this EU in a correct order.");
			}
			holder.setId(annuityHolderId);
			logger.fine("ready to find holder: " + holder);
			IAnnuityHolder readResult = getServerAdapter().findHolderById(holder);
			logger.fine("found holder : " + readResult);
			if (enableVerify){
				if (! (annuityHolderId.equals(readResult.getId()))){
					throw new ExecutionUnitVerificationException ("the client contact id: " + annuityHolderId + " did not match the server contactId: " + readResult.getId());					
				}				
			}	
			// now update it
			long time = System.currentTimeMillis();
			readResult.setFirstName("Updated First Name - " + time);
			readResult.setGovernmentId("Updated Govvernment Id - " + time);
			readResult.setLastName("Updated Last Name - " + time);						
			readResult.setConfiguration(getConfiguration());
			logger.fine("ready to update holder: " + readResult);
			IAnnuityHolder updateResult = getServerAdapter().updateAnnuityHolder(readResult);
			logger.fine("updated holder: " + updateResult);
			if (enableVerify){
				ExecutionUnitVerificationHelper.assertEqual(this, readResult, updateResult, 
						"AnnuityHolder Client Value did not match Server Contact Value.", "Mismatch was found");
			}
		} catch (Exception e) {
			getExecutionUnitEvent().addException(e);			
		}		
	}
	private void setEUParams() {
		try {
			enableVerify = getParameterValueBoolean("enableVerify");
		} catch (InvalidExecutionUnitParameterException e) {
			enableVerify = true;
		}
		
	}

}
