package com.ibm.wssvt.acme.annuity.common.client.adapter;

import javax.naming.NamingException;

import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.AnnuityMgmtSvcEJB30;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.AnnuityMgmtSvcEJB30Local;
import com.ibm.wssvt.acme.annuity.common.exception.AnnuitySecurityException;
import com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3.EJB3ServiceLookup;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.exception.InvalidConfigurationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/**
 * $Rev: 807 $
 * $Date: 2007-09-24 01:29:31 -0500 (Mon, 24 Sep 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class EJB30ServerAdapter extends AbstractAnnuityServerAdapter{
		
	private static final long serialVersionUID = -7761738176018995756L;

		
	protected IAnnuityService getAnnuityService() throws ServerAdapterCommunicationException {
		IAnnuityService proxy = null;
		AcmeLogger logger = getLogger(getClass().getName());
		AnnuityMgmtSvcEJB30 svcRemote = null;
		AnnuityMgmtSvcEJB30Local svcLocal = null;
		if ("injectionWithRemote".equalsIgnoreCase(getConfiguration().getParameterValue("connectionMode"))){
			logger.fine("EJB Adapter - Using Injection");
			svcRemote = 
				(AnnuityMgmtSvcEJB30) getClientContext().getInjectedObjects().get("AnnuityMgmtSvcEJB30");
			if (svcRemote == null) {
				logger.severe("EJB Adapter was configured to use injection (annotations).  " +
						"However the injected value is null.  Object is: AnnuityMgmtSvcEJB30");
				throw new ServerAdapterCommunicationException("Failed to get the Server Service Interface.  " +
						"The injected value of annuityMgmtSvcRemote is null.  " +
						"Make sure that the injection configuration for annuityMgmtSvcRemote is accurate.");						
			}
			logger.finer("svcRemote via injection was found.  value is: " + svcRemote);			
		} else if ("injectionWithLocal".equalsIgnoreCase(getConfiguration().getParameterValue("connectionMode"))){
			logger.fine("EJB Adapter - Using Injection with Local");
			svcLocal = 
				(AnnuityMgmtSvcEJB30Local) getClientContext().getInjectedObjects().get("AnnuityMgmtSvcEJB30Local");
			if (svcLocal== null) {
				logger.severe("EJB Adapter was configured to use injection.  " +
						"However the injected value is null.  Object is: AnnuityMgmtSvcEJB30Local");
				throw new ServerAdapterCommunicationException("Failed to get the Server Service Interface.  " +
						"The injected value of AnnuityMgmtSvcEJB30Local is null.  " +
						"Make sure that the injection configuration for AnnuityMgmtSvcEJB30Local is accurate.");						
			}		
			logger.finer("svcLocal via injection was found.  value is: " + svcLocal);										
		}
		
		try {				
			proxy = EJB3ServiceLookup.getAnnuityEJB3Service(svcRemote, svcLocal, this, logger);
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
