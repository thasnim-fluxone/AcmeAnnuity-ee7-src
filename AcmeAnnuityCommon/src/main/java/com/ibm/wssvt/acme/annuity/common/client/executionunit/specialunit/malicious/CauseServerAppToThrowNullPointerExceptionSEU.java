package com.ibm.wssvt.acme.annuity.common.client.executionunit.specialunit.malicious;

import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.AbastractAnnuityExecutionUnit;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.BasicExecutionUnitLibrarry;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class CauseServerAppToThrowNullPointerExceptionSEU extends AbastractAnnuityExecutionUnit {

	private static final long serialVersionUID = 7285382382976016204L;

	public void execute() {
		try{
			IContact contact = BasicExecutionUnitLibrarry.getContact(getAnnuityBeansFactory());
			contact.setConfiguration(getConfiguration());
			contact.getConfiguration().addParameter("specialServiceName", "ExceptionReturnService");
			contact.getConfiguration().addParameter("exceptionType", "nullPointerException");
			getServerAdapter().createContact(contact);				
		}catch (Exception e) {
			if (e.getMessage().contains("NullPointerException was thrown by design from class")){
				
			}else{
				ExecutionUnitVerificationException ex = new ExecutionUnitVerificationException("failed to verify that an NPE was thrown as requested. " +
						"See exception for error", e );
				getExecutionUnitEvent().addException(ex);
			}
		}		
	}
	
}
