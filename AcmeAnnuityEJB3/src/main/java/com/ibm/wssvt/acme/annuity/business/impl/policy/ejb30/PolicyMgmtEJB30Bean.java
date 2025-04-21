package com.ibm.wssvt.acme.annuity.business.impl.policy.ejb30;
/*
 * Bean for the Mgmt and Serach functions for Policy
 * rhaque@us.ibm.com
 * 
 */
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import com.ibm.wssvt.acme.annuity.business.logic.PolicyMgmtSvc;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityObject;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneContact;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneficiary;
import com.ibm.wssvt.acme.annuity.common.bean.IFund;
import com.ibm.wssvt.acme.annuity.common.bean.IPersisteble;
import com.ibm.wssvt.acme.annuity.common.bean.IPolicy;
import com.ibm.wssvt.acme.annuity.common.bean.jpa.BeneContactId;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.annuity.common.persistence.PolicyPersistence;
import com.ibm.wssvt.acme.annuity.common.persistence.PolicyPersistenceFactory;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityLoggerFactory;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;


@Stateless
@Local(PolicyMgmtEJB30Local.class)
public class PolicyMgmtEJB30Bean implements PolicyMgmtSvc {


	@SuppressWarnings("unchecked")
	public IPolicy createPolicy(IPolicy policy)
	throws ServerInternalErrorException, EntityAlreadyExistsException,
	InvalidArgumentException {

		validateParam(policy);
		return (IPolicy) createPersisteble((IPersisteble<String>)policy);
	}

	@SuppressWarnings("unchecked")
	public IBeneficiary createBeneficiary(IBeneficiary beneficiary)
	throws ServerInternalErrorException, EntityAlreadyExistsException,
	InvalidArgumentException {

		validateParam(beneficiary);
		return (IBeneficiary) createPersisteble((IPersisteble<String>)beneficiary);
	}

	@SuppressWarnings("unchecked")
	public IBeneContact createBeneContact(IBeneContact beneContact)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		return (IBeneContact) createPersisteble((IPersisteble<BeneContactId>)beneContact);
	}

	@SuppressWarnings("unchecked")
	public void deletePolicy(IPolicy policy)
	throws ServerInternalErrorException, InvalidArgumentException {
		validateParam(policy);
		deletePersisteble((IPersisteble<String>)policy);
	}

	@SuppressWarnings("unchecked")
	public void deleteBeneficiary(IBeneficiary beneficiary)
	throws ServerInternalErrorException, InvalidArgumentException {
		validateParam(beneficiary);
		deletePersisteble((IPersisteble<String>)beneficiary);
	}

	@SuppressWarnings("unchecked")
	public void deleteBeneContact(IBeneContact beneContact)
			throws ServerInternalErrorException, InvalidArgumentException {
		deletePersisteble((IPersisteble<BeneContactId>)beneContact);
	}

	@SuppressWarnings("unchecked")
	public IPolicy updatePolicy(IPolicy policy)
	throws ServerInternalErrorException, InvalidArgumentException {
		validateParam(policy);
		return (IPolicy) updatePersisteble((IPersisteble<String>)policy);
	}

	@SuppressWarnings("unchecked")
	public IBeneficiary updateBeneficiary(IBeneficiary beneficiary)
	throws ServerInternalErrorException, InvalidArgumentException {
		validateParam(beneficiary);
		return (IBeneficiary) updatePersisteble((IPersisteble<String>)beneficiary);
	}

	@SuppressWarnings("unchecked")
	public IBeneContact updateBeneContact(IBeneContact beneContact)
			throws ServerInternalErrorException, InvalidArgumentException {
		return (IBeneContact) updatePersisteble((IPersisteble<BeneContactId>)beneContact);
	}

	public IPolicy findPolicyById(IPolicy policy) 
	throws EntityNotFoundException, ServerPersistenceModuleException, InvalidArgumentException {		
		AcmeLogger logger = getLogger(policy);
		logger.fine("find policy by id called for id: " + policy.getId());
		PolicyPersistence<IPersisteble<?>, String, String> persistenceAdapter = 
			PolicyPersistenceFactory.getPolicyPersistence(policy, logger);				
		return persistenceAdapter.readObject(policy.getClass(), policy.getId(), policy);						
	}

	public IBeneficiary findBeneficiaryById(IBeneficiary beneficiary) 
	throws EntityNotFoundException, ServerPersistenceModuleException, InvalidArgumentException {		
		AcmeLogger logger = getLogger(beneficiary);
		logger.fine("find beneficiary by id called for id: " + beneficiary.getId());
		PolicyPersistence<IPersisteble<?>, String, String> persistenceAdapter = 
			PolicyPersistenceFactory.getPolicyPersistence(beneficiary, logger);				
		return persistenceAdapter.readObject(beneficiary.getClass(), beneficiary.getId(), beneficiary);						
	}

	public IBeneContact findBeneContactById(IBeneContact beneContact)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		AcmeLogger logger = getLogger(beneContact);
		logger.fine("find beneContact by id called for id: " + beneContact.getId().toString());
		PolicyPersistence<IPersisteble<?>, String, String> persistenceAdapter =
			PolicyPersistenceFactory.getPolicyPersistence(beneContact, logger);
		return persistenceAdapter.readObject(beneContact.getClass(), beneContact.getId(), beneContact);
	}

	public IAnnuityHolder findHolderById(IAnnuityHolder holder) 
	throws EntityNotFoundException, ServerPersistenceModuleException, InvalidArgumentException {		
		AcmeLogger logger = getLogger(holder);
		logger.fine("find holder by id called for id: " + holder.getId());
		PolicyPersistence<IPersisteble<?>, String, String> persistenceAdapter = 
			PolicyPersistenceFactory.getPolicyPersistence(holder, logger);				
		return persistenceAdapter.readObject(holder.getClass(), holder.getId(), holder);						
	}
	
	public List<IPolicy> findHolderPolicies(IAnnuityHolder annuityHolder)
	throws ServerInternalErrorException, InvalidArgumentException{
		AcmeLogger logger = getLogger(annuityHolder);
		logger.fine("find holder policies called for id: " + annuityHolder.getId());
		PolicyPersistence<IPersisteble<?>, String, String> persistenceAdapter =
			PolicyPersistenceFactory.getPolicyPersistence(annuityHolder, logger);
		return persistenceAdapter.getHolderPolicies(annuityHolder);	
	}
	
	public List<IBeneficiary> findHolderBeneficiaries(IAnnuityHolder annuityHolder)
	throws ServerInternalErrorException, InvalidArgumentException{
		AcmeLogger logger = getLogger(annuityHolder);
		logger.fine("find holder beneficiaries called for id: " + annuityHolder.getId());
		PolicyPersistence<IPersisteble<?>, String, String> persistenceAdapter =
			PolicyPersistenceFactory.getPolicyPersistence(annuityHolder, logger);
		return persistenceAdapter.getHolderBeneficiaries(annuityHolder);	
	}
	
	public List<String> findHolderBeneficiaryContactEmails(IAnnuityHolder annuityHolder)
	throws ServerInternalErrorException, InvalidArgumentException{
		AcmeLogger logger = getLogger(annuityHolder);
		logger.fine("find holder beneficiary contacte e-mails called for id: " + annuityHolder.getId());
		PolicyPersistence<IPersisteble<?>, String, String> persistenceAdapter =
			PolicyPersistenceFactory.getPolicyPersistence(annuityHolder, logger);
		return persistenceAdapter.getHolderBeneficiaryContactEmails(annuityHolder);	
	}
	
	public IFund findFundIndexRateFromFundName(IAnnuityHolder annuityHolder, String fundName) 
	throws ServerInternalErrorException, InvalidArgumentException{
		AcmeLogger logger = getLogger(annuityHolder);
		logger.fine("find funds for fundName: "+ fundName +" called for id: " + annuityHolder.getId());
		PolicyPersistence<IPersisteble<?>, String, String> persistenceAdapter =
			PolicyPersistenceFactory.getPolicyPersistence(annuityHolder, logger);
		return persistenceAdapter.getFundIndexRateFromFundName(annuityHolder, fundName);	
	}
	
	public List<IFund> findFundsFromState(IAnnuityHolder holder, String state)
			throws InvalidArgumentException, ServerInternalErrorException {
		AcmeLogger logger = getLogger(holder);
		logger.fine("find funds for state: "+ state +" called for id: " + holder.getId());
		PolicyPersistence<IPersisteble<?>, String, String> persistenceAdapter =
			PolicyPersistenceFactory.getPolicyPersistence(holder, logger);
		return persistenceAdapter.getFundsInState(holder, state);
	}

	public List<IPersisteble<?>> customQuery(IAnnuityHolder annuityHolder) 
	throws ServerInternalErrorException, InvalidArgumentException{
		AcmeLogger logger = getLogger(annuityHolder);
		logger.fine("custom query called for holder: " + annuityHolder.getId());
		PolicyPersistence<IPersisteble<?>, String, String> persistenceAdapter =
			PolicyPersistenceFactory.getPolicyPersistence(annuityHolder, logger);
		return persistenceAdapter.customQuery(annuityHolder);	
	}
	
	private IPersisteble<?> createPersisteble(IPersisteble<?> persisteble) 
	throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		AcmeLogger logger = getLogger(persisteble);
		if (persisteble == null) {
			logger.info("Invalid argument.  Policy is null");
			throw new InvalidArgumentException("Invalid argument - passed value is null" + logger.getAllLogs());
		}
		logger.fine("entering create Persisteble.  Id:" + persisteble.getId());		
		PolicyPersistence<IPersisteble<?>, String, String> persistenceAdapter = 
			PolicyPersistenceFactory.getPolicyPersistence(persisteble, logger);			
		persisteble = persistenceAdapter.createObject(persisteble);	
		logger.fine("create completed id: " + persisteble.getId());
		return persisteble;
	}


	private IPersisteble<?> updatePersisteble(IPersisteble<?> persisteble) throws InvalidArgumentException, ServerPersistenceModuleException {
		AcmeLogger logger = getLogger(persisteble);
		logger.fine("updatePersisteble for class: " +persisteble.getClass().getName() 
				+ " called for IPersisteble<?> with id" + persisteble.getId());		
		PolicyPersistence<IPersisteble<?>, String, String> persistenceAdapter = 
			PolicyPersistenceFactory.getPolicyPersistence(persisteble, logger);
		persisteble = persistenceAdapter.updateObject((IPersisteble<?>)persisteble);		
		logger.fine("Persisteble updated. id: " + persisteble.getId());
		return persisteble;
	}

	private void deletePersisteble(IPersisteble<?> persisteble) 
	throws ServerPersistenceModuleException, InvalidArgumentException {	
		AcmeLogger logger = getLogger(persisteble);
		logger.fine("delete Persisteble called for class: " + persisteble.getClass().getName()
				+ ".  id: " + persisteble.getId());			
		PolicyPersistence<IPersisteble<?>, String, String> persistenceAdapter = 
			PolicyPersistenceFactory.getPolicyPersistence(persisteble, logger);
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
			logger.info("Business Logic error - Attempted to persist an non IPersisteble implementaion. " +
					"Returnning an ServerPersistenceException error. Object is:" + annuityObject);
			throw new InvalidArgumentException ("the parameter passed: " + annuityObject 
					+ " is not a valid implementaion of IPersisteble<?>");
		}
	}


	private AcmeLogger getLogger(Configrable<String, String> configrable) {
		return AnnuityLoggerFactory.getAcmeServerLogger(configrable, getClass().getName());
	}




}