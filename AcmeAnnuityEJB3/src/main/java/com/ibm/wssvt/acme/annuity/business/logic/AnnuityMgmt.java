/**
 * 
 */
package com.ibm.wssvt.acme.annuity.business.logic;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;

/**
 * $Rev: 483 $
 * $Date: 2007-07-06 03:22:50 -0500 (Fri, 06 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */

public interface AnnuityMgmt {
	
	public IAnnuity createAnnuity(IAnnuity ann) 
		throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException;
	public IAnnuity updateAnnuity(IAnnuity ann) throws ServerInternalErrorException, InvalidArgumentException;
	public void deleteAnnuity(IAnnuity annuity) throws ServerInternalErrorException, InvalidArgumentException;	
		
	public IAnnuityHolder createAnnuityHolder(IAnnuityHolder annHolder) 
		throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException;
	public IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annHolder)
		throws ServerInternalErrorException, InvalidArgumentException;
	public void deleteAnnuityHolder(IAnnuityHolder annHolder)
		throws ServerInternalErrorException, InvalidArgumentException;

	
	public IPayor createPayor(IPayor payor)
		throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException;
	public IPayor updatePayor(IPayor payor)
		throws ServerInternalErrorException, InvalidArgumentException;
	public void deletePayor(IPayor payor) 
		throws ServerInternalErrorException, InvalidArgumentException;


	public IContact createContact(IContact contact)
		throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException;
	public IContact updateContact(IContact contact)
		throws ServerInternalErrorException, InvalidArgumentException;
	public void deleteContact(IContact contact) 
		throws ServerInternalErrorException, InvalidArgumentException;
	
	public IPayout createPayout(IPayout payout)
		throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException;	
	public IPayout updatePayout(IPayout payout)
		throws ServerInternalErrorException, InvalidArgumentException;
	public void deletePayout(IPayout payout) 
		throws ServerInternalErrorException, InvalidArgumentException;
	
	public void deleteRider(IRider rider) 
		throws ServerInternalErrorException, InvalidArgumentException;
		
	
}
