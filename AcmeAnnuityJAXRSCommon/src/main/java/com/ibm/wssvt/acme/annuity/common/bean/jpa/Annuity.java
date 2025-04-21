package com.ibm.wssvt.acme.annuity.common.bean.jpa;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;

/**
 * $Rev: 483 $
 * $Date: 2007-07-06 03:22:50 -0500 (Fri, 06 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */

@SuppressWarnings("serial")
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="DTYPE", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="ANNUITY")
@AttributeOverride(name="lastUpdateDate", column=@Column(name="LAST_UPDATE_TS"))
public class Annuity extends AnnuityPersistebleObject implements IAnnuity {
	
	private Double lastPaidAmt;	
	private String AccountNumber;		
	private Double amount;
	private String annuityHolderId;
	private List<IPayout> payouts = new ArrayList<IPayout>();
	private List<IRider> riders = new ArrayList<IRider>();
	private List<IPayor> payors = new ArrayList<IPayor>();
	
	public Annuity(){
	}

	@Column(name="LAST_PAID_AMT")
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
	
	@Column(name="ACCOUNT_NUMBER")
	public String getAccountNumber() {
		return AccountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		AccountNumber = accountNumber;
	}
	
	@Column(name="AMOUNT")
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

	@Column(name="FK_ANNUITY_HOLDER_ID")
	public String getAnnuityHolderId() {
		return this.annuityHolderId;
	}
	public void setAnnuityHolderId(String annuityHolderId) {
		this.annuityHolderId = annuityHolderId;
		
	}
	
	@ManyToMany(targetEntity=Payor.class, 
			fetch=FetchType.EAGER)			
	@JoinTable(name="ANNUITY_PAYOR", 
			joinColumns={@JoinColumn(name="FK_ANNUITY_ID")}, 
			inverseJoinColumns={@JoinColumn(name="FK_PAYOR_ID")})
	public List<IPayor> getPayors() {
		return this.payors;
	}
	public void setPayors(List<IPayor> payors) {
		this.payors = payors;
		
	}
	
	@OneToMany(targetEntity=Payout.class, 		
			mappedBy="annuity", 
			fetch=FetchType.EAGER)	
	public List<IPayout> getPayouts() {
		return this.payouts;
	}
	public void setPayouts(List<IPayout> payouts) {
		this.payouts = payouts;		
	}

	@OneToMany(cascade={CascadeType.ALL}, 
			targetEntity=Rider.class, 
			fetch=FetchType.EAGER)
	@JoinTable(name="ANNUITY_RIDER", 
			joinColumns={@JoinColumn(name="FK_ANNUITY_ID")}, 
			inverseJoinColumns={@JoinColumn(name="FK_RIDER_ID")})	
	public List<IRider> getRiders() {
		return this.riders;
	}
	public void setRiders(List<IRider> riders) {
		this.riders = riders;
	}

	
	
	
}
