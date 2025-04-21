package com.ibm.wssvt.acme.annuity.common.client.executionunit.stackunit;

import com.ibm.wssvt.acme.annuity.common.bean.IContact;
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
public class UpdateContactEU extends AbstractAnnuityStackableExecutionUnit {

	private static final long serialVersionUID = 5751982103363852382L;
	boolean enableVerify = true;
	AcmeLogger logger;
	public void execute() {
		logger = getLogger(getClass().getName());		
		try {
			setEUParams();
			IContact contact = BasicExecutionUnitLibrarry.getContact(getAnnuityBeansFactory());
			contact.setConfiguration(getConfiguration());
			String contactId = (String) getStackMap().get("contactId");
			if (contactId== null || contactId.trim().length()==0){
				throw new InvalidExecutionUnitParameterException("This EU expects to find the attribute contactId in the stack but found null or empty.  found: " + contactId
						+ ".  Make sure that you use this EU in a correct order.");
			}
			contact.setId(contactId);
			logger.fine("ready to find contact: " + contact);
			IContact readResult = getServerAdapter().findContactById(contact);
			logger.fine("found Contact: " + readResult);
			if (enableVerify){
				if (! (contact.getId().equals(readResult.getId()))){
					throw new ExecutionUnitVerificationException ("the client contact id: " + contactId + " did not match the server contactId: " + readResult.getId());					
				}				
			}	
			// now update it
			long time =  System.currentTimeMillis();
			readResult.setEmail("updated Email - " + time);
			readResult.setPhone("512-222-3333 - " + time);
			readResult.getAddress().setCity("updated City - " + time);
			readResult.setConfiguration(getConfiguration());
			logger.fine("ready to update contact: " + readResult);
			IContact updateResult = getServerAdapter().updateContact(readResult);
			logger.fine("updated contact: " + updateResult);
			if (enableVerify){
				ExecutionUnitVerificationHelper.assertEqual(this, readResult, updateResult, 
						"Contact Client Value did not match Server Contact Value.", "Mismatch was found");
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
