package com.ibm.wssvt.acme.annuity.common.bean.jpa;

import java.text.DecimalFormat;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.ibm.wssvt.acme.annuity.common.bean.IEquityAnnuity;
/**
 * $Rev: 481 $
 * $Date: 2007-07-04 17:42:05 -0500 (Wed, 04 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
@Entity
@DiscriminatorValue(value = "EQUITY")
public class EquityAnnuity extends Annuity implements IEquityAnnuity {

	private static final long serialVersionUID = -7227462924769151013L;

	private String fundNames;

	private Double indexRate;

	@Column(name="FUND_NAMES")
	public String getFundNames() {
		return fundNames;
	}

	public void setFundNames(String fundNames) {
		this.fundNames = fundNames;
	}

	@Column(name="INDEX_RATE")
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
