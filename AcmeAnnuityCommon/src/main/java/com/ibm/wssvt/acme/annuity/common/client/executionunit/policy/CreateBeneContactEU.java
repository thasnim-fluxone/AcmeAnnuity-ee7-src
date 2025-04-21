package com.ibm.wssvt.acme.annuity.common.client.executionunit.policy;

import java.util.UUID;

import com.ibm.wssvt.acme.annuity.common.bean.IBeneContact;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.BasicExecutionUnitLibrarry;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class CreateBeneContactEU extends AbastractPolicyExecutionUnit {

	private static final long serialVersionUID = -1407325400400042846L;
	private AcmeLogger logger;

	public void execute() {
		try{
			setScenarioVariables();
			
			IBeneContact beneContact = BasicExecutionUnitLibrarry.getBeneContact(getAnnuityBeansFactory(), UUID.randomUUID().toString());
			beneContact.setConfiguration(getConfiguration());
			logger.info("creating BeneContact");
			logger.info("BeneContact details : id, phone = " + beneContact.getId()+","+beneContact.getPhone());
			IBeneContact result = getServerAdapter().createBeneContact(beneContact);
			logger.info("BeneContact's e-mail is: " + result.getEmail());
			//verifyBeneContactValues(beneContact);
		}catch (Exception e) {
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
		}
	}
	
	private void setScenarioVariables() {
		logger = getLogger(getClass().getName());
	}

}
