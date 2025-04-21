package com.ibm.wssvt.acme.annuity.common.client.executionunit.policy;

import java.rmi.RemoteException;
import java.util.Map;

import com.ibm.wssvt.acme.annuity.common.bean.IFund;
import com.ibm.wssvt.acme.annuity.common.bean.IPolicy;
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

/* 
 * $Rev$
 * $Date$
 * $Author$
 * $LastChangedBy$
 */

public class CRUDPolicyEU extends AbastractPolicyExecutionUnit {

	private static final long serialVersionUID = -8980124038714550844L;

	private AcmeLogger logger;

	public void execute() {
		setScenarioVariables();

		logger.fine("Creating Policy");
		IPolicy policy = null;
		try {
			policy = createPolicy();
		} catch (Exception e) {
			logger.warning("Failed to create Policy. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Create Policy");
		try {
			verifyCreate(policy);
		} catch (Exception e) {
			logger.info("Create Policy verification failed.  Error: " + e);
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Updating Policy");
		IPolicy updated = null;
		try {
			updated = updatePolicy(policy);
		} catch (Exception e) {
			logger.warning("Failed to update Policy. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Update Policy");
		try {
			verifyUpdate(updated);
		} catch (Exception e) {
			logger.info("Update policy verification failed. Error: " + e);
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Deleting Policy");
		try {
			deletePolicy(updated);
		} catch (Exception e) {
			logger.info("Failed to delete Policy. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Delete Policy");
		try {
			verifyDelete(updated);
		} catch (Exception e) {
			logger.info("Delete Policy verification failed.  Error: " + e);
			getExecutionUnitEvent().addException(e);
		}
		
	}

	private void setScenarioVariables() {
		logger = getLogger(getClass().getName());
	}

	private IPolicy createPolicy() throws EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, RemoteException {
		IPolicy policy = BasicExecutionUnitLibrarry
				.getPolicy(getAnnuityBeansFactory());
		policy.setConfiguration(getConfiguration());
		//contact.setContactType(getRandomEnum(ContactType.class));
		policy = getServerAdapter().createPolicy(policy);
		return policy;
	}

	private IPolicy updatePolicy(IPolicy before)
			throws EntityNotFoundException,
			ServerAdapterCommunicationException, InvalidArgumentException,
			ServerInternalErrorException, RemoteException {
		IPolicy after = BasicExecutionUnitLibrarry
				.getPolicy(getAnnuityBeansFactory());
		after.setId(before.getId());
		after.setConfiguration(getConfiguration());
		after = getServerAdapter().findPolicyById(after);
		Map <Integer, IFund> fundMap = after.getFunds();
		IFund fund = BasicExecutionUnitLibrarry.getFund(getAnnuityBeansFactory());
		fund.setFundName("Oracle");
		fund.setIndexRate(new Double(3.0));
		fundMap.put(new Integer(3), fund);
		after.setFunds(fundMap);
		after.setConfiguration(getConfiguration());
		after = getServerAdapter().updatePolicy(after);
		return after;
	}

	private void deletePolicy(IPolicy policy)
			throws EntityAlreadyExistsException, InvalidArgumentException,
			ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException, EntityNotFoundException {
		IPolicy removed = BasicExecutionUnitLibrarry
				.getPolicy(getAnnuityBeansFactory());
		removed.setId(policy.getId());
		removed.setConfiguration(getConfiguration());
		getServerAdapter().deletePolicy(removed);
	}

	private void verifyCreate(IPolicy policy) throws EntityNotFoundException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException {
		// read the policy with id.
		IPolicy results = ((JPABeansFactory) getAnnuityBeansFactory()).createPolicy();
		results.setId(policy.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findPolicyById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, policy, results,
				"policy from client is not equal to DB value",
				"policy Create mismacth was found.");
	}

	private void verifyUpdate(IPolicy policy) throws EntityNotFoundException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException {
		// read the policy with id.
		IPolicy results = ((JPABeansFactory) getAnnuityBeansFactory()).createPolicy();
		results.setId(policy.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findPolicyById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, policy, results,
				"policy from client is not equal to DB value",
				"policy Update mismacth was found.");
	}

	private void verifyDelete(IPolicy policy) throws EntityNotFoundException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException {
		// read the contact with id.
		IPolicy results = ((JPABeansFactory) getAnnuityBeansFactory()).createPolicy();
		results.setId(policy.getId());
		results.setConfiguration(getConfiguration());
		try {
			results = getServerAdapter().findPolicyById(results);
			if (results != null) {
				throw new ExecutionUnitVerificationException("Deletion of policy with ID = " + policy.getId() 
						+ "was not successful! The object still exists in DB." );
			}
		} catch (EntityNotFoundException e) {
			
		}
	}
}
