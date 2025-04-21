package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;
import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.AnnuityHolderCategory;
import com.ibm.wssvt.acme.annuity.common.bean.ContactType;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IEquityAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IFixedAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityType;
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

public class CRUDHoldeAndAnnuityEU extends AbastractAnnuityExecutionUnit {

	private static final long serialVersionUID = -8978758038714550844L;

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

		logger.fine("Creating AnnuityHolder");
		IAnnuityHolder holder = null;
		try {
			holder = createAnnuityHolder(contact);
		} catch (Exception e) {
			logger.warning("Failed to create AnnuityHolder. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Create AnnuityHolder");
		try {
			verifyCreate(holder);
		} catch (Exception e) {
			logger.info("Create AnnuityHolder verification failed.  Error: "
					+ e);
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Creating Annuity");
		IAnnuity annuity = null;
		AnnuityType annuityType = getRandomEnum(AnnuityType.class);
		try {
			annuity = createAnnuity(holder, annuityType);
		} catch (Exception e) {
			logger.warning("Failed to create Annuity. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Create Annuity");
		try {
			verifyAnnuityValues(annuity, annuityType);
		} catch (Exception e) {
			logger.info("Create Annuity verification failed.  Error: " + e);
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Updating Contact");
		IContact updatedContact = null;
		try {
			updatedContact = updateContact(contact);
		} catch (Exception e) {
			logger.warning("Failed to update Contact. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Update Contact");
		try {
			verifyUpdate(updatedContact);
		} catch (Exception e) {
			logger.info("Update Contact verification failed. Error: " + e);
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Updating AnnuityHolder");
		IAnnuityHolder updatedHolder = null;
		try {
			updatedHolder = updateAnnuityHolder(holder, updatedContact);
		} catch (Exception e) {
			logger.warning("Failed to update AnnuityHolder. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Update AnnuityHolder");
		try {
			verifyUpdate(updatedHolder);
		} catch (Exception e) {
			logger
					.info("Update AnnuityHolder verification failed. Error: "
							+ e);
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Updating AnnuityHolder with annuity");
		try {
			updatedHolder = updateAnnuityHolderWithAnnuity(updatedHolder);
		} catch (Exception e) {
			logger.warning("Failed to update AnnuityHolder. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Update AnnuityHolder");
		try {
			verifyUpdate(updatedHolder);
		} catch (Exception e) {
			logger
					.info("Update AnnuityHolder verification failed. Error: "
							+ e);
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Deleting AnnuityHolder");
		try {
			deleteAnnuityHolder(updatedHolder);
		} catch (Exception e) {
			logger.info("Failed to delete AnnuityHolder. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Delete AnnuityHolder");
		try {
			verifyDelete(updatedHolder);
		} catch (Exception e) {
			logger.info("Delete AnnuityHolder verification failed.  Error: "
					+ e);
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Deleting Contact");
		try {
			deleteContact(updatedContact);
		} catch (Exception e) {
			logger.info("Failed to delete Contact. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Delete Contact");
		try {
			verifyDelete(updatedContact);
		} catch (Exception e) {
			logger.info("Delete Contact verification failed.  Error: " + e);
			getExecutionUnitEvent().addException(e);
		}
	}

	private void setScenarioVariables() {
		logger = getLogger(getClass().getName());
	}

	private IAnnuityHolder createAnnuityHolder(IContact contact)
			throws EntityAlreadyExistsException, InvalidArgumentException,
			ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
		IAnnuityHolder holder = BasicExecutionUnitLibrarry
				.getAnnuityHolder(getAnnuityBeansFactory());
		holder.setCategory(getRandomEnum(AnnuityHolderCategory.class));
		holder.setContact(contact);
		holder.setConfiguration(getConfiguration());
		holder = getServerAdapter().createAnnuityHolder(holder);
		return holder;
	}

	private IAnnuityHolder updateAnnuityHolder(IAnnuityHolder before,
			IContact updatedContact) throws EntityNotFoundException,
			ServerAdapterCommunicationException, InvalidArgumentException,
			ServerInternalErrorException, RemoteException {
		IAnnuityHolder after = BasicExecutionUnitLibrarry
				.getAnnuityHolder(getAnnuityBeansFactory());
		after.setId(before.getId());
		after.setConfiguration(getConfiguration());
		after = getServerAdapter().findHolderById(after);
		after.setGovernmentId(after.getGovernmentId() + "-updated");
		after.setContact(updatedContact);
		after.setConfiguration(getConfiguration());
		after = getServerAdapter().updateAnnuityHolder(after);
		return after;
	}

	private void deleteAnnuityHolder(IAnnuityHolder holder)
			throws EntityNotFoundException, EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, RemoteException {
		IAnnuityHolder removed = BasicExecutionUnitLibrarry
				.getAnnuityHolder(getAnnuityBeansFactory());
		removed.setId(holder.getId());
		removed.setConfiguration(getConfiguration());
		List<IAnnuity> annuities = getServerAdapter().findHolderAnnuities(
				removed);
		for (IAnnuity annuity : annuities) {
			// find the associated annuity
			IAnnuity updated = BasicExecutionUnitLibrarry
					.getBasicAnnuity(getAnnuityBeansFactory());
			updated.setId(annuity.getId());
			updated.setConfiguration(getConfiguration());
			updated = getServerAdapter().findAnnuityById(updated);
			// update the annuity to set the FK_AnnHolder to be null
			updated.setAnnuityHolderId(null);
			updated.setConfiguration(getConfiguration());
			updated = getServerAdapter().updateAnnuity(updated);
			// delete the annuity
			updated.setConfiguration(getConfiguration());
			getServerAdapter().deleteAnnuityById(updated);

		}
		getServerAdapter().deleteAnnuityHolder(removed);
	}

	private void verifyCreate(IAnnuityHolder holder)
			throws EntityNotFoundException, InvalidArgumentException,
			ServerAdapterCommunicationException, ServerInternalErrorException,
			ExecutionUnitVerificationException, RemoteException {
		// read the holder with id.
		IAnnuityHolder results = getAnnuityBeansFactory().createAnnuityHolder();
		results.setId(holder.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findHolderById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, holder, results,
				"AnnuityHolder from client is not equal to DB value",
				"AnnuityHolder Create mismacth was found.");
	}

	private void verifyUpdate(IAnnuityHolder holder)
			throws EntityNotFoundException, InvalidArgumentException,
			ServerAdapterCommunicationException, ServerInternalErrorException,
			ExecutionUnitVerificationException, RemoteException {
		// read the holder with id.
		IAnnuityHolder results = getAnnuityBeansFactory().createAnnuityHolder();
		results.setId(holder.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findHolderById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, holder, results,
				"AnnuityHolder from client is not equal to DB value",
				"AnnuityHolder Update mismacth was found.");
		// todo add verification to list of annuity
	}

	private void verifyDelete(IAnnuityHolder holder)
			throws EntityNotFoundException, InvalidArgumentException,
			ServerAdapterCommunicationException, ServerInternalErrorException,
			ExecutionUnitVerificationException, RemoteException, InvalidExecutionUnitParameterException, InterruptedException {
        AsynchMethodHelper.verifyHolderDelete(holder, this, logger);
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
			ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException, InvalidExecutionUnitParameterException, InterruptedException {
        AsynchMethodHelper.verifyContactDelete(contact, this, logger);
	}

	private IAnnuity createAnnuity(IAnnuityHolder holder,
			AnnuityType annuityType) throws EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, RemoteException {
		IAnnuity annuity = BasicExecutionUnitLibrarry.getAnnuity(
				getAnnuityBeansFactory(), annuityType);
		annuity.setAnnuityHolderId(holder.getId());
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().createAnnuity(annuity);
		return annuity;
	}

	private IAnnuityHolder updateAnnuityHolderWithAnnuity(IAnnuityHolder holder)
			throws EntityNotFoundException, EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, RemoteException {
		// add an annuity
		IAnnuity annuity2 = BasicExecutionUnitLibrarry.getAnnuity(
				getAnnuityBeansFactory(), getRandomEnum(AnnuityType.class));
		annuity2.setAnnuityHolderId(holder.getId());
		annuity2.setConfiguration(getConfiguration());
		annuity2 = getServerAdapter().createAnnuity(annuity2);

		// find the list of annuity from holder
		IAnnuityHolder updatedHolder = getAnnuityBeansFactory()
				.createAnnuityHolder();
		updatedHolder.setId(holder.getId());
		updatedHolder.setConfiguration(getConfiguration());
		List<IAnnuity> annuities = getServerAdapter().findHolderAnnuities(
				updatedHolder);

		// to remove an annuity, you need to find it, null the FK, then delete
		IAnnuity removed = BasicExecutionUnitLibrarry
				.getBasicAnnuity(getAnnuityBeansFactory());
		removed.setId(annuities.get(0).getId());
		removed.setConfiguration(getConfiguration());
		removed = getServerAdapter().findAnnuityById(removed);
		removed.setAnnuityHolderId(null);
		removed.setConfiguration(getConfiguration());
		removed = getServerAdapter().updateAnnuity(removed);
		removed.setConfiguration(getConfiguration());
		getServerAdapter().deleteAnnuityById(removed);

		// add the 3rd annuity
		IAnnuity annuity3 = BasicExecutionUnitLibrarry.getAnnuity(
				getAnnuityBeansFactory(), getRandomEnum(AnnuityType.class));
		annuity3.setAnnuityHolderId(holder.getId());
		annuity3.setConfiguration(getConfiguration());
		annuity3 = getServerAdapter().createAnnuity(annuity3);

		return holder;
	}

	private void verifyAnnuityValues(IAnnuity annuity, AnnuityType annuityType)
			throws EntityNotFoundException, InvalidArgumentException,
			ServerAdapterCommunicationException, ServerInternalErrorException,
			ExecutionUnitVerificationException, RemoteException {
		IAnnuity results = null;
		if (AnnuityType.BASIC.equals(annuityType))
			results = getAnnuityBeansFactory().createAnnuity();
		if (AnnuityType.EQUITY.equals(annuityType))
			results = getAnnuityBeansFactory().createAnnEquity();
		if (AnnuityType.FIXED.equals(annuityType))
			results = getAnnuityBeansFactory().createAnnFixed();
		results.setId(annuity.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findAnnuityById(results);
		if (annuity instanceof IFixedAnnuity) {
			ExecutionUnitVerificationHelper.assertEqual(this,
					(IFixedAnnuity) annuity, (IFixedAnnuity) results,
					"Fixed Annuity from Client is not equal to DB value",
					"Mismacth was found.");
		} else if (annuity instanceof IEquityAnnuity) {
			ExecutionUnitVerificationHelper.assertEqual(this,
					(IEquityAnnuity) annuity, (IEquityAnnuity) results,
					"Equity Annuity from Client is not equal to DB value",
					"Mismacth was found.");
		} else {
			ExecutionUnitVerificationHelper.assertEqual(this, annuity, results,
					"Basic Annuity from Client is not equal to DB value",
					"Mismacth was found.");
		}

		ExecutionUnitVerificationHelper.assertEqual(this, annuity.getPayouts(),
				results.getPayouts(),
				"Annuity payouts from Client is not equal to DB value",
				"Mismacth was found in number of payouts");
		boolean found = false;
		if (annuity.getPayouts() != null) {
			IPayout clientPayout = null;
			for (int i = 0; i < annuity.getPayouts().size(); i++) {
				found = false; // reset
				clientPayout = annuity.getPayouts().get(i);
				for (IPayout resultPayout : results.getPayouts()) {
					if (clientPayout.getId().equals(resultPayout.getId())) {
						found = true;
						ExecutionUnitVerificationHelper.assertEqual(this,
								clientPayout, resultPayout,
								"Annuity Payout from Client is not equal to DB value at location: "
										+ i, "Mismacth was found");
					} else {
						continue;
					}
				}
				if (!(found) && clientPayout != null) {
					throw new ExecutionUnitVerificationException(
							"Annuity Payout from client is not equal to DB.  "
									+ "Found Payout with id: "
									+ clientPayout.getId()
									+ " on the client side, but not in the database for annuity id:"
									+ annuity.getId());

				}
			}
		}

		ExecutionUnitVerificationHelper.assertEqual(this, annuity.getRiders(),
				results.getRiders(),
				"Annuity rider from Client is not equal to DB value",
				"Mismacth was found in number of rider");
		if (annuity.getRiders() != null) {
			IRider clientRider = null;
			for (int i = 0; i < annuity.getRiders().size(); i++) {
				found = false; // reset
				clientRider = annuity.getRiders().get(i);
				for (IRider resultRider : results.getRiders()) {
					if (clientRider.getId().equals(resultRider.getId())) {
						found = true;
						ExecutionUnitVerificationHelper.assertEqual(this,
								clientRider, resultRider,
								"Annuity rider from Client is not equal to DB value at location: "
										+ i, "Mismacth was found");
					} else {
						continue;
					}
				}
				if (!(found) && clientRider != null) {
					throw new ExecutionUnitVerificationException(
							"Annuity rider from client is not equal to DB.  "
									+ "Found rider with id: "
									+ clientRider.getId()
									+ " on the client side, but not in the database for annuity id:"
									+ annuity.getId());

				}
			}
		}

		ExecutionUnitVerificationHelper.assertEqual(this, annuity.getPayors(),
				results.getPayors(),
				"Annuity Payor from Client is not equal to DB value",
				"Mismacth was found.");
		if (annuity.getPayors() != null) {
			IPayor clientPayor = null;
			for (int i = 0; i < annuity.getPayors().size(); i++) {
				found = false; // reset
				clientPayor = annuity.getPayors().get(i);
				for (IPayor resultPayor : results.getPayors()) {
					if (clientPayor.getId().equals(resultPayor.getId())) {
						found = true;
						ExecutionUnitVerificationHelper.assertEqual(this, annuity
								.getPayors().get(i), resultPayor,
								"Annuity payor from Client is not equal to DB value at location: "
										+ i, "Mismacth was found");
					} else {
						continue;
					}
				}
				if (!(found) && clientPayor != null) {
					throw new ExecutionUnitVerificationException(
							"Annuity payor from client is not equal to DB.  "
									+ "Found payor with id: "
									+ clientPayor.getId()
									+ " on the client side, but not in the database for annuity id:"
									+ annuity.getId());

				}
			}
		}
	}
}
