package com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3mdb;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.JMSConnectionFactory;
import javax.inject.Inject;
import javax.annotation.Resource;

import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.exception.ExceptionFormatter;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class EJB3MDBServiceLookup {
	static final long JMSREPLYTIMEOUT = 30000;	
	public static IAnnuityService getAnnuityEJB3MDBService(Configrable<String, String> configrable, AcmeLogger logger) throws NamingException, JMSException{
		try{
			String connFactoryJNDI = configrable.getConfiguration().getParameterValue("ACMEAnnuityJMSConnectionFactoryJNDI");
			String destQueueJNDI = configrable.getConfiguration().getParameterValue("AcmeAnnuityMgmtSvcEJB30MDBQueueJNDI");
			String consumerReceiveTimeoutStr = configrable.getConfiguration().getParameterValue("consumerReceiveTimeout");
			
			String useJMS20 = null;
			if (configrable.getConfiguration().getParameterValue("JMS2.0") != null ) {
				useJMS20 = configrable.getConfiguration().getParameterValue("JMS2.0");
				logger.fine("Testing JMS 2.0 : " +useJMS20);
			}
			
			long consumerReceiveTimeout = JMSREPLYTIMEOUT;
			if (connFactoryJNDI == null || connFactoryJNDI.trim().length() ==0) {
				connFactoryJNDI = "jms/ACMEAnnuityJMSConnectionFactory";
			}
			if (destQueueJNDI == null || destQueueJNDI.trim().length() ==0) {
				destQueueJNDI = "jms/AcmeAnnuityMgmtSvcEJB30MDBQueue";
			}
			
			if (consumerReceiveTimeoutStr == null || consumerReceiveTimeoutStr.trim().length() ==0) {
				consumerReceiveTimeout = JMSREPLYTIMEOUT;
			}else {
			    try{
				    consumerReceiveTimeout = Long.parseLong(consumerReceiveTimeoutStr);
			    } catch(Exception e){
				    consumerReceiveTimeout = JMSREPLYTIMEOUT;
			    }
			}
	
			Properties p =new Properties();						
			p.put(Context.INITIAL_CONTEXT_FACTORY, configrable.getConfiguration().getParameterValue("initialContextFactory"));
			String iiopAddress = configrable.getConfiguration().getParameterValue("iiopAddress");
			p.put(Context.PROVIDER_URL, iiopAddress);				
			logger.fine("supplied iiop address is: " + iiopAddress);
			InitialContext ic;
	/*		if (configrable.getConfiguration().getParameterValue("initialContextFactory") != null &&
					configrable.getConfiguration().getParameterValue("iiopAddress") != null){
				ic = new InitialContext(p);	
			}else{
			*/
				ic = new InitialContext();
			// }
			
			logger.fine("obtained initial context: " + ic);
	
			ConnectionFactory qcf = (javax.jms.ConnectionFactory) ic.lookup(connFactoryJNDI);
			logger.fine("ConnectionFactory qcf: " + qcf);
			
			
			// Finding the Queue Destination
	        Destination queue = (Destination) ic.lookup(destQueueJNDI);
	        logger.fine("Destination queue : " + queue);  
	        
	        //return new AnnuityEJB3MDBServiceProxy(qcf, queue, consumerReceiveTimeout, logger);
	        return new AnnuityEJB3MDBServiceProxy(qcf, queue, consumerReceiveTimeout, logger, useJMS20);
	        
	        
		}catch(NamingException e){
			String msg = "Failed to obtain initial context.  " +
					"initialContextFactory:" + configrable.getConfiguration().getParameterValue("initialContextFactory") +
					"iiopAddress: " + configrable.getConfiguration().getParameterValue("iiopAddress")
					+ " Reported Error: " + ExceptionFormatter.deepFormatToString(e);
			throw new NamingException(msg);
		}		
	}
	

}
