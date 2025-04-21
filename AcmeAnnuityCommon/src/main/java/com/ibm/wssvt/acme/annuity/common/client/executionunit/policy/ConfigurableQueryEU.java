package com.ibm.wssvt.acme.annuity.common.client.executionunit.policy;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IPersisteble;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.BasicExecutionUnitLibrarry;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.ExecutionUnitVerificationHelper;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class ConfigurableQueryEU extends AbastractPolicyExecutionUnit {

	private static final long serialVersionUID = 5255817825147387299L;
	
	private String useId = null;
	private int maxId = 0;
	private int startId = 0;
	private String rootClass = null;
	
	private static final String USE_ID = "useId";
	private static final String MAX_ID = "maxId";
	private static final String START_ID_KEY = "startId";
	private static final String ROOT_CLASS_KEY = "queryRoot";
	private AcmeLogger logger;

	/*
	 * This EU requires the queryRoot parameter to be set.  The values
	 * for queryRoot are: Policy (default), Beneficiary.
	 * 
	 * A criteria query will be performed regardless of the setting of
	 * queryType.
	 */
	public void execute() {
		
		String sHolderId = null;		
		IAnnuityHolder holder = null;
		
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
		while (holder == null && count < 3) {
			// using configured holder id or random?
			if(useId != null) 
				sHolderId = useId;
			else // generate a random holder id to lookup, 1 <-> maxId
				sHolderId = Integer.toString(getRandomInteger(startId, maxId));
			
			logger.fine("Looking up id: "  + sHolderId);
			
			try {
				holder = findAnnuityHolder(sHolderId);
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
		logger.fine("Successful find of Holder, id = " + holder.getId()+ " Call verify." );
		
		try {
			ExecutionUnitVerificationHelper.assertEquals(this, sHolderId, holder.getId(), 
					"Returned Annuity Holder id is not equal to requested id", "Mismatch was found.");
		} catch (Exception e) {
			logger.severe("Failed on verify find holder. Error: " +e);
			getExecutionUnitEvent().addException(e);
			return;	
		}
		
		List<IPersisteble<?>> results = null;
		try {
			logger.fine("Query for holder ID: " + holder.getId());

			holder.setConfiguration(getConfiguration());
			results = getServerAdapter().customQuery(holder);
			
			logger.fine("Finished query for holder ID: " + holder.getId());
		} catch (Exception e) {
			logger.severe("Error Holder ID: " + holder.getId()
					+ " failed finding results. Exception: "+ e);
			getExecutionUnitEvent().addException(e);
			return;
		}
		
		if (results != null) {
			
			logger.fine("HolderId: " + sHolderId + " has: " + results.size()
					+ " objects of root type");
			
			Iterator<IPersisteble<?>> it = results.iterator();
			
			while (it.hasNext()) {
				try {
					IPersisteble<?> persistable = it.next();
					
					logger.fine("Object ID: " + persistable.getId());
					ExecutionUnitVerificationHelper.assertIdContains(this, persistable.getId().toString(), sHolderId, "ERROR: requested object's id: "
							+ persistable.getId(),
							" does not contain holder id: " + sHolderId); 
				} catch (Exception e) {
					logger.severe("validating holder ID: " + sHolderId
							+ " Failed for policy: ");
					getExecutionUnitEvent().addException(e);
					return;
				}
			}
		}
		else {
			logger.warning("Holder ID: " + sHolderId + " has 0 objects of root type");
			Exception e = new EntityNotFoundException("Holder ID: " + sHolderId + " has no objects of root type");
			getExecutionUnitEvent().addException(e);
			return;
		}

	}
	
	private void setScenarioVariables() {		
		logger = getLogger(getClass().getName());
		
//		 retrieve the (optional) params from the config file
		try {
			rootClass = getConfiguration().getParameterValue(ROOT_CLASS_KEY);
			if(rootClass.toLowerCase().equals("beneficiary")) 
				rootClass = "Beneficiary";
			else if(rootClass.toLowerCase().equals("policy"))
				rootClass = "Policy";
			else {
				logger.warning("Unknown value for rootClass, defaulting to Policy");
				rootClass = "Policy";
			}
		} catch (Exception e) {
			logger.warning("rootClass parameter not in config file, defaulting to Policy");
			rootClass = "Policy";
		}
		
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
