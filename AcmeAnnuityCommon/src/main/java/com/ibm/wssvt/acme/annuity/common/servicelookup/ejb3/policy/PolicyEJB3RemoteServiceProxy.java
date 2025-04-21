package com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3.policy;

import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneContact;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneficiary;
import com.ibm.wssvt.acme.annuity.common.bean.IFund;
import com.ibm.wssvt.acme.annuity.common.bean.IPersisteble;
import com.ibm.wssvt.acme.annuity.common.bean.IPolicy;
import com.ibm.wssvt.acme.annuity.common.business.IPolicyService;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.policy.PolicyMgmtSvcEJB30;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;



public class PolicyEJB3RemoteServiceProxy implements IPolicyService{

	private PolicyMgmtSvcEJB30 remote;		
	
	public PolicyEJB3RemoteServiceProxy(PolicyMgmtSvcEJB30 remote) {
		super();
		this.remote = remote;
	}
	
	
	public IPolicy createPolicy(IPolicy policy)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		return remote.createPolicy(policy);
	}

	public IPolicy findPolicyById(IPolicy policy)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		return remote.findPolicyById(policy);
	}

	public void deletePolicy(IPolicy policy)
	throws ServerInternalErrorException, InvalidArgumentException {
		remote.deletePolicy(policy);
	}

	public IPolicy updatePolicy(IPolicy policy)
	throws ServerInternalErrorException, InvalidArgumentException {
		return remote.updatePolicy(policy);
	}

	public IBeneficiary createBeneficiary(IBeneficiary beneficiary)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		return remote.createBeneficiary(beneficiary);
	}

	public void deleteBeneficiary(IBeneficiary beneficiary)
			throws ServerInternalErrorException, InvalidArgumentException {
		remote.deleteBeneficiary(beneficiary);
	}

	public IBeneficiary findBeneficiaryById(IBeneficiary beneficiary)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		return remote.findBeneficiaryById(beneficiary);
	}

	public IBeneficiary updateBeneficiary(IBeneficiary beneficiary)
			throws ServerInternalErrorException, InvalidArgumentException {
		return remote.updateBeneficiary(beneficiary);
	}

	public IBeneContact createBeneContact(IBeneContact beneContact)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		return remote.createBeneContact(beneContact);
	}

	public void deleteBeneContact(IBeneContact beneContact)
			throws ServerInternalErrorException, InvalidArgumentException {
		remote.deleteBeneContact(beneContact);
	}

	public IBeneContact findBeneContactById(IBeneContact beneContact)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		return remote.findBeneContactById(beneContact);
	}

	public IBeneContact updateBeneContact(IBeneContact beneContact)
			throws ServerInternalErrorException, InvalidArgumentException {
		return remote.updateBeneContact(beneContact);
	}


	public List<IPolicy> findHolderPolicies(IAnnuityHolder annuityHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		return remote.findHolderPolicies(annuityHolder);
	}
	
	public List<IBeneficiary> findHolderBeneficiaries(IAnnuityHolder annuityHolder)
		throws ServerInternalErrorException, InvalidArgumentException {
		return remote.findHolderBeneficiaries(annuityHolder);
	}
	
	public List<String> findHolderBeneficiaryContactEmails(IAnnuityHolder annuityHolder)
		throws ServerInternalErrorException, InvalidArgumentException {
		return remote.findHolderBeneficiaryContactEmails(annuityHolder);
	}

	public IAnnuityHolder findHolderById(IAnnuityHolder holder)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		return remote.findHolderById(holder);
	}
	
	public IFund findFundIndexRateFromFundName(IAnnuityHolder holder, String fundName) 
	throws InvalidArgumentException, ServerInternalErrorException {
		return remote.findFundIndexRateFromFundName(holder, fundName);
	}
	
	public List<IFund> findFundsFromState(IAnnuityHolder holder, String state)
	throws InvalidArgumentException, ServerInternalErrorException {
		return remote.findFundsFromState(holder, state);
	}

	public List<IPersisteble<?>> customQuery(IAnnuityHolder holder)
			throws InvalidArgumentException, ServerInternalErrorException {
		return remote.customQuery(holder);
	}
}
