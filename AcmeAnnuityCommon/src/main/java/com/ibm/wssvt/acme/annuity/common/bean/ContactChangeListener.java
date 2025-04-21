package com.ibm.wssvt.acme.annuity.common.bean;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PreUpdate;

//Rumana added for CDI and JPA21
@Named
public class ContactChangeListener {
	
	//CDI injection of ContactVerifier which will validate that the email and phone are not null
	@Inject ContactVerifier contactVerifier;
	 
	@PreUpdate
	    public void preUpdate(IContact cont) {
	        //Uncomment to verify that the Injected value is not null
			//System.out.println("contactVerify is " +contactVerifier) ; 
			contactVerifier.setEmail(cont.getEmail());
			contactVerifier.setPhone(cont.getPhone()); 
	}
	
}
