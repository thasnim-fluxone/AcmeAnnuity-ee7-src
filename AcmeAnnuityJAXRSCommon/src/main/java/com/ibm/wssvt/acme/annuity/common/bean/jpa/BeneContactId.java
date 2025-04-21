package com.ibm.wssvt.acme.annuity.common.bean.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.ibm.wssvt.acme.annuity.common.bean.ContactType;

@Embeddable
public class BeneContactId implements Serializable {
	private static final long serialVersionUID = -837443719842439462L;
    //@Enumerated(EnumType.STRING)
    //@Column(name="TYPE")
	ContactType type; //name and type matches PK of BeneContact

    //@Column(name="ID")
    String beneficiaryPK; //matches type of Beneficiary PK
	
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if((obj != null) && (obj instanceof BeneContactId)) {
			BeneContactId other = (BeneContactId) obj;
			if(this.type.equals(other.type) && this.beneficiaryPK.equals(other.beneficiaryPK))
				return true;
		}
		return false;
	}
	
	public int hashCode() {
		String hash = beneficiaryPK + Integer.toString(type.ordinal());
		return hash.hashCode();
	}
	
	public String toString() {
		return type.toString() + "-" + beneficiaryPK;
	}
	
    @Column(name="ID")
	public String getBeneficiaryPK() {
		return beneficiaryPK;
	}
	public void setBeneficiaryPK(String id) {
		beneficiaryPK = id;
	}
	
    @Enumerated(EnumType.STRING)
    @Column(name="TYPE")
	public ContactType getContactType() {
		return type;
	}
	public void setContactType(ContactType type) {
		this.type = type;
	}
}
