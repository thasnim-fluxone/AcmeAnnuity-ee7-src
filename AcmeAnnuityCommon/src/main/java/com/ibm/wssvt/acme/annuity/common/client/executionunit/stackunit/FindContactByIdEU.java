package com.ibm.wssvt.acme.annuity.common.client.executionunit.stackunit;

import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.AbstractAnnuityStackableExecutionUnit;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.BasicExecutionUnitLibrarry;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
/**
 * $Rev: 476 $
 * $Date: 2007-07-02 17:58:29 -0500 (Mon, 02 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class FindContactByIdEU extends AbstractAnnuityStackableExecutionUnit {

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
			IContact result = getServerAdapter().findContactById(contact);
			logger.fine("found Contact: " + result);
			if (enableVerify){
				if (! (contact.getId().equals(result.getId()))){
					throw new ExecutionUnitVerificationException ("the client contact id: " + contactId + " did not match the server contactId: " + result.getId());					
				}
				
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
