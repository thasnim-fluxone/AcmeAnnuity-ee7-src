package com.ibm.wssvt.acme.annuity.common.bean.java;

import com.ibm.wssvt.acme.annuity.common.bean.ContactType;
import com.ibm.wssvt.acme.annuity.common.bean.IAddress;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;

public class Contact extends AnnuityPersistebleObject implements IContact {
	private static final long serialVersionUID = 4015672780551057807L;
	private Address address;
	private String email;
	private String phone;
	private ContactType contactType;
	
	public IAddress getAddress() {
		return this.address;
	}
	public void setAddress(IAddress address) {
		if (address instanceof Address){
			this.address = (Address)address;
		}else if(address == null) {
			this.address = null;
		}
		else{
			throw new ClassCastException("Invalid Implementaion of IAddress.  " +
					"Class must be instance of " + Address.class.getName());
		}
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public ContactType getContactType() {
		return contactType;
	}
	public void setContactType(ContactType contactType) {
		this.contactType = contactType;
	}
	
}
