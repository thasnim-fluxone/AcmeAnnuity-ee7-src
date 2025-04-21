package com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ibm.wssvt.acme.annuity.common.bean.IPayout;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PayoutValueObject", namespace = "http://bean.common.annuity.acme.wssvt.ibm.com/jaxbjpa/")
public class PayoutValueObject implements Serializable {		
	private static final long serialVersionUID = -5665063561765985026L;
	@XmlElement
	private Payout payout;	
	@XmlElement
	private AnnuityValueObject annuityVO;
			
	public void setPayout(IPayout payout) {
	
		if (payout == null){
			this.payout = null;
			this.annuityVO = null;
			return;
		}
		this.payout = (Payout)payout;				
		if (payout.getAnnuity() != null) {		
			this.annuityVO = new AnnuityValueObject();		
			this.annuityVO.setAnnuity((Annuity) this.payout.getAnnuity());
		}else{
			annuityVO = null;
		}
		//payout.setAnnuity(null);			
	}
	
	public IPayout getPayout(){	
		if (this.payout == null) return null;
		
		if (annuityVO == null) {
			this.payout.setAnnuity(null);
			return this.payout;
		}		
		this.payout.setAnnuity(annuityVO.getAnnuity());		
		return payout;		
	}
	
	
}
