package com.ibm.wssvt.acme.annuity.common.bean.jpa;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import com.ibm.wssvt.acme.annuity.common.bean.IBeneContact;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneficiary;
import com.ibm.wssvt.acme.annuity.common.bean.IPersisteble;

@Entity
@Table(name="BENE_CONTACT")
@Access(AccessType.PROPERTY)
@AttributeOverride(name="lastUpdateDate", column=@Column(name="LAST_UPDATE_TS"))
public class BeneContact extends AbstractPersistebleObject implements IBeneContact, IPersisteble<BeneContactId> {

	private static final long serialVersionUID = 4571838649566012594L;
	private BeneContactId id;
	@Access(AccessType.FIELD)
	@ManyToOne(targetEntity=Beneficiary.class, fetch=FetchType.EAGER)
	@JoinColumn(name="ID")
    @MapsId("beneficiaryPK")
	private Beneficiary beneficiary;
	private String email;
	private String phone;

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
	
	@EmbeddedId
	public BeneContactId getId() {
		return id;
	}
	public void setId(BeneContactId id) {
		this.id = id;
	}

	public IBeneficiary getBeneficiary() {
		return beneficiary;
	}
	public void setBeneficiary(IBeneficiary beneficiary) {
		this.beneficiary = (Beneficiary) beneficiary;
	}
}
