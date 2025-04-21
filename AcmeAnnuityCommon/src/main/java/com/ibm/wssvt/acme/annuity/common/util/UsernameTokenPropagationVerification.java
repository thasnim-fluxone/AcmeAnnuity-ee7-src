/*
package com.ibm.wssvt.acme.annuity.common.util;

import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import javax.security.auth.Subject;

import com.ibm.websphere.security.WSSecurityException;
import com.ibm.websphere.security.auth.WSPrincipal;
import com.ibm.websphere.security.auth.WSSubject;
import com.ibm.websphere.wssecurity.wssapi.token.UsernameToken;
import com.ibm.ws.security.util.AccessController;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class UsernameTokenPropagationVerification {
	
	public UsernameTokenPropagationVerification(){
		
	}
	
	public void verifyUsernameToken(Configrable<String, String> configurable, String untToVerify) {
		
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(
				configurable, getClass().getName());
		
		logger.info(untToVerify + " is enabled");
		UsernameToken usernameToken = null;
		String username = null;		
		String callerPrincipalName = null;
		String callerPrincipalShortName = null;
		ArrayList<String> principalNames = new ArrayList<String>();

		try {
			Subject runAsSubject = getRunAsSubject(configurable);
			if (runAsSubject != null) {
				logger.info("calling getUsernameTokenFromSubject ...");
				usernameToken = getUsernameTokenFromSubject(runAsSubject,
						configurable);
				if (usernameToken != null) {
					username = usernameToken.getUsername();
					if (username != null) {
						logger
								.info("username from usernameToken.getUsername()is: "
										+ username);
					} else {
						logger
								.info("username from userNameID.getValue() is null");
					}
				}
			} else {
				logger.info(" runAsSubject returns null ...");
			}
			Subject callerSubject = this.getCallerSubject(configurable);
			if (callerSubject != null) {
				Set<?> principals = callerSubject.getPrincipals();

				if (principals != null && principals.size() > 0) {
					Iterator<?> principalIterator = principals.iterator();
					while (principalIterator.hasNext()) {
						Object principal = principalIterator.next();
						if (principal != null
								&& principal instanceof WSPrincipal) {
							callerPrincipalName = ((WSPrincipal) principal)
									.getName();
							if (callerPrincipalName != null) {
								logger.info("callerPrincipalName is: "
										+ callerPrincipalName);
								int indexOfSlahs = callerPrincipalName
										.indexOf("/");
								callerPrincipalShortName = callerPrincipalName
										.substring(indexOfSlahs + 1);
								principalNames.add(callerPrincipalShortName);
								logger.info("callerPrincipalShortName is: "
										+ callerPrincipalShortName);
							}
						}
					}
				}
			}
			if ((username != null) && (principalNames.size() > 0)) {
				for (int i = 0; i < principalNames.size(); i++) {
					if (username.equalsIgnoreCase(principalNames.get(i))) {
						logger
								.info("userName and callerPrincipalName matches. Username token propagation test succeeded.");
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception verifying Username token: "
					+ e.getMessage(), e);
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private UsernameToken getUsernameTokenFromSubject(final Subject subject,
			Configrable<String, String> configrable) throws WSSecurityException {
		final AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(
				configrable, getClass().getName());

		UsernameToken usernameToken = null;
		logger.info("getUsernameTokenFromSubject() entry... " + subject);

		if (subject == null) {
			return null;
		}
		try {
			usernameToken = (UsernameToken) AccessController
					.doPrivileged(new PrivilegedExceptionAction() {
						public Object run() throws Exception {
							final Iterator authIterator = subject
									.getPrivateCredentials(UsernameToken.class)
									.iterator();
							if (authIterator.hasNext()) {
								final UsernameToken token = (UsernameToken) authIterator
										.next();
								if (token != null) {
									logger.info("Found UsernameToken, ID is: "
											+ token.getId());
								} else {
									logger
											.info("NO Usernmae Token is found to be processed...");
								}
								return token;
							}
							return null;
						}
					});

			if (usernameToken == null) {
				logger.info("Could not find UsernameToken.");
			} else {
				logger.info("Found UsernameToken from runAsSubject: "
						+ usernameToken);
			}
			logger.info("getUsernameTokenFromSubject() exits... ");
		} catch (Exception e) {
			logger.log(Level.WARNING,
					"Exception getting username token from subject: "
							+ e.getMessage(), e);
			e.printStackTrace();
		}
		return usernameToken;
	}

	private Subject getRunAsSubject(Configrable<String, String> configrable) {
		Subject runAsSubject = null;
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(
				configrable, getClass().getName());

		try {
			runAsSubject = WSSubject.getRunAsSubject();
			if (runAsSubject != null) {
				logger.info("runAsSubject NOT null: " + runAsSubject);
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
			if (callerSubject != null) {
				logger.info("callerSubject NOT null: " + callerSubject);
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
}*/
