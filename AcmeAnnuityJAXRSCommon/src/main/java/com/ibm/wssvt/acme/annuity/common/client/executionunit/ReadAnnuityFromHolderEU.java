package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class ReadAnnuityFromHolderEU extends AbastractAnnuityExecutionUnit {

	/**
	 * This execution unit requires that the Annuity database 
	 * be pre-populated.
	 * 1 - find Holder, based on entered id (useId) or random,
	 * 2 - find Annuities attached to this Holder,
	 * 3 - validate ids for both
	 */
	private static final long serialVersionUID = -5232684206187027962L;

	/*
	 * useId and maxId are scenario params, useId is a specifc id to find,
	 * maxId is the upper limit of the Holder table in the preloaded data base.
	 */
	private String useId = null;	
	private int maxId = 0;
	private int startId = 0;
	
	private static final String USE_ID = "useId";
	private static final String MAX_ID = "maxId";
	private static final String START_ID_KEY = "startId";
	AcmeLogger logger = null;
	
	public void execute() {
		String sHolderId = null;		
		IAnnuityHolder annuityHolderReturn = null;
		String sAnnuityId = null;				
						
		setScenarioVariables();
		if (useId == null && maxId == 0) {
			logger.severe("Scenario parameter error: Either useId or maxId must be set > 0 for scenario: " + getDescription() + ": failed to execute");						
			Exception e = new InvalidArgumentException("Scenario parameter error: Either useId or maxId must be set > 0 for scenario");
			getExecutionUnitEvent().addException(e);	
			return;
		}		
		
		logger.fine("After getting parameters, Begin execution");
		int count = 0;
		while (annuityHolderReturn == null && count < 3) {
			// using configured holder id or random?
			if(useId != null) 
				sHolderId = useId;
			else // generate a random holder id to lookup, 1 <-> maxId
				sHolderId = Integer.toString(getRandomInteger(startId, maxId));
			
			logger.fine("Looking up id: "  + sHolderId);
			
			try {
				annuityHolderReturn = findAnnuityHolder(sHolderId);
			} catch (Exception e) {
				if (useId != null) {
					logger.warning("failed to find Holder, Id = " + sHolderId + " Error: " + e);
					getExecutionUnitEvent().addException(e);			
					return;
				} else {
					logger.warning("Exception while looking up Annuity, id: "  + sAnnuityId + "Error: " + e);
					// just continue in while to get new random id
					count++;
					continue;
				}					
			}
		}
		if (count >= 3) {
			logger.warning("failed to find Holder in " + count + " attempts, Id = " + sHolderId);
			Exception e = new EntityNotFoundException("failed to find Holder in " + count + " attempts, Id = " + sHolderId);
			getExecutionUnitEvent().addException(e);
			return;
		}
		logger.fine("Successful find of Holder, id = " + annuityHolderReturn.getId()+ "Call verify" );
		
		try {
			ExecutionUnitVerificationHelper.assertEquals(this, sHolderId, annuityHolderReturn.getId(), 
					"Returned Annuity Holder id is not equal to requested id", "Mismatch was found.");
		} catch (Exception e) {
			logger.severe("Failed on verify find annuity holder. Error: " +e);
			getExecutionUnitEvent().addException(e);
			return;	
		}		
		
		// get the annuities for this Holder
		List<IAnnuity> annuityListResults = null;
		try {
			logger.fine("Looking up annuities for holder ID: " + annuityHolderReturn.getId());

			annuityHolderReturn.setConfiguration(getConfiguration());		
			annuityListResults = getServerAdapter().findHolderAnnuities(annuityHolderReturn);

			logger.fine("Looked up annuities for holder ID: " + annuityHolderReturn.getId());
		} catch (Exception e) {
			logger.severe("Error Holder ID: " + annuityHolderReturn.getId()
					+ " failed finding annuities. Exception: "+ e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		if (annuityListResults != null) {
			
			logger.fine("HolderId: " + sHolderId + " has: " + annuityListResults.size()
					+ " annuities");
			
			Iterator<IAnnuity> it = annuityListResults.iterator();
			
			while (it.hasNext()) {
				try {
					IAnnuity ann = it.next();

					logger.fine("HolderId: " + ann.getAnnuityHolderId()
								+ " Annuity ID: " + ann.getId());
					ExecutionUnitVerificationHelper.assertIdContains(this, ann.getAnnuityHolderId(), sHolderId, "ERROR: requested annuity id: "
							+ ann.getAnnuityHolderId(),
							" does not contain holder id: " + sHolderId);
					
				} catch (Exception e) {
					logger.severe("validating holder ID: " + sHolderId
							+ " Failed for annuity: ");
					getExecutionUnitEvent().addException(e);
					return;
				}
			}
		}
		else {
			logger.warning("Holder ID: " + sHolderId + " has 0 annuities");
			Exception e = new EntityNotFoundException("Holder ID: " + sHolderId + " has 0 annuities");
			getExecutionUnitEvent().addException(e);
			return;
		}

	} // end execute()
	
	/*
	 * 
	 */
	private void setScenarioVariables() {		
		logger = getLogger(getClass().getName());
		
//		 retrieve the (optional) parms from the config file
		
		try {			
			useId = getConfiguration().getParameterValue(USE_ID);
		} catch (Exception e) {
			// if parm not there just continue
			logger.warning("useId parameter not in config file, use random");
		}
		
		try {
			maxId = getParameterValueInt(MAX_ID);
		} catch (Exception e) {
			// if parm not there just continue
			logger.warning("maxId parameter not in config file, use default max");
		}	
		
//		 optional, but must be 1 or more
		try {
			startId = getParameterValueInt(START_ID_KEY);
		} catch(Exception e){
//			 if parm not there just continue
			logger.warning( "the attribute:"+ START_ID_KEY+" is missing for scenario: " + getDescription()+" .Setting the default to 1");
			startId = 1;
		}
	}
	/*
	 * 
	 */	
	private IAnnuityHolder findAnnuityHolder(String id) 
	throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
		IAnnuityHolder annuityHolder = null;
		
		annuityHolder = BasicExecutionUnitLibrarry.getAnnuityHolder(getAnnuityBeansFactory());
		annuityHolder.setId(id);
		annuityHolder.setConfiguration(getConfiguration());
		annuityHolder = getServerAdapter().findHolderById(annuityHolder);		
		return annuityHolder;
	}
}
