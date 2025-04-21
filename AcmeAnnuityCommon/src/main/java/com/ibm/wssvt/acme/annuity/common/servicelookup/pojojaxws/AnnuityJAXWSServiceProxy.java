package com.ibm.wssvt.acme.annuity.common.servicelookup.pojojaxws;

import java.util.ArrayList;
import java.util.List;

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
import com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl.AnnuityMgmtSvcPojoJAXWS;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl.EntityAlreadyExistsException_Exception;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl.EntityNotFoundException_Exception;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl.InvalidArgumentException_Exception;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl.ServerBusinessModuleException_Exception;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl.ServerPersistenceModuleException_Exception;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerBusinessModuleException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.common.exception.ExceptionFormatter;


public class AnnuityJAXWSServiceProxy implements IAnnuityService{

	private AnnuityMgmtSvcPojoJAXWS webService;
	public AnnuityJAXWSServiceProxy(AnnuityMgmtSvcPojoJAXWS webServiceInterface ){
		this.webService = webServiceInterface;
	}
	private void processCommonExceptions(Exception e) throws InvalidArgumentException, ServerBusinessModuleException, ServerPersistenceModuleException{
		if (e instanceof InvalidArgumentException_Exception) {
			throw new InvalidArgumentException(e);
		}else if (e instanceof ServerBusinessModuleException_Exception) {
			throw new ServerBusinessModuleException(e);
		}else if (e instanceof ServerPersistenceModuleException_Exception) {
			throw new ServerPersistenceModuleException(e);
		} else{
			e.printStackTrace();
			throw new RuntimeException("Unexpected error. Erros is: " + ExceptionFormatter.deepFormatToString(e), e);
		}
	}
	public IAnnuity createAnnuity(IAnnuity ann) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		AnnuityValueObject result = null;
		try {
			AnnuityValueObject annuityValueObject = new AnnuityValueObject();
			annuityValueObject.setAnnuity(ann);
			result = webService.createAnnuity(annuityValueObject);
			// rebuild remote relationship
			annuityValueObject.getAnnuity();
		} catch (EntityAlreadyExistsException_Exception e){
			throw new EntityAlreadyExistsException(e);
		}catch(Exception e){
			processCommonExceptions(e);
		}			
		return (result == null)? null : result.getAnnuity();
		
	}

	public IAnnuity findAnnuityById(IAnnuity annuity) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {			
		AnnuityValueObject result = null;
		try {
			AnnuityValueObject annuityValueObject = new AnnuityValueObject();
			annuityValueObject.setAnnuity(annuity);
			result = webService.findAnnuityById(annuityValueObject);
			// rebuild remote relationship
			annuityValueObject.getAnnuity();
		} catch (EntityNotFoundException_Exception e) {
			throw new EntityNotFoundException(e);
		}catch (Exception e) {
			processCommonExceptions(e);
		}
		return (result == null)? null : result.getAnnuity();			
	}

	public IAnnuityHolder findAnnuityHolder(IAnnuity annuity) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		IAnnuityHolder result = null;
		try {
			AnnuityValueObject annuityValueObject = new AnnuityValueObject();
			annuityValueObject.setAnnuity(annuity);
			result = webService.findAnnuityHolder(annuityValueObject);
			// rebuild remote relationship
			annuityValueObject.getAnnuity();
		} catch (EntityNotFoundException_Exception e) {
			throw new EntityNotFoundException(e); 
		} catch (Exception e) {
			processCommonExceptions(e);
		}
		return result;

	}

	public IContact findContactById(IContact contact) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		IContact result = null;
		try {
			result = webService.findContactById((Contact)contact);					
		} catch (EntityNotFoundException_Exception e) {
			throw new EntityNotFoundException(e); 
		} catch (Exception e) {
			e.printStackTrace();				
			processCommonExceptions(e);
		}
		return result;

	}

	public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder) throws ServerInternalErrorException, InvalidArgumentException {
		List<IAnnuity> result = new ArrayList<IAnnuity>();
		List<AnnuityValueObject> tempList = new ArrayList<AnnuityValueObject>();
		try {
			tempList = webService.findHolderAnnuities((AnnuityHolder) annuityHolder);
			for (AnnuityValueObject object : tempList) {
				result.add(object.getAnnuity());
			}
		} catch (Exception e){
			processCommonExceptions(e);
		}
		return result;
	}

	public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		IAnnuityHolder result = null;
		try {
			result = webService.findHolderById((AnnuityHolder)annuityHolder);									
		} catch(EntityNotFoundException_Exception e) {				
			throw new EntityNotFoundException(e);				
		}catch (Exception e) {
			processCommonExceptions(e);
		}
		return result;
	}

	public List<IAnnuity> findPayorAnnuities(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		List<IAnnuity> result = null;
		try{
			List<AnnuityValueObject> tempList = webService.findPayorAnnuities((Payor) payor);
			if (tempList != null){				
				result = new ArrayList<IAnnuity>();
				for (AnnuityValueObject object : tempList) {
					result.add(object.getAnnuity());
				}
			}
		}catch (Exception e) {
			processCommonExceptions(e);
		}
		return result;
		
	}

	public IPayor findPayorById(IPayor payor) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		IPayor result = null;
		try {
			result = webService.findPayorById((Payor)payor);									
		}  catch (EntityNotFoundException_Exception e) {
			throw new EntityNotFoundException(e); 
		} catch (Exception e) {
			processCommonExceptions(e);
		}
		return result;			
	}

	public IPayout findPayoutById(IPayout payout) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		PayoutValueObject result = null;
		try {
			PayoutValueObject in = new PayoutValueObject();
			in.setPayout(payout);
			result = webService.findPayoutById(in);
			// rebuild remote relationship
			in.getPayout();				
		} catch (EntityNotFoundException_Exception e) {
			throw new EntityNotFoundException(e); 
		} catch (Exception e) {
			processCommonExceptions(e);
		}			
		return (result== null) ? null: result.getPayout();		
	}

	public IRider findRiderById(IRider rider) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException {
		IRider result = null;
		try {
			result = webService.findRiderById((Rider) rider);									
		} catch (EntityNotFoundException_Exception e) {
			throw new EntityNotFoundException(e); 
		} catch (Exception e) {
			processCommonExceptions(e);
		}
		return result;
	}

	public IAnnuityHolder createAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		IAnnuityHolder result = null;
		try {
			result = webService.createAnnuityHolder((AnnuityHolder) annHolder);									
		} catch (EntityAlreadyExistsException_Exception e){
			throw new EntityAlreadyExistsException(e);
		} catch (Exception e) {
			processCommonExceptions(e);
		}
		return result;
	}

	public IContact createContact(IContact contact) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		IContact result = null;
		try {
			result = webService.createContact((Contact)contact);									
		} catch (EntityAlreadyExistsException_Exception e){
			throw new EntityAlreadyExistsException(e);
		}catch (Exception e) {
			processCommonExceptions(e);
		}
		return result;
	}

	public IPayor createPayor(IPayor payor) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		IPayor result = null;
		try {
			result = webService.createPayor((Payor)payor);									
		} catch (EntityAlreadyExistsException_Exception e){
			throw new EntityAlreadyExistsException(e);
		} catch (Exception e) {
			processCommonExceptions(e);
		}
		return result;		}

	public IPayout createPayout(IPayout payout) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {
		PayoutValueObject result = null;
		try {				
			PayoutValueObject in = new PayoutValueObject();
			in.setPayout(payout);				
			result = webService.createPayout(in);
			// rebuild the remote relationship
			in.getPayout();				
		} catch (EntityAlreadyExistsException_Exception e){
			throw new EntityAlreadyExistsException(e);
		} catch (Exception e) {
			processCommonExceptions(e);
		}
		return (result == null) ? null : result.getPayout();
	}

	public void deleteAnnuity(IAnnuity annuity) throws ServerInternalErrorException, InvalidArgumentException {			
		try {
			AnnuityValueObject annuityValueObject = new AnnuityValueObject();
			annuityValueObject.setAnnuity(annuity);
			webService.deleteAnnuity(annuityValueObject);		
			//  rebuild the remote relationship
			annuityValueObject.getAnnuity();
		} catch (Exception e) {
			processCommonExceptions(e);
		}		
	}

	public void deleteAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, InvalidArgumentException {
		try {							
			webService.deleteAnnuityHolder((AnnuityHolder) annHolder);									
		} catch (Exception e) {
			processCommonExceptions(e);
		}			
	}

	public void deleteContact(IContact contact) throws ServerInternalErrorException, InvalidArgumentException {
		try {							
			webService.deleteContact((Contact)contact);									
		} catch (Exception e) {
			processCommonExceptions(e);
		}						
	}

	public void deletePayor(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		try {							
			webService.deletePayor((Payor)payor);									
		} catch (Exception e) {
			processCommonExceptions(e);
		}			
	}

	public void deletePayout(IPayout payout) throws ServerInternalErrorException, InvalidArgumentException {
		try {						
			PayoutValueObject in = new PayoutValueObject();
			in.setPayout(payout);
			webService.deletePayout(in);
			// rebuild remote relationship
			in.getPayout();

		} catch (Exception e) {
			processCommonExceptions(e);
		}						
	}

	public void deleteRider(IRider rider) throws ServerInternalErrorException, InvalidArgumentException {
		try {							
			webService.deleteRider((Rider)rider);									
		} catch (Exception e) {
			processCommonExceptions(e);
		}						
	}

	public IAnnuity updateAnnuity(IAnnuity annuity) throws ServerInternalErrorException, InvalidArgumentException {
		AnnuityValueObject result =null;
		try {							
			AnnuityValueObject annuityValueObject = new AnnuityValueObject();
			annuityValueObject.setAnnuity(annuity);
			result = webService.updateAnnuity(annuityValueObject);
			//rebuild the remote relationship
			annuityValueObject.getAnnuity();				
		} catch (Exception e) {
			processCommonExceptions(e);
		}	
		return (result == null)? null : result.getAnnuity();
	}

	public IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annHolder) throws ServerInternalErrorException, InvalidArgumentException {
		IAnnuityHolder result =null;
		try {							
			result = webService.updateAnnuityHolder((AnnuityHolder)annHolder);				
		} catch (Exception e) {
			processCommonExceptions(e);
		}	
		return result;
	}

	public IContact updateContact(IContact contact) throws ServerInternalErrorException, InvalidArgumentException {
		IContact result =null;
		try {							
			result = webService.updateContact((Contact)contact);				
		} catch (Exception e) {
			processCommonExceptions(e);
		}	
		return result;		
	}

	public IPayor updatePayor(IPayor payor) throws ServerInternalErrorException, InvalidArgumentException {
		IPayor result =null;
		try {							
			result = webService.updatePayor((Payor)payor);				
		} catch (Exception e) {
			processCommonExceptions(e);
		}	
		return result;		
	}

	public IPayout updatePayout(IPayout payout) throws ServerInternalErrorException, InvalidArgumentException {
		PayoutValueObject result =null;
		try {							
			PayoutValueObject in = new PayoutValueObject();
			in.setPayout(payout);
			result = webService.updatePayout(in);
			// rebuild remote relationship
			in.getPayout();
		} catch (Exception e) {
			processCommonExceptions(e);
		}	
		return (result == null) ? null : result.getPayout();
	}

}
