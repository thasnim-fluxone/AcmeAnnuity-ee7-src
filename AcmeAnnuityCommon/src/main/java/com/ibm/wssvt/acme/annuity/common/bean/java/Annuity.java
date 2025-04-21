package com.ibm.wssvt.acme.annuity.common.bean.java;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;

public class Annuity extends AnnuityPersistebleObject implements IAnnuity {
	
	private static final long serialVersionUID = 7674976466413083140L;
	private Double lastPaidAmt;	
	private String AccountNumber;		
	private Double amount;
	private String annuityHolderId;
	private List<IPayout> payouts = new ArrayList<IPayout>();
	private List<IRider> riders = new ArrayList<IRider>();
	private List<IPayor> payors = new ArrayList<IPayor>();
	
	public Annuity(){
	}


	public Double getLastPaidAmt() {
		return lastPaidAmt;
	}
	public void setLastPaidAmt(Double lastPaidAmt) {
		this.lastPaidAmt = lastPaidAmt;
		if (this.lastPaidAmt != null) {
			DecimalFormat df = new DecimalFormat("#.##");
			this.lastPaidAmt= new Double(df.format(lastPaidAmt));
		}
	}
	

	public String getAccountNumber() {
		return AccountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		AccountNumber = accountNumber;
	}
	

	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
		if (this.amount != null) {
			DecimalFormat df = new DecimalFormat("#.##");
			this.amount = new Double(df.format(amount));
		}
	}


	public String getAnnuityHolderId() {
		return this.annuityHolderId;
	}
	public void setAnnuityHolderId(String annuityHolderId) {
		this.annuityHolderId = annuityHolderId;
		
	}
	
	public List<IPayor> getPayors() {
		return this.payors;
	}
	public void setPayors(List<IPayor> payors) {
		this.payors = payors;
		
	}
	
	public List<IPayout> getPayouts() {
		return this.payouts;
	}
	public void setPayouts(List<IPayout> payouts) {
		this.payouts = payouts;		
	}
	
	public List<IRider> getRiders() {
		return this.riders;
	}
	public void setRiders(List<IRider> riders) {
		this.riders = riders;
	}

	
	
	
}
