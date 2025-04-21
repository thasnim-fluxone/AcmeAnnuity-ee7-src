package com.ibm.wssvt.acme.annuity.common.bean;

import java.util.List;

public interface IBeneficiary extends IAnnuityObject {

	public String getAnnuityHolderId();
	public void setAnnuityHolderId(String annuityHolderId);
	
	public String getFirstName();
	public void setFirstName(String first);
	
	public String getLastName();
	public void setLastName(String last);
	
	public String getRelationship();
	public void setRelationship(String relationship);
	
	public List<IBeneContact> getBeneContacts();
	public void setBeneContacts(List<IBeneContact> contacts);
}
