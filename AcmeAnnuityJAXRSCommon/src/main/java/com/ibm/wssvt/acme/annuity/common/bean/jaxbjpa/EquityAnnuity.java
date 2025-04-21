package com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa;

import java.text.DecimalFormat;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ibm.wssvt.acme.annuity.common.bean.IEquityAnnuity;
/**
 * $Rev: 481 $
 * $Date: 2007-07-04 17:42:05 -0500 (Wed, 04 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EquityAnnuity", namespace = "http://bean.common.annuity.acme.wssvt.ibm.com/jaxbjpa/")
@Entity
@DiscriminatorValue(value = "EQUITY")
public class EquityAnnuity extends Annuity implements IEquityAnnuity {

	private static final long serialVersionUID = -7227462924769151013L;
	@XmlElement
	private String fundNames;
	@XmlElement
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
