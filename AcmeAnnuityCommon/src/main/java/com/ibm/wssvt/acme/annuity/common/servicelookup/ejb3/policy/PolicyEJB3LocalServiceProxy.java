package com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3.policy;

import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneContact;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneficiary;
import com.ibm.wssvt.acme.annuity.common.bean.IFund;
import com.ibm.wssvt.acme.annuity.common.bean.IPersisteble;
import com.ibm.wssvt.acme.annuity.common.bean.IPolicy;
import com.ibm.wssvt.acme.annuity.common.business.IPolicyService;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.policy.PolicyMgmtSvcEJB30Local;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;



public class PolicyEJB3LocalServiceProxy implements IPolicyService{

	private PolicyMgmtSvcEJB30Local local;		
	
	public PolicyEJB3LocalServiceProxy(PolicyMgmtSvcEJB30Local local) {
		super();
		this.local = local;
	}
	
	
	public IPolicy createPolicy(IPolicy policy)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		return local.createPolicy(policy);
	}

	public IPolicy findPolicyById(IPolicy policy)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		return local.findPolicyById(policy);
	}


	public void deletePolicy(IPolicy policy)
			throws ServerInternalErrorException, InvalidArgumentException {
		local.deletePolicy(policy);
	}

	public IPolicy updatePolicy(IPolicy policy)
			throws ServerInternalErrorException, InvalidArgumentException {
		return local.updatePolicy(policy);
	}

	public IBeneficiary createBeneficiary(IBeneficiary beneficiary)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		return local.createBeneficiary(beneficiary);
	}

	public void deleteBeneficiary(IBeneficiary beneficiary)
			throws ServerInternalErrorException, InvalidArgumentException {
		local.deleteBeneficiary(beneficiary);
	}

	public IBeneficiary findBeneficiaryById(IBeneficiary beneficiary)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		return local.findBeneficiaryById(beneficiary);
	}

	public IBeneficiary updateBeneficiary(IBeneficiary beneficiary)
			throws ServerInternalErrorException, InvalidArgumentException {
		return local.updateBeneficiary(beneficiary);
	}

	public IBeneContact createBeneContact(IBeneContact beneContact)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		return local.createBeneContact(beneContact);
	}

	public void deleteBeneContact(IBeneContact beneContact)
			throws ServerInternalErrorException, InvalidArgumentException {
		local.deleteBeneContact(beneContact);
	}

	public IBeneContact findBeneContactById(IBeneContact beneContact)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		return local.findBeneContactById(beneContact);
	}

	public IBeneContact updateBeneContact(IBeneContact beneContact)
			throws ServerInternalErrorException, InvalidArgumentException {
		return local.updateBeneContact(beneContact);
	}

	public List<IPolicy> findHolderPolicies(IAnnuityHolder annuityHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		return local.findHolderPolicies(annuityHolder);
	}
	
	public List<IBeneficiary> findHolderBeneficiaries(IAnnuityHolder annuityHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		return local.findHolderBeneficiaries(annuityHolder);
	}
	
	public List<String> findHolderBeneficiaryContactEmails(IAnnuityHolder annuityHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		return local.findHolderBeneficiaryContactEmails(annuityHolder);
	}

	public IAnnuityHolder findHolderById(IAnnuityHolder holder)
			throws EntityNotFoundException, ServerInternalErrorException,InvalidArgumentException {
		return local.findHolderById(holder);
	}

	public IFund findFundIndexRateFromFundName(IAnnuityHolder holder, String fundName) 
	throws InvalidArgumentException, ServerInternalErrorException {
		return local.findFundIndexRateFromFundName(holder, fundName);
	}
	
	public List<IFund> findFundsFromState(IAnnuityHolder holder, String state)
	throws InvalidArgumentException, ServerInternalErrorException {
		return local.findFundsFromState(holder, state);
	}

	public List<IPersisteble<?>> customQuery(IAnnuityHolder holder)
			throws InvalidArgumentException, ServerInternalErrorException {
		return local.customQuery(holder);
	}
}
