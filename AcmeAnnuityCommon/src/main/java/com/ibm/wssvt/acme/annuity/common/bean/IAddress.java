package com.ibm.wssvt.acme.annuity.common.bean;

import java.io.Serializable;
/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public interface IAddress extends Serializable {

	public String getCity();

	public void setCity(String city);

	public String getCountry();

	public void setCountry(String country);

	public String getLine1();

	public void setLine1(String line1);

	public String getLine2();

	public void setLine2(String line2);

	public String getState();

	public void setState(String state);

	public String getZipCode();

	public void setZipCode(String zipCode);

}