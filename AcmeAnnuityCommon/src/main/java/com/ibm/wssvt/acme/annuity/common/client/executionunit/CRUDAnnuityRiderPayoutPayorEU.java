package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
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

public class CRUDAnnuityRiderPayoutPayorEU extends AbastractAnnuityExecutionUnit {

	private static final long serialVersionUID = -8980124038714550844L;

	private AcmeLogger logger;

	public void execute() {
		try {
			setScenarioVariables();
		} catch (Exception e) {
			logger.warning("Failed to get correct parameters. Error: " + e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Creating Annuity with Rider");

		IAnnuity annuity = null;
		AnnuityType annuityType = getRandomEnum(AnnuityType.class);
		try {
			annuity = createAnnuityWithRider(annuityType);
		} catch (Exception e) {
			logger.warning("Failed to create annuity with rider. Error: " + e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Annuity with Rider");
		try {
			verifyAnnuityValues(annuity, annuityType);
		} catch (Exception e) {
			logger
					.warning("Failed to verify create annuity with rider. Error: "
							+ e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Updating Annuity with Rider");
		try {
			annuity = updateAnnuityWithRider(annuity);
		} catch (Exception e) {
			logger.warning("Failed to update annuity with rider. Error: " + e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Annuity with Rider");
		try {
			verifyAnnuityValues(annuity, annuityType);
		} catch (Exception e) {
			logger
					.warning("Failed to verify update annuity with rider. Error: "
							+ e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Deleting Annuity with Rider");
		try {
			deleteAnnuityWithRider(annuity);
		} catch (Exception e) {
			logger.warning("Failed to delete annuity with rider. Error: " + e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Annuity with Rider");
		try {
			verifyDeleteAnnuity(annuity, annuityType);
		} catch (Exception e) {
			logger
					.warning("Failed to verify delete annuity with rider. Error: "
							+ e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Creating Annuity with Payout");
		// IAnnuity annuity = null;
		// AnnuityType annuityType = getRandomEnum(AnnuityType.class);
		try {
			annuity = createAnnuityWithPayout(annuityType);
		} catch (Exception e) {
			logger.warning("Failed to create annuity with Payout. Error: " + e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Annuity with Payout");
		try {
			verifyAnnuityValues(annuity, annuityType);
		} catch (Exception e) {
			logger
					.warning("Failed to verify create annuity with Payout. Error: "
							+ e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Updating Annuity with Payout");
		try {
			annuity = updateAnnuityWithPayout(annuity);
		} catch (Exception e) {
			logger.warning("Failed to update annuity with Payout. Error: " + e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Annuity with Payout");
		try {
			verifyAnnuityValues(annuity, annuityType);
		} catch (Exception e) {
			logger
					.warning("Failed to verify update annuity with Payout. Error: "
							+ e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Deleting Annuity with Payout");
		try {
			deleteAnnuityWithPayout(annuity);
		} catch (Exception e) {
			logger.warning("Failed to delete annuity with Payout. Error: " + e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Annuity with Payout");
		try {
			verifyDeleteAnnuity(annuity, annuityType);
		} catch (Exception e) {
			logger
					.warning("Failed to verify delete annuity with Payout. Error: "
							+ e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Creating Payor");
		IPayor payor = null;
		try {
			payor = createPayor();
		} catch (Exception e) {
			logger.warning("Failed to create Payor. Error: " + e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Create Payor");
		try {
			verifyCreatePayor(payor);
		} catch (Exception e) {
			logger.info("Create Payor verification failed.  Error: " + e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
		}

		// IAnnuity annuity = null;
		// AnnuityType annuityType = getRandomEnum(AnnuityType.class);

		logger.fine("Creating Annuity with Payor");
		try {
			annuity = createAnnuityWithPayor(annuityType, payor);
		} catch (Exception e) {
			logger.warning("Failed to create annuity with payor. Error: " + e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
			return;
		}
		logger.fine("Verifying Annuity with Payor");
		try {
			verifyAnnuityValues(annuity, annuityType);
		} catch (Exception e) {
			logger.info("Failed to verify create annuity with payor. Error: "
					+ e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Updating Annuity with Payor");
		try {
			annuity = updateAnnuityWithPayor(annuity);
		} catch (Exception e) {
			logger.warning("Failed to update annuity with Payor. Error: " + e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Annuity with Payor");
		try {
			verifyAnnuityValues(annuity, annuityType);
		} catch (Exception e) {
			logger
					.warning("Failed to verify update annuity with Payor. Error: "
							+ e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
		}

		logger.fine("Deleting Annuity with Payor");
		try {
			deleteAnnuityWithPayor(annuity);
		} catch (Exception e) {
			logger.warning("Failed to delete annuity with Payor. Error: " + e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Verifying Annuity with Payor");
		try {
			verifyDeleteAnnuity(annuity, annuityType);
		} catch (Exception e) {
			logger
					.warning("Failed to verify delete annuity with Payor. Error: "
							+ e);
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
		}
	}

	private void setScenarioVariables()
			throws InvalidExecutionUnitParameterException {
		logger = getLogger(getClass().getName());
	}

	private IPayor createPayor() throws EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, RemoteException {
		IPayor payor = BasicExecutionUnitLibrarry.getPayor(getAnnuityBeansFactory());
		payor.setConfiguration(getConfiguration());
		payor = getServerAdapter().createPayor(payor);
		return payor;
	}

	private void verifyCreatePayor(IPayor payor)
			throws EntityNotFoundException, InvalidArgumentException,
			ServerAdapterCommunicationException, ServerInternalErrorException,
			ExecutionUnitVerificationException, RemoteException {
		// read the contact with id.
		IPayor results = getAnnuityBeansFactory().createPayor();
		results.setId(payor.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findPayorById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, payor, results,
				"Payor from client is not equal to DB value",
				"Payor Create mismacth was found.");
	}

	private IAnnuity createAnnuityWithRider(AnnuityType annuityType)
			throws EntityAlreadyExistsException, InvalidArgumentException,
			ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
		IAnnuity annuity = BasicExecutionUnitLibrarry.getAnnuity(
				getAnnuityBeansFactory(), annuityType);
		IRider rider = BasicExecutionUnitLibrarry.getRider(getAnnuityBeansFactory());
		annuity.getRiders().add(rider);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().createAnnuity(annuity);
		return annuity;
	}

	private IAnnuity updateAnnuityWithRider(IAnnuity annuity)
			throws EntityNotFoundException, EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException , RemoteException {
		// add 2nd rider
		IRider rider2 = BasicExecutionUnitLibrarry
				.getRider(getAnnuityBeansFactory());
		annuity.getRiders().add(rider2);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);

		IRider rider1 = annuity.getRiders().get(0);
		// following code is needed to remove
		annuity.getRiders().remove(0);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		// remove 1st rider
		IRider removed = BasicExecutionUnitLibrarry
				.getRider(getAnnuityBeansFactory());
		removed.setId(rider1.getId());
		removed.setConfiguration(getConfiguration());
		getServerAdapter().deleteRider(removed);

		// add 3rd rider
		IRider rider3 = BasicExecutionUnitLibrarry
				.getRider(getAnnuityBeansFactory());
		annuity.getRiders().add(rider3);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		// update 2nd rider with a new value
		annuity.getRiders().get(0).setRule("updated rule");
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		return annuity;
	}

	private void deleteAnnuityWithRider(IAnnuity annuity)
			throws EntityNotFoundException, EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, RemoteException {
		List<String> riderIds = new ArrayList<String>();
		for (IRider rider : annuity.getRiders()) {
			riderIds.add(rider.getId());
		}

		annuity.getRiders().clear();
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		IRider removed = BasicExecutionUnitLibrarry
				.getRider(getAnnuityBeansFactory());

		for (String id : riderIds) {
			removed.setId(id);
			removed.setConfiguration(getConfiguration());
			getServerAdapter().deleteRider(removed);
		}
		annuity.setConfiguration(getConfiguration());
		getServerAdapter().deleteAnnuityById(annuity);
	}

	private IAnnuity createAnnuityWithPayout(AnnuityType annuityType)
			throws EntityAlreadyExistsException, InvalidArgumentException,
			ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
		IAnnuity annuity = BasicExecutionUnitLibrarry.getAnnuity(
				getAnnuityBeansFactory(), annuityType);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().createAnnuity(annuity);

		IPayout payout = BasicExecutionUnitLibrarry
				.getPayout(getAnnuityBeansFactory());
		payout.setAnnuity(annuity);
		payout.setConfiguration(getConfiguration());
		payout = getServerAdapter().createPayout(payout);
		annuity.getPayouts().add(payout);
		return annuity;
	}

	private IAnnuity updateAnnuityWithPayout(IAnnuity annuity)
			throws EntityNotFoundException, EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, RemoteException {
		// keep track of all the payouts
		IPayout payout1 = annuity.getPayouts().get(0);
		// add 2nd payout
		IPayout payout2 = BasicExecutionUnitLibrarry
				.getPayout(getAnnuityBeansFactory());
		payout2.setAnnuity(annuity);
		payout2.setConfiguration(getConfiguration());
		payout2 = getServerAdapter().createPayout(payout2);
		annuity.getPayouts().add(payout2);
		annuity.setConfiguration(getConfiguration());
		
		annuity = getServerAdapter().updateAnnuity(annuity);
		
		// remove 1st payout
		annuity.getPayouts().remove(0);
		payout1.setConfiguration(getConfiguration());
		getServerAdapter().deletePayout(payout1);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		// add 3rd payout
		IPayout payout3 = BasicExecutionUnitLibrarry
				.getPayout(getAnnuityBeansFactory());
		payout3.setAnnuity(annuity);
		payout3.setConfiguration(getConfiguration());
		payout3 = getServerAdapter().createPayout(payout3);
		annuity.getPayouts().add(payout3);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		// update 2nd payout with a new value
		Calendar newEndDate = new GregorianCalendar();
		newEndDate.add(Calendar.MONTH, 11);
		IPayout updatePayout = annuity.getPayouts().get(0);
		updatePayout.setEndDate(newEndDate);
		updatePayout.setAnnuity(annuity);
		updatePayout.setConfiguration(getConfiguration());
		updatePayout = getServerAdapter().updatePayout(updatePayout);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		return annuity;
	}

	private void deleteAnnuityWithPayout(IAnnuity annuity)
			throws EntityNotFoundException, EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, RemoteException {
		List<IPayout> payouts = annuity.getPayouts();
		for (IPayout payout : payouts) {
			payout.setConfiguration(getConfiguration());
			getServerAdapter().deletePayout(payout);
			// annuity.getPayouts().remove(payout);
		}
		annuity.setConfiguration(getConfiguration());
		getServerAdapter().deleteAnnuityById(annuity);
	}

	private IAnnuity createAnnuityWithPayor(AnnuityType annuityType,
			IPayor payor) throws EntityNotFoundException,
			EntityAlreadyExistsException, InvalidArgumentException,
			ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
		IAnnuity annuity = BasicExecutionUnitLibrarry.getAnnuity(
				getAnnuityBeansFactory(), annuityType);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().createAnnuity(annuity);

		annuity.getPayors().add(payor);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		return annuity;
	}

	private IAnnuity updateAnnuityWithPayor(IAnnuity annuity)
			throws EntityNotFoundException, EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, RemoteException {
		// add 2nd payor
		IPayor payor2 = BasicExecutionUnitLibrarry
				.getPayor(getAnnuityBeansFactory());
		payor2.setConfiguration(getConfiguration());
		payor2 = getServerAdapter().createPayor(payor2);
		annuity.getPayors().add(payor2);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);

		// remove 1st payor
		IPayor payor1 = annuity.getPayors().get(0);
		annuity.getPayors().remove(0);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		payor1.setConfiguration(getConfiguration());
		getServerAdapter().deletePayor(payor1);

		// add 3rd payor
		IPayor payor3 = BasicExecutionUnitLibrarry
				.getPayor(getAnnuityBeansFactory());
		payor3.setConfiguration(getConfiguration());
		payor3 = getServerAdapter().createPayor(payor3);
		annuity.getPayors().add(payor3);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		// update 2nd payor with a new value
		IPayor updatePayor = annuity.getPayors().get(0);
		updatePayor.setName("updated name");
		updatePayor.setConfiguration(getConfiguration());
		updatePayor = getServerAdapter().updatePayor(updatePayor);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		return annuity;
	}

	private void deleteAnnuityWithPayor(IAnnuity annuity)
			throws EntityNotFoundException, EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, RemoteException {
		List<String> payorIds = new ArrayList<String>();
		for (IPayor payor : annuity.getPayors()) {
			payorIds.add(payor.getId());
		}

		annuity.getPayors().clear();
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);

		IPayor removed = BasicExecutionUnitLibrarry
				.getPayor(getAnnuityBeansFactory());
		for (String id : payorIds) {
			removed.setId(id);
			removed.setConfiguration(getConfiguration());
			getServerAdapter().deletePayor(removed);
		}
		annuity.setConfiguration(getConfiguration());
		getServerAdapter().deleteAnnuityById(annuity);
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

	private void verifyDeleteAnnuity(IAnnuity annuity, AnnuityType annuityType)
			throws EntityNotFoundException, InvalidArgumentException,
			ServerAdapterCommunicationException, ServerInternalErrorException,
			ExecutionUnitVerificationException, RemoteException, InvalidExecutionUnitParameterException, InterruptedException  {
	/*	IAnnuity results = BasicExecutionUnitLibrarry.getAnnuity(
				getAnnuityBeansFactory(), annuityType);
		results.setId(annuity.getId());
		results.setConfiguration(getConfiguration());
		try {
			results = getServerAdapter().findAnnuityById(results);
			if (results != null) {
				throw new ExecutionUnitVerificationException(
						"Deletion of Annuity with ID = "
								+ annuity.getId()
								+ "was not successful! The object still exists in DB.");
			}
		} catch (EntityNotFoundException e) {

		}
*/
		AsynchMethodHelper.verifyAnnuityDelete(annuity,annuityType, this, logger);
		}
}
