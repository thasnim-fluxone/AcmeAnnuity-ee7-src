package com.ibm.wssvt.acme.annuity.common.business.ejb31;

import java.util.List;
import java.util.concurrent.Future;

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


public interface AnnuityMgmtAsynchSvcEJB31Local {
//	 search

	public Future<IAnnuity> findAnnuityById(IAnnuity annuity) 
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;		
	public Future<IAnnuityHolder> findHolderById(IAnnuityHolder annuityHolder)
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	public Future<IContact> findContactById(IContact contact)
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	
	public Future<List<IAnnuity>> findHolderAnnuities(IAnnuityHolder annuityHolder)
		throws ServerInternalErrorException, InvalidArgumentException;
	public Future<IAnnuityHolder> findAnnuityHolder(IAnnuity annuity) 	
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	public Future<List<IAnnuity>> findPayorAnnuities(IPayor payor)
		throws ServerInternalErrorException, InvalidArgumentException;
	
	public Future<IPayor> findPayorById(IPayor payor)
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	public Future<IPayout> findPayoutById(IPayout payout)
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	public Future<IRider> findRiderById(IRider rider)
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	
	// mgmt

	public Future<IAnnuity> createAnnuity(IAnnuity ann) 
		throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException;
	public Future<IAnnuity> updateAnnuity(IAnnuity ann) throws ServerInternalErrorException, InvalidArgumentException;
	public void deleteAnnuity(IAnnuity annuity);	
		
	public Future<IAnnuityHolder> createAnnuityHolder(IAnnuityHolder annHolder) 
		throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException;
	public Future<IAnnuityHolder> updateAnnuityHolder(IAnnuityHolder annHolder)
		throws ServerInternalErrorException, InvalidArgumentException;
	public void deleteAnnuityHolder(IAnnuityHolder annHolder);

	
	public Future<IPayor> createPayor(IPayor payor)
		throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException;
	public Future<IPayor> updatePayor(IPayor payor)
		throws ServerInternalErrorException, InvalidArgumentException;
	public void deletePayor(IPayor payor) ;


	public Future<IContact> createContact(IContact contact)
		throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException;
	public Future<IContact> updateContact(IContact contact)
		throws ServerInternalErrorException, InvalidArgumentException;
	public void deleteContact(IContact contact) ;
	
	public Future<IPayout> createPayout(IPayout payout)
		throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException;	
	public Future<IPayout> updatePayout(IPayout payout)
		throws ServerInternalErrorException, InvalidArgumentException;
	public void deletePayout(IPayout payout) ;
	
	public void deleteRider(IRider rider) ;		
}
