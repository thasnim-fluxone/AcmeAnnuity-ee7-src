package com.ibm.wssvt.acme.annuity.common.client.adapter;

import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.exception.AnnuitySecurityException;
import com.ibm.wssvt.acme.annuity.common.servicelookup.jaxrs.JAXRSServiceLookup;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;


/**
 * $Rev: 807 $
 * $Date: 2007-09-24 01:29:31 -0500 (Mon, 24 Sep 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class JAXRSServerAdapter extends AbstractAnnuityServerAdapter{
		
	private static final long serialVersionUID = -7761738176018995756L;
	
	protected IAnnuityService getAnnuityService() throws ServerAdapterCommunicationException {
		IAnnuityService proxy = null;
		AcmeLogger logger = getLogger(getClass().getName());
		// there is no service to lookup ~
		String serverURL = getConfiguration().getParameterValue("serverURL");
		logger.fine("url: " + serverURL);
				
		try {
			proxy = JAXRSServiceLookup.getAnnuityJAXRSService((Configrable<String, String>)this, logger);
		} catch (AnnuitySecurityException e) {
			throw new ServerAdapterCommunicationException(e.getMessage(), e);
		}				
		return proxy;
	}
	
}