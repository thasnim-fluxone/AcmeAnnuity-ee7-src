package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;

import com.ibm.wssvt.acme.annuity.common.bean.ContactType;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/* 
 * $Rev$
 * $Date$
 * $Author$
 * $LastChangedBy$
 */

public class CRUDContactEU extends AbastractAnnuityExecutionUnit {

	private static final long serialVersionUID = -8980124038714550844L;

	private AcmeLogger logger;

	public void execute() {
		setScenarioVariables();

		logger.fine("Creating Contact");
		IContact contact = null;
		try {
			contact = createContact();
		} catch (Exception e) {
			logger.warning("Failed to create Contact. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Create Contact");
		try {
			verifyCreate(contact);
		} catch (Exception e) {
			logger.info("Create Contact verification failed.  Error: " + e);
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Updating Contact");
		IContact updated = null;
		try {
			updated = updateContact(contact);
		} catch (Exception e) {
			logger.warning("Failed to update Contact. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Update Contact");
		try {
			verifyUpdate(updated);
		} catch (Exception e) {
			logger.info("Update Contact verification failed. Error: " + e);
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Deleting Contact");
		try {
			deleteContact(updated);
		} catch (Exception e) {
			logger.info("Failed to delete Contact. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Delete Contact");
		try {
			verifyDelete(updated);
		} catch (Exception e) {
			logger.info("Delete Contact verification failed.  Error: " + e);
			getExecutionUnitEvent().addException(e);
		}
	}

	private void setScenarioVariables() {
		logger = getLogger(getClass().getName());
	}

	private IContact createContact() throws EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, RemoteException {
		IContact contact = BasicExecutionUnitLibrarry
				.getContact(getAnnuityBeansFactory());
		contact.setConfiguration(getConfiguration());
		contact.setContactType(getRandomEnum(ContactType.class));
		contact = getServerAdapter().createContact(contact);
		return contact;
	}

	private IContact updateContact(IContact before)
			throws EntityNotFoundException,
			ServerAdapterCommunicationException, InvalidArgumentException,
			ServerInternalErrorException, RemoteException {
		IContact after = BasicExecutionUnitLibrarry
				.getContact(getAnnuityBeansFactory());
		after.setId(before.getId());
		after.setConfiguration(getConfiguration());
		after = getServerAdapter().findContactById(after);
		after.setPhone(after.getPhone() + "-updated");
		after.setEmail(after.getEmail() + "-updated");
		after.setConfiguration(getConfiguration());
		after = getServerAdapter().updateContact(after);
		return after;
	}

	private void deleteContact(IContact contact)
			throws EntityAlreadyExistsException, InvalidArgumentException,
			ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
		IContact removed = BasicExecutionUnitLibrarry
				.getContact(getAnnuityBeansFactory());
		removed.setId(contact.getId());
		removed.setConfiguration(getConfiguration());
		getServerAdapter().deleteContact(removed);
	}

	private void verifyCreate(IContact contact) throws EntityNotFoundException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException {
		// read the contact with id.
		IContact results = getAnnuityBeansFactory().createContact();
		results.setId(contact.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findContactById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, contact, results,
				"Contact from client is not equal to DB value",
				"Contact Create mismacth was found.");
	}

	private void verifyUpdate(IContact contact) throws EntityNotFoundException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException {
		// read the contact with id.
		IContact results = getAnnuityBeansFactory().createContact();
		results.setId(contact.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findContactById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, contact, results,
				"Contact from client is not equal to DB value",
				"Contact Update mismacth was found.");
	}

	private void verifyDelete(IContact contact) throws EntityNotFoundException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException, InvalidExecutionUnitParameterException, InterruptedException  {
		/*
		// read the contact with id.
		IContact results = getAnnuityBeansFactory().createContact();
		results.setId(contact.getId());
		results.setConfiguration(getConfiguration());
		try {
			results = getServerAdapter().findContactById(results);
			if (results != null) {
				throw new ExecutionUnitVerificationException("Deletion of Contact with ID = " + contact.getId() 
						+ "was not successful! The object still exists in DB." );
			}
		} catch (EntityNotFoundException e) {
			
		}
		*/
		//AsynchMethodHelper.verifyContactDelete(contact, this, logger);
	}
}
