package com.ibm.wssvt.acme.annuity.common.client.adapter;

import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl.AnnuityMgmtSvcPojoJAXWSImpl;
import com.ibm.wssvt.acme.annuity.common.exception.AnnuitySecurityException;
import com.ibm.wssvt.acme.annuity.common.servicelookup.pojojaxws.JAXWSServiceLookup;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.exception.InvalidConfigurationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class JAXWSServerAdapter extends AbstractAnnuityServerAdapter{
		
	private static final long serialVersionUID = -7761738176018995756L;	
	private static final String CONNECTION_MODE_KEY = "connectionMode"; 
			
	protected IAnnuityService getAnnuityService() throws ServerAdapterCommunicationException {
		AcmeLogger logger = getLogger(getClass().getName());							
		IAnnuityService result = null;
		result = getService(logger);	
		return result;												
	}	
	
	
	private IAnnuityService getService(AcmeLogger logger) throws ServerAdapterCommunicationException {
		AnnuityMgmtSvcPojoJAXWSImpl service = null;
		if ("useInjection".equalsIgnoreCase(getConfiguration().getParameterValue(CONNECTION_MODE_KEY))){
			service = (AnnuityMgmtSvcPojoJAXWSImpl)getClientContext().getInjectedObjects().get("AnnuityMgmtSvcPojoJAXWSImpl");
			if (service == null){
				throw new ServerAdapterCommunicationException("configuration for Adapter: " + getId() 
						+ " which is: " + JAXWSServerAdapter.class.getName() + " is setup with connection mode = useInjection, but the injected object value is null");
			}
		}		
		try {
			return JAXWSServiceLookup.getAnnuityJAXWSService(service, this, logger);		
		} catch (InvalidConfigurationException e) {
			throw new ServerAdapterCommunicationException(e.getMessage(), e);
		} catch (AnnuitySecurityException e) {
			throw new ServerAdapterCommunicationException(e.getMessage(), e);
		}				
	}

}
