//***********************************************************
// The EJB31SingletonServerAdapter adapter is for testing 
// @Singleton Mbean support in EJB 3.1.
//
// Randy Erickson 07/02/2009
// Amirhossein Kiani 09/28/2009 - Added the ConfigData Map store configuration values
//                               (tests concurrency).
//***********************************************************

package com.ibm.wssvt.acme.annuity.common.client.adapter;

import java.util.logging.Logger;

import javax.naming.NamingException;

import com.ibm.wssvt.acme.annuity.common.business.IAnnuitySingletonService;
import com.ibm.wssvt.acme.annuity.common.business.ejb31.AnnuityMgmtSingletonSvcEJB31;
import com.ibm.wssvt.acme.annuity.common.business.ejb31.AnnuityMgmtSingletonSvcEJB31Local;
import com.ibm.wssvt.acme.annuity.common.exception.AnnuitySecurityException;
import com.ibm.wssvt.acme.annuity.common.servicelookup.ejb31.EJB31SingletonServiceLookup;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityLoggerFactory;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.exception.InvalidConfigurationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
import com.ibm.wssvt.acme.common.log.AcmeLoggerConfig;


public class EJB31SingletonServerAdapter extends AbstractSingletonServerAdapter {
	
	private static final long serialVersionUID = -151248388793589559L;

	protected IAnnuitySingletonService getAnnuityService() throws ServerAdapterCommunicationException {
		IAnnuitySingletonService proxy = null;
		AcmeLogger logger = getLogger(getClass().getName());
		AnnuityMgmtSingletonSvcEJB31 svcRemote = null;
		AnnuityMgmtSingletonSvcEJB31Local svcLocal = null;
		if ("injectionWithRemote".equalsIgnoreCase(getConfiguration().getParameterValue("connectionMode"))){
			logger.fine("EJB Adapter - Using Injection");
			svcRemote = 
				(AnnuityMgmtSingletonSvcEJB31) getClientContext().getInjectedObjects().get("AnnuityMgmtSingletonSvcEJB31");
			if (svcRemote == null) {
				logger.severe("EJB Adapter was configured to use injection (annotations).  " +
						"However the injected value is null.  Object is: AnnuityMgmtSingletonSvcEJB31");
				throw new ServerAdapterCommunicationException("Failed to get the Server Service Interface.  " +
						"The injected value of annuityMgmtSvcRemote is null.  " +
						"Make sure that the injection configuration for annuityMgmtSvcRemote is accurate.");						
			}
			logger.finer("svcRemote via injection was found.  value is: " + svcRemote);			
		} else if ("injectionWithLocal".equalsIgnoreCase(getConfiguration().getParameterValue("connectionMode"))){
			logger.fine("EJB Adapter - Using Injection with Local");
			svcLocal = 
				(AnnuityMgmtSingletonSvcEJB31Local) getClientContext().getInjectedObjects().get("AnnuityMgmtSingletonSvcEJB31Local");
			if (svcLocal== null) {
				logger.severe("EJB Adapter was configured to use injection.  " +
						"However the injected value is null.  Object is: AnnuityMgmtSingletonSvcEJB31Local");
				throw new ServerAdapterCommunicationException("Failed to get the Server Service Interface.  " +
						"The injected value of AnnuityMgmtSingletonSvcEJB31Local is null.  " +
						"Make sure that the injection configuration for AnnuityMgmtSingletonSvcEJB31Local is accurate.");						
			}		
			logger.finer("svcLocal via injection was found.  value is: " + svcLocal);										
		}
		
		try {				
			proxy = EJB31SingletonServiceLookup.getAnnuityEJB31Service(svcRemote, svcLocal, this, logger);
		} catch (InvalidConfigurationException e) {
			throw new ServerAdapterCommunicationException(e.getMessage(), e);
		} catch (NamingException e) {
			throw new ServerAdapterCommunicationException(e.getMessage(), e);
		} catch (AnnuitySecurityException e) {
			throw new ServerAdapterCommunicationException(e.getMessage(), e);
		}					
		return proxy;
	}
		
	protected AcmeLogger getLogger(String loggerName){	
		Logger root = Logger.getLogger(getClientContext().getPrefixedRootLoggerName());		
		AcmeLoggerConfig.setClientFileNamePattern("",
				getClientContext().getClientId(), getClientContext().getThreadId(), this);
		Logger theLogger = AnnuityLoggerFactory.getClientLogger(root, this, getClientContext().getLoggerPrefix()+ loggerName);		
		return new AcmeLogger(theLogger);
	}


}
