package com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityType;

//CDI Annotation ApplicationScoped added to ensure this class can be injected, since beans.xml has been removed
@ApplicationScoped
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnnuityValueObject", namespace = "http://bean.common.annuity.acme.wssvt.ibm.com/jaxbjpa/")
public class AnnuityValueObject implements Serializable {
	private static final long serialVersionUID = -4402726312235247013L;
	@XmlElement
	private AnnuityType annuityType;
	@XmlElement
	private Annuity basic;
	@XmlElement
	private FixedAnnuity fixed;
	@XmlElement
	private EquityAnnuity equity;
			
	public void setAnnuity(IAnnuity annuity) {
		if (annuity instanceof FixedAnnuity){
			this.annuityType = AnnuityType.FIXED;
			this.fixed = (FixedAnnuity)annuity;
		}else if (annuity instanceof EquityAnnuity){
			this.annuityType = AnnuityType.EQUITY;
			this.equity = (EquityAnnuity)annuity;
		}else {
			this.annuityType = AnnuityType.BASIC;
			this.basic= (Annuity) annuity;
		}
		if (annuity != null && annuity.getPayouts() != null){
			for (IPayout payout : annuity.getPayouts()) {
				payout.setAnnuity(null);
			}
		}
	}
	
	public IAnnuity getAnnuity(){
		IAnnuity result = null;
		if (annuityType == null ) {
			return null;			
		}
		if (AnnuityType.FIXED.equals(annuityType)){			
			result = this.fixed;
		}else if (AnnuityType.EQUITY.equals(annuityType)){
			result = this.equity; 
		}else {
			result = this.basic;
		}		
		for (IPayout payout : result.getPayouts()) {
			payout.setAnnuity(result);
		} 
		return result;
	}
	
	
}
