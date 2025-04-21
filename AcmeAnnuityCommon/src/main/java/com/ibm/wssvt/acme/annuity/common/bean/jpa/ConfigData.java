//***********************************************************
// The ConfigData object used for testing 
// @Singleton Mbean support in EJB 3.1.
//
// Randy Erickson 07/02/2009
//***********************************************************

package com.ibm.wssvt.acme.annuity.common.bean.jpa;

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

import com.ibm.wssvt.acme.annuity.common.bean.IConfigData;

@Entity
@Access(AccessType.FIELD) 
@Table(name="CONFIGDATA")
@AttributeOverride(name="lastUpdateDate", column=@Column(name="LAST_UPDATE_TS"))
public class ConfigData extends AnnuityPersistebleObject implements IConfigData {

    private static final long serialVersionUID = 9160772456178395978L;


    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="CONFIGDATA_MAP",joinColumns={@JoinColumn(name="FK_CONFIGDATA_ID")})
    @MapKeyColumn(name = "CONFIGNAME")
    @Column (name="CONFIGVALUE", length=256)
    private Map <String, String> configMap = new HashMap <String, String> ();

    public Map<String, String> getConfigMap() {
        return this.configMap;
    }

    public void setConfigMap(Map<String, String> cMap) {
        this.configMap= cMap;       
    }

}
