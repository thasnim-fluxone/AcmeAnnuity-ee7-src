package com.ibm.wssvt.acme.annuity.common.client.adapter;

import javax.naming.NamingException;

import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.business.ejb31.AnnuityMgmtAsynchSvcEJB31;
import com.ibm.wssvt.acme.annuity.common.business.ejb31.AnnuityMgmtAsynchSvcEJB31Local;
import com.ibm.wssvt.acme.annuity.common.exception.AnnuitySecurityException;
import com.ibm.wssvt.acme.annuity.common.servicelookup.ejb31.EJB31AsynchServiceLookup;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.exception.InvalidConfigurationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;


public class EJB31AsynchServerAdapter extends AbstractAnnuityServerAdapter {
	
	private static final long serialVersionUID = 421549961113184896L;

	protected IAnnuityService getAnnuityService() throws ServerAdapterCommunicationException {
		IAnnuityService proxy = null;
		AcmeLogger logger = getLogger(getClass().getName());
		AnnuityMgmtAsynchSvcEJB31 svcRemote = null;
		AnnuityMgmtAsynchSvcEJB31Local svcLocal = null;
		if ("injectionWithRemote".equalsIgnoreCase(getConfiguration().getParameterValue("connectionMode"))){
			logger.fine("EJB Adapter - Using Injection");
			svcRemote = 
				(AnnuityMgmtAsynchSvcEJB31) getClientContext().getInjectedObjects().get("AnnuityMgmtAsynchSvcEJB31");
			if (svcRemote == null) {
				logger.severe("EJB Adapter was configured to use injection (annotations).  " +
						"However the injected value is null.  Object is: AnnuityMgmtAsynchSvcEJB31");
				throw new ServerAdapterCommunicationException("Failed to get the Server Service Interface.  " +
						"The injected value of annuityMgmtSvcRemote is null.  " +
						"Make sure that the injection configuration for annuityMgmtSvcRemote is accurate.");						
			}
			logger.finer("svcRemote via injection was found.  value is: " + svcRemote);			
		} else if ("injectionWithLocal".equalsIgnoreCase(getConfiguration().getParameterValue("connectionMode"))){
			logger.fine("EJB Adapter - Using Injection with Local");
			svcLocal = 
				(AnnuityMgmtAsynchSvcEJB31Local) getClientContext().getInjectedObjects().get("AnnuityMgmtAsynchSvcEJB31Local");
			if (svcLocal== null) {
				logger.severe("EJB Adapter was configured to use injection.  " +
						"However the injected value is null.  Object is: AnnuityMgmtAsynchSvcEJB31Local");
				throw new ServerAdapterCommunicationException("Failed to get the Server Service Interface.  " +
						"The injected value of AnnuityMgmtAsynchSvcEJB31Local is null.  " +
						"Make sure that the injection configuration for AnnuityMgmtAsynchSvcEJB31Local is accurate.");						
			}		
			logger.finer("svcLocal via injection was found.  value is: " + svcLocal);										
		}
		
		try {				
			proxy = EJB31AsynchServiceLookup.getAnnuityEJB31Service(svcRemote, svcLocal, this, logger);
		} catch (InvalidConfigurationException e) {
			throw new ServerAdapterCommunicationException(e.getMessage(), e);
		} catch (NamingException e) {
			throw new ServerAdapterCommunicationException(e.getMessage(), e);
		} catch (AnnuitySecurityException e) {
			throw new ServerAdapterCommunicationException(e.getMessage(), e);
		}					
		return proxy;
	}
		
}
