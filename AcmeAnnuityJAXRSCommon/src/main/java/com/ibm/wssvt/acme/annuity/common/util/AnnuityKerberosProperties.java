package com.ibm.wssvt.acme.annuity.common.util;

import java.io.Serializable;

import com.ibm.wssvt.acme.annuity.common.exception.AnnuitySecurityException;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class AnnuityKerberosProperties implements Serializable{
	
	private static final long serialVersionUID = 8188568204515228696L;
	
	public static String REFRESH_ACTION_REFRESH = "refresh";
	public static String REFRESH_ACTION_NEW_TICKET_ONLY = "newTicketOnly";
	public static String LOGIN_STYLE_NEW_TICKET= "newTicketOnly";
	public static String LOGIN_STYLE_NEW_OR_REFRESH_TICKET = "newOrRefreshTicket";
	
	private static final String KERB_USER_ID = "kerbUserId";
	private static final String UNIQUE_TICKET_PER_THREAD = "uniqueTicketPerThread";
	private static final String REFRESH_ACTION = "refreshAction";
	private static final String LOGIN_STYLE = "loginStyle";
	private static final String MILLIS_BEFORE_REFRESH_TICKET = "millisBeforeRefreshTicket";
	private static final String MILLIS_BEFORE_NEW_TICKET = "millisBeforeNewTicket";
	private static final String TARGET_SERVER_NAME = "targetServerName";
	private static final String JAAS_CONFIG_ENTRY = "jaasConfigEntry";
	private static final String JAAS_CONFIG_FILE = "jaasConfigFile";
	private static final String KERB5_OID = "kerb5Oid";
	private static final String KERB_REALM = "kerbRealm";
	private static final String KERB_USER_PASSWORD = "kerbUserPassword";
	
	private static final String SPNEGO_OID = "spnegoOid";
		
	private String kerbUserId = null;
	private String kerbUserPassword = null;
	private String kerbRealm = null;
	private String targetServerName = null;	
	private String kerb5Oid = null;	
	private String jaasConfigFile="";
	private String jaasConfigEntry="";
	private String loginStyle;
	private String refreshAction;
	private String uniqueTicketPerThread = "true";
	
	private String spnegoOid = null;
	
	public String getRefreshAction() {
		return refreshAction;
	}
	public void setRefreshAction(String refreshAction) {
		this.refreshAction = refreshAction;
	}

	private long millisBeforeNewTicket;
	private long millisBeforeRefreshTicket;
	
	
	public long getMillisBeforeNewTicket() {
		return millisBeforeNewTicket;
	}
	public void setMillisBeforeNewTicket(long millisBeforeNewTicket) {
		this.millisBeforeNewTicket = millisBeforeNewTicket;
	}
	public long getMillisBeforeRefreshTicket() {
		return millisBeforeRefreshTicket;
	}
	public void setMillisBeforeRefreshTicket(long millisBeforeRefreshTicket) {
		this.millisBeforeRefreshTicket = millisBeforeRefreshTicket;
	}
	public String getLoginStyle() {
		return loginStyle;
	}
	public void setLoginStyle(String loginStyle) {
		this.loginStyle = loginStyle;
	}
	public String getKerbUserId() {
		return kerbUserId;
	}
	public void setKerbUserId(String kerbUserId) {
		this.kerbUserId = kerbUserId;
	}
	public String getKerbUserPassword() {
		return kerbUserPassword;
	}
	public void setKerbUserPassword(String kerbUserPassword) {
		this.kerbUserPassword = kerbUserPassword;
	}
	public String getKerbRealm() {
		return kerbRealm;
	}
	public void setKerbRealm(String kerbRealm) {
		this.kerbRealm = kerbRealm;
	}
	public String getTargetServerName() {
		return targetServerName;
	}
	public void setTargetServerName(String targetServerName) {
		this.targetServerName = targetServerName;
	}
		
	public String getKerb5Oid() {
		return kerb5Oid;
	}
	public void setKerb5Oid(String kerb5Oid) {
		this.kerb5Oid = kerb5Oid;
	}
	public String getJaasConfigFile() {
		return jaasConfigFile;
	}
	public void setJaasConfigFile(String jaasConfigFile) {
		this.jaasConfigFile = jaasConfigFile;
	}
	public String getJaasConfigEntry() {
		return jaasConfigEntry;
	}
	public void setJaasConfigEntry(String jaasConfigEntry) {
		this.jaasConfigEntry = jaasConfigEntry;
	}
	
	public void setKerbProperties(final Configrable<String, String> configrable, final AcmeLogger logger) throws AnnuitySecurityException {
		kerbUserId = configrable.getConfiguration().getParameterValue(KERB_USER_ID);
		if (kerbUserId == null || kerbUserId.trim().length() == 0) {
			throw new AnnuitySecurityException("The parameter kerbUserId must be provided.  Its null or blank.");
		}
		logger.fine("kerbUserId: " + kerbUserId);
		
		kerbUserPassword = configrable.getConfiguration().getParameterValue(KERB_USER_PASSWORD);
		if (kerbUserPassword == null || kerbUserPassword.trim().length() == 0) {
			throw new AnnuitySecurityException("The parameter kerbUserPassword must be provided.  Its null or blank.");
		}
		logger.fine("kerbUserPassword: " + kerbUserPassword);
		
		kerbRealm = configrable.getConfiguration().getParameterValue(KERB_REALM);
		if (kerbRealm == null || kerbRealm.trim().length() == 0) {
			throw new AnnuitySecurityException("The parameter kerbRealm must be provided.  Its null or blank.");
		}
		logger.fine("kerbRealm: " + kerbRealm);
		
		kerb5Oid = configrable.getConfiguration().getParameterValue(KERB5_OID);		
		if (kerb5Oid == null || kerb5Oid.trim().length() == 0) {
			throw new AnnuitySecurityException("The parameter kerb5Oid must be provided.  Its null or blank.");
		}
		logger.fine("kerb5Oid: " + kerb5Oid);

		// no error checking as its optional for none spnego calls
		spnegoOid = configrable.getConfiguration().getParameterValue(SPNEGO_OID);		
		logger.fine("spnegoOid: " + spnegoOid);
		
		jaasConfigFile = configrable.getConfiguration().getParameterValue(JAAS_CONFIG_FILE);
		if (jaasConfigFile == null || jaasConfigFile.trim().length() == 0) {
			throw new AnnuitySecurityException("The parameter jaasConfigFile must be provided.  Its null or blank.");
		}
		logger.fine("jaasConfigFile: " + jaasConfigFile);
		
		jaasConfigEntry = configrable.getConfiguration().getParameterValue(JAAS_CONFIG_ENTRY);
		if (jaasConfigEntry == null || jaasConfigEntry.trim().length() == 0) {
			throw new AnnuitySecurityException("The parameter jaasConfigEntry must be provided.  Its null or blank.");
		}
		logger.fine("jaasConfigEntry: " + jaasConfigEntry);
		
		targetServerName = configrable.getConfiguration().getParameterValue(TARGET_SERVER_NAME);			
		if (targetServerName == null || targetServerName.trim().length() == 0) {
			throw new AnnuitySecurityException("The parameter targetServerName must be provided.  Its null or blank.");
		}
		logger.fine("targetServerName: " + targetServerName);
								
		uniqueTicketPerThread = configrable.getConfiguration().getParameterValue(UNIQUE_TICKET_PER_THREAD);
		if (uniqueTicketPerThread == null || uniqueTicketPerThread.trim().length() == 0) {
			throw new AnnuitySecurityException("The parameter: " + UNIQUE_TICKET_PER_THREAD + " must be provided.  Its null or blank. Valid values are true or false");
		}
		if (uniqueTicketPerThread.equalsIgnoreCase("true") || LOGIN_STYLE_NEW_OR_REFRESH_TICKET.equalsIgnoreCase("false")){		
		}else{
			throw new AnnuitySecurityException(UNIQUE_TICKET_PER_THREAD  + " property is not valid. Found value is: " + 
					uniqueTicketPerThread + ". Valid values are: true or false");
		}
		logger.fine(UNIQUE_TICKET_PER_THREAD + ": " + uniqueTicketPerThread);
		
		
		loginStyle = configrable.getConfiguration().getParameterValue(LOGIN_STYLE);
		if (loginStyle == null || loginStyle.trim().length() == 0) {
			throw new AnnuitySecurityException("The parameter loginStyle must be provided.  Its null or blank.");
		}
		if (LOGIN_STYLE_NEW_TICKET.equalsIgnoreCase(loginStyle) || LOGIN_STYLE_NEW_OR_REFRESH_TICKET.equalsIgnoreCase(loginStyle)){		
		}else{
			throw new AnnuitySecurityException("loginStyle property is not valid. Found value is: " + loginStyle
					+ ". Valid values are: " + LOGIN_STYLE_NEW_TICKET + ", "+ LOGIN_STYLE_NEW_OR_REFRESH_TICKET);
		}
		logger.fine("loginStyle: " + loginStyle);
		
		if (LOGIN_STYLE_NEW_OR_REFRESH_TICKET.equalsIgnoreCase(loginStyle)){
			// these props are only needed when login style is newOrRefresh
			refreshAction = configrable.getConfiguration().getParameterValue(REFRESH_ACTION);
			if (refreshAction == null || refreshAction.trim().length() == 0) {
				throw new AnnuitySecurityException("The parameter refreshAction must be provided.  Its null or blank.");
			}
			if (REFRESH_ACTION_REFRESH.equalsIgnoreCase(refreshAction) || REFRESH_ACTION_NEW_TICKET_ONLY.equalsIgnoreCase(refreshAction)){		
			}else{
				throw new AnnuitySecurityException("refreshAction property is not valid. Found value is: " + refreshAction
						+ ". Valid values are: " + REFRESH_ACTION_REFRESH + ", " + REFRESH_ACTION_NEW_TICKET_ONLY );
			}
			logger.fine("refreshAction: " + refreshAction);
			
			try{
				millisBeforeNewTicket = Long.parseLong(configrable.getConfiguration().getParameterValue(MILLIS_BEFORE_NEW_TICKET));
				logger.fine("millisBeforeNewTicket: " + millisBeforeNewTicket);
			}catch (Throwable t) {
				throw new AnnuitySecurityException("The parameter millisBeforeNewTicket must be provided and a valid long value. Current value is: "
						+ configrable.getConfiguration().getParameterValue(MILLIS_BEFORE_NEW_TICKET));
			}			
			try{
				millisBeforeRefreshTicket= Long.parseLong(configrable.getConfiguration().getParameterValue(MILLIS_BEFORE_REFRESH_TICKET));
				logger.fine("millisBeforeRefreshTicket: " + millisBeforeRefreshTicket);
			}catch (Throwable t) {
				throw new AnnuitySecurityException("The parameter millisBeforeRefreshTicket must be provided and a valid long value. Current value is: "
						+ configrable.getConfiguration().getParameterValue(MILLIS_BEFORE_REFRESH_TICKET));
			}						
									
		}
		
	}
	public void setUniqueTicketPerThread(String uniqueTicketPerThread) {
		this.uniqueTicketPerThread = uniqueTicketPerThread;
	}
	public String getUniqueTicketPerThread() {
		return uniqueTicketPerThread;
	}
	public void setSpnegoOid(String spnegoOid) {
		this.spnegoOid = spnegoOid;
	}
	public String getSpnegoOid() {
		return spnegoOid;
	}	
}
