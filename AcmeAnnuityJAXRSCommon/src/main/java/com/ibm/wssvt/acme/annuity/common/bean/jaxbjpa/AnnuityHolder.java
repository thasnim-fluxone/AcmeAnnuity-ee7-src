package com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ibm.wssvt.acme.annuity.common.bean.AnnuityHolderCategory;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
/**
 * $Rev: 537 $
 * $Date: 2007-07-17 17:40:49 -0500 (Tue, 17 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnnuityHolder", namespace = "http://bean.common.annuity.acme.wssvt.ibm.com/jaxbjpa/")

@Entity
@PrimaryKeyJoinColumn(name="ID")
@Table(name="ANNUITY_HOLDER")
@AttributeOverride(name="lastUpdateDate", column=@Column(name="LAST_UPDATE_TS"))
public class AnnuityHolder extends Person implements IAnnuityHolder{
	private static final long serialVersionUID = 3307367871936336517L;	
	@XmlElement
	private AnnuityHolderCategory category;
	
	@Column(name="CATEGORY")
	@Enumerated(EnumType.ORDINAL)	
	public AnnuityHolderCategory getCategory() {
		return this.category;
	}
	
	public void setCategory(AnnuityHolderCategory category) {
		this.category = category;
	}


}
