package com.ibm.wssvt.acme.annuity.common.bean.jpa;

//import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.ibm.wssvt.acme.annuity.common.bean.AnnuityHolderCategory;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
/**
 * $Rev: 726 $
 * $Date: 2007-08-28 16:14:57 -0500 (Tue, 28 Aug 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
@Entity
@PrimaryKeyJoinColumn(name="ID")
@Table(name="ANNUITY_HOLDER")
//Commented out due to RTC defect 140662
//@AttributeOverride(name="lastUpdateDate", column=@Column(name="LAST_UPDATE_TS"))

public class AnnuityHolder extends Person implements IAnnuityHolder{
	private static final long serialVersionUID = 3307367871936336517L;	
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
