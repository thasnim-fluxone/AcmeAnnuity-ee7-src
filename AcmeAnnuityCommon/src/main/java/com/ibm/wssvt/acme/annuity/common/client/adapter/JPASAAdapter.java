package com.ibm.wssvt.acme.annuity.common.client.adapter;

import java.util.List;
import java.util.logging.Level;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IPersisteble;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.annuity.common.persistence.AnnuityPerisstenceFactory;
import com.ibm.wssvt.acme.annuity.common.persistence.AnnuityPersistence;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
/**
 * $Rev: 483 $
 * $Date: 2007-07-06 03:22:50 -0500 (Fri, 06 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class JPASAAdapter extends AbstractAnnuityServerAdapter{
		
	private static final long serialVersionUID = -7761738176018995756L;
	
	private static AnnuityJPAStandAlonePersistenceProxy standAlonePersistence = null;
	protected synchronized IAnnuityService getAnnuityService() throws ServerAdapterCommunicationException {
		AcmeLogger logger = getLogger(getClass().getName());
		try {			
			if (standAlonePersistence == null) {
				logger.fine("We are in the JPA SA");
				logger.fine(getConfiguration().getParameters().toString());
				AnnuityPersistence<IPersisteble<String>, String, String> annuityPersistence = 
					AnnuityPerisstenceFactory.getAnnuityPersistence(this, logger);
				standAlonePersistence = new AnnuityJPAStandAlonePersistenceProxy(annuityPersistence);
			}			
		} catch (ServerPersistenceModuleException e) {
			logger.log(Level.SEVERE, "Failed to get a AnnuityJPAStandAlonePersistenceProxy - error: " +e, e);
			throw new ServerAdapterCommunicationException("Failed to get a AnnuityJPAStandAlonePersistenceProxy ");
		}
		return standAlonePersistence;
		
	}
	
	private class AnnuityJPAStandAlonePersistenceProxy implements IAnnuityService {
		private AnnuityPersistence<IPersisteble<String>, String, String> annuityPersistence;
		
		public AnnuityJPAStandAlonePersistenceProxy(AnnuityPersistence<IPersisteble<String>, String, String> persistenceAdapter) {
			this.annuityPersistence = persistenceAdapter;
		}
		
		public IAnnuity findAnnuityById(IAnnuity annuity) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
			String id = annuity.getId();
			annuity = annuityPersistence.readObject(annuity.getClass(), id, annuity);
			return annuity;
			
		}
		@SuppressWarnings("unchecked")
		public IAnnuity updateAnnuity(IAnnuity ann) throws ServerInternalErrorException, InvalidArgumentException {
			return (IAnnuity) annuityPersistence.updateObject((IPersisteble<String>)ann);
			
		}
		public void deleteAnnuity(IAnnuity annuity) throws ServerInternalErrorException, InvalidArgumentException {
			annuityPersistence.deleteObject(annuity.getClass(), annuity.getId(), annuity);			
		}
		@SuppressWarnings("unchecked")
		public IAnnuity createAnnuity(IAnnuity ann) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
			return (IAnnuity) annuityPersistence.createObject((IPersisteble<String>) ann);			
		}
		public IAnnuityHolder findAnnuityHolder(IAnnuity annuity) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
			return annuityPersistence.getAnnuityHolder(annuity);
		}
		public IContact findContactById(IContact contact) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
			return annuityPersistence.readObject(contact.getClass(), contact.getId(), contact);
		}
		public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder) throws ServerInternalErrorException, InvalidArgumentException {
			return annuityPersistence.getHolderAnnuities(annuityHolder);
		}
		public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
			return annuityPersistence.readObject(annuityHolder.getClass(), annuityHolder.getId(), annuityHolder);
		}
		@SuppressWarnings("unchecked")
		public IAnnuityHolder createAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
			return (IAnnuityHolder) annuityPersistence.createObject((IPersisteble<String>)annHolder);			
		}
		@SuppressWarnings("unchecked")
		public IContact createContact(IContact contact) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
			return (IContact) annuityPersistence.createObject((IPersisteble<String>) contact);
		}
		@SuppressWarnings("unchecked")
		public IPayor createPayor(IPayor payor) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
			return (IPayor) annuityPersistence.createObject((IPersisteble<String>)payor);
		}
		public void deleteAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, InvalidArgumentException {
			annuityPersistence.deleteObject(annHolder.getClass(), annHolder.getId(), annHolder);
		}
		public void deleteContact(IContact contact) throws ServerInternalErrorException, InvalidArgumentException {
			annuityPersistence.deleteObject(contact.getClass(), contact.getId(), contact);
			
		}
		public void deletePayor(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
			annuityPersistence.deleteObject(payor.getClass(), payor.getId(), payor);
			
		}
		@SuppressWarnings("unchecked")
		public IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, InvalidArgumentException {
			return (IAnnuityHolder) annuityPersistence.updateObject((IPersisteble<String>)annHolder);
		}
		@SuppressWarnings("unchecked")
		public IContact updateContact(IContact contact) throws ServerInternalErrorException, InvalidArgumentException {
			return (IContact) annuityPersistence.updateObject((IPersisteble<String>)contact);			
		}
		@SuppressWarnings("unchecked")
		public IPayor updatePayor(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
			return (IPayor) annuityPersistence.updateObject((IPersisteble<String>)payor);
		}
		public void deletePayout(IPayout payout) throws ServerInternalErrorException, InvalidArgumentException {
			annuityPersistence.deleteObject(payout.getClass(), payout.getId(), payout);
			
		}
		public void deleteRider(IRider rider) throws ServerInternalErrorException, InvalidArgumentException {
			annuityPersistence.deleteObject(rider.getClass(), rider.getId(), rider);			
		}
		
		public IPayor findPayorById(IPayor payor) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
			return annuityPersistence.readObject(payor.getClass(), payor.getId(), payor);
		}
		
		public IPayout findPayoutById(IPayout payout) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
			return annuityPersistence.readObject(payout.getClass(), payout.getId(), payout);
		}
		
		public IRider findRiderById(IRider rider) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
			return annuityPersistence.readObject(rider.getClass(), rider.getId(), rider);
		}
		
		public List<IAnnuity> findPayorAnnuities(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {			
			return annuityPersistence.getPayorAnnuities(payor);
		}

		@SuppressWarnings("unchecked")
		public IPayout createPayout(IPayout payout) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
			return (IPayout) annuityPersistence.createObject((IPersisteble<String>) payout);
		}

		@SuppressWarnings("unchecked")
		public IPayout updatePayout(IPayout payout) throws ServerInternalErrorException, InvalidArgumentException {
			return (IPayout) annuityPersistence.updateObject((IPersisteble<String>) payout);			
		}
				
	}

}
