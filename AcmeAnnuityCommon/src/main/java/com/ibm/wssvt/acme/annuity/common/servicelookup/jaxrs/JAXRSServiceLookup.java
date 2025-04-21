package com.ibm.wssvt.acme.annuity.common.servicelookup.jaxrs;

import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.exception.AnnuitySecurityException;
//import com.ibm.wssvt.acme.annuity.common.util.AnnuityKerberosProperties;
//import com.ibm.wssvt.acme.annuity.common.util.AnnuityKerberosSecurity;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class JAXRSServiceLookup {

	//JAX-RS 1.1 client
	public static IAnnuityService getAnnuityJAXRSService(Configrable<String, String> configs, AcmeLogger logger) throws AnnuitySecurityException {
		String serverURL = configs.getConfiguration().getParameterValue("serverURL");
		String contentType = configs.getConfiguration().getParameterValue("contentType");
		byte[] spngoKey = null;
//		if ("SPNEGO_AUTH".equalsIgnoreCase(configs.getConfiguration().getParameterValue("useSecurity"))){								
//			AnnuityKerberosProperties akp = new AnnuityKerberosProperties();
//			akp.setKerbProperties(configs, logger);															
//			AnnuityKerberosSecurity annuityKerberosSecurity = new AnnuityKerberosSecurity();
//			if (AnnuityKerberosProperties.LOGIN_STYLE_NEW_TICKET.equalsIgnoreCase(akp.getLoginStyle())){
//				spngoKey = annuityKerberosSecurity.loginForSPNEGO(akp, logger);												
//			}else if (AnnuityKerberosProperties.LOGIN_STYLE_NEW_OR_REFRESH_TICKET.equalsIgnoreCase(akp.getLoginStyle())){
//				spngoKey = annuityKerberosSecurity.renewLoginForSPNEGO(akp, logger);
//			}
//		}

		return new AnnuityJAXRSServicePorxy(configs, serverURL, contentType, logger, spngoKey);
		
	}
	//JAX-RS 2.0 client
	public static IAnnuityService getAnnuityJAXRS20Service(Configrable<String, String> configs, AcmeLogger logger) throws AnnuitySecurityException {
		String serverURL = configs.getConfiguration().getParameterValue("serverURL");
		String contentType = configs.getConfiguration().getParameterValue("contentType");
		
		return new AnnuityJAXRS20ServiceProxy(configs, serverURL, contentType, logger);
		
	}
	
	//JAX-RS 2.0 async client
	public static IAnnuityService getAnnuityJAXRS20AsyncService(Configrable<String, String> configs, AcmeLogger logger) throws AnnuitySecurityException {
		String serverURL = configs.getConfiguration().getParameterValue("serverURL");
		String contentType = configs.getConfiguration().getParameterValue("contentType");

		return new AnnuityJAXRS20AsyncServiceProxy(configs, serverURL, contentType, logger);
		
	}
}

