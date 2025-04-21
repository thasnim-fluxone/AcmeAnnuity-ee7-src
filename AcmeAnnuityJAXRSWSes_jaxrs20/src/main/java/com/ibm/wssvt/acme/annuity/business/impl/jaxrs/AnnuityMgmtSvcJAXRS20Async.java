/**
 * Date created: 2015-02-02 
 * Author: Tam 
 * LastChangedBy: Tam
 * Description:  Test asynchronous JAXRS-2.0 server.
 * Only CRUDContact and CRUDPayor with JAXB content type were implemented with asynchronous jaxrs-2.0 
 */
package com.ibm.wssvt.acme.annuity.business.impl.jaxrs;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.TimeoutHandler;

import java.util.List;
import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Annuity;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityJAXRSReturn;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityValueObject;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Contact;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.ListOfObjects;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Payor;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Payout;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.PayoutValueObject;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Rider;
import com.ibm.wssvt.acme.annuity.common.business.AnnuityBusinessServiceLookup;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerBusinessModuleException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityLoggerFactory;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.exception.ExceptionFormatter;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;


@Path(value = "/jaxrs20Async")
@RolesAllowed({"users","admins"})
public class AnnuityMgmtSvcJAXRS20Async {
	@Context
	private UriInfo uriInfo;
	
	private static int SLEEP_TIME_IN_MILLIS = 500; 
	private static int TIMEOUT_IN_SEC = 20; 
	private static int numberOfSuccessResponses = 0;
    private static int numberOfFailures = 0;
    private static Throwable lastException = null;
   
    
	@POST
	@Path("/contact")
	@Consumes(value ={"application/xml", "application/json"})
	@Produces(value ={"application/xml", "application/json"})
	@RolesAllowed("admins")
	public void createContactAsync(final Contact contact, @Suspended final AsyncResponse asyncResponse) {
		
		//delay(asyncResponse);
	   	Response result = createContact(contact);
	    asyncResponse.resume(result);	
	    checkStatus(asyncResponse);
	    

	    
	}
	
	public Response createContact(Contact contact){
		AnnuityJAXRSReturn retData= null;
		
		try {
			AcmeLogger logger = getLogger(contact);
			logger.fine("jaxrs-2.0 Async test: thread id in id in createContact: " +Thread.currentThread().getId());
			logger.fine("create contact call. id: " + contact.getId());
			IContact result = getAnnuityService(contact).createContact(contact);
			logger.fine("contact id " + contact.getId() + " created");
			retData = objectToReturn(result);
			
			
		} catch (EntityAlreadyExistsException e) {
			retData = exceptionToReturn(e);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}
		
		return Response.status(Status.OK).entity(retData).build();
	}
	
	public void checkStatus(AsyncResponse asyncResponse){
		
		/*
		// timeout is checked on the client side
		asyncResponse.setTimeoutHandler(new TimeoutHandler() {
		    @Override
        	public void handleTimeout(AsyncResponse asyncResponse) {
            	asyncResponse.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("Operation time out.").build());
            	printThread("handleTimeout");
        	}
        	
		});
		
		boolean timeout = asyncResponse.setTimeout(TIMEOUT_IN_SEC, TimeUnit.SECONDS);
		
		if (timeout == true) {
			System.out.println("time out in " +TIMEOUT_IN_SEC+ " seconds");
			return;
		}
		*/
		
		 CompletionCallback status = new CompletionCallback(){
		//A completion callback notification method that will be invoked when the request processing is finished.
	            @Override
	            public void onComplete(Throwable throwable) {
	            	
	            	if (getPropFile()){
	            		System.out.println("jaxrs-2.0 async test: thread id in onComplete() " +Thread.currentThread().getId());
	            	}
	                if (throwable == null) {
	                    // no throwable - the processing ended successfully
	                    // (response already written to the client)
	                    numberOfSuccessResponses++;
	                    if (getPropFile()){
	                    	System.out.println("numberOfSuccessResponses: " +numberOfSuccessResponses);
	                    }
	                } else {
	                	
	                    numberOfFailures++;
	                    lastException = throwable;
	                    if (getPropFile()){
	                    	System.out.println("numberOfFailures: " +numberOfFailures);
	                    	System.out.println("lastException: " +lastException);
	                    }
	                }
	            }
		 };
		 
		 asyncResponse.register(status);
		 
		/*
        asyncResponse.register(new CompletionCallback() {
        	
            @Override
            public void onComplete(Throwable throwable) {
            	if (System.getProperty("JAXRS20_ASYNC_DEBUG").equals("ON")){
            		System.out.println("onComplete thread id " +Thread.currentThread().getId());
            	}
                if (throwable == null) {
                    // no throwable - the processing ended successfully
                    // (response already written to the client)
                    numberOfSuccessResponses++;
                    if (System.getProperty("JAXRS20_ASYNC_DEBUG").equals("ON")){
                    	System.out.println("numberOfSuccessResponses: " +numberOfSuccessResponses);
                    }
                } else {
                	
                    numberOfFailures++;
                    lastException = throwable;
                    if (System.getProperty("JAXRS20_ASYNC_DEBUG").equals("ON")){
                    	System.out.println("numberOfFailures: " +numberOfFailures);
                    	System.out.println("lastException: " +lastException);
                    }
                }
            }
        });
        */
	}
	
	public void delay(AsyncResponse asyncResponse) {
	 	try {
			Thread.currentThread().sleep(SLEEP_TIME_IN_MILLIS);
			
		} catch (final InterruptedException ex) {
			asyncResponse.cancel();
			System.out.println("InterruptedException");
			ex.printStackTrace();
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

	@POST 
	@Path(value = "/payor")
	@Consumes(value ={"application/xml", "application/json"})
	@Produces(value ={"application/xml", "application/json"})
	@RolesAllowed("admins")
	public void createPayorAsync(final Payor payor, @Suspended final AsyncResponse asyncResponse) {
		
		//delay(asyncResponse);
    	Response result = createPayor(payor);
        asyncResponse.resume(result);

		checkStatus(asyncResponse);
        
	}
	public Response createPayor(Payor payor){
		AnnuityJAXRSReturn retData= null;
		
		try {
			AcmeLogger logger = getLogger(payor);
			logger.fine("jaxrs-2.0 Async test: thread id in createPayor: " +Thread.currentThread().getId());
			logger.fine("create payor call. id: " + payor.getId());
			IPayor result = getAnnuityService(payor).createPayor(payor);
			retData = objectToReturn(result);
		} catch (EntityAlreadyExistsException e) {
			retData = exceptionToReturn(e);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}		
		return Response.status(Status.OK).entity(retData).build();
	}

	@PUT
	@Path(value = "/payor")
	@Consumes(value ={"application/xml", "application/json"})
	@Produces(value ={"application/xml", "application/json"})
	public void updatePayorAsync(final Payor payor, @Suspended final AsyncResponse asyncResponse) {
		
		//delay(asyncResponse);
    	Response result = updatePayor(payor);
        asyncResponse.resume(result);
  
		checkStatus(asyncResponse);
        
	}
	public Response updatePayor(Payor payor){
		AnnuityJAXRSReturn retData= null;
		
		try {
			AcmeLogger logger = getLogger(payor);
			logger.fine("jaxrs-2.0 Async test: thread id in updatePayor: " +Thread.currentThread().getId());
			logger.fine("update payor call. id: " + payor.getId());
			IPayor result = getAnnuityService(payor).updatePayor(payor);
			retData = objectToReturn(result);	
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}		
		return Response.status(Status.OK).entity(retData).build();
	}
	
	@GET 
	@Path(value ="/payor/{payorId}")
	@Produces(value ={"application/xml", "application/json"})
	@PermitAll
	public void findPayorByIdAsync(@PathParam(value="payorId") final String payorId, @Suspended final AsyncResponse asyncResponse){
			
		//delay(asyncResponse);
    	Response result = findPayorById(payorId);
        asyncResponse.resume(result);
			
		checkStatus(asyncResponse);
	    
	}
	
	public Response findPayorById(String payorId) {
		AnnuityJAXRSReturn retData = null;	
		
		Payor payor= new Payor();
		payor.setId(payorId);
		addParams(payor, uriInfo.getQueryParameters());		
		try {
			AcmeLogger logger = getLogger(payor);
			logger.fine("jaxrs-2.0 Async test: thread id in findPayorById: " +Thread.currentThread().getId());
			logger.fine("find payor call. id: " + payor.getId());
			IPayor result = getAnnuityService(payor).findPayorById(payor);
			retData = objectToReturn(result);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		} catch (EntityNotFoundException e) {
			retData = exceptionToReturn(e);			
		}					
		return Response.status(Status.OK).entity(retData).build();
	}
	
	@DELETE 
	@Path(value ="/payor/{payorId}")
	@Produces(value ={"application/xml", "application/json"})
	@RolesAllowed("admins")
	public void deletePayorAsync(@PathParam(value="payorId") final String payorId, @Suspended final AsyncResponse asyncResponse) {
		
		//delay(asyncResponse);
    	Response result = deletePayor(payorId);
        asyncResponse.resume(result);
		
		checkStatus(asyncResponse);
        
	}
	
	public Response deletePayor(String payorId){
		AnnuityJAXRSReturn retData = new AnnuityJAXRSReturn();		
		
		Payor payor= new Payor();
		payor.setId(payorId);
		addParams(payor, uriInfo.getQueryParameters());		
		try {
			AcmeLogger logger = getLogger(payor);
			logger.fine("jaxrs-2.0 Async test: thread id in deletePayor: " +Thread.currentThread().getId());
			logger.fine("delete payor call. id: " + payor.getId());
			getAnnuityService(payor).deletePayor(payor);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}	
		return Response.status(Status.OK).entity(retData).build();				
	}
	
	@PUT
	@Path(value = "/contact")	
	@Consumes(value ={"application/xml", "application/json"})
	@Produces(value ={"application/xml", "application/json"})
	public synchronized void updateContactAsync(final Contact contact, @Suspended final AsyncResponse asyncResponse) {
		
		AcmeLogger logger = getLogger(contact);
		//delay(asyncResponse);
    	Response result = updateContact(contact);
        asyncResponse.resume(result);
		
		checkStatus(asyncResponse);

	}
	
	public Response updateContact(Contact contact){
		AnnuityJAXRSReturn retData= null;
		
		try {
			AcmeLogger logger = getLogger(contact);
			logger.fine("jaxrs-2.0 Async test: thread id in updateContact: " +Thread.currentThread().getId());
			logger.fine("update contact call. id: " + contact.getId());
			IContact result = getAnnuityService(contact).updateContact(contact);
			retData = objectToReturn(result);		
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}		
		
		return Response.status(Status.OK).entity(retData).build();
		
	}
	
	@DELETE 
	@Path(value ="/contact/{contactId}")
	@Produces(value ={"application/xml", "application/json"})
	@RolesAllowed("admins")
	public synchronized void deleteContactAsync(@PathParam(value="contactId") final String contactId, @Suspended final AsyncResponse asyncResponse) {
			
		//delay(asyncResponse);
        Response result = deleteContact(contactId);
        asyncResponse.resume(result);
            
	    checkStatus(asyncResponse);
				
	}
	
	public Response deleteContact(String contactId){
		AnnuityJAXRSReturn retData = new AnnuityJAXRSReturn();
		
		MultivaluedMap<String, String> qparams = uriInfo.getQueryParameters();
		
		Contact contact = new Contact();
		contact.setId(contactId);
		addParams(contact, qparams);		
		try {
			AcmeLogger logger = getLogger(contact);
			logger.fine("jaxrs-2.0 Async test: thread id in deleteContact: " +Thread.currentThread().getId());
			logger.fine("delete contact call. id: " + contact.getId());
			getAnnuityService(contact).deleteContact(contact);	
			logger.fine("contact deleted!");
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}	
		return Response.status(Status.OK).entity(retData).build();				
	}
	
	
	@GET 
	@Path(value ="/contact/{contactId}")
	@Produces(value ={"application/xml", "application/json"})
	@PermitAll
	public void findContactByIdAsync(@PathParam(value="contactId") final String contactId, @Suspended final AsyncResponse asyncResponse) {
		
		//delay(asyncResponse);
    	Response result = findContactById(contactId);
        asyncResponse.resume(result);
        
		checkStatus(asyncResponse);
	        
	}
	public Response findContactById(String contactId){
		AnnuityJAXRSReturn retData = null;
		MultivaluedMap<String, String> qparams = uriInfo.getQueryParameters();
		
		Contact contact = new Contact();
		contact.setId(contactId);
		addParams(contact, qparams);		
		try {
			AcmeLogger logger = getLogger(contact);
			logger.fine("jaxrs-2.0 Async test: thread id in findContactById: " +Thread.currentThread().getId());
			logger.fine("find contact call. id: " + contact.getId());	
			IContact result = getAnnuityService(contact).findContactById(contact);
			logger.fine("Found contact " +result.getId());
			retData = objectToReturn(result);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		} catch (EntityNotFoundException e) {
			retData = exceptionToReturn(e);			
		}					
		return Response.status(Status.OK).entity(retData).build();
	}
	
	private void addParams(Configrable<String, String> configrable, MultivaluedMap<String, String> qparams){		
		if (qparams != null){
			for (String key: qparams.keySet()){
				String value = qparams.getFirst(key);
				configrable.getConfiguration().addParameter(key, value);				
			}
		}
		AcmeLogger logger = getLogger(configrable);
		logger.fine("params are: " + configrable.getConfiguration().getParameters());
	}
	
	private AnnuityJAXRSReturn objectToReturn(Object o){		
		AnnuityJAXRSReturn retData = new AnnuityJAXRSReturn();
		retData.setReturnObjectClass(o.getClass().getName());
		retData.setReturnObject(o);
		return retData;
	}
	
	private AnnuityJAXRSReturn exceptionToReturn(Exception e){
		AnnuityJAXRSReturn retData = new AnnuityJAXRSReturn();
		retData.setErrorClass(e.getClass().getName());
		retData.setErrorMsg(ExceptionFormatter.deepFormatToString(e));		
		return retData;
	}
	
	// Print thread id based on the flag JAXRS20_ASYNC_DEBUG in liberty server bootstrap.properties file
	// print thread id if JAXRS20_ASYNC_DEBUG=ON
	public void printThread(String message) {
		
		if (System.getProperty("JAXRS20_ASYNC_DEBUG") != null) {
			if (System.getProperty("JAXRS20_ASYNC_DEBUG").equals("ON")) {
				long tid = Thread.currentThread().getId();
				System.out.println(message+ " : thread id " +tid);
				
			}
		}
	}
	
	//Checking for JAXRS20_ASYNC_DEBUG in bootstrap.properties
	public boolean getPropFile(){
		boolean flag = false;
		if (System.getProperty("JAXRS20_ASYNC_DEBUG") != null) {
			if (System.getProperty("JAXRS20_ASYNC_DEBUG").equals("ON")) {
				flag = true;
			}
		}
			
		return flag;
	}
}
