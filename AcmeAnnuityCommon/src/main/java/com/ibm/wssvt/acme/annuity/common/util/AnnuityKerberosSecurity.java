package com.ibm.wssvt.acme.annuity.common.util;
/*
import java.security.PrivilegedActionException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.security.auth.RefreshFailedException;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

import com.ibm.websphere.security.WSSecurityException;
import com.ibm.websphere.security.auth.WSSubject;
//import com.ibm.websphere.security.auth.callback.NonPromptCallbackHandler;
//import com.ibm.ws.wssecurity.platform.websphere.token.KRBTicket;
//import com.ibm.wsspi.wssecurity.platform.token.KRBAuthnToken;
import com.ibm.wssvt.acme.annuity.common.exception.AnnuitySecurityException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

*/
/*
public class AnnuityKerberosSecurity {
	
	private static Map<String, Subject> userSubjectCache = Collections.synchronizedMap(new HashMap<String, Subject>());	
	private static Map<String, GSSCredential> userGSSCredCache = Collections.synchronizedMap(new HashMap<String, GSSCredential>());
		
	@SuppressWarnings("unchecked")
	public Subject login(AnnuityKerberosProperties akp, final AcmeLogger logger) throws AnnuitySecurityException{
		GSSManager gssManager = null;
		try {									
			logger.fine("generating a kerb5MechOid object from the string: " + akp.getKerb5Oid());
			Oid kerb5MechOid  = new Oid(akp.getKerb5Oid());			
			
			System.setProperty("javax.security.auth.useSubjectCredsOnly", "true");
	        System.setProperty("java.security.auth.login.config", akp.getJaasConfigFile());
	        
			GSSCredential clientGssCreds = null;
	        GSSName gssServerName;
	        GSSContext clientContext = null;	        	        	        	        	    
	        logger.fine("attempt to log via JAAS. JAAS file: " + akp.getJaasConfigFile() + " JAAS Config Entry: " + akp.getJaasConfigEntry());
	        CallbackHandler jaasCallbackHandler = new NonPromptCallbackHandler(akp.getKerbRealm(),akp.getKerbUserId(),akp.getKerbUserPassword());
	        LoginContext jaasLoginContext = null;	        
	        try {	        
	        	jaasLoginContext = new LoginContext(akp.getJaasConfigEntry(), jaasCallbackHandler);
	        	jaasLoginContext.login();
	        	logger.fine("logged in OK");
	        } catch (LoginException e) {	        	
	        	String msg = "Failed to login via JAAS. Error: " + e.getMessage();
	        	logger.log(Level.INFO, msg, e);
	        	logger.info("Failed to login via JAAS. Error: " + e.getMessage() + logger.getAllLogs());
	        	throw new AnnuitySecurityException ("Failed to login via JAAS. Error: " + e.getMessage() , e);
	        	
	        }	        
	        Subject kerberoseSubject = jaasLoginContext.getSubject();	        	     
	        logger.fine("subject before we get clientGSSCreds : " +  kerberoseSubject);
	        gssManager = GSSManager.getInstance();
	        AnnuityKerberosCredential annuityKerberosCredential = new AnnuityKerberosCredential(gssManager, kerb5MechOid, null, logger, akp);
	        // create cred and associate an authenticated Subject with the AccessControlContext dynamically via doAs method.
	        clientGssCreds = (GSSCredential) Subject.doAs(kerberoseSubject, annuityKerberosCredential);
	        logger.fine("subject after we got clientGSSCreds : " +  kerberoseSubject);	        
	        
	        gssServerName = gssManager.createName(akp.getTargetServerName(), GSSName.NT_USER_NAME);
			clientContext = gssManager.createContext(gssServerName.canonicalize(kerb5MechOid),kerb5MechOid, clientGssCreds, GSSContext.INDEFINITE_LIFETIME);			
			clientContext.requestCredDeleg(true);
	        byte[] krbToken = new byte[0]; 	      	       
	        logger.fine("initing the Sec context ...");
	        krbToken = clientContext.initSecContext(krbToken, 0, krbToken.length);	        	        	        
	        logger.fine(" security context initted, kerb token is: " + krbToken);
	        
	        logger.fine("adding the user: " + akp.getKerbUserId() + " to userSubjectCache.");	
	        String cacheKey = getCacheKey(akp);
			logger.fine("Cache Key is: " + cacheKey);			
	        addSubjectToCache(cacheKey, kerberoseSubject);			
			logger.fine("userSubjectCache is: " + userSubjectCache +". cache size: " + userSubjectCache.size());
			
			logger.fine("calling runAsKerberosSubject");
			runAsKerberosSubject(logger, kerberoseSubject); 
			
			return kerberoseSubject;	        	        	       
		} catch (GSSException e) {						
			logger.info("Failed to authenticate with Kerberose. Error: " + e.getMessage());
        	throw new AnnuitySecurityException ("Failed to authenticate with Kerberose. Error: " + e.getMessage()+ logger.getAllLogs(), e);
		} catch (PrivilegedActionException e) {		
			logger.info("Failed to authenticate with Kerberose. Error: " + e.getMessage());
			throw new AnnuitySecurityException ("Failed to authenticate with Kerberose. Error: " + e.getMessage() + logger.getAllLogs(), e);		
		}		
	}

	private String getCacheKey(AnnuityKerberosProperties akp) {
		if ("true".equalsIgnoreCase(akp.getUniqueTicketPerThread())){
			return akp.getKerbUserId() + "_T_" + Thread.currentThread().getId();	
		}else{
			return akp.getKerbUserId();
		}
		
	}
	
	private void runAsKerberosSubject(final AcmeLogger logger,
			Subject kerberoseSubject) throws AnnuitySecurityException {
		javax.security.auth.Subject currentRunAsSubject, currentCallerSubject;
		try {
			currentRunAsSubject = WSSubject.getRunAsSubject();
			logger.fine("current current run as subject is: " + currentRunAsSubject);
			currentCallerSubject = WSSubject.getCallerSubject();
			logger.fine("current current caller subject is: " + currentCallerSubject);
			// change the run as subject to our subject 
			logger.fine("setting the runassubject to kerberose subject which is: " + kerberoseSubject);
			WSSubject.setRunAsSubject(kerberoseSubject);		
			logger.fine("Subject was set.");
		} catch (WSSecurityException e) {			
		   	logger.info("got WSSecurityException  when trying to replace subject. Error: " + e.getMessage());
		   	throw new AnnuitySecurityException("got WSSecurityException  when trying to replace subject. Error: " + e.getMessage() + logger.getAllLogs(), e);		
		}
	}
	
	public Subject renewLogin(AnnuityKerberosProperties akp, final AcmeLogger logger) throws AnnuitySecurityException{		
		String cacheKey = getCacheKey(akp);
		logger.fine("Cache Key is: " + cacheKey);		
		Subject subject = getSubjectFromCache(cacheKey);
		if (subject == null){
			logger.info("no subject was found, obtain a new login");
			return login(akp, logger);
		}
				
		logger.fine("subject was found. checking its data to see if refresh is needed. Subject is: " +  subject);				
		Set<Object> privateCreds = subject.getPrivateCredentials();
		if (privateCreds == null || privateCreds.size() ==0){
			String msg = "The subject private creds are not valid.  its either null or empty.  found: " + privateCreds;
			logger.info(msg);
			throw new AnnuitySecurityException (msg);
		}
		
		KRBAuthnToken krbAuthToken = null;
		for (Object cred : privateCreds) {
			if (cred instanceof KRBAuthnToken ){
				krbAuthToken = (KRBAuthnToken)cred;
				break;
			}
		}
		
		if (krbAuthToken == null){
			String msg = "Found Private Credentails for the subject, but could not find any instance of KRBAuthnToken.  Private Credentials are: " + privateCreds;
			logger.info(msg);
			throw new AnnuitySecurityException (msg);
		}
		logger.fine("krbAuthToken found in private creds is: " + krbAuthToken);			
		KerberosTicket  kerberosTicket = ((KRBTicket) krbAuthToken).getKerberosTicket();
		if (kerberosTicket == null) {
			String msg = "did not find a KerberosTicket from the krbAuthToken - value got back is null";
			logger.info(msg);
			throw new AnnuitySecurityException (msg);			
		}
		
		logger.fine("Kerberos Ticket found from the krbAuthToken is: " + kerberosTicket);
		if (!(kerberosTicket.isRenewable())) {
			String msg = "This Kerberos Ticket is NOT renewable!  Cannot renew.";
			logger.info(msg);
			throw new AnnuitySecurityException (msg + logger.getAllLogs());
		}			
		Date renewTillDate =  kerberosTicket.getRenewTill();
		logger.fine("renewTillDate: " + renewTillDate);
		Date endTimeDate = kerberosTicket.getEndTime();
		logger.fine("endTimeDate: " + endTimeDate);			
		Date now = new Date();
		logger.fine("now: " + now);
		
		if (now.after(renewTillDate)) {
			String msg = "renew till date passed without renewal.  Should not renew.  " +
				"  Now is: " +now + " and renewTillDate is: " + renewTillDate + ".  System is only allowed to renew if renewTillDate is still before now." + 
				"  In most cases, you need to adjust the configuration to allow renewal before the renewTillDate." +
				"  Your current config is to renew before expiration with: " +akp.getMillisBeforeNewTicket() + " milliseconds";							
			logger.info(msg);
			throw new AnnuitySecurityException (msg + logger.getAllLogs());
		}
		
		if (now.after(endTimeDate)) {
			String msg = "endTimeDate passed without refresh/renew.  Should not refresh/renew.  " +
				"  Now is: " +now + " and endTimeDate is: " + endTimeDate+ ".  System is only allowed to renew if endTimeDate is still before now." + 
				"  In most cases, you need to adjust the configuration to allow renewal before the endTimeDate." +
				"  Your current config is to renew before expiration with: " +akp.getMillisBeforeRefreshTicket() + " milliseconds"; 				
			logger.info(msg);
			throw new AnnuitySecurityException (msg + logger.getAllLogs());
		}
		
		// if we are within millisBeforeNewTicket, just get a new ticket,			
		Date cutOffDate = new Date(renewTillDate.getTime() - akp.getMillisBeforeNewTicket());
		logger.fine("MillisBeforeNewTicket: " + akp.getMillisBeforeNewTicket());
		logger.fine("cutoff date for getting a new tickict, (not a refresh): " + cutOffDate);
		if (now.after(cutOffDate)) {
			logger.info("New Kerberos Ticket is required.  now date is after the cutoff date for getting a brand new ticket... getting a new one."
					+ " now: " + now + " cutOffDate: " + cutOffDate);
			return login(akp, logger);				
		}							
		// if we are within the cut off to refresh, then refresh
		cutOffDate = new Date(endTimeDate.getTime() - akp.getMillisBeforeRefreshTicket());			
		if (now.after(cutOffDate)) {
			try {
				logger.info("Refresh Kerberos Ticket is required.  now Date is after the cut off date for refreshing the ticket.  now: " + now + " cutOffDate: " + cutOffDate);
				logger.fine("kerberosTicket is BEFORE REFRESH: " + kerberosTicket);
				if ("newTicket".equalsIgnoreCase(akp.getRefreshAction())) {
					logger.info("refresh action requested is to newTicket.  Requesting New Ticket.");
					return login(akp, logger);
				}else{
					kerberosTicket.refresh();																				
					((KRBTicket)krbAuthToken).setKerberosTicket(kerberosTicket);												
					logger.fine("kerberosTicket is AFTER REFRESH: " + kerberosTicket);
				}
			} catch (RefreshFailedException e) {
				// TODO Auto-generated catch block
				String msg = "Kerberos Ticket Refersh Failed.  Error: " + e.getMessage() + " Kerbero Ticket: " + kerberosTicket;
				logger.log(Level.INFO, msg, e);
				throw new AnnuitySecurityException("Failed to refresh the Kerberose Ticket. Error is: " + e.getMessage()
						+ " kerberose ticket data: " + kerberosTicket + "\n" + logger.getAllLogs());
			}
		}							
		logger.fine("calling runAsKerberosSubject");
		runAsKerberosSubject(logger, subject);
		return subject;
	}
	
	@SuppressWarnings("unchecked")
	public byte[] loginForSPNEGO(AnnuityKerberosProperties akp, final AcmeLogger logger) throws AnnuitySecurityException{
		GSSManager gssManager = null;
		try {									
			logger.fine("generating a kerb5MechOid object from the string: " + akp.getKerb5Oid());
			Oid kerb5MechOid  = new Oid(akp.getKerb5Oid());	
			if (akp.getSpnegoOid() == null || akp.getSpnegoOid().trim().length() == 0) {
				throw new AnnuitySecurityException("the SPNEGO Oid provided s not valid. its null or empty. provided value is: " + akp.getSpnegoOid());
			}
			logger.fine("generating a spnegoMechOid object from the string: " + akp.getSpnegoOid());
			Oid spnegoOid = new Oid(akp.getSpnegoOid());
			
			System.setProperty("javax.security.auth.useSubjectCredsOnly", "true");
	        System.setProperty("java.security.auth.login.config", akp.getJaasConfigFile());
	        
			GSSCredential clientGssCreds = null;
	        GSSName gssServerName;
	        GSSContext clientContext = null;	        	        	        	        	    
	        logger.fine("attempt to log via JAAS. JAAS file: " + akp.getJaasConfigFile() + " JAAS Config Entry: " + akp.getJaasConfigEntry());
	        CallbackHandler jaasCallbackHandler = new NonPromptCallbackHandler(akp.getKerbRealm(),akp.getKerbUserId(),akp.getKerbUserPassword());
	        LoginContext jaasLoginContext = null;	        
	        try {	        
	        	jaasLoginContext = new LoginContext(akp.getJaasConfigEntry(), jaasCallbackHandler);
	        	jaasLoginContext.login();
	        	logger.fine("logged in OK");
	        } catch (LoginException e) {	        	
	        	String msg = "Failed to login via JAAS. Error: " + e.getMessage();
	        	logger.log(Level.INFO, msg, e);
	        	logger.info("Failed to login via JAAS. Error: " + e.getMessage() + logger.getAllLogs());
	        	throw new AnnuitySecurityException ("Failed to login via JAAS. Error: " + e.getMessage() , e);
	        	
	        }	        
	        Subject kerberoseSubject = jaasLoginContext.getSubject();	        	     
	        logger.fine("subject before we get clientGSSCreds : " +  kerberoseSubject);
	        gssManager = GSSManager.getInstance();
	        AnnuityKerberosCredential annuityKerberosCredential = new AnnuityKerberosCredential(gssManager, kerb5MechOid, spnegoOid, logger, akp);
	        // create cred and associate an authenticated Subject with the AccessControlContext dynamically via doAs method.
	        clientGssCreds = (GSSCredential) Subject.doAs(kerberoseSubject, annuityKerberosCredential);	        
	        logger.fine("subject after we got clientGSSCreds : " +  kerberoseSubject);	        
	        
	        gssServerName = gssManager.createName(akp.getTargetServerName(), GSSName.NT_USER_NAME);
			clientContext = gssManager.createContext(gssServerName.canonicalize(spnegoOid),spnegoOid, clientGssCreds, GSSContext.INDEFINITE_LIFETIME);			
			clientContext.requestCredDeleg(true);
	        byte[] serverToken = new byte[0]; 	      	       
	        logger.fine("initing the Sec context ...");
	        serverToken = clientContext.initSecContext(serverToken, 0, serverToken.length);	        	        	        
	        logger.fine(" security context initted, server token is: " + serverToken);

	        	
	        String cacheKey = getCacheKey(akp);
			logger.fine("Cache Key is: " + cacheKey);		
			logger.fine("adding the user: " + akp.getKerbUserId() + " GSSCred to userGSSCredCache.");
			addGSSCredToCache(cacheKey, clientGssCreds);
			
			logger.fine("adding the user: " + akp.getKerbUserId() + " to userSubjectCache.");
			addSubjectToCache(cacheKey, kerberoseSubject);	        
			logger.fine("userSubjectCache is: " + userSubjectCache +". cache size: " + userSubjectCache.size());						
			logger.fine("calling runAsKerberosSubject");
			runAsKerberosSubject(logger, kerberoseSubject); 
			
	        return serverToken;
	        
		} catch (GSSException e) {						
			logger.info("Failed to authenticate with Kerberose. Error: " + e.getMessage());
        	throw new AnnuitySecurityException ("Failed to authenticate with Kerberose. Error: " + e.getMessage()+ logger.getAllLogs(), e);
		} catch (PrivilegedActionException e) {		
			logger.info("Failed to authenticate with Kerberose. Error: " + e.getMessage());
			throw new AnnuitySecurityException ("Failed to authenticate with Kerberose. Error: " + e.getMessage() + logger.getAllLogs(), e);		
		}		
	}
	
	@SuppressWarnings("unchecked")
	public byte[] renewLoginForSPNEGO(AnnuityKerberosProperties akp, final AcmeLogger logger) throws AnnuitySecurityException{		
		String cacheKey = getCacheKey(akp);
		logger.fine("Cache Key is: " + cacheKey);
		Subject subject = getSubjectFromCache(cacheKey); 			
		if (subject == null){
			logger.info("no subject was found, obtain a new login");
			return loginForSPNEGO(akp, logger);
		}        	      	       
		// check for kerberos renewal
		logger.fine("subject was found. checking its data to see if refresh is needed. Subject is: " +  subject);				
		Set<Object> privateCreds = subject.getPrivateCredentials();
		if (privateCreds == null || privateCreds.size() ==0){
			String msg = "The subject private creds are not valid.  its either null or empty.  found: " + privateCreds;
			logger.info(msg);
			throw new AnnuitySecurityException (msg);
		}
		
		KRBAuthnToken krbAuthToken = null;
		for (Object cred : privateCreds) {
			if (cred instanceof KRBAuthnToken ){
				krbAuthToken = (KRBAuthnToken)cred;
				break;
			}
		}
		
		if (krbAuthToken == null){
			String msg = "Found Private Credentails for the subject, but could not find any instance of KRBAuthnToken.  Private Credentials are: " + privateCreds;
			logger.info(msg);
			throw new AnnuitySecurityException (msg);
		}
		
		logger.fine("krbAuthToken found in private creds is: " + krbAuthToken);			
		KerberosTicket  kerberosTicket = ((KRBTicket) krbAuthToken).getKerberosTicket();
		if (kerberosTicket == null) {
			String msg = "did not find a KerberosTicket from the krbAuthToken - value got back is null";
			logger.info(msg);
			throw new AnnuitySecurityException (msg);			
		}
		
		logger.fine("Kerberos Ticket found from the krbAuthToken is: " + kerberosTicket);
		if (!(kerberosTicket.isRenewable())) {
			String msg = "This Kerberos Ticket is NOT renewable!  Cannot renew.";
			logger.info(msg);
			throw new AnnuitySecurityException (msg + logger.getAllLogs());
		}			
		Date renewTillDate =  kerberosTicket.getRenewTill();
		logger.fine("renewTillDate: " + renewTillDate);
		Date endTimeDate = kerberosTicket.getEndTime();
		logger.fine("endTimeDate: " + endTimeDate);			
		Date now = new Date();
		logger.fine("now: " + now);
		
		if (now.after(renewTillDate)) {
			String msg = "renew till date passed without renewal.  Should not renew.  " +
				"  Now is: " +now + " and renewTillDate is: " + renewTillDate + ".  System is only allowed to renew if renewTillDate is still before now." + 
				"  In most cases, you need to adjust the configuration to allow renewal before the renewTillDate." +
				"  Your current config is to renew before expiration with: " +akp.getMillisBeforeNewTicket() + " milliseconds";							
			logger.info(msg);
			throw new AnnuitySecurityException (msg + logger.getAllLogs());
		}
		
		if (now.after(endTimeDate)) {
			String msg = "endTimeDate passed without refresh/renew.  Should not refresh/renew.  " +
				"  Now is: " +now + " and endTimeDate is: " + endTimeDate+ ".  System is only allowed to renew if endTimeDate is still before now." + 
				"  In most cases, you need to adjust the configuration to allow renewal before the endTimeDate." +
				"  Your current config is to renew before expiration with: " +akp.getMillisBeforeRefreshTicket() + " milliseconds"; 				
			logger.info(msg);
			throw new AnnuitySecurityException (msg + logger.getAllLogs());
		}
		
		// if we are within millisBeforeNewTicket, just get a new ticket,			
		Date cutOffDate = new Date(renewTillDate.getTime() - akp.getMillisBeforeNewTicket());
		logger.fine("MillisBeforeNewTicket: " + akp.getMillisBeforeNewTicket());
		logger.fine("cutoff date for getting a new tickict, (not a refresh): " + cutOffDate);
		if (now.after(cutOffDate)) {
			logger.info("New Kerberos Ticket is required.  now date is after the cutoff date for getting a brand new ticket... getting a new one."
					+ " now: " + now + " cutOffDate: " + cutOffDate);
			return loginForSPNEGO(akp, logger);				
		}							
		byte[] serverToken = new byte[0];
		// if we are within the cut off to refresh, then refresh
		cutOffDate = new Date(endTimeDate.getTime() - akp.getMillisBeforeRefreshTicket());			
		if (now.after(cutOffDate)) {
			//refresh needed
			try {
				logger.info("Refresh Kerberos Ticket is required.  now Date is after the cut off date for refreshing the ticket.  now: " + now + " cutOffDate: " + cutOffDate);
				logger.info("kerberosTicket is BEFORE REFRESH: " + kerberosTicket);
				logger.info("Subject is BEFORE REFRESH: " + subject);
				
				if (AnnuityKerberosProperties.REFRESH_ACTION_NEW_TICKET_ONLY.equalsIgnoreCase(akp.getRefreshAction())) {
					logger.info("refresh action requested is to newTicketOnly.  Requesting New Ticket.");
					return loginForSPNEGO(akp, logger);
				}else{
					logger.info("refresh action requested is refresh. Attempt to refresh...");
					// remove the GSSCreds from the subject.
					Set<GSSCredential> subjectGSSCredSet = subject.getPrivateCredentials(GSSCredential.class);
                    logger.fine("Subject private GSS Creds Set is : " + subjectGSSCredSet);
					if (subjectGSSCredSet != null){
                    	subject.getPrivateCredentials().remove(subjectGSSCredSet);           
                    	addGSSCredToCache(cacheKey, null); // remove entry from cache.
                    }else{
                    	logger.info("This is unexpceted.  Found the subject, but did not find nay GSSCredentials.  Will attempt the refresh any way.");
                    }
                    kerberosTicket.refresh();
                    ((KRBTicket)krbAuthToken).setKerberosTicket(kerberosTicket);
                    logger.info("Subject is AFTER REFRESH and BEFORE initSecContext " + subject);
					try {
						Oid kerb5MechOid = new Oid(akp.getKerb5Oid());						
						Oid spnegoOid = new Oid(akp.getSpnegoOid());
	        	        GSSManager gssManager = GSSManager.getInstance();
	        	        AnnuityKerberosCredential annuityKerberosCredential = new AnnuityKerberosCredential(gssManager, kerb5MechOid, spnegoOid, logger, akp);
	        	        // create cred and associate an authenticated Subject with the AccessControlContext dynamically via doAs method.
	        	        GSSCredential clientGssCreds = (GSSCredential) Subject.doAs(subject, annuityKerberosCredential);	        	
	        	        // add entry to cache
	        	        addGSSCredToCache(cacheKey, clientGssCreds);
	        	        GSSName gssServerName = gssManager.createName(akp.getTargetServerName(), GSSName.NT_USER_NAME);	        			
	        			GSSContext clientContext = gssManager.createContext(gssServerName.canonicalize(spnegoOid),spnegoOid, clientGssCreds, GSSContext.INDEFINITE_LIFETIME);			
	        			clientContext.requestCredDeleg(true);
	        	        logger.fine("initing the Sec context ...");
	        	        serverToken = clientContext.initSecContext(serverToken, 0, serverToken.length);	        	        	        
	        	        logger.fine("security context initted, server token is: " + serverToken);		        	        	        	      
					} catch (GSSException e) {						
						logger.info("Unexpected GSSException. Error: " + e.getMessage());
			        	throw new AnnuitySecurityException ("Unexpected GSSException. Error: " + e.getMessage()+ logger.getAllLogs(), e);
					} catch (PrivilegedActionException e) {		
						logger.info("Unexpected PrivilegedActionException. Error: " + e.getMessage());
						throw new AnnuitySecurityException ("Unexpected PrivilegedActionException. Error: " + e.getMessage() + logger.getAllLogs(), e);		
					}					
					logger.info("subject AFTER REFRESH and AFTER initSecContext: " +  subject);
					logger.fine("kerberosTicket is AFTER REFRESH: " + kerberosTicket);					
					logger.fine("Server Key AFTER REFRESH: " + serverToken);
				}
			} catch (RefreshFailedException e) {
				// TODO Auto-generated catch block
				String msg = "Kerberos Ticket Refersh Failed.  Error: " + e.getMessage() + " Kerbero Ticket: " + kerberosTicket;
				logger.log(Level.INFO, msg, e);
				throw new AnnuitySecurityException("Failed to refresh the Kerberose Ticket. Error is: " + e.getMessage()
						+ " kerberose ticket data: " + kerberosTicket + "\n" + logger.getAllLogs());
			}
		}else{			
			// no refresh is needed, just obtain a new servre token.			
			try {		
				logger.fine("no refresh is needed, just issue a new server token");
				GSSCredential clientGssCreds = getGSSCredFromCache(cacheKey);
				if (clientGssCreds == null){					
					String msg = "The clientGSSCreds from cache are null. make sure that when you run this application to set the " +
							"property uniqueTicketPerThread to true to ensure that the app is thread safe.  Current value is: " + akp.getUniqueTicketPerThread();
					logger.warning(msg);
		        	throw new AnnuitySecurityException (msg + logger.getAllLogs());
				}
				Oid spnegoOid = new Oid(akp.getSpnegoOid());								
				GSSName gssServerName = GSSManager.getInstance().createName(akp.getTargetServerName(), GSSName.NT_USER_NAME);	        			
    			GSSContext clientContext = GSSManager.getInstance().createContext(gssServerName.canonicalize(spnegoOid),spnegoOid, clientGssCreds, GSSContext.INDEFINITE_LIFETIME);			
    			clientContext.requestCredDeleg(true);
    	        logger.fine("initing the Sec context ...");
    	        serverToken = clientContext.initSecContext(serverToken, 0, serverToken.length);	        	        	        
    	        logger.fine("security context initted, server token is: " + serverToken);	   
			} catch (GSSException e) {
				logger.info("Unexpected GSSException. Error: " + e.getMessage());
	        	throw new AnnuitySecurityException ("Unexpected GSSException. Error: " + e.getMessage()+ logger.getAllLogs(), e);
			}
            
		}
		addSubjectToCache(cacheKey, subject);
		logger.fine("calling runAsKerberosSubject");
		runAsKerberosSubject(logger, subject);		
		return serverToken;
	}	
	
	private synchronized void addSubjectToCache(String key, Subject value){
		synchronized(userSubjectCache){
			userSubjectCache.put(key,value);	
		}		
	}
	private synchronized Subject getSubjectFromCache(String key){
		synchronized(userSubjectCache){ 
			return userSubjectCache.get(key);
		}
	}

	private synchronized void addGSSCredToCache(String key, GSSCredential value){
		synchronized(userGSSCredCache){
			userGSSCredCache.put(key,value);	
		}		
	}
	private synchronized GSSCredential getGSSCredFromCache(String key){
		synchronized(userGSSCredCache){ 
			return userGSSCredCache.get(key);
		}
	}
}*/
