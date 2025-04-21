package com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;

/**
 * $Rev: 538 $
 * $Date: 2007-07-17 17:41:32 -0500 (Tue, 17 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Payout", namespace = "http://bean.common.annuity.acme.wssvt.ibm.com/jaxbjpa/")

@Entity
@Access(AccessType.PROPERTY)
@AttributeOverride(name="lastUpdateDate", column=@Column(name="LAST_UPDATE_TS"))
public class Payout extends AnnuityPersistebleObject implements IPayout {	
	private static final long serialVersionUID = 2837981324963617180L;
	@XmlElement
	private BigDecimal taxableAmount;
	@XmlElement
	@Access(AccessType.FIELD)
	@Column(name="START_DATE")
	@Temporal(TemporalType.DATE)
	private Calendar startDate;
	@XmlElement
	private Calendar endDate;
	@XmlElement(type=Annuity.class)
	@Access(AccessType.FIELD)
	@ManyToOne(targetEntity=Annuity.class,
			fetch=FetchType.EAGER)
	@JoinColumn(name="FK_ANNUITY_ID")
	private IAnnuity annuity;
		
	@Column(name="TAXABLE_AMOUNT")
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

	@Transient
	public Calendar getStartDate() {
		return startDate;
	}
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}
	
	@Column(name="END_DATE")
	@Temporal(TemporalType.DATE)
	public Calendar getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Calendar payoutEndDate) {
		this.endDate = payoutEndDate;
	}
	
	@Transient
	public IAnnuity getAnnuity() {
		return this.annuity;
	}
	public void setAnnuity(IAnnuity annuity) {
		this.annuity = annuity;
		
	}

}
