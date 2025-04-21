package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.util.ServerExceptionMapper;
import com.ibm.wssvt.acme.annuity.common.util.ServerExceptionType;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class UpdateRefactorEU extends AbastractAnnuityExecutionUnit {

	private static final long serialVersionUID = -8978758038714550844L;
	private static final String START_HOLDER_ID = "startHolderId";
	private static final String END_HOLDER_ID = "endHolderId";
	private static final String MAX_THRESHOLD = "maximumThreshold";
	private static final String ENABLE_VERIFY_KEY = "enableVerify";
	
	private int startHolderId;
	private int endHolderId;
	private int holderRange; // 0 to end-start
	private int maximumThreshold;
	private boolean enableVerify = true;
	
	private int retryThinkTime = 5000;
	
	private AcmeLogger logger;
	
	
	public void execute() {
		int threshold = 0;		
		try {
			setScenarioVariables();
		} catch (Exception e) {
			logger.warning("Invalid scenario parameters for scenario description:"
							+ getDescription() + "  Error is: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}
		
		
		String randomHolderId = "" + (startHolderId + getRandomInteger(0, holderRange));
		logger.fine("Got the random holder ID --- " + randomHolderId);
		logger.fine("Find the holder from existing database");
		IAnnuityHolder holder = null;
		try {
			holder = findAnnuityHolderById(randomHolderId);
			if (holder == null) {
				throw new ExecutionUnitVerificationException("Expected to find an Annuity Holder for id: " + randomHolderId
						+ " but found null");
			}
			if (!(holder.getId().equals(randomHolderId))) {
				throw new ExecutionUnitVerificationException("Found an Annuity Holder for id: " + randomHolderId
						+ " but the object id is different! - found id: " + holder.getId());
			}						
		} catch (Exception e) {
			logger.warning("Failed to find holder. ID - " + randomHolderId
					+ " Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}
		
		logger.fine("Getting list of Annuity from AnnuityHolder");
		List<IAnnuity> annuities = null;
		try {
			annuities = getAnnuities(holder);
			if (logger.getLogger().isLoggable(Level.FINE)) {
				String annIds = "";
				for (IAnnuity ann : annuities) {
					annIds += ann.getId() + " : ";
				}
				logger.fine("Got the the annuity IDs here --- " + annIds);
			}
		} catch (Exception e) {
			logger.warning("Failed to get list of annuities. Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}
		
		

		IAnnuity resultAnnuity = null;
		logger.fine("Annuity size is " + annuities.size());
		for (IAnnuity annuity : annuities) {
			// go through each Annuity and update the children
			
			
			
			/*
			 * Attempting to add riders to Annuity.
			 */
			logger.fine("attempting to add riders to annuity");
			ArrayList<String> riders = new ArrayList<String>();
			{
				logger.fine("Updating Annuity with Rider");
				while(threshold <= maximumThreshold ){
					try {
						resultAnnuity = addRidersToAnnuity(annuity, riders);
						break;
					} catch (Exception e) {
						threshold = recoverableExceptionCheck( e, threshold, annuity);
						if( threshold == 0 ){   
							logger.severe("Failed to update annuity with rider. ID:"+annuity.getId()+" Error: " + e);
							getExecutionUnitEvent().addException(e);
							return;
						}
					}
				}
				threshold = 0;

				if (enableVerify) {
					logger.fine("Verifying Annuity with Rider");				
					try {					
						verifyAnnuityValues(annuity, resultAnnuity);
					} catch (Exception e) {
						logger.warning("Failed to verify update annuity with rider. Error: " + e);
						getExecutionUnitEvent().addException(e);
					}
				}

				//Make sure the annuity currently being used is the most up to date one.
				annuity = resultAnnuity;
			}
			
			
			/*
			 * Attempting to update Riders
			 */
			logger.fine("Attempting to update Riders");
			{
				while(threshold <= maximumThreshold ){
					try {
						resultAnnuity = updateAnnuityRiderRule(annuity, riders.get(0));			
						break;
					} catch (Exception e) {
						
						threshold = recoverableExceptionCheck( e, threshold, annuity);
						if( threshold == 0 ){   
							logger.severe("Failed to update annuity with rider. ID:"+annuity.getId()+" Error: " + e);
							getExecutionUnitEvent().addException(e);
							return;
						}
					}
				}
				threshold = 0;

				if (enableVerify) {
					logger.fine("Verifying Annuity with Rider");				
					try {					
						verifyAnnuityValues(annuity, resultAnnuity);
					} catch (Exception e) {
						logger.warning("Failed to verify update annuity with rider. Error: " + e);
						getExecutionUnitEvent().addException(e);
					}
				}

				//Make sure the annuity currently being used is the most up to date one.
				annuity = resultAnnuity;
			}
			
			
			/*
			 * Attempting to remove riders from Annuity.
			 */
			logger.fine("Attempting to remove riders from Annuity");
			
				while(threshold <= maximumThreshold ){
					try {					
						resultAnnuity = removeRiderFromAnnuity(annuity, riders);								
						break;
					} catch (Exception e) {
						threshold = recoverableExceptionCheck( e, threshold, annuity);
						if( threshold == 0 ){   
							logger.severe("Failed to update annuity with rider. ID:"+annuity.getId()+" Error: " + e);
							getExecutionUnitEvent().addException(e);
							return;
						}
					}
				}
				threshold = 0;

				if (enableVerify) {
					logger.fine("Verifying Annuity with Rider");
					try {
						verifyAnnuityValues(annuity, resultAnnuity);
					} catch (Exception e) {
						logger.warning("Failed to verify update annuity. Error: " + e);
						getExecutionUnitEvent().addException(e);
					}
				}
//				Make sure the annuity currently being used is the most up to date one.
				annuity = resultAnnuity;
			
			
			
			
			
			/*
			 * Attempting to add payouts to annuity
			 */
			logger.fine("Attempting to add payors to annuity");
			ArrayList<String> payouts = new ArrayList<String>();
			
				while(threshold <= maximumThreshold ){
					try {
						resultAnnuity = addPayoutsToAnnuity(annuity, payouts);
						
						break;
					} catch (Exception e) {
						threshold = recoverableExceptionCheck( e, threshold, annuity);
						if( threshold == 0 ){   
							logger.severe("Failed to update annuity with payout. ID:"+annuity.getId()+" Error: " + e);
							getExecutionUnitEvent().addException(e);
							return;
						}
					}
				}
				threshold = 0;

				if (enableVerify) {
					logger.fine("Verifying Annuity with Rider");
					try {
						logger.fine("as = " + annuity.getPayors().size() + "ar = " + resultAnnuity.getPayors().size());
						verifyAnnuityValues(annuity, resultAnnuity);
					} catch (Exception e) {
						logger.warning("Failed to verify update annuity. Error: " + e);
						getExecutionUnitEvent().addException(e);
					}
				}
				annuity = resultAnnuity;
			
			
			
			
			/*
			 * Attempting to update payouts
			 */
			logger.fine("Attempting to update payouts");
			
				while(threshold <= maximumThreshold ){
					try {
						resultAnnuity = updateAnnuityPayout(annuity, payouts.get(0));
						break;
					} catch (Exception e) {
						threshold = recoverableExceptionCheck( e, threshold, annuity);
						if( threshold == 0 ){   
							logger.severe("Failed to update annuity with payout. ID:"+annuity.getId()+" Error: " + e);
							getExecutionUnitEvent().addException(e);
							return;
						}
					}
				}
				threshold = 0;

				if (enableVerify) {
					logger.fine("Verifying Annuity with Rider");
					try {
						verifyAnnuityValues(annuity, resultAnnuity);
					} catch (Exception e) {
						logger.warning("Failed to verify update annuity. Error: " + e);
						getExecutionUnitEvent().addException(e);
					}
				}
				annuity = resultAnnuity;
			
			
			
			
			/*
			 * Attempting to delete payouts
			 */
			logger.fine("Attempting to delete payouts");
			
				for(String payoutId : payouts)
				{
					while(threshold <= maximumThreshold ){
						try {
							resultAnnuity = removeAnnuityPayout(annuity, payoutId);
							break;
						} catch (Exception e) {
							threshold = recoverableExceptionCheck( e, threshold, annuity);
							if( threshold == 0 ){   
								logger.severe("Failed to update annuity with payout. ID:"+annuity.getId()+" Error: " + e);
								getExecutionUnitEvent().addException(e);
								return;
							}
						}
					}
					threshold = 0;

					if (enableVerify) {
						logger.fine("Verifying Annuity with Rider");
						try {
							verifyAnnuityValues(annuity, resultAnnuity);
						} catch (Exception e) {
							logger.warning("Failed to verify update annuity. Error: " + e);
							getExecutionUnitEvent().addException(e);
						}
					}
					annuity = resultAnnuity;
				}

			
			
//			
//			
//			/*
//			 * Attempting to add payors to annuity
//			 */
//			logger.fine("Attempting to add payors to annuity");
//			ArrayList<String> payors = new ArrayList<String>();
//			{
//				while(threshold <= maximumThreshold ){
//					try {
//						resultAnnuity = addPayorToAnnuity(annuity, payors);
//						break;
//					} catch (Exception e) {
//						threshold = recoverableExceptionCheck( e, threshold, annuity);
//						if( threshold == 0 ){   
//							logger.severe("Failed to update annuity with payor. ID:"+annuity.getId()+" Error: " + e);
//							getScenarioEvent().addException(e);
//							return;
//						}
//					}
//				}
//				threshold = 0;
//
//				if (enableVerify) {
//					logger.fine("Verifying Annuity with Rider");
//					try {
//						verifyAnnuityValues(annuity, resultAnnuity);
//					} catch (Exception e) {
//						logger.warning("Failed to verify update annuity. Error: " + e);
//						getScenarioEvent().addException(e);
//					}
//				}
//				annuity = resultAnnuity;
//			}
//			
//			
//			/*
//			 * Attempting to update payors
//			 */
//			logger.fine("Attempting to update payors");
//			{
//				while(threshold <= maximumThreshold ){
//					try {
//						resultAnnuity = updateAnnuityPayor(annuity, payors.get(0));
//						break;
//					} catch (Exception e) {
//						threshold = recoverableExceptionCheck( e, threshold, annuity);
//						if( threshold == 0 ){   
//							logger.severe("Failed to update annuity with payor. ID:"+annuity.getId()+" Error: " + e);
//							getScenarioEvent().addException(e);
//							return;
//						}
//					}
//				}
//				threshold = 0;
//
//				if (enableVerify) {
//					logger.fine("Verifying Annuity with Rider");
//					try {
//						verifyAnnuityValues(annuity, resultAnnuity);
//					} catch (Exception e) {
//						logger.warning("Failed to verify update annuity. Error: " + e);
//						getScenarioEvent().addException(e);
//					}
//				}
//				annuity = resultAnnuity;
//			}
//			
//			
//			/*
//			 * Attempting to delete payors
//			 */
//			logger.fine("Attempting to delete payors");
//			{
//				for(String payorId : payors)
//				{
//					while(threshold <= maximumThreshold ){
//						try {
//							resultAnnuity = removePayorFromAnnuity(annuity, payorId);
//							break;
//						} catch (Exception e) {
//							threshold = recoverableExceptionCheck( e, threshold, annuity);
//							if( threshold == 0 ){   
//								logger.severe("Failed to remove payor from annuity. ID:"+annuity.getId()+" Error: " + e);
//								getScenarioEvent().addException(e);
//								return;
//							}
//						}
//					}
//					threshold = 0;
//
//					if (enableVerify) {
//						logger.fine("Verifying Annuity with Rider");
//						try {
//							logger.fine("payors | as = " + annuity.getPayors().size() + "ar = " + resultAnnuity.getPayors().size());
//							logger.fine("payouts | as = " + annuity.getPayouts().size() + "ar = " + resultAnnuity.getPayouts().size());
//							
//							verifyAnnuityValues(annuity, resultAnnuity);
//						} catch (Exception e) {
//							logger.warning("Failed to verify update annuity. Error: " + e);
//							getScenarioEvent().addException(e);
//							return;
//						}
//					}
//					/*
//					try {
//						resultAnnuity.setConfiguration(getConfiguration());
//						annuity = getServerAdapter().findAnnuityById(resultAnnuity);
//					} catch (Exception e) {
//						logger.severe("big big problem");
//						e.printStackTrace();
//					}
//					*/
//					annuity = null;
//					annuity = resultAnnuity;
//				}
//
//			}
//			
			logger.fine("finish12");
			
			
			/*
			 * Attempting to update just the annuity
			 */
			/*
			logger.fine("Attempting to update annuity fields");
			{
				while(threshold <= maximumThreshold ){
					try {
						resultAnnuity = updateAnnuityAlone(annuity);
						break;
					} catch (Exception e) {
						threshold = recoverableExceptionCheck( e, threshold, annuity);
						if( threshold == 0 ){   
							logger.severe("Failed to update just the annuity. ID:"+annuity.getId()+" Error: " + e);
							getScenarioEvent().addException(e);
							return;
						}
					}
				}
				threshold = 0;

				if (enableVerify) {
					logger.fine("Verifying Annuity with Rider");
					try {
						verifyAnnuityValues(annuity, resultAnnuity);
					} catch (Exception e) {
						logger.warning("Failed to verify update annuity. Error: " + e);
						getScenarioEvent().addException(e);
						return;
					}
				}
				annuity = resultAnnuity;
			}
			*/
			
			
		}

		
		
	}
	
	
	
	private void setScenarioVariables() throws InvalidExecutionUnitParameterException {
		logger = getLogger(getClass().getName());
		// retrieve the parms from the config file
		startHolderId = getParameterValueInt(START_HOLDER_ID);
		endHolderId = getParameterValueInt(END_HOLDER_ID);
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
		holderRange = endHolderId - startHolderId;
		
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
	}
	
	
			
		
	private IPayout getAnnuityPayout(IAnnuity annuity, String payoutId)
	{
		for(int i = 0; i < annuity.getPayouts().size(); i ++)
		{
			if(annuity.getPayouts().get(i).getId().equals(payoutId))
			{
				return annuity.getPayouts().get(i);
			}
		}
		
		return null;
	}
	
	private boolean removeAnnuityPayoutHelp(IAnnuity annuity, String payoutId)
	{
		for(int i = 0; i < annuity.getPayouts().size(); i ++)
		{
			if(annuity.getPayouts().get(i).getId().equals(payoutId))
			{
				annuity.getPayouts().remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	private IAnnuity removeAnnuityPayout(IAnnuity annuity, String payoutId) throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException  {	
		IAnnuity annuityBug = annuity;
		annuityBug.setConfiguration(getConfiguration());
		annuityBug = getServerAdapter().findAnnuityById(annuityBug);
		if (annuityBug.getPayouts() == null || annuityBug.getPayouts().size()<1) {
			throw new ExecutionUnitVerificationException ("Attempting to remove a payout from annuity, but payouts is either null or has 0 elements");
		}
		
		IPayout payout = getAnnuityPayout(annuity, payoutId);
		if(removeAnnuityPayoutHelp(annuity, payoutId) == false)
		{
			throw new ExecutionUnitVerificationException ("The payout ID does not exist after it was added this is a possible defect");
		}
		annuity.setConfiguration(getConfiguration());
		IAnnuity resultAnnuity = getServerAdapter().updateAnnuity(annuity);
		payout.setConfiguration(getConfiguration());
		getServerAdapter().deletePayout(payout);
		return resultAnnuity;
	}
	
	private IAnnuity updateAnnuityPayout(IAnnuity annuity, String payoutId) throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, ExecutionUnitVerificationException, EntityAlreadyExistsException, RemoteException  {	
		
		IAnnuity annuityBug = annuity;
		annuityBug.setConfiguration(getConfiguration());
		annuityBug = getServerAdapter().findAnnuityById(annuityBug);
		if (annuityBug.getPayouts() == null || annuityBug.getPayouts().size()<1) {
			throw new ExecutionUnitVerificationException ("Attempting to update a payout from annuity, but payouts is either null or has 0 elements");
		}

		Calendar newEndDate = Calendar.getInstance();
		newEndDate.add(Calendar.MONTH, 11);
		IPayout payout = getAnnuityPayout(annuity, payoutId);
		if(payout == null)
		{
			throw new ExecutionUnitVerificationException ("The payout ID does not exist after it was added this is a possible defect");
		}
		payout.setEndDate(newEndDate);
		payout.setTaxableAmount(new BigDecimal(getRandomInteger(10000,
				100000)));
		
		payout.setAnnuity(annuity);
		payout.setConfiguration(getConfiguration());
		payout = getServerAdapter().updatePayout(payout);
		
		annuity.setConfiguration(getConfiguration());
		return getServerAdapter().updateAnnuity(annuity);
		
	}
	
	private IAnnuity addPayoutsToAnnuity(IAnnuity annuity, ArrayList<String> payouts) throws EntityAlreadyExistsException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, RemoteException  {		
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().findAnnuityById(annuity);		
		
		IPayout payout1 = BasicExecutionUnitLibrarry .getPayout(getAnnuityBeansFactory());
		payout1.setAnnuity(annuity);
		payout1.setTaxableAmount(new BigDecimal(getRandomInteger(100, 1000)));
		payout1.setConfiguration(getConfiguration());
		payout1 = getServerAdapter().createPayout(payout1);
		
		IPayout payout2 = BasicExecutionUnitLibrarry.getPayout(getAnnuityBeansFactory());
		payout2.setAnnuity(annuity);
		payout2.setTaxableAmount(new BigDecimal(getRandomInteger(100, 1000)));
		payout2.setConfiguration(getConfiguration());
		payout2 = getServerAdapter().createPayout(payout2);
						
		annuity.getPayouts().add(payout1);
		annuity.getPayouts().add(payout2);
		payouts.add(payout1.getId());
		payouts.add(payout2.getId());
		
		annuity.setConfiguration(getConfiguration());
		
		return getServerAdapter().updateAnnuity(annuity);

	}
	
	
	
	
	private IAnnuity updateAnnuityRiderRule(IAnnuity annuity, String riderId) throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
		IRider rider = null;
		
		for(int i = 0; i < annuity.getRiders().size(); i++)
		{
			if(annuity.getRiders().get(i).getId().equals(riderId))
			{
				rider = annuity.getRiders().get(i);
				break;
			}
		}
		rider.setRule(getUpdatedString(rider.getRule()));	
		annuity.setConfiguration(getConfiguration());
		return getServerAdapter().updateAnnuity(annuity);	
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
	
	private IAnnuityHolder findAnnuityHolderById(String randomHolderId) throws ServerAdapterCommunicationException, EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException, RemoteException  {
		IAnnuityHolder holder;
		holder = getAnnuityBeansFactory().createAnnuityHolder();
		holder.setId(randomHolderId);
		holder.setConfiguration(getConfiguration());
		holder = getServerAdapter().findHolderById(holder);
		return holder;
	}
	
	
	private List<IAnnuity> getAnnuities(IAnnuityHolder holder) throws ServerAdapterCommunicationException,InvalidArgumentException, ServerInternalErrorException, RemoteException  {
		holder.setConfiguration(getConfiguration());
		List<IAnnuity> annuities = getServerAdapter().findHolderAnnuities(holder);
		return annuities;
	}
	
	
	private IAnnuity addRidersToAnnuity(IAnnuity annuity, ArrayList<String> toAdd) throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException  {
		IRider rider1 = BasicExecutionUnitLibrarry.getRider(getAnnuityBeansFactory());
		IRider rider2 = BasicExecutionUnitLibrarry.getRider(getAnnuityBeansFactory());
		annuity.getRiders().add(rider1);
		annuity.getRiders().add(rider2);
		toAdd.add(rider1.getId());
		toAdd.add(rider2.getId());
		annuity.setConfiguration(getConfiguration());
		return getServerAdapter().updateAnnuity(annuity);
	}
	
	private IAnnuity removeRiderFromAnnuity(IAnnuity annuity, ArrayList<String> toRemove) throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException  {
		annuity.setConfiguration(getConfiguration());
		
		//This protects against defects that don't properly update the annuity.
		IAnnuity annuityBug = annuity;
		annuityBug.setConfiguration(getConfiguration());
		annuityBug = getServerAdapter().findAnnuityById(annuityBug);
		if (annuityBug.getRiders() == null || annuityBug.getRiders().size() <1) {
			throw new ExecutionUnitVerificationException ("Attempting to remove a rider from annuity, but riders is either null or has 0 elements");
		}
		
		List<IRider> curRiders = annuity.getRiders();
		ArrayList<IRider> ridersToRemove = new ArrayList<IRider>();
		for(int i = 0; i < toRemove.size(); i++)
		{
			for(int c = 0; c < curRiders.size(); c++)
			{
				if(curRiders.get(c).getId().equals(toRemove.get(i)))
				{
					ridersToRemove.add(curRiders.get(c));
					curRiders.remove(c);
					break;
				}
			}
		}
		
		annuity.setRiders(curRiders);
		annuity.setConfiguration(getConfiguration());
		IAnnuity toReturn = getServerAdapter().updateAnnuity(annuity);
		
		//could be a problem if curRider is managed by the em.
		for(IRider curRider : ridersToRemove)
		{
			curRider.setConfiguration(getConfiguration());
			getServerAdapter().deleteRider(curRider);
		}
		
		return toReturn;
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
	
	private void verifyAnnuityValues(IAnnuity annuity, IAnnuity results)throws EntityNotFoundException, InvalidArgumentException,ServerAdapterCommunicationException, ServerInternalErrorException,ExecutionUnitVerificationException 
	{
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
