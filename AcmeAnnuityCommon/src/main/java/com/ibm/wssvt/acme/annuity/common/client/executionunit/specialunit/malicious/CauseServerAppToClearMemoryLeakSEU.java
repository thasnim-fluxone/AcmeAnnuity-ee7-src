package com.ibm.wssvt.acme.annuity.common.client.executionunit.specialunit.malicious;

import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.AbastractAnnuityExecutionUnit;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.BasicExecutionUnitLibrarry;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class CauseServerAppToClearMemoryLeakSEU extends AbastractAnnuityExecutionUnit {

	private static final long serialVersionUID = 7285382382976016204L;
	public void execute() {
		try{
			IContact contact = BasicExecutionUnitLibrarry.getContact(getAnnuityBeansFactory());
			contact.setConfiguration(getConfiguration());
			contact.getConfiguration().addParameter("specialServiceName", "MemoryManagementService");			
			contact.getConfiguration().addParameter("memoryAction", "clear");			
			getServerAdapter().createContact(contact);				
		}catch (Exception e) {
			getExecutionUnitEvent().addException(e);			
		}		
	}
}
