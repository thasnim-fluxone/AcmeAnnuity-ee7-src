package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;

import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class CreateContactEU extends AbastractAnnuityExecutionUnit {
private static final long serialVersionUID = -3967925682867028969L;
	
	public void execute() {
		try{
			AcmeLogger logger = getLogger(getClass().getName());
			
			IContact original = BasicExecutionUnitLibrarry.getContact(getAnnuityBeansFactory());			
			original.setConfiguration(getConfiguration());
			logger.info("creating contact");
			IContact result = getServerAdapter().createContact(original);
			logger.info("contact email is: " + result.getEmail());
			verifyContactValues(original);
			
		}catch (Exception e) {
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
		}		
	}
	
	private void verifyContactValues(IContact contact) 
	throws EntityNotFoundException, InvalidArgumentException, 
		ServerAdapterCommunicationException, ServerInternalErrorException, 
		ExecutionUnitVerificationException , RemoteException{
		// read the contact with id.
		IContact results = getAnnuityBeansFactory().createContact();
		results.setId(contact.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findContactById(results);
		ExecutionUnitVerificationHelper.assertEqual(this,contact, results, 
			"contact from Client is not equal to DB value", "Mismacth was found.");		
	
}
	

}
