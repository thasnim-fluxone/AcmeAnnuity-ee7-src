package com.ibm.wssvt.acme.annuity.common.util;

import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.security.auth.Subject;

import com.ibm.websphere.security.WSSecurityException;


import com.ibm.websphere.security.auth.WSSubject;
//import com.ibm.websphere.security.saml2.Saml20Token;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/**
 * @author malbedaiwi
 * 
 */
public class EJBLoggingInterceptor implements Serializable {
	private static final long serialVersionUID = -813019909465130674L;
	@Resource
	EJBContext ejbContext;

	@SuppressWarnings("unchecked")
	@AroundInvoke
	public Object methodLogAndTime(InvocationContext ic) throws Exception {
		AcmeLogger logger = null;
		Object result = null;
		long startTime = 0;
		try {
			Configrable<String, String> configrable = null;
			Object[] params = ic.getParameters();
			for (Object param : params) {
				if (param instanceof Configrable) {
					configrable = (Configrable<String, String>) param;
					break;
				}
			}
			if (configrable == null) {
				Logger defLogger = Logger.getLogger(getClass().getName());
				defLogger
						.logp(
								Level.WARNING,
								getClass().getName(),
								"methodLogAndTime",
								"The method call did not conatin any configrable paramenter."
										+ "Method is: "
										+ ic.getMethod().getName()
										+ ". Its is expected that EACH method must contain at least one argument of type Configrable."
										+ ".  Using default logger: "
										+ defLogger.getName());
			} else {
				logger = AnnuityLoggerFactory.getAcmeServerLogger(configrable,
						getClass().getName());
				if (logger.getLogger().isLoggable(Level.FINE)) {
					logger.logp(Level.FINE, getClass().getName(),
							"methodLogAndTime", "entering: "
									+ getClass().getName() + " for method: "
									+ ic.getMethod().getName());
					logger.logp(Level.FINE, getClass().getName(),
							"methodLogAndTime", "params: ");
					for (Object param : params) {
						logger.logp(Level.FINE, getClass().getName(),
								"methodLogAndTime", "param: "
										+ param.toString());
					}

				}

				startTime = System.currentTimeMillis();
				if (logger.getLogger().isLoggable(Level.FINE)) {
					logger.logp(Level.FINE, getClass().getName(),
							"methodLogAndTime", "method: "
									+ ic.getMethod().getName()
									+ " in class: "
									+ ic.getMethod().getDeclaringClass()
											.getName()
									+ " before invoke time: " + startTime);
				}
			}
			logSecuirtyInfo(configrable);
			//verifySamlToken(configrable);
			verifyOAuthToken(configrable);
			result = ic.proceed();
			return result;
		} finally {
			long endTime = System.currentTimeMillis();
			if (logger != null && logger.getLogger().isLoggable(Level.FINE)) {
				logger.logp(Level.FINE, getClass().getName(),
						"methodLogAndTime", "method: "
								+ ic.getMethod().getName() + " in class: "
								+ ic.getMethod().getDeclaringClass().getName()
								+ " after invoke time: " + endTime
								+ ". Total time in ms: "
								+ (endTime - startTime));

				logger.logp(Level.FINE, getClass().getName(),
						"methodLogAndTime", "total time for method: "
								+ ic.getMethod().getName() + " in class: "
								+ ic.getMethod().getDeclaringClass().getName()
								+ " in ms: " + (endTime - startTime));
			}
		}
	}

	private void logSecuirtyInfo(Configrable<String, String> configrable) {
		if ("true".equalsIgnoreCase(configrable.getConfiguration()
				.getParameterValue("EJB3LogSecurityInfo"))) {
			AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(
					configrable, getClass().getName());
			logger.info("ejbContext Info:");
			logger.info("ejbContext.getCallerPrincipal(): "
					+ ejbContext.getCallerPrincipal());
			if (ejbContext.getCallerPrincipal() != null) {
				logger.info("ejbContext.getCallerPrincipal.getName(): "
						+ ejbContext.getCallerPrincipal().getName());
			}
			logger
					.info("ejbContext.getCallerPrincipal.isCallerInRole(\"admins\"): "
							+ ejbContext.isCallerInRole("admins"));
			logger
					.info("ejbContext.getCallerPrincipalisCallerInRole(\"users\"): "
							+ ejbContext.isCallerInRole("admins"));

			logger.info("WSSubject Info:");
			try {
				logger.info("WSSubject.getCallerPrincipal(): "
						+ WSSubject.getCallerPrincipal());
				logger.info("WSSubject.getCallerSubject(): "
						+ WSSubject.getCallerSubject());
				if (WSSubject.getCallerSubject() != null) {
					logger
							.info("WSSubject.getCallerSubject().getPublicCredentials(): "
									+ WSSubject.getCallerSubject()
											.getPublicCredentials());
					logger
							.info("WSSubject.getCallerSubject().getgetPrivateCredentials(): "
									+ WSSubject.getCallerSubject()
											.getPrivateCredentials());
				}
			} catch (WSSecurityException e) {
				logger
						.log(
								Level.WARNING,
								"the call WSSubject.getCallerSubject() returned WSSecurityException."
										+ "  No Action will be taken, but needs to investigate why we got this error.  Error: "
										+ e.getMessage(), e);
				e.printStackTrace();
			}
			try {
				logger.info("WSSubject.getRunAsSubject(): "
						+ WSSubject.getRunAsSubject());
				if (WSSubject.getRunAsSubject() != null) {
					logger
							.info("WSSubject.getRunAsSubject().getPublicCredentials(): "
									+ WSSubject.getRunAsSubject()
											.getPublicCredentials());
					logger
							.info("WSSubject.getRunAsSubject().getPrivateCredentials(): "
									+ WSSubject.getRunAsSubject()
											.getPrivateCredentials());
				}
			} catch (WSSecurityException e) {
				logger
						.log(
								Level.WARNING,
								"the call WSSubject.getRunAsSubject() returned WSSecurityException."
										+ " No Action will be taken, but needs to investigate why we got this error.  Error: "
										+ e.getMessage(), e);
				e.printStackTrace();
			}
		}
	}

	/*	@SuppressWarnings("unused")
	private void verifySamlToken(Configrable<String, String> configurable) {
		
			if ("true".equalsIgnoreCase(configurable.getConfiguration()
				.getParameterValue("verifySamlToken"))) {			
			AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(
					configurable, getClass().getName());
			logger.info("verifySamlToken is enabled");
			Saml20Token samlToken = null;
			//SAMLNameID samlNameID = null;
			//String samlNameID = null;
			String SamlUserName = null;
			String callerPrincipalName = null;
			String callerPrincipalShortName = null;
			ArrayList<String> principalNames = new ArrayList<String>();

			try {
				Subject runAsSubject = getRunAsSubject(configurable);
				if (runAsSubject != null) {
					logger.info("calling getSAMLTokenFromSubject ...");
					samlToken = getSAMLTokenFromSubject(runAsSubject,
							configurable);
					if (samlToken != null) {
						//samlNameID = samlToken.getSAMLNameID();
						//SamlUserName = samlNameID.getValue();
						SamlUserName = samlToken.getSAMLNameID();
						if (SamlUserName != null) {
							logger
									.info("Saml username from samlNameID.getValue() is: "
											+ SamlUserName);
						} else {
							logger
									.info("Saml username from samlNameID.getValue() is null");
						}
					}
				}
				else {
					logger.info(" runAsSubject returns null ...");
				}
				Subject callerSubject = this.getCallerSubject(configurable);
				if (callerSubject != null) {
					Set<?> principals = callerSubject.getPrincipals();

					if (principals != null && principals.size() > 0) {
						Iterator<?> principalIterator = principals.iterator();
						while (principalIterator.hasNext()) {
							Object principal = principalIterator.next();
						
						}
					}
				}
				if ((SamlUserName != null) && (principalNames.size() > 0)) {
					for (int i = 0; i < principalNames.size(); i++) {
						if (SamlUserName
								.equalsIgnoreCase(principalNames.get(i))) {
							logger
									.info("SamlUserName and callerPrincipalName matches. SAML token EJB propagation test succeeded.");
						}
					}
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, "Exception verifying Saml token: "
						+ e.getMessage(), e);
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Saml20Token getSAMLTokenFromSubject(final Subject subject,
			Configrable<String, String> configrable) throws WSSecurityException {
		final AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(
				configrable, getClass().getName());

		Saml20Token samlToken = null;
		logger.info("getSAMLTokenFromSubject() entry... " + subject);

		if (subject == null) {
			return null;
		}
		try {
			samlToken = (Saml20Token) AccessController
					.doPrivileged(new PrivilegedExceptionAction() {
						@Override
						public Object run() throws Exception {
							final Iterator authIterator = subject
									.getPrivateCredentials(Saml20Token.class)
									.iterator();
							if (authIterator.hasNext()) {
								final Saml20Token token = (Saml20Token) authIterator
										.next();
								if (token != null) {
									logger.info("Found Saml20Token, ID is: "
											+ token.getSamlID());
								} else {
									logger
											.info("NO Saml20Token is found to be processed...");
								}
								return token;
							}
							return null;
						}
					});

			if (samlToken == null) {
				logger.info("Could not find Saml20Token.");
			} else {
				logger.info("Found Saml20Token from runAsSubject: " + samlToken);
			}
			logger.info("getSAMLTokenFromSubject() exits... ");
		} catch (Exception e) {
			logger.log(Level.WARNING,
					"Exception getting saml token from subject: "
							+ e.getMessage(), e);
			e.printStackTrace();
		}
		return samlToken;
	}
*/
	@SuppressWarnings("unused")
	private void verifyOAuthToken(Configrable<String, String> configurable) {
		if ("true".equalsIgnoreCase(configurable.getConfiguration()
				.getParameterValue("verifyOAuthToken"))) {			
			AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(
					configurable, getClass().getName());
			logger.info("verifyOAuthToken is enabled");
			//WSOAuth20Token oauthToken = null;
			//String oauthUserName = null;
			//ArrayList<String> principalNames = new ArrayList<String>();

			try {
				Subject runAsSubject = getRunAsSubject(configurable);
			/* if (runAsSubject != null) {
								
					logger.info("calling getOAuthTokenFromSubject ...");
					oauthToken = getOAuthTokenFromSubject(runAsSubject,
							configurable);
					if (oauthToken != null) {
						oauthUserName = oauthToken.getUser();
						if (oauthUserName != null) {
							logger
									.info("OAuth username from oauth token is: "
											+ oauthUserName + "OAuth token propagation succeeded.\n");
						} else {
							logger
									.info("OAuth username from oauth token is null");
						}
					}
				}
				else {
					logger.info(" runAsSubject returns null ...");
				}*/
				
			} catch (Exception e) {
				logger.log(Level.WARNING, "Exception verifying Oauth token: "
						+ e.getMessage(), e);
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

	private Subject getCallerSubject(Configrable<String, String> configrable) {
		Subject callerSubject = null;
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(
				configrable, getClass().getName());

		try {			
			callerSubject = WSSubject.getCallerSubject();
			if ( callerSubject!= null) {
				logger.info("callerSubject NOT null: "
						+ callerSubject);
			}
		} catch (WSSecurityException e) {
			logger
					.log(
							Level.WARNING,
							"No getRunAsSubject exists. The call WSSubject.getCallerSubject() returned WSSecurityException."
									+ e.getMessage(), e);
			e.printStackTrace();
		}
		return callerSubject;
	}

}
