package com.ibm.wssvt.acme.annuity.common.client.executionunit.stackunit;

import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.AbstractAnnuityStackableExecutionUnit;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.BasicExecutionUnitLibrarry;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.ExecutionUnitVerificationHelper;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
/**
 * $Rev: 476 $
 * $Date: 2007-07-02 17:58:29 -0500 (Mon, 02 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class CreateContactEU extends AbstractAnnuityStackableExecutionUnit {

	private static final long serialVersionUID = 5751982103363852382L;
	boolean enableVerify = true;
	AcmeLogger logger;
	public void execute() {
		logger = getLogger(getClass().getName());		
		try {
			setEUParams();
			IContact contact = BasicExecutionUnitLibrarry.getContact(getAnnuityBeansFactory());
			contact.setConfiguration(getConfiguration());
			logger.fine("ready to create contact: " + contact);
			IContact result = getServerAdapter().createContact(contact);
			logger.fine("created contact: " + result);
			if (enableVerify){
				ExecutionUnitVerificationHelper.assertEqual(this, contact, result, 
						"Contact Value from client did not match DB. ", "Mismatch was Found");
			}
			getStackMap().put("contactId", result.getId());			
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
