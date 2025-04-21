package com.ibm.wssvt.acme.annuity.common.bean;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */


public interface IContact extends IAnnuityObject{
	
	public IAddress getAddress();
	
	public void setAddress(IAddress address);

	public String getEmail();

	public void setEmail(String email);

	public String getPhone();

	public void setPhone(String phone);
	
	public ContactType getContactType();
	public void setContactType(ContactType type);

}