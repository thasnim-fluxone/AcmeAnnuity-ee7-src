package com.ibm.wssvt.acme.annuity.common.bean.jpa;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.ibm.wssvt.acme.annuity.common.bean.IBeneContact;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneficiary;

@Entity
@Table(name="BENEFICIARY")
@Access(AccessType.PROPERTY)
@AttributeOverride(name="lastUpdateDate", column=@Column(name="LAST_UPDATE_TS"))
public class Beneficiary extends AnnuityPersistebleObject implements IBeneficiary {

	private static final long serialVersionUID = -452903666159175508L;
	private String annuityHolderId;
	private String firstName;
	private String lastName;
	private String relationship;
	private List<IBeneContact> contacts;

	@Column(name="FK_ANNUITY_HOLDER_ID")
	public String getAnnuityHolderId() {
		return annuityHolderId;
	}
	public void setAnnuityHolderId(String annuityHolderId) {
		this.annuityHolderId = annuityHolderId;
	}

	@Column(name="FIRST_NAME")
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String first) {
		this.firstName = first;
	}

	@Column(name="LAST_NAME")
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String last) {
		this.lastName = last;
	}

	public String getRelationship() {
		return relationship;
	}
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	@OneToMany(targetEntity=BeneContact.class, mappedBy="beneficiary", fetch=FetchType.EAGER, orphanRemoval=true)
	public List<IBeneContact> getBeneContacts() {
		return contacts;
	}
	public void setBeneContacts(List<IBeneContact> contacts) {
		this.contacts = contacts;
	}
}
