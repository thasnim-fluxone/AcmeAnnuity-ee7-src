package com.ibm.wssvt.acme.annuity.common.bean;

import java.io.Serializable;

public interface IFund extends Serializable {

	public String getFundName();

	public void setFundName(String fundName);

	public Double getIndexRate();

	public void setIndexRate(Double indexRate);
	
	public IAddress getAddress();

	public void setAddress(IAddress address);

}