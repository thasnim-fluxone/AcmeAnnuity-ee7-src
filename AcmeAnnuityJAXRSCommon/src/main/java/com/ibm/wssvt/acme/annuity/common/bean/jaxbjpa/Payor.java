package com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa;


import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
/**
 * $Rev: 436 $
 * $Date: 2007-06-26 17:51:49 -0500 (Tue, 26 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Payor", namespace = "http://bean.common.annuity.acme.wssvt.ibm.com/jaxbjpa/")
@Entity
@AttributeOverride(name="lastUpdateDate", column=@Column(name="LAST_UPDATE_TS"))
public class Payor extends AnnuityPersistebleObject implements IPayor{	
	private static final long serialVersionUID = 3462390122289537362L;
	@XmlElement
	private String name;	
	
	@Column(name="NAME")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
