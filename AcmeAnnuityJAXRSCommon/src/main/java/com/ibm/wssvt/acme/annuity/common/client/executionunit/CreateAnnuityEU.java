package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.exception.AnnuityException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerBusinessLogicException;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityType;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class CreateAnnuityEU extends AbastractAnnuityExecutionUnit {
	private static final long serialVersionUID = -3967925682867028969L;
	private static final String ANNUITY_TYPE = "annuityType";
	public void execute() {
		try{
			AnnuityType annuityType = super.getParameterValueEnum(AnnuityType.class, ANNUITY_TYPE);			
			IAnnuity annuity = createAnnuity(annuityType);
			verifySuccess(annuity);								
		}catch (Exception e) {
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
		}		
	}
	
	private IAnnuity createAnnuity(AnnuityType annuityType) throws InterruptedException, ServerAdapterCommunicationException, ServerBusinessLogicException, AnnuityException , RemoteException{
		IAnnuity annuity = BasicExecutionUnitLibrarry.getAnnuity(getAnnuityBeansFactory(), annuityType);				
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().createAnnuity(annuity);
		return annuity;
	}

	
	private void verifySuccess(IAnnuity annuity) throws ExecutionUnitVerificationException {
		if (annuity == null) {
			throw new ExecutionUnitVerificationException("Create Annuity Failed.  Returned object is null");
		}
		if (annuity.getId() == null) {
			throw new ExecutionUnitVerificationException("Create Annuity Failed. Returned object id == null");
		}
	}
	

}
