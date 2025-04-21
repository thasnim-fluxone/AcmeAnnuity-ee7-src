package com.ibm.wssvt.acme.annuity.common.business.ejb30;

import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;


public interface AnnuityMgmtSvcEJB30  {
//	 search

	public IAnnuity findAnnuityById(IAnnuity annuity) 
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;		
	public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder)
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	public IContact findContactById(IContact contact)
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	
	public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder)
		throws ServerInternalErrorException, InvalidArgumentException;
	public IAnnuityHolder findAnnuityHolder(IAnnuity annuity) 	
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	public List<IAnnuity> findPayorAnnuities(IPayor payor)
		throws ServerInternalErrorException, InvalidArgumentException;
	
	public IPayor findPayorById(IPayor payor)
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	public IPayout findPayoutById(IPayout payout)
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	public IRider findRiderById(IRider rider)
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	
	// mgmt

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
