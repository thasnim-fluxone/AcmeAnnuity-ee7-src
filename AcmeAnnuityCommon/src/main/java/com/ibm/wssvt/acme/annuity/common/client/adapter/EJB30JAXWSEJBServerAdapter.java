package com.ibm.wssvt.acme.annuity.common.client.adapter;

import javax.naming.NamingException;

import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.business.ejbws.ejb3jaxws.AnnuityMgmtSvcEJB30JAXWS;
import com.ibm.wssvt.acme.annuity.common.business.ejbws.ejb3jaxws.AnnuityMgmtSvcEJB30JAXWSLocal;
import com.ibm.wssvt.acme.annuity.common.exception.AnnuitySecurityException;
import com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3jaxws.EJB3JAXWSEJBLocalServiceProxy;
import com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3jaxws.EJB3JAXWSEJBRemoteServiceProxy;
import com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3jaxws.EJB3JAXWSEJBServiceLookup;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.exception.InvalidConfigurationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/**
 * @author malbedaiwi
 */
public class EJB30JAXWSEJBServerAdapter extends AbstractAnnuityServerAdapter{
		
	private static final long serialVersionUID = -7761738176018995756L;

		
	protected IAnnuityService getAnnuityService() throws ServerAdapterCommunicationException {
		IAnnuityService proxy = null;
		AcmeLogger logger = getLogger(getClass().getName());
		if ("injectionWithRemote".equalsIgnoreCase(getConfiguration().getParameterValue("connectionMode"))){
			logger.fine("EJB Adapter - Using Injection");			
			AnnuityMgmtSvcEJB30JAXWS svcRemote = 
				(AnnuityMgmtSvcEJB30JAXWS) getClientContext().getInjectedObjects().get("AnnuityMgmtSvcEJB30JAXWS");
			proxy = new EJB3JAXWSEJBRemoteServiceProxy(svcRemote);
			logger.finer("svcRemote via injection was found.  value is: " + svcRemote);
			if (svcRemote == null) {
				logger.severe("EJB Adapter was configured to use injection (annotations).  " +
						"However the injected value is null.  Object is: AnnuityMgmtSvcEJB30JAXWS");
				throw new ServerAdapterCommunicationException("Failed to get the Server Service Interface.  " +
						"The injected value of AnnuityMgmtSvcEJB30JAXWS is null.  " +
						"Make sure that the injection configuration for AnnuityMgmtSvcEJB30JAXWS is accurate.");						
			}		
			
		} else if ("injectionWithLocal".equalsIgnoreCase(getConfiguration().getParameterValue("connectionMode"))){
			logger.fine("EJB Adapter - Using Injection with Local");
			AnnuityMgmtSvcEJB30JAXWSLocal svcLocal = 
				(AnnuityMgmtSvcEJB30JAXWSLocal) getClientContext().getInjectedObjects().get("AnnuityMgmtSvcEJB30JAXWSLocal");
			proxy = new EJB3JAXWSEJBLocalServiceProxy(svcLocal);
			logger.finer("svcLocal via injection was found.  value is: " + svcLocal);
			if (svcLocal== null) {
				logger.severe("EJB Adapter was configured to use injection.  " +
						"However the injected value is null.  Object is: AnnuityMgmtSvcEJB30Local");
				throw new ServerAdapterCommunicationException("Failed to get the Server Service Interface.  " +
						"The injected value of AnnuityMgmtSvcEJB30Local is null.  " +
						"Make sure that the injection configuration for AnnuityMgmtSvcEJB30Local is accurate.");						
			}		
			
		}
		else{
			try {
				proxy = EJB3JAXWSEJBServiceLookup.getAnnuityEJB3JAXWSEJBService(this, logger);
			} catch (InvalidConfigurationException e) {
				throw new ServerAdapterCommunicationException(e.getMessage(), e);
			} catch (NamingException e) {
				throw new ServerAdapterCommunicationException(e.getMessage(), e);
			} catch (AnnuitySecurityException e) {
				throw new ServerAdapterCommunicationException(e.getMessage(), e);
			}
		}
				
		return proxy;
	}
		
}
