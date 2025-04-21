package com.ibm.wssvt.acme.annuity.common.bean.java;

import java.util.Date;

import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.bean.RiderType;
/**
 * $Rev: 726 $
 * $Date: 2007-08-28 16:14:57 -0500 (Tue, 28 Aug 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */

public class Rider extends AnnuityPersistebleObject implements IRider{
	private static final long serialVersionUID = 2088116709551706187L;

	private String rule;
	private Date effectiveDate;
	private RiderType type;
	

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
	

	public RiderType getType() {
		return type;
	}
	public void setType(RiderType type) {
		this.type = type;
	}
	

}
