package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class DeleteAnnuityByProvidedIdEU extends AbastractAnnuityExecutionUnit {
	
	private static final long serialVersionUID = 5751982103363852382L;
	private static final String ID = "id";
	public void execute() {
		AcmeLogger logger = getLogger(getClass().getName());
		try{
			String id = getConfiguration().getParameterValue(ID);
			logger.fine("deleting id: "  + id);
			IAnnuity annuity = getAnnuityBeansFactory().createAnnuity();
			annuity.setId(id);
			annuity.setConfiguration(getConfiguration());
			IAnnuity results;
			try{
				results = getServerAdapter().findAnnuityById(annuity);
			}catch(EntityNotFoundException ex){
				throw new ExecutionUnitVerificationException("Delete Annuity Scenario failed.  " +
						"The annity with id: " + id + " does not exist!");
			}
			verifyFindSuccess(results, id);
			getServerAdapter().deleteAnnuityById(annuity);
			try{				
				annuity = getServerAdapter().findAnnuityById(annuity);
				if (annuity != null){
					throw new ExecutionUnitVerificationException("DeleteAnnuityByIdScenario indicated that the annuity " +
						"was deleted.  However susequent calls were able to find it - " +
						"If other threads created it, that would be OK - otherwise, this test has failed.  " +
						"Annuity id: " + id);
				}
			}catch(Exception ignore_me){
					
			}

		}catch(Exception e){			
			getExecutionUnitEvent().addException(e);			
		}
					
	}

	private void verifyFindSuccess(IAnnuity annuity, String id) throws ExecutionUnitVerificationException {
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
