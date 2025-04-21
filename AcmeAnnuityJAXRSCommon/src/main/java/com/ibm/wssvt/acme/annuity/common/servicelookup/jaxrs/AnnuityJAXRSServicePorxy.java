
package com.ibm.wssvt.acme.annuity.common.servicelookup.jaxrs;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.wink.client.ApacheHttpClientConfig;
import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.handlers.BasicAuthSecurityHandler;
//import org.apache.wink.client.handlers.LtpaAuthSecurityHandler;
import org.apache.wink.common.model.atom.AtomContent;
import org.apache.wink.common.model.atom.AtomEntry;
import org.apache.wink.common.model.wadl.ObjectFactory;
import org.apache.wink.common.model.wadl.Resources;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

//import com.ibm.ws.util.Base64;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityObject;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityJAXRSReturn;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityValueObject;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.ListOfObjects;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.PayoutValueObject;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.business.jaxrs.MyJAXBResolver;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerBusinessModuleException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.annuity.common.util.MultivaluedMapImpl;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;


public class AnnuityJAXRSServicePorxy implements IAnnuityService {

	private static final String POST = "post";
	private static final String PUT = "put";
	private static final String GET = "get";
	private static final String DELETE = "delete";
	private static final String JAXB ="jaxb";
	private static final String JSON ="json";
	private static final String ATOM = "atom";
	private static final int DEFAULT_READ_TIMEOUT = 600000;
	
	private AcmeLogger logger;
	private String serverURL;
	private byte[] spnegoKey;
	private String contentType;
	private Configrable<String, String> configs;
	
	public AnnuityJAXRSServicePorxy(Configrable<String, String> configs, String serverURL, String contentType, AcmeLogger logger, byte[] spnegoKey) {
		this.logger = logger;		
		this.serverURL = serverURL;
		this.contentType = contentType;
		this.logger.fine("inited ok");
		this.spnegoKey = spnegoKey;
		this.configs = configs;
		
		if (JAXB.equalsIgnoreCase(contentType)){
			this.contentType = JAXB; 
		}else if (JSON.equalsIgnoreCase(contentType)){
			this.contentType= JSON;
		}else if (ATOM.equalsIgnoreCase(contentType)) {
			this.contentType = ATOM;
		}else{
			// default to JAXB			
			logger.fine("contentType is not JAXB, JSON, or ATOM.  Defaulting to JAXB.  Current value is: " + this.contentType);
			this.contentType = JAXB;
		}
	}
	
	private class MyJAXRSApplication extends Application {
		
		@Override
        public Set<Class<?>> getClasses() {
            Set<Class<?>> set = new HashSet<Class<?>>();
            set.add(MyJAXBResolver.class);
            return set;
        }
		
	    @Override
		public Set<Object> getSingletons() {
	    	Set<Object> set = new HashSet<Object>();
	        set.add(getJacksonProvider());
	        return set;
		}
		
	    private JacksonJsonProvider getJacksonProvider() {
	    	ObjectMapper mapper = new ObjectMapper();
	    	mapper.enableDefaultTyping();
	        JaxbAnnotationIntrospector jaxbIntrospector = new JaxbAnnotationIntrospector();
	        mapper.getSerializationConfig().setAnnotationIntrospector(jaxbIntrospector);
	        mapper.getSerializationConfig().set(Feature.WRITE_DATES_AS_TIMESTAMPS, true);
	        mapper.getDeserializationConfig().setAnnotationIntrospector(jaxbIntrospector);
	        JacksonJsonProvider jacksonProvider = new JacksonJsonProvider();
	        jacksonProvider.setMapper(mapper);
	        return jacksonProvider;
	    }
	}
	
	
    private RestClient getRestClient() throws ServerAdapterCommunicationException {
		ClientConfig config;
		String useApacheClient = configs.getConfiguration().getParameterValue("useApacheClient");
    	if (useApacheClient.equalsIgnoreCase("true")) {
    		config = new ApacheHttpClientConfig();
    	} else {
    		config = new ClientConfig();    		
    	}
    	String useSecurity = configs.getConfiguration().getParameterValue("useSecurity");
    	String userTimeout = configs.getConfiguration().getParameterValue("readTimeout"); 
    	String useSSL = null;
    	boolean isSSLRequired = true;    	
    	if (configs.getConfiguration().getParameterValue("useSSL")!= null )
    		useSSL = configs.getConfiguration().getParameterValue("useSSL");
    	
    	
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
			
			BasicAuthSecurityHandler secHandler = new BasicAuthSecurityHandler();	
			if ("false".equalsIgnoreCase(useSSL))				
				isSSLRequired = false;
			
			//Rumana commented out on 02/21/2022 - for compiling with Maven
			//apache 1.4 does not have that field
			//secHandler.setSSLRequired(isSSLRequired);
			secHandler.setUserName(userName);
			secHandler.setPassword(userPassword);		
			config.handlers(secHandler);			
    	}
//    	if ("DOWN_STREAM".equalsIgnoreCase(useSecurity)) {
//    		logger.fine("useSecurity is enabled and set to DOWN_STREAM.");
//			LtpaAuthSecurityHandler ltpaSecHandler = new LtpaAuthSecurityHandler();
//			config.handlers(ltpaSecHandler);
//		}
    	
    	if (userTimeout != null){    		
    		config.readTimeout(Integer.parseInt(userTimeout));
    		logger.fine("Read timeout value is set to: " + userTimeout);
    	}
    	else{
    		config.readTimeout(DEFAULT_READ_TIMEOUT);
    		logger.fine("Read timeout value is set to: " + DEFAULT_READ_TIMEOUT);
    	}
    	
// if you wish to test spnego, do something like this:
//			if (spnegoKey != null){
//				logger.fine("SPNEGO key is set to: " + spnegoKey);
//				method.addRequestHeader("Authorization", "Negotiate " + Base64.encode(spnegoKey));
//				method.addRequestHeader("Cookie","ClientAuthenticationToken=FFEEBCC");
//			}						

        return new RestClient(config.applications(new MyJAXRSApplication()));
		
    }


	@Override
	public IAnnuity createAnnuity(IAnnuity ann)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerify(ann, null, POST, "/annuity", AnnuityValueObject.class);
		if (ret.getErrorClass() != null) {
			if (ret.getErrorClass().equals(EntityAlreadyExistsException.class.getName())){
				throw new EntityAlreadyExistsException(ret.getErrorMsg());
			}
		}
		AnnuityValueObject result = (AnnuityValueObject)ret.getReturnObject();		
		return result.getAnnuity();
	}

	@Override
	public IAnnuityHolder createAnnuityHolder(IAnnuityHolder annHolder)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		
		AnnuityJAXRSReturn ret = executeAndVerify(annHolder, null, POST, "/annuityholder", null);
		if (ret.getErrorClass() != null) {
			if (ret.getErrorClass().equals(EntityAlreadyExistsException.class.getName())){
				throw new EntityAlreadyExistsException(ret.getErrorMsg());
			}
		}
		return (IAnnuityHolder) ret.getReturnObject();		
	}

	

	@Override
	public IPayor createPayor(IPayor payor)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerify(payor, null, POST, "/payor", null);
		if (ret.getErrorClass() != null) {
			if (ret.getErrorClass().equals(EntityAlreadyExistsException.class.getName())){
				throw new EntityAlreadyExistsException(ret.getErrorMsg());
			}
		}
		return (IPayor) ret.getReturnObject();		
			
	}

	@Override
	public IPayout createPayout(IPayout payout)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerify(payout, null, POST, "/payout", PayoutValueObject.class);
		if (ret.getErrorClass() != null) {
			if (ret.getErrorClass().equals(EntityAlreadyExistsException.class.getName())) {
				throw new EntityAlreadyExistsException(ret.getErrorMsg());
			}
		}
		PayoutValueObject result = (PayoutValueObject) ret.getReturnObject();
		return result.getPayout();
	}

	@Override
	public void deleteAnnuity(IAnnuity annuity)
			throws ServerInternalErrorException, InvalidArgumentException {

		executeAndVerify(null, annuity, DELETE, "/annuity/"+annuity.getId(), null);
			
	}

	@Override
	public void deleteAnnuityHolder(IAnnuityHolder annHolder)
			throws ServerInternalErrorException, InvalidArgumentException {

		executeAndVerify(null, annHolder, DELETE, "/annuityholder/"+annHolder.getId(), null);

	}

	@Override
	public void deletePayor(IPayor payor) throws ServerInternalErrorException,
			InvalidArgumentException {

		executeAndVerify(null, payor, DELETE, "/payor/"+payor.getId(), null);

	}

	@Override
	public void deletePayout(IPayout payout)
			throws ServerInternalErrorException, InvalidArgumentException {
		
		executeAndVerify(null, payout, DELETE, "/payout/"+payout.getId(), null);

	}

	@Override
	public void deleteRider(IRider rider) throws ServerInternalErrorException,
			InvalidArgumentException {
		
		executeAndVerify(null, rider, DELETE, "/rider/"+rider.getId(), null);

	}

	@Override
	public IAnnuity findAnnuityById(IAnnuity annuity)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerify(null, annuity, GET, "/annuity/"+annuity.getId(), null);
		if (ret.getErrorClass() != null) {
			if (ret.getErrorClass().equals(EntityNotFoundException.class.getName())){
				throw new EntityNotFoundException(ret.getErrorMsg());
			}
		}
		AnnuityValueObject result = (AnnuityValueObject)ret.getReturnObject();
		return result.getAnnuity();				
	}

	@Override
	public IAnnuityHolder findAnnuityHolder(IAnnuity annuity)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerify(null, annuity, GET, "/annuityholder/annuity/"+annuity.getId(), null);
		if (ret.getErrorClass() != null) {
			if (ret.getErrorClass().equals(EntityNotFoundException.class.getName())){
				throw new EntityNotFoundException(ret.getErrorMsg());
			}
		}
		return (IAnnuityHolder)ret.getReturnObject();
						
	}
	
	@Override
	public IContact createContact(IContact contact)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerify(contact, null, POST, "/contact", null);
		if (ret.getErrorClass() != null) {
			if (ret.getErrorClass().equals(EntityAlreadyExistsException.class.getName())){
				throw new EntityAlreadyExistsException(ret.getErrorMsg());
			}
		}
		IContact result = (IContact )ret.getReturnObject();
		return result;
	}
	
	@Override
	public IContact updateContact(IContact contact)
			throws ServerInternalErrorException, InvalidArgumentException {
		
		AnnuityJAXRSReturn ret = executeAndVerify(contact, null, PUT, "/contact", null);
		IContact result = (IContact)ret.getReturnObject();
		return result;
	}
	
	@Override
	public IContact findContactById(IContact contact)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		
		AnnuityJAXRSReturn ret = executeAndVerify(null, contact, GET, "/contact/"+contact.getId(), null);
		if (ret.getErrorClass() != null) {
			if (ret.getErrorClass().equals(EntityNotFoundException.class.getName())){
				throw new EntityNotFoundException(ret.getErrorMsg());
			}
		}
		IContact result = (IContact )ret.getReturnObject();
		return result;				
	}
	
	@Override
	public void deleteContact(IContact contact)
			throws ServerInternalErrorException, InvalidArgumentException {

		executeAndVerify(null, contact, DELETE, "/contact/"+contact.getId(), null);
	}
	
	@Override
	public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		
		AnnuityJAXRSReturn ret = executeAndVerify(null, annuityHolder, GET, "/annuity/holder/"+annuityHolder.getId(), null);

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
	public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		
		AnnuityJAXRSReturn ret = executeAndVerify(null, annuityHolder, GET, "/annuityholder/"+annuityHolder.getId(), null);
		if (ret.getErrorClass() != null) {
			if (ret.getErrorClass().equals(EntityNotFoundException.class.getName())){
				throw new EntityNotFoundException(ret.getErrorMsg());
			}
		}
		IAnnuityHolder result = (IAnnuityHolder)ret.getReturnObject();
		return result;				
	}

	@Override
	public List<IAnnuity> findPayorAnnuities(IPayor payor)
			throws ServerInternalErrorException, InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerify(null, payor, GET, "/annuity/payor/"+payor.getId(), null);
		
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
		
		AnnuityJAXRSReturn ret = executeAndVerify(null, payor, GET, "/payor/"+payor.getId(), null);
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


		AnnuityJAXRSReturn ret = executeAndVerify(null, payout, GET, "/payout/"+payout.getId(), null);
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
		
		AnnuityJAXRSReturn ret = executeAndVerify(null, rider, GET, "/rider/"+rider.getId(), null);
		if (ret.getErrorClass() != null){
			if (ret.getErrorClass().equals(EntityNotFoundException.class.getName())){
				throw new EntityNotFoundException(ret.getErrorMsg());
			}
		}
		IRider result = (IRider)ret.getReturnObject();
		return result;				
	}

	@Override
	public IAnnuity updateAnnuity(IAnnuity ann)
			throws ServerInternalErrorException, InvalidArgumentException {
		
		AnnuityJAXRSReturn ret = executeAndVerify(ann, null, PUT, "/annuity", AnnuityValueObject.class);		
		AnnuityValueObject result = (AnnuityValueObject)ret.getReturnObject();
		return result.getAnnuity();
	}

	@Override
	public IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		
		AnnuityJAXRSReturn ret = executeAndVerify(annHolder, null, PUT, "/annuityholder", null);		
		IAnnuityHolder result = (IAnnuityHolder)ret.getReturnObject();
		return result;
	}


	@Override
	public IPayor updatePayor(IPayor payor)
			throws ServerInternalErrorException, InvalidArgumentException {
		
		AnnuityJAXRSReturn ret = executeAndVerify(payor, null, PUT, "/payor", null);		
		IPayor result = (IPayor)ret.getReturnObject();
		return result;
	}

	@Override
	public IPayout updatePayout(IPayout payout)
			throws ServerInternalErrorException, InvalidArgumentException {

		AnnuityJAXRSReturn ret = executeAndVerify(payout, null, PUT, "/payout", PayoutValueObject.class);
		PayoutValueObject result = (PayoutValueObject)ret.getReturnObject();
		return result.getPayout();
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

	private AnnuityJAXRSReturn executeAndVerify(IAnnuityObject requestObject, Configrable<String, String> configrable, String methodType, String urlPath, Class wrapperObjectClass) throws InvalidArgumentException, ServerBusinessModuleException, ServerPersistenceModuleException {

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

		AnnuityJAXRSReturn ret = execute(realRequestObject, configrable, methodType, urlPath);
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
    
	/**
	 * 
	 * requestObject is non-null when calling with POST or PUT
	 * configurable is non-null when calling with GET or DELETE
	 * 
	 */

	private AnnuityJAXRSReturn execute(Object requestObject,
			Configrable<String, String> configrable, String methodType,
			String urlPath) {
		int responseCode = 0;

		RestClient client;
		Resource resource;

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
			resource = client.resource(serverURL + urlPath);			
			
			
			logger.fine("contentType is set to: " + this.contentType);
			if (JAXB.equalsIgnoreCase(this.contentType)) {
				resource.contentType(MediaType.APPLICATION_XML);
				resource.accept(MediaType.APPLICATION_XML);
			} else if (ATOM.equalsIgnoreCase(this.contentType)) {
				resource.contentType(MediaType.APPLICATION_ATOM_XML);
				resource.accept(MediaType.APPLICATION_ATOM_XML);
				// wrap it in an Atom object for testing
				if (requestObject != null) {
					AtomContent atomContent = new AtomContent();
					// requestObject is a JAXB object in all cases
					atomContent.setType(MediaType.APPLICATION_XML);
					atomContent.setValue(requestObject);
					AtomEntry atomEntry = new AtomEntry();
					atomEntry.setContent(atomContent);
					requestObject = atomEntry;
				}
			} else {
				resource.contentType(MediaType.APPLICATION_JSON);
				resource.accept(MediaType.APPLICATION_JSON);
			}

			AnnuityJAXRSReturn annuityReturn = null;
			try {
//				if (spnegoKey != null) {
//					logger.fine("SPNEGO key is set to: " + spnegoKey);
//					resource.header("Authorization", "Negotiate "
//							+ Base64.encode(spnegoKey));
//					resource.cookie("ClientAuthenticationToken=FFEEBCC");
//				}
				ClientResponse clientResponse = null;
				if (configs.getConfiguration().getParameterValue("useWADL")!= null ){		    		
		    		if ("true".equalsIgnoreCase(configs.getConfiguration().getParameterValue("useWADL"))){
		    			logger.info("useWADL is set to true");	    				
		    			clientResponse = client.resource(serverURL + urlPath).accept("application/xml").options(); 
	    				org.apache.wink.common.model.wadl.Application wadlDoc = clientResponse.getEntity(org.apache.wink.common.model.wadl.Application.class);
	    				if (wadlDoc != null){
	    					logger.info("Got WADL");
	    					List<Resources> resourcesFromWADL = wadlDoc.getResources();
		    				int count = resourcesFromWADL.size();
		    				logger.info("size of the WADL resources is: " + count);
		    				Marshaller marshaller = JAXBContext.newInstance(ObjectFactory.class).createMarshaller();
		    				for (int i = 0; i < count; i++){
		    					Resources r = resourcesFromWADL.get(i);
		    					logger.info("Resources in the WADL");		    			        
		    			        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		    			        StringWriter writer = new StringWriter();
		    			        marshaller.marshal(r, writer);
		    			        logger.info(writer.getBuffer().toString());		    						    					
		    				}
	    				}	    						    				
		    		}
				}
				
				if (methodType.equalsIgnoreCase(GET)) {
					logger.fine("processing GET method. URL: " + serverURL
							+ urlPath);
					resource.queryParams(toMultivaluedMap(configrable));
					clientResponse = resource.get();
				} else if (methodType.equalsIgnoreCase(POST)) {
					logger.fine("processing POST method. URL: " + serverURL
							+ urlPath);
					clientResponse = resource.post(requestObject);
				} else if (methodType.equalsIgnoreCase(PUT)) {
					logger.fine("processing PUT method. URL: " + serverURL
							+ urlPath);
					clientResponse = resource.put(requestObject);
				} else if (methodType.equalsIgnoreCase(DELETE)) {
					logger.fine("processing DELETE method. URL: " + serverURL
							+ urlPath);
					resource.queryParams(toMultivaluedMap(configrable));
					clientResponse = resource.delete();
				}
				responseCode = clientResponse.getStatusCode();
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
					// need to unwrap the response
					annuityReturn = clientResponse.getEntity(AtomEntry.class).getContent().getValue(AnnuityJAXRSReturn.class);
				} else {
					annuityReturn = clientResponse
							.getEntity(AnnuityJAXRSReturn.class);
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

			return annuityReturn;
		} catch (ServerAdapterCommunicationException e) {
			logger.fine(stackToString(e));
			throw new RuntimeException(e.getMessage());
		} catch (Throwable t) {
			logger.fine(stackToString(t));
			throw new RuntimeException(t.getMessage());
		}

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
    
    private MultivaluedMap<String, String> toMultivaluedMap(Configrable<String, String> configrable){
    	MultivaluedMap<String, String> multiValuedMap = new MultivaluedMapImpl<String, String>();
    	if (configrable == null || configrable.getConfiguration() == null || configrable.getConfiguration().getParameters() == null) return multiValuedMap;
    	for (String key: configrable.getConfiguration().getParameters().keySet()){
    		String value = configrable.getConfiguration().getParameterValue(key);
    		if (value != null && value.trim().length() >0){
    			multiValuedMap.add(key, value);
    		}
    	}
    	logger.fine("Params: " + multiValuedMap);
    	return multiValuedMap;
    	
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


    
}

