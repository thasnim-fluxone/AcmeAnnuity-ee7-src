package com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa;

import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.ibm.wssvt.acme.annuity.common.bean.jpa.JPAPersisteble;
import com.ibm.wssvt.acme.common.bean.Parameterizable;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnnuityPersistebleObject", namespace = "http://bean.common.annuity.acme.wssvt.ibm.com/jaxbjpa/")

@MappedSuperclass
public class AnnuityPersistebleObject implements JPAPersisteble {
	private static final long serialVersionUID = -1752164352355128830L;
	@XmlElement
	private String id;
	@XmlElement
	private Date lastUpdateDate;
	@XmlElement (type=StringParameterizable.class)
	@JsonDeserialize(as=StringParameterizable.class)  //added for defect 151630
	//@JsonDeserialize(as=StringParameterizable.class,keyAs=String.class)  //added for defect 160064
	private Parameterizable<String, String> config = new StringParameterizable();
	@XmlElement
	private int version;
	
	@Version
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@javax.persistence.Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	@Transient
	@JsonIgnore		//for 160064
	public Parameterizable<String, String> getConfiguration() {
		return this.config;
	}
	@Transient
	@JsonIgnore		//for 160064
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
