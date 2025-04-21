package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class FindAnnuityByIdFromProvidedRangeEU extends AbastractAnnuityExecutionUnit {
	
	private static final long serialVersionUID = 5751982103363852382L;
	private static final String START_ID = "startId";
	private static final String END_ID = "endId";
	
	public void execute() {			
		try{			
			int startId = getParameterValueInt(START_ID);
			int endId = getParameterValueInt(END_ID);
			int id = (int)Math.round( (Math.random() * (endId-startId)) + startId); 		
			IAnnuity annuity = getAnnuityBeansFactory().createAnnuity();
			annuity.setId(""+id);
			annuity.setConfiguration(getConfiguration());
			IAnnuity results = getServerAdapter().findAnnuityById(annuity);	
			verifySuccess(results, ""+id);
		}catch(Exception e){
			getExecutionUnitEvent().addException(e);			
		}
		//fireToListeners();
				
	}
	
	private void verifySuccess(IAnnuity annuity, String id) throws ExecutionUnitVerificationException {
		if (annuity == null) {
			throw new ExecutionUnitVerificationException(
				"The FindAnnuityByIdEU failed to find an annuity.  retrned null value.");
		}
		if (!annuity.getId().equals(id)){
			throw new ExecutionUnitVerificationException(
				"The FindAnnuityByIdEU returned a different object with with different id!"
					+ "expected id: " + id + " but was: " + annuity.getId());
		}
		
	}
	

}
