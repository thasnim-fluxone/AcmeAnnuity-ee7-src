package com.ibm.wssvt.acme.annuity.common.business.nonbusinessimpl;
/*
 * QuickReturn Service for Policy
 * rhaque@us.ibm.com
 */



import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneContact;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneficiary;
import com.ibm.wssvt.acme.annuity.common.bean.IFund;
import com.ibm.wssvt.acme.annuity.common.bean.IPersisteble;
import com.ibm.wssvt.acme.annuity.common.bean.IPolicy;
import com.ibm.wssvt.acme.annuity.common.business.IPolicyService;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;

public class PolicyQuickReturnService implements IPolicyService {
	
	public IPolicy createPolicy(IPolicy policy)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		return policy;
	}

	public IPolicy findPolicyById(IPolicy policy)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {		
		return policy;
	}


	public void deletePolicy(IPolicy policy)
			throws ServerInternalErrorException, InvalidArgumentException {		
		
	}

	public IPolicy updatePolicy(IPolicy policy)
			throws ServerInternalErrorException, InvalidArgumentException {		
		return policy;
	}

	public IBeneficiary createBeneficiary(IBeneficiary beneficiary)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		return beneficiary;
	}

	public void deleteBeneficiary(IBeneficiary beneficiary)
			throws ServerInternalErrorException, InvalidArgumentException {
		return;
	}

	public IBeneficiary findBeneficiaryById(IBeneficiary beneficiary)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		return beneficiary;
	}

	public IBeneficiary updateBeneficiary(IBeneficiary beneficiary)
			throws ServerInternalErrorException, InvalidArgumentException {
		return beneficiary;
	}

	public IBeneContact createBeneContact(IBeneContact beneContact)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		return beneContact;
	}

	public void deleteBeneContact(IBeneContact beneContact)
			throws ServerInternalErrorException, InvalidArgumentException {
		return;
	}

	public IBeneContact findBeneContactById(IBeneContact beneContact)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		return beneContact;
	}

	public IBeneContact updateBeneContact(IBeneContact beneContact)
			throws ServerInternalErrorException, InvalidArgumentException {
		return beneContact;
	}

	public List<IPolicy> findHolderPolicies(IAnnuityHolder annuityHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		return null;
	}
	
	public List<IBeneficiary> findHolderBeneficiaries(IAnnuityHolder annuityHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		return null;
	}
	
	public List<String> findHolderBeneficiaryContactEmails(IAnnuityHolder annuityHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		return null;
	}

	public IAnnuityHolder findHolderById(IAnnuityHolder holder)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		return holder;
	}

	public IFund findFundIndexRateFromFundName(IAnnuityHolder holder, String fundName) 
	throws InvalidArgumentException, ServerPersistenceModuleException {
		return null;
	}

	public List<IFund> findFundsFromState(IAnnuityHolder holder, String state)
			throws InvalidArgumentException, ServerInternalErrorException {
		return null;
	}
	
	public List<IPersisteble<?>> customQuery(IAnnuityHolder holder)
			throws InvalidArgumentException, ServerInternalErrorException {
		return null;
}
}
