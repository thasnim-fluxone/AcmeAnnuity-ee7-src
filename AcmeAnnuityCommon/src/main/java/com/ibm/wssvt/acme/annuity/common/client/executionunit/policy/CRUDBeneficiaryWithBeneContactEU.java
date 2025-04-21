package com.ibm.wssvt.acme.annuity.common.client.executionunit.policy;

import java.rmi.RemoteException;

import com.ibm.wssvt.acme.annuity.common.bean.IBeneContact;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneficiary;
import com.ibm.wssvt.acme.annuity.common.bean.jpa.JPABeansFactory;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.BasicExecutionUnitLibrarry;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.ExecutionUnitVerificationHelper;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class CRUDBeneficiaryWithBeneContactEU extends
		AbastractPolicyExecutionUnit {


	private static final long serialVersionUID = -5069230737845432381L;
	private AcmeLogger logger;
	
	public void execute() {
		setScenarioVariables();
		logger.fine("Creating beneficiary");
		IBeneficiary beneficiary = null;
		try {
			beneficiary = createBeneficiary();
		} catch (Exception e) {
			logger.warning("Failed to create Beneficiary. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}
		
		logger.fine("Verifying create beneficiary");
		try {
			verifyCreate(beneficiary);
		} catch (Exception e) {
			logger.info("Create Beneficiary verification failed.  Error: " + e);
			getExecutionUnitEvent().addException(e);
		}
		
		logger.fine("Creating beneficiary contact");
		IBeneContact beneContact = null;
		try {
			beneContact = createBeneContact(beneficiary.getId(),beneficiary);
		} catch (Exception e) {
			logger.warning("Failed to create Beneficiary contact. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}
		
		logger.fine("Verifying create beneficiary contact");
		try {
			verifyCreate(beneContact);
		} catch (Exception e) {
			logger.info("Create Beneficiary contact verification failed.  Error: " + e);
			getExecutionUnitEvent().addException(e);
		}
		
		logger.fine("Updating beneficiary contact");
		IBeneContact updatedBeneContact = null;
		try {
			updatedBeneContact = updateBeneContact(beneContact);
		} catch (Exception e) {
			logger.warning("Failed to update Beneficiary contact. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}
		
		logger.fine("Verifying update beneficiary contact");
		try {
			verifyUpdate(updatedBeneContact);
		} catch (Exception e) {
			logger.info("Update Beneficiary contact verification failed. Error: " + e);
			getExecutionUnitEvent().addException(e);
		}
		
		logger.fine("Reading beneficiary via beneficiary contact");
		try {
			readBeneficiaryFromBeneContact(updatedBeneContact);
		} catch (Exception e) {
			logger.info("Could not read beneficiary from beneficiary contact. Error: " + e);
			getExecutionUnitEvent().addException(e);
		}
		
		logger.fine("Updating beneficiary");
		IBeneficiary updatedBeneficiary = null;
		try {
			updatedBeneficiary = updateBeneficiary(beneficiary);
		} catch (Exception e) {
			logger.warning("Failed to update Beneficiary. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}
		
		logger.fine("Verifying update beneficiary");
		try {
			verifyUpdate(updatedBeneficiary);
		} catch (Exception e) {
			logger.info("Update Beneficiary verification failed. Error: " + e);
			getExecutionUnitEvent().addException(e);
		}
		
		logger.fine("Deleting beneficiary");
		try {
			deleteBeneficiary(updatedBeneficiary);
		} catch (Exception e) {
			logger.info("Failed to delete Beneficiary. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}
		
		logger.fine("Verifying delete beneficiary");
		try {
			verifyDelete(updatedBeneficiary);
		} catch (Exception e) {
			logger.info("Delete Beneficiary verification failed.  Error: " + e);
			getExecutionUnitEvent().addException(e);
		}
		
		logger.fine("Verifying delete beneficiary contact via automatic orphan removal");
		try {
			verifyDelete(updatedBeneContact);
		} catch (Exception e) {
			logger.info("Delete Beneficiary contact verification failed.  Error: " + e);
			getExecutionUnitEvent().addException(e);
		}

	}
	
	private void readBeneficiaryFromBeneContact(IBeneContact beneContact)
	throws EntityNotFoundException, InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException, ExecutionUnitVerificationException {
		IBeneficiary beneficiary = beneContact.getBeneficiary();
		IBeneficiary results = ((JPABeansFactory) getAnnuityBeansFactory()).createBeneficiary();
		results.setId(beneficiary.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findBeneficiaryById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, beneficiary, results,
				"beneficiary from client is not equal to DB value",
				"beneficiary read mismatch was found.");
	}

	private IBeneficiary createBeneficiary()
	throws EntityAlreadyExistsException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
		IBeneficiary beneficiary = BasicExecutionUnitLibrarry
		.getBeneficiary(getAnnuityBeansFactory());
		beneficiary.setConfiguration(getConfiguration());
		
		beneficiary = getServerAdapter().createBeneficiary(beneficiary);
		return beneficiary;
	}
	
	private IBeneContact createBeneContact(String beneficiaryPK, IBeneficiary contact_beneficiary)
	throws EntityAlreadyExistsException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
		IBeneContact beneContact = BasicExecutionUnitLibrarry
		.getBeneContact(getAnnuityBeansFactory(), beneficiaryPK);
		beneContact.setConfiguration(getConfiguration());
		//Rumana Added 9/29/2014 for defect 147548
		beneContact.setBeneficiary(contact_beneficiary);
		beneContact = getServerAdapter().createBeneContact(beneContact);
		return beneContact;
	}

	private IBeneficiary updateBeneficiary(IBeneficiary before) throws EntityNotFoundException, InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException {
		IBeneficiary after = BasicExecutionUnitLibrarry.getBeneficiary(getAnnuityBeansFactory());
		after.setId(before.getId());
		after.setConfiguration(getConfiguration());
		after = getServerAdapter().findBeneficiaryById(after);
		after.setFirstName(after.getFirstName() + "-updated");
		after.setLastName(after.getLastName() + "-updated");
		after.setRelationship(after.getRelationship() + "-updated");
		after.setConfiguration(getConfiguration());
		after = getServerAdapter().updateBeneficiary(after);
		return after;
	}
	
	private IBeneContact updateBeneContact(IBeneContact before) throws EntityNotFoundException, InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException {
		IBeneContact after = BasicExecutionUnitLibrarry.getBeneContact(getAnnuityBeansFactory(), before.getId().getBeneficiaryPK());
		after.setId(before.getId());
		after.setConfiguration(getConfiguration());
		after = getServerAdapter().findBeneContactById(after);
		after.setEmail(after.getEmail() + "-updated");
		after.setConfiguration(getConfiguration());
		after = getServerAdapter().updateBeneContact(after);
		return after;
	}

	private void deleteBeneficiary(IBeneficiary beneficiary) throws InvalidArgumentException, EntityNotFoundException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException {
		IBeneficiary removed = BasicExecutionUnitLibrarry.getBeneficiary(getAnnuityBeansFactory());
		removed.setId(beneficiary.getId());
		removed.setConfiguration(getConfiguration());
		getServerAdapter().deleteBeneficiary(removed);
	}
	
	/*
	 * Not using this method due to automatic orphan removal
	private void deleteBeneContact(IBeneContact beneContact) throws InvalidArgumentException, EntityNotFoundException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException {
		IBeneContact removed = BasicExecutionUnitLibrarry.getBeneContact(getAnnuityBeansFactory(), beneContact.getId().getBeneficiaryPK());
		removed.setId(beneContact.getId());
		removed.setConfiguration(getConfiguration());
		getServerAdapter().deleteBeneContact(removed);
	}*/

	private void verifyCreate(IBeneficiary beneficiary)
	throws EntityNotFoundException, InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException, ExecutionUnitVerificationException {
		IBeneficiary results = ((JPABeansFactory) getAnnuityBeansFactory()).createBeneficiary();
		results.setId(beneficiary.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findBeneficiaryById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, beneficiary, results,
				"beneficiary from client is not equal to DB value",
				"beneficiary Create mismatch was found.");
	}
	
	private void verifyCreate(IBeneContact beneContact)
	throws EntityNotFoundException, InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException, ExecutionUnitVerificationException {
		IBeneContact results = ((JPABeansFactory) getAnnuityBeansFactory()).createBeneContact();
		results.setId(beneContact.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findBeneContactById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, beneContact, results,
				"beneficiary contact from client is not equal to DB value",
				"beneficiary contact Create mismatch was found.");
	}

	private void verifyUpdate(IBeneficiary beneficiary) throws ExecutionUnitVerificationException, EntityNotFoundException, InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException {
		IBeneficiary results = ((JPABeansFactory) getAnnuityBeansFactory()).createBeneficiary();
		results.setId(beneficiary.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findBeneficiaryById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, beneficiary, results,
				"beneficiary from client is not equal to DB value",
				"beneficiary Update mismatch was found.");
	}
	
	private void verifyUpdate(IBeneContact beneContact) throws ExecutionUnitVerificationException, EntityNotFoundException, InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException {
		IBeneContact results = ((JPABeansFactory) getAnnuityBeansFactory()).createBeneContact();
		results.setId(beneContact.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findBeneContactById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, beneContact, results,
				"beneficiary from client is not equal to DB value",
				"beneficiary Update mismatch was found.");
	}

	private void verifyDelete(IBeneficiary beneficiary) throws InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException, ExecutionUnitVerificationException {
		IBeneficiary results = ((JPABeansFactory) getAnnuityBeansFactory()).createBeneficiary();
		results.setId(beneficiary.getId());
		results.setConfiguration(getConfiguration());
		try {
			results = getServerAdapter().findBeneficiaryById(results);
			if (results != null) {
				throw new ExecutionUnitVerificationException("Deletion of beneficiary with ID = " + beneficiary.getId() 
						+ " was not successful! The object still exists in DB." );
			}
		} catch (EntityNotFoundException e) {
			// this is what we're expecting
		}
	}
	
	private void verifyDelete(IBeneContact beneContact) throws InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException, ExecutionUnitVerificationException {
		IBeneContact results = ((JPABeansFactory) getAnnuityBeansFactory()).createBeneContact();
		results.setId(beneContact.getId());
		results.setConfiguration(getConfiguration());
		try {
			results = getServerAdapter().findBeneContactById(results);
			if (results != null) {
				throw new ExecutionUnitVerificationException("Deletion of beneficiary contact with ID = " + beneContact.getId() 
						+ " was not successful! The object still exists in DB." );
			}
		} catch (EntityNotFoundException e) {
			// this is what we're expecting
		}
	}

	private void setScenarioVariables() {
		logger = getLogger(getClass().getName());
	}
}
