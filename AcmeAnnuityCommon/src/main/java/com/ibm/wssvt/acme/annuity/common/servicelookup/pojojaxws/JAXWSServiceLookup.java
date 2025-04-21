package com.ibm.wssvt.acme.annuity.common.servicelookup.pojojaxws;

//import java.net.MalformedURLException;
import java.net.URL;
//import java.util.HashMap;
//import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

//import com.ibm.websphere.webservices.Constants;
//import com.ibm.ws.util.Base64;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl.AnnuityMgmtSvcPojoJAXWS;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl.AnnuityMgmtSvcPojoJAXWSImpl;
import com.ibm.wssvt.acme.annuity.common.exception.AnnuitySecurityException;
//import com.ibm.wssvt.acme.annuity.common.util.AnnuityKerberosProperties;
//import com.ibm.wssvt.acme.annuity.common.util.AnnuityKerberosSecurity;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.exception.ExceptionFormatter;
import com.ibm.wssvt.acme.common.exception.InvalidConfigurationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;


public class JAXWSServiceLookup {
	
	
	private static final String JAXWS_SERVICE_KEY = "jaxwsPojoServiceURL";
	private static final String USE_ASYNC_KEY = "useAsync";
	
	public static IAnnuityService getAnnuityJAXWSService(AnnuityMgmtSvcPojoJAXWSImpl injectedService, Configrable<String, String> configrable, AcmeLogger logger) throws InvalidConfigurationException, AnnuitySecurityException  {
		String wsdlURL = "file:/META-INF/wsdl/AnnuityMgmtSvcPojoJAXWSImpl.wsdl";
		String jaxwsServiceURL = configrable.getConfiguration().getParameterValue(JAXWS_SERVICE_KEY);
		AnnuityMgmtSvcPojoJAXWS port;
		try{		
			if (injectedService != null) {
				logger.fine("using injected service: " + injectedService);	
				port = injectedService.getAnnuityMgmtSvcPojoJAXWSImplPort();
				((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, jaxwsServiceURL);
				logger.fine("using injected service port: " + port);
			}else{
				//URL url = new URL(wsdlURL);
			    URL url = JAXWSServiceLookup.class.getClassLoader().getResource("META-INF/wsdl/AnnuityMgmtSvcPojoJAXWSImpl.wsdl");
				AnnuityMgmtSvcPojoJAXWSImpl service = new AnnuityMgmtSvcPojoJAXWSImpl(
						url, new QName("http://jaxws.client.common.annuity.acme.wssvt.ibm.com/pojoimpl/", 
						"AnnuityMgmtSvcPojoJAXWSImpl"));	
				port = service.getAnnuityMgmtSvcPojoJAXWSImplPort();				
				((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, jaxwsServiceURL);
				logger.fine("using injected service port: " + port);					
				logger.fine("the ws port is: "  + port);		
			}
		/*	
			if ("true".equalsIgnoreCase(configrable.getConfiguration().getParameterValue("useKerberosSecurity"))){						
				byte[] serverKey = null;
				AnnuityKerberosProperties akp = new AnnuityKerberosProperties();
				akp.setKerbProperties(configrable, logger);															
				AnnuityKerberosSecurity annuityKerberosSecurity = new AnnuityKerberosSecurity();
				if (AnnuityKerberosProperties.LOGIN_STYLE_NEW_TICKET.equalsIgnoreCase(akp.getLoginStyle())){
					serverKey = annuityKerberosSecurity.loginForSPNEGO(akp, logger);												
				}else if (AnnuityKerberosProperties.LOGIN_STYLE_NEW_OR_REFRESH_TICKET.equalsIgnoreCase(akp.getLoginStyle())){
					serverKey = annuityKerberosSecurity.renewLoginForSPNEGO(akp, logger);
				}
				Map<String, String> spnegoData = new HashMap<String, String>();
				spnegoData.put("Cookie","ClientAuthenticationToken=FFEEBCC"); 
				spnegoData.put("Authorization", "Negotiate " + Base64.encode(serverKey));
				((BindingProvider)port).getRequestContext().put(Constants.REQUEST_TRANSPORT_PROPERTIES, spnegoData);
			}					
		*/	
			String useAsuncFlag = configrable.getConfiguration().getParameterValue(USE_ASYNC_KEY);
			if (Boolean.parseBoolean(useAsuncFlag)){
				return new AnnuityJAXWSServiceProxyAsync(port, logger);
			}else{
				return new AnnuityJAXWSServiceProxy(port);
			}		
		} catch (Exception e) {
			logger.warning("The url is invalid.  url value is: " + wsdlURL 
					+ " This url should refer to a local file that is bundled with this application logic."
					+ " Most probably this is a defect in the Application.");
			throw new InvalidConfigurationException ("The url is invalid.  url value is: " + wsdlURL 
					+ " Error: " +  ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs());
		}

	}
	
}
