package com.ibm.wssvt.acme.annuity.common.client.executionunit;


import com.ibm.wssvt.acme.annuity.common.client.adapter.IAnnuityServerAdapter;

/**
 * $Rev: 773 $
 * $Date: 2007-09-13 11:32:04 -0500 (Thu, 13 Sep 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public abstract class AbastractAnnuityExecutionUnit extends AbstractBasicExecutionUnit {
	
	private static final long serialVersionUID = -1689423746552791070L;

	public IAnnuityServerAdapter getServerAdapter() {
		return (IAnnuityServerAdapter)super.getServerAdapter();
	}

	public void setServerAdapter(IAnnuityServerAdapter adapter) {
		super.setServerAdapter(adapter);
	}	

}