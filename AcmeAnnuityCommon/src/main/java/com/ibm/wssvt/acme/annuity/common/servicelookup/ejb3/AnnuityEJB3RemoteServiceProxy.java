package com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3;

import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.AnnuityMgmtSvcEJB30;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;

public class AnnuityEJB3RemoteServiceProxy implements IAnnuityService{
	AnnuityMgmtSvcEJB30 remote;		
	
	public AnnuityEJB3RemoteServiceProxy(AnnuityMgmtSvcEJB30 remote) {		
		this.remote = remote;
	}
		
	public IAnnuity findAnnuityById(IAnnuity annuity) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		return remote.findAnnuityById(annuity);
	}
	public IAnnuityHolder findAnnuityHolder(IAnnuity annuity) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		return remote.findAnnuityHolder(annuity);
	}
	public IContact findContactById(IContact contact) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		return remote.findContactById(contact);
	}
	public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder) throws ServerInternalErrorException, InvalidArgumentException {
		return remote.findHolderAnnuities(annuityHolder);
	}
	public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		return remote.findHolderById(annuityHolder);
	}
	public IPayor findPayorById(IPayor payor) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		return remote.findPayorById(payor);
	}
	public IPayout findPayoutById(IPayout payout) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		return remote.findPayoutById(payout);
	}
	public IRider findRiderById(IRider rider) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		return remote.findRiderById(rider);
	}
	public IAnnuity createAnnuity(IAnnuity ann) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		return remote.createAnnuity(ann);
	}
	public IAnnuityHolder createAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		return remote.createAnnuityHolder(annHolder);
	}
	public IContact createContact(IContact contact) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		return remote.createContact(contact);
	}
	public IPayor createPayor(IPayor payor) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		return remote.createPayor(payor);
	}
	public void deleteAnnuity(IAnnuity annuity) throws ServerInternalErrorException, InvalidArgumentException {
		remote.deleteAnnuity(annuity);			
	}
	public void deleteAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, InvalidArgumentException {
		remote.deleteAnnuityHolder(annHolder);			
	}
	public void deleteContact(IContact contact) throws ServerInternalErrorException, InvalidArgumentException {
		remote.deleteContact(contact);			
	}
	public void deletePayor(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		remote.deletePayor(payor);		
	}
	public void deletePayout(IPayout payout) throws ServerInternalErrorException, InvalidArgumentException {
		remote.deletePayout(payout);
	}
	public void deleteRider(IRider rider) throws ServerInternalErrorException, InvalidArgumentException {
		remote.deleteRider(rider);
	}
	public IAnnuity updateAnnuity(IAnnuity ann) throws ServerInternalErrorException, InvalidArgumentException {
		return remote.updateAnnuity(ann);
	}
	public IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, InvalidArgumentException {
		return remote.updateAnnuityHolder(annHolder);
	}
	public IContact updateContact(IContact contact) throws ServerInternalErrorException, InvalidArgumentException {
		return remote.updateContact(contact);
	}
	public IPayor updatePayor(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		return remote.updatePayor(payor);
	}

	public List<IAnnuity> findPayorAnnuities(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		return remote.findPayorAnnuities(payor);
	}

	public IPayout createPayout(IPayout payout) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		return remote.createPayout(payout);
	}

	public IPayout updatePayout(IPayout payout) throws ServerInternalErrorException, InvalidArgumentException {
		return remote.updatePayout(payout);
	}
	
}
