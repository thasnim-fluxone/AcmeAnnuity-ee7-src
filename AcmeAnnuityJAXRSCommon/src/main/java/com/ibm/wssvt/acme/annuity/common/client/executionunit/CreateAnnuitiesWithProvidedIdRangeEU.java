package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityType;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.executionunit.ISingleRunExecutionUnit;
import com.ibm.wssvt.acme.common.executionunit.ISingleThreadedExecutionUnit;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
/**
 * $Rev: 476 $
 * $Date: 2007-07-02 17:58:29 -0500 (Mon, 02 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class CreateAnnuitiesWithProvidedIdRangeEU extends
		AbastractAnnuityExecutionUnit implements ISingleThreadedExecutionUnit, ISingleRunExecutionUnit {

	private static final long serialVersionUID = 5751982103363852382L;
	private static final String START_ID = "startId";
	private static final String END_ID = "endId";
	private static final String ANNUITY_TYPE = "annuityType";
	private static AnnuityType annuityType;

	public void execute() {
		try {
			AcmeLogger logger = getLogger(getClass().getName());
			int startId = getParameterValueInt(START_ID);
			int endId = getParameterValueInt(END_ID);
			annuityType = (AnnuityType) getParameterValueEnum(AnnuityType.class, ANNUITY_TYPE);				
			for (int i = startId; i < endId; i++) {
				IAnnuity annuity = BasicExecutionUnitLibrarry.getAnnuity(getAnnuityBeansFactory(), annuityType);
				annuity.setId(""+i);
				annuity.setConfiguration(getConfiguration());
				logger.fine("Create Annuity ID="+annuity.getId());
				IAnnuity results = getServerAdapter().createAnnuity(annuity);
				verifySuccess(results, ""+i);
			}			
		} catch (Exception e) {
			getExecutionUnitEvent().addException(e);			
		}
	
	}

	private void verifySuccess(IAnnuity annuity, String id)
			throws ExecutionUnitVerificationException {
		ExecutionUnitVerificationHelper.assertValidId(this, annuity, "Failed to find Annuity", null);
		ExecutionUnitVerificationHelper.assertEquals(this, annuity.getId(), id, "Failed to find Annuity", null);
	}

}
