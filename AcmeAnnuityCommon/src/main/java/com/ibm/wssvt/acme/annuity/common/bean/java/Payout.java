package com.ibm.wssvt.acme.annuity.common.bean.java;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;

public class Payout extends AnnuityPersistebleObject implements IPayout {	
	private static final long serialVersionUID = 2837981324963617180L;
	private BigDecimal taxableAmount;
	private Calendar startDate;
	private Calendar endDate;
	private IAnnuity annuity;
		

	public BigDecimal getTaxableAmount() {		
		return this.taxableAmount;
	}
	public void setTaxableAmount(BigDecimal payoutTaxableAmt) {		
		this.taxableAmount = payoutTaxableAmt;
		if (payoutTaxableAmt != null) {
			DecimalFormat df = new DecimalFormat("#.##");
			this.taxableAmount = new BigDecimal(df.format(payoutTaxableAmt)); 				
		}
		
	}

	public Calendar getStartDate() {
		return startDate;
	}
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}
	
	public Calendar getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Calendar payoutEndDate) {
		this.endDate = payoutEndDate;
	}

	public IAnnuity getAnnuity() {
		return this.annuity;
	}
	public void setAnnuity(IAnnuity annuity) {
		this.annuity = annuity;
		
	}

}
