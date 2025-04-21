package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.util.ServerExceptionMapper;
import com.ibm.wssvt.acme.annuity.common.util.ServerExceptionType;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/**
 * $Rev: 746 $
 * $Date: 2007-09-07 09:57:38 -0500 (Fri, 07 Sep 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class UpdateAnnuityWithProvidedIdEU extends AbastractAnnuityExecutionUnit {
	
	private static final long serialVersionUID = -4582734443081148303L;
	private static final String ID = "id";
	private AcmeLogger logger;
	private static final String MAX_THRESHOLD = "maximumThreshold";
	private int maximumThreshold;
	
	public void execute() {
		try{			
			setScenarioVariables();
			IAnnuity annuity = getAnnuityBeansFactory().createAnnuity();
			String id = getConfiguration().getParameterValue(ID);
			annuity.setId(id);
			annuity.setConfiguration(getConfiguration());
			IAnnuity results = getServerAdapter().findAnnuityById(annuity);				
			
			verifyFindSuccess(results);			
			String prefixUpdate = ""  +  System.currentTimeMillis();
			try{
				updateAnnuity(annuity, prefixUpdate);
			}catch (Exception e) {
				examinException(annuity, e);					
			}
			
			results = getServerAdapter().findAnnuityById(annuity);
			verifyFindSuccess(results);
			if (!(results.getAccountNumber().startsWith(prefixUpdate))){
				throw new ExecutionUnitVerificationException("Updated Annuity with id:" + id + " failed."
						+ " Expected account number to start with the words: " + prefixUpdate 
						+ " but found: " + annuity.getAccountNumber());	
			}
						
		}catch(Exception e){
			getExecutionUnitEvent().addException(e);			
		}		
	}

	
	private void examinException(IAnnuity annuity, Exception e) {		
		boolean success = false;
		ServerExceptionType exType = ServerExceptionMapper.getExceptionType(e);
		if (ServerExceptionType.OPTIMISTIC_LOCKING_EXCEPTION.equals(exType)){
			for (int i=1; i<=maximumThreshold; i++) {
				try{
					logger.info("OPTIMISTIC LOCKING EXCEPTION encountered while updating contact id: " + annuity.getId()
							+".  Trying again to a max of: " + maximumThreshold + " times. This is the: " + (i +1) + " attempt.");
					String prefixUpdate = ""  +  System.currentTimeMillis();
					annuity.setConfiguration(getConfiguration());
					annuity = getServerAdapter().findAnnuityById(annuity);				
					updateAnnuity(annuity, prefixUpdate);
					success = true;
					break;
				}catch (Exception exp){
					continue;
				}
			}
			if (!success) {
				ExecutionUnitVerificationException exp = new ExecutionUnitVerificationException("Attempted to update annuity for: " + maximumThreshold
						+ " but was not successfuil");
				getExecutionUnitEvent().addException(exp);
				return;
			}
		}
	}
		
	private IAnnuity updateAnnuity(IAnnuity annuity, String prefix) throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
		String newAccountNumber = prefix + annuity.getAccountNumber();
		annuity.setConfiguration(getConfiguration());
		annuity.setAccountNumber(newAccountNumber);
		return getServerAdapter().updateAnnuity(annuity);
	}

	private void verifyFindSuccess(IAnnuity annuity) throws  ExecutionUnitVerificationException {		
		if (annuity == null) {
			throw new ExecutionUnitVerificationException(getDescription() + " Find Annuity Failed in.  Returned object is null");
		}
		if (annuity.getId() == null) {
			throw new ExecutionUnitVerificationException(getDescription() + " Find Annuity Failed. Returned object id == 0");
		}
	}
	private void setScenarioVariables() {		
		logger = getLogger(getClass().getName());
		
		try {
			maximumThreshold = getParameterValueInt(MAX_THRESHOLD);
		} catch(Exception e) {
			logger.fine("maximumThreshold parameter not specified, using default");
			maximumThreshold = 2;
		}
	}

}