/*
 * package com.ibm.wssvt.acme.annuity.common.util;


import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import com.ibm.websphere.security.WSSecurityException;
import com.ibm.websphere.security.auth.WSSubject;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

//import java.security.AccessController;
//import java.security.PrivilegedExceptionAction;
//import java.util.ArrayList;
//import java.util.Iterator;
import javax.security.auth.Subject;


 * @author malbedaiwi
 * 
 
public class EJBLoggingInterceptor implements Serializable{
	private static final long serialVersionUID = -813019909465130674L;
//	UsernameTokenPropagationVerification untverify = new UsernameTokenPropagationVerification();
	@Resource EJBContext ejbContext;
	
	@SuppressWarnings("unchecked")
	@AroundInvoke
	public Object methodLogAndTime(InvocationContext ic) throws Exception{
		AcmeLogger logger = null;
		Object result = null;
		long startTime = 0;
		try {					
			Configrable<String, String> configrable = null;
			Object[] params = ic.getParameters();
			for (Object param : params) {
				if (param instanceof Configrable){
					configrable = (Configrable<String, String>) param;
					break;
				}
			}
			if (configrable == null){
				Logger defLogger = Logger.getLogger(getClass().getName());
				defLogger.logp(Level.WARNING,getClass().getName(), "methodLogAndTime", 
						"The method call did not conatin any configrable paramenter."
						+ "Method is: " + ic.getMethod().getName()
						+ ". Its is expected that EACH method must contain at least one argument of type Configrable."
						+ ".  Using default logger: " + defLogger.getName());			
			}else {				
				logger = AnnuityLoggerFactory.getAcmeServerLogger(configrable, getClass().getName());			
				if (logger.getLogger().isLoggable(Level.FINE)){
					logger.logp(Level.FINE, getClass().getName(), "methodLogAndTime",
							"entering: " + getClass().getName() + " for method: " + ic.getMethod().getName());
					logger.logp(Level.FINE, getClass().getName(), "methodLogAndTime",
							"params: ");
					for (Object param : params) {
						logger.logp(Level.FINE, getClass().getName(), "methodLogAndTime",
								"param: " + param.toString());
					}
					
				}
				
				startTime = System.currentTimeMillis();
				if (logger.getLogger().isLoggable(Level.FINE)){
					logger.logp(Level.FINE, getClass().getName(), "methodLogAndTime",
							"method: " + ic.getMethod().getName() +
						 " in class: " + ic.getMethod().getDeclaringClass().getName() +
						 " before invoke time: " + startTime);	
				}
				// Move to only call if configurable is not null
				logSecuirtyInfo(configrable);
				
//				if ((configrable.getConfiguration()
//						.getParameterValue("verifyEjbUNToken") != null) && ("true".equalsIgnoreCase(configrable.getConfiguration()
//						.getParameterValue("verifyEjbUNToken")))) {
//					untverify.verifyUsernameToken(configrable, "verifyEjbUNToken");
//				}
			}
			result = ic.proceed();	
			return result;
		} finally {		
			long endTime = System.currentTimeMillis();
			if (logger != null && logger.getLogger().isLoggable(Level.FINE)){				
				logger.logp(Level.FINE, getClass().getName(), "methodLogAndTime",
						"method: " + ic.getMethod().getName() +
						" in class: " + ic.getMethod().getDeclaringClass().getName() +
						" after invoke time: " + endTime +
						". Total time in ms: " + (endTime - startTime));						
				
				logger.logp(Level.FINE, getClass().getName(), "methodLogAndTime",
						"total time for method: " + ic.getMethod().getName() + 
						" in class: " + ic.getMethod().getDeclaringClass().getName() + 
						" in ms: " + (endTime - startTime));		
			}										
		}								
	}

	private void logSecuirtyInfo(Configrable<String, String> configrable) {
		if ("true".equalsIgnoreCase(configrable.getConfiguration().getParameterValue("EJB3LogSecurityInfo"))){
			AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(configrable, getClass().getName());			
			logger.info("ejbContext Info:");			
			logger.info("ejbContext.getCallerPrincipal(): " + ejbContext.getCallerPrincipal());
			if (ejbContext.getCallerPrincipal() != null) {
				logger.info("ejbContext.getCallerPrincipal.getName(): " + ejbContext.getCallerPrincipal().getName());
			}
			logger.info("ejbContext.getCallerPrincipal.isCallerInRole(\"admins\"): " + ejbContext.isCallerInRole("admins"));
			logger.info("ejbContext.getCallerPrincipalisCallerInRole(\"users\"): " + ejbContext.isCallerInRole("admins"));
			
			logger.info("WSSubject Info:");			
			try {
				logger.info("WSSubject.getCallerPrincipal(): " + WSSubject.getCallerPrincipal());
				logger.info("WSSubject.getCallerSubject(): " + WSSubject.getCallerSubject());
				if (WSSubject.getCallerSubject() != null){
					logger.info("WSSubject.getCallerSubject().getPublicCredentials(): " + WSSubject.getCallerSubject().getPublicCredentials());
					logger.info("WSSubject.getCallerSubject().getgetPrivateCredentials(): " + WSSubject.getCallerSubject().getPrivateCredentials());
				}
			} catch (WSSecurityException e) {
				logger.log(Level.WARNING,"the call WSSubject.getCallerSubject() returned WSSecurityException." +
						"  No Action will be taken, but needs to investigate why we got this error.  Error: " + e.getMessage(),e);
				e.printStackTrace();
			}
			try {
				logger.info("WSSubject.getRunAsSubject(): " + WSSubject.getRunAsSubject());
				if (WSSubject.getRunAsSubject() != null){
					logger.info("WSSubject.getRunAsSubject().getPublicCredentials(): " + WSSubject.getRunAsSubject().getPublicCredentials());
					logger.info("WSSubject.getRunAsSubject().getPrivateCredentials(): " + WSSubject.getRunAsSubject().getPrivateCredentials());
				}
			} catch (WSSecurityException e) {
				logger.log(Level.WARNING,"the call WSSubject.getRunAsSubject() returned WSSecurityException." +
						" No Action will be taken, but needs to investigate why we got this error.  Error: " + e.getMessage(),e);
				e.printStackTrace();
			}
						
			
		}
		
	}
	
	private Subject getRunAsSubject(Configrable<String, String> configrable) {
		Subject runAsSubject = null;
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(
				configrable, getClass().getName());

		try {
			runAsSubject = WSSubject.getRunAsSubject();
			if (runAsSubject != null) {
				logger.info("runAsSubject NOT null: "
						+ runAsSubject);
			}
		} catch (WSSecurityException e) {
			logger
					.log(
							Level.WARNING,
							"No getRunAsSubject exists. The call WSSubject.getRunAsSubject() returned WSSecurityException."
									+ e.getMessage(), e);
			e.printStackTrace();
		}

		return runAsSubject;
	}


	
}
*/