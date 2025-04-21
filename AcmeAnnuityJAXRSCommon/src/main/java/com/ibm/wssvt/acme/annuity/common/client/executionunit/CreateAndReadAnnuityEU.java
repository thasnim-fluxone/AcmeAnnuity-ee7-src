package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.exception.AnnuityException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerBusinessLogicException;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.exception.AcmeException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class CreateAndReadAnnuityEU extends AbastractAnnuityExecutionUnit {

	private static final long serialVersionUID = 3868305156006203220L;
	private static String UPDATED_ACCOUNT_NUMBER = "updatedAccountNumber";
	
	
	public void execute() {		
		// first create
		try{	
			AcmeLogger logger = getLogger(getClass().getName());
			logger.fine("0100:Try CreateAnnuity->");
			IAnnuity annuity = createAnnuity();
			logger.fine("0200:Annuity Created? ID="+annuity.getId());
			verifyCreateSucess(annuity);
			// Save the old annuity for comparison to the new annuity
			String oldAccountNumber = annuity.getAccountNumber();
			String oldId = annuity.getId();
			
			logger.fine("0300:Try Annuity Update");
						
			updateAnnuity(annuity);
			
			logger.fine("0500:annuity="+annuity.toString());
			logger.fine("0600:Annuity Update");
			IAnnuity updatedAnnuity = findAnnuity(annuity.getId());
			verifyUpdateSuccess(oldAccountNumber, oldId, updatedAnnuity);			
			
		}catch (Exception e) {
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);			
		}
	}

	private IAnnuity findAnnuity(String id) throws ServerAdapterCommunicationException, AnnuityException, RemoteException {
		IAnnuity annuity = getAnnuityBeansFactory().createAnnuity();
		annuity.setId(id);
		annuity.setConfiguration(getConfiguration());
		return getServerAdapter().findAnnuityById(annuity);
		
	}

	private void verifyUpdateSuccess(String oldAccountNumber, String oldId, IAnnuity updatedAnnuity) throws ExecutionUnitVerificationException {
		if (updatedAnnuity== null) {
			throw new ExecutionUnitVerificationException("Update  Annuity Failed.  Returned object is null");
		}
		if (updatedAnnuity.getId() == null) {
			throw new ExecutionUnitVerificationException("Update Annuity Failed. Returned object id == null");
		}
		if (!oldId.equals(updatedAnnuity.getId())) {
			throw new ExecutionUnitVerificationException ("The updatde Annuity has different id that the created annuity!"
					+ " created id: " + oldId + " updated id: " + updatedAnnuity.getId());
		}
		if (oldAccountNumber.equals(updatedAnnuity.getAccountNumber())) {
			throw new ExecutionUnitVerificationException ("The updatde Annuity has same acct number as the created annuity!"
					+ " created id: " + oldId + " updated id: " + updatedAnnuity.getId()
					+ " created account number: " + oldAccountNumber 
					+ " updated account number: " + updatedAnnuity.getAccountNumber());
		}
		
	}

	private void verifyCreateSucess(IAnnuity annuity) throws AnnuityException, ExecutionUnitVerificationException {
		if (annuity == null) {
			throw new ExecutionUnitVerificationException("Create Annuity Failed.  Returned object is null");
		}
		if (annuity.getId() == null) {
			throw new ExecutionUnitVerificationException("Create Annuity Failed. Returned object id == null");
		}
		
	}

	private void updateAnnuity(IAnnuity annuity) 
		throws ServerAdapterCommunicationException, ServerBusinessLogicException, AnnuityException, InvalidExecutionUnitParameterException, RemoteException{
		if (getConfiguration().getParameterValue(UPDATED_ACCOUNT_NUMBER) != null){
			annuity.setAccountNumber(getConfiguration().getParameterValue(UPDATED_ACCOUNT_NUMBER));
		}else{
			throw new InvalidExecutionUnitParameterException ("Configuratioin for scenario:" + getClass().getName()
					+ " is incomplete.  Attribute: " + UPDATED_ACCOUNT_NUMBER + " is requiered");
		}
		annuity.setConfiguration(getConfiguration());
		getServerAdapter().updateAnnuity(annuity);
		
	}

	private IAnnuity createAnnuity() throws AcmeException, RemoteException {
		IAnnuity annuity = BasicExecutionUnitLibrarry.getBasicAnnuity(getAnnuityBeansFactory());
		annuity.setId("20000");
		annuity.setConfiguration(getConfiguration());
		// Added for EJB3.1 Asynchronous Methods.  Set a timeout value to wait on the Future<v>.get(timeout, unit) call.
		annuity.getConfiguration().addParameter("asynchTimeoutValue", "3");
		IAnnuity resAnnuity = getServerAdapter().createAnnuity(annuity);		
		return resAnnuity;
	}

	

}
