package com.ibm.wssvt.acme.annuity.common.bean.jpa;

import com.ibm.wssvt.acme.annuity.common.bean.IAddress;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityBeansFactory;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneContact;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneficiary;
import com.ibm.wssvt.acme.annuity.common.bean.IConfigData;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IEquityAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IFixedAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IFund;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IPerson;
import com.ibm.wssvt.acme.annuity.common.bean.IPolicy;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.common.bean.Parameterizable;
import com.ibm.wssvt.acme.common.bean.StringParameterizable;

/**
 * $Rev: 694 $
 * $Date: 2007-08-22 16:47:52 -0500 (Wed, 22 Aug 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class JPABeansFactory implements IAnnuityBeansFactory {

	public IAddress createAddress() {
		return new Address();
	}

	public IEquityAnnuity createAnnEquity() {
		return new EquityAnnuity();
	}

	public IFixedAnnuity createAnnFixed() {
		return new FixedAnnuity();
	}

	public IAnnuity createAnnuity() {
		return new Annuity();
	}

	public IAnnuityHolder createAnnuityHolder() {
		return new AnnuityHolder();
	}

	public IPayout createPayout() {
		return new Payout();
	}

	public IRider createRider() {
		return new Rider();
	}

	public IContact createContact() {
		return new Contact();
	}

	
	public IPayor createPayor() {
		return new Payor();
	}

	public IPerson createPerson() {
		return new Person();
	}

	// New ConfigData object support added for @Singleton Mbean
	public IConfigData createConfigData() {
		return new ConfigData();
	}

	//New Policy Entities
	public IPolicy createPolicy() {
		return new Policy();
	}
	
	public IFund createFund() {
		return new Fund();
	}
	
	public IBeneficiary createBeneficiary() {
		return new Beneficiary();
	}
	
	public IBeneContact createBeneContact() {
		return new BeneContact();
	}
	
	public Parameterizable<String, String> createParameterizable() {
		return new StringParameterizable();
	}


}
