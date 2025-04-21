package com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3jaxws;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.business.ejbws.ejb3jaxws.AnnuityMgmtSvcEJB30JAXWS;
import com.ibm.wssvt.acme.annuity.common.business.ejbws.ejb3jaxws.AnnuityMgmtSvcEJB30JAXWSLocal;
import com.ibm.wssvt.acme.annuity.common.exception.AnnuitySecurityException;
//import com.ibm.wssvt.acme.annuity.common.util.AnnuityKerberosProperties;
//import com.ibm.wssvt.acme.annuity.common.util.AnnuityKerberosSecurity;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.exception.ExceptionFormatter;
import com.ibm.wssvt.acme.common.exception.InvalidConfigurationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class EJB3JAXWSEJBServiceLookup {
		
	public static IAnnuityService getAnnuityEJB3JAXWSEJBService(Configrable<String, String> configrable, AcmeLogger logger) 
		throws InvalidConfigurationException, NamingException, AnnuitySecurityException {
		String mode = configrable.getConfiguration().getParameterValue("connectionMode");
		if ("local".equalsIgnoreCase(mode)) {
			return new EJB3JAXWSEJBLocalServiceProxy(getAnnuityEJB3JAXWSEJBLocalService (configrable, logger));
		}else if ("remote".equalsIgnoreCase(mode)){
			return new EJB3JAXWSEJBRemoteServiceProxy (getAnnuityEJB3JAXWSEJBRemoteService(configrable, logger));
		}else{			
			logger.info("The connectionMode configuration is not valid.  " +
					"Supplied value is: " + mode );
			throw new InvalidConfigurationException ("The connectionMode configuration is not valid.  " +
					"Supplied value is: " + mode + logger.getAllLogs());
		}	
	}
	
	public static AnnuityMgmtSvcEJB30JAXWS getAnnuityEJB3JAXWSEJBRemoteService(Configrable<String, String> configrable, AcmeLogger logger) throws NamingException, AnnuitySecurityException  {
		AnnuityMgmtSvcEJB30JAXWS svcRemote = null;
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
			p.put(Context.PROVIDER_URL, configrable.getConfiguration().getParameterValue("iiopAddress"));				
			InitialContext ic = new InitialContext(p);
			logger.fine("obtained initial context: " + ic);
			String jndi = configrable.getConfiguration().getParameterValue("ejb3JaxwsRemoteJNDI");
			if (jndi == null || "".equals(jndi.trim())) {
				throw new NamingException("The JNDI Name for the parameter ejb3JaxwsRemoteJNDI is null or blank.  Invalid value");
			}
			Object ref = ic.lookup(jndi);
			svcRemote = (AnnuityMgmtSvcEJB30JAXWS) PortableRemoteObject.narrow(ref, AnnuityMgmtSvcEJB30JAXWS.class);
			logger.fine("svcRemote was found.  value is: " + svcRemote);
		}catch (NamingException e) {				
			String msg = "Failed to get the EJB3 Remote Service Interface.  " +
				"Make sure that the Server is accessable." + "  Server Reported Error: " + ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();				
			logger.warning(msg);
			NamingException ne = new NamingException(msg);
			ne.setRootCause(e);
			throw ne;
		}		
		return svcRemote;
	}

	public static AnnuityMgmtSvcEJB30JAXWSLocal getAnnuityEJB3JAXWSEJBLocalService(Configrable<String, String> configrable, AcmeLogger logger) throws NamingException  {
		AnnuityMgmtSvcEJB30JAXWSLocal svc  = null;	
		try {						
			InitialContext ic = new InitialContext();
			logger.fine("obtained initial context: " + ic);
			String jndi = configrable.getConfiguration().getParameterValue("ejb3JaxwsLocalJNDI");
			if (jndi == null || "".equals(jndi.trim())) {
				throw new NamingException("The JNDI Name for the parameter ejb3JaxwsLocalJNDI is null or blank.  Invalid value");
			}
			Object ref = ic.lookup(jndi);
			svc = (AnnuityMgmtSvcEJB30JAXWSLocal) ref; 
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
