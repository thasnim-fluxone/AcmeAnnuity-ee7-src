package com.ibm.wssvt.acme.annuity.common.client.adapter.policy;

import java.util.List;
import java.util.logging.Level;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneContact;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneficiary;
import com.ibm.wssvt.acme.annuity.common.bean.IFund;
import com.ibm.wssvt.acme.annuity.common.bean.IPersisteble;
import com.ibm.wssvt.acme.annuity.common.bean.IPolicy;
import com.ibm.wssvt.acme.annuity.common.bean.jpa.BeneContactId;
import com.ibm.wssvt.acme.annuity.common.business.IPolicyService;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.annuity.common.persistence.PolicyPersistence;
import com.ibm.wssvt.acme.annuity.common.persistence.PolicyPersistenceFactory;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class PolicyJPASAAdapter extends AbstractPolicyServerAdapter{
		
	private static final long serialVersionUID = -7761738176018995756L;
	
	private static PolicyJPAStandAlonePersistenceProxy standAlonePersistence = null;
	//AcmeLogger logger = getLogger(getClass().getName());
	protected synchronized IPolicyService getPolicyService() throws ServerAdapterCommunicationException {
		AcmeLogger logger = getLogger(getClass().getName());
		try {			
			if (standAlonePersistence == null) {
				logger.fine("We are in the JPA SA");
				logger.fine(getConfiguration().getParameters().toString());
				PolicyPersistence<IPersisteble<?>, String, String> policyPersistence = 
					PolicyPersistenceFactory.getPolicyPersistence(this, logger);
				standAlonePersistence = new PolicyJPAStandAlonePersistenceProxy(policyPersistence);
			}			
		} catch (ServerPersistenceModuleException e) {
			logger.log(Level.SEVERE, "Failed to get a PolicyJPAStandAlonePersistenceProxy - error: " +e, e);
			throw new ServerAdapterCommunicationException("Failed to get a PolicyJPAStandAlonePersistenceProxy ");
		}
		return standAlonePersistence;
		
	}
	
	private class PolicyJPAStandAlonePersistenceProxy implements IPolicyService {
		private PolicyPersistence<IPersisteble<?>, String, String> policyPersistence;
		//private AcmeLogger proxylogger;
		
		public PolicyJPAStandAlonePersistenceProxy(PolicyPersistence<IPersisteble<?>, String, String> persistenceAdapter) {
			this.policyPersistence = persistenceAdapter;
			//this.proxylogger = mylogger;
		}
		
		@SuppressWarnings("unchecked")
		public IPolicy createPolicy(IPolicy pol)
		throws ServerInternalErrorException, EntityAlreadyExistsException,
		InvalidArgumentException {
			//proxylogger.info("createPolicy in PolicyJPASA");
			//proxylogger.info("Policy details : id, fundname = " + pol.getId()+","+pol.getFund().getFundName());
			return (IPolicy) policyPersistence.createObject((IPersisteble<String>) pol);
		}


		public IPolicy findPolicyById(IPolicy policy)
				throws EntityNotFoundException, ServerInternalErrorException,
				InvalidArgumentException {
			return policyPersistence.readObject(policy.getClass(), policy.getId(), policy);
		}

		public void deletePolicy(IPolicy policy)
				throws ServerInternalErrorException, InvalidArgumentException {
			policyPersistence.deleteObject(policy.getClass(), policy.getId(), policy);
			
		}

		@SuppressWarnings("unchecked")
		public IPolicy updatePolicy(IPolicy policy)
				throws ServerInternalErrorException, InvalidArgumentException {
			return (IPolicy) policyPersistence.updateObject((IPersisteble<String>) policy);
		}

		public IAnnuityHolder findHolderById(IAnnuityHolder holder)
		throws EntityNotFoundException, ServerInternalErrorException,
		InvalidArgumentException {
			return policyPersistence.readObject(holder.getClass(), holder.getId(), holder);
		}
		@SuppressWarnings("unchecked")
		public IBeneficiary createBeneficiary(IBeneficiary beneficiary)
				throws ServerInternalErrorException,
				EntityAlreadyExistsException, InvalidArgumentException {
			return (IBeneficiary) policyPersistence.createObject((IPersisteble<String>) beneficiary);
		}

		public void deleteBeneficiary(IBeneficiary beneficiary)
				throws ServerInternalErrorException, InvalidArgumentException {
			policyPersistence.deleteObject(beneficiary.getClass(), beneficiary.getId(), beneficiary);
		}

		public IBeneficiary findBeneficiaryById(IBeneficiary beneficiary)
				throws EntityNotFoundException, ServerInternalErrorException,
				InvalidArgumentException {
			return policyPersistence.readObject(beneficiary.getClass(), beneficiary.getId(), beneficiary);
		}

		@SuppressWarnings("unchecked")
		public IBeneficiary updateBeneficiary(IBeneficiary beneficiary)
				throws ServerInternalErrorException, InvalidArgumentException {
			return (IBeneficiary) policyPersistence.updateObject((IPersisteble<String>) beneficiary);
		}

		@SuppressWarnings("unchecked")
		public IBeneContact createBeneContact(IBeneContact beneContact)
				throws ServerInternalErrorException,
				EntityAlreadyExistsException, InvalidArgumentException {
			return (IBeneContact) policyPersistence.createObject((IPersisteble<BeneContactId>) beneContact);
		}

		public void deleteBeneContact(IBeneContact beneContact)
				throws ServerInternalErrorException, InvalidArgumentException {
			policyPersistence.deleteObject(beneContact.getClass(), beneContact.getId(), beneContact);
		}

		public IBeneContact findBeneContactById(IBeneContact beneContact)
				throws EntityNotFoundException, ServerInternalErrorException,
				InvalidArgumentException {
			return policyPersistence.readObject(beneContact.getClass(), beneContact.getId(), beneContact);
		}

		@SuppressWarnings("unchecked")
		public IBeneContact updateBeneContact(IBeneContact beneContact)
				throws ServerInternalErrorException, InvalidArgumentException {
			return (IBeneContact) policyPersistence.updateObject((IPersisteble<BeneContactId>) beneContact);
		}
		
		public List<IPolicy> findHolderPolicies(IAnnuityHolder annuityHolder) throws ServerInternalErrorException, InvalidArgumentException {
			return policyPersistence.getHolderPolicies(annuityHolder);
		}
		
		public List<IBeneficiary> findHolderBeneficiaries(IAnnuityHolder annuityHolder) throws ServerInternalErrorException, InvalidArgumentException {
			return policyPersistence.getHolderBeneficiaries(annuityHolder);
		}
		
		public List<String> findHolderBeneficiaryContactEmails(IAnnuityHolder annuityHolder) throws ServerInternalErrorException, InvalidArgumentException {
			return policyPersistence.getHolderBeneficiaryContactEmails(annuityHolder);
		}
		
		public IFund findFundIndexRateFromFundName(IAnnuityHolder holder, String fundName) throws InvalidArgumentException, ServerInternalErrorException {
			return policyPersistence.getFundIndexRateFromFundName(holder, fundName);
		}
		
		public List <IFund> findFundsFromState(IAnnuityHolder holder, String state) throws InvalidArgumentException, ServerInternalErrorException {
			return policyPersistence.getFundsInState(holder, state);   
		}
		
		public List<IPersisteble<?>> customQuery(IAnnuityHolder holder) throws InvalidArgumentException, ServerInternalErrorException {
			return policyPersistence.customQuery(holder);
		}
	}

}
