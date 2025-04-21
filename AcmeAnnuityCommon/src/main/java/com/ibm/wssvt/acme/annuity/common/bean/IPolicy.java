package com.ibm.wssvt.acme.annuity.common.bean;

import java.util.Map;


public interface IPolicy extends IAnnuityObject {

	public abstract String getAnnuityHolderId();
	public abstract void setAnnuityHolderId(String annuityHolderId);	
	
	public abstract Map<Integer, IFund> getFunds();
	public abstract void setFunds(Map<Integer, IFund> funds);
	
}
