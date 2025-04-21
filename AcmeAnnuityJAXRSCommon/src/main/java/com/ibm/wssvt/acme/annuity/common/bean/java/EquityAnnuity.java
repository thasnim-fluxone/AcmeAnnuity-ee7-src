package com.ibm.wssvt.acme.annuity.common.bean.java;

import java.text.DecimalFormat;

import com.ibm.wssvt.acme.annuity.common.bean.IEquityAnnuity;

public class EquityAnnuity extends Annuity implements IEquityAnnuity {

	private static final long serialVersionUID = -7227462924769151013L;

	private String fundNames;

	private Double indexRate;

	public String getFundNames() {
		return fundNames;
	}

	public void setFundNames(String fundNames) {
		this.fundNames = fundNames;
	}

	public Double getIndexRate() {
		return indexRate;
	}

	public void setIndexRate(Double indexRate) {
		this.indexRate = indexRate;
		if (this.indexRate != null) {
			DecimalFormat df = new DecimalFormat("#.##");
			this.indexRate= new Double(df.format(indexRate));
		}
	}

}
