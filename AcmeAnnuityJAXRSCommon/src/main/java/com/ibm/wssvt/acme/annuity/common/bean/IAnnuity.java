package com.ibm.wssvt.acme.annuity.common.bean;


import java.util.List;
/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public interface IAnnuity extends IAnnuityObject{
	
	public abstract Double getLastPaidAmt();

	public abstract void setLastPaidAmt(Double lastPaidAmt);
	
	public abstract String getAccountNumber();

	public abstract void setAccountNumber(String accountNumber);

	public abstract Double getAmount();

	public abstract void setAmount(Double amount);
	
	public abstract List<IPayout> getPayouts();
	public abstract void setPayouts(List<IPayout> payout);
	
	public abstract List<IRider> getRiders();
	public abstract void setRiders(List<IRider> riders);
	
	public abstract String getAnnuityHolderId();
	public abstract void setAnnuityHolderId(String annuityHolderId);
	
	public abstract List<IPayor> getPayors();
	public abstract void setPayors(List<IPayor> payors) ;	
	
	
	
}