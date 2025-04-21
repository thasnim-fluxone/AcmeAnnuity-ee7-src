package com.ibm.wssvt.acme.annuity.common.client.executionunit.policy;

import java.rmi.RemoteException;
import java.util.Map;

import com.ibm.wssvt.acme.annuity.common.bean.IFund;
import com.ibm.wssvt.acme.annuity.common.bean.IPolicy;
import com.ibm.wssvt.acme.annuity.common.bean.jpa.JPABeansFactory;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.BasicExecutionUnitLibrarry;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.ExecutionUnitVerificationHelper;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class CreatePolicyEU extends AbastractPolicyExecutionUnit {
private static final long serialVersionUID = -3967925682867028969L;
	
	public void execute() {
		try{
			AcmeLogger logger = getLogger(getClass().getName());
			
			IPolicy original = BasicExecutionUnitLibrarry.getPolicy(getAnnuityBeansFactory());			
			original.setConfiguration(getConfiguration());
			logger.info("creating policy");

			IPolicy  result = getServerAdapter().createPolicy (original);

			//logger.info("Policy from DB's id  is " + result.getId());
			Map <Integer, IFund> fundMap = result.getFunds();			
			//logger.info("Policy's funds are " + fundMap);
			
			for(int i=1; i <= fundMap.size(); i++) {
				logger.info("Policy's fund name is: " + result.getFunds().get(new Integer(i)).getFundName());
			}
			verifyPolicyValues(original);
			
		}catch (Exception e) {
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
		}		
	}
	
	private void verifyPolicyValues(IPolicy policy) 
	throws EntityNotFoundException, InvalidArgumentException, 
		ServerAdapterCommunicationException, ServerInternalErrorException, 
		ExecutionUnitVerificationException , RemoteException{
		// read the policy with id.
		IPolicy results = ((JPABeansFactory) getAnnuityBeansFactory()).createPolicy();
		results.setId(policy.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findPolicyById(results);
		ExecutionUnitVerificationHelper.assertEqual(this,policy, results, 
			"policy from Client is not equal to DB value", "Mismacth was found.");		
	
}
	

}
