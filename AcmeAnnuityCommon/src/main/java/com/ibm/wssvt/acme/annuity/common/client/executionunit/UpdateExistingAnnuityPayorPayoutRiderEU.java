
package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

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

public class UpdateExistingAnnuityPayorPayoutRiderEU extends
		AbastractAnnuityExecutionUnit {

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
        
		logger.fine("Annuity size is " + annuities.size());
		for (IAnnuity annuity : annuities) {
			// go through each Annuity and update the children

			AnnuityType annuityType = getAnnuityType(annuity);
			logger.fine("Updating Annuity with Rider");
			while(threshold <= maximumThreshold ){
				try {
					annuity= addRidersToAnnuity(annuity);					
					break;
				} catch (Exception e) {
					ServerExceptionType exType = ServerExceptionMapper.getExceptionType(e);
					if (ServerExceptionType.OPTIMISTIC_LOCKING_EXCEPTION.equals(exType)) {
						logger.info("OPTIMISTIC LOCKING EXCEPTION encountered while updating annuity id: " + annuity.getId()
								+".  Trying again to a max of: " + maximumThreshold + " times. This is the: " + (threshold +1) + " attempt.");
						threshold++;
						annuity.setConfiguration(getConfiguration());
						try {
							annuity = getServerAdapter().findAnnuityById(annuity);
						} catch (Exception ex) {
							getExecutionUnitEvent().addException(e);
							return;
						}
					} else {  
						logger.warning("Failed to update annuity with rider. Error: " + e);
						getExecutionUnitEvent().addException(e);
						return;
					}
				}
			}
			threshold = 0;
			
			if (enableVerify) {
				logger.fine("Verifying Annuity with Rider");				
				try {					
					verifyAnnuityValues(annuity, annuityType);
				} catch (Exception e) {
					logger.warning("Failed to verify update annuity with rider. Error: " + e);
					getExecutionUnitEvent().addException(e);
				}
			}
			
			while(threshold <= maximumThreshold ){
				try {					
					annuity = removeRiderFromAnnuity(annuity);								
					break;
				} catch (Exception e) {
					ServerExceptionType exType = ServerExceptionMapper.getExceptionType(e);
					if (ServerExceptionType.OPTIMISTIC_LOCKING_EXCEPTION.equals(exType)) {
						logger.info("OPTIMISTIC LOCKING EXCEPTION encountered while updating annuity id: " + annuity.getId()
								+".  Trying again to a max of: " + maximumThreshold + " times. This is the: " + (threshold +1) + " attempt.");
						threshold++;
						annuity.setConfiguration(getConfiguration());
						try {
							annuity = getServerAdapter().findAnnuityById(annuity);
						} catch (Exception ex) {
							getExecutionUnitEvent().addException(e);
							return;
						}
					} else {  
						logger.warning("Failed to update annuity with rider. Error: " + e);
						getExecutionUnitEvent().addException(e);
						return;
					}
				}
			}
			threshold = 0;
			
			if (enableVerify) {
				logger.fine("Verifying Annuity with Rider");
				try {
					verifyAnnuityValues(annuity, annuityType);
				} catch (Exception e) {
					logger.warning("Failed to verify update annuity. Error: " + e);
					getExecutionUnitEvent().addException(e);
				}
			}
			
			while(threshold <= maximumThreshold ){
				try {
					annuity = updateAnnuityRiderRule(annuity);					
					break;
				} catch (Exception e) {
					ServerExceptionType exType = ServerExceptionMapper.getExceptionType(e);
					if (ServerExceptionType.OPTIMISTIC_LOCKING_EXCEPTION.equals(exType)) {
						logger.info("OPTIMISTIC LOCKING EXCEPTION encountered while updating annuity id: " + annuity.getId()
								+".  Trying again to a max of: " + maximumThreshold + " times. This is the: " + (threshold +1) + " attempt.");
						threshold++;
						annuity.setConfiguration(getConfiguration());
						try {
							annuity = getServerAdapter().findAnnuityById(annuity);
						} catch (Exception ex) {
							getExecutionUnitEvent().addException(e);
							return;
						}
					} else {  
						logger.warning("Failed to update annuity. Error: " + e);
						getExecutionUnitEvent().addException(e);
						return;
					}
				}
			}
			threshold = 0;
			
			if (enableVerify) {
				logger.fine("Verifying Annuity with Rider");
				try {
					verifyAnnuityValues(annuity, annuityType);
				} catch (Exception e) {
					logger.warning("Failed to verify update annuity. Error: " + e);
					getExecutionUnitEvent().addException(e);
				}
			}
			
			while(threshold <= maximumThreshold ){
				try {
					annuity = addPayoutsToAnnuity(annuity);
					break;
				} catch (Exception e) {
					ServerExceptionType exType = ServerExceptionMapper.getExceptionType(e);
					if (ServerExceptionType.OPTIMISTIC_LOCKING_EXCEPTION.equals(exType)) {
						logger.info("OPTIMISTIC LOCKING EXCEPTION encountered while updating annuity id: " + annuity.getId()
								+".  Trying again to a max of: " + maximumThreshold + " times. This is the: " + (threshold +1) + " attempt.");
						threshold++;
						annuity.setConfiguration(getConfiguration());
						try {
							annuity = getServerAdapter().findAnnuityById(annuity);
						} catch (Exception ex) {
							getExecutionUnitEvent().addException(e);
							return;
						}
					} else {  
						logger.warning("Failed to update annuity. Error: " + e);
						getExecutionUnitEvent().addException(e);
						return;
					}
				}
			}
			threshold = 0;
			
			if (enableVerify) {
				logger.fine("Verifying Annuity with Rider");
				try {
					verifyAnnuityValues(annuity, annuityType);
				} catch (Exception e) {
					logger.warning("Failed to verify update annuity. Error: " + e);
					getExecutionUnitEvent().addException(e);
				}
			}
			
			while(threshold <= maximumThreshold ){
				try {
					annuity = removeAnnuityPayout(annuity);					
					break;
				} catch (Exception e) {
					ServerExceptionType exType = ServerExceptionMapper.getExceptionType(e);
					if (ServerExceptionType.OPTIMISTIC_LOCKING_EXCEPTION.equals(exType)) {
						logger.info("OPTIMISTIC LOCKING EXCEPTION encountered while updating annuity id: " + annuity.getId()
								+".  Trying again to a max of: " + maximumThreshold + " times. This is the: " + (threshold +1) + " attempt.");
						threshold++;
						annuity.setConfiguration(getConfiguration());
						try {
							annuity = getServerAdapter().findAnnuityById(annuity);
						} catch (Exception ex) {
							getExecutionUnitEvent().addException(e);
							return;
						}
					} else {  
						logger.warning("Failed to update annuity. Error: " + e);
						getExecutionUnitEvent().addException(e);
						return;
					}
				}
			}
			threshold = 0;
			
			if (enableVerify) {
				logger.fine("Verifying Annuity with Rider");
				try {
					verifyAnnuityValues(annuity, annuityType);
				} catch (Exception e) {
					logger.warning("Failed to verify update annuity. Error: " + e);
					getExecutionUnitEvent().addException(e);
				}
			}
			
			while(threshold <= maximumThreshold ){
				try {
					annuity = updateAnnuityPayout(annuity);					
					break;
				} catch (Exception e) {
					ServerExceptionType exType = ServerExceptionMapper.getExceptionType(e);
					if (ServerExceptionType.OPTIMISTIC_LOCKING_EXCEPTION.equals(exType)) {
						logger.info("OPTIMISTIC LOCKING EXCEPTION encountered while updating annuity id: " + annuity.getId()
								+".  Trying again to a max of: " + maximumThreshold + " times. This is the: " + (threshold +1) + " attempt.");
						threshold++;
						annuity.setConfiguration(getConfiguration());
						try {
							annuity = getServerAdapter().findAnnuityById(annuity);
						} catch (Exception ex) {
							getExecutionUnitEvent().addException(e);
							return;
						}
					} else {  
						logger.warning("Failed to update annuity. Error: " + e);
						getExecutionUnitEvent().addException(e);
						return;
					}
				}
			}
			threshold = 0;
			
			if (enableVerify) {
				logger.fine("Verifying Annuity with Rider");
				try {
					verifyAnnuityValues(annuity, annuityType);
				} catch (Exception e) {
					logger.warning("Failed to verify update annuity. Error: " + e);
					getExecutionUnitEvent().addException(e);
				}
			}

			while(threshold <= maximumThreshold ){
				try {
					annuity = addPayorToAnnuity(annuity);					
					break;
				} catch (Exception e) {
					ServerExceptionType exType = ServerExceptionMapper.getExceptionType(e);
					if (ServerExceptionType.OPTIMISTIC_LOCKING_EXCEPTION.equals(exType)) {
						logger.info("OPTIMISTIC LOCKING EXCEPTION encountered while updating annuity id: " + annuity.getId()
								+".  Trying again to a max of: " + maximumThreshold + " times. This is the: " + (threshold +1) + " attempt.");
						threshold++;
						annuity.setConfiguration(getConfiguration());
						try {
							annuity = getServerAdapter().findAnnuityById(annuity);
						} catch (Exception ex) {
							getExecutionUnitEvent().addException(e);
							return;
						}
					} else {  
						logger.warning("Failed to update annuity. Error: " + e);
						getExecutionUnitEvent().addException(e);
						return;
					}
				}
			}
			threshold = 0;
			
			if (enableVerify) {
				logger.fine("Verifying Annuity with Rider");
				try {
					verifyAnnuityValues(annuity, annuityType);
				} catch (Exception e) {
					logger.warning("Failed to verify update annuity. Error: " + e);
					getExecutionUnitEvent().addException(e);
				}
			}
			
			while(threshold <= maximumThreshold ){
				try {
					annuity = removePayorFromAnnuity(annuity);					
					break;
				} catch (Exception e) {
					ServerExceptionType exType = ServerExceptionMapper.getExceptionType(e);
					if (ServerExceptionType.OPTIMISTIC_LOCKING_EXCEPTION.equals(exType)) {
						logger.info("OPTIMISTIC LOCKING EXCEPTION encountered while updating annuity id: " + annuity.getId()
								+".  Trying again to a max of: " + maximumThreshold + " times. This is the: " + (threshold +1) + " attempt.");
						threshold++;
						annuity.setConfiguration(getConfiguration());
						try {
							annuity = getServerAdapter().findAnnuityById(annuity);
						} catch (Exception ex) {
							getExecutionUnitEvent().addException(e);
							return;
						}
					} else {  
						logger.warning("Failed to update annuity. Error: " + e);
						getExecutionUnitEvent().addException(e);
						return;
					}
				}
			}
			threshold = 0;
			
			if (enableVerify) {
				logger.fine("Verifying Annuity with Rider");
				try {
					verifyAnnuityValues(annuity, annuityType);
				} catch (Exception e) {
					logger.warning("Failed to verify update annuity. Error: " + e);
					getExecutionUnitEvent().addException(e);
				}
			}

			while(threshold <= maximumThreshold ){
				try {
					annuity = updateAnnuityPayor(annuity);					
					break;
				} catch (Exception e) {
					ServerExceptionType exType = ServerExceptionMapper.getExceptionType(e);
					if (ServerExceptionType.OPTIMISTIC_LOCKING_EXCEPTION.equals(exType)) {
						logger.info("OPTIMISTIC LOCKING EXCEPTION encountered while updating annuity id: " + annuity.getId()
								+".  Trying again to a max of: " + maximumThreshold + " times. This is the: " + (threshold +1) + " attempt.");
						threshold++;
						annuity.setConfiguration(getConfiguration());
						try {
							annuity = getServerAdapter().findAnnuityById(annuity);
						} catch (Exception ex) {
							getExecutionUnitEvent().addException(e);
							return;
						}
					} else {  
						logger.warning("Failed to update annuity. Error: " + e);
						getExecutionUnitEvent().addException(e);
						return;
					}
				}
			}
			threshold = 0;
			
			if (enableVerify) {
				logger.fine("Verifying Annuity with Rider");
				try {
					verifyAnnuityValues(annuity, annuityType);
				} catch (Exception e) {
					logger.warning("Failed to verify update annuity. Error: " + e);
					getExecutionUnitEvent().addException(e);
				}
			}
					

			logger.fine("Updating Annuity by itself");
			threshold = 0;
			while(threshold <= maximumThreshold ){
				try {
					annuity = updateAnnuityAlone(annuity);
					break;
				} catch (Exception e) {
					ServerExceptionType exType = ServerExceptionMapper.getExceptionType(e);
					if (ServerExceptionType.OPTIMISTIC_LOCKING_EXCEPTION.equals(exType)) {
						logger.info("OPTIMISTIC LOCKING EXCEPTION encountered while updating annuity id: " + annuity.getId()
								+".  Trying again to a max of: " + maximumThreshold + " times. This is the: " + (threshold +1) + " attempt.");
						threshold++;
						annuity.setConfiguration(getConfiguration());
						try {
							annuity = getServerAdapter().findAnnuityById(annuity);
						} catch (Exception ex) {
							getExecutionUnitEvent().addException(e);
							return;
						}
					} else {  
						logger.warning("Failed to update annuity by itself. Error: " + e);
						getExecutionUnitEvent().addException(e);
						return;
					}
				}
			}
			threshold = 0;
			
			if (enableVerify) {
				logger.fine("Verifying Annuity with Rider");
				try {
					verifyAnnuityValues(annuity, annuityType);
				} catch (Exception e) {
					logger.warning("Failed to verify update annuity. Error: " + e);
					getExecutionUnitEvent().addException(e);
				}
			}
		}
	}

	private IAnnuityHolder findAnnuityHolderById(String randomHolderId) throws ServerAdapterCommunicationException, EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException, RemoteException {
		IAnnuityHolder holder;
		holder = getAnnuityBeansFactory().createAnnuityHolder();
		holder.setId(randomHolderId);
		holder.setConfiguration(getConfiguration());
		holder = getServerAdapter().findHolderById(holder);
		return holder;
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

	
	private List<IAnnuity> getAnnuities(IAnnuityHolder holder)
			throws ServerAdapterCommunicationException,
			InvalidArgumentException, ServerInternalErrorException, RemoteException  {
		holder.setConfiguration(getConfiguration());
		List<IAnnuity> annuities = getServerAdapter().findHolderAnnuities(holder);
		return annuities;
	}
	private IAnnuity addRidersToAnnuity(IAnnuity annuity) throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException  {
		IRider rider1 = BasicExecutionUnitLibrarry.getRider(getAnnuityBeansFactory());
		IRider rider2 = BasicExecutionUnitLibrarry.getRider(getAnnuityBeansFactory());
		annuity.getRiders().add(rider1);
		annuity.getRiders().add(rider2);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		return annuity;
	}
	
	private IAnnuity removeRiderFromAnnuity(IAnnuity annuity) throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException  {
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().findAnnuityById(annuity);
		if (annuity.getRiders() == null || annuity.getRiders().size() <1) {
			throw new ExecutionUnitVerificationException ("Attempting to remove a rider from annuity, but riders is either null or has 0 elements");
		}
		IRider removed = annuity.getRiders().get(0);
	
		annuity.getRiders().remove(0);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		
		removed.setConfiguration(getConfiguration());
		getServerAdapter().deleteRider(removed);
		
		return annuity;
	}
	
	private IAnnuity updateAnnuityRiderRule(IAnnuity annuity) 
	throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException  {
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().findAnnuityById(annuity);		
		IRider rider = annuity.getRiders().get(0);
		rider.setRule(getUpdatedString(rider.getRule()));				
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		return annuity;		
	}
	
	
	private IAnnuity addPayoutsToAnnuity(IAnnuity annuity) throws EntityAlreadyExistsException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, RemoteException  {		
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
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		
		return annuity;

	}
	
	private IAnnuity removeAnnuityPayout(IAnnuity annuity) throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException  {	
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().findAnnuityById(annuity);
		if (annuity.getPayouts() == null || annuity.getPayouts().size()<1) {
			throw new ExecutionUnitVerificationException ("Attempting to remove a payout from annuity, but payouts is either null or has 0 elements");
		}
		IPayout payout = annuity.getPayouts().get(0);
		annuity.getPayouts().remove(0);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		payout.setConfiguration(getConfiguration());
		getServerAdapter().deletePayout(payout);
		return annuity;
		
	}
	private IAnnuity updateAnnuityPayout(IAnnuity annuity) throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, ExecutionUnitVerificationException, EntityAlreadyExistsException, RemoteException  {	
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().findAnnuityById(annuity);
		if (annuity.getPayouts() == null || annuity.getPayouts().size()<1) {
			throw new ExecutionUnitVerificationException ("Attempting to update a payout from annuity, but payouts is either null or has 0 elements");
		}

		Calendar newEndDate = Calendar.getInstance();
		newEndDate.add(Calendar.MONTH, 11);
		IPayout payout = annuity.getPayouts().get(0);
		payout.setEndDate(newEndDate);
		payout.setTaxableAmount(new BigDecimal(getRandomInteger(10000,
				100000)));
		
		payout.setAnnuity(annuity);
		payout.setConfiguration(getConfiguration());
		payout = getServerAdapter().updatePayout(payout);		
		
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		return annuity;		
		
	}

	private IAnnuity addPayorToAnnuity(IAnnuity annuity) throws EntityAlreadyExistsException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, RemoteException  {
		IPayor payor1 = BasicExecutionUnitLibrarry.getPayor(getAnnuityBeansFactory());
		payor1.setConfiguration(getConfiguration());
		payor1 = getServerAdapter().createPayor(payor1);
		IPayor payor2 = BasicExecutionUnitLibrarry.getPayor(getAnnuityBeansFactory());
		payor2.setConfiguration(getConfiguration());
		payor2 = getServerAdapter().createPayor(payor2);
		annuity.getPayors().add(payor2);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		return annuity;
	}
	
	private IAnnuity removePayorFromAnnuity(IAnnuity annuity) throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException {
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().findAnnuityById(annuity);
		if (annuity.getPayors() == null || annuity.getPayors().size() <1) {
			throw new ExecutionUnitVerificationException ("Attempting to remove a payor from annuity, but payors is either null or has 0 elements");
		}
		IPayor payor = annuity.getPayors().get(0);
		annuity.getPayors().remove(0);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		payor.setConfiguration(getConfiguration());
		getServerAdapter().deletePayor(payor);
		
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
		return annuity;		
	}
	
	private IAnnuity updateAnnuityPayor(IAnnuity annuity) throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException {
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().findAnnuityById(annuity);
		if (annuity.getPayors() == null || annuity.getPayors().size() <1) {
			throw new ExecutionUnitVerificationException ("Attempting to update a payor from annuity, but payors is either null or has 0 elements");
		}
		IPayor payor = annuity.getPayors().get(0);
		payor.setName(getUpdatedString(payor.getName()));
		payor.setConfiguration(getConfiguration());
		payor = getServerAdapter().updatePayor(payor);
		
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().updateAnnuity(annuity);
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
