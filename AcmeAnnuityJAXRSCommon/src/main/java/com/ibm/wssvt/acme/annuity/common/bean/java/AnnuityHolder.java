package com.ibm.wssvt.acme.annuity.common.bean.java;

import com.ibm.wssvt.acme.annuity.common.bean.AnnuityHolderCategory;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;

public class AnnuityHolder extends Person implements IAnnuityHolder{
	private static final long serialVersionUID = 3307367871936336517L;	
	private AnnuityHolderCategory category;
		
	public AnnuityHolderCategory getCategory() {
		return this.category;
	}
	
	public void setCategory(AnnuityHolderCategory category) {
		this.category = category;
	}


}
