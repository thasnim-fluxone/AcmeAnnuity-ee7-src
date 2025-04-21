
package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.util.ServerExceptionMapper;
import com.ibm.wssvt.acme.annuity.common.util.ServerExceptionType;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class MultipleUpdateAnnuityEU extends AbastractAnnuityExecutionUnit {

	
	private static final long serialVersionUID = 5301722316503041271L;
	private int useId = -1;	
	private int holderStartId;
	private int holderEndId;
	private int maximumThreshold;
	boolean enableVerify = true;
	
	private static final String ENABLE_VERIFY_KEY = "enableVerify";	
	private static final String USE_ID_KEY = "useId";
	private static final String START_ID_KEY = "holderStartId";
	private static final String MAX_THRESHOLD_KEY = "maximumThreshold";
	private static final String END_ID_KEY = "holderEndId";
	
	
	AcmeLogger logger = null;
	
	public void execute() {	
		int threshold = 0;
		try{
			setScenarioVariables();		
			String holderId;
			if (useId == -1 ){
				holderId = "" + getRandomInteger(holderStartId, holderEndId);
			}else{
				holderId = "" + useId;
			}
			IAnnuityHolder holder = getAnnuityBeansFactory().createAnnuityHolder();
			holder.setId(holderId);
			holder.setConfiguration(getConfiguration());
			holder = getServerAdapter().findHolderById(holder);
			if (enableVerify) {
				if (holder == null) {
					throw new ExecutionUnitVerificationException ("returned holder for id: " + holderId + " is null.");
				}
				if (!(holderId.equals(holder.getId()))) {
					throw new ExecutionUnitVerificationException ("returned holder for id: " + holderId 
							+ " returned different holder.  returned holder id is: " + holder.getId());
				}
			}
			holder.setConfiguration(getConfiguration());
			List<IAnnuity> annList = getServerAdapter().findHolderAnnuities(holder);
			if (annList == null || annList.size() <1) {
				throw new ExecutionUnitVerificationException ("The holder has null or 0 annuities.  This test on this holder cannot be executed.");
			}
			
			IAnnuity annuity = annList.get(0);			
			IAnnuity updateOneResult = null;
			String origActNumberValue = null;
			logger.fine("Annuity id to be modified: " + annuity.getId());
			while(threshold <= maximumThreshold) {
				origActNumberValue = annuity.getAccountNumber();
				annuity.setAccountNumber("updated-1" + annuity.getAccountNumber());
				annuity.setConfiguration(getConfiguration());
				try {
					updateOneResult = getServerAdapter().updateAnnuity(annuity);
					break;
				} catch (Exception e) {
					ServerExceptionType exType = ServerExceptionMapper.getExceptionType(e);
					if (ServerExceptionType.OPTIMISTIC_LOCKING_EXCEPTION.equals(exType)) {
						logger.info("OPTIMISTIC LOCKING EXCEPTION encountered while updating annuity id: " + annuity.getId()
								+".  Trying again to a max of: " + maximumThreshold + " times. This is the: " + (threshold +1) + " attempt.");
						threshold++;		
						try{
							annuity.setConfiguration(getConfiguration());
							annuity = getServerAdapter().findAnnuityById(annuity);							
						}catch (Exception ex) {
							logger.warning("Failed to re-read annuity after OPTIMISTIC LOCK. Error: " + ex);
							getExecutionUnitEvent().addException(ex);
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
			if (enableVerify){
				if (updateOneResult == null) {
					throw new ExecutionUnitVerificationException ("Annuity update returned a null value.");
				}
				if (!(updateOneResult.getAccountNumber().startsWith("updated-1"))) {
					throw new ExecutionUnitVerificationException ("Annuity update returned invalid value for account number.");
				}
			}
			
			// now attempt to update annuity 2 - which should fail.
			annuity.setAccountNumber("updated-2" + annuity.getAccountNumber());
			annuity.setConfiguration(getConfiguration());
			try{
				IAnnuity res = getServerAdapter().updateAnnuity(annuity);
				throw new ExecutionUnitVerificationException ("Expected the update to throw an Optimistic Locking exception, " +
						"but it did not.  AnnuityId: " + annuity.getId() + " act# " + res.getAccountNumber());
			}catch (Exception e) {
				if (ServerExceptionType.OPTIMISTIC_LOCKING_EXCEPTION.equals(ServerExceptionMapper.getExceptionType(e))) {
					// good - pass test
				}else{
					throw new ExecutionUnitVerificationException ("Expected the update to throw an Optimistic Locking exception, but it threw: " + e);
				}
			}
			
			// clean up.
			annuity.setConfiguration(getConfiguration());
			IAnnuity finalResult = null;
			while(threshold <= maximumThreshold) {
				annuity = getServerAdapter().findAnnuityById(annuity);
				annuity.setAccountNumber(origActNumberValue);
				annuity.setConfiguration(getConfiguration());
				try {
					finalResult = getServerAdapter().updateAnnuity(annuity);
					break;
				} catch (Exception e) {
					ServerExceptionType exType = ServerExceptionMapper.getExceptionType(e);
					if (ServerExceptionType.OPTIMISTIC_LOCKING_EXCEPTION.equals(exType)) {
						logger.info("OPTIMISTIC LOCKING EXCEPTION encountered while updating annuity id: " + annuity.getId()
								+".  Trying again to a max of: " + maximumThreshold + " times. This is the: " + (threshold +1) + " attempt.");
						threshold++;						
					} else {  
						logger.warning("Failed to update annuity with rider. Error: " + e);
						getExecutionUnitEvent().addException(e);
						return;
					}
				}
			}
			threshold = 0;
			if (enableVerify){
				if (finalResult == null) {
					throw new ExecutionUnitVerificationException ("Annuity update returned a null value.");
				}
				if (!(finalResult.getAccountNumber()).equals(origActNumberValue)) {
					throw new ExecutionUnitVerificationException ("Annuity update returned invalid value for account number.");
				}
			}
		
		} catch (Exception e) {
			getExecutionUnitEvent().addException(e);
		}
		
	}
	
	
	/*
	 * 
	 */
	private void setScenarioVariables() throws InvalidExecutionUnitParameterException {		
		logger = getLogger(getClass().getName());		
		try {
			enableVerify = getParameterValueBoolean(ENABLE_VERIFY_KEY);
		} catch (InvalidExecutionUnitParameterException e1) {
			// the enable attribute is missing, or invalid - no big deal, set to true as default
			// and issue warning
			logger.warning("Missing or invalid value for: " + ENABLE_VERIFY_KEY + 
					" parameter.  for scenario: " + getDescription() + ".  Defaulting to true.");
			enableVerify = true;
		}
				
		try {			
			useId = getParameterValueInt(USE_ID_KEY);
		} catch (Exception e) {
			// if parm not there just continue
			logger.info("useId parameter not in config file, use random");
			useId = -1;
		}
		
		try {
			maximumThreshold = getParameterValueInt(MAX_THRESHOLD_KEY);
		} catch(Exception e) {
			logger.warning(MAX_THRESHOLD_KEY +" parameter not specified, using default set to 2.");
			maximumThreshold = 2;
		}
		
		if (useId == -1) {
			try{
				holderStartId = getParameterValueInt(START_ID_KEY);
			}catch (Exception e) {
				throw new InvalidExecutionUnitParameterException ("The parameter " + START_ID_KEY + " is invalid.  it must be an integer");
			}
			try{
				holderEndId = getParameterValueInt(END_ID_KEY);
			}catch (Exception e) {
				throw new InvalidExecutionUnitParameterException ("The parameter " + END_ID_KEY + " is invalid.  it must be an integer");
			}
			
			if (holderStartId <0 || holderEndId <0) {
				throw new InvalidExecutionUnitParameterException ("Either prameter: " + START_ID_KEY + " or parameter: " 
						+ END_ID_KEY + " is less than zero. Invalid values.");
			}
			if (holderStartId == holderEndId) {
				throw new InvalidExecutionUnitParameterException("The prameter: " + START_ID_KEY 
						+ " has the same value as parameter : " + END_ID_KEY + ". Invalid values.");
			}
			if (holderEndId < holderStartId) {
				throw new InvalidExecutionUnitParameterException ("Either prameter: " + END_ID_KEY + " is less than the parameter: " 
						+ START_ID_KEY + ". Invalid values.");
			}			
		}
	}	
}
