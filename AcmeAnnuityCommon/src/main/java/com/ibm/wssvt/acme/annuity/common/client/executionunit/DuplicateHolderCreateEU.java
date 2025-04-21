package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;

import com.ibm.wssvt.acme.annuity.common.bean.AnnuityHolderCategory;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
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

public class DuplicateHolderCreateEU extends AbastractAnnuityExecutionUnit {

	/**
	 * 1-Find a Holder, using an id param or a random generated id,
	 * 2-If there try to create with same id,
	 * 3-expect correct exception.
	 */
	private static final long serialVersionUID = -7924793447916434856L;
	
	/*
	 * useId and startHolderId/endHolderId are scenario params, useId is a specifc id to find,
	 * startHolderId/endHolderId is the range of the Holder table in the preloaded data base.
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
		logger.fine("Looking up id: "  + sHolderId);
		
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
		
		logger.fine("Creating annuity holder, id "  + annuityHolderReturn.getId());
		IAnnuityHolder annuityHolder = null;
		try{
			annuityHolder = createAnnuityHolder(annuityHolderReturn.getId());
			// should not get here.
			logger.warning("Failed on create duplicate Holder, created instead of expected exception. Error: ");
			logger.warning("Created id = " + annuityHolder.getId());
			Exception e = new ExecutionUnitVerificationException("Failed on create duplicate Holder, created instead of expected exception. Error: ");
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
	
	private IAnnuityHolder createAnnuityHolder(String id) 
		throws EntityAlreadyExistsException, InvalidArgumentException, 
		ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
		// we don't really need all of the generated holder contents, but, use the library anyway
		IAnnuityHolder annuityHolder = BasicExecutionUnitLibrarry.getAnnuityHolder(getAnnuityBeansFactory());
		annuityHolder.setCategory(getRandomEnum(AnnuityHolderCategory.class));		
		annuityHolder.setConfiguration(getConfiguration());
		// overlay generated id with our id
		annuityHolder.setId(id);
		annuityHolder =getServerAdapter().createAnnuityHolder(annuityHolder);
		return annuityHolder;
	}
}
