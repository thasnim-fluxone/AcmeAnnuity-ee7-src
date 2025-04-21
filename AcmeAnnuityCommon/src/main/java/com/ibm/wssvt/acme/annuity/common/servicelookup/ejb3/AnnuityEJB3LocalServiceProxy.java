package com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3;

import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.AnnuityMgmtSvcEJB30Local;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;

public class AnnuityEJB3LocalServiceProxy implements IAnnuityService{
	private AnnuityMgmtSvcEJB30Local local;		
	
	public AnnuityEJB3LocalServiceProxy(AnnuityMgmtSvcEJB30Local local) {
		super();
		this.local = local;
	}
		
	public IAnnuity findAnnuityById(IAnnuity annuity) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException{
		return local.findAnnuityById(annuity);
	}
	public IAnnuityHolder findAnnuityHolder(IAnnuity annuity) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException{
		return local.findAnnuityHolder(annuity);
	}
	public IContact findContactById(IContact contact) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException{
		return local.findContactById(contact);
	}
	public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder) throws ServerInternalErrorException, InvalidArgumentException{
		return local.findHolderAnnuities(annuityHolder);
	}
	public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException{
		return local.findHolderById(annuityHolder);
	}
	public IPayor findPayorById(IPayor payor) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException{
		return local.findPayorById(payor);
	}
	public IPayout findPayoutById(IPayout payout) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException{
		return local.findPayoutById(payout);
	}
	public IRider findRiderById(IRider rider) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException{
		return local.findRiderById(rider);
	}
	public IAnnuity createAnnuity(IAnnuity ann) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException{
		return local.createAnnuity(ann);
	}
	public IAnnuityHolder createAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException{
		return local.createAnnuityHolder(annHolder);
	}
	public IContact createContact(IContact contact) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException{
		return local.createContact(contact);
	}
	public IPayor createPayor(IPayor payor) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException{
		return local.createPayor(payor);
	}
	public void deleteAnnuity(IAnnuity annuity) throws ServerInternalErrorException, InvalidArgumentException{
		local.deleteAnnuity(annuity);			
	}
	public void deleteAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, InvalidArgumentException{
		local.deleteAnnuityHolder(annHolder);			
	}
	public void deleteContact(IContact contact) throws ServerInternalErrorException, InvalidArgumentException{
		local.deleteContact(contact);			
	}
	public void deletePayor(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException{
		local.deletePayor(payor);		
	}
	public void deletePayout(IPayout payout) throws ServerInternalErrorException, InvalidArgumentException{
		local.deletePayout(payout);
	}
	public void deleteRider(IRider rider) throws ServerInternalErrorException, InvalidArgumentException{
		local.deleteRider(rider);
	}
	public IAnnuity updateAnnuity(IAnnuity ann) throws ServerInternalErrorException, InvalidArgumentException{
		return local.updateAnnuity(ann);
	}
	public IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, InvalidArgumentException{
		return local.updateAnnuityHolder(annHolder);
	}
	public IContact updateContact(IContact contact) throws ServerInternalErrorException, InvalidArgumentException{
		return local.updateContact(contact);
	}
	public IPayor updatePayor(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException{
		return local.updatePayor(payor);
	}

	public List<IAnnuity> findPayorAnnuities(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException{
		return local.findPayorAnnuities(payor);
	}

	public IPayout createPayout(IPayout payout) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException{
		return local.createPayout(payout);
	}

	public IPayout updatePayout(IPayout payout) throws ServerInternalErrorException, InvalidArgumentException{
		return local.updatePayout(payout);
	}

}
