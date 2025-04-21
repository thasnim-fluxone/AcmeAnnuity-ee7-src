package com.ibm.wssvt.acme.annuity.common.client.executionunit.policy;


import com.ibm.wssvt.acme.annuity.common.client.adapter.policy.IPolicyServerAdapter;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.AbstractBasicExecutionUnit;


public abstract class AbastractPolicyExecutionUnit extends AbstractBasicExecutionUnit {
	
	private static final long serialVersionUID = -1689423746552791070L;

	public IPolicyServerAdapter getServerAdapter() {
		return (IPolicyServerAdapter)super.getServerAdapter();
	}

	public void setServerAdapter(IPolicyServerAdapter adapter) {
		super.setServerAdapter(adapter);
	}	

}