package com.ibm.wssvt.acme.annuity.common.client.adapter;

import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.exception.AnnuitySecurityException;
import com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3jaxws.EJB3JAXWSJAXWSAsyncServiceProxy;
import com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3jaxws.EJB3JAXWSJAXWSSyncServiceProxy;
import com.ibm.wssvt.acme.annuity.common.servicelookup.jaxrs.JAXRSServiceLookup;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/**
 * Date created: 2015-01-15 
 * Author: Tam Dinh
 * LastChangedBy: Tam
 * Description:  Adapter to test sync and async JAXRS-2.0  
 */
public class JAXRS20ServerAdapter extends AbstractAnnuityServerAdapter{

	private static final long serialVersionUID = -7761738176018995756L;
	
	protected IAnnuityService getAnnuityService() throws ServerAdapterCommunicationException {
		IAnnuityService proxy = null;
		String asyncFlag = null;
		AcmeLogger logger = getLogger(getClass().getName());
		String serverURL = getConfiguration().getParameterValue("serverURL");
		
		if (getConfiguration().getParameterValue("useAsync") != null) {
			asyncFlag = getConfiguration().getParameterValue("useAsync");
		}
		else {
			logger.fine("Invalid client configuration because useAsync key is missing.  "
						+ " Need to specify <entry><key>useAsync</key><value>true or false</value></entry> "
						+ " in client config file for JAXRS20ServerAdapter");
			System.out.println("Invalid client configuration because useAsync key is missing.  "
					+ " Need to specify <entry><key>useAsync</key><value>true or false</value></entry> "
					+ " in client config file for JAXRS20ServerAdapter");
			return proxy;
		}
		
		logger.fine("url: " + serverURL);
		logger.fine("Asynch: " + Boolean.parseBoolean(asyncFlag));
		
		if (Boolean.parseBoolean(asyncFlag)){
			try {
				logger.fine("Lookup using jaxrs-2.0 Async client ");
				proxy = JAXRSServiceLookup.getAnnuityJAXRS20AsyncService((Configrable<String, String>)this, logger);
			} catch (AnnuitySecurityException e) {
				throw new ServerAdapterCommunicationException(e.getMessage(), e);
			}
		}
		else {
			try {
				logger.fine("Lookup using jaxrs-2.0 sync client ");
				proxy = JAXRSServiceLookup.getAnnuityJAXRS20Service((Configrable<String, String>)this, logger);
			} catch (AnnuitySecurityException e) {
				throw new ServerAdapterCommunicationException(e.getMessage(), e);
			}
		}
				
		return proxy;	
	}
}
