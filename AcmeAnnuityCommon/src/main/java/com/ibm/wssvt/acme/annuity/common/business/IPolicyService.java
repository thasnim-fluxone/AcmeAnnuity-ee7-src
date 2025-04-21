package com.ibm.wssvt.acme.annuity.common.business;
/*
 * PolicyService for the Mgmt and Search functions for Policy
 * rhaque@us.ibm.com
 * 
 */

//import java.rmi.RemoteException;
import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneContact;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneficiary;
import com.ibm.wssvt.acme.annuity.common.bean.IFund;
import com.ibm.wssvt.acme.annuity.common.bean.IPersisteble;
import com.ibm.wssvt.acme.annuity.common.bean.IPolicy;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;


public interface IPolicyService {

	
//mgmt	
	public IPolicy createPolicy(IPolicy policy)
	throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException;
	
	public IBeneficiary createBeneficiary(IBeneficiary beneficiary)
	throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException;
	
	public IBeneContact createBeneContact(IBeneContact beneContact)
	throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException;
	
	public IPolicy updatePolicy(IPolicy policy)
	throws ServerInternalErrorException, InvalidArgumentException;
	
	public IBeneficiary updateBeneficiary(IBeneficiary beneficiary)
	throws ServerInternalErrorException, InvalidArgumentException;
	
	public IBeneContact updateBeneContact(IBeneContact beneContact)
	throws ServerInternalErrorException, InvalidArgumentException;
	
	public void deletePolicy(IPolicy policy) 
	throws ServerInternalErrorException, InvalidArgumentException;
	
	public void deleteBeneficiary(IBeneficiary beneficiary) 
	throws ServerInternalErrorException, InvalidArgumentException;
	
	public void deleteBeneContact(IBeneContact beneContact)
	throws ServerInternalErrorException, InvalidArgumentException;
	
	//search
	public IPolicy findPolicyById(IPolicy policy)
	throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	
	public IBeneficiary findBeneficiaryById(IBeneficiary beneficiary)
	throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	
	public IBeneContact findBeneContactById(IBeneContact beneContact)
	throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;

	public List<IPolicy> findHolderPolicies(IAnnuityHolder annuityHolder)
	throws ServerInternalErrorException, InvalidArgumentException;
	
	public List<IBeneficiary> findHolderBeneficiaries(IAnnuityHolder annuityHolder)
	throws ServerInternalErrorException, InvalidArgumentException;
	
	public List<String> findHolderBeneficiaryContactEmails(IAnnuityHolder annuityHolder)
	throws ServerInternalErrorException, InvalidArgumentException;

	public IAnnuityHolder findHolderById(IAnnuityHolder holder)
	throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	
	public IFund findFundIndexRateFromFundName(IAnnuityHolder holder, String fundName) 
	throws InvalidArgumentException, ServerInternalErrorException;

	public List<IFund> findFundsFromState(IAnnuityHolder holder, String state)
	throws InvalidArgumentException, ServerInternalErrorException;
	
	public List<IPersisteble<?>> customQuery(IAnnuityHolder holder)
	throws InvalidArgumentException, ServerInternalErrorException;
		
}