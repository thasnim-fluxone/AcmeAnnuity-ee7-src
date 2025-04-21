package com.ibm.wssvt.acme.annuity.common.bean.jpa;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Transient;

import com.ibm.wssvt.acme.annuity.common.bean.IAddress;
import com.ibm.wssvt.acme.annuity.common.bean.IFund;



@Access(AccessType.PROPERTY)
@Embeddable
public class Fund implements IFund {	
	private static final long serialVersionUID = -2017682230659955349L;
	@Transient
	private String fundName;
	
	@Transient
	private Double indexRate;		
	
	@Transient
	private Address theAddress;
	
	@Column(name="FUND_NAME")
	public String getFundName(){
		return fundName;
	}

	public void setFundName(String fundName){
		this.fundName = fundName;
	}
	
	@Column(name="INDEX_RATE")
	public Double getIndexRate(){
		return indexRate;
	}

	public void setIndexRate(Double indexRate){
		this.indexRate = indexRate;
	}
	
	@Transient
	public IAddress getAddress() {
		return (IAddress) this.getTheAddress();
	}
	public void setAddress(IAddress address) {
		if (address instanceof Address){
			this.setTheAddress((Address)address);			
		}else if(address == null) {
			this.setTheAddress(null);			
		}
		else{
			throw new ClassCastException("Invalid Implementaion of IAddress.  " +
					"Class must be instance of com.ibm.wssvt.acme.annuity.common.bean.jpa.Address");
		}
	}

	@Embedded
	protected  Address getTheAddress() {
		return  theAddress;
	}
	protected  void setTheAddress(Address address) {
		this.theAddress = address;
	}
	
}