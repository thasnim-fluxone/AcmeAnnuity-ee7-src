package com.ibm.wssvt.acme.annuity.common.client.adapter;

import javax.jms.JMSException;
import javax.naming.NamingException;

import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3mdb.EJB3MDBServiceLookup;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;


public class EJB30MDBServerAdapter extends AbstractAnnuityServerAdapter{
	
	private static final long serialVersionUID = 2178434150881666470L;

	protected IAnnuityService getAnnuityService() throws ServerAdapterCommunicationException {
		IAnnuityService proxy = null;
		AcmeLogger logger = getLogger(getClass().getName());						
		try {				
			proxy = EJB3MDBServiceLookup.getAnnuityEJB3MDBService(this, logger);
		} catch (JMSException e) {
			throw new ServerAdapterCommunicationException(e.getMessage(), e);
		} catch (NamingException e) {
			throw new ServerAdapterCommunicationException(e.getMessage(), e);		
		}					
		return proxy;
	}
		
}
