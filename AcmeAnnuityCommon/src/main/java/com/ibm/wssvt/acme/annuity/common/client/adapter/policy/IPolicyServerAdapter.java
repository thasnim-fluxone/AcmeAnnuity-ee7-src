package com.ibm.wssvt.acme.annuity.common.client.adapter.policy;

import java.rmi.RemoteException;
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
import com.ibm.wssvt.acme.common.adapter.IServerAdapter;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;

public interface IPolicyServerAdapter extends IServerAdapter{
	public IPolicy createPolicy(IPolicy policy)
	throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException, ServerAdapterCommunicationException, RemoteException;
	
	public IPolicy updatePolicy(IPolicy policy)
	throws ServerInternalErrorException, InvalidArgumentException, ServerAdapterCommunicationException, EntityNotFoundException, RemoteException;
	
	public void deletePolicy(IPolicy policy) 
	throws ServerInternalErrorException, InvalidArgumentException, ServerAdapterCommunicationException, EntityNotFoundException, RemoteException;
	
	public IPolicy findPolicyById(IPolicy policy)
	throws EntityNotFoundException, ServerInternalErrorException,InvalidArgumentException, ServerAdapterCommunicationException, RemoteException;
	
	public IBeneficiary createBeneficiary(IBeneficiary beneficiary)
	throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException, ServerAdapterCommunicationException, RemoteException;
	
	public IBeneficiary updateBeneficiary(IBeneficiary beneficiary)
	throws ServerInternalErrorException, InvalidArgumentException, ServerAdapterCommunicationException, EntityNotFoundException, RemoteException;
	
	public void deleteBeneficiary(IBeneficiary beneficiary)
	throws ServerInternalErrorException, InvalidArgumentException, ServerAdapterCommunicationException, EntityNotFoundException, RemoteException;
	
	public IBeneficiary findBeneficiaryById(IBeneficiary beneficiary)
	throws EntityNotFoundException, ServerInternalErrorException,InvalidArgumentException, ServerAdapterCommunicationException, RemoteException;

	public IBeneContact createBeneContact(IBeneContact beneContact)
	throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException, ServerAdapterCommunicationException, RemoteException;
	
	public IBeneContact updateBeneContact(IBeneContact beneContact)
	throws ServerInternalErrorException, InvalidArgumentException, ServerAdapterCommunicationException, EntityNotFoundException, RemoteException;
	
	public void deleteBeneContact(IBeneContact beneContact)
	throws ServerInternalErrorException, InvalidArgumentException, ServerAdapterCommunicationException, EntityNotFoundException, RemoteException;

	public IBeneContact findBeneContactById(IBeneContact beneContact)
	throws EntityNotFoundException, ServerInternalErrorException,InvalidArgumentException, ServerAdapterCommunicationException, RemoteException;

	public List<IPolicy> findHolderPolicies(IAnnuityHolder holder) 
	throws InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException;
	
	public List<IBeneficiary> findHolderBeneficiaries(IAnnuityHolder holder) 
	throws InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException;
	
	public List<String> findHolderBeneficiaryContactEmails(IAnnuityHolder holder) 
	throws InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException;

	public IFund findFundIndexRateFromFundName(IAnnuityHolder holder, String fundName) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException, RemoteException;
	
	public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder)
	throws EntityNotFoundException, ServerInternalErrorException,InvalidArgumentException, ServerAdapterCommunicationException, RemoteException;

	public List <IFund> findFundsFromState(IAnnuityHolder holder, String state) 
	throws InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException;
	
	public List<IPersisteble<?>> customQuery(IAnnuityHolder holder) 
	throws InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException;
}