package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
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
import com.ibm.wssvt.acme.annuity.common.util.ServerExceptionMapper;
import com.ibm.wssvt.acme.annuity.common.util.ServerExceptionType;
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

public class UpdateExistingAnnuityPayorPayoutRiderWithoutAddPayorEU extends
		AbastractAnnuityExecutionUnit {

	private static final long serialVersionUID = -8978758038714550844L;

	private static final String START_HOLDER_ID = "startHolderId";

	private static final String END_HOLDER_ID = "endHolderId";

	private int startHolderId;

	private int endHolderId;

	private int holderRange; // 0 to end-start

	private AcmeLogger logger;
	
	private int overRideIdint = 0;
	
	private int maximumThreshold = 2;
	
	private int retryThinkTime = 5000;
	
	private boolean enableVerify = true;

	public void execute() {
		int threshold = 0;	
		try {
			setScenarioVariables();
		} catch (Exception e) {
			logger.severe("Invalid scenario parameters for scenario description:"
							+ getDescription() + "  Error is: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		String randomHolderId = "" + (startHolderId + getRandomInteger(0, holderRange));
		logger.fine("Got the random holder ID --- " + randomHolderId);
		logger.fine("Find the holder from existing database");
		IAnnuityHolder holder = null;
		try {
			holder = getAnnuityBeansFactory().createAnnuityHolder();
			holder.setId(randomHolderId);
			holder.setConfiguration(getConfiguration());
			holder = getServerAdapter().findHolderById(holder);
		} catch (Exception e) {
			logger.severe("Failed to find holder. ID - " + randomHolderId
					+ " Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		if (enableVerify) {
			logger.fine("Verifying annuity holder from db");
			try {
				verifyAnnuityHolderValue(holder);
			} catch (Exception e) {
				logger.severe("Find AnnuityHolder verification failed. Holder ID:"+holder.getId()+" Error: "+ e);
				getExecutionUnitEvent().addException(e);
				return;
			}
		}

		logger.fine("Getting list of Annuity from AnnuityHolder:"+holder.getId());
		List<IAnnuity> annuities = new ArrayList<IAnnuity>();
		try {
			annuities = getAnnuities(holder);
			String annIds = "";
			for (IAnnuity ann : annuities) {
				annIds += ann.getId() + " : ";
			}
			logger.fine("Got the the annuity IDs here --- " + annIds);
		} catch (Exception e) {
			logger.severe("Failed to get list of annuities. Holder ID:"+holder.getId()+"  Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		logger.fine("Annuity size is " + annuities.size());
		IAnnuity results=null;
		for (IAnnuity annuity : annuities) {
			// go through each Annuity and update the children

			AnnuityType annuityType = getAnnuityType(annuity);
			logger.fine("Updating Annuity with Rider:"+annuity.getId());
			threshold = 0;
			while(threshold <= maximumThreshold ){
				try {
					annuity.setConfiguration(getConfiguration());
					results = updateAnnuityWithRider(annuity);
					break;
				} catch (Exception e) {
					threshold = recoverableExceptionCheck( e, threshold, annuity);
					if( threshold == 0 ){   
						logger.severe("Failed to update annuity with rider. ID:"+annuity.getId()+" Error: " + e);
						getExecutionUnitEvent().addException(e);
						return;
					}
				if( (annuity = rereadAnnuity(annuity))== null)
					return;
				}
			}
			

			if (enableVerify) {
				logger.fine("Verifying Annuity with Rider:"+results.getId());
				try {
					verifyAnnuityValues(results, annuityType);
				} catch (Exception e) {
					logger.severe("Failed to verify update annuity with rider. ID:"+annuity.getId()+" Error: " + e);
					getExecutionUnitEvent().addException(e);
					return;
				}
			}

			logger.fine("Updating Annuity with Payout:"+results.getId());
			threshold = 0;
			IAnnuity resultsA=null;
			while(threshold <= maximumThreshold ){
				try {
					results.setConfiguration(getConfiguration());
					resultsA = updateAnnuityWithPayout(results);
					break;
				} catch (Exception e) {
					threshold = recoverableExceptionCheck( e, threshold, results);
					if( threshold == 0 ){   
						logger.severe("Failed to update annuity with Payout. ID:"+annuity.getId()+"  Error: " + e);
						getExecutionUnitEvent().addException(e);
						return;
					}
					if( (results = rereadAnnuity(results))== null)
						return;
				}
			}

			if (enableVerify) {
				logger.fine("Verifying Annuity with Payout:"+resultsA.getId());
				try {
					verifyAnnuityValues(resultsA, annuityType);
				} catch (Exception e) {
					logger.severe("Failed to verify update annuity with Payout. ID:"+annuity.getId()+"  Error: " + e);
					getExecutionUnitEvent().addException(e);
					return;
				}
			}

			logger.fine("Updating Annuity with Payor:"+annuity.getId());
			threshold = 0;
			IAnnuity resultsB=null;
			while(threshold <= maximumThreshold ){
				try {
					resultsA.setConfiguration(getConfiguration());
					resultsB = updateAnnuityWithPayor(resultsA);
					break;
				} catch (Exception e) {
					threshold = recoverableExceptionCheck( e, threshold, resultsA);
					if( threshold == 0 ){  
						logger.severe("Failed to update annuity with Payor. ID:"+annuity.getId()+"  Error: " + e);
						getExecutionUnitEvent().addException(e);
						return;
					}
					if( (resultsA = rereadAnnuity(resultsA))== null)
						return;
				}
			}

			if (enableVerify) {
				logger.fine("Verifying Annuity with Payor:"+annuity.getId());
				try {
					verifyAnnuityValues(resultsB, annuityType);
				} catch (Exception e) {
					logger.severe("Failed to verify update annuity with Payor. ID:"+annuity.getId()+"  Error: " + e);
					getExecutionUnitEvent().addException(e);
					return;
				}
			}

			logger.fine("Updating Annuity by itself:"+resultsB.getId());
			threshold = 0;
			IAnnuity resultsC=null;
			while(threshold <= maximumThreshold ){
				try {
					resultsB.setConfiguration(getConfiguration());
					resultsC = updateAnnuityAlone(resultsB);
					break;
				} catch (Exception e) {
					threshold = recoverableExceptionCheck( e, threshold, resultsB);
					if( threshold == 0) {
						logger.severe("Failed to update annuity by itself: ID:"+annuity.getId()+"  " + e);
						getExecutionUnitEvent().addException(e);
						return;
					}
					if( (resultsB = rereadAnnuity(resultsB))== null)
						return;
				}
			}

			if (enableVerify) {
				logger.fine("Verifying Annuity by itself:"+resultsC.getId());
				try {
					verifyAnnuityValues(resultsC, annuityType);
				} catch (Exception e) {
					logger.severe("Failed to verify update annuity by itself. ID:"+annuity.getId()+" Error: "+ e);
					getExecutionUnitEvent().addException(e);
					return;
				}
			}
		}
	}

	private int recoverableExceptionCheck( Exception e, int threshold, IAnnuity annuity){
		boolean tryAgain = false;
		ServerExceptionType exType = ServerExceptionMapper.getExceptionType(e);
		if (ServerExceptionType.OPTIMISTIC_LOCKING_EXCEPTION.equals(exType)) {
			logger.info("OPTIMISTIC LOCKING EXCEPTION encountered while updating annuity id: " + annuity.getId()
					+".  Trying again to a max of: " + maximumThreshold + " times. This is attempt: " + (threshold +1) );
			tryAgain = true;
		}
		else if(ServerExceptionType.DEADLOCK_EXCEPTION.equals(exType)){
			logger.info("DEADLOCK EXCEPTION encountered while updating annuity id: " + annuity.getId()
					+".  Trying again to a max of: " + maximumThreshold + " times. This is attempt: " + (threshold +1) );
			tryAgain = true;
			}
		
		if( tryAgain){
			threshold++;
			if( threshold == maximumThreshold) threshold = 0;
			try{	
				Thread.sleep(retryThinkTime);
			} catch (InterruptedException eSleep) {			
				logger.severe("Thread Sleep has been interrupted"+ eSleep.toString());
				getExecutionUnitEvent().addException(eSleep);
			}
		}
		return threshold;
	}
	
	private void setScenarioVariables() throws InvalidExecutionUnitParameterException {
		final String OVERRIDE_ID_KEY = "overRideId";
		final String MAX_THRESHOLD = "maximumThreshold";
		final String ENABLE_VERIFY_KEY = "enableVerify";
		final String RETRY_THINK_TIME="retryThinkTime";
		
		logger = getLogger(getClass().getName());
		// retrieve the parms from the config file
		startHolderId = getParameterValueInt(START_HOLDER_ID);
		endHolderId = getParameterValueInt(END_HOLDER_ID);
		try {
			retryThinkTime = getParameterValueInt(RETRY_THINK_TIME);
		} catch(Exception e){
//			 if parm not there just continue
			logger.warning( "the attribute:"+ RETRY_THINK_TIME+" is missing for scenario: " + getDescription()+" .Setting the default to 5 seconds");
			retryThinkTime = 5000;
		}
		// optional, but must be 0 or more
		try {
			overRideIdint = getParameterValueInt(OVERRIDE_ID_KEY);
		} catch(Exception e){
//			 if parm not there just continue
			logger.warning( "the attribute:"+ OVERRIDE_ID_KEY+" is missing for scenario: " + getDescription()+" .Setting the default to zero");
			overRideIdint = 0;
		}
		
		if (startHolderId <= 0) {
			throw new InvalidExecutionUnitParameterException(START_HOLDER_ID
					+ " is less or equal to 0.  "
					+ "Invalid value for scenario with description: "
					+ getDescription() + ".  Value must be one or more.");
		}
		if (endHolderId <= 0) {
			throw new InvalidExecutionUnitParameterException(END_HOLDER_ID
					+ " is less or equal to 0.  "
					+ "Invalid value for scenario with description: "
					+ getDescription() + ".  Value must be one or more.");
		}
		if (overRideIdint <0){
			throw new InvalidExecutionUnitParameterException (OVERRIDE_ID_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be one or more.");
		}
		
		try {
			maximumThreshold = getParameterValueInt(MAX_THRESHOLD);
		} catch(Exception e) {
			logger.fine("maximumThreshold parameter not specified, using default");
			maximumThreshold = 2;
		}
		
		try {
			enableVerify = getParameterValueBoolean(ENABLE_VERIFY_KEY);
		} catch(Exception e) {
			logger.warning(ENABLE_VERIFY_KEY +" parameter is not set, using default true.");
			enableVerify = true;
		}
		
		// check for the over-ride id, if set, run the read only 1 time and exit
		// else read from startId -> startId+count
		if (overRideIdint > 0) {
			startHolderId = overRideIdint;
			holderRange = 0;
		} else {
		holderRange = endHolderId - startHolderId;
		}
	}

	private IAnnuity rereadAnnuity( IAnnuity annuity){
		IAnnuity results = null;
		try{
		annuity.setConfiguration(getConfiguration());
		results = getServerAdapter().findAnnuityById(annuity);
		} catch (Exception e) {
			logger.severe("Failed to re-read annuity ID:"+annuity.getId()+"  Error: " + e);
			getExecutionUnitEvent().addException(e);
		}
		return results;
	}
	
	private void verifyAnnuityHolderValue(IAnnuityHolder annuityHolder)
			throws EntityNotFoundException, InvalidArgumentException,
			ServerAdapterCommunicationException, ServerInternalErrorException,
			ExecutionUnitVerificationException, RemoteException  {
		IAnnuityHolder results = getAnnuityBeansFactory().createAnnuityHolder();
		results.setId(annuityHolder.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findHolderById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, annuityHolder, results,
				"AnnuityHolder from Client is not equal to DB value",
				"Mismacth was found.");
		ExecutionUnitVerificationHelper.assertEqual(this,
				annuityHolder.getContact(), results.getContact(),
				"AnnuityHolder Contact from Client is not equal to DB value",
				"Mismacth was found.");
	}

	private List<IAnnuity> getAnnuities(IAnnuityHolder holder)
			throws ServerAdapterCommunicationException,
			InvalidArgumentException, ServerInternalErrorException, RemoteException  {
		holder.setConfiguration(getConfiguration());
		List<IAnnuity> annuities = getServerAdapter().findHolderAnnuities(holder);
		return annuities;
	}

	private IAnnuity updateAnnuityWithRider(IAnnuity annuity)
			throws EntityNotFoundException, EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, RemoteException  {
		// add 2 riders
		IRider rider1 = BasicExecutionUnitLibrarry
				.getRider(getAnnuityBeansFactory());
		IRider rider2 = BasicExecutionUnitLibrarry
				.getRider(getAnnuityBeansFactory());
		annuity.getRiders().add(rider1);
		annuity.getRiders().add(rider2);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);

		// remove 1st rider
		IRider removed = BasicExecutionUnitLibrarry
				.getRider(getAnnuityBeansFactory());
		removed.setId(rider1.getId());
		int removeIndex = 0;
		for (int i = 0; i < annuity.getRiders().size(); i++) {
			if (removed.getId().equals(annuity.getRiders().get(i).getId())) {
				removeIndex = i;
				break;
			}
		}
		annuity.getRiders().remove(removeIndex);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		removed.setConfiguration(getConfiguration());
		getServerAdapter().deleteRider(removed);

		// update 2nd rider with a new value
		rider2.setRule(getUpdatedString(rider2.getRule()));
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().findAnnuityById(annuity);
		for (IRider rider : annuity.getRiders()) {
			if (rider2.getId().equals(rider.getId())) {
				rider.setRule(getUpdatedString(rider.getRule()));
				break;
			}
		}
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		return annuity;
	}

	private IAnnuity updateAnnuityWithPayout(IAnnuity annuity)
			throws EntityNotFoundException, EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, RemoteException  {
		// add 2 payouts
		IPayout payout1 = BasicExecutionUnitLibrarry
				.getPayout(getAnnuityBeansFactory());
		payout1.setAnnuity(annuity);
		payout1.setTaxableAmount(new BigDecimal(getRandomInteger(100, 1000)));
		payout1.setConfiguration(getConfiguration());
		payout1 = getServerAdapter().createPayout(payout1);
		IPayout payout2 = BasicExecutionUnitLibrarry
				.getPayout(getAnnuityBeansFactory());
		payout2.setAnnuity(annuity);
		payout2.setTaxableAmount(new BigDecimal(getRandomInteger(100, 1000)));
		payout2.setConfiguration(getConfiguration());
		payout2 = getServerAdapter().createPayout(payout2);

		annuity.getPayouts().add(payout1);
		annuity.getPayouts().add(payout2);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);

		// remove 1st payout
		IPayout removed = BasicExecutionUnitLibrarry
				.getPayout(getAnnuityBeansFactory());
		removed.setId(payout1.getId());
		int removeIndex = 0;
		for (int i = 0; i < annuity.getPayouts().size(); i++) {
			if (removed.getId().equals(annuity.getPayouts().get(i).getId())) {
				removeIndex = i;
				break;
			}
		}
		annuity.getPayouts().remove(removeIndex);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		removed.setConfiguration(getConfiguration());
		getServerAdapter().deletePayout(removed);

		// update 2nd payout with a new value
		Calendar newEndDate = new GregorianCalendar();
		newEndDate.add(Calendar.MONTH, 11);
		for (IPayout payout : annuity.getPayouts()) {
			if (payout2.getId().equals(payout.getId())) {
				payout.setEndDate(newEndDate);
				payout.setTaxableAmount(new BigDecimal(getRandomInteger(10000,
						100000)));
				payout.setAnnuity(annuity);
				payout.setConfiguration(getConfiguration());
				payout = getServerAdapter().updatePayout(payout);
				break;
			}
		}
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		return annuity;
	}

	private IAnnuity updateAnnuityWithPayor(IAnnuity annuity)
			throws EntityNotFoundException, EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException  {
		// add 2nd payor
		logger.fine("Annuity:"+annuity.getId()+ " Payor list size:"+ annuity.getPayors().size());
		IPayor payor1 = BasicExecutionUnitLibrarry
				.getPayor(getAnnuityBeansFactory());
		payor1.setConfiguration(getConfiguration());
		payor1 = getServerAdapter().createPayor(payor1);
		IPayor payor2 = BasicExecutionUnitLibrarry
				.getPayor(getAnnuityBeansFactory());
		payor2.setConfiguration(getConfiguration());
		payor2 = getServerAdapter().createPayor(payor2);
		annuity.getPayors().add(payor1);
		annuity.getPayors().add(payor2);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		logger.fine("2 payors added? Annuity:"+annuity.getId()+ " Payor list size:"+ annuity.getPayors().size());

		// remove 1st payor
		IPayor removed = BasicExecutionUnitLibrarry
				.getPayor(getAnnuityBeansFactory());
		removed.setId(payor1.getId());
		int removeIndex = 0;
		boolean foundIt = false;
		for (int i = 0; i < annuity.getPayors().size(); i++) {
			if (removed.getId().equals(annuity.getPayors().get(i).getId())) {
				removeIndex = i;
				foundIt = true;
				break;
			}
		}
		
		if(!foundIt){
			throw new ExecutionUnitVerificationException(
					"Updated Annuity Payor1 from client list was not found in DB.  "
							+ " annuity payor size:"+annuity.getPayors().size()
							+ " Annuity:"+annuity.getId()
							+ " Payor id:"+removed.getId());
		}
		
		annuity.getPayors().remove(removeIndex);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		removed.setConfiguration(getConfiguration());
		getServerAdapter().deletePayor(removed);

		// update 2nd payor with a new value
		for (IPayor payor : annuity.getPayors()) {
			if (payor2.getId().equals(payor.getId())) {
				payor.setName(getUpdatedString(payor2.getName()));
				payor.setConfiguration(getConfiguration());
				payor = getServerAdapter().updatePayor(payor);
				break;
			}
		}
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		
		// remove the 2nd payor as well
		IPayor removed2 = BasicExecutionUnitLibrarry
			.getPayor(getAnnuityBeansFactory());
		removed2.setId(payor2.getId());
		int removeIndex2 = 0;
		boolean foundIt2 = false;
		for (int i = 0; i < annuity.getPayors().size(); i++) {
			if (removed2.getId().equals(annuity.getPayors().get(i).getId())) {
				removeIndex2 = i;
				foundIt2 = true;
				break;
			}
		}
		
		if(!foundIt2){
			throw new ExecutionUnitVerificationException(
					"Updated Annuity Payor2 from client list was not found in DB.  "
							+ " annuity payor size:"+annuity.getPayors().size()
							+ " Annuity:"+annuity.getId()
							+ " Payor id:"+removed.getId());
		}
		
		annuity.getPayors().remove(removeIndex2);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		removed2.setConfiguration(getConfiguration());
		getServerAdapter().deletePayor(removed2);

		return annuity;
	}

	private IAnnuity updateAnnuityAlone(IAnnuity annuity)
			throws EntityNotFoundException, EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, RemoteException  {
		if (annuity instanceof IEquityAnnuity) {
			return updateEquityAnnuity((IEquityAnnuity) annuity);
		} else if (annuity instanceof IFixedAnnuity) {
			return updateFixedAnnuity((IFixedAnnuity) annuity);
		} else {
			return updateBasicAnnuity(annuity);
		}
	}

	private IAnnuity updateBasicAnnuity(IAnnuity annuity)
			throws EntityNotFoundException, EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, RemoteException  {
		annuity.setAmount(new Double(111111));
		annuity.setLastPaidAmt(new Double(1111));
		annuity.setAccountNumber(getUpdatedString(annuity.getAccountNumber()));
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		return annuity;
	}

	private IEquityAnnuity updateEquityAnnuity(IEquityAnnuity annuity)
			throws EntityNotFoundException, EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, RemoteException  {
		annuity.setAmount(new Double(111111));
		annuity.setLastPaidAmt(new Double(1111));
		annuity.setFundNames(getUpdatedString(annuity.getFundNames()));
		annuity.setAccountNumber(getUpdatedString(annuity.getAccountNumber()));
		annuity.setIndexRate(new Double(0.88));
		annuity.setConfiguration(getConfiguration());
		annuity = (IEquityAnnuity) getServerAdapter().updateAnnuity(annuity);
		return annuity;
	}

	private IFixedAnnuity updateFixedAnnuity(IFixedAnnuity annuity)
			throws EntityNotFoundException, EntityAlreadyExistsException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, RemoteException  {

		annuity.setAmount(new Double(111111));
		annuity.setLastPaidAmt(new Double(1111));
		annuity.setAccountNumber(getUpdatedString(annuity.getAccountNumber()));
		annuity.setRate(new Double(0.88));
		annuity.setConfiguration(getConfiguration());
		annuity = (IFixedAnnuity) getServerAdapter().updateAnnuity(annuity);
		return annuity;
	}

	private void verifyAnnuityValues(IAnnuity annuity, AnnuityType annuityType)
			throws EntityNotFoundException, InvalidArgumentException,
			ServerAdapterCommunicationException, ServerInternalErrorException,
			ExecutionUnitVerificationException, RemoteException  {
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

	private String getUpdatedString(String st) {
		String newName = "";
		String first = "";
		String last = "";
		if (st.indexOf("**") < 0) {
			newName = st + "**0";
		} else {
			StringTokenizer parser = new StringTokenizer(st, "**");
			first = parser.nextToken();
			last = parser.nextToken();
			newName = first + "**" + (Integer.valueOf(last).intValue() + 1);
		}
		return newName;
	}

	private AnnuityType getAnnuityType(IAnnuity annuity) {
		if (annuity instanceof IEquityAnnuity) {
			return AnnuityType.EQUITY;
		} else if (annuity instanceof IFixedAnnuity) {
			return AnnuityType.FIXED;
		} else {
			return AnnuityType.BASIC;
		}
	}
}
