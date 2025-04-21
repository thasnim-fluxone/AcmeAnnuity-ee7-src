package com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3mdb;

import java.util.List;
import java.util.logging.Level;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import javax.jms.JMSContext;
import javax.jms.JMSConsumer;
import javax.jms.JMSRuntimeException;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.JMSConnectionFactory;
import javax.inject.Inject;
import javax.annotation.Resource;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerBusinessModuleException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityAction;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityJMSMessage;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.exception.ExceptionFormatter;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class AnnuityEJB3MDBServiceProxy implements IAnnuityService{
	private ConnectionFactory connFactory = null;
	private Destination queueDest = null;
	private long replyConsumerTimeout = 10000;
	private AcmeLogger logger = null;
	private String jms20 = "false";
	private AnnuityJMSMessage annuityJMSMessageReply = null;
	private Destination receiveQ = null;
	
	public AnnuityEJB3MDBServiceProxy(ConnectionFactory connectionFactory, Destination queueDestination, long replyConsumerTimeout, AcmeLogger logger, String jms20) {		
		this.connFactory = connectionFactory;
		this.queueDest = queueDestination;
		this.replyConsumerTimeout = replyConsumerTimeout;
		this.logger = logger;
		this.jms20 = jms20;
	}
	
	private void processCommonExceptions(Exception e) throws InvalidArgumentException, ServerBusinessModuleException, ServerPersistenceModuleException {
		if (e instanceof InvalidArgumentException) {
			throw (InvalidArgumentException)e;
		} else if (e instanceof ServerBusinessModuleException) {
			throw  (ServerBusinessModuleException)e;
		} else if (e instanceof ServerPersistenceModuleException) {
			throw (ServerPersistenceModuleException) e;
		} else {
			String msg = "The exception type is not known. Exception class is: " + e.getClass()
				+ " error is: " + ExceptionFormatter.deepFormatToString(e);
			logger.log(Level.INFO, msg);
			e.printStackTrace();			
			throw new RuntimeException(msg,e);
		}		
	}
	
	private AnnuityJMSMessage jmsSendAndReceiveCall(Configrable<String, String> configrable, AnnuityAction annuityAction) throws JMSException{
		logger.fine("In jmsSendAndReceive");
		Connection connection = null;
		MessageConsumer messageConsumer = null;
		AnnuityJMSMessage annuityReply  = null;
		
		try {
			logger.fine("Before Connection created ");
			logger.fine("Connection factory is "+ connFactory);
			connection = connFactory.createConnection();
			
			logger.fine("After Connection created ");
			Session jmsSession = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			Destination tempQueue = jmsSession.createTemporaryQueue();
			
			logger.fine("tempQueue created ");
			MessageProducer queueSender = jmsSession.createProducer(queueDest);
			ObjectMessage objectMessage = jmsSession.createObjectMessage();

			AnnuityJMSMessage annuityJMSMessage = new AnnuityJMSMessage();
			annuityJMSMessage.setAction(annuityAction);
			annuityJMSMessage.setInput(configrable);
			objectMessage.setObject(annuityJMSMessage);
			objectMessage.setJMSDestination(queueDest);
			objectMessage.setJMSReplyTo(tempQueue);

			logger.fine("Object of objectMessage " + objectMessage.getObject());

			queueSender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			queueSender.send(objectMessage);

			logger.fine("Message sent " + objectMessage);
			logger.fine("objectMessage " + objectMessage.getJMSMessageID());

			messageConsumer = jmsSession.createConsumer(tempQueue);
			connection.start();
			Message replyMessage = messageConsumer
					.receive(replyConsumerTimeout);
			if (replyMessage == null) {
				throw new JMSException(
						"The reply JMS Message is not valid. Its null.  Maybe the server did not respond in the allocated time of: "
								+ replyConsumerTimeout);
			}
			ObjectMessage replyObject = null;
			if (replyMessage instanceof ObjectMessage) {
				replyObject = (ObjectMessage) replyMessage;
				logger.fine("reply" + replyObject);
			} else {
				throw new JMSException(
						"The reply JMS Message is not instance of ObjectMesasge as expected.  Its class is: "
								+ replyMessage.getClass()
								+ " to String is: "
								+ replyMessage);
			}

			if (replyObject.getObject() instanceof AnnuityJMSMessage) {
				annuityReply = (AnnuityJMSMessage) replyObject.getObject();
			} else {
				throw new JMSException(
						"The reply JMS Message internal object is not instance of AnnuityJMSMessage as expected.  Its class is: "
								+ replyObject.getObject().getClass()
								+ " toString is: " + replyObject.getObject());
			}
		} catch (JMSException e){						
			throw e;	
		} catch (Exception e){
			throw new JMSException(
				"The JMS send/receive method: jmsSendAndReceiveCall() failed with this exception - " + e);
		} catch (Throwable t){		    
			throw new JMSException(
				"The JMS send/receive method: jmsSendAndReceiveCall() failed with this throwable - " 
					+ t.toString() + ".  Throwable's cause was: " + t.getCause());		
		} finally {
			// Closing the connection will free all related JMS resources
			if (connection != null) {							
				connection.close();
			}
		}
        return annuityReply;                
	}
	
	// JMS20 Simplified API
	private AnnuityJMSMessage jms20SendAndReceiveCall(Configrable<String, String> configrable, AnnuityAction annuityAction) throws JMSException{
		
		logger.fine("jms2.0: In jms20SendAndReceiveCall method");
		
		AnnuityJMSMessage annuityReply  = null;
		
		//JMSContext in a try-with-resources block, the close method will be called automatically at the end of the block
		try (JMSContext context = connFactory.createContext();){
			
			logger.fine("jms2.0: Connection factory is "+ connFactory);
			logger.fine("jms2.0: After Connection created ");
			logger.fine("jms2.0: creating receive Queue ...");
			
			receiveQ = context.createTemporaryQueue();
			
			logger.fine("jms2.0: receiveQ created " +receiveQ);
			
			AnnuityJMSMessage annuityJMSMessage = new AnnuityJMSMessage();
			annuityJMSMessage.setAction(annuityAction);
			annuityJMSMessage.setInput(configrable);
		
			logger.fine("jms2.0: Sending message.");
			
			context.createProducer().setDeliveryMode(DeliveryMode.NON_PERSISTENT).setJMSReplyTo(receiveQ).send(queueDest, annuityJMSMessage);
			
			logger.fine("jms2.0: Object of objectMessage " + annuityJMSMessage);
			logger.fine("jms2.0: Message sent " + context);
			logger.fine("jms2.0: objectMessage Action" + annuityJMSMessage.getAction());
					
			
			Message replyMessage = null;
			logger.fine("jms2.0: createConsumer ...  ");
			JMSConsumer consumer = context.createConsumer(receiveQ);
			replyMessage = consumer.receive(replyConsumerTimeout);
			
			logger.fine("jms2.0: received response message  ");
			
			if (replyMessage != null) {
				annuityReply = replyMessage.getBody(AnnuityJMSMessage.class);
			} else {
				// throw errors about receiving null message
				throw new JMSException(
						"The reply JMS Message is null.  Its class is: "
								+ replyMessage.getClass()
								+ " toString is: " + replyMessage.getClass().toString());
			}
					
		} catch (JMSRuntimeException ex) {
			 System.out.println("The JMS 2.0 jms20SendAndReceiveCall method: jms20SendAndReceiveCall() failed with this exception " + ex);
		}
		
        return annuityReply;                
	}
	
	 
	//private void jmsSendOnlyCall(Configrable<String, String> configrable, AnnuityAction annuityAction) throws JMSException{
	//	Connection connection = connFactory.createConnection();
	//	Session jmsSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);		
	//	MessageProducer queueSender = jmsSession.createProducer(queueDest);
	//	ObjectMessage objectMessage = jmsSession.createObjectMessage();
	//	AnnuityJMSMessage annuityJMSMessage = new AnnuityJMSMessage();
	//	annuityJMSMessage.setAction(annuityAction);
	//	annuityJMSMessage.setInput(configrable);
	//	
	//	objectMessage.setObject(annuityJMSMessage);		
	//    objectMessage.setJMSDestination(queueDest);	    	    
	//   queueSender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);        
	//   queueSender.send(objectMessage);	    
	//}
	 
	public IAnnuity findAnnuityById(IAnnuity annuity) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		
		try{
			if ("true".equalsIgnoreCase(jms20)) {
				annuityJMSMessageReply = jms20SendAndReceiveCall(annuity, AnnuityAction.FIND_ANNUITY_BY_ID);
			} else	{
				annuityJMSMessageReply = jmsSendAndReceiveCall(annuity, AnnuityAction.FIND_ANNUITY_BY_ID);
			}
			//AnnuityJMSMessage annuityJMSMessageReply  = jmsSendAndReceiveCall(annuity, AnnuityAction.FIND_ANNUITY_BY_ID);
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}
			IAnnuity retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof IAnnuity){				
				retData = (IAnnuity) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				if (annuityJMSMessageReply.getOutput() instanceof EntityNotFoundException){
					throw (EntityNotFoundException)annuityJMSMessageReply.getOutput();					
				}else {
					processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());
				}
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}
	
	public IAnnuityHolder findAnnuityHolder(IAnnuity annuity) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20)) {
				annuityJMSMessageReply = jms20SendAndReceiveCall(annuity, AnnuityAction.FIND_ANNUITY_HOLDER);
			} else	{
				annuityJMSMessageReply = jmsSendAndReceiveCall(annuity, AnnuityAction.FIND_ANNUITY_HOLDER);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(annuity, AnnuityAction.FIND_ANNUITY_HOLDER);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}

			IAnnuityHolder retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof IAnnuityHolder){				
				retData = (IAnnuityHolder) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				if (annuityJMSMessageReply.getOutput()instanceof EntityNotFoundException){
					throw (EntityNotFoundException)annuityJMSMessageReply.getOutput();					
				}else {
					processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());
				}
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}
	
	public IContact findContactById(IContact contact) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20)) {
				annuityJMSMessageReply = jms20SendAndReceiveCall(contact, AnnuityAction.FIND_CONTACT_BY_ID);
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(contact, AnnuityAction.FIND_CONTACT_BY_ID);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(contact, AnnuityAction.FIND_CONTACT_BY_ID);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}

			IContact retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof IContact){				
				retData = (IContact) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				if (annuityJMSMessageReply.getOutput() instanceof EntityNotFoundException){
					throw (EntityNotFoundException)annuityJMSMessageReply.getOutput();					
				}else {
					processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());
				}
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}
	@SuppressWarnings("unchecked")
	public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder) throws ServerInternalErrorException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20)) {
				annuityJMSMessageReply = jms20SendAndReceiveCall(annuityHolder, AnnuityAction.FIND_HOLDER_ANNUITIES);		
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(annuityHolder, AnnuityAction.FIND_HOLDER_ANNUITIES);		
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(annuityHolder, AnnuityAction.FIND_HOLDER_ANNUITIES);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}

			List<IAnnuity> retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof List){				
				retData = (List<IAnnuity>) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());				
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}
	public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20)) {
				annuityJMSMessageReply = jms20SendAndReceiveCall(annuityHolder, AnnuityAction.FIND_HOLDER_BY_ID);
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(annuityHolder, AnnuityAction.FIND_HOLDER_BY_ID);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(annuityHolder, AnnuityAction.FIND_HOLDER_BY_ID);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}

			IAnnuityHolder retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof IAnnuityHolder){				
				retData = (IAnnuityHolder) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				if (annuityJMSMessageReply.getOutput() instanceof EntityNotFoundException){
					throw (EntityNotFoundException)annuityJMSMessageReply.getOutput();					
				}else {
					processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());
				}
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}
	public IPayor findPayorById(IPayor payor) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20)) {
				annuityJMSMessageReply = jms20SendAndReceiveCall(payor, AnnuityAction.FIND_PAYOR_BY_ID);
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(payor, AnnuityAction.FIND_PAYOR_BY_ID);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(payor, AnnuityAction.FIND_PAYOR_BY_ID);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}

			IPayor retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof IPayor){				
				retData = (IPayor) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				if (annuityJMSMessageReply.getOutput() instanceof EntityNotFoundException){
					throw (EntityNotFoundException)annuityJMSMessageReply.getOutput();					
				}else {
					processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());
				}
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}
	public IPayout findPayoutById(IPayout payout) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20)) {
				annuityJMSMessageReply = jms20SendAndReceiveCall(payout, AnnuityAction.FIND_PAYOUT_BY_ID);	
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(payout, AnnuityAction.FIND_PAYOUT_BY_ID);	
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(payout, AnnuityAction.FIND_PAYOUT_BY_ID);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}

			IPayout retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof IPayout){				
				retData = (IPayout) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				if (annuityJMSMessageReply.getOutput() instanceof EntityNotFoundException){
					throw (EntityNotFoundException)annuityJMSMessageReply.getOutput();					
				}else {
					processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());
				}
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}
	public IRider findRiderById(IRider rider) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20)) {
				annuityJMSMessageReply = jms20SendAndReceiveCall(rider, AnnuityAction.FIND_RIDER_BY_ID);	
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(rider, AnnuityAction.FIND_RIDER_BY_ID);	
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(rider, AnnuityAction.FIND_RIDER_BY_ID);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}

			IRider retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof IRider){				
				retData = (IRider) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				if (annuityJMSMessageReply.getOutput() instanceof EntityNotFoundException){
					throw (EntityNotFoundException)annuityJMSMessageReply.getOutput();					
				}else {
					processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());
				}
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}
	
	public IAnnuity createAnnuity(IAnnuity ann) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20))	{
				annuityJMSMessageReply = jms20SendAndReceiveCall(ann, AnnuityAction.CREATE_ANNUITY);
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(ann, AnnuityAction.CREATE_ANNUITY);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(ann, AnnuityAction.CREATE_ANNUITY);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}

			IAnnuity retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof IAnnuity){				
				retData = (IAnnuity) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				if (annuityJMSMessageReply.getOutput() instanceof EntityAlreadyExistsException){
					throw (EntityAlreadyExistsException)annuityJMSMessageReply.getOutput();					
				}else {
					processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());
				}
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
		
	}
	
	public IAnnuityHolder createAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20))	{
				annuityJMSMessageReply = jms20SendAndReceiveCall(annHolder, AnnuityAction.CREATE_ANNUITY_HOLDER);	
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(annHolder, AnnuityAction.CREATE_ANNUITY_HOLDER);	
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(annHolder, AnnuityAction.CREATE_ANNUITY_HOLDER);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}

			IAnnuityHolder retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof IAnnuityHolder){				
				retData = (IAnnuityHolder) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				if (annuityJMSMessageReply.getOutput() instanceof EntityAlreadyExistsException){
					throw (EntityAlreadyExistsException)annuityJMSMessageReply.getOutput();					
				}else {
					processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());
				}
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}
	public IContact createContact(IContact contact) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20))	{
				annuityJMSMessageReply = jms20SendAndReceiveCall(contact, AnnuityAction.CREATE_CONTACT);
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(contact, AnnuityAction.CREATE_CONTACT);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(contact, AnnuityAction.CREATE_CONTACT);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}

			IContact retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof IContact){				
				retData = (IContact) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				if (annuityJMSMessageReply.getOutput() instanceof EntityAlreadyExistsException){
					throw (EntityAlreadyExistsException)annuityJMSMessageReply.getOutput();					
				}else {
					processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());
				}
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}
	public IPayor createPayor(IPayor payor) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20))	{
				annuityJMSMessageReply = jms20SendAndReceiveCall(payor, AnnuityAction.CREATE_PAYOR);
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(payor, AnnuityAction.CREATE_PAYOR);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(payor, AnnuityAction.CREATE_PAYOR);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}

			IPayor retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof IPayor){				
				retData = (IPayor) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				if (annuityJMSMessageReply.getOutput() instanceof EntityAlreadyExistsException){
					throw (EntityAlreadyExistsException)annuityJMSMessageReply.getOutput();					
				}else {
					processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());
				}
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}

	}
	public void deleteAnnuity(IAnnuity annuity) throws ServerInternalErrorException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20))	{
				annuityJMSMessageReply = jms20SendAndReceiveCall(annuity, AnnuityAction.DELETE_ANNUITY);	
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(annuity, AnnuityAction.DELETE_ANNUITY);	
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(annuity, AnnuityAction.DELETE_ANNUITY);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				// good behavior
				return; 
			}else { 
				if (annuityJMSMessageReply.getOutput() instanceof Exception){				
					processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());				
				} else{
					throw new ServerInternalErrorException("The result object is of an an expected type " +
							"(expected value was null (for correct behavior) or exception. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
				}
			}
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}
	public void deleteAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, InvalidArgumentException {
		try{			
			if ("true".equalsIgnoreCase(jms20))	{
				annuityJMSMessageReply = jms20SendAndReceiveCall(annHolder, AnnuityAction.DELETE_ANNUITY_HOLDER);
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(annHolder, AnnuityAction.DELETE_ANNUITY_HOLDER);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(annHolder, AnnuityAction.DELETE_ANNUITY_HOLDER);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				// good behavior
				return; 
			}else { 
				if (annuityJMSMessageReply.getOutput() instanceof Exception){				
					processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());				
				} else{
					throw new ServerInternalErrorException("The result object is of an an expected type " +
							"(expected value was null (for correct behavior) or exception. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
				}
			}
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}
	public void deleteContact(IContact contact) throws ServerInternalErrorException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20)) {
				annuityJMSMessageReply = jms20SendAndReceiveCall(contact, AnnuityAction.DELETE_CONTACT);
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(contact, AnnuityAction.DELETE_CONTACT);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(contact, AnnuityAction.DELETE_CONTACT);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				// good behavior
				return; 
			}else { 
				if (annuityJMSMessageReply.getOutput() instanceof Exception){				
					processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());				
				} else{
					throw new ServerInternalErrorException("The result object is of an an expected type " +
							"(expected value was null (for correct behavior) or exception. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
				}
			}
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}
	public void deletePayor(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20)) {
				annuityJMSMessageReply = jms20SendAndReceiveCall(payor, AnnuityAction.DELETE_PAYOR);
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(payor, AnnuityAction.DELETE_PAYOR);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(payor, AnnuityAction.DELETE_PAYOR);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				// good behavior
				return; 
			}else { 
				if (annuityJMSMessageReply.getOutput() instanceof Exception){				
					processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());				
				} else{
					throw new ServerInternalErrorException("The result object is of an an expected type " +
							"(expected value was null (for correct behavior) or exception. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
				}
			}
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}
	public void deletePayout(IPayout payout) throws ServerInternalErrorException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20))	{
				annuityJMSMessageReply = jms20SendAndReceiveCall(payout, AnnuityAction.DELETE_PAYOUT);
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(payout, AnnuityAction.DELETE_PAYOUT);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(payout, AnnuityAction.DELETE_PAYOUT);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				// good behavior
				return; 
			}else { 
				if (annuityJMSMessageReply.getOutput() instanceof Exception){				
					processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());				
				} else{
					throw new ServerInternalErrorException("The result object is of an an expected type " +
							"(expected value was null (for correct behavior) or exception. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
				}
			}
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}
	public void deleteRider(IRider rider) throws ServerInternalErrorException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20)) {
				annuityJMSMessageReply = jms20SendAndReceiveCall(rider, AnnuityAction.DELETE_RIDER);
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(rider, AnnuityAction.DELETE_RIDER);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(rider, AnnuityAction.DELETE_RIDER);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				// good behavior
				return; 
			}else { 
				if (annuityJMSMessageReply.getOutput() instanceof Exception){				
					processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());				
				} else{
					throw new ServerInternalErrorException("The result object is of an an expected type " +
							"(expected value was null (for correct behavior) or exception. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
				}
			}
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}
	public IAnnuity updateAnnuity(IAnnuity ann) throws ServerInternalErrorException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20)) {
				annuityJMSMessageReply = jms20SendAndReceiveCall(ann, AnnuityAction.UPDATE_ANNUITY);
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(ann, AnnuityAction.UPDATE_ANNUITY);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(ann, AnnuityAction.UPDATE_ANNUITY);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}

			IAnnuity retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof IAnnuity){				
				retData = (IAnnuity) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());				
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}
	public IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20)) {
				annuityJMSMessageReply = jms20SendAndReceiveCall(annHolder, AnnuityAction.UPDATE_ANNUITY_HOLDER);
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(annHolder, AnnuityAction.UPDATE_ANNUITY_HOLDER);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(annHolder, AnnuityAction.UPDATE_ANNUITY_HOLDER);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}

			IAnnuityHolder retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof IAnnuityHolder){				
				retData = (IAnnuityHolder) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());				
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}

	}
	public IContact updateContact(IContact contact) throws ServerInternalErrorException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20)) {
				annuityJMSMessageReply = jms20SendAndReceiveCall(contact, AnnuityAction.UPDATE_CONTACT);
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(contact, AnnuityAction.UPDATE_CONTACT);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(contact, AnnuityAction.UPDATE_CONTACT);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}

			IContact retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof IContact){				
				retData = (IContact) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());				
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}

	}
	public IPayor updatePayor(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20)) {
				annuityJMSMessageReply = jms20SendAndReceiveCall(payor, AnnuityAction.UPDATE_PAYOR);
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(payor, AnnuityAction.UPDATE_PAYOR);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(payor, AnnuityAction.UPDATE_PAYOR);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}

			IPayor retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof IPayor){				
				retData = (IPayor) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());				
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}

	}

	@SuppressWarnings("unchecked")
	public List<IAnnuity> findPayorAnnuities(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20)) {
				annuityJMSMessageReply = jms20SendAndReceiveCall(payor, AnnuityAction.FIND_PAYOR_ANNUITIES);
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(payor, AnnuityAction.FIND_PAYOR_ANNUITIES);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(payor, AnnuityAction.FIND_PAYOR_ANNUITIES);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}

			List<IAnnuity> retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof List){				
				retData = (List<IAnnuity>) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());				
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}

	public IPayout createPayout(IPayout payout) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20)) {
				annuityJMSMessageReply = jms20SendAndReceiveCall(payout, AnnuityAction.CREATE_PAYOUT);
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(payout, AnnuityAction.CREATE_PAYOUT);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(payout, AnnuityAction.CREATE_PAYOUT);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}

			IPayout retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof IPayout){				
				retData = (IPayout) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				if (annuityJMSMessageReply.getOutput() instanceof EntityAlreadyExistsException){
					throw (EntityAlreadyExistsException)annuityJMSMessageReply.getOutput();					
				}else {
					processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());
				}
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}

	public IPayout updatePayout(IPayout payout) throws ServerInternalErrorException, InvalidArgumentException {
		try{
			if ("true".equalsIgnoreCase(jms20)) {
				annuityJMSMessageReply = jms20SendAndReceiveCall(payout, AnnuityAction.UPDATE_PAYOUT);
			} else {
				annuityJMSMessageReply = jmsSendAndReceiveCall(payout, AnnuityAction.UPDATE_PAYOUT);
			}
			//AnnuityJMSMessage annuityJMSMessageReply = jmsSendAndReceiveCall(payout, AnnuityAction.UPDATE_PAYOUT);		
			if (annuityJMSMessageReply == null) {
				throw new ServerInternalErrorException("The JMS Call returned a null object."); 
			}
			if (annuityJMSMessageReply.getOutput() == null) {
				throw new ServerInternalErrorException("The JMS call returned invalid data.  The reply message has a null output."); 
			}

			IPayout retData = null; 
			if (annuityJMSMessageReply.getOutput() instanceof IPayout){				
				retData = (IPayout) annuityJMSMessageReply.getOutput();
			}else if (annuityJMSMessageReply.getOutput() instanceof Exception){				
				processCommonExceptions((Exception) annuityJMSMessageReply.getOutput());				
			} else{
				throw new ServerInternalErrorException("The result object is of an an expected type. Its type is: " + annuityJMSMessageReply.getOutput().getClass());
			}
	        return retData;
	        
		}catch(JMSException e){
			String msg = "Failed to send/receive the JMS Message.  Received a JMSException.  Error is:" + e
				+ " returning a ServerInternalErrorException";
			logger.log(Level.INFO, msg, e);
			throw new ServerInternalErrorException (msg, e);
		}
	}
	
}
