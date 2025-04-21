package com.ibm.wssvt.acme.annuity.common.client.executionunit.policy;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IPolicy;
import com.ibm.wssvt.acme.annuity.common.bean.IFund;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.BasicExecutionUnitLibrarry;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.ExecutionUnitVerificationHelper;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class ReadPolicyFundsFromStateEU extends AbastractPolicyExecutionUnit {

	/**
	 * This execution unit requires that the Annuity (including Policy) database 
	 * be pre-populated.
	 * 1 - find Holder, based on entered id (useId) or random,
	 * 2 - find Policies attached to this Holder,
	 * 3 - Find the funds that the holder has which has address in a specific state
	 * 4 - Use the findPolicies - to get the list of funds - and get only the funds form the 
	 *     state specified
	 * 5 - Verified the list returned by both the queries is the same.
	 */
	private static final long serialVersionUID = -5232684206187027962L;

	/*
	 * useId and maxId are scenario params, useId is a specifc id to find,
	 * maxId is the upper limit of the Holder table in the preloaded data base.
	 */
	private String useId = null;	
	private int maxId = 0;
	private int startId = 0;
	private String stateName = "TX";
	
	private static final String USE_ID = "useId";
	private static final String MAX_ID = "maxId";
	private static final String START_ID_KEY = "startId";
	private static final String STATE_NAME = "stateName";
	

	AcmeLogger logger = null;
	
	public void execute() {
		String sHolderId = null;		
		IAnnuityHolder policyHolder = null;
		
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
		while (policyHolder == null && count < 3) {
			// using configured holder id or random?
			if(useId != null) 
				sHolderId = useId;
			else // generate a random holder id to lookup, 1 <-> maxId
				sHolderId = Integer.toString(getRandomInteger(startId, maxId));
			
			logger.fine("Looking up id: "  + sHolderId);
			
			try {
				policyHolder = findAnnuityHolder(sHolderId);
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
		logger.fine("Successful find of Holder, id = " + policyHolder.getId()+ " Call verify." );
		
		try {
			ExecutionUnitVerificationHelper.assertEquals(this, sHolderId, policyHolder.getId(), 
					"Returned Annuity Holder id is not equal to requested id", "Mismatch was found.");
		} catch (Exception e) {
			logger.severe("Failed on verify find holder. Error: " +e);
			getExecutionUnitEvent().addException(e);
			return;	
		}		
		
		//get the Funds for the holder which are in a particular state.
		List <IFund> stateFunds = null;
		if (stateName == null)
				stateName = "TX";
		
		try {
			logger.fine("Looking up Funds for holder ID: " + policyHolder.getId()+" in state: "+ stateName);

			policyHolder.setConfiguration(getConfiguration());
			stateFunds = getServerAdapter().findFundsFromState(policyHolder, stateName);
			logger.fine("Looked up Funds for holder ID: " + policyHolder.getId()+" in state: "+ stateName);
		} catch (Exception e) {
			logger.severe("Error Holder ID: " + policyHolder.getId()
					+ " failed finding funds for State: " + stateName +". Exception: "+ e);
			getExecutionUnitEvent().addException(e);
			return;
		}
		
		if (stateFunds != null) {
			Iterator<IFund> fundIt = stateFunds.iterator();
			logger.fine("The funds in State " + stateName + " are :");
			while (fundIt.hasNext()) {
				IFund fund = fundIt.next();
				logger.fine("Fund's name is "+ fund.getFundName());
				//logger.fine("Fund's name and index rate is "+ fund.getFundName()+": "+ fund.getIndexRate());
				}
			}
					
		// verify the list of funds
		logger.fine("Verify by querying policies for this holder");
		
		List<IFund> verifyList = new ArrayList<IFund>();
		List<IPolicy> policyListResults = null;
		try {
			logger.fine("Looking up policies for holder ID: " + policyHolder.getId());

			policyHolder.setConfiguration(getConfiguration());
			policyListResults = getServerAdapter().findHolderPolicies(policyHolder);
			
			//logger.fine("Looked up policies for holder ID: " + policyHolder.getId());
		} catch (Exception e) {
			logger.severe("Error Holder ID: " + policyHolder.getId()
					+ " failed finding policies. Exception: "+ e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		if (policyListResults != null) {
									
			Iterator<IPolicy> it = policyListResults.iterator();
			IFund fundInState = null;
			
			while (it.hasNext()) {
				
					IPolicy pol = it.next();
					for(int i=1; i <= pol.getFunds().size(); i++) {
						//fundInState = pol.getFunds().get(new Integer(i)).getAddress().getState();
						
						if (pol.getFunds().get(new Integer(i)).getAddress().getState().equalsIgnoreCase(stateName)) {
							fundInState = pol.getFunds().get(new Integer(i));
							logger.fine("Found fund with name : " + pol.getFunds().get(new Integer(i)).getFundName()+ " with address in " + stateName);
							verifyList.add(fundInState);
						}						
					}
					
				}
			if (verifyList.isEmpty()){
				logger.fine("There are no Funds which have address at State " + stateName);
				}	
			}				
		else {
			logger.warning("Holder ID: " + sHolderId + " has 0 policies");
			Exception e = new EntityNotFoundException("Holder ID: " + sHolderId + " has 0 policies");
			getExecutionUnitEvent().addException(e);
			return;
		}
		
		logger.fine("Comparing query results");
		try {
			ExecutionUnitVerificationHelper.assertEqual(this, stateFunds, verifyList, "Results of queries do not match!", "State and Policy queries returned different results.");
			for(int i=0; i<stateFunds.size(); i++) {
				ExecutionUnitVerificationHelper.assertEqual(this, stateFunds.get(i), verifyList.get(i), "Results of queries do not match!", "Result from state query (" + ") is not equal to result from policy query (" + ").");
			}
		} catch (Exception e) {
			logger.severe("Validation of fund lists from state failed for holder: " + sHolderId);
			getExecutionUnitEvent().addException(e);
		}
		logger.fine("Finished comparing results");

	} // end execute()
	

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
		try {
			stateName = getConfiguration().getParameterValue(STATE_NAME);
		} catch (Exception e) {
			// if parm not there just continue
			logger.warning("State parameter not in config file, using state TX");
			//stateName = "TX";
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
