package com.ibm.wssvt.acme.annuity.common.client.executionunit.stackunit;

import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.AbstractAnnuityStackableExecutionUnit;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.BasicExecutionUnitLibrarry;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
/**
 * $Rev: 476 $
 * $Date: 2007-07-02 17:58:29 -0500 (Mon, 02 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class DeleteContactEU extends AbstractAnnuityStackableExecutionUnit {

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
			logger.fine("ready to delete contact: " + contact);
			getServerAdapter().deleteContact(contact);			
			logger.fine("deleted Contact ");
			if (enableVerify){
				contact.setConfiguration(getConfiguration());
				try{
					getServerAdapter().findContactById(contact);
					throw new ExecutionUnitVerificationException("Deleted the contact and expected an EntityNotFoundException on a subsequent read, " +
							"but did not get the exception");
				}catch(EntityNotFoundException ignoreMe){
					
				}
								
			}	
			getStackMap().remove("contactId");
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
