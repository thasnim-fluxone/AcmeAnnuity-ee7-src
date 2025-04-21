package com.ibm.wssvt.acme.annuity.common.bean;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
//CDI Bean which will check that email and phone are not null. Used in the EntityListener ContactChangeListener
@Named 
@ApplicationScoped

public class ContactVerifier {
	
	private String email;
	private String phone;
	

	@NotNull
	public String getEmail()
	{ return email; }

	public void setEmail(String em)
	{ this.email = em; }
	
	@NotNull
	public String getPhone()
	{ return phone; }

	public void setPhone(String ph)
	{ this.phone = ph; }
	
}
