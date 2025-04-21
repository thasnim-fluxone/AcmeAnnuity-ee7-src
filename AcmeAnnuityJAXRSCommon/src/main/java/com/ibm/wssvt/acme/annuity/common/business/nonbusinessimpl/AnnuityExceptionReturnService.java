package com.ibm.wssvt.acme.annuity.common.business.nonbusinessimpl;

import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.util.RunningServerInfo;
import com.ibm.wssvt.acme.common.bean.Configrable;
/**
 * NOTICE: This class simply returns the same object that it has received.
 * If the method returns a different type of object, a null will be returned.
 * If the method returns nothing, then nothing will be returned.
 * @author malbedaiwi
 *
 */
public class AnnuityExceptionReturnService implements IAnnuityService {
	private static final String EXCEPTION_TYPE ="exceptionType";
	
	@Override
	public IAnnuity createAnnuity(IAnnuity ann)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {		
		processConfigrable(ann);
		return ann;
	}

	@Override
	public IAnnuityHolder createAnnuityHolder(IAnnuityHolder annHolder)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		processConfigrable(annHolder);
		return annHolder;
	}

	@Override
	public IContact createContact(IContact contact)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {		
		processConfigrable(contact);
		return contact;
	}

	@Override
	public IPayor createPayor(IPayor payor)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		processConfigrable(payor);
		return payor;
	}

	@Override
	public IPayout createPayout(IPayout payout)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		processConfigrable(payout);
		return payout;
	}

	@Override
	public void deleteAnnuity(IAnnuity annuity)
			throws ServerInternalErrorException, InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(annuity);
	}

	@Override
	public void deleteAnnuityHolder(IAnnuityHolder annHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(annHolder);
	}

	@Override
	public void deleteContact(IContact contact)
			throws ServerInternalErrorException, InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(contact);
	}

	@Override
	public void deletePayor(IPayor payor) throws ServerInternalErrorException,
			InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(payor);
	}

	@Override
	public void deletePayout(IPayout payout)
			throws ServerInternalErrorException, InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(payout);
	}

	@Override
	public void deleteRider(IRider rider) throws ServerInternalErrorException,
			InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(rider);
	}

	@Override
	public IAnnuity findAnnuityById(IAnnuity annuity)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(annuity);
		return annuity;
	}

	@Override
	public IAnnuityHolder findAnnuityHolder(IAnnuity annuity)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(annuity);
		return null;
	}

	@Override
	public IContact findContactById(IContact contact)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(contact);
		return contact;
	}

	@Override
	public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(annuityHolder);
		return null;
	}

	@Override
	public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(annuityHolder);
		return annuityHolder;
	}

	@Override
	public List<IAnnuity> findPayorAnnuities(IPayor payor)
			throws ServerInternalErrorException, InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(payor);
		return null;
	}

	@Override
	public IPayor findPayorById(IPayor payor) throws EntityNotFoundException,
			ServerInternalErrorException, InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(payor);
		return payor;
	}

	@Override
	public IPayout findPayoutById(IPayout payout)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(payout);
		return payout;
	}

	@Override
	public IRider findRiderById(IRider rider) throws EntityNotFoundException,
			ServerInternalErrorException, InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(rider);
		return rider;
	}

	@Override
	public IAnnuity updateAnnuity(IAnnuity ann)
			throws ServerInternalErrorException, InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(ann);
		return ann;
	}

	@Override
	public IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(annHolder);
		return annHolder;
	}

	@Override
	public IContact updateContact(IContact contact)
			throws ServerInternalErrorException, InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(contact);
		return contact;
	}

	@Override
	public IPayor updatePayor(IPayor payor)
			throws ServerInternalErrorException, InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(payor);
		return payor;
	}

	@Override
	public IPayout updatePayout(IPayout payout)
			throws ServerInternalErrorException, InvalidArgumentException {
		// TODO Auto-generated method stub
		processConfigrable(payout);
		return payout;
	}

		
	private void processConfigrable(Configrable<String, String> configrable){				
		String exceptionType = configrable.getConfiguration().getParameterValue(EXCEPTION_TYPE);
		RunningServerInfo.getInstance().setRunningServerInfo(configrable);
		if ("runtimeException".equalsIgnoreCase(exceptionType)) {			
			throw new RuntimeException("RuntimeException was thrown by design from class: " + getClass().getName());
		}else if ("nullPointerException".equalsIgnoreCase(exceptionType)) {
			throw new NullPointerException("NullPointerException was thrown by design from class: " + getClass().getName());
		}else {
			throw new IllegalArgumentException("IllegalArgumentException was thrown by design from class: " + getClass().getName());
		}
		
		
	}
}
