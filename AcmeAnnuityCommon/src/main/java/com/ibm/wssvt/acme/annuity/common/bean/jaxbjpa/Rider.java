package com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.bean.RiderType;
/**
 * $Rev: 512 $
 * $Date: 2007-07-12 15:06:02 -0500 (Thu, 12 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Rider", namespace = "http://bean.common.annuity.acme.wssvt.ibm.com/jaxbjpa/")

@Entity
@AttributeOverride(name="lastUpdateDate", column=@Column(name="LAST_UPDATE_TS"))
public class Rider extends AnnuityPersistebleObject implements IRider{
	private static final long serialVersionUID = 2088116709551706187L;
	@XmlElement
	private String rule;
	@XmlElement
	private Date effectiveDate;
	@XmlElement
	private RiderType type;
	
	@Column(name="EFFECTIVE_DATE")
	@Temporal(TemporalType.DATE)
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	
	@Enumerated(EnumType.STRING)
	public RiderType getType() {
		return type;
	}
	public void setType(RiderType type) {
		this.type = type;
	}
	

}
