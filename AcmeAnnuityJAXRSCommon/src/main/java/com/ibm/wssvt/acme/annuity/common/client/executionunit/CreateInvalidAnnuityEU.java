package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.util.logging.Level;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityType;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class CreateInvalidAnnuityEU extends AbastractAnnuityExecutionUnit {
	private static final long serialVersionUID = -3967925682867028969L;	
	public void execute() {
		try{			 				
			IAnnuity result = null;
			try{
				AnnuityType annuityType = getRandomEnum(AnnuityType.class);
				IAnnuity annuity = BasicExecutionUnitLibrarry.getAnnuity(getAnnuityBeansFactory(), annuityType);				
				annuity.setConfiguration(getConfiguration());
				// make it invalid
				annuity.setId(null);
				result  = getServerAdapter().createAnnuity(annuity);											
			}catch (ServerPersistenceModuleException e) {
				//good!  we expected this exception.
				getLogger(getClass().getName()).log(Level.FINE, e.getMessage(),e);
			}			
			if (result != null) {
				throw new ExecutionUnitVerificationException("Was able to create an annuity with a null id!");
			}
		}catch (Exception e) {
			e.printStackTrace();
			getExecutionUnitEvent().addException(e);
		}		
	}
	
}
