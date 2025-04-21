package com.ibm.wssvt.acme.annuity.common.bean;

import java.util.Date;

import com.ibm.wssvt.acme.annuity.common.bean.jpa.BeneContactId;

public interface IBeneContact extends Identifiable<BeneContactId>, Versionable {

	public String getEmail();
	public void setEmail(String email);
	
	public String getPhone();
	public void setPhone(String phone);
	
	public void setLastUpdateDate(Date date);
	public Date getLastUpdateDate();
	
	public IBeneficiary getBeneficiary();
	public void setBeneficiary(IBeneficiary beneficiary);
}
