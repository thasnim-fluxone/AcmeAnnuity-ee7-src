package com.ibm.wssvt.acme.annuity.common.client.adapter;

import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Logger;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
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


/**
 * $Rev: 672 $
 * $Date: 2007-08-16 17:35:23 -0500 (Thu, 16 Aug 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public abstract class AbstractAnnuityServerAdapter  extends AbstractServerAdapter implements IAnnuityServerAdapter{

	private static final long serialVersionUID = 6826107504077969435L;
	protected AcmeLogger getLogger(String loggerName){	
		Logger root = Logger.getLogger(getClientContext().getPrefixedRootLoggerName());		
		AcmeLoggerConfig.setClientFileNamePattern("",
				getClientContext().getClientId(), getClientContext().getThreadId(), this);
		Logger theLogger = AnnuityLoggerFactory.getClientLogger(root, this, getClientContext().getLoggerPrefix()+ loggerName);		
		return new AcmeLogger(theLogger);
		/*
		Logger logger = Logger.getLogger(getClientContext().getLoggerPrefix()+ loggerName);
		logger.setParent(super.getClientContext().getRootLogger());		
		logger.setLevel(super.getClientContext().getRootLogger().getLevel());		
		return logger;
		*/		
	}

	
	public  IAnnuity createAnnuity(IAnnuity annuity) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, 
		EntityAlreadyExistsException, InvalidArgumentException, RemoteException {				
		IAnnuityService service = getAnnuityService();
		getServerAdapterEvent().incrumentRequest();
		IAnnuity result = service.createAnnuity(annuity);						
		getServerAdapterEvent().incrumentCreate();
		incrumentRunServerInfo(result);	
		return result;
	}


	public  IAnnuity findAnnuityById(IAnnuity annuity) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, InvalidArgumentException, RemoteException{			
		IAnnuityService service = getAnnuityService();			
		getServerAdapterEvent().incrumentRequest();
		IAnnuity result = service.findAnnuityById(annuity);
		getServerAdapterEvent().incrumentRead();
		incrumentRunServerInfo(result);
		return result;
	
	}

	public  void deleteAnnuityById(IAnnuity annuity) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, InvalidArgumentException, RemoteException {
		IAnnuityService service = getAnnuityService();						
		getServerAdapterEvent().incrumentRequest();
		service.deleteAnnuity(annuity);	
		getServerAdapterEvent().incrumentDelete();		
	}
	
	public IAnnuity updateAnnuity(IAnnuity annuity) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, InvalidArgumentException, RemoteException {
		IAnnuityService service = getAnnuityService();	
		getServerAdapterEvent().incrumentRequest();
		IAnnuity result = service.updateAnnuity(annuity);			
		getServerAdapterEvent().incrumentUpdate();
		incrumentRunServerInfo(result);
		return result;		
	}
	
	public IAnnuityHolder createAnnuityHolder(IAnnuityHolder annHolder) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException, RemoteException {
		IAnnuityService service = getAnnuityService();
		getServerAdapterEvent().incrumentRequest();
		IAnnuityHolder result = service.createAnnuityHolder(annHolder);			
		getServerAdapterEvent().incrumentCreate();
		incrumentRunServerInfo(result);	
		return result;
	}

	public IContact createContact(IContact contact) throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException, RemoteException {
		IAnnuityService service = getAnnuityService();
		//System.out.print("testing with service: " +service.getClass());
		getServerAdapterEvent().incrumentRequest();
		IContact result = service.createContact(contact);			
		getServerAdapterEvent().incrumentCreate();
		incrumentRunServerInfo(result);
		return result;

	}

	public IPayor createPayor(IPayor payor) throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();
		getServerAdapterEvent().incrumentRequest();
		IPayor result = service.createPayor(payor);		
		getServerAdapterEvent().incrumentCreate();
		incrumentRunServerInfo(result);
		return result;

	}

	public void deleteAnnuityHolder(IAnnuityHolder annHolder) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();
		getServerAdapterEvent().incrumentRequest();
		service.deleteAnnuityHolder(annHolder);			
		getServerAdapterEvent().incrumentDelete();
	}

	public void deleteContact(IContact contact) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();
		getServerAdapterEvent().incrumentRequest();
		service.deleteContact(contact);			
		getServerAdapterEvent().incrumentDelete();		
	}

	public void deletePayor(IPayor payor) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();
		getServerAdapterEvent().incrumentRequest();
		service.deletePayor(payor);			
		getServerAdapterEvent().incrumentDelete();		
	}

	public IAnnuityHolder findAnnuityHolder(IAnnuity annuity) throws ServerAdapterCommunicationException, EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();						
		getServerAdapterEvent().incrumentRequest();
		IAnnuityHolder result = service.findAnnuityHolder(annuity);
		getServerAdapterEvent().incrumentRead();
		incrumentRunServerInfo(result);
		return result;
	}

	public IContact findContactById(IContact contact) throws ServerAdapterCommunicationException, EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();
		getServerAdapterEvent().incrumentRequest();
		IContact result = service.findContactById(contact);
		getServerAdapterEvent().incrumentRead();
		incrumentRunServerInfo(result);
		return result;

	}

	public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();
		getServerAdapterEvent().incrumentRequest();
		List<IAnnuity> results = service.findHolderAnnuities(annuityHolder);
		getServerAdapterEvent().incrumentRead();
		return results;

	}

	public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder) throws ServerAdapterCommunicationException, EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();					
		getServerAdapterEvent().incrumentRequest();
		IAnnuityHolder result = service.findHolderById(annuityHolder);		
		getServerAdapterEvent().incrumentRead();
		incrumentRunServerInfo(result);
		return result;

	}

	
	public IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annHolder) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();
		getServerAdapterEvent().incrumentRequest();
		IAnnuityHolder result = service.updateAnnuityHolder(annHolder);
		getServerAdapterEvent().incrumentUpdate();
		incrumentRunServerInfo(result);
		return result;

	}

	public IContact updateContact(IContact contact) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();
		getServerAdapterEvent().incrumentRequest();
		IContact result = service.updateContact(contact);
		getServerAdapterEvent().incrumentUpdate();
		incrumentRunServerInfo(result);
		return result;

	}

	public IPayor updatePayor(IPayor payor) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();
		getServerAdapterEvent().incrumentRequest();
		IPayor result = service.updatePayor(payor);
		getServerAdapterEvent().incrumentUpdate();
		incrumentRunServerInfo(result);
		return result;

	}

	public IPayor findPayorById(IPayor payor) throws ServerAdapterCommunicationException, EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();
		getServerAdapterEvent().incrumentRequest();
		IPayor result = service.findPayorById(payor);
		getServerAdapterEvent().incrumentRead();
		incrumentRunServerInfo(result);
		return result;
	}

	public IPayout findPayoutById(IPayout payout) throws ServerAdapterCommunicationException, EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();		
		getServerAdapterEvent().incrumentRequest();
		IPayout result = service.findPayoutById(payout);
		getServerAdapterEvent().incrumentRead();
		incrumentRunServerInfo(result);
		return result;
	}

	public IRider findRiderById(IRider rider) throws ServerAdapterCommunicationException, EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();					
		getServerAdapterEvent().incrumentRequest();
		IRider result = service.findRiderById(rider);
		getServerAdapterEvent().incrumentRead();
		incrumentRunServerInfo(result);
		return result;	
	}
	public List<IAnnuity> findPayorAnnuities(IPayor payor) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();
		getServerAdapterEvent().incrumentRequest();
		List<IAnnuity> results = service.findPayorAnnuities(payor);
		getServerAdapterEvent().incrumentRead();
		return results;

	}
	
	public IPayout createPayout(IPayout payout) throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();
		getServerAdapterEvent().incrumentRequest();
		IPayout result = service.createPayout(payout);
		getServerAdapterEvent().incrumentCreate();
		incrumentRunServerInfo(result);
		return result;
	}

	public IPayout updatePayout(IPayout payout) throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();		
		getServerAdapterEvent().incrumentRequest();
		IPayout result = service.updatePayout(payout);
		getServerAdapterEvent().incrumentUpdate();
		incrumentRunServerInfo(result);
		return result;
	}

	public void deletePayout(IPayout payout) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();			
		getServerAdapterEvent().incrumentRequest();
		service.deletePayout(payout);
		getServerAdapterEvent().incrumentDelete();
	}

	public void deleteRider(IRider rider) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
		IAnnuityService service = getAnnuityService();
		getServerAdapterEvent().incrumentRequest();
		service.deleteRider(rider);
		getServerAdapterEvent().incrumentDelete();		
	}

	private void incrumentRunServerInfo(Configrable<String, String> configrable) {	
		if (configrable != null &&
				configrable.getConfiguration() != null && 
				configrable.getConfiguration().getParameterValue("internal.runningOnServerInfo") != null) {
			String[] servers = configrable.getConfiguration().getParameterValue("internal.runningOnServerInfo").split(";;");
			getServerAdapterEvent().incrumentRunServerCount(servers[0]);
			for(int i=0; i<servers.length; i++) {
				getServerAdapterEvent().incrumentOverallRunServerCount(servers[i]);
			}
			synchronized (configrable) {
			    configrable.getConfiguration().addParameter("internal.runningOnServerInfo", "");    
            }
		}
	}
	protected abstract IAnnuityService getAnnuityService() throws ServerAdapterCommunicationException;


}
