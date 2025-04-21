package com.ibm.wssvt.acme.annuity.business.impl.ejb30;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import com.ibm.wssvt.acme.annuity.business.logic.AnnuityMgmtSvc;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.AnnuityMgmtSvcEJB30;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.AnnuityMgmtSvcEJB30Local;
import com.ibm.wssvt.acme.annuity.common.business.nonbusinessimpl.AnnuitySpecialImplFactory;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.util.EJBInputParamExaminerInterceptor;
import com.ibm.wssvt.acme.annuity.common.util.EJBLoggingInterceptor;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.exception.InvalidConfigurationException;

@Stateless
@RolesAllowed("users")
@Local(AnnuityMgmtSvcEJB30Local.class)
@Remote(AnnuityMgmtSvcEJB30.class)
@Interceptors({EJBInputParamExaminerInterceptor.class, EJBLoggingInterceptor.class} )// EJBRunningServerInfoInterceptor.class
public class AnnuityMgmtSvcEJB30Bean implements AnnuityMgmtSvc{

	@EJB
	AnnuitySearchEJB30Local annuitySearchSession = null;
	@EJB
	AnnuityMgmtEJB30Local annuityMgmtSession = null;
	@PermitAll
	public IAnnuity findAnnuityById(IAnnuity annuity) 
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException  {		
			return getAnnuityService(annuity).findAnnuityById(annuity);
	}
	@RolesAllowed ("admins")
	public IAnnuity createAnnuity(IAnnuity ann) 
		throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {		
			return getAnnuityService(ann).createAnnuity(ann);
	}
	@RolesAllowed ("admins")
	public IAnnuity updateAnnuity(IAnnuity ann) 
		throws ServerInternalErrorException, InvalidArgumentException {
		return getAnnuityService(ann).updateAnnuity(ann);
	}
	@RolesAllowed ("admins")
	public void deleteAnnuity(IAnnuity annuity) 
		throws ServerInternalErrorException, InvalidArgumentException {
		getAnnuityService(annuity).deleteAnnuity(annuity);
	}
	@PermitAll
	public IAnnuityHolder findAnnuityHolder(IAnnuity annuity) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		return getAnnuityService(annuity).findAnnuityHolder(annuity);
	}
	@PermitAll
	public IContact findContactById(IContact contact) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		return getAnnuityService(contact).findContactById(contact);
	}
	@PermitAll
	public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder) throws ServerInternalErrorException, InvalidArgumentException {
		return getAnnuityService(annuityHolder).findHolderAnnuities(annuityHolder);
	}
	@PermitAll
	public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		return getAnnuityService(annuityHolder).findHolderById(annuityHolder);
	}
	@RolesAllowed ("admins")
	public IAnnuityHolder createAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		return getAnnuityService(annHolder).createAnnuityHolder(annHolder);
	}
	@RolesAllowed ("admins")
	public IContact createContact(IContact contact) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		return getAnnuityService(contact).createContact(contact);
	}
	@RolesAllowed ("admins")
	public IPayor createPayor(IPayor payor) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		return getAnnuityService(payor).createPayor(payor);
	}
	@RolesAllowed ("admins")
	public void deleteAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, InvalidArgumentException {
		getAnnuityService(annHolder).deleteAnnuityHolder(annHolder);
		
	}
	@RolesAllowed ("admins")
	public void deleteContact(IContact contact) throws ServerInternalErrorException, InvalidArgumentException {
		getAnnuityService(contact).deleteContact(contact);
	}
	@RolesAllowed ("admins")
	public void deletePayor(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		getAnnuityService(payor).deletePayor(payor);		
	}
	@RolesAllowed ("admins")
	public IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, InvalidArgumentException {
		return getAnnuityService(annHolder).updateAnnuityHolder(annHolder);		
	}
	@RolesAllowed ("admins")
	public IContact updateContact(IContact contact) throws ServerInternalErrorException, InvalidArgumentException {
		return getAnnuityService(contact).updateContact(contact);
	}
	@RolesAllowed ("admins")
	public IPayor updatePayor(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		return getAnnuityService(payor).updatePayor(payor);
	}
	@RolesAllowed ("admins")
	public IPayout createPayout(IPayout payout) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		return getAnnuityService(payout).createPayout(payout);
	}
	@RolesAllowed ("admins")
	public IPayout updatePayout(IPayout payout) throws ServerInternalErrorException, InvalidArgumentException {
		return getAnnuityService(payout).updatePayout(payout);
	}
	@RolesAllowed ("admins")
	public void deletePayout(IPayout payout) throws ServerInternalErrorException, InvalidArgumentException {
		getAnnuityService(payout).deletePayout(payout);
		
	}
	@RolesAllowed ("admins")
	public void deleteRider(IRider rider) throws ServerInternalErrorException, InvalidArgumentException {
		getAnnuityService(rider).deleteRider(rider);
		
	}
	@PermitAll
	public IPayor findPayorById(IPayor payor) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		return getAnnuityService(payor).findPayorById(payor);
	}
	@PermitAll
	public IPayout findPayoutById(IPayout payout) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		return getAnnuityService(payout).findPayoutById(payout);
	}
	@PermitAll
	public IRider findRiderById(IRider rider) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		return getAnnuityService(rider).findRiderById(rider);
	}
	@PermitAll
	public List<IAnnuity> findPayorAnnuities(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		return getAnnuityService(payor).findPayorAnnuities(payor);
	}
	
	private IAnnuityService getAnnuityService(Configrable<String, String> configrable) throws InvalidArgumentException {
		String specialService = configrable.getConfiguration().getParameterValue("specialServiceName");
		if (specialService != null && specialService.trim().length() >0) {
			try {
				return new AnnuitySpecialImplFactory().getSpecialAnnuityService(configrable);
			} catch (InvalidConfigurationException e) {
				throw new InvalidArgumentException(e.getMessage());			
			}
		}
		return new LocalEJBService();
	}
	private class LocalEJBService implements IAnnuityService {

		@Override
		public IAnnuity createAnnuity(IAnnuity ann)
				throws ServerInternalErrorException,
				EntityAlreadyExistsException, InvalidArgumentException {
				return annuityMgmtSession.createAnnuity(ann);
		}

		@Override
		public IAnnuityHolder createAnnuityHolder(IAnnuityHolder annHolder)
				throws ServerInternalErrorException,
				EntityAlreadyExistsException, InvalidArgumentException {
			return annuityMgmtSession.createAnnuityHolder(annHolder);
		}

		@Override
		public IContact createContact(IContact contact)
				throws ServerInternalErrorException,
				EntityAlreadyExistsException, InvalidArgumentException {
			return annuityMgmtSession.createContact(contact);
		}

		@Override
		public IPayor createPayor(IPayor payor)
				throws ServerInternalErrorException,
				EntityAlreadyExistsException, InvalidArgumentException {
			return annuityMgmtSession.createPayor(payor);
		}

		@Override
		public IPayout createPayout(IPayout payout)
				throws ServerInternalErrorException,
				EntityAlreadyExistsException, InvalidArgumentException {
			return annuityMgmtSession.createPayout(payout);
		}

		@Override
		public void deleteAnnuity(IAnnuity annuity)
				throws ServerInternalErrorException, InvalidArgumentException {
			annuityMgmtSession.deleteAnnuity(annuity);
			
		}

		@Override
		public void deleteAnnuityHolder(IAnnuityHolder annHolder)
				throws ServerInternalErrorException, InvalidArgumentException {
			annuityMgmtSession.deleteAnnuityHolder(annHolder);
			
		}

		@Override
		public void deleteContact(IContact contact)
				throws ServerInternalErrorException, InvalidArgumentException {
			annuityMgmtSession.deleteContact(contact);
			
		}

		@Override
		public void deletePayor(IPayor payor)
				throws ServerInternalErrorException, InvalidArgumentException {
			annuityMgmtSession.deletePayor(payor);
			
		}

		@Override
		public void deletePayout(IPayout payout)
				throws ServerInternalErrorException, InvalidArgumentException {
			annuityMgmtSession.deletePayout(payout);
			
		}

		@Override
		public void deleteRider(IRider rider)
				throws ServerInternalErrorException, InvalidArgumentException {
			annuityMgmtSession.deleteRider(rider);
			
		}

		@Override
		public IAnnuity findAnnuityById(IAnnuity annuity)
				throws EntityNotFoundException, ServerInternalErrorException,
				InvalidArgumentException {
			return annuitySearchSession.findAnnuityById(annuity);
		}

		@Override
		public IAnnuityHolder findAnnuityHolder(IAnnuity annuity)
				throws EntityNotFoundException, ServerInternalErrorException,
				InvalidArgumentException {
			return annuitySearchSession.findAnnuityHolder(annuity);
		}

		@Override
		public IContact findContactById(IContact contact)
				throws EntityNotFoundException, ServerInternalErrorException,
				InvalidArgumentException {
			return annuitySearchSession.findContactById(contact);
		}

		@Override
		public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder)
				throws ServerInternalErrorException, InvalidArgumentException {
			return annuitySearchSession.findHolderAnnuities(annuityHolder);
		}

		@Override
		public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder)
				throws EntityNotFoundException, ServerInternalErrorException,
				InvalidArgumentException {			
			return annuitySearchSession.findHolderById(annuityHolder);
		}

		@Override
		public List<IAnnuity> findPayorAnnuities(IPayor payor)
				throws ServerInternalErrorException, InvalidArgumentException {
			return annuitySearchSession.findPayorAnnuities(payor);
		}

		@Override
		public IPayor findPayorById(IPayor payor)
				throws EntityNotFoundException, ServerInternalErrorException,
				InvalidArgumentException {
			return annuitySearchSession.findPayorById(payor);
		}

		@Override
		public IPayout findPayoutById(IPayout payout)
				throws EntityNotFoundException, ServerInternalErrorException,
				InvalidArgumentException {
			return annuitySearchSession.findPayoutById(payout);
		}

		@Override
		public IRider findRiderById(IRider rider)
				throws EntityNotFoundException, ServerInternalErrorException,
				InvalidArgumentException {		
			return findRiderById(rider);
		}

		@Override
		public IAnnuity updateAnnuity(IAnnuity ann)
				throws ServerInternalErrorException, InvalidArgumentException {			
			return annuityMgmtSession.updateAnnuity(ann);
		}

		@Override
		public IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annHolder)
				throws ServerInternalErrorException, InvalidArgumentException {
			return annuityMgmtSession.updateAnnuityHolder(annHolder);
		}

		@Override
		public IContact updateContact(IContact contact)
				throws ServerInternalErrorException, InvalidArgumentException {
			return annuityMgmtSession.updateContact(contact);
		}

		@Override
		public IPayor updatePayor(IPayor payor)
				throws ServerInternalErrorException, InvalidArgumentException {
			return annuityMgmtSession.updatePayor(payor);
		}

		@Override
		public IPayout updatePayout(IPayout payout)
				throws ServerInternalErrorException, InvalidArgumentException {
			return annuityMgmtSession.updatePayout(payout);
		}
		
	}
}
