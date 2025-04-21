package com.ibm.wssvt.acme.annuity.common.bean;

import java.util.Date;
/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public interface IPerson extends IAnnuityObject {

	public Date getDateOfBirth();

	public void setDateOfBirth(Date dateOfBirth);

	public String getFirstName();

	public void setFirstName(String firstName);

	public String getGovernmentId();

	public void setGovernmentId(String governmentId);

	public String getLastName();

	public void setLastName(String lastName);

	public Byte[] getPicture();

	public void setPicture(Byte[] picture);

	public Date getTimeOfBirth();

	public void setTimeOfBirth(Date timeOfBirth);	
	
	public IContact getContact();
	public void setContact(IContact contact);
	
}
