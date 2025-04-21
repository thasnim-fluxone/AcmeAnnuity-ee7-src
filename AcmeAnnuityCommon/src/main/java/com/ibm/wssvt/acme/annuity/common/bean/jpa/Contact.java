package com.ibm.wssvt.acme.annuity.common.bean.jpa;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
//import javax.persistence.EnumType;
//import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Convert;

import com.ibm.wssvt.acme.annuity.common.bean.ContactChangeListener;
import com.ibm.wssvt.acme.annuity.common.bean.ContactType;
import com.ibm.wssvt.acme.annuity.common.bean.IAddress;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.ContactTypeConverter;
/**
 * $Rev: 491 $
 * $Date: 2007-07-09 18:24:12 -0500 (Mon, 09 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
@Entity
@Table(name="CONTACT")
@AttributeOverride(name="lastUpdateDate", column=@Column(name="LAST_UPDATE_TS"))
@Access(AccessType.FIELD)
@EntityListeners({ContactChangeListener.class})
public class Contact extends AnnuityPersistebleObject implements IContact {
	private static final long serialVersionUID = 4015672780551057807L;
	@Embedded
	private Address theAddress;
	@Column(name="EMAIL")
	private String email;
	@Column(name="PHONE")
	private String phone;
	@Transient
	private ContactType contactType;
	
	public IAddress getAddress() {
		return (IAddress) this.getTheAddress();
	}
	public void setAddress(IAddress address) {
		if (address instanceof Address){
			this.setTheAddress((Address)address);
		}else if(address == null) {
			this.setTheAddress(null);
		}
		else{
			throw new ClassCastException("Invalid Implementaion of IAddress.  " +
					"Class must be instance of com.ibm.wssvt.acme.annuity.common.bean.jpa.Address");
		}
	}

	private  Address getTheAddress() {
		return theAddress;
	}
	private  void setTheAddress(Address address) {
		this.theAddress = address;
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
	@Column(name="TYPE")
	//@Enumerated(EnumType.STRING)
	@Access(AccessType.PROPERTY)
	@Convert(converter = ContactTypeConverter.class)
	public ContactType getContactType() {
		return contactType;
	}
	public void setContactType(ContactType contactType) {
		this.contactType = contactType;
	}
	
}
