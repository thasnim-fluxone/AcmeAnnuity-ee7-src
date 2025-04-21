package com.ibm.wssvt.acme.annuity.business.impl.ejb30mdb;

import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RunAs;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import javax.jms.JMSContext;
import javax.jms.JMSRuntimeException;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.business.AnnuityBusinessServiceLookup;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerBusinessModuleException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityAction;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityJMSMessage;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityLoggerFactory;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.exception.ExceptionFormatter;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

@MessageDriven(
		activationConfig = { @ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Queue"
		) })
@RunAs("admins")
@DeclareRoles("admins")
public class JMS20AcmeAnnuityMgmtSvcEJB30MDBBean implements MessageListener {

	@Resource(name = "jms/ACMEAnnuityJMSConnectionFactory")
	private ConnectionFactory jmsConnFactory;
	
	    
	public void onMessage(Message message) {       
    	//ObjectMessage msg = null;    	
    	AnnuityJMSMessage annuityJMSMessage = null;
    	Destination replyDest = null;    	
    	AcmeLogger logger = null;
    	Logger systemLogger = Logger.getLogger(getClass().getName());
    	
    	try {
    		systemLogger.fine("jms2.0: Receive message in queue.  Validating messsage...");
			validateInputMessageJMS20(message);
			replyDest = message.getJMSReplyTo();
		} catch (ServerBusinessModuleException e) {
			// there is not much you can do here other than log it.
			systemLogger.warning("Received a message that is NOT valid. Error:  " + ExceptionFormatter.deepFormatToString(e)
					+ "This message will only be reported here and will not be returned to the client." 
					+ "  This is usually an application error. Check application logic.");
			e.printStackTrace();
			return;
		} catch (JMSException e) {			
			systemLogger.warning("Received a JMS Exception while trynig to verify the incoming message. Error:  " + ExceptionFormatter.deepFormatToString(e)
					+ "This message will only be reported here and will not be returned to the client.");					
			e.printStackTrace();
			return;
		}  
		
    	try {    	    		
    		
    		// JMS 2.0 -Simplified API
    		annuityJMSMessage = message.getBody(AnnuityJMSMessage.class);
    		
    		logger = getLogger(annuityJMSMessage.getInput());
    		logger.fine("jms20: inside the onMessage of the MDB. message: " + message);
    		logger.fine("Calling the Annuity Service ...");
    		
        	Object result = callAnnuityService(annuityJMSMessage, logger);
        	
        	logger.fine("Calling the Annuity Service Completed. Returned: " + result  + " note: null is a valid value.");        	
    		logger.fine("message.getJMSReplyTo()" + replyDest);
    		
			annuityJMSMessage = new AnnuityJMSMessage();			
        	annuityJMSMessage.setOutput(result);
        	
    		logger.fine("ready to send reply annuityJMSMessage: " + annuityJMSMessage);
    		logger.fine("annuityJMSMessage results are: " + result);
    		
    		// JMS 2.0 -Simplified API
    		sendReplyJMS20(replyDest, annuityJMSMessage, logger);
    		
    		logger.fine("reply sent");    		
		} catch (Exception e) {			
			if (replyDest == null) {
				String errorMsg = "An exception occured. The code wants to reply to the caller with the exception, but the " +
					"but reply destination is null. I can only log it here.  Error: "  + ExceptionFormatter.deepFormatToString(e);
				if (logger != null) {
					logger.log(Level.INFO, errorMsg, e);
				}
				systemLogger.info(errorMsg);				
				e.printStackTrace();
				return;
			}			

			// we can post the error back to the caller.
			annuityJMSMessage = new AnnuityJMSMessage();			
			annuityJMSMessage.setOutput(e);			
			
			// JMS 2.0, call sendReplyJSM2.0()
			logger.fine("jms2.0: calling sendReplyJMS20 ...");
    		sendReplyJMS20(replyDest, annuityJMSMessage, logger);
		} 
    }


	
	private void sendReplyJMS20(Destination replyDest,
			AnnuityJMSMessage annuityJMSMessage, AcmeLogger logger) {		        	        	    				
		
		logger.fine("jms2.0: in sendReplyJMS20 ");
		try (JMSContext context = jmsConnFactory.createContext();){
			context.createProducer().send(replyDest, annuityJMSMessage);
			logger.fine("Sent reply messsage");
			
		} catch (JMSRuntimeException ex) {
			Logger.getLogger(getClass().getName()).warning("Failed to send JMS reply. JMSException error. Error: " + ExceptionFormatter.deepFormatToString(ex) );
			ex.printStackTrace();
			
			// not much i can do!  I cannot send the message back to the client
			//if (logger != null) {
			//	Logger.getLogger(getClass().getName()).warning("Failed to send JMS reply. JMSException error. Error: " + ExceptionFormatter.deepFormatToString(ex));
			//	logger.log(Level.WARNING, "Failed to send JMS reply. JMSException error. Error: " + ExceptionFormatter.deepFormatToString(ex), ex);
			//	ex.printStackTrace();
			//}else{			
			//	Logger.getLogger(getClass().getName()).warning("Failed to send JMS reply. JMSException error. Error: " + ExceptionFormatter.deepFormatToString(ex));				
			//	ex.printStackTrace();
			//}
			
		 } 
	}

	
	private Message validateInputMessageJMS20(Message message) throws JMSException, ServerBusinessModuleException {
		 
		
		if (message == null ){
			throw new ServerBusinessModuleException ("The JMS parameter message is null.");
		}
		if (!(message instanceof ObjectMessage)){
			throw new ServerBusinessModuleException ("The JMS parameter message is invalid. It must be of type ObjectMessage. " +
					"found class is: " + message.getClass());
		}
		
		AnnuityJMSMessage annuityJMSMessage = message.getBody(AnnuityJMSMessage.class);
				
		if (annuityJMSMessage.getInput() == null){
			throw new ServerBusinessModuleException ("The JMS parameter message contains invalid object." +
					"The internal object (the AnnuityJMSMessage) must contain an input object, but it was null");					
		}
		if (!(annuityJMSMessage.getInput() instanceof Configrable)) {
			throw new ServerBusinessModuleException ("The JMS parameter message contains invalid object." +
				"The internal object (the AnnuityJMSMessage) must contain an input object of type Configrable. The class that was found is:"
					+ annuityJMSMessage.getInput().getClass());
		}
		if (message.getJMSReplyTo() == null) {
			throw new ServerBusinessModuleException ("The JMS Reply is null.  It's expected that each operation to have provided a JMS Reply.");
		}	
		
		return message;
	}
	
	private Object callAnnuityService(AnnuityJMSMessage annuityJMSMessage, AcmeLogger logger) throws EntityAlreadyExistsException, InvalidArgumentException, ServerInternalErrorException, EntityNotFoundException{		
		AnnuityAction action = annuityJMSMessage.getAction();		
		if (AnnuityAction.CREATE_ANNUITY.equals(action)){
			return createAnnuity((IAnnuity)annuityJMSMessage.getInput());
		}else if (AnnuityAction.FIND_ANNUITY_BY_ID.equals(action)){
			return findAnnuityById((IAnnuity)annuityJMSMessage.getInput());
		}else if (AnnuityAction.UPDATE_ANNUITY.equals(action)){
			return updateAnnuity((IAnnuity)annuityJMSMessage.getInput());
		}else if (AnnuityAction.DELETE_ANNUITY.equals(action)){
			deleteAnnuity((IAnnuity)annuityJMSMessage.getInput());
			return null;
		}else if (AnnuityAction.FIND_ANNUITY_HOLDER.equals(action)){
			return findAnnuityHolder((IAnnuity)annuityJMSMessage.getInput());			
			
		}else if (AnnuityAction.CREATE_ANNUITY_HOLDER.equals(action)){
			return createAnnuityHolder((IAnnuityHolder)annuityJMSMessage.getInput());			
		}else if (AnnuityAction.FIND_HOLDER_BY_ID.equals(action)){
			return findHolderById((IAnnuityHolder)annuityJMSMessage.getInput());			
		}else if (AnnuityAction.UPDATE_ANNUITY_HOLDER.equals(action)){
			return updateAnnuityHolder((IAnnuityHolder)annuityJMSMessage.getInput());			
		}else if (AnnuityAction.DELETE_ANNUITY_HOLDER.equals(action)){
			deleteAnnuityHolder((IAnnuityHolder)annuityJMSMessage.getInput());
			return null;
		}else if (AnnuityAction.FIND_HOLDER_ANNUITIES.equals(action)){
			return findHolderAnnuities((IAnnuityHolder)annuityJMSMessage.getInput());			
		
		}else if(AnnuityAction.CREATE_CONTACT.equals(action)){
			return createContact((IContact)annuityJMSMessage.getInput());
		}else if (AnnuityAction.FIND_CONTACT_BY_ID.equals(action)){
			return findContactById((IContact)annuityJMSMessage.getInput());
		}else if (AnnuityAction.UPDATE_CONTACT.equals(action)){
			return updateContact((IContact)annuityJMSMessage.getInput());
		}else if (AnnuityAction.DELETE_CONTACT.equals(action)){
			deleteContact((IContact)annuityJMSMessage.getInput());
			return null;
		
		}else if(AnnuityAction.CREATE_PAYOR.equals(action)){
			return createPayor((IPayor)annuityJMSMessage.getInput());
		}else if (AnnuityAction.FIND_PAYOR_BY_ID.equals(action)){
			return findPayorById((IPayor)annuityJMSMessage.getInput());
		}else if (AnnuityAction.UPDATE_PAYOR.equals(action)){
			return updatePayor((IPayor)annuityJMSMessage.getInput());
		}else if (AnnuityAction.DELETE_PAYOR.equals(action)){
			deletePayor((IPayor)annuityJMSMessage.getInput());
			return null;
		}else if (AnnuityAction.FIND_PAYOR_ANNUITIES.equals(action)){
			return findPayorAnnuities((IPayor)annuityJMSMessage.getInput());
		
		}else if(AnnuityAction.CREATE_PAYOUT.equals(action)){
			return createPayout((IPayout)annuityJMSMessage.getInput());
		}else if (AnnuityAction.FIND_PAYOUT_BY_ID.equals(action)){
			return findPayoutById((IPayout)annuityJMSMessage.getInput());
		}else if (AnnuityAction.UPDATE_PAYOUT.equals(action)){
			return updatePayout((IPayout)annuityJMSMessage.getInput());
		}else if (AnnuityAction.DELETE_PAYOUT.equals(action)){
			deletePayout((IPayout)annuityJMSMessage.getInput());
			return null;
		
		
		}else if (AnnuityAction.FIND_RIDER_BY_ID.equals(action)){
		return findRiderById((IRider)annuityJMSMessage.getInput());
		}else if (AnnuityAction.DELETE_RIDER.equals(action)){
			deleteRider((IRider)annuityJMSMessage.getInput());
			return null;
		}		
		
		logger.info("An Action was reqursted, but there is no handler for this action. The action is: " + action);
		throw new ServerBusinessModuleException("JMS Action is not supported by this service. Requested Action is: " + action);
	}
	
	private IAnnuity findAnnuityById(IAnnuity annuity) 
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException  {		
		IAnnuity result = null;
		try {
			result = getAnnuityService(annuity).findAnnuityById(annuity);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);		
		}
		return result;
	}
	
	private IAnnuity createAnnuity(IAnnuity ann) 
		throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {		
		IAnnuity result = null;
		try {
			result = getAnnuityService(ann).createAnnuity(ann);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);		
		}
		return result;
	}

	private IAnnuity updateAnnuity(IAnnuity ann) 
		throws ServerInternalErrorException, InvalidArgumentException {
		IAnnuity result = null;
		try {
			result = getAnnuityService(ann).updateAnnuity(ann);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);		
		}
		return result;
	}

	private void deleteAnnuity(IAnnuity annuity) 
		throws ServerInternalErrorException, InvalidArgumentException {
		try {
			getAnnuityService(annuity).deleteAnnuity(annuity);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);	
		}	
	}

	private IAnnuityHolder findAnnuityHolder(IAnnuity annuity) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		IAnnuityHolder result = null;
		try {
			result = getAnnuityService(annuity).findAnnuityHolder(annuity);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);		
		}
		return result;
	}

	private IContact findContactById(IContact contact) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		IContact result = null;
		try {
			result = getAnnuityService(contact).findContactById(contact);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);	
		}
		return result;
	}

	private List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder) throws ServerInternalErrorException, InvalidArgumentException {
		List<IAnnuity> result = null;
		try {
			result = getAnnuityService(annuityHolder).findHolderAnnuities(annuityHolder);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);		
		}
		return result;
	}

	private IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		IAnnuityHolder result = null;
		try {
			result = getAnnuityService(annuityHolder).findHolderById(annuityHolder);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);	
		}
		return result;
	}

	private IAnnuityHolder createAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		IAnnuityHolder result = null;
		try {
			result = getAnnuityService(annHolder).createAnnuityHolder(annHolder);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);		
		}
		return result;
	}

	private IContact createContact(IContact contact) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		IContact result = null;
		try {
			result = getAnnuityService(contact).createContact(contact);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);	
		}
		return result;
	}

	private IPayor createPayor(IPayor payor) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		IPayor result = null;
		try {
			result = getAnnuityService(payor).createPayor(payor);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		return result;
	}

	private void deleteAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, InvalidArgumentException {		
		try {
			getAnnuityService(annHolder).deleteAnnuityHolder(annHolder);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}			
	}

	private void deleteContact(IContact contact) throws ServerInternalErrorException, InvalidArgumentException {
		try {
			getAnnuityService(contact).deleteContact(contact);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);	
		}
	}

	private void deletePayor(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {	
		try {
			getAnnuityService(payor).deletePayor(payor);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}		
	}

	private IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, InvalidArgumentException {
		IAnnuityHolder result = null;
		try {
			result = getAnnuityService(annHolder).updateAnnuityHolder(annHolder);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		return result;	
	}

	private IContact updateContact(IContact contact) throws ServerInternalErrorException, InvalidArgumentException {
		IContact result = null;
		try {
			result = getAnnuityService(contact).updateContact(contact);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);	
		}
		return result;
	}

	private IPayor updatePayor(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		IPayor result = null;
		try {
			result = getAnnuityService(payor).updatePayor(payor);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		return result;
	}

	private IPayout createPayout(IPayout payout) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		IPayout result = null;
		try {
			result = getAnnuityService(payout).createPayout(payout);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);		
		}
		return result;
	}

	private IPayout updatePayout(IPayout payout) throws ServerInternalErrorException, InvalidArgumentException {
		IPayout result = null;
		try {
			result = getAnnuityService(payout).updatePayout(payout);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);	
		}
		return result;
	}
	private void deletePayout(IPayout payout) throws ServerInternalErrorException, InvalidArgumentException {
		try {
			getAnnuityService(payout).deletePayout(payout);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);	
		}
	}

	private void deleteRider(IRider rider) throws ServerInternalErrorException, InvalidArgumentException {
		try {
			getAnnuityService(rider).deleteRider(rider);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);		
		}
		
	}

	private IPayor findPayorById(IPayor payor) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		IPayor result = null;
		try {
			result = getAnnuityService(payor).findPayorById(payor);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);		
		}
		return result;
	}

	private IPayout findPayoutById(IPayout payout) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		IPayout result = null;
		try {
			result = getAnnuityService(payout).findPayoutById(payout);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		return result;
	}

	private IRider findRiderById(IRider rider) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		IRider result = null;
		try {
			result = getAnnuityService(rider).findRiderById(rider);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);		
		}
		return result;
	}

	private List<IAnnuity> findPayorAnnuities(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		List<IAnnuity> result = null;
		try {
			result = getAnnuityService(payor).findPayorAnnuities(payor);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		return result;
	}

	
		
	private void convertServerException(Exception e) throws ServerPersistenceModuleException, ServerBusinessModuleException {
		if (e instanceof ServerInternalErrorException){
			if (e instanceof ServerPersistenceModuleException){
				throw new ServerPersistenceModuleException(e);
			}
			throw new ServerBusinessModuleException (e);				
		}else if (e instanceof RemoteException) {		
			throw new ServerBusinessModuleException ("Remote Exception" + e.getMessage(), e);
		}else{
			throw new ServerBusinessModuleException ("Unknown Server Exception: " + e.getMessage(), e);
		}
	}
	
    private AcmeLogger getLogger(Configrable<String, String> configrable) {
		return AnnuityLoggerFactory.getAcmeServerLogger(configrable, getClass().getName());
	}
    
    protected IAnnuityService getAnnuityService(Configrable<String, String> configrable) throws ServerBusinessModuleException {		
		try {			
			return new AnnuityBusinessServiceLookup().getAnnuityAnnuityService(configrable);
		} catch (Exception e) {
			AcmeLogger logger = getLogger(configrable);
			logger.info("Annuity Service lookup returned the error: " +e.getMessage());			
			throw new ServerBusinessModuleException(e.getMessage(), e);
		} 
	}

}
