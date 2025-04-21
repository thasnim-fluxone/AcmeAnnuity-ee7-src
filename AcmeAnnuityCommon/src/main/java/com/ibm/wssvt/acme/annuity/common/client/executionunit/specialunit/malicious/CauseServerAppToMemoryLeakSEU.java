package com.ibm.wssvt.acme.annuity.common.client.executionunit.specialunit.malicious;

import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.AbastractAnnuityExecutionUnit;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.BasicExecutionUnitLibrarry;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class CauseServerAppToMemoryLeakSEU extends AbastractAnnuityExecutionUnit {

	private static final long serialVersionUID = 7285382382976016204L;
	public void execute() {
		try{
			IContact contact = BasicExecutionUnitLibrarry.getContact(getAnnuityBeansFactory());
			contact.setConfiguration(getConfiguration());
			contact.getConfiguration().addParameter("specialServiceName", "MemoryManagementService");			
			setSEUParams();			
			getServerAdapter().createContact(contact);				
		}catch (Exception e) {
			getExecutionUnitEvent().addException(e);			
		}		
	}
	private void setSEUParams(){
		String memoryAction = getConfiguration().getParameterValue("memoryAction");	
		if (memoryAction != null && memoryAction.trim().length() >0) {
			if ("increase".equalsIgnoreCase(memoryAction) || "decrease".equalsIgnoreCase(memoryAction)){
				// valid, leave it as is
			}else{
				getConfiguration().addParameter("memoryAction", "increase");				
			}
		}else{
			getConfiguration().addParameter("memoryAction", "increase");
		}		
		
		try {
			getParameterValueInt("sizeDelta");
			// valid, leave it as is
		} catch (InvalidExecutionUnitParameterException e) {			
			getConfiguration().addParameter("sizeDelta", "10000");  //100
		}		
			
		
	}
}
