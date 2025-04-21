package com.ibm.wssvt.acme.annuity.common.business;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.ibm.wssvt.acme.annuity.common.business.nonbusinessimpl.AnnuitySpecialImplFactory;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.AnnuityMgmtSvcEJB30JAXWSImpl;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl.AnnuityMgmtSvcPojoJAXWSImpl;
import com.ibm.wssvt.acme.annuity.common.exception.AnnuitySecurityException;
import com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3.EJB3ServiceLookup;
import com.ibm.wssvt.acme.annuity.common.servicelookup.ejb31.EJB31AsynchServiceLookup;
import com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3jaxws.EJB3JAXWSEJBServiceLookup;
import com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3jaxws.EJB3JAXWSJAXWSServiceLookup;
import com.ibm.wssvt.acme.annuity.common.servicelookup.jaxrs.JAXRSServiceLookup;
import com.ibm.wssvt.acme.annuity.common.servicelookup.pojojaxws.JAXWSServiceLookup;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityLoggerFactory;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityServiceName;
import com.ibm.wssvt.acme.annuity.common.util.ServerInjectedObjects;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.bean.StringConfigrable;
import com.ibm.wssvt.acme.common.exception.InvalidConfigurationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class AnnuityBusinessServiceLookup {

	
	private static final String INTERNAL_CURRENT_SERVICE_NUMBER = "internal.CURRENT_SERVICE_NUMBER";

	public IAnnuityService getAnnuityAnnuityService(Configrable<String, String> inConfig) throws InvalidConfigurationException, NamingException, RemoteException, CreateException, AnnuitySecurityException{
		AnnuityServiceName annuityServiceName = getServiceName(inConfig);
		Configrable<String, String> configrable = new StringConfigrable();
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(inConfig, getClass().getName());
		
		if (AnnuityServiceName.EJB31.equals(annuityServiceName)){
			logger.fine("Hopping to: " + annuityServiceName);
			getEJB31ServiceConfig(inConfig, configrable);
			return EJB31AsynchServiceLookup.getAnnuityEJB31Service(null, null, configrable, logger);
		}else if (AnnuityServiceName.EJB30.equals(annuityServiceName)){
			logger.fine("Hopping to: " + annuityServiceName);
			getEJB30ServiceConfig(inConfig, configrable);
			return EJB3ServiceLookup.getAnnuityEJB3Service(null, null, configrable, logger);
		}else if (AnnuityServiceName.EJB30JAXWS_EJB.equals(annuityServiceName)){
			logger.info("Hopping to: " + annuityServiceName);
			getEJB30JAXWS_EJBServiceConfig(inConfig, configrable);	
			return EJB3JAXWSEJBServiceLookup.getAnnuityEJB3JAXWSEJBService(configrable, logger);
		}else if (AnnuityServiceName.EJB30JAXWS_JAXWS.equals(annuityServiceName)){
			logger.info("Hopping to: " + annuityServiceName);
			getEJB30JAXWS_JAXWSServiceConfig(inConfig, configrable);			
			if ("useInjection".equalsIgnoreCase(configrable.getConfiguration().getParameterValue("connectionMode"))){
				logger.info("connectionMode is set to useInjection");
				AnnuityMgmtSvcEJB30JAXWSImpl injectedObject = (AnnuityMgmtSvcEJB30JAXWSImpl) ServerInjectedObjects.getInstance().getObject("AnnuityMgmtSvcEJB30JAXWSImpl");
				logger.info("injected Service AnnuityMgmtSvcEJB30JAXWSImpl: " + injectedObject);
				if (injectedObject == null){
					throw new InvalidConfigurationException ("The Configuration requested to use the Injected Object of AnnuityMgmtSvcEJB30JAXWSImpl, " +
							"but the object value is null.");
				}
				return EJB3JAXWSJAXWSServiceLookup.getAnnuityJAXWSService(injectedObject, configrable, logger);
			}else{
				return EJB3JAXWSJAXWSServiceLookup.getAnnuityJAXWSService(null, configrable, logger);
			}
		}else if (AnnuityServiceName.POJO_JAXWS.equals(annuityServiceName)){
			logger.info("Hopping to: " + annuityServiceName);
			getPOJO_JAXWSServiceConfig(inConfig, configrable);
			if ("useInjection".equalsIgnoreCase(configrable.getConfiguration().getParameterValue("connectionMode"))){
				logger.info("connectionMode is set to useInjection");
				AnnuityMgmtSvcPojoJAXWSImpl injectedObject = (AnnuityMgmtSvcPojoJAXWSImpl) ServerInjectedObjects.getInstance().getObject("AnnuityMgmtSvcPojoJAXWSImpl");
				logger.info("injected Service AnnuityMgmtSvcPojoJAXWSImpl: " + injectedObject);
				if (injectedObject == null){
					throw new InvalidConfigurationException ("The Configuration requested to use the Injected Object of AnnuityMgmtSvcPojoJAXWSImpl, " +
							"but the object value is null.");
				}
				return JAXWSServiceLookup.getAnnuityJAXWSService(injectedObject, configrable, logger);
			}else  if ("useInjection1".equalsIgnoreCase(configrable.getConfiguration().getParameterValue("connectionMode"))){
				logger.info("connectionMode is set to useInjection1");
				
				AnnuityMgmtSvcPojoJAXWSImpl injectedObject = (AnnuityMgmtSvcPojoJAXWSImpl) ServerInjectedObjects.getInstance().getObject("AnnuityMgmtSvcPojoJAXWSImpl1");
				logger.info("injected Service AnnuityMgmtSvcPojoJAXWSImpl1: " + injectedObject);
				if (injectedObject == null){
					throw new InvalidConfigurationException ("The Configuration requested to use the Injected Object of AnnuityMgmtSvcPojoJAXWSImpl1, " +
							"but the object value is null.");
				}
				return JAXWSServiceLookup.getAnnuityJAXWSService(injectedObject, configrable, logger);			
			}else if ("useInjection2".equalsIgnoreCase(configrable.getConfiguration().getParameterValue("connectionMode"))){
				logger.info("connectionMode is set to useInjection2");
				
				AnnuityMgmtSvcPojoJAXWSImpl injectedObject = (AnnuityMgmtSvcPojoJAXWSImpl) ServerInjectedObjects.getInstance().getObject("AnnuityMgmtSvcPojoJAXWSImpl2");
				logger.info("injected Service AnnuityMgmtSvcPojoJAXWSImpl2: " + injectedObject);
				if (injectedObject == null){
					throw new InvalidConfigurationException ("The Configuration requested to use the Injected Object of AnnuityMgmtSvcPojoJAXWSImpl2, " +
							"but the object value is null.");
				}
				return JAXWSServiceLookup.getAnnuityJAXWSService(injectedObject, configrable, logger);			
			}else if ("useInjection3".equalsIgnoreCase(configrable.getConfiguration().getParameterValue("connectionMode"))){
				logger.info("connectionMode is set to useInjection3");
				
				AnnuityMgmtSvcPojoJAXWSImpl injectedObject = (AnnuityMgmtSvcPojoJAXWSImpl) ServerInjectedObjects.getInstance().getObject("AnnuityMgmtSvcPojoJAXWSImpl3");
				logger.info("injected Service AnnuityMgmtSvcPojoJAXWSImpl3: " + injectedObject);
				if (injectedObject == null){
					throw new InvalidConfigurationException ("The Configuration requested to use the Injected Object of AnnuityMgmtSvcPojoJAXWSImpl3, " +
							"but the object value is null.");
				}
				return JAXWSServiceLookup.getAnnuityJAXWSService(injectedObject, configrable, logger);			
			}
			else{
				return JAXWSServiceLookup.getAnnuityJAXWSService(null, configrable, logger);
			}					
		
		}else if (AnnuityServiceName.JAXRS_1.equals(annuityServiceName)){
			logger.info("Hopping to: " + annuityServiceName);
			getConfigForJAXRS(inConfig, configrable);
			return JAXRSServiceLookup.getAnnuityJAXRSService(configrable, logger);
		}
		else if (AnnuityServiceName.JAXRS_2.equals(annuityServiceName)){
			logger.info("Hopping to: " + annuityServiceName);
			getConfigForJAXRS(inConfig, configrable);
			return JAXRSServiceLookup.getAnnuityJAXRSService(configrable, logger);				
		
		}else if (AnnuityServiceName.SPECIAL_SERVICE.equals(annuityServiceName)){
			return new AnnuitySpecialImplFactory().getSpecialAnnuityService(inConfig);
		}else{
			throw new InvalidConfigurationException("Service name: " + annuityServiceName + " does not support service hopper configuration.");
		}
				
	}
	
	public AnnuityServiceName getServiceName(Configrable<String, String> inConfig) throws InvalidConfigurationException {
		int shCount;
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(inConfig, getClass().getName());
		String specialService = inConfig.getConfiguration().getParameterValue("specialServiceName");
		
		if (specialService != null && specialService.trim().length() >0) {
			return AnnuityServiceName.SPECIAL_SERVICE;
		}
		try{
			shCount = Integer.parseInt(inConfig.getConfiguration().getParameterValue("SH.Count"));			
		}catch (Throwable t) {
			shCount = 0;
		}
		logger.fine("shCount: " + shCount);
		
		if (shCount <= 0){
			logger.fine("No Service Hopping is requested. Final destination is EJB30");
			return AnnuityServiceName.EJB30;
		}		
		if (shCount>0){
			logger.info("shCount: " + shCount);
		}
		String currentServiceNumber = inConfig.getConfiguration().getParameterValue(INTERNAL_CURRENT_SERVICE_NUMBER);
		if (currentServiceNumber == null) {
			currentServiceNumber = "1";
			inConfig.getConfiguration().addParameter(INTERNAL_CURRENT_SERVICE_NUMBER, currentServiceNumber);			
		}else {						
			if (Integer.parseInt(currentServiceNumber) > shCount) {
				logger.fine("Completed all Service hops - final service is to EJB30");
				return AnnuityServiceName.EJB30;
			}			
		}
		logger.info("Hopping to Service Number: " + inConfig.getConfiguration().getParameterValue(INTERNAL_CURRENT_SERVICE_NUMBER));
		String serviceNameKey = "SH" + currentServiceNumber + ".Name"; //like SH1.Name
		String shName = inConfig.getConfiguration().getParameterValue(serviceNameKey);
		
		if (shName == null) {
			throw new InvalidConfigurationException("Invalid value for a service name at key: " + serviceNameKey);
		} 		 
		try{
			return AnnuityServiceName.valueOf(shName);			
		}catch(Throwable t){			
			throw new InvalidConfigurationException("Service name is not valid.  Key: " + serviceNameKey + " current value value: " + shName);
		}
	}
	
	private void getEJB2xJAXRPC_JAXRPCServiceConfig(Configrable<String, String> inConfig, Configrable<String, String> outConfig)
	throws InvalidConfigurationException {

		String prefix = getPrefixAndUpdatedServiceNumber(inConfig);
		getGenericJAXRPCServiceConfig(inConfig, outConfig, prefix, "ejb2xJAXRPCServiceURL");
		
	}
	
	private void getEJB30JAXRPC_JAXRPCServiceConfig(Configrable<String, String> inConfig, Configrable<String, String> outConfig)
			throws InvalidConfigurationException {
		
		String prefix = getPrefixAndUpdatedServiceNumber(inConfig);
		getGenericJAXRPCServiceConfig(inConfig, outConfig, prefix, "ejb3JAXRPCServiceURL");
				
	}
	
	private void getEJB30JAXWS_JAXWSServiceConfig(Configrable<String, String> inConfig,Configrable<String, String> outConfig)
			throws InvalidConfigurationException {
		
		String prefix = getPrefixAndUpdatedServiceNumber(inConfig);
		getGenericJAXWSServiceConfig(inConfig, outConfig, prefix, "ejb30JaxwsServiceURL");										
	}
	
	private void getPOJO_JAXRPCServiceConfig(Configrable<String, String> inConfig, Configrable<String, String> outConfig)
			throws InvalidConfigurationException {
		
		String prefix = getPrefixAndUpdatedServiceNumber(inConfig);
		getGenericJAXRPCServiceConfig(inConfig, outConfig, prefix, "pojoJAXRPCServiceURL");
		
	}
	
	private void getPOJO_JAXWSServiceConfig(Configrable<String, String> inConfig, Configrable<String, String> outConfig)
			throws InvalidConfigurationException {
		
		String prefix = getPrefixAndUpdatedServiceNumber(inConfig);
		getGenericJAXWSServiceConfig(inConfig, outConfig, prefix, "jaxwsPojoServiceURL");										
	}
	
	private void getEJB2xJAXRPC_EJBServiceConfig(Configrable<String, String> inConfig, Configrable<String, String> outConfig)
			throws InvalidConfigurationException {
		
		String prefix = getPrefixAndUpdatedServiceNumber(inConfig);
		getGenericEJBServiceConfigs(inConfig, outConfig, prefix, "ejb2xJaxrpcRemoteHomeJNDI", "ejb2xJaxrpcLocalHomeJNDI");		
	}
	
	private void getEJB30JAXRPC_EJBServiceConfig(Configrable<String, String> inConfig, Configrable<String, String> outConfig)
			throws InvalidConfigurationException {
		
		String prefix = getPrefixAndUpdatedServiceNumber(inConfig);
		getGenericEJBServiceConfigs(inConfig, outConfig, prefix, "ejb3JaxrpcRemoteJNDI", "ejb3JaxrpcLocalJNDI");		
	}

	
	private void getEJB30JAXWS_EJBServiceConfig(Configrable<String, String> inConfig, Configrable<String, String> outConfig)
			throws InvalidConfigurationException {
		
		String prefix = getPrefixAndUpdatedServiceNumber(inConfig);
		getGenericEJBServiceConfigs(inConfig, outConfig, prefix, "ejb3JaxwsRemoteJNDI", "ejb3JaxwsLocalJNDI");	
	}
	
	private void getEJB2xServiceConfig(
			Configrable<String, String> inConfig,
			Configrable<String, String> outConfig)
			throws InvalidConfigurationException {
		
		String prefix = getPrefixAndUpdatedServiceNumber(inConfig);

		getGenericEJBServiceConfigs(inConfig, outConfig, prefix, "ejb2xRemoteHomeJNDI", "ejb2xLocalHomeJNDI");
		
	}

		
	private void getEJB31ServiceConfig(Configrable<String, String> inConfig, Configrable<String, String> outConfig)
		throws InvalidConfigurationException {

		getGenericEJBServiceConfigs(inConfig, outConfig, "", "ejb31RemoteJNDI", "ejb31LocalJNDI");
	}

	private void getEJB30ServiceConfig(Configrable<String, String> inConfig, Configrable<String, String> outConfig)
			throws InvalidConfigurationException {

			getGenericEJBServiceConfigs(inConfig, outConfig, "", "ejb3RemoteJNDI", "ejb3LocalJNDI");
	}

	private void getConfigForJAXRS(Configrable<String, String> inConfig, Configrable<String, String> outConfig)
	throws InvalidConfigurationException {		
		String prefix = getPrefixAndUpdatedServiceNumber(inConfig);		
		getGenericJAXRSServiceConfig(inConfig, outConfig, prefix, "jaxrsServerURL");
	}
	
	private void getGenericEJBServiceConfigs(Configrable<String, String> inConfig,Configrable<String, String> outConfig,
			String prefix, String remoteJNDIKey, String localJNDIKey)
			throws InvalidConfigurationException {
		
		// connectioMode remote + local
		String connectionMode = inConfig.getConfiguration().getParameterValue(prefix + "connectionMode");
		if ("remote".equalsIgnoreCase(connectionMode)) {
			outConfig.getConfiguration().addParameter("connectionMode", "remote");
			if (inConfig.getConfiguration().getParameterValue(prefix + "iiopAddress") == null) {
				throw new InvalidConfigurationException(prefix + "iiopAddress for EJB service is null");					
			}else{
				outConfig.getConfiguration().addParameter("iiopAddress", inConfig.getConfiguration().getParameterValue(prefix + "iiopAddress"));
			}
			
			if (inConfig.getConfiguration().getParameterValue(prefix + "initialContextFactory") == null) {
				throw new InvalidConfigurationException(prefix + "initialContextFactory for EJB service is null");					
			}else{
				outConfig.getConfiguration().addParameter("initialContextFactory", inConfig.getConfiguration().getParameterValue(prefix + "initialContextFactory"));
			}
			
			if (inConfig.getConfiguration().getParameterValue(prefix + remoteJNDIKey) == null) {
				throw new InvalidConfigurationException(prefix + remoteJNDIKey + " for EJB service is null");					
			}else{
				outConfig.getConfiguration().addParameter(remoteJNDIKey, inConfig.getConfiguration().getParameterValue(prefix + remoteJNDIKey ));
			}
			
		}else if ("local".equalsIgnoreCase(connectionMode)) {
			outConfig.getConfiguration().addParameter("connectionMode", "local");
			if (inConfig.getConfiguration().getParameterValue(prefix + localJNDIKey) == null) {
				throw new InvalidConfigurationException(prefix + localJNDIKey + " for EJB service is null");					
			}else{
				outConfig.getConfiguration().addParameter(localJNDIKey, inConfig.getConfiguration().getParameterValue(prefix + localJNDIKey));
			}
			
		}else{
			throw new InvalidConfigurationException("Service Configurations are not valid for Service" + prefix + "connectionMode"+
					"  Expected connectionMode to be remote or local, but found: " + connectionMode);
		}
	}	

	private void getGenericJAXWSServiceConfig(Configrable<String, String> inConfig, Configrable<String, String> outConfig,
			String prefix, String serviceURLKey)
			throws InvalidConfigurationException {
						
		if (inConfig.getConfiguration().getParameterValue(prefix + serviceURLKey) == null) {
			throw new InvalidConfigurationException(prefix + serviceURLKey + " for jaxws service is null");					
		}else{
			outConfig.getConfiguration().addParameter(serviceURLKey, inConfig.getConfiguration().getParameterValue(prefix + serviceURLKey));
		}
		
		if (inConfig.getConfiguration().getParameterValue(prefix + "useAsync") == null) {
			throw new InvalidConfigurationException(prefix + "useAsync for jaxws service is null");					
		}else{
			outConfig.getConfiguration().addParameter("useAsync", inConfig.getConfiguration().getParameterValue(prefix + "useAsync"));
		}		
		
		if ("true".equalsIgnoreCase(outConfig.getConfiguration().getParameterValue("useAsync"))) {
		
			if (inConfig.getConfiguration().getParameterValue(prefix + "waitTime") == null) {
				throw new InvalidConfigurationException(prefix + "waitTime for jaxws service is null");					
			}else{
				outConfig.getConfiguration().addParameter("waitTime", inConfig.getConfiguration().getParameterValue(prefix + "waitTime"));
			}		
			
			if (inConfig.getConfiguration().getParameterValue(prefix + "waitCount") == null) {
				throw new InvalidConfigurationException(prefix + "waitCount for jaxws service is null");					
			}else{
				outConfig.getConfiguration().addParameter("waitCount", inConfig.getConfiguration().getParameterValue(prefix + "waitCount"));
			}	
		}
		
		if (inConfig.getConfiguration().getParameterValue(prefix + "connectionMode") == null) {
			outConfig.getConfiguration().addParameter("connectionMode", "noInjection");					
		}else{
			outConfig.getConfiguration().addParameter("connectionMode", inConfig.getConfiguration().getParameterValue(prefix + "connectionMode"));
		}			
	}
	
	private void getGenericJAXRPCServiceConfig(Configrable<String, String> inConfig, Configrable<String, String> outConfig,
			String prefix, String serviceURL)
			throws InvalidConfigurationException {
			
		if (inConfig.getConfiguration().getParameterValue(prefix + serviceURL) == null) {
			throw new InvalidConfigurationException(prefix + serviceURL + " for jaxrpc service is null");					
		}else{
			outConfig.getConfiguration().addParameter(serviceURL, inConfig.getConfiguration().getParameterValue(prefix + serviceURL));
		}				
	}
	
	private void getGenericJAXRSServiceConfig(Configrable<String, String> inConfig, Configrable<String, String> outConfig,
			String prefix, String serverURL)
			throws InvalidConfigurationException {
						
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(inConfig, getClass().getName());
		
		if (inConfig.getConfiguration().getParameterValue(prefix + serverURL) == null) {
			throw new InvalidConfigurationException(prefix + serverURL + " for jaxrs service is null");					
		}else{
			
			outConfig.getConfiguration().addParameter("serverURL", inConfig.getConfiguration().getParameterValue(prefix + serverURL));
			
			logger.fine("jaxrs serverURL is " + inConfig.getConfiguration().getParameterValue(prefix + serverURL));
		}	
		
		if (inConfig.getConfiguration().getParameterValue(prefix + "contentType") == null) {
			outConfig.getConfiguration().addParameter("contentType", "JAXB");					
		}else{
			outConfig.getConfiguration().addParameter("contentType", inConfig.getConfiguration().getParameterValue(prefix + "contentType"));
		}
		
		if (inConfig.getConfiguration().getParameterValue(prefix + "useSecurity") == null) {
			outConfig.getConfiguration().addParameter("useSecurity", "NONE");					
		}else{
			outConfig.getConfiguration().addParameter("useSecurity", inConfig.getConfiguration().getParameterValue(prefix + "useSecurity"));
		}
		
		if (inConfig.getConfiguration().getParameterValue(prefix + "readTimeout") == null) {
			outConfig.getConfiguration().addParameter("readTimeout", "300000");					
		}else{
			outConfig.getConfiguration().addParameter("readTimeout", inConfig.getConfiguration().getParameterValue(prefix + "readTimeout"));
		}
		
		if (inConfig.getConfiguration().getParameterValue(prefix + "userName") == null) {
			outConfig.getConfiguration().addParameter("userName", "NONE");					
		}else{
			outConfig.getConfiguration().addParameter("userName", inConfig.getConfiguration().getParameterValue(prefix + "userName"));
		}
		
		if (inConfig.getConfiguration().getParameterValue(prefix + "userPassword") == null) {
			outConfig.getConfiguration().addParameter("userPassword", "NONE");					
		}else{
			outConfig.getConfiguration().addParameter("userPassword", inConfig.getConfiguration().getParameterValue(prefix + "userPassword"));
		}
		
		if (inConfig.getConfiguration().getParameterValue(prefix + "useApacheClient") == null) {
			outConfig.getConfiguration().addParameter("useApacheClient", "true");					
		}else{
			outConfig.getConfiguration().addParameter("useApacheClient", inConfig.getConfiguration().getParameterValue(prefix + "useApacheClient"));
		}
	}
	private String getPrefixAndUpdatedServiceNumber(
			Configrable<String, String> inConfig) {
		String serviceNumber = inConfig.getConfiguration().getParameterValue(INTERNAL_CURRENT_SERVICE_NUMBER);						
		String prefix = "SH" + serviceNumber + ".";
		
		int updatedNextCount = Integer.parseInt(serviceNumber) + 1;
		inConfig.getConfiguration().addParameter(INTERNAL_CURRENT_SERVICE_NUMBER, "" + updatedNextCount);
		
		return prefix;
	}			
}
