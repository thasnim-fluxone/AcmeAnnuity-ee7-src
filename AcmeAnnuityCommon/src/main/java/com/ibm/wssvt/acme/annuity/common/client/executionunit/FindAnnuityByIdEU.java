package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class FindAnnuityByIdEU extends AbastractAnnuityExecutionUnit {
	
	private static final long serialVersionUID = 5751982103363852382L;
	private static final String ID = "id";
	public void execute() {			
		try{			
			String id = getConfiguration().getParameterValue(ID);
			IAnnuity annuity = getAnnuityBeansFactory().createAnnuity();
			annuity.setId(id);
			annuity.setConfiguration(getConfiguration());
			IAnnuity results;
			try{
				results = getServerAdapter().findAnnuityById(annuity);				
			}catch (EntityNotFoundException e){
				throw new ExecutionUnitVerificationException(
					"The FindAnnuityByIdEU failed to find an annuity with provided id: " + id +" e: " +e);
			}
			verifySuccess(results);
		}catch(Exception e){
			getExecutionUnitEvent().addException(e);
		}
		//fireToListeners();
				
	}

	private void verifySuccess(IAnnuity annuity) throws ExecutionUnitVerificationException {
		if (annuity == null) {
			throw new ExecutionUnitVerificationException(
				"The FindAnnuityByIdEU failed to find an annuity.  retrned null value.");
		}
		if (!annuity.getId().equals(getConfiguration().getParameterValue(ID))){
			throw new ExecutionUnitVerificationException(
				"The FindAnnuityByIdEU returned a different object with with different id!"
					+ "expected id: " + getConfiguration().getParameterValue(ID) + " but was: " + annuity.getId());
		}
		
	}
	

}
