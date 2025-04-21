package com.ibm.wssvt.acme.annuity.common.client.executionunit.policy;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneficiary;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.BasicExecutionUnitLibrarry;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.ExecutionUnitVerificationHelper;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class ReadBeneficiaryFromHolderEU extends AbastractPolicyExecutionUnit {

	/**
	 * This execution unit requires that the Annuity (including Policy) database 
	 * be pre-populated.
	 * 1 - find Holder, based on entered id (useId) or random,
	 * 2 - find Beneficiaries attached to this Holder,
	 * 3 - validate ids for both
	 */
	private static final long serialVersionUID = 3877652038279179804L;

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
		IAnnuityHolder beneficiaryHolder = null;
		
		setScenarioVariables();
		if (useId == null && maxId == 0) {
			logger.severe("Scenario parameter error: Either useId or maxId must be set > 0 for scenario: " + getDescription() + ": failed to execute");						
			Exception e = new InvalidArgumentException("Scenario parameter error: Either useId or maxId must be set > 0 for scenario");
			getExecutionUnitEvent().addException(e);	
			return;
		}
		
		logger.fine("After getting parameters, Begin execution");

		if(useId != null) 
			sHolderId = useId;
		else // generate a random holder id to lookup, 1 <-> maxId
			sHolderId = Integer.toString(getRandomInteger(startId, maxId));
		
			
		int count = 0;
		while (beneficiaryHolder == null && count < 3) {
			// using configured holder id or random?
			if(useId != null) 
				sHolderId = useId;
			else // generate a random holder id to lookup, 1 <-> maxId
				sHolderId = Integer.toString(getRandomInteger(startId, maxId));
			
			logger.fine("Looking up id: "  + sHolderId);
			
			try {
				beneficiaryHolder = findAnnuityHolder(sHolderId);
			} catch (Exception e) {
				if (useId != null) {
					logger.warning("failed to find Holder, Id = " + sHolderId + " Error: " + e);
					getExecutionUnitEvent().addException(e);			
					return;
				} else {
					logger.warning("Exception while looking up Holder with, id: "  + sHolderId + "Error: " + e);
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
		logger.fine("Successful find of Holder, id = " + beneficiaryHolder.getId()+ " Call verify" );
		
		try {
			ExecutionUnitVerificationHelper.assertEquals(this, sHolderId, beneficiaryHolder.getId(), 
					"Returned Annuity Holder id is not equal to requested id", "Mismatch was found.");
		} catch (Exception e) {
			logger.severe("Failed on verify find holder. Error: " +e);
			getExecutionUnitEvent().addException(e);
			return;	
		}
		
		// get the beneficiaries for this Holder		
		
		List<IBeneficiary> beneficiaryListResults = null;
		try {
			logger.fine("Looking up beneficiaries for holder ID: " + beneficiaryHolder.getId());

			beneficiaryHolder.setConfiguration(getConfiguration());
			beneficiaryListResults = getServerAdapter().findHolderBeneficiaries(beneficiaryHolder);
			
			logger.fine("Looked up beneficiaries for holder ID: " + beneficiaryHolder.getId());
		} catch (Exception e) {
			logger.severe("Error Holder ID: " + beneficiaryHolder.getId()
					+ " failed finding beneficiaries. Exception: "+ e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		if (beneficiaryListResults != null) {
			
			logger.fine("HolderId: " + sHolderId + " has: " + beneficiaryListResults.size()
					+ " beneficiaries");
			
			Iterator<IBeneficiary> it = beneficiaryListResults.iterator();
			
			while (it.hasNext()) {
				try {
					IBeneficiary benef = it.next();
					
					logger.fine("HolderId: " + benef.getAnnuityHolderId()
								+ " Beneficiary ID: " + benef.getId());
					ExecutionUnitVerificationHelper.assertIdContains(this, benef.getId(), sHolderId, "ERROR: requested beneficiary id: "
							+ benef.getId(),
							" does not contain holder id: " + sHolderId);
					for(int i=0; i < benef.getBeneContacts().size(); i++) {
						logger.fine("Beneficiary's contact number (" + benef.getBeneContacts().get(i).getId().getContactType().toString() + ") is: " + benef.getBeneContacts().get(i).getPhone());
					}
				} catch (Exception e) {
					logger.severe("validating holder ID: " + sHolderId
							+ " Failed for beneficiary: ");
					getExecutionUnitEvent().addException(e);
					return;
				}
			}
		}
		else {
			logger.warning("Holder ID: " + sHolderId + " has 0 beneficiaries");
			Exception e = new EntityNotFoundException("Holder ID: " + sHolderId + " has 0 beneficiaries");
			getExecutionUnitEvent().addException(e);
			return;
		}

	}
	
	private void setScenarioVariables() {		
		logger = getLogger(getClass().getName());
		
//		 retrieve the (optional) params from the config file
		
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
