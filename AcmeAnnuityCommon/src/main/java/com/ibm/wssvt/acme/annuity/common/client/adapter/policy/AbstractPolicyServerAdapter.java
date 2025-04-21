package com.ibm.wssvt.acme.annuity.common.client.adapter.policy;

import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Logger;

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
import com.ibm.wssvt.acme.annuity.common.util.AnnuityLoggerFactory;
import com.ibm.wssvt.acme.common.adapter.AbstractServerAdapter;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
import com.ibm.wssvt.acme.common.log.AcmeLoggerConfig;

public abstract class AbstractPolicyServerAdapter  extends AbstractServerAdapter implements IPolicyServerAdapter{

	private static final long serialVersionUID = 6826107504077969435L;
	protected AcmeLogger getLogger(String loggerName){	
		Logger root = Logger.getLogger(getClientContext().getPrefixedRootLoggerName());		
		AcmeLoggerConfig.setClientFileNamePattern("",
				getClientContext().getClientId(), getClientContext().getThreadId(), this);
		Logger theLogger = AnnuityLoggerFactory.getClientLogger(root, this, getClientContext().getLoggerPrefix()+ loggerName);		
		return new AcmeLogger(theLogger);
			
	}

	
	public  IPolicy createPolicy(IPolicy policy) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, 
		EntityAlreadyExistsException, InvalidArgumentException, RemoteException {				
		IPolicyService service = getPolicyService();
		getServerAdapterEvent().incrumentRequest();
		IPolicy result = service.createPolicy(policy);						
		getServerAdapterEvent().incrumentCreate();
		incrumentRunServerInfo(result);	
		return result;
	}
	
	public IBeneficiary createBeneficiary(IBeneficiary beneficiary)
		throws ServerAdapterCommunicationException, ServerInternalErrorException, 
		EntityAlreadyExistsException, InvalidArgumentException, RemoteException {
		IPolicyService service = getPolicyService();
		getServerAdapterEvent().incrumentRequest();
		IBeneficiary result = service.createBeneficiary(beneficiary);
		getServerAdapterEvent().incrumentCreate();
		incrumentRunServerInfo(result);
		return result;
	}
	
	public IBeneContact createBeneContact(IBeneContact beneContact)
		throws ServerAdapterCommunicationException, ServerInternalErrorException, 
		EntityAlreadyExistsException, InvalidArgumentException, RemoteException {
		IPolicyService service = getPolicyService();
		getServerAdapterEvent().incrumentRequest();
		IBeneContact result = service.createBeneContact(beneContact);
		getServerAdapterEvent().incrumentCreate();
		incrumentRunServerInfo(result);
		return result;
	}

	public  IPolicy findPolicyById(IPolicy policy) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, InvalidArgumentException, RemoteException{			
		IPolicyService service = getPolicyService();			
		getServerAdapterEvent().incrumentRequest();
		IPolicy result = service.findPolicyById(policy);
		getServerAdapterEvent().incrumentRead();
		incrumentRunServerInfo(result);
		return result;
	}
	
	public  IBeneficiary findBeneficiaryById(IBeneficiary beneficiary) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, InvalidArgumentException, RemoteException{			
		IPolicyService service = getPolicyService();			
		getServerAdapterEvent().incrumentRequest();
		IBeneficiary result = service.findBeneficiaryById(beneficiary);
		getServerAdapterEvent().incrumentRead();
		incrumentRunServerInfo(result);
		return result;
	}
	
	public  IBeneContact findBeneContactById(IBeneContact beneContact) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, InvalidArgumentException, RemoteException{			
		IPolicyService service = getPolicyService();			
		getServerAdapterEvent().incrumentRequest();
		IBeneContact result = service.findBeneContactById(beneContact);
		getServerAdapterEvent().incrumentRead();
		incrumentRunServerInfo(result);
		return result;
	}
	
	public IAnnuityHolder findHolderById(IAnnuityHolder holder)
	throws EntityNotFoundException, ServerInternalErrorException,InvalidArgumentException, ServerAdapterCommunicationException, RemoteException {
		IPolicyService service = getPolicyService();			
		getServerAdapterEvent().incrumentRequest();
		IAnnuityHolder result = service.findHolderById(holder);
		getServerAdapterEvent().incrumentRead();
		incrumentRunServerInfo(result);
		return result;	
	}
	public  void deletePolicy(IPolicy policy) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, InvalidArgumentException, RemoteException {
		IPolicyService service = getPolicyService();						
		getServerAdapterEvent().incrumentRequest();
		service.deletePolicy(policy);	
		getServerAdapterEvent().incrumentDelete();		
	}

	public  void deleteBeneficiary(IBeneficiary beneficiary) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, InvalidArgumentException, RemoteException {
		IPolicyService service = getPolicyService();						
		getServerAdapterEvent().incrumentRequest();
		service.deleteBeneficiary(beneficiary);	
		getServerAdapterEvent().incrumentDelete();		
	}
	
	public  void deleteBeneContact(IBeneContact beneContact) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, InvalidArgumentException, RemoteException {
		IPolicyService service = getPolicyService();						
		getServerAdapterEvent().incrumentRequest();
		service.deleteBeneContact(beneContact);	
		getServerAdapterEvent().incrumentDelete();		
	}

	public IPolicy updatePolicy(IPolicy policy) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, InvalidArgumentException, RemoteException {
		IPolicyService service = getPolicyService();	
		getServerAdapterEvent().incrumentRequest();
		IPolicy result = service.updatePolicy(policy);			
		getServerAdapterEvent().incrumentUpdate();
		incrumentRunServerInfo(result);
		return result;		
	}

	public IBeneficiary updateBeneficiary(IBeneficiary beneficiary) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, InvalidArgumentException, RemoteException {
		IPolicyService service = getPolicyService();	
		getServerAdapterEvent().incrumentRequest();
		IBeneficiary result = service.updateBeneficiary(beneficiary);			
		getServerAdapterEvent().incrumentUpdate();
		incrumentRunServerInfo(result);
		return result;		
	}
	
	public IBeneContact updateBeneContact(IBeneContact beneContact) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, InvalidArgumentException, RemoteException {
		IPolicyService service = getPolicyService();	
		getServerAdapterEvent().incrumentRequest();
		IBeneContact result = service.updateBeneContact(beneContact);			
		getServerAdapterEvent().incrumentUpdate();
		incrumentRunServerInfo(result);
		return result;		
	}
	
	public List<IPolicy> findHolderPolicies(IAnnuityHolder annuityHolder) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IPolicyService service = getPolicyService();
		getServerAdapterEvent().incrumentRequest();
		List<IPolicy> results = service.findHolderPolicies(annuityHolder);
		getServerAdapterEvent().incrumentRead();
		return results;

	}
	
	public List<IBeneficiary> findHolderBeneficiaries(IAnnuityHolder annuityHolder) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IPolicyService service = getPolicyService();
		getServerAdapterEvent().incrumentRequest();
		List<IBeneficiary> results = service.findHolderBeneficiaries(annuityHolder);
		getServerAdapterEvent().incrumentRead();
		return results;

	}
	
	public List<String> findHolderBeneficiaryContactEmails(IAnnuityHolder annuityHolder) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IPolicyService service = getPolicyService();
		getServerAdapterEvent().incrumentRequest();
		List<String> results = service.findHolderBeneficiaryContactEmails(annuityHolder);
		getServerAdapterEvent().incrumentRead();
		return results;

	}
	
	public IFund findFundIndexRateFromFundName(IAnnuityHolder holder, String fundName) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException, RemoteException  {
		IPolicyService service = getPolicyService();
		getServerAdapterEvent().incrumentRequest();
		IFund result = service.findFundIndexRateFromFundName(holder, fundName);
		getServerAdapterEvent().incrumentRead();
		return result;

	}
	
	public List <IFund> findFundsFromState(IAnnuityHolder holder, String state) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException, RemoteException  {
		IPolicyService service = getPolicyService();
		getServerAdapterEvent().incrumentRequest();
		List <IFund> results = service.findFundsFromState(holder, state);
		getServerAdapterEvent().incrumentRead();
		return results;

	}
	
	public List <IPersisteble<?>> customQuery(IAnnuityHolder holder) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException, RemoteException  {
		IPolicyService service = getPolicyService();
		getServerAdapterEvent().incrumentRequest();
		List <IPersisteble<?>> results = service.customQuery(holder);
		getServerAdapterEvent().incrumentRead();
		return results;

	}
	
	private void incrumentRunServerInfo(Configrable<String, String> configrable) {	
		if (configrable != null && configrable.getConfiguration() != null) {
			if(configrable.getConfiguration().getParameterValue("internal.runningOnServerInfo") != null) {
				String[] servers = configrable.getConfiguration().getParameterValue("internal.runningOnServerInfo").split(";;");
				getServerAdapterEvent().incrumentRunServerCount(servers[0]);
				for(int i=0; i<servers.length; i++) {
					getServerAdapterEvent().incrumentOverallRunServerCount(servers[i]);
				}
			}
			configrable.getConfiguration().removeParameter("internal.runningOnServerInfo");
		}
	}
	
		
	protected abstract IPolicyService getPolicyService() throws ServerAdapterCommunicationException;
}