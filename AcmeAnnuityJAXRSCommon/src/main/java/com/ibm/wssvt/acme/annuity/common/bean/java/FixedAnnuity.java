package com.ibm.wssvt.acme.annuity.common.bean.java;

import java.text.DecimalFormat;

import com.ibm.wssvt.acme.annuity.common.bean.IFixedAnnuity;

public class FixedAnnuity extends Annuity implements IFixedAnnuity {

	private static final long serialVersionUID = 1527245835840605452L;

	private Double rate;

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
		if (this.rate != null) {
			DecimalFormat df = new DecimalFormat("#.##");
			this.rate= new Double(df.format(rate));
		}
	}
}
