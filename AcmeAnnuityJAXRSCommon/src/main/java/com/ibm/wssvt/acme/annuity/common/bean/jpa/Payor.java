package com.ibm.wssvt.acme.annuity.common.bean.jpa;


import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
/**
 * $Rev: 436 $
 * $Date: 2007-06-26 17:51:49 -0500 (Tue, 26 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
@Entity
@AttributeOverride(name="lastUpdateDate", column=@Column(name="LAST_UPDATE_TS"))
public class Payor extends AnnuityPersistebleObject implements IPayor{	
	private static final long serialVersionUID = 3462390122289537362L;
	private String name;	
	
	@Column(name="NAME")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
