package com.ibm.wssvt.acme.annuity.common.bean.jpa;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPerson;
/**
 * $Rev: 726 $
 * $Date: 2007-08-28 16:14:57 -0500 (Tue, 28 Aug 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@AttributeOverride(name="lastUpdateDate", column=@Column(name="LAST_UPDATE_TS"))
public class Person extends AnnuityPersistebleObject implements IPerson {

	private static final long serialVersionUID = 6583119146735692154L;
	private String firstName;
	private String lastName;
	private String governmentId;
	private Date dateOfBirth;
	private Date timeOfBirth;
	private Byte[] picture;
	private IContact contact;
	
	
	@Column(name="DATE_OF_BIRTH")
	@Temporal(TemporalType.DATE)
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	@Column(name="FIRST_NAME")
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	@Column(name="GOVERNMENT_ID")
	public String getGovernmentId() {
		return governmentId;
	}
	public void setGovernmentId(String governmentId) {
		this.governmentId = governmentId;
	}
	@Column(name="LAST_NAME")
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	@Column(name="PICTURE")
	@Lob	
	public Byte[] getPicture() {
		return picture;
	}
	public void setPicture(Byte[] picture) {
		this.picture = picture;
	}
	@Column(name="TIME_OF_BIRTH")
	@Temporal(TemporalType.TIME)
	public Date getTimeOfBirth() {
		return timeOfBirth;
	}
	public void setTimeOfBirth(Date timeOfBirth) {
		this.timeOfBirth = timeOfBirth;
	}
	
	@OneToOne(
			cascade={CascadeType.REFRESH, CascadeType.MERGE}, 
			targetEntity=Contact.class)
	@JoinColumn(name="FK_CONTACT_ID")	
	public IContact getContact() {
		return this.contact;
	}
	public void setContact(IContact contact) {
		this.contact = contact;
	}
	
}
