package com.ibm.wssvt.acme.annuity.common.client.adapter;

import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.AnnuityMgmtSvcEJB30JAXWSImpl;
import com.ibm.wssvt.acme.annuity.common.exception.AnnuitySecurityException;
import com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3jaxws.EJB3JAXWSJAXWSServiceLookup;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.exception.InvalidConfigurationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class EJB30JAXWSJAXWSServerAdapter extends AbstractAnnuityServerAdapter{
		
	private static final long serialVersionUID = -7761738176018995756L;		
	private static final String CONNECTION_MODE_KEY = "connectionMode"; // default to true
	

			
	protected IAnnuityService getAnnuityService() throws ServerAdapterCommunicationException {
		AcmeLogger logger = getLogger(getClass().getName());							
		IAnnuityService result = null;
		result = getService(logger);	
		return result;								
							
	}	
		
	private IAnnuityService getService(AcmeLogger logger) throws ServerAdapterCommunicationException {
		AnnuityMgmtSvcEJB30JAXWSImpl service = null;
		if ("useInjection".equalsIgnoreCase(getConfiguration().getParameterValue(CONNECTION_MODE_KEY))){
			service = (AnnuityMgmtSvcEJB30JAXWSImpl)getClientContext().getInjectedObjects().get("AnnuityMgmtSvcEJB30JAXWSImpl");
			if (service == null){
				throw new ServerAdapterCommunicationException("configuration for Adapter: " + getId() 
						+ " which is: " + EJB30JAXWSJAXWSServerAdapter.class.getName() + " is setup with connection mode = useInjection, but the injected object value is null");
			}
		}
		else{
			if ("1".equalsIgnoreCase(getConfiguration().getParameterValue("ejbServiceRefNumber"))) {		
				logger.info("using ejb30 service-ref 1 ...");
				service = (AnnuityMgmtSvcEJB30JAXWSImpl)getClientContext().getInjectedObjects().get("AnnuityMgmtSvcEJB30JAXWSImpl");
			}else if ("2".equalsIgnoreCase(getConfiguration().getParameterValue("ejbServiceRefNumber"))) {	
				logger.info("using ejb30 service-ref 2 ...");
				service = (AnnuityMgmtSvcEJB30JAXWSImpl)getClientContext().getInjectedObjects().get("AnnuityMgmtSvcEJB30JAXWSImpl2");
			}else if ("3".equalsIgnoreCase(getConfiguration().getParameterValue("ejbServiceRefNumber"))) {	
				logger.info("using ejb30 service-ref 3 ...");
				service = (AnnuityMgmtSvcEJB30JAXWSImpl)getClientContext().getInjectedObjects().get("AnnuityMgmtSvcEJB30JAXWSImpl3");
			}
		}
		try {
			return EJB3JAXWSJAXWSServiceLookup.getAnnuityJAXWSService(service, this, logger);		
		} catch (InvalidConfigurationException e) {
			throw new ServerAdapterCommunicationException(e.getMessage(), e);
		} catch (AnnuitySecurityException e) {
			throw new ServerAdapterCommunicationException(e.getMessage(), e);
		}					
	}
	
}
