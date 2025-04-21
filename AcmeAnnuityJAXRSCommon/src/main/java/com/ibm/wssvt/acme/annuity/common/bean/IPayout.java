package com.ibm.wssvt.acme.annuity.common.bean;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public interface IPayout extends IAnnuityObject{

	public void setTaxableAmount(BigDecimal taxableAmount);
	public BigDecimal getTaxableAmount();
	public void setStartDate(Calendar startDate);
	public Calendar getStartDate();
	public void setEndDate(Calendar payoutEndDate);
	public Calendar getEndDate();
	public IAnnuity getAnnuity();
	public void setAnnuity(IAnnuity annuity);	
	

}