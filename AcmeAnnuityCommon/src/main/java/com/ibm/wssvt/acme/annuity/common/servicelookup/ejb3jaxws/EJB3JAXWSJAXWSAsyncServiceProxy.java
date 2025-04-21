package com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3jaxws;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.xml.ws.Response;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityValueObject;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Contact;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Payor;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.PayoutValueObject;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Rider;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.AnnuityMgmtSvcEJB30JAXWS;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.CreateAnnuityHolderResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.CreateAnnuityResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.CreateContactResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.CreatePayorResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.CreatePayoutResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.DeleteAnnuityHolderResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.DeleteAnnuityResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.DeleteContactResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.DeletePayorResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.DeletePayoutResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.DeleteRiderResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.EntityAlreadyExistsException_Exception;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.EntityNotFoundException_Exception;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.FindAnnuityByIdResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.FindAnnuityHolderResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.FindContactByIdResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.FindHolderAnnuitiesResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.FindHolderByIdResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.FindPayorAnnuitiesResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.FindPayorByIdResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.FindPayoutByIdResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.FindRiderByIdResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.InvalidArgumentException_Exception;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.ServerBusinessModuleException_Exception;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.ServerPersistenceModuleException_Exception;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.UpdateAnnuityHolderResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.UpdateAnnuityResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.UpdateContactResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.UpdatePayorResponse;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.UpdatePayoutResponse;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerBusinessModuleException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.exception.ExceptionFormatter;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class EJB3JAXWSJAXWSAsyncServiceProxy implements IAnnuityService{

	private AnnuityMgmtSvcEJB30JAXWS webService;
	private AcmeLogger logger;
	public EJB3JAXWSJAXWSAsyncServiceProxy(AnnuityMgmtSvcEJB30JAXWS webServiceInterface, AcmeLogger logger ){
		this.webService = webServiceInterface;
		this.logger = logger;		
	}

	public IAnnuity createAnnuity(IAnnuity annuity) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		AnnuityValueObject result = null;
		
		try {							
			AnnuityValueObject annuityValueObject = new AnnuityValueObject();
			annuityValueObject.setAnnuity(annuity);			
			Response<CreateAnnuityResponse> response = webService.createAnnuityAsync(annuityValueObject);			
			waitAndCheckForValidResponse(response, annuity);
			//rebuild the remote relationship
			annuityValueObject.getAnnuity();						
			result = response.get().getReturn();
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processEntityAlreadyExistsException(cause);
			processCommonException(cause);
		}								
		return (result == null)? null : result.getAnnuity();
		
	}

	public IAnnuity findAnnuityById(IAnnuity annuity) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {			
		AnnuityValueObject result = null;
		try {
			AnnuityValueObject annuityValueObject = new AnnuityValueObject();
			annuityValueObject.setAnnuity(annuity);
			Response<FindAnnuityByIdResponse> response = webService.findAnnuityByIdAsync(annuityValueObject);
			waitAndCheckForValidResponse(response, annuity);
			//rebuild the remote relationship
			annuityValueObject.getAnnuity();			
			result = response.get().getReturn();				
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processEntityNotFoundException(cause);
			processCommonException(cause);
		}			
		return (result == null)? null : result.getAnnuity();			
	}

	public IAnnuityHolder findAnnuityHolder(IAnnuity annuity) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		IAnnuityHolder result = null;
		try {
			AnnuityValueObject annuityValueObject = new AnnuityValueObject();
			annuityValueObject.setAnnuity(annuity);			
			Response<FindAnnuityHolderResponse> response = webService.findAnnuityHolderAsync(annuityValueObject);
			waitAndCheckForValidResponse(response, annuity);
			//rebuild the remote relationship
			annuityValueObject.getAnnuity();						
			result = response.get().getReturn();
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processEntityNotFoundException(cause);
			processCommonException(cause);
		}			
		return result;

	}

	public IContact findContactById(IContact contact) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		IContact result = null;
		try {			
			Response<FindContactByIdResponse> response = webService.findContactByIdAsync((Contact)contact);
			waitAndCheckForValidResponse(response, contact);
			result = response.get().getReturn();
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processEntityNotFoundException(cause);
			processCommonException(cause);
		}			
		return result;

	}

	public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder) throws ServerInternalErrorException, InvalidArgumentException {
		List<IAnnuity> result = new ArrayList<IAnnuity>();
		List<AnnuityValueObject> tempList = new ArrayList<AnnuityValueObject>();
		try {			
			Response<FindHolderAnnuitiesResponse> response = webService.findHolderAnnuitiesAsync((AnnuityHolder) annuityHolder);
			waitAndCheckForValidResponse(response, annuityHolder);
			tempList = response.get().getReturn();
			for (AnnuityValueObject object : tempList) {
				result.add(object.getAnnuity());
			}
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();			
			processCommonException(cause);
		}			
		return result;
	}

	public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		IAnnuityHolder result = null;
		try {			
			Response<FindHolderByIdResponse> response = webService.findHolderByIdAsync((AnnuityHolder) annuityHolder);
			waitAndCheckForValidResponse(response, annuityHolder);
			result = response.get().getReturn();
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processEntityNotFoundException(cause);
			processCommonException(cause);
		}		
		return result;
	}

	public List<IAnnuity> findPayorAnnuities(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		List<IAnnuity> result = null;
		try{			
			Response<FindPayorAnnuitiesResponse> response = webService.findPayorAnnuitiesAsync((Payor)payor);
			waitAndCheckForValidResponse(response, payor);
			List<AnnuityValueObject> tempList = response.get().getReturn();
			if (tempList != null){				
				result = new ArrayList<IAnnuity>();
				for (AnnuityValueObject object : tempList) {
					result.add(object.getAnnuity());
				}
			}
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processCommonException(cause);
		}		

		return result;
		
	}

	public IPayor findPayorById(IPayor payor) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		IPayor result = null;
		try {				
			Response<FindPayorByIdResponse> response = webService.findPayorByIdAsync((Payor) payor);
			waitAndCheckForValidResponse(response, payor);
			result = response.get().getReturn();
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processEntityNotFoundException(cause);
			processCommonException(cause);
		}		
		return result;			
	}

	public IPayout findPayoutById(IPayout payout) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		PayoutValueObject result = null;
		try {
			PayoutValueObject in = new PayoutValueObject();
			in.setPayout(payout);			
			Response<FindPayoutByIdResponse> response = webService.findPayoutByIdAsync(in);			
			waitAndCheckForValidResponse(response, payout);
			// rebuild remote relationship
			in.getPayout();
			result = response.get().getReturn();			
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processEntityNotFoundException(cause);
			processCommonException(cause);
		}		
		return (result== null) ? null: result.getPayout();		
	}

	public IRider findRiderById(IRider rider) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		IRider result = null;
		try {			
			Response<FindRiderByIdResponse> response = webService.findRiderByIdAsync((Rider) rider);
			waitAndCheckForValidResponse(response, rider);
			result = response.get().getReturn();
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processEntityNotFoundException(cause);
			processCommonException(cause);
		}		
		return result;
	}

	public IAnnuityHolder createAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		IAnnuityHolder result = null;
		try {
			Response<CreateAnnuityHolderResponse> response = webService.createAnnuityHolderAsync((AnnuityHolder) annHolder);														
			waitAndCheckForValidResponse(response, annHolder);
			result = response.get().getReturn();					
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processEntityAlreadyExistsException(cause);
			processCommonException(cause);
		}		
		return result;
	}

	
	public IContact createContact(IContact contact) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		IContact result = null;
		try {
			Response<CreateContactResponse> response = webService.createContactAsync((Contact)contact);								
			waitAndCheckForValidResponse(response, contact);
			result = response.get().getReturn();										
		} catch (InterruptedException e) {			
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();					
			processEntityAlreadyExistsException(cause);
			processCommonException(cause);	
		}
		return result;
	}

	public IPayor createPayor(IPayor payor) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		IPayor result = null;
		try {						
			Response<CreatePayorResponse> response = webService.createPayorAsync((Payor) payor);
			waitAndCheckForValidResponse(response, payor);
			result = response.get().getReturn();		
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			processEntityAlreadyExistsException(cause);
			processCommonException(cause);
		} 
		return result;		
	}

	public IPayout createPayout(IPayout payout) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		PayoutValueObject result = null;
		try {				
			PayoutValueObject in = new PayoutValueObject();
			in.setPayout(payout);						
			Response<CreatePayoutResponse> response = webService.createPayoutAsync(in);			
			waitAndCheckForValidResponse(response, payout);
			// rebuild the remote relationship
			in.getPayout();							
			result = response.get().getReturn();
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			processEntityAlreadyExistsException(cause);
			processCommonException(cause);
		}
		return (result == null) ? null : result.getPayout();
	}

	public void deleteAnnuity(IAnnuity annuity) throws ServerInternalErrorException, InvalidArgumentException {			
		try {
			AnnuityValueObject annuityValueObject = new AnnuityValueObject();
			annuityValueObject.setAnnuity(annuity);			
			Response<DeleteAnnuityResponse> response = webService.deleteAnnuityAsync(annuityValueObject);			
			waitAndCheckForValidResponse(response, annuity);
			//  rebuild the remote relationship
			annuityValueObject.getAnnuity();			
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processCommonException(cause);
		}
	}

	public void deleteAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, InvalidArgumentException {
		try {										
			Response<DeleteAnnuityHolderResponse> response = webService.deleteAnnuityHolderAsync((AnnuityHolder) annHolder);
			waitAndCheckForValidResponse(response, annHolder);			
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processCommonException(cause);
		}
	}

	public void deleteContact(IContact contact) throws ServerInternalErrorException, InvalidArgumentException {
		try {										
			Response<DeleteContactResponse> response = webService.deleteContactAsync((Contact)contact);
			waitAndCheckForValidResponse(response, contact);			
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processCommonException(cause);
		}
		
	}

	public void deletePayor(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		try {										
			Response<DeletePayorResponse> response = webService.deletePayorAsync((Payor)payor);
			waitAndCheckForValidResponse(response, payor);			
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processCommonException(cause);
		}		
	}

	public void deletePayout(IPayout payout) throws ServerInternalErrorException, InvalidArgumentException {
		try {						
			PayoutValueObject in = new PayoutValueObject();
			in.setPayout(payout);		
			Response<DeletePayoutResponse> response = webService.deletePayoutAsync(in);
			waitAndCheckForValidResponse(response, payout);
			// rebuild remote relationship
			in.getPayout();			
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processCommonException(cause);
		}								
	}

	public void deleteRider(IRider rider) throws ServerInternalErrorException, InvalidArgumentException {
		try {										
			Response<DeleteRiderResponse> response = webService.deleteRiderAsync((Rider)rider);
			waitAndCheckForValidResponse(response, rider);			
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processCommonException(cause);
		}						
	}

	public IAnnuity updateAnnuity(IAnnuity annuity) throws ServerInternalErrorException, InvalidArgumentException {
		AnnuityValueObject result =null;
		try {							
			AnnuityValueObject annuityValueObject = new AnnuityValueObject();
			annuityValueObject.setAnnuity(annuity);			
			Response<UpdateAnnuityResponse> response = webService.updateAnnuityAsync(annuityValueObject);			
			waitAndCheckForValidResponse(response, annuity);
			//rebuild the remote relationship
			annuityValueObject.getAnnuity();			
			result = response.get().getReturn();
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processCommonException(cause);
		}		
		return (result == null)? null : result.getAnnuity();
	}

	public IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, InvalidArgumentException {
		IAnnuityHolder result =null;
		try {										
			Response<UpdateAnnuityHolderResponse> response = webService.updateAnnuityHolderAsync((AnnuityHolder) annHolder);
			waitAndCheckForValidResponse(response, annHolder);
			result = response.get().getReturn();
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processCommonException(cause);
		}		
		return result;
	}

	public IContact updateContact(IContact contact) throws ServerInternalErrorException, InvalidArgumentException {
		IContact result =null;
		try {										
			Response<UpdateContactResponse> response = webService.updateContactAsync((Contact)contact);
			waitAndCheckForValidResponse(response, contact);
			result = response.get().getReturn();
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processCommonException(cause);
		}		
		return result;		
	}

	public IPayor updatePayor(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		IPayor result =null;
		try {										
			Response<UpdatePayorResponse> response = webService.updatePayorAsync((Payor)payor);
			waitAndCheckForValidResponse(response, payor);
			result = response.get().getReturn();
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processCommonException(cause);
		}				
		return result;		
	}

	public IPayout updatePayout(IPayout payout) throws ServerInternalErrorException, InvalidArgumentException {
		PayoutValueObject result =null;
		try {							
			PayoutValueObject in = new PayoutValueObject();
			in.setPayout(payout);			
			Response<UpdatePayoutResponse> response = webService.updatePayoutAsync(in);
			waitAndCheckForValidResponse(response, payout);
			// rebuild remote relationship
			in.getPayout();			
			result = response.get().getReturn();
		} catch (InterruptedException e) {
			processInterruptedException(e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();		
			processCommonException(cause);
		}
		return (result == null) ? null : result.getPayout();
	}
	
	@SuppressWarnings("unchecked")
	private void waitForResponse(Response resp, Configrable<String, String> configrable) throws InterruptedException{
		long cntr = 0;
		long waitTime;
		long waitCount;		
		try{
			waitTime = Long.parseLong(configrable.getConfiguration().getParameterValue("waitTime"));
			waitCount = Long.parseLong(configrable.getConfiguration().getParameterValue("waitCount"));
		}catch (Exception e) {
			throw new RuntimeException("Invalid Configuration.  WaitTime or WaitCount are not valid values." +
					"current values are: waitTime:" + configrable.getConfiguration().getParameterValue("waitTime")
					+" and waitCount: " + configrable.getConfiguration().getParameterValue("waitCount"));
		}
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

	@SuppressWarnings("unchecked")
	private void waitAndCheckForValidResponse(Response response, Configrable<String, String> configrable) throws ServerInternalErrorException, InterruptedException, ExecutionException{
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
	private void processEntityNotFoundException (Throwable e) throws EntityNotFoundException {		
		if (e instanceof EntityNotFoundException_Exception){
			throw new EntityNotFoundException(e);
		}
	}
	private void processEntityAlreadyExistsException (Throwable e) throws EntityAlreadyExistsException  {		
		if (e instanceof EntityAlreadyExistsException_Exception){
			throw new EntityAlreadyExistsException(e);
		}
	}
	private void processInterruptedException(InterruptedException e){
		logger.log(Level.SEVERE,"Got an unexpected InterruptedException while waiting for a response object");
		throw new RuntimeException("Got an unexpected InterruptedException while waiting for a response object. error " +ExceptionFormatter.deepFormatToString(e), e);
	}
	private void processCommonException (Throwable e) throws ServerInternalErrorException, InvalidArgumentException {		
		if (e instanceof InvalidArgumentException_Exception) {
			throw new InvalidArgumentException(e);
		}else if (e instanceof ServerBusinessModuleException_Exception) {
			throw new ServerBusinessModuleException(e);
		}else if (e instanceof ServerPersistenceModuleException_Exception) {
			throw new ServerPersistenceModuleException(e);
		} else{
			logger.log(Level.WARNING, getClass().getName()+  " is unable to detect the type of the exception.  Returning a RuntimeException.  Error is: " + ExceptionFormatter.deepFormatToString(e), e);
			e.printStackTrace();
			throw new RuntimeException("Unexpected error. Erros is: " + ExceptionFormatter.deepFormatToString(e),e);
		}
	}
	
}
