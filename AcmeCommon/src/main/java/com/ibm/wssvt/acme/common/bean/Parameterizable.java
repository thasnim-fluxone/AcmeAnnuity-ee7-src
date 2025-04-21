package com.ibm.wssvt.acme.common.bean;

import java.io.Serializable;
import java.util.Map;
/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public interface Parameterizable <K,V> extends Serializable{
	public void addParameter(K key, V value);
	public void removeParameter(K key);
	public void clearAllParameters();
	public Map<K, V> getParameters();
	public V getParameterValue(K key);
	public void addAllParams(Map<K,V> newParams);
}
