package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
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

public class DuplicateAnnuityCreateEU extends AbastractAnnuityExecutionUnit {

	/**
	 * 1-Find Annuity using either a configured id from params or a random,
	 * 2-try to create another using same id,
	 * 3-expect correct exception
	 */
	private static final long serialVersionUID = 7091502946413030326L;
	
	/*
	 * useId and maxId are scenario params, useId is a specifc id to find,
	 * maxId is the upper limit of the Holder table in the preloaded data base.
	 */
	private String useId = null;	
	private int startHolderId = 0;
	private int endHolderId = 0;
	boolean enableVerify = true;
	
	private static final String ENABLE_VERIFY_KEY = "enableVerify";	
	private static final String USE_ID = "useId";
	private static final String START_ID = "startHolderId";
	private static final String END_ID = "endHolderId";
	
	AcmeLogger logger = null;

	public void execute() {
		setScenarioVariables();
		String sHolderId = null;		
		IAnnuityHolder annuityHolderReturn = null;
		IAnnuity annuityReturn = null;
		List<IAnnuity> annuities = new ArrayList<IAnnuity>();
		
		if (useId == null && endHolderId == 0) {
			logger.severe("Scenario parameter error: Either useId or endHolderId must be set > 0 for scenario: " + getDescription() + ": failed to execute");						
			Exception e = new InvalidArgumentException("Scenario parameter error: Either useId or endHolderId must be set > 0 for scenario");
			getExecutionUnitEvent().addException(e);	
			return;
		}		
		// using configured id or random?
		if (useId == null) {
			sHolderId =  ""	+ (startHolderId + getRandomInteger(0, endHolderId - startHolderId));
		} else {
			sHolderId = useId;
		}
		logger.fine("Looking up holder id: "  + sHolderId);
		
		try {
			annuityHolderReturn = findAnnuityHolder(sHolderId);
		} catch (Exception e) {
			logger.warning("Failed to find AnnuityHolder, Id = " + sHolderId + " Error: " + e);
			getExecutionUnitEvent().addException(e);			
			return;
		}

		// just make sure we read the one we want
		try {
			ExecutionUnitVerificationHelper.assertEquals(this, sHolderId, annuityHolderReturn.getId(), 
					"Returned Annuity Holder id is not equal to requested id", "Mismatch was found.");
		} catch (Exception e) {
			logger.info("Failed on verify find annuity holder. Error: " +e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		try {
			annuityHolderReturn.setConfiguration(getConfiguration());
			annuities = getServerAdapter().findHolderAnnuities(annuityHolderReturn);
			annuityReturn = annuities.get(0);
		} catch (Exception e) {
			logger.warning("Failed to find list of annuities from holder Id = " + sHolderId + " Error: " + e);
			getExecutionUnitEvent().addException(e);			
			return;
		}		
		
		logger.fine("creating annuity, id "  + annuityReturn.getId());
		IAnnuity annuity = null;		
		AnnuityType annuityType = getRandomEnum(AnnuityType.class);
		try{
			annuity = createAnnuity(annuityReturn.getId(), annuityType);
			// should not get here.
			logger.warning("Failed on create duplicate Annuity, created instead of expected exception. Error: ");
			logger.warning("Created id = " + annuity.getId());
			Exception e = new ExecutionUnitVerificationException("Failed on create duplicate Annuity, created instead of expected exception. Error: ");
			getExecutionUnitEvent().addException(e);
			return;
		} catch (Exception e) {
			if (ServerExceptionType.ENTITY_ALREADY_EXISTS_EXCEPTION.equals(ServerExceptionMapper.getExceptionType(e))) {
				logger.fine("Success, received expected exception when trying to create an existing Annuity");
				return;
			} else {
				logger.info("Failed on creating duplicate Annuity. expecting EntityAlreadyExistsException, got: " + e);
				getExecutionUnitEvent().addException(e);
				return;
			}
		}
	}
	
	private void setScenarioVariables() {		
		logger = getLogger(getClass().getName());
		
		try {
			enableVerify = getParameterValueBoolean(ENABLE_VERIFY_KEY);
		} catch (InvalidExecutionUnitParameterException e1) {
			// the enable attribute is missing, or invalid - no big deal, set to true as default
			// and issue warning
			logger.warning("Missing or invalid value for: " + ENABLE_VERIFY_KEY + 
					" parameter.  for scenario: " + getDescription() + "Defaulting to true." +
					" current value is: " + getConfiguration().getParameterValue(ENABLE_VERIFY_KEY));
			enableVerify = true;
		}
		// retrieve the (optional) parms from the config file
		try {
			useId = getConfiguration().getParameterValue(USE_ID);
			startHolderId = endHolderId = 0;
		} catch (Exception e) {
			// if parm not there just continue
			logger.fine("useId parameter not in config file, use random");
		}

		if (useId == null) {
			try {
				startHolderId = getParameterValueInt(START_ID);
			} catch (Exception e) {
				// if parm not there just continue
				logger.fine("startHolderId parameter not in config file, use default startHolderId");
			}	
			
			try {
				endHolderId = getParameterValueInt(END_ID);
			} catch (Exception e) {
				// if parm not there just continue
				logger.fine("endHolderId parameter not in config file, use default endHolderId");
			}	
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
	
	private IAnnuity createAnnuity(String id, AnnuityType annuityType) 
	throws EntityAlreadyExistsException, InvalidArgumentException, 
	ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
		IAnnuity annuity = BasicExecutionUnitLibrarry.getAnnuity(getAnnuityBeansFactory(), annuityType);
		// overlay generated id with the one we want to use		
		annuity.setId(id);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().createAnnuity(annuity);
		return annuity;
	}
}
