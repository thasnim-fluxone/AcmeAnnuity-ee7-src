package com.ibm.wssvt.acme.annuity.common.bean.jpa;

import java.text.DecimalFormat;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.ibm.wssvt.acme.annuity.common.bean.IFixedAnnuity;
/**
 * $Rev: 481 $
 * $Date: 2007-07-04 17:42:05 -0500 (Wed, 04 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
@Entity
@DiscriminatorValue(value = "FIXED")
public class FixedAnnuity extends Annuity implements IFixedAnnuity {

	private static final long serialVersionUID = 1527245835840605452L;

	private Double rate;

	@Column(name="FIXED_RATE")
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
