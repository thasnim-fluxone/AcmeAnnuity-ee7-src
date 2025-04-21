package com.ibm.wssvt.acme.annuity.common.client.executionunit.policy;

import java.rmi.RemoteException;

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

public class CRUDBeneficiaryEU extends AbastractPolicyExecutionUnit {

	private static final long serialVersionUID = 1516157826365359836L;
	private AcmeLogger logger;

	public void execute() {
		setScenarioVariables();

		logger.fine("Creating Beneficiary");
		IBeneficiary beneficiary = null;
		try {
			beneficiary = createBeneficiary();
		} catch (Exception e) {
			logger.warning("Failed to create Beneficiary. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Create Beneficiary");
		try {
			verifyCreate(beneficiary);
		} catch (Exception e) {
			logger.info("Create Beneficiary verification failed.  Error: " + e);
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Updating Beneficiary");
		IBeneficiary updated = null;
		try {
			updated = updateBeneficiary(beneficiary);
		} catch (Exception e) {
			logger.warning("Failed to update Beneficiary. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Update Beneficiary");
		try {
			verifyUpdate(updated);
		} catch (Exception e) {
			logger.info("Update Beneficiary verification failed. Error: " + e);
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Deleting Beneficiary");
		try {
			deleteBeneficiary(updated);
		} catch (Exception e) {
			logger.info("Failed to delete Beneficiary. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Delete Beneficiary");
		try {
			verifyDelete(updated);
		} catch (Exception e) {
			logger.info("Delete Beneficiary verification failed.  Error: " + e);
			getExecutionUnitEvent().addException(e);
		}
	}

	private IBeneficiary createBeneficiary()
	throws EntityAlreadyExistsException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
		IBeneficiary beneficiary = BasicExecutionUnitLibrarry
		.getBeneficiary(getAnnuityBeansFactory());
		beneficiary.setConfiguration(getConfiguration());
		beneficiary = getServerAdapter().createBeneficiary(beneficiary);
		return beneficiary;
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

	private void deleteBeneficiary(IBeneficiary beneficiary) throws InvalidArgumentException, EntityNotFoundException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException {
		IBeneficiary removed = BasicExecutionUnitLibrarry.getBeneficiary(getAnnuityBeansFactory());
		removed.setId(beneficiary.getId());
		removed.setConfiguration(getConfiguration());
		getServerAdapter().deleteBeneficiary(removed);
	}

	private void verifyCreate(IBeneficiary beneficiary)
	throws EntityNotFoundException, InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException, ExecutionUnitVerificationException {
		IBeneficiary results = ((JPABeansFactory) getAnnuityBeansFactory()).createBeneficiary();
		results.setId(beneficiary.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findBeneficiaryById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, beneficiary, results,
				"beneficiary from client is not equal to DB value",
				"beneficiary Create mismacth was found.");
	}

	private void verifyUpdate(IBeneficiary beneficiary) throws ExecutionUnitVerificationException, EntityNotFoundException, InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException {
		IBeneficiary results = ((JPABeansFactory) getAnnuityBeansFactory()).createBeneficiary();
		results.setId(beneficiary.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findBeneficiaryById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, beneficiary, results,
				"beneficiary from client is not equal to DB value",
				"beneficiary Update mismacth was found.");
	}

	private void verifyDelete(IBeneficiary beneficiary) throws InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException, ExecutionUnitVerificationException {
		IBeneficiary results = ((JPABeansFactory) getAnnuityBeansFactory()).createBeneficiary();
		results.setId(beneficiary.getId());
		results.setConfiguration(getConfiguration());
		try {
			results = getServerAdapter().findBeneficiaryById(results);
			if (results != null) {
				throw new ExecutionUnitVerificationException("Deletion of beneficiary with ID = " + beneficiary.getId() 
						+ "was not successful! The object still exists in DB." );
			}
		} catch (EntityNotFoundException e) {
			// this is what we're expecting
		}
	}

	private void setScenarioVariables() {
		logger = getLogger(getClass().getName());
	}

}
