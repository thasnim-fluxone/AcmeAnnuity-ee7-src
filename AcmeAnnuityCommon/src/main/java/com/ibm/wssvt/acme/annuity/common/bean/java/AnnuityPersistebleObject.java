package com.ibm.wssvt.acme.annuity.common.bean.java;

import java.util.Date;

import com.ibm.wssvt.acme.annuity.common.bean.IPersisteble;
import com.ibm.wssvt.acme.annuity.common.bean.Identifiable;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.bean.Parameterizable;
import com.ibm.wssvt.acme.common.bean.StringParameterizable;

public class AnnuityPersistebleObject implements  IPersisteble<String>, Identifiable<String>, Configrable<String, String> {
	private static final long serialVersionUID = -1752164352355128830L;
	private String id;
	private Date lastUpdateDate;
	private Parameterizable<String, String> config = new StringParameterizable();
	private int version;
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
		
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public Parameterizable<String, String> getConfiguration() {
		return this.config;
	}

	public void setConfiguration(Parameterizable<String, String> config) {
		this.config = config;
	}
		
}
