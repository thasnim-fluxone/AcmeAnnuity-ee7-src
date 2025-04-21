package com.ibm.wssvt.acme.annuity.common.client.adapter.policy;

import javax.naming.NamingException;

import com.ibm.wssvt.acme.annuity.common.business.IPolicyService;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.policy.PolicyMgmtSvcEJB30;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.policy.PolicyMgmtSvcEJB30Local;
import com.ibm.wssvt.acme.annuity.common.exception.AnnuitySecurityException;
import com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3.policy.PolicyEJB3ServiceLookup;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.exception.InvalidConfigurationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/**
 * $Rev: 807 $
 * $Date: 2007-09-24 01:29:31 -0500 (Mon, 24 Sep 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class PolicyEJB30ServerAdapter extends AbstractPolicyServerAdapter{
		
	private static final long serialVersionUID = -7761738176018995756L;

		
	protected IPolicyService getPolicyService() throws ServerAdapterCommunicationException {
		IPolicyService proxy = null;
		AcmeLogger logger = getLogger(getClass().getName());
		PolicyMgmtSvcEJB30 svcRemote = null;
		PolicyMgmtSvcEJB30Local svcLocal = null;
		if ("injectionWithRemote".equalsIgnoreCase(getConfiguration().getParameterValue("connectionMode"))){
			logger.fine("EJB Adapter - Using Injection");
			svcRemote = 
				(PolicyMgmtSvcEJB30) getClientContext().getInjectedObjects().get("PolicyMgmtSvcEJB30");
			if (svcRemote == null) {
				logger.severe("EJB Adapter was configured to use injection (annotations).  " +
						"However the injected value is null.  Object is: PolicyMgmtSvcEJB30");
				throw new ServerAdapterCommunicationException("Failed to get the Server Service Interface.  " +
						"The injected value of PolicyMgmtSvcRemote is null.  " +
						"Make sure that the injection configuration for PolicyMgmtSvcRemote is accurate.");						
			}
			logger.finer("svcRemote via injection was found.  value is: " + svcRemote);			
		} else if ("injectionWithLocal".equalsIgnoreCase(getConfiguration().getParameterValue("connectionMode"))){
			logger.fine("EJB Adapter - Using Injection with Local");
			svcLocal = 
				(PolicyMgmtSvcEJB30Local) getClientContext().getInjectedObjects().get("PolicyMgmtSvcEJB30Local");
			if (svcLocal== null) {
				logger.severe("EJB Adapter was configured to use injection.  " +
						"However the injected value is null.  Object is: AnnuityMgmtSvcEJB30Local");
				throw new ServerAdapterCommunicationException("Failed to get the Server Service Interface.  " +
						"The injected value of PolicyMgmtSvcEJB30Local is null.  " +
						"Make sure that the injection configuration for PolicyMgmtSvcEJB30Local is accurate.");						
			}		
			logger.finer("svcLocal via injection was found.  value is: " + svcLocal);										
		}
		
		try {				
			proxy = PolicyEJB3ServiceLookup.getPolicyEJB3Service(svcRemote, svcLocal, this, logger);
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
