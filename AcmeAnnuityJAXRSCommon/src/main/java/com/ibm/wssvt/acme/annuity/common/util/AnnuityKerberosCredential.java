package com.ibm.wssvt.acme.annuity.common.util;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;

import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

import com.ibm.wssvt.acme.annuity.common.exception.AnnuitySecurityException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
@SuppressWarnings("unchecked")
public class AnnuityKerberosCredential implements PrivilegedExceptionAction{
	private AcmeLogger logger;
	private GSSManager gssManager;
	private AnnuityKerberosProperties annuityKerberosProperties;
	private Oid kerb5MechOid;
	private Oid spnegoMechOid;
	
	public  AnnuityKerberosCredential (GSSManager mgr, Oid kerb5MechOid, Oid spnegoMechOid, AcmeLogger logger, AnnuityKerberosProperties annuityKerberosProperties) {
		this.logger = logger;
		this.gssManager = mgr;
		this.annuityKerberosProperties = annuityKerberosProperties;
		this.kerb5MechOid = kerb5MechOid;
		this.spnegoMechOid = spnegoMechOid;
	}
	
	@Override
	public Object run() throws AnnuitySecurityException {
		try {
			logger.fine("getting Client GSSCredentials");
            GSSName gssName = gssManager.createName(annuityKerberosProperties.getKerbUserId(), GSSName.NT_USER_NAME, kerb5MechOid);
            GSSCredential gssCred = gssManager.createCredential(gssName.canonicalize(kerb5MechOid), GSSCredential.INDEFINITE_LIFETIME, kerb5MechOid, GSSCredential.INITIATE_ONLY);
            if (this.spnegoMechOid != null) {
            	gssCred.add (gssName, GSSCredential.INDEFINITE_LIFETIME, GSSCredential.INDEFINITE_LIFETIME, spnegoMechOid, GSSCredential.INITIATE_ONLY);
            }
            logger.fine("Got Client GSSCredentials");
            return gssCred;
        } catch (GSSException e) {		            	        	   
     	   logger.log(Level.INFO, "got GSSException when trying to execute a doAs method. Error: " + e.getMessage() + logger.getAllLogs(), e);
     	   throw new AnnuitySecurityException("got GSSException when trying to execute a doAs method. Error: " + e.getMessage() , e );
        } catch (Exception e) {		            	        	 
     	   logger.log(Level.INFO, "got GSSException when trying to execute a doAs method. Error: " + e.getMessage()+ logger.getAllLogs(), e);
     	   throw new AnnuitySecurityException("got Exception when trying to execute a doAs method. Error: " + e.getMessage(), e);
        }		               
	}

}
