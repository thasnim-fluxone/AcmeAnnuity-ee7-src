package com.ibm.wssvt.acme.annuity.business.impl.policy.ejb30;

//import java.util.List;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import com.ibm.wssvt.acme.annuity.business.logic.PolicyMgmtSvc;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneContact;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneficiary;
import com.ibm.wssvt.acme.annuity.common.bean.IFund;
import com.ibm.wssvt.acme.annuity.common.bean.IPersisteble;
import com.ibm.wssvt.acme.annuity.common.bean.IPolicy;
import com.ibm.wssvt.acme.annuity.common.business.IPolicyService;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.policy.PolicyMgmtSvcEJB30;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.policy.PolicyMgmtSvcEJB30Local;
import com.ibm.wssvt.acme.annuity.common.business.nonbusinessimpl.PolicyQuickReturnService;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.util.EJBInputParamExaminerInterceptor;
import com.ibm.wssvt.acme.annuity.common.util.EJBLoggingInterceptor;
import com.ibm.wssvt.acme.common.bean.Configrable;

@Stateless
@RolesAllowed("users")
@Local(PolicyMgmtSvcEJB30Local.class)
@Remote(PolicyMgmtSvcEJB30.class)
@Interceptors({EJBInputParamExaminerInterceptor.class, EJBLoggingInterceptor.class} )
public class PolicyMgmtSvcEJB30Bean implements PolicyMgmtSvc{

	@EJB
	PolicyMgmtEJB30Local policyMgmtSession = null;


	@RolesAllowed ("admins")
	public IPolicy createPolicy(IPolicy policy)
	throws ServerInternalErrorException, EntityAlreadyExistsException,
	InvalidArgumentException {
		return getPolicyService(policy).createPolicy(policy);
	}
	
	@RolesAllowed ("admins")
	public IBeneficiary createBeneficiary(IBeneficiary beneficiary)
	throws ServerInternalErrorException, EntityAlreadyExistsException,
	InvalidArgumentException {
		return getPolicyService(beneficiary).createBeneficiary(beneficiary);
	}

	@RolesAllowed("admins")
	public IBeneContact createBeneContact(IBeneContact beneContact)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		return getPolicyService(beneContact).createBeneContact(beneContact);
	}

	public IPolicy findPolicyById(IPolicy policy)
	throws EntityNotFoundException, ServerInternalErrorException,
	InvalidArgumentException {
		return getPolicyService(policy).findPolicyById(policy);
	}
	
	public List<IPolicy> findHolderPolicies(IAnnuityHolder annuityHolder)
	throws ServerInternalErrorException, InvalidArgumentException{
		return getPolicyService(annuityHolder).findHolderPolicies(annuityHolder);
	}
	
	public List<IBeneficiary> findHolderBeneficiaries(IAnnuityHolder annuityHolder)
	throws ServerInternalErrorException, InvalidArgumentException{
		return getPolicyService(annuityHolder).findHolderBeneficiaries(annuityHolder);
	}
	
	public List<String> findHolderBeneficiaryContactEmails(IAnnuityHolder annuityHolder)
	throws ServerInternalErrorException, InvalidArgumentException{
		return getPolicyService(annuityHolder).findHolderBeneficiaryContactEmails(annuityHolder);
	}
	
	public IFund findFundIndexRateFromFundName(IAnnuityHolder holder, String fundName) 
	throws InvalidArgumentException, ServerInternalErrorException {
		return getPolicyService(holder).findFundIndexRateFromFundName(holder, fundName);
	}
	
	public List<IFund> findFundsFromState(IAnnuityHolder annuityHolder, String state)
	throws ServerInternalErrorException, InvalidArgumentException{
		return getPolicyService(annuityHolder).findFundsFromState(annuityHolder, state);
	}
	
	public IAnnuityHolder findHolderById(IAnnuityHolder holder)
	throws EntityNotFoundException, ServerInternalErrorException,
	InvalidArgumentException {
		return getPolicyService(holder).findHolderById(holder);
	}
	
	public IBeneficiary findBeneficiaryById(IBeneficiary beneficiary)
	throws EntityNotFoundException, ServerInternalErrorException,
	InvalidArgumentException {
		return getPolicyService(beneficiary).findBeneficiaryById(beneficiary);
	}

	public IBeneContact findBeneContactById(IBeneContact beneContact)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		return getPolicyService(beneContact).findBeneContactById(beneContact);
	}
	
	public List<IPersisteble<?>> customQuery(IAnnuityHolder holder)
			throws ServerInternalErrorException, InvalidArgumentException {
		return getPolicyService(holder).customQuery(holder);
	}

	@RolesAllowed ("admins")
	public void deletePolicy(IPolicy policy)
	throws ServerInternalErrorException, InvalidArgumentException {
		getPolicyService(policy).deletePolicy(policy);		
	}
	
	@RolesAllowed ("admins")
	public void deleteBeneficiary(IBeneficiary beneficiary)
	throws ServerInternalErrorException, InvalidArgumentException {
		getPolicyService(beneficiary).deleteBeneficiary(beneficiary);	
	}

	@RolesAllowed("admins")
	public void deleteBeneContact(IBeneContact beneContact)
			throws ServerInternalErrorException, InvalidArgumentException {
		getPolicyService(beneContact).deleteBeneContact(beneContact);
	}

	@RolesAllowed ("admins")
	public IPolicy updatePolicy(IPolicy policy)
	throws ServerInternalErrorException, InvalidArgumentException {
		return getPolicyService(policy).updatePolicy(policy);
	}
	
	@RolesAllowed ("admins")
	public IBeneficiary updateBeneficiary(IBeneficiary beneficiary)
	throws ServerInternalErrorException, InvalidArgumentException {
		return getPolicyService(beneficiary).updateBeneficiary(beneficiary);
	}

	@RolesAllowed("admins")
	public IBeneContact updateBeneContact(IBeneContact beneContact)
			throws ServerInternalErrorException, InvalidArgumentException {
		return getPolicyService(beneContact).updateBeneContact(beneContact);
	}

	private IPolicyService getPolicyService(Configrable<String, String> configrable) throws InvalidArgumentException {
		String specialService = configrable.getConfiguration().getParameterValue("specialServiceName");

		if (specialService != null && specialService.trim().length() >0) {
			if ("QuickReturnService".equalsIgnoreCase(specialService)){
				return new PolicyQuickReturnService();
			}				
		}

		return new LocalEJBService();
	}


	private class LocalEJBService implements IPolicyService {

		public IPolicy createPolicy(IPolicy policy)
		throws ServerInternalErrorException,
		EntityAlreadyExistsException, InvalidArgumentException {
			return policyMgmtSession.createPolicy(policy);		
		}

		public IPolicy findPolicyById(IPolicy policy)
		throws EntityNotFoundException, ServerInternalErrorException,
		InvalidArgumentException {
			return policyMgmtSession.findPolicyById(policy);
		}

		public void deletePolicy(IPolicy policy)
		throws ServerInternalErrorException, InvalidArgumentException {
			policyMgmtSession.deletePolicy(policy);

		}

		public IPolicy updatePolicy(IPolicy policy)
		throws ServerInternalErrorException, InvalidArgumentException {
			return policyMgmtSession.updatePolicy(policy);	
		}

		public IBeneficiary createBeneficiary(IBeneficiary beneficiary)
		throws ServerInternalErrorException, EntityAlreadyExistsException,
		InvalidArgumentException {
			return policyMgmtSession.createBeneficiary(beneficiary);
		}

		public void deleteBeneficiary(IBeneficiary beneficiary)
		throws ServerInternalErrorException, InvalidArgumentException {
			policyMgmtSession.deleteBeneficiary(beneficiary);

		}

		public IBeneficiary findBeneficiaryById(IBeneficiary beneficiary)
		throws EntityNotFoundException, ServerInternalErrorException,
		InvalidArgumentException {
			return policyMgmtSession.findBeneficiaryById(beneficiary);
		}

		public IBeneficiary updateBeneficiary(IBeneficiary beneficiary)
		throws ServerInternalErrorException, InvalidArgumentException {
			return policyMgmtSession.updateBeneficiary(beneficiary);
		}

		public IBeneContact createBeneContact(IBeneContact beneContact)
				throws ServerInternalErrorException,
				EntityAlreadyExistsException, InvalidArgumentException {
			return policyMgmtSession.createBeneContact(beneContact);
		}

		public void deleteBeneContact(IBeneContact beneContact)
				throws ServerInternalErrorException, InvalidArgumentException {
			policyMgmtSession.deleteBeneContact(beneContact);
		}

		public IBeneContact findBeneContactById(IBeneContact beneContact)
				throws EntityNotFoundException, ServerInternalErrorException,
				InvalidArgumentException {
			return policyMgmtSession.findBeneContactById(beneContact);
		}

		public IBeneContact updateBeneContact(IBeneContact beneContact)
				throws ServerInternalErrorException, InvalidArgumentException {
			return policyMgmtSession.updateBeneContact(beneContact);
		}

		public List<IPolicy> findHolderPolicies(IAnnuityHolder annuityHolder)
				throws ServerInternalErrorException, InvalidArgumentException {
			return policyMgmtSession.findHolderPolicies(annuityHolder);
		}
		
		public List<IBeneficiary> findHolderBeneficiaries(IAnnuityHolder annuityHolder)
				throws ServerInternalErrorException, InvalidArgumentException {
			return policyMgmtSession.findHolderBeneficiaries(annuityHolder);
		}
		
		public List<String> findHolderBeneficiaryContactEmails(IAnnuityHolder annuityHolder)
				throws ServerInternalErrorException, InvalidArgumentException {
			return policyMgmtSession.findHolderBeneficiaryContactEmails(annuityHolder);
		}

		public IAnnuityHolder findHolderById(IAnnuityHolder holder)
				throws EntityNotFoundException, ServerInternalErrorException,
				InvalidArgumentException {
			return policyMgmtSession.findHolderById(holder);
		}

		public IFund findFundIndexRateFromFundName(IAnnuityHolder holder, String fundName) 
		throws InvalidArgumentException, ServerInternalErrorException {
			return policyMgmtSession.findFundIndexRateFromFundName(holder, fundName);
		}
		
		public List<IFund> findFundsFromState(IAnnuityHolder holder, String state)
		throws InvalidArgumentException, ServerInternalErrorException {
			return policyMgmtSession.findFundsFromState(holder, state);
		}
		
		public List<IPersisteble<?>> customQuery(IAnnuityHolder holder)
		throws InvalidArgumentException, ServerInternalErrorException {
			return policyMgmtSession.customQuery(holder);
		}
	
	}


}

