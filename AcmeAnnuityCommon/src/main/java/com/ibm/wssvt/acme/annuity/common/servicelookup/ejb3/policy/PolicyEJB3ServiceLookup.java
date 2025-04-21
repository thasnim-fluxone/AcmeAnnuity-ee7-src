package com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3.policy;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import com.ibm.wssvt.acme.annuity.common.business.IPolicyService;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.policy.PolicyMgmtSvcEJB30;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.policy.PolicyMgmtSvcEJB30Local;
import com.ibm.wssvt.acme.annuity.common.exception.AnnuitySecurityException;
//import com.ibm.wssvt.acme.annuity.common.util.AnnuityKerberosProperties;
//import com.ibm.wssvt.acme.annuity.common.util.AnnuityKerberosSecurity;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.exception.ExceptionFormatter;
import com.ibm.wssvt.acme.common.exception.InvalidConfigurationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class PolicyEJB3ServiceLookup {

	public static IPolicyService getPolicyEJB3Service(PolicyMgmtSvcEJB30 injectedRemote,
			PolicyMgmtSvcEJB30Local injectedLocal,	Configrable<String, String> configrable, AcmeLogger logger) 
	throws InvalidConfigurationException, NamingException, AnnuitySecurityException {
		String mode = configrable.getConfiguration().getParameterValue("connectionMode");
		if ("injectionWithLocal".equalsIgnoreCase(mode)) {
			return new PolicyEJB3LocalServiceProxy(injectedLocal);
		}else if ("local".equalsIgnoreCase(mode)) {
			return new PolicyEJB3LocalServiceProxy(getPolicyEJB3LocalService(configrable, logger));
		}else if ("remote".equalsIgnoreCase(mode)){
			return new PolicyEJB3RemoteServiceProxy (getPolicyEJB3RemoteService(configrable, logger));
		}else if ("injectionWithRemote".equalsIgnoreCase(mode)){
			return new PolicyEJB3RemoteServiceProxy (injectedRemote);
		}else{			
			logger.info("The connectionMode configuration is not valid.  " +
					"Supplied value is: " + mode );
			throw new InvalidConfigurationException ("The connectionMode configuration is not valid.  " +
					"Supplied value is: " + mode + logger.getAllLogs());
		}	
	}
	
	public static PolicyMgmtSvcEJB30 getPolicyEJB3RemoteService(Configrable<String, String> configrable, AcmeLogger logger) throws NamingException, AnnuitySecurityException  {
		PolicyMgmtSvcEJB30 svcRemote = null;
		//fixme: check for null properties
		try {			
//			if ("true".equalsIgnoreCase(configrable.getConfiguration().getParameterValue("useKerberosSecurity"))){						
//				AnnuityKerberosProperties akp = new AnnuityKerberosProperties();
//				akp.setKerbProperties(configrable, logger);															
//				AnnuityKerberosSecurity annuityKerberosSecurity = new AnnuityKerberosSecurity();
//				if (AnnuityKerberosProperties.LOGIN_STYLE_NEW_TICKET.equalsIgnoreCase(akp.getLoginStyle())){
//					annuityKerberosSecurity.login(akp, logger);												
//				}else if (AnnuityKerberosProperties.LOGIN_STYLE_NEW_OR_REFRESH_TICKET.equalsIgnoreCase(akp.getLoginStyle())){
//					annuityKerberosSecurity.renewLogin(akp, logger);
//				}						
//			}														
			Properties p =new Properties();
			p.put(Context.INITIAL_CONTEXT_FACTORY, configrable.getConfiguration().getParameterValue("initialContextFactory"));
			String iiopAddress = configrable.getConfiguration().getParameterValue("iiopAddress");
			p.put(Context.PROVIDER_URL, iiopAddress);					
			logger.fine("supplied iiop address is: " + iiopAddress);
			InitialContext ic = new InitialContext(p);
			logger.fine("obtained initial context: " + ic);
			Object ref = ic.lookup(configrable.getConfiguration().getParameterValue("ejb3PolicyRemoteJNDI"));
			svcRemote = (PolicyMgmtSvcEJB30) PortableRemoteObject.narrow(ref, PolicyMgmtSvcEJB30.class);
			logger.fine("svcRemote was found.  value is: " + svcRemote);						
			
		}catch (NamingException e) {				
			String msg = "Failed to get the EJB3 Remote Service Interface.  " +
				"Make sure that the Server is accessible." + "  Server Reported Error is: " + ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();				
			logger.warning(msg);
			NamingException ne = new NamingException(msg);
			ne.setRootCause(e);
			throw ne;
		} 
		
		return svcRemote;
	}
	

	public static PolicyMgmtSvcEJB30Local getPolicyEJB3LocalService(Configrable<String, String> configrable, AcmeLogger logger) throws NamingException  {
		PolicyMgmtSvcEJB30Local svc  = null;	
		try {						
			InitialContext ic = new InitialContext();
			logger.fine("obtained initial context: " + ic);
			Object ref = ic.lookup(configrable.getConfiguration().getParameterValue("ejb3PolicyLocalJNDI"));
			svc = (PolicyMgmtSvcEJB30Local) ref; 
			logger.fine("svcLocal was found.  value is: " + svc);
		}catch (NamingException e) {			
			String msg = "Failed to get the EJB3 Local Service Interface.  " +
				"Make sure that the Server is accessable." + "  Server Reported Error: " + ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.warning(msg);
			NamingException ne = new NamingException(msg);
			ne.setRootCause(e);
			throw ne;
		}		
		return svc;
	}
	
}
