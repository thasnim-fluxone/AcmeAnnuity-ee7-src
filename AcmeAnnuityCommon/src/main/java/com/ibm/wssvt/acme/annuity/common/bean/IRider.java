package com.ibm.wssvt.acme.annuity.common.bean;

import java.util.Date;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public interface IRider extends IAnnuityObject{
	public Date getEffectiveDate();
	public void setEffectiveDate(Date date);
	
	public String getRule();

	public void setRule(String rule);

	public RiderType getType();

	public void setType(RiderType type);
	
}