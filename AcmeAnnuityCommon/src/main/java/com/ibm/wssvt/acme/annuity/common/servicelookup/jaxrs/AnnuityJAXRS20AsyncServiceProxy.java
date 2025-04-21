/**
 * Date created: 2015-01-26 
 * Author: Tam Dinh
 * LastChangedBy: Tam
 * Description:  Test Asynchronous JAXRS-2.0  client.  The main async code is in executeAsync() method.
 * Only CRUDContact and CRUDPayor with JAXB content type were implemented on the server side  so 
 * valid ACME async jaxrs-2.0 EUs are CRUDContactQuickReturnSEU, CRUDPayorQuickReturnSEU, CRUDContactEU, and CRUDPayorEU
 */
package com.ibm.wssvt.acme.annuity.common.servicelookup.jaxrs;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.InvocationCallback;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityObject;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityValueObject;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.Entity;

import org.apache.wink.common.model.atom.AtomEntry;

import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityJAXRSReturn;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.ListOfObjects;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.PayoutValueObject;

import java.lang.String;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerBusinessModuleException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class AnnuityJAXRS20AsyncServiceProxy implements IAnnuityService {

	final String resourceUrl;
	private static final String POST = "post";
	private static final String PUT = "put";
	private static final String GET = "get";
	private static final String DELETE = "delete";
	private static final String JAXB ="jaxb";
	private static final String JSON ="json";
	private static final String ATOM = "atom";
	
	private AcmeLogger logger;
	private String serverURL;
	private String contentType;
	private Configrable<String, String> configs;
	private long waitTime;
	private long waitCount;
	
    int remoteAsynchTimeout = 10;
	
	public AnnuityJAXRS20AsyncServiceProxy(Configrable<String, String> configs, String serverURL, String contentType, AcmeLogger logger) {
		
		this.resourceUrl = serverURL;
		this.logger = logger;		
		this.serverURL = serverURL;
		this.contentType = contentType;
		this.logger.fine("initialized ok");
		this.configs = configs;
		
		try{
			if (configs != null) {
				this.waitTime = Long.parseLong(configs.getConfiguration().getParameterValue("waitTime"));
				this.waitCount = Long.parseLong(configs.getConfiguration().getParameterValue("waitCount"));
			}
			else {
				this.logger.fine("configs is null !");
			}
				
		
		}catch (Exception e) {
			throw new RuntimeException("Invalid Configuration.  WaitTime or WaitCount are not valid values." +
					"current values are: waitTime:" + configs.getConfiguration().getParameterValue("waitTime")
					+" and waitCount: " + configs.getConfiguration().getParameterValue("waitCount"));
		}
		
	}

	@Override
	public IAnnuity findAnnuityById(IAnnuity annuity)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		
		AnnuityJAXRSReturn ret = executeAndVerifyAsync(null, annuity, GET, "/annuity/"+annuity.getId(), null);
		if (ret.getErrorClass() != null) {
			if (ret.getErrorClass().equals(EntityNotFoundException.class.getName())){
				throw new EntityNotFoundException(ret.getErrorMsg());
			}
		}
		AnnuityValueObject result = (AnnuityValueObject)ret.getReturnObject();
		return result.getAnnuity();
	}

	@Override
	public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerifyAsync(null, annuityHolder, GET, "/annuityholder/"+annuityHolder.getId(), null);
		if (ret.getErrorClass() != null) {
			if (ret.getErrorClass().equals(EntityNotFoundException.class.getName())){
				throw new EntityNotFoundException(ret.getErrorMsg());
			}
		}
		IAnnuityHolder result = (IAnnuityHolder)ret.getReturnObject();
		return result;
	}

	@Override
	public IContact findContactById(IContact contact)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		
		AnnuityJAXRSReturn ret = executeAndVerifyAsync(null, contact, GET, "/contact/"+contact.getId(), null);
		if (ret.getErrorClass() != null) {
			if (ret.getErrorClass().equals(EntityNotFoundException.class.getName())){
				throw new EntityNotFoundException(ret.getErrorMsg());
			}
		}
		IContact result = (IContact )ret.getReturnObject();
		return result;	
	
	}

	@Override
	public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder)
			throws ServerInternalErrorException, InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerifyAsync(null, annuityHolder, GET, "/annuity/holder/"+annuityHolder.getId(), null);

		ListOfObjects result = (ListOfObjects)ret.getReturnObject();
		List<IAnnuity> annuities = new ArrayList<IAnnuity>();
		if (result != null){
			for(Object obj : result.getObjects()){
				if (!(obj instanceof AnnuityValueObject)){
					String msg = "expected a return of AnnuityValueObject, but found: " + obj.getClass().getName() + " internal logs: " + logger.getAllLogs();
					logger.info(msg);
					throw new RuntimeException(msg);
				}
				annuities.add(((AnnuityValueObject)obj).getAnnuity());
			}
		}
		return annuities;	
	}

	@Override
	public IAnnuityHolder findAnnuityHolder(IAnnuity annuity)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerifyAsync(null, annuity, GET, "/annuityholder/annuity/"+annuity.getId(), null);
		if (ret.getErrorClass() != null) {
			if (ret.getErrorClass().equals(EntityNotFoundException.class.getName())){
				throw new EntityNotFoundException(ret.getErrorMsg());
			}
		}
		return (IAnnuityHolder)ret.getReturnObject();
	}

	@Override
	public List<IAnnuity> findPayorAnnuities(IPayor payor)
			throws ServerInternalErrorException, InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerifyAsync(null, payor, GET, "/annuity/payor/"+payor.getId(), null);
		
		ListOfObjects result = (ListOfObjects)ret.getReturnObject();
		List<IAnnuity> annuities = new ArrayList<IAnnuity>();
		if (result != null){
			for(Object obj : result.getObjects()){
				if (!(obj instanceof AnnuityValueObject)){
					String msg = "expected a return of AnnuityValueObject, but found: " + obj.getClass().getName() + " internal logs: " +logger.getAllLogs();
					logger.info(msg);
					throw new RuntimeException(msg);
				}
				annuities.add( ((AnnuityValueObject)obj).getAnnuity());
			}
		}
		return annuities;
	}

	@Override
	public IPayor findPayorById(IPayor payor) throws EntityNotFoundException,
			ServerInternalErrorException, InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerifyAsync(null, payor, GET, "/payor/"+payor.getId(), null);
		if (ret.getErrorClass() != null){
			if (ret.getErrorClass().equals(EntityNotFoundException.class.getName())){
				throw new EntityNotFoundException(ret.getErrorMsg());
			}
		}
		IPayor result = (IPayor)ret.getReturnObject();
		return result;
	}

	@Override
	public IPayout findPayoutById(IPayout payout)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerifyAsync(null, payout, GET, "/payout/"+payout.getId(), null);
		if (ret.getErrorClass() != null){
			if (ret.getErrorClass().equals(EntityNotFoundException.class.getName())){
				throw new EntityNotFoundException(ret.getErrorMsg());
			}
		}
		PayoutValueObject result = (PayoutValueObject )ret.getReturnObject();
		return result.getPayout();	
	}

	@Override
	public IRider findRiderById(IRider rider) throws EntityNotFoundException,
			ServerInternalErrorException, InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerifyAsync(null, rider, GET, "/rider/"+rider.getId(), null);
		if (ret.getErrorClass() != null){
			if (ret.getErrorClass().equals(EntityNotFoundException.class.getName())){
				throw new EntityNotFoundException(ret.getErrorMsg());
			}
		}
		IRider result = (IRider)ret.getReturnObject();
		return result;	
	}

	@Override
	public IAnnuity createAnnuity(IAnnuity ann)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerifyAsync(ann, null, POST, "/annuity", AnnuityValueObject.class);
		if (ret.getErrorClass() != null) {
			if (ret.getErrorClass().equals(EntityAlreadyExistsException.class.getName())){
				throw new EntityAlreadyExistsException(ret.getErrorMsg());
			}
		}
		AnnuityValueObject result = (AnnuityValueObject)ret.getReturnObject();		
		return result.getAnnuity();
	
	}

	@Override
	public IAnnuity updateAnnuity(IAnnuity ann)
			throws ServerInternalErrorException, InvalidArgumentException {
		
		AnnuityJAXRSReturn ret = executeAndVerifyAsync(ann, null, PUT, "/annuity", AnnuityValueObject.class);		
		AnnuityValueObject result = (AnnuityValueObject)ret.getReturnObject();
		return result.getAnnuity();
	}

	@Override
	public void deleteAnnuity(IAnnuity annuity)
			throws ServerInternalErrorException, InvalidArgumentException {
		
		executeAndVerifyAsync(null, annuity, DELETE, "/annuity/"+annuity.getId(), null);
		
	}

	@Override
	public IAnnuityHolder createAnnuityHolder(IAnnuityHolder annHolder)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerifyAsync(annHolder, null, POST, "/annuityholder", null);
		if (ret.getErrorClass() != null) {
			if (ret.getErrorClass().equals(EntityAlreadyExistsException.class.getName())){
				throw new EntityAlreadyExistsException(ret.getErrorMsg());
			}
		}
		return (IAnnuityHolder) ret.getReturnObject();
	}

	@Override
	public IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		
		AnnuityJAXRSReturn ret = executeAndVerifyAsync(annHolder, null, PUT, "/annuityholder", null);		
		IAnnuityHolder result = (IAnnuityHolder)ret.getReturnObject();
		return result;
	}

	@Override
	public void deleteAnnuityHolder(IAnnuityHolder annHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		
		executeAndVerifyAsync(null, annHolder, DELETE, "/annuityholder/"+annHolder.getId(), null);
		
	}

	@Override
	public IPayor createPayor(IPayor payor)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerifyAsync(payor, null, POST, "/payor", null);
		if (ret.getErrorClass() != null) {
			if (ret.getErrorClass().equals(EntityAlreadyExistsException.class.getName())){
				throw new EntityAlreadyExistsException(ret.getErrorMsg());
			}
		}
		return (IPayor) ret.getReturnObject();	
	}

	@Override
	public IPayor updatePayor(IPayor payor)
			throws ServerInternalErrorException, InvalidArgumentException {
		
		AnnuityJAXRSReturn ret = executeAndVerifyAsync(payor, null, PUT, "/payor", null);		
		IPayor result = (IPayor)ret.getReturnObject();
		return result;
	}

	@Override
	public void deletePayor(IPayor payor) throws ServerInternalErrorException,
			InvalidArgumentException {
		
		executeAndVerifyAsync(null, payor, DELETE, "/payor/"+payor.getId(), null);
	}

	@Override
	public IContact createContact(IContact contact)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerifyAsync(contact, null, POST, "/contact", null);
		
		if (ret.getErrorClass() != null) {
			if (ret.getErrorClass().equals(EntityAlreadyExistsException.class.getName())){
				throw new EntityAlreadyExistsException(ret.getErrorMsg());
			}
		}
		IContact result = (IContact) ret.getReturnObject();
		return result;
	}

	@Override
	public IContact updateContact(IContact contact)
			throws ServerInternalErrorException, InvalidArgumentException {
		
		AnnuityJAXRSReturn ret = executeAndVerifyAsync(contact, null, PUT, "/contact", null);
		IContact result = (IContact)ret.getReturnObject();
		return result;
	}

	@Override
	public void deleteContact(IContact contact)
			throws ServerInternalErrorException, InvalidArgumentException {
		
		executeAndVerifyAsync(null, contact, DELETE, "/contact/"+contact.getId(), null);	
		
	}

	@Override
	public IPayout createPayout(IPayout payout)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {


		AnnuityJAXRSReturn ret = executeAndVerifyAsync(payout, null, POST, "/payout", PayoutValueObject.class);
		if (ret.getErrorClass() != null) {
			if (ret.getErrorClass().equals(EntityAlreadyExistsException.class.getName())) {
				throw new EntityAlreadyExistsException(ret.getErrorMsg());
			}
		}
		PayoutValueObject result = (PayoutValueObject) ret.getReturnObject();
		return result.getPayout();
	}

	@Override
	public IPayout updatePayout(IPayout payout)
			throws ServerInternalErrorException, InvalidArgumentException {
		
		AnnuityJAXRSReturn ret = executeAndVerifyAsync(payout, null, PUT, "/payout", PayoutValueObject.class);
		PayoutValueObject result = (PayoutValueObject)ret.getReturnObject();
		return result.getPayout();
	}

	@Override
	public void deletePayout(IPayout payout)
			throws ServerInternalErrorException, InvalidArgumentException {

		executeAndVerifyAsync(null, payout, DELETE, "/payout/"+payout.getId(), null);
	}

	@Override
	public void deleteRider(IRider rider) throws ServerInternalErrorException,
			InvalidArgumentException {

		executeAndVerifyAsync(null, rider, DELETE, "/rider/"+rider.getId(), null);
	}
	/**
	 * 
	 * @param requestObject
	 * @param configrable
	 * @param methodType - POST, PUT, GET, or DELETE
	 * @param urlPath - the path of the JAX-RS resource method only
	 * @param expectedType - the expected return type.  May be null for POST and DELETE
	 * @param wrapperObject - AnnuityValueObject or PayoutValueObject, to wrap the IAnnuityObject
	 * @return AnnuityJAXRSReturn
	 * @throws InvalidArgumentException
	 * @throws ServerBusinessModuleException
	 * @throws ServerPersistenceModuleException
	 */
	private AnnuityJAXRSReturn executeAndVerifyAsync(IAnnuityObject requestObject, Configrable<String, String> configrable, String methodType, String urlPath, Class wrapperObjectClass) throws InvalidArgumentException, ServerBusinessModuleException, ServerPersistenceModuleException {

		AnnuityJAXRSReturn retVal = null;

		Object realRequestObject = requestObject;

		// wrap IAnnuityObject, if necessary -- lots of casting and assumptions going on here
		if (wrapperObjectClass != null) {
			Object wrapperObject;
			try {
				// assuming a no-arg constructor, which is what JAXB objects and POJOs must have anyway
				wrapperObject = wrapperObjectClass.newInstance();
			} catch (Exception e) {
				logger.fine(stackToString(e));
				throw new RuntimeException(e);
			}
			if (wrapperObject instanceof AnnuityValueObject) {
				((AnnuityValueObject)wrapperObject).setAnnuity((IAnnuity)requestObject);
				realRequestObject = wrapperObject;
			} else if (wrapperObject instanceof PayoutValueObject) {
				((PayoutValueObject)wrapperObject).setPayout((IPayout)requestObject);
				realRequestObject = wrapperObject;
			}
		}
		// done wrapping

		AnnuityJAXRSReturn ret = executeAsync(realRequestObject, configrable, methodType, urlPath);
		
		if (retVal == null) {
			retVal = ret;
		}
		if (methodType.equals(GET) || methodType.equals(PUT)) {  // POST and DELETE do not return anything
			if (ret.getErrorClass() != null){
				processCommonExceptions(ret);
			}
			else if (!(ret.getReturnObjectClass().equals(ret.getReturnObject().getClass().getName()))){
				String msg = "expected a return of " + ret.getReturnObjectClass() + ", but found: " + ret.getReturnObject().getClass().getName() 
				+ " internal logs: " +logger.getAllLogs();
				logger.info(msg);
				throw new RuntimeException(msg);
			}
		}

		return retVal;
	}
    
	private void waitAndCheckForValidResponse(Future<Response> response, Configrable<String, String> configrable) throws ServerInternalErrorException, InterruptedException, ExecutionException{
		logger.fine("in waitAndCheckForValidResponse()");
		waitForResponse(response, configrable);
		
		if (response.isDone()) {
			if (response.get() == null){
				throw new ServerInternalErrorException("The server responded with a null object.  " +
						"Expected either an error or a non-null object." );					
			}								
		}else{				
			throw new ServerInternalErrorException("The server did not respond in in the expected time." +
					"wait values are: waitTime:" + configrable.getConfiguration().getParameterValue("waitTime") +
					" and waitCount: " + configrable.getConfiguration().getParameterValue("waitCount") +
					" was waiting for Response class: " +response.getClass().getName());
					
		}			
	}

	private void waitForResponse(Future<Response> resp, Configrable<String, String> configrable) throws InterruptedException{
		long cntr = 0;
		logger.fine("in waitForResponse()");
		logger.fine("waitTime = " +waitTime+ " and waitCount = " +waitCount);
		
		while (cntr <= waitCount ){
			if (resp.isDone()){
				return;					
			}else{		
				logger.fine("waiting for response ... wait count: " + cntr);
				cntr++;				
				Thread.sleep(waitTime);
			}
		}		
	}

    private Client getRestClient() throws ServerAdapterCommunicationException {
		   	
    	ClientBuilder cb = ClientBuilder.newBuilder();
    	
    	String useSecurity = configs.getConfiguration().getParameterValue("useSecurity");
      	//String userTimeout = configs.getConfiguration().getParameterValue("readTimeout"); 
    	String useSSL = null;
    	String sslRefId = null;
    	   	
    	if (configs.getConfiguration().getParameterValue("useSSL")!= null )
    		useSSL = configs.getConfiguration().getParameterValue("useSSL");
    	
    	//sslRefId is the ssl ref id in Liberty server.xml
    	if (configs.getConfiguration().getParameterValue("sslRefId")!= null )
    		sslRefId = configs.getConfiguration().getParameterValue("sslRefId");
    	
    	if ("BASIC_AUTH".equalsIgnoreCase(useSecurity)) {
			logger.fine("useSecurity is enabled and set to BASIC_AUTH.");
			String userName = configs.getConfiguration().getParameterValue(
					"userName");
			String userPassword = configs.getConfiguration()
					.getParameterValue("userPassword");
			if (userName == null || userName.trim().length() == 0) {
				throw new ServerAdapterCommunicationException(
						"Connection to REST service is set to use security, "
								+ "but the userName is null or empty.  Current value is: "
								+ userName);
			}
			if (userPassword == null || userPassword.trim().length() == 0) {
				throw new ServerAdapterCommunicationException(
						"Connection to REST service is set to use security, "
								+ "but the userPassword is null or empty.  Current value is: "
								+ userPassword);
			}
			logger.fine("userName: " + userName);
			logger.fine("userPassword: " + userPassword);
			
			cb.register(new BasicAuthFilter(userName,userPassword));
    	}    	
    	Client c = cb.build();
    	
    	if ("true".equalsIgnoreCase(useSSL)) {
  		  c.property("com.ibm.ws.jaxrs.client.ssl.config", sslRefId);  
  		  
  	    }
    	
    	return c;		
    }
    private static String stackToString(Throwable e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.BufferedWriter bw = new java.io.BufferedWriter(sw);
        java.io.PrintWriter pw = new java.io.PrintWriter(bw);
        e.printStackTrace(pw);
        pw.close();
        String text = sw.getBuffer().toString();
        // Jump past the throwable
        text = text.substring(text.indexOf("at"));
        text = replace(text, "at ", "DEBUG_FRAME = ");
        return text;
    }
    
    private static final String replace(String name, String oldT, String newT) {

		if (name == null)
			return "";

		// Create a string buffer that is twice initial length.
		// This is a good starting point.
		StringBuffer sb = new StringBuffer(name.length() * 2);

		int len = oldT.length();
		try {
			int start = 0;
			int i = name.indexOf(oldT, start);

			while (i >= 0) {
				sb.append(name.substring(start, i));
				sb.append(newT);
				start = i + len;
				i = name.indexOf(oldT, start);
			}
			if (start < name.length())
				sb.append(name.substring(start));
		} catch (NullPointerException e) {
			// No FFDC code needed
		}

		return new String(sb);
	}
    
    private void processCommonExceptions(AnnuityJAXRSReturn ar) throws InvalidArgumentException, ServerBusinessModuleException, ServerPersistenceModuleException{
    	if (InvalidArgumentException.class.getName().equals(ar.getErrorClass())){
    		throw new InvalidArgumentException(ar.getErrorMsg());
    	}
    	if (ServerBusinessModuleException.class.getName().equals(ar.getErrorClass())){
    		throw new ServerBusinessModuleException(ar.getErrorMsg());
    	}
    	if (ServerPersistenceModuleException.class.getName().equals(ar.getErrorClass())){
    		throw new ServerPersistenceModuleException(ar.getErrorMsg());
    	}
    	if (EntityNotFoundException.class.getName().equals(ar.getErrorClass())) {
    		return;  // let calling method handle this one, since it's not common
    	}
    	if (EntityAlreadyExistsException.class.getName().equals(ar.getErrorClass())) {
    		return;  // let calling method handle this one, since it's not common
    	}
    	throw new RuntimeException("Unexpected exception class. Error class is: " + ar.getErrorClass() 
    			+ "error message is: " + ar.getErrorMsg() + " internal logs: " +logger.getAllLogs());   	
    }

	/**
	 * 
	 * requestObject is non-null when calling with POST or PUT
	 * configurable is non-null when calling with GET or DELETE
	 * @throws ServerAdapterCommunicationException 
	 * 
	 */
	private AnnuityJAXRSReturn executeAsync(Object requestObject,
			Configrable<String, String> configurable , String methodType,
			String urlPath) {
		
		int responseCode = 0;
			
		Client client;
		WebTarget target;
		Invocation.Builder builder = null;
		String mediaType = null;
		
		try {
			URL url = null;
			try {
				url = new URL(serverURL);
			} catch (MalformedURLException e) {
				throw new ServerAdapterCommunicationException(
						"a URL provided in the execution unit is not a valid URL. Current value is: "
								+ serverURL + " Error is: " + e);
	
			}
	
			logger.fine("Host: " + url.getHost());
			logger.fine("Port: " + url.getPort());
			
			client = getRestClient();
			target = client.target(serverURL + urlPath);
				
			logger.fine("contentType is set to: " + this.contentType);
			
			if (JAXB.equalsIgnoreCase(this.contentType)) {
				mediaType = MediaType.APPLICATION_XML;
				
			} 
			//jaxrs-2.0 client and ATOM payload is not fully implemented so it is not working now
			else if (ATOM.equalsIgnoreCase(this.contentType)) {
				mediaType = MediaType.APPLICATION_ATOM_XML;
				
				//wink client
				//resource.contentType(MediaType.APPLICATION_ATOM_XML);
				//resource.accept(MediaType.APPLICATION_ATOM_XML);
				// wrap it in an Atom object for testing
			
			} else {
				//jaxrs-2.0 client does not work with JSON b/c Jackson issues. See defect 160064
				mediaType = MediaType.APPLICATION_JSON;
			
			}
			
	
			
			AnnuityJAXRSReturn annuityReturn = null;
			Future<Response> clientResponse = null;	
			
			logger.fine("MediaType : " + mediaType);
			
			builder = target.request(mediaType).accept(mediaType);
			
			/*
			InvocationCallback<Response> callback = new InvocationCallback<Response>() {
				@Override
				public void completed(final Response res) {
					logger.fine("Request success!");
					
				}
				@Override
				public void failed(final Throwable throwable) {
					logger.fine("Request failed!");
					throwable.printStackTrace();
				}
			}; */
			
			try {
				if (methodType.equalsIgnoreCase(GET)) {
					logger.fine("processing GET method. URL: " + serverURL
							+ urlPath);
					printThread("calling with GET");
					
					target = setConfigParamsInTarget(target, configurable);
					clientResponse = target.request(mediaType).async().get(callbackReg());
					//clientResponse = target.request(mediaType).async().get(callback);
					
				} else if (methodType.equalsIgnoreCase(POST)) {
					logger.fine("processing POST method. URL: " + serverURL
							+ urlPath);
					
					printThread("calling with POST");
					clientResponse = builder.async().post(Entity.entity(requestObject,mediaType),callbackReg());
									
				} 
				
				else if (methodType.equalsIgnoreCase(PUT)) {
					logger.fine("processing PUT method. URL: " + serverURL
							+ urlPath);
					printThread("calling with PUT");
					clientResponse = builder.async().put(Entity.entity(requestObject, mediaType),callbackReg() );
					
				} else if (methodType.equalsIgnoreCase(DELETE)) {
					logger.fine("processing DELETE method. URL: " + serverURL
							+ urlPath);
					printThread("calling with DELETE");
					
					target = setConfigParamsInTarget(target, configurable);
					clientResponse = target.request(mediaType).async().delete(callbackReg());
	
				}
				logger.fine("waitAndCheckForValidResponse ....." );
				waitAndCheckForValidResponse(clientResponse, configurable);
			
				logger.fine("Getting clientResponse status code ..." );
				responseCode = clientResponse.get().getStatus();
							
				logger.fine("Executed method " + methodType
						+ ".  response code is: " + responseCode);
				if (responseCode != 200) {
					String msg = "Expected the response code to be 200, but found: "
							+ responseCode;
					logger.info(msg);
					throw new RuntimeException(msg);
				}
				
				// get the client response object
				
				if (ATOM.equalsIgnoreCase(this.contentType)) {
					//jaxrs-2.0 client and ATOM payload is not fully implemented so it is not working now
					annuityReturn = clientResponse.get().readEntity(AtomEntry.class).getContent().getValue(AnnuityJAXRSReturn.class);
				} else {
					annuityReturn = clientResponse.get().readEntity(AnnuityJAXRSReturn.class);
				}
			} catch (Exception e) {
				logger.fine(stackToString(e));
				throw new RuntimeException(e.getMessage());
			} 
			
			// interrogate the return object.
			if (annuityReturn == null) {
				String msg = "Unexpected error . got a null return object. response code is: "
						+ responseCode
						+ " internal logs: "
						+ logger.getAllLogs();
				logger.info(msg);
				throw new RuntimeException(msg);
	
			} else if (annuityReturn.getReturnObjectClass() != null
					&& annuityReturn.getReturnObject() == null) {
				String msg = "Unexpected error . get a return object with a class String, but with null object. response code is: "
						+ responseCode
						+ " internal logs: "
						+ logger.getAllLogs();
				logger.info(msg);
				throw new RuntimeException(msg);
	
			} else if (annuityReturn.getReturnObjectClass() == null
					&& annuityReturn.getReturnObject() != null) {
				String msg = "Unexpected error . get a return object with null class String, but with a non null object. response code is: "
						+ responseCode
						+ " internal logs: "
						+ logger.getAllLogs();
				logger.info(msg);
				throw new RuntimeException(msg);
	
			} else if (annuityReturn.getErrorClass() == null
					&& annuityReturn.getErrorMsg() != null) {
				String msg = "Unexpected error . get a return object with No error class, but with error message. response code is: "
						+ responseCode
						+ " internal logs: "
						+ logger.getAllLogs();
				logger.info(msg);
				throw new RuntimeException(msg);
	
			} else if (annuityReturn.getErrorClass() != null
					&& annuityReturn.getErrorMsg() == null) {
				String msg = "Unexpected error . get a return object with error class, but with no error message. response code is: "
						+ responseCode
						+ " internal logs: "
						+ logger.getAllLogs();
				logger.info(msg);
				throw new RuntimeException(msg);
			}
			
			//Need to close to release resource of a client instance
			client.close();
			
		return annuityReturn;
			
		} catch (ServerAdapterCommunicationException e) {
			logger.fine(stackToString(e));
			throw new RuntimeException(e.getMessage());
		} catch (Throwable t) {
			logger.fine(stackToString(t));
			throw new RuntimeException(t.getMessage());
		}
	
	}
	
	// async callback registration
	private InvocationCallback<Response> callbackReg () {
		
			InvocationCallback<Response> callback = new InvocationCallback<Response>() {
				@Override
				public void completed(final Response res) {
					logger.fine("Request success!");
					
				}
				@Override
				public void failed(final Throwable throwable) {
					logger.fine("Request failed!");
					throwable.printStackTrace();
				}
			};
		return callback;
	}
	
	// set client config key/value pairs in WebTarget
	private WebTarget setConfigParamsInTarget(WebTarget t, Configrable<String, String> config){
		
		logger.fine("Setting keys/value pairs to WebTarget...");
		for (String key: config.getConfiguration().getParameters().keySet()){		
    		String value = config.getConfiguration().getParameterValue(key);
    		if (value != null && value.trim().length() >0){
    			t = t.queryParam(key, value);
    			
    		}
    	} 
		
		return t;	
	}
	
	// Print thread id with fine logger
	public void printThread(String message) {
		
		long tid = Thread.currentThread().getId();
		logger.fine("jaxrs-2.0 Async client : " +message+ " : thread id " +tid);
		
	}

}
