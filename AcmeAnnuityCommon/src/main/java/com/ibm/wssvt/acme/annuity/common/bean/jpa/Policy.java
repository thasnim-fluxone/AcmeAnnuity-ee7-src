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
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ibm.wssvt.acme.annuity.common.bean.IFund;
import com.ibm.wssvt.acme.annuity.common.bean.IPolicy;

@NamedStoredProcedureQuery(
	    name = "GetPolicyByHolderId",
	    resultClasses = Policy.class,
	    procedureName = "EJB30.HOLDER_POLICY",
	    parameters = {
	        @StoredProcedureParameter(mode=ParameterMode.IN, name="holderId_1", type=String.class)
	  }
	)

@Entity
@Access(AccessType.PROPERTY)
@Table(name="INSPOLICY")
@AttributeOverride(name="lastUpdateDate", column=@Column(name="LAST_UPDATE_TS"))
public class Policy extends AnnuityPersistebleObject implements IPolicy {
	private static final long serialVersionUID = 4015672780551057807L;
	
	private String annuityHolderId;
	
	@Access(AccessType.FIELD)
	@ElementCollection(targetClass = Fund.class, fetch=FetchType.EAGER)
	@CollectionTable(name="POLICY_FUNDS",joinColumns={@JoinColumn(name="FK_POLICY_ID")})
	@MapKeyColumn(name = "FUND_ID")
	private Map<Integer,IFund> Funds = new HashMap <Integer, IFund>();

	
	@Column(name="FK_ANNUITY_HOLDER_ID")
	public String getAnnuityHolderId() {
		return this.annuityHolderId;
	}
	
	public void setAnnuityHolderId(String annuityHolderId) {
		this.annuityHolderId = annuityHolderId;
		
	}
	
	//Added for defect 146617
    @Transient
	public Map<Integer, IFund> getFunds() {
		return Funds;
	}
			
	public void setFunds(Map<Integer, IFund> funds) {
		this.Funds = funds;		
	}
}
