package com.ibm.wssvt.acme.annuity.common.bean;

import java.util.Date;
/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public interface IAnnuityObject extends Identifiable<String>, Versionable {	
	public void setLastUpdateDate(Date date);
	public Date getLastUpdateDate();
}
