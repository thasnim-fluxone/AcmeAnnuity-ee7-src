package com.ibm.wssvt.acme.annuity.common.bean;

import com.ibm.wssvt.acme.common.bean.IBeansFactory;

/**
 * $Rev: 694 $
 * $Date: 2007-08-22 16:47:52 -0500 (Wed, 22 Aug 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public interface IAnnuityBeansFactory extends IBeansFactory{

	public IAddress createAddress();
	public IAnnuity createAnnuity();
	public IFixedAnnuity createAnnFixed();	
	public IEquityAnnuity createAnnEquity();
	public IAnnuityHolder createAnnuityHolder();
	public IPayout createPayout();
	public IRider createRider();
	public IContact createContact();	
	public IPayor createPayor();
	public IPerson createPerson();	
	public IConfigData createConfigData();
}
