package com.ibm.wssvt.acme.annuity.common.bean.jpa;

import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.bean.Parameterizable;
import com.ibm.wssvt.acme.common.bean.StringParameterizable;

@MappedSuperclass
public abstract class AbstractPersistebleObject implements Configrable<String, String> {
	private static final long serialVersionUID = -3613354849799979342L;
	private Date lastUpdateDate;
	private Parameterizable<String, String> config = new StringParameterizable();
	private int version;
	
	@Version
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	@Transient
	public Parameterizable<String, String> getConfiguration() {
		return this.config;
	}
	@Transient
	public void setConfiguration(Parameterizable<String, String> config) {
		this.config = config;
	}
	
	@SuppressWarnings("unused")
	@PrePersist
	@PreUpdate
	private void fixLastUpdateDate(){
		setLastUpdateDate(new Date());
	}
}
