package com.ibm.wssvt.acme.annuity.common.client.executionunit.policy;

import java.rmi.RemoteException;

import com.ibm.wssvt.acme.annuity.common.bean.IBeneficiary;
import com.ibm.wssvt.acme.annuity.common.bean.jpa.JPABeansFactory;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.BasicExecutionUnitLibrarry;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.ExecutionUnitVerificationHelper;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;


public class CreateBeneficiaryEU extends AbastractPolicyExecutionUnit {

	private static final long serialVersionUID = -8814364386358328406L;
	private AcmeLogger logger;
	
	public void execute() {
		try{
			setScenarioVariables();
			
			IBeneficiary beneficiary = BasicExecutionUnitLibrarry.getBeneficiary(getAnnuityBeansFactory());
			beneficiary.setConfiguration(getConfiguration());
			logger.info("creating beneficiary");
			logger.info("Beneficiary details : id, name = " + beneficiary.getId()+","+beneficiary.getFirstName() +" " + beneficiary.getLastName());
			IBeneficiary result = getServerAdapter().createBeneficiary(beneficiary);
			logger.info("Beneficiary's name is: " + result.getFirstName() + " " + result.getLastName());
			verifyBeneficiaryValues(beneficiary);
		}catch (Exception e) {
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
		}
	}
	
	private void setScenarioVariables() {
		logger = getLogger(getClass().getName());
	}

	private void verifyBeneficiaryValues(IBeneficiary original)
		throws EntityNotFoundException, InvalidArgumentException, 
		ServerAdapterCommunicationException, ServerInternalErrorException, 
		ExecutionUnitVerificationException , RemoteException {
		IBeneficiary results = ((JPABeansFactory) getAnnuityBeansFactory()).createBeneficiary();
		results.setId(original.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findBeneficiaryById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, original, results, "Beneficiary from client is not equal to DB value", "Mismatch was found.");
	}
}
