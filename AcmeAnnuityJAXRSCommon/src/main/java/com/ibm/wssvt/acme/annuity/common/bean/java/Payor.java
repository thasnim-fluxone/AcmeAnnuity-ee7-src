package com.ibm.wssvt.acme.annuity.common.bean.java;


import com.ibm.wssvt.acme.annuity.common.bean.IPayor;

public class Payor extends AnnuityPersistebleObject implements IPayor{	
	private static final long serialVersionUID = 3462390122289537362L;
	private String name;	
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
