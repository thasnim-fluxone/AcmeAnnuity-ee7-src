/**
 * 
 */
package com.ibm.wssvt.acme.annuity.business.impl.ejb30;

import javax.ejb.Local;
import javax.ejb.Stateless;

import com.ibm.wssvt.acme.annuity.business.logic.AnnuityMgmt;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityObject;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IPersisteble;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.annuity.common.persistence.AnnuityPerisstenceFactory;
import com.ibm.wssvt.acme.annuity.common.persistence.AnnuityPersistence;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityLoggerFactory;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/**
 * $Rev:400 $
 * $Date: 2007-07-06 03:22:50 -0500 (Fri, 06 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 * 
 */
@Stateless
@Local(AnnuityMgmtEJB30Local.class)
public class AnnuityMgmtEJB30Bean implements AnnuityMgmt {

		
	@SuppressWarnings("unchecked")
	public IAnnuity createAnnuity(IAnnuity annuity) 
		throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		validateParam(annuity);
		return (IAnnuity) createPersisteble((IPersisteble<String>)annuity);
		
	}
		
	@SuppressWarnings("unchecked")
	public IAnnuity updateAnnuity(IAnnuity annuity) 
		throws ServerPersistenceModuleException, InvalidArgumentException {	
		validateParam(annuity);
		return (IAnnuity) updatePersisteble((IPersisteble<String>)annuity);
	}


	@SuppressWarnings("unchecked")
	public void deleteAnnuity(IAnnuity annuity) 
		throws ServerPersistenceModuleException, InvalidArgumentException {	
		validateParam(annuity);
		deletePersisteble((IPersisteble<String>)annuity);	
	}
	
	@SuppressWarnings("unchecked")
	public IAnnuityHolder createAnnuityHolder(IAnnuityHolder annuityHolder) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		validateParam(annuityHolder);
		return (IAnnuityHolder) createPersisteble((IPersisteble<String>) annuityHolder);
	}

	@SuppressWarnings("unchecked")
	public IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annuityHolder) throws ServerInternalErrorException, InvalidArgumentException {
		validateParam(annuityHolder);
		return (IAnnuityHolder) updatePersisteble((IPersisteble<String>) annuityHolder);
	}

	@SuppressWarnings("unchecked")
	public void deleteAnnuityHolder(IAnnuityHolder annuityHolder) throws ServerInternalErrorException, InvalidArgumentException {
		validateParam(annuityHolder);
		deletePersisteble((IPersisteble<String>) annuityHolder);
		
	}

	@SuppressWarnings("unchecked")
	public IPayor createPayor(IPayor payor) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		validateParam(payor);
		return (IPayor) createPersisteble((IPersisteble<String>) payor);

	}
	@SuppressWarnings("unchecked")
	public IPayor updatePayor(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		validateParam(payor);
		return (IPayor) updatePersisteble((IPersisteble<String>) payor);

	}	
	@SuppressWarnings("unchecked")
	public void deletePayor(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		validateParam(payor);
		deletePersisteble((IPersisteble<String>) payor);
		
	}

	@SuppressWarnings("unchecked")
	public IContact createContact(IContact contact) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		validateParam(contact);
		return (IContact) createPersisteble((IPersisteble<String>) contact);
	}

	@SuppressWarnings("unchecked")
	public IContact updateContact(IContact contact) throws ServerInternalErrorException, InvalidArgumentException {
		validateParam(contact);
		return (IContact) updatePersisteble((IPersisteble<String>) contact);
	}
	
	@SuppressWarnings("unchecked")
	public void deleteContact(IContact contact) throws ServerInternalErrorException, InvalidArgumentException {
		validateParam(contact);
		deletePersisteble((IPersisteble<String>) contact);		
	}


	@SuppressWarnings("unchecked")
	public void deletePayout(IPayout payout) throws ServerInternalErrorException, InvalidArgumentException {
		validateParam(payout);
		deletePersisteble((IPersisteble<String>) payout);
		
	}

	@SuppressWarnings("unchecked")
	public void deleteRider(IRider rider) throws ServerInternalErrorException, InvalidArgumentException {
		validateParam(rider);
		deletePersisteble((IPersisteble<String>) rider);		
	}	
	@SuppressWarnings("unchecked")
	public IPayout createPayout(IPayout payout) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		validateParam(payout);
		return (IPayout) createPersisteble((IPersisteble<String>) payout);
	}

	@SuppressWarnings("unchecked")
	public IPayout updatePayout(IPayout payout) throws ServerInternalErrorException, InvalidArgumentException {
		validateParam(payout);
		return (IPayout) updatePersisteble((IPersisteble<String>) payout);
	}	
				
	private IPersisteble<String> createPersisteble(IPersisteble<String> persisteble) 
		throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		AcmeLogger logger = getLogger(persisteble);
		if (persisteble == null) {
			logger.info("Invalid argument.  Annuity is null");
			throw new InvalidArgumentException("Invalid argument - passed value is null" + logger.getAllLogs());
		}
		logger.fine("entering create Persisteble.  Id:" + persisteble.getId());		
		AnnuityPersistence<IPersisteble<String>, String, String> persistenceAdapter = 
			AnnuityPerisstenceFactory.getAnnuityPersistence(persisteble, logger);			
		persisteble = persistenceAdapter.createObject(persisteble);	
		logger.fine("create completed id: " + persisteble.getId());
		return persisteble;
	}

	private IPersisteble<String> updatePersisteble(IPersisteble<String> persisteble) throws InvalidArgumentException, ServerPersistenceModuleException {
		AcmeLogger logger = getLogger(persisteble);
		logger.fine("updatePersisteble for class: " +persisteble.getClass().getName() 
				+ " called for IPersisteble<String> with id" + persisteble.getId());		
		AnnuityPersistence<IPersisteble<String>, String, String> persistenceAdapter = 
			AnnuityPerisstenceFactory.getAnnuityPersistence(persisteble, logger);
		persisteble = persistenceAdapter.updateObject((IPersisteble<String>)persisteble);		
		logger.fine("Persisteble updated. id: " + persisteble.getId());
		return persisteble;
	}
	

	private void deletePersisteble(IPersisteble<String> persisteble) 
		throws ServerPersistenceModuleException, InvalidArgumentException {	
		AcmeLogger logger = getLogger(persisteble);
		logger.fine("delete Persisteble called for class: " + persisteble.getClass().getName()
				+ ".  id: " + persisteble.getId());			
		AnnuityPersistence<IPersisteble<String>, String, String> persistenceAdapter = 
			AnnuityPerisstenceFactory.getAnnuityPersistence(persisteble, logger);
		persistenceAdapter.deleteObject(persisteble.getClass(), persisteble.getId(), persisteble);	
		logger.fine("persisteble deleted.  id: " + persisteble.getId());
	
	}
	
	private void validateParam(IAnnuityObject annuityObject) throws InvalidArgumentException {
		AcmeLogger logger = getLogger(annuityObject);
		if (annuityObject == null) {
			logger.info("Invalid argument.  Annuity object is null");
			throw new InvalidArgumentException("Invalid argument - passed value is null");
		}
		
		if (!(annuityObject instanceof IPersisteble)) {
			logger.info("Business Logic error - Attempted to persist an non IPersisteble<String> implementaion. " +
					"Returnning an ServerPersistenceException error. Object is:" + annuityObject);
			throw new InvalidArgumentException ("the parameter passed: " + annuityObject 
					+ " is not a valid implementaion of IPersisteble<String>");
		}
	}

	private AcmeLogger getLogger(Configrable<String, String> configrable) {
		return AnnuityLoggerFactory.getAcmeServerLogger(configrable, getClass().getName());
	}

}
