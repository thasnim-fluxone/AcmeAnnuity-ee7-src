package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.ibm.wssvt.acme.annuity.common.bean.AnnuityHolderCategory;
import com.ibm.wssvt.acme.annuity.common.bean.ContactType;
import com.ibm.wssvt.acme.annuity.common.bean.IAddress;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityBeansFactory;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneContact;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneficiary;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IConfigData;
import com.ibm.wssvt.acme.annuity.common.bean.IEquityAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IFixedAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IFund;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IPerson;
import com.ibm.wssvt.acme.annuity.common.bean.IPolicy;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.bean.RiderType;
import com.ibm.wssvt.acme.annuity.common.bean.jpa.BeneContactId;
import com.ibm.wssvt.acme.annuity.common.bean.jpa.JPABeansFactory;
//import com.ibm.wssvt.acme.annuity.common.bean.jpa.Fund;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityType;
/**
 * $Rev: 481 $
 * $Date: 2007-07-04 17:42:05 -0500 (Wed, 04 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class BasicExecutionUnitLibrarry {
	
	
	public static IAnnuity getBasicAnnuity(IAnnuityBeansFactory beansFactory){
		IAnnuity annuity = beansFactory.createAnnuity();		
		annuity.setId(UUID.randomUUID().toString());
		annuity.setAccountNumber("123456");
		annuity.setLastPaidAmt(new Double(50.544));		
		annuity.setAmount(new Double(1000.156));
		annuity.setLastUpdateDate(new Date());
		return annuity;
	}	
	
	public static IFixedAnnuity getFixedAnnuity(IAnnuityBeansFactory beansFactory){
		IFixedAnnuity annFixed = beansFactory.createAnnFixed();
		IAnnuity basic = getBasicAnnuity(beansFactory);
		copyBasicAnnuityAttributes(basic, annFixed);
		//now add the additional fields
		annFixed.setRate(new Double(10.546));
			
		return annFixed;
		
	}	
	
	public static IEquityAnnuity getEquityAnnuity(IAnnuityBeansFactory beansFactory) {
		IEquityAnnuity annEqity = beansFactory.createAnnEquity();
		IAnnuity basic = getBasicAnnuity(beansFactory);
		copyBasicAnnuityAttributes(basic, annEqity);
		//now add the additional fields		
		annEqity.setFundNames("AFUND, BFUND");
		annEqity.setIndexRate(new Double(50.599));
			
		return annEqity;
	}
	
	private static void copyBasicAnnuityAttributes(IAnnuity fromAnnuity, IAnnuity toAnnuity) {
		toAnnuity.setId(fromAnnuity.getId());
		toAnnuity.setAccountNumber(fromAnnuity.getAccountNumber());		
		toAnnuity.setLastPaidAmt(fromAnnuity.getLastPaidAmt());
		toAnnuity.setAmount(fromAnnuity.getAmount());		
		toAnnuity.setLastUpdateDate(fromAnnuity.getLastUpdateDate());
	}
	
	public static IAnnuity getAnnuity(IAnnuityBeansFactory beansFactory, AnnuityType type){		
		if (AnnuityType.BASIC.equals(type)){
			return getBasicAnnuity(beansFactory);
		}	
		else if (AnnuityType.FIXED.equals(type)){
			return getFixedAnnuity(beansFactory);
		}		
		else if (AnnuityType.EQUITY.equals(type)){
			return getEquityAnnuity(beansFactory);
		}
		return null;
	}

	public static IPayout getPayout(IAnnuityBeansFactory beansFactory) {
		IPayout payout = beansFactory.createPayout();
		payout.setId(UUID.randomUUID().toString());
		payout.setStartDate(Calendar.getInstance());
		payout.setEndDate(Calendar.getInstance());
		payout.setTaxableAmount(new BigDecimal(10000.01));
		payout.setLastUpdateDate(new Date());
		return payout;
	}

	public static IRider getRider(IAnnuityBeansFactory beansFactory) {
		IRider rider = beansFactory.createRider();
		rider.setId(UUID.randomUUID().toString());
		rider.setEffectiveDate(new Date());
		rider.setLastUpdateDate(new Date());
		rider.setRule("The Rider rule");
		rider.setType(RiderType.OVERRIDE);
		return rider;
	}

	public static IPayor getPayor(IAnnuityBeansFactory beansFactory) {
		IPayor payor = beansFactory.createPayor();
		payor.setId(UUID.randomUUID().toString());
		payor.setName("Payor Name");
		payor.setLastUpdateDate(new Date());
		return payor;
	}

	public static IPerson getPerson(IAnnuityBeansFactory beansFactory) {
		IPerson person = beansFactory.createPerson();
		person.setId(UUID.randomUUID().toString());
		person.setDateOfBirth(new Date());
		person.setFirstName("First Name");
		person.setGovernmentId("111-22-3333");
		person.setLastName("Last Name");
		person.setLastUpdateDate(new Date());
		Byte[] b = new Byte[100];
		for (int i=0; i<100;i++){
			b[i]= (byte) i;
		}
		person.setPicture(b);
		person.setTimeOfBirth(new Date());		
		return person;
	}
	
	public static IAnnuityHolder getAnnuityHolder(IAnnuityBeansFactory beansFactory) {
		IAnnuityHolder  annuityHolder = beansFactory.createAnnuityHolder();
		annuityHolder.setId(UUID.randomUUID().toString());
		annuityHolder.setDateOfBirth(new Date());
		annuityHolder.setFirstName("First Name");
		annuityHolder.setGovernmentId("111-22-3333");
		annuityHolder.setLastName("Last Name");
		annuityHolder.setLastUpdateDate(new Date());
		Byte[] b = new Byte[100];
		for (int i=0; i<100;i++){
			b[i]= (byte) i;
		}
		annuityHolder.setPicture(b);
		annuityHolder.setTimeOfBirth(new Date());				
		annuityHolder.setCategory(AnnuityHolderCategory.GOLD);
		
		return annuityHolder;
	}

	public static IAddress getAddress(IAnnuityBeansFactory beansFactory) {
		IAddress address = beansFactory.createAddress();
		address.setLine1("123 Main Street");
		address.setLine2("APT # 123");
		address.setCity("Austin");
		address.setState("Texas");
		address.setZipCode("78758");
		address.setCountry("USA");
		return address;
	}
	public static IContact getContact(IAnnuityBeansFactory beansFactory) {
		IContact contact = beansFactory.createContact();
		contact.setId(UUID.randomUUID().toString());
		contact.setEmail("userName@domain.com");
		contact.setPhone("512-111-2222");
		contact.setLastUpdateDate(new Date());
		contact.setContactType(ContactType.HOME);
		IAddress address = getAddress(beansFactory);
		contact.setAddress(address);
		return contact;
	}

	// New ConfigData object support added for @Singleton Mbean
	public static IConfigData getConfigData(IAnnuityBeansFactory beansFactory) {
		IConfigData configData = beansFactory.createConfigData();
		configData.setId(UUID.randomUUID().toString());
		Map<String,String> newMap= new HashMap<String, String>();
		newMap.put("newKey", "newValue");
		configData.setConfigMap(newMap);
		configData.setLastUpdateDate(new Date());
		return configData;
	}
	
	public static IFund getFund(IAnnuityBeansFactory beansFactory) {
		IFund fund =  ((JPABeansFactory) beansFactory).createFund();
		fund.setFundName("IBM");
		fund.setIndexRate(new Double(5.3));
		IAddress address = getAddress(beansFactory);
		fund.setAddress(address);
		return fund;
	}
	
		
	public static IPolicy getPolicy(IAnnuityBeansFactory beansFactory) {
		IPolicy policy = ((JPABeansFactory) beansFactory).createPolicy();
		policy.setId(UUID.randomUUID().toString());
		IFund fund1 = getFund(beansFactory);
		IFund fund2 = getFund(beansFactory);		
		Map<Integer,IFund> funds = new HashMap <Integer, IFund>();
		funds.put(new Integer(1), fund1);
		funds.put(new Integer(2), fund2);
		policy.setFunds(funds);
		policy.setLastUpdateDate(new Date());
		return policy;
	}
	
	public static IBeneficiary getBeneficiary(IAnnuityBeansFactory beansFactory) {
		IBeneficiary beneficiary = ((JPABeansFactory) beansFactory).createBeneficiary();
		beneficiary.setId(UUID.randomUUID().toString());
		beneficiary.setFirstName("First Name");
		beneficiary.setLastName("Last Name");
		beneficiary.setRelationship("child");
		beneficiary.setLastUpdateDate(new Date());
		return beneficiary;
	}
	
	public static IBeneContact getBeneContact(IAnnuityBeansFactory beansFactory, String beneficiaryId) {
		IBeneContact beneContact = ((JPABeansFactory) beansFactory).createBeneContact();
		BeneContactId id = new BeneContactId();
		id.setBeneficiaryPK(beneficiaryId);
		id.setContactType(ContactType.HOME);
		beneContact.setId(id);
		beneContact.setEmail("userName@domain.com");
		beneContact.setPhone("512-111-2222");
		beneContact.setLastUpdateDate(new Date());
		return beneContact;
	}

	
}
