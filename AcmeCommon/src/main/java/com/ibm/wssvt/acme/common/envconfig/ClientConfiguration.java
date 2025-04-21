package com.ibm.wssvt.acme.common.envconfig;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.bean.Parameterizable;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */

@XmlRootElement (name="ClientConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClientConfiguration", namespace = "http://common.acme.wssvt.ibm.com/envconfig/")
public class ClientConfiguration implements Configrable<String, String>{
	
	private static final long serialVersionUID = 7447084428569069597L;
	@XmlAttribute (name="id")
	private String id;
	@XmlAttribute (required=true)
	private long runTime;
	@XmlAttribute
	private int threadCount =1;
	@XmlAttribute
	private long threadExecDelay=0;	
		
	@XmlElement (type=EnvConfigStringParameterizable.class, name="configuration")
	private Parameterizable<String, String> configuration = new EnvConfigStringParameterizable();
	
	
	public long getThreadExecDelay() {
		return threadExecDelay;
	}
	public void setThreadExecDelay(long threadExecDelay) {
		this.threadExecDelay = threadExecDelay;
	}
	public long getRunTime() {
		return runTime;
	}
	public void setRunTime(long runTime) {
		this.runTime = runTime;
	}
	public int getThreadCount() {
		return threadCount;
	}
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId(){
		return this.id;
	}
	public Parameterizable<String, String> getConfiguration() {
		return this.configuration;
	}
	public void setConfiguration(Parameterizable<String, String> config) {
		this.configuration = config;
	}
	
	
}
