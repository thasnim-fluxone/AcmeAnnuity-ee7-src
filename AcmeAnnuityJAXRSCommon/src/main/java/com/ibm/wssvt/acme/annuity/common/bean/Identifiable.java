package com.ibm.wssvt.acme.annuity.common.bean;

import java.io.Serializable;

import com.ibm.wssvt.acme.common.bean.Configrable;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public interface Identifiable<T> extends Configrable<String, String>, Serializable{
	public T getId();
	public void setId(T id);	
}
