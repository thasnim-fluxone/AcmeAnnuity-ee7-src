//***********************************************************
// The ConfigData object used for testing 
// @Singleton Mbean support in EJB 3.1.
//
// Randy Erickson 07/02/2009
//***********************************************************

package com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ibm.wssvt.acme.annuity.common.bean.IConfigData;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConfigData", namespace = "http://bean.common.annuity.acme.wssvt.ibm.com/jaxbjpa/")

@Entity
@Access(AccessType.FIELD)
@Table(name="CONFIGDATA")
@AttributeOverride(name="lastUpdateDate", column=@Column(name="LAST_UPDATE_TS"))
public class ConfigData extends AnnuityPersistebleObject implements IConfigData {

    private static final long serialVersionUID = 1771167269635512044L;
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="CONFIGDATA_MAP",joinColumns={@JoinColumn(name="FK_CONFIGDATA_ID")})
    @MapKeyColumn(name = "CONFIGNAME")
    @Column (name="CONFIGVALUE", length=256)
  	@XmlElement (type= HashMap.class)
    private Map <String, String> configMap = new HashMap <String, String> ();

    public Map<String, String> getConfigMap() {
        return this.configMap;
    }

    public void setConfigMap(Map<String, String> cMap) {
        this.configMap= cMap;       
    }

}
