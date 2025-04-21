package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.exception.AnnuityException;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityType;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class CreateAnnuityWithProvidedIdEU extends AbastractAnnuityExecutionUnit {
	private static final long serialVersionUID = -3967925682867028969L;
	private static final String ANNUITY_TYPE = "annuityType";
	private static final String ID = "id";
	
	
	public void execute() {
		try{
			AcmeLogger logger = getLogger(getClass().getName());
			AnnuityType annuityType = super.getParameterValueEnum(AnnuityType.class, ANNUITY_TYPE);			
			IAnnuity annuity = createAnnuity(annuityType);			
			verifySuccess(annuity);	
			logger.fine("Annuity created ID="+ annuity.getId());
		}catch (Exception e) {			
			getExecutionUnitEvent().addException(e);			
		}		
	}
	
	private IAnnuity createAnnuity(AnnuityType annuityType) throws AnnuityException, ServerAdapterCommunicationException , RemoteException{
			IAnnuity annuity = BasicExecutionUnitLibrarry.getAnnuity(getAnnuityBeansFactory(), annuityType);		
			annuity.setConfiguration(getConfiguration());
			annuity.setId(getConfiguration().getParameterValue(ID));		
			getServerAdapter().createAnnuity(annuity);
			return annuity;
	}

	
	private void verifySuccess(IAnnuity annuity) throws ExecutionUnitVerificationException {
		if (annuity == null) {
			throw new ExecutionUnitVerificationException("Create Annuity Failed.  Returned object is null");
		}
		if (annuity.getId() == null) {
			throw new ExecutionUnitVerificationException("Create Annuity Failed. Returned object id == null");
		}
		//System.out.println("Ann Statrt Date " + annuity.getStartDate().getClass());	
		
	}
	

}
