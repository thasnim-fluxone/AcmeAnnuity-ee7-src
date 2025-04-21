package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.util.ServerExceptionMapper;
import com.ibm.wssvt.acme.annuity.common.util.ServerExceptionType;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class DeletePayorWODeletingFromAnnuityEU extends AbastractAnnuityExecutionUnit {

	/**
	 * 1- find an Annuity that has a Payor link,
	 * 2- delete the Payor without deleting from Annuity,
	 * 3- expect error.
	 */
	private static final long serialVersionUID = -4336074758814892669L;
	/*
	 * useId is a specifc id to try to delete, 
	 * assumes knowledge that it doesn't exist.
	 */
	private String useId = null;	
	private int startHolderId = 0;
	private int endHolderId = 0;
	
	private static final String USE_ID = "useId";
	private static final String START_ID = "startHolderId";
	private static final String END_ID = "endHolderId";
	
	AcmeLogger logger = null;

	public void execute() {
		setScenarioVariables();
		String sHolderId = null;	
		String sAnnuityId = null;
		IAnnuityHolder annuityHolderReturn = null;
		IAnnuity returnAnnuity = null;
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
			returnAnnuity = annuities.get(0);
		} catch (Exception e) {
			logger.warning("Failed to find list of annuities from holder Id = " + sHolderId + " Error: " + e);
			getExecutionUnitEvent().addException(e);			
			return;
		}		
		
		sAnnuityId = returnAnnuity.getId();
		
		logger.fine("Found Annuity,  look for Payor link.");
		List<IPayor> payorsList = returnAnnuity.getPayors();
		// if no payor on this annuity, then error
		if (payorsList == null) {
			logger.fine("This Annuity did not have any Payors, unable to continue");
			Exception e = new ExecutionUnitVerificationException("This Annuity did not have any Payors, unable to continue.");
			getExecutionUnitEvent().addException(e);			
			return;
		}
		logger.fine(" AnnuityID:"+returnAnnuity.getId()+ " #payors: " + payorsList.size());
		
		for (IPayor payor : payorsList) {
			logger.fine("Validating - AnnuityID: " + returnAnnuity.getId()+ " payor: " + payor.getId());			
		}
		// use the first one in the list
		IPayor payorToDelete = payorsList.get(0);
		logger.fine("Try to delete the payor: " + payorToDelete.getId());
		try {
			deletePayor(payorToDelete);
			logger.fine("No exception on delete of Payor, this is an error.");
			Exception e = new ExecutionUnitVerificationException("No exception on delete of Payor, this is an error.");
			getExecutionUnitEvent().addException(e);			
			return;
		} catch (Exception e) {
			if (ServerExceptionType.FOREIGN_KEY_VIOLATION_EXCEPTION.equals(ServerExceptionMapper.getExceptionType(e))) {
				logger.fine("Success - got expected exception on delete of payor");
			} else {
				logger.info("Fail - got different exception on delete of payor, error: " + e);
				getExecutionUnitEvent().addException(e);
				return;
			}
		}		
		
		// find Annuity again and validate the payor list
		logger.fine("Second lookup of Annuity id: "  + sAnnuityId);
		try {
			returnAnnuity = findAnnuity(sAnnuityId);						
		} catch(EntityNotFoundException e){			
			logger.warning("Got a not found exception on second lookup of : "  + sAnnuityId);
			getExecutionUnitEvent().addException(e);			
			return;			
		} catch (Exception e1) {			
			logger.warning("Unexpected exception on second lookup of , Id = " + sAnnuityId + " Error: " + e1);
			getExecutionUnitEvent().addException(e1);			
			return;
		}
		logger.fine("Second time - Found Annuity,  look for Payor link.");
		payorsList = returnAnnuity.getPayors();
		// if no payor on this annuity, then error
		if (payorsList == null) {
			logger.fine("Annuity did not have any Payors this time - error");
			Exception e = new ExecutionUnitVerificationException("This Annuity did not have any Payors after exception on delete of Payor");
			getExecutionUnitEvent().addException(e);			
			return;
		}
		logger.fine(" AnnuityID:"+returnAnnuity.getId()+ " #payors: " + payorsList.size());
		
		for (IPayor payor : payorsList) {
			logger.fine("Validating - AnnuityID: " + returnAnnuity.getId()+
					" payor: " + payor.getId());			
		}
		// use the first one in the list again
		IPayor payorToDelete2 = payorsList.get(0);
		
		try {			
			ExecutionUnitVerificationHelper.assertEquals(this, payorToDelete2.getId(), payorToDelete.getId(),
					"Exception from validating Payor ids after attempted delete ", "Mismatch was found.");
		} catch (ExecutionUnitVerificationException e) {
			logger.fine("Exception from validating Payor ids after attempted delete - : " + e);
			getExecutionUnitEvent().addException(e);			
			return;
		}
		
		logger.fine("Validation successful, Payor list the same after attempted delete.");
	}


	private void setScenarioVariables() {		
		logger = getLogger(getClass().getName());

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
	
	private IAnnuity findAnnuity(String id) 
	throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
		
		IAnnuity result = null;
		
		IAnnuity annuity = BasicExecutionUnitLibrarry.getBasicAnnuity(getAnnuityBeansFactory());
		annuity.setId(id);
		annuity.setConfiguration(getConfiguration());		
		result = getServerAdapter().findAnnuityById(annuity);
			
		return result;
	}
	/*
	 * 
	 */
	private void deletePayor(IPayor payor)
	throws EntityAlreadyExistsException, InvalidArgumentException,
	ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
		IPayor removed = BasicExecutionUnitLibrarry.getPayor(getAnnuityBeansFactory());
		removed.setId(payor.getId());
		removed.setConfiguration(getConfiguration());
		getServerAdapter().deletePayor(removed);
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
