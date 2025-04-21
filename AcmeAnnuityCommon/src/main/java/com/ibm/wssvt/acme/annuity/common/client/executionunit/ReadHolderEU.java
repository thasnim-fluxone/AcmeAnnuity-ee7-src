package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class ReadHolderEU extends AbastractAnnuityExecutionUnit {

	/**
	 * This execution unit requires that the Annuity database 
	 * be pre-populated.
	 */
	private static final long serialVersionUID = -4362529230415268925L;

	/*
	 * useId and maxId are scenario params, useId is a specifc id to find,
	 * maxId is the upper limit of the Holder table in the preloaded data base.
	 */
	private String useId = null;	
	private int maxId = 0;
	private int startId = 0;
	// verbose, output to console
	boolean verbose = false;
	
	private static final String USE_ID = "useId";
	private static final String MAX_ID = "maxId";
	private static final String VERBOSE = "verbose";
	private static final String START_ID_KEY = "startId";
	
	AcmeLogger logger = null;
	
	public void execute() {
		String sHolderId = null;		
		IAnnuityHolder annuityHolderReturn = null;		
				
		setScenarioVariables();
		if (useId == null && maxId == 0) {
			logger.warning("Scenario parameter error: Either useId or maxId must be set > 0 for scenario: " + getDescription() + ": failed to execute");						
			return;
		}
		
		// using configured id or random?
		if(useId != null) 
			sHolderId = useId;
		else // generate a random holder id to lookup, 1 <-> maxId
			sHolderId = Integer.toString(getRandomInteger(startId, maxId));
		
		logger.fine("Looking up id: "  + sHolderId);
		if (verbose)
			System.out.println("Looking up id: " + sHolderId);
		try {
			annuityHolderReturn = findAnnuityHolder(sHolderId);
		} catch (Exception e) {
			logger.warning("failed to find AnnuityHolder, Id = " + sHolderId + " Error: " + e);
			getExecutionUnitEvent().addException(e);			
			return;
		}
		try {
			ExecutionUnitVerificationHelper.assertEquals(this, sHolderId, annuityHolderReturn.getId(), 
					"Returned Annuity Holder id is not equal to requested id", "Mismatch was found.");
		} catch (Exception e) {
			logger.warning("Failed on verify find annuity holder. Error: " +e);
			getExecutionUnitEvent().addException(e);
			return;
		}
		logger.fine("Successful find of Holder, id = " + annuityHolderReturn.getId());
		if (verbose)
			System.out.println("Successful find of Holder, id = " + annuityHolderReturn.getId() );
	}
	
	private void setScenarioVariables() {	
		logger = getLogger(getClass().getName());
		
		
		// retrieve the (optional) parms from the config file
		
		try {
			verbose = getParameterValueBoolean(VERBOSE);
		} catch (InvalidExecutionUnitParameterException e1) {
			// not there, leave default
			logger.fine("Missing or invalid value for: " + VERBOSE + 
					" parameter.  for scenario: " + getDescription() + "Defaulting to false.");
			verbose = false;
		}
		
		try {
			useId = getConfiguration().getParameterValue(USE_ID);
		} catch (Exception e) {
			// if parm not there just continue
			logger.fine("useId parameter not in config file, use random");
		}
		
		try {
			maxId = getParameterValueInt(MAX_ID);
		} catch (Exception e) {
			// if parm not there just continue
			logger.fine("maxId parameter not in config file, use default max");
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
