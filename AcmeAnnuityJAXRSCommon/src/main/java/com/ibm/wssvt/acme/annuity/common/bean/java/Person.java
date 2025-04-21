package com.ibm.wssvt.acme.annuity.common.bean.java;

import java.util.Date;

import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPerson;

public class Person extends AnnuityPersistebleObject implements IPerson {

	private static final long serialVersionUID = 6583119146735692154L;
	private String firstName;
	private String lastName;
	private String governmentId;
	private Date dateOfBirth;
	private Date timeOfBirth;
	private Byte[] picture;
	private IContact contact;

	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getGovernmentId() {
		return governmentId;
	}
	public void setGovernmentId(String governmentId) {
		this.governmentId = governmentId;
	}
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public Byte[] getPicture() {
		return picture;
	}
	public void setPicture(Byte[] picture) {
		this.picture = picture;
	}
	
	public Date getTimeOfBirth() {
		return timeOfBirth;
	}
	public void setTimeOfBirth(Date timeOfBirth) {
		this.timeOfBirth = timeOfBirth;
	}
	
	public IContact getContact() {
		return this.contact;
	}
	public void setContact(IContact contact) {
		this.contact = contact;
	}
	
}
