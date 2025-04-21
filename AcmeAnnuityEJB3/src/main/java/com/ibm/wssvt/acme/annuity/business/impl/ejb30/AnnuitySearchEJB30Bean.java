/**
 * 
 */
package com.ibm.wssvt.acme.annuity.business.impl.ejb30;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import com.ibm.wssvt.acme.annuity.business.logic.AnnuitySearch;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IPersisteble;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.annuity.common.persistence.AnnuityPerisstenceFactory;
import com.ibm.wssvt.acme.annuity.common.persistence.AnnuityPersistence;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityLoggerFactory;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
/**
 * @author gli@us.ibm.com
 *
 */
@Stateless
@Local(AnnuitySearchEJB30Local.class)
public class AnnuitySearchEJB30Bean implements AnnuitySearch {
			
	public IAnnuity findAnnuityById(IAnnuity annuity) 
		throws EntityNotFoundException, ServerPersistenceModuleException, InvalidArgumentException {		
		AcmeLogger logger = getLogger(annuity);
		logger.fine("find annuity by id called for id: " + annuity.getId());
		AnnuityPersistence<IPersisteble<String>, String, String> persistenceAdapter = 
			AnnuityPerisstenceFactory.getAnnuityPersistence(annuity, logger);				
		return persistenceAdapter.readObject(annuity.getClass(), annuity.getId(), annuity);						
	}

	public IContact findContactById(IContact contact) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		AcmeLogger logger = getLogger(contact);
		logger.fine("find contact by id called for id: " + contact.getId());
		AnnuityPersistence<IPersisteble<String>, String, String> persistenceAdapter = 
			AnnuityPerisstenceFactory.getAnnuityPersistence(contact, logger);				
		return persistenceAdapter.readObject(contact.getClass(), contact.getId(), contact);						
	}
	public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		AcmeLogger logger = getLogger(annuityHolder);
		logger.fine("find holder by id called for id: " + annuityHolder.getId());
		AnnuityPersistence<IPersisteble<String>, String, String> persistenceAdapter = 
			AnnuityPerisstenceFactory.getAnnuityPersistence(annuityHolder, logger);				
		return persistenceAdapter.readObject(annuityHolder.getClass(), annuityHolder.getId(), annuityHolder);
	}

	public IAnnuityHolder findAnnuityHolder(IAnnuity annuity) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		AcmeLogger logger = getLogger(annuity);
		logger.fine("find holder by from annuity." );
		AnnuityPersistence<IPersisteble<String>, String, String> persistenceAdapter = 
			AnnuityPerisstenceFactory.getAnnuityPersistence(annuity, logger);				
		return persistenceAdapter.getAnnuityHolder(annuity);
	}
	
	public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder) throws ServerInternalErrorException, InvalidArgumentException {
		AcmeLogger logger = getLogger(annuityHolder);
		logger.fine("find holder annuities called for id: " + annuityHolder.getId());
		AnnuityPersistence<IPersisteble<String>, String, String> persistenceAdapter = 
			AnnuityPerisstenceFactory.getAnnuityPersistence(annuityHolder, logger);				
		return persistenceAdapter.getHolderAnnuities(annuityHolder);
	}


	public IPayor findPayorById(IPayor payor) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		AcmeLogger logger = getLogger(payor);
		logger.fine("find Payor by Id called. id: " + payor.getId());
		AnnuityPersistence<IPersisteble<String>, String, String> persistenceAdapter = 
			AnnuityPerisstenceFactory.getAnnuityPersistence(payor, logger);				
		return persistenceAdapter.readObject(payor.getClass(), payor.getId(), payor);		
	}

	public IPayout findPayoutById(IPayout payout) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		AcmeLogger logger = getLogger(payout);
		logger.fine("find Payout by Id called. id: " + payout.getId());
		AnnuityPersistence<IPersisteble<String>, String, String> persistenceAdapter = 
			AnnuityPerisstenceFactory.getAnnuityPersistence(payout, logger);				
		return persistenceAdapter.readObject(payout.getClass(), payout.getId(), payout);	
	}

	public IRider findRiderById(IRider rider) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		AcmeLogger logger = getLogger(rider);
		logger.fine("find Rider by Id called. id: " + rider.getId());
		AnnuityPersistence<IPersisteble<String>, String, String> persistenceAdapter = 
			AnnuityPerisstenceFactory.getAnnuityPersistence(rider, logger);				
		return persistenceAdapter.readObject(rider.getClass(), rider.getId(), rider);		
	}

	public List<IAnnuity> findPayorAnnuities(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		AcmeLogger logger = getLogger(payor);
		logger.fine("find Payor annuities called for id: " + payor.getId());
		AnnuityPersistence<IPersisteble<String>, String, String> persistenceAdapter = 
			AnnuityPerisstenceFactory.getAnnuityPersistence(payor, logger);				
		return persistenceAdapter.getPayorAnnuities(payor);	
	}
	
	private AcmeLogger getLogger(Configrable<String, String> configrable) {
		return AnnuityLoggerFactory.getAcmeServerLogger(configrable, getClass().getName());
	}

}
