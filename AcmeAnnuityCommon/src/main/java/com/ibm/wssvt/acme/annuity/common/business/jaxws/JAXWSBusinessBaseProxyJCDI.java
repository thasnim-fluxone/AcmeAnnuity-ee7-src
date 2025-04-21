package com.ibm.wssvt.acme.annuity.common.business.jaxws;

/*
 * This code was commented when testing jaxws without cdi, but uncommented 
 * since we are using it in the jcdi jaxws App now
 */
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Annuity;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityValueObject;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Contact;
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
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityLoggerFactory;
import com.ibm.wssvt.acme.annuity.common.util.RunningServerInfo;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class JAXWSBusinessBaseProxyJCDI implements AnnuityMgmtSvcJAXWS{

	@Inject AnnuityValueObject avo;
	public AnnuityValueObject createAnnuity(AnnuityValueObject annuityVO) 
	throws ServerBusinessModuleException, ServerPersistenceModuleException, EntityAlreadyExistsException, InvalidArgumentException{
		checkAnnuityValueObject(annuityVO);
		IAnnuity annuity = annuityVO.getAnnuity();	
		AcmeLogger logger = getLogger(annuity);
		try {
			annuity = (Annuity) getAnnuityService(annuity).createAnnuity(annuity);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);			
		}
		RunningServerInfo.getInstance().setRunningServerInfo(annuity);
		//AnnuityValueObject result = new AnnuityValueObject();
		//result.setAnnuity(annuity);
		//return result;	
		if (avo != null){
			logger.fine("JCDI is enabled successfully");
			avo.setAnnuity(annuity);
			logger.fine("create annuity - ready to return. result annuityVO: " + avo );
			return avo;
		}
		else{
			System.out.println("Failed to enable JCDI");
			return null;
		}		
	}


	public AnnuityHolder createAnnuityHolder(AnnuityHolder annHolder) throws ServerBusinessModuleException, ServerPersistenceModuleException, EntityAlreadyExistsException, InvalidArgumentException {
		checkConfigrableArgument(annHolder);
		AnnuityHolder results = null;
		try {													
			results = (AnnuityHolder) getAnnuityService(annHolder).createAnnuityHolder(annHolder);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);			
		}
		RunningServerInfo.getInstance().setRunningServerInfo(results);
		return results;
	}

	public Contact createContact(Contact contact) throws ServerBusinessModuleException, ServerPersistenceModuleException, EntityAlreadyExistsException, InvalidArgumentException {
		checkConfigrableArgument(contact);
		Contact results = null;
		try {
			results = (Contact) getAnnuityService(contact).createContact(contact);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		RunningServerInfo.getInstance().setRunningServerInfo(results);
		return results;
	}

	public Payor createPayor(Payor payor) throws ServerBusinessModuleException, ServerPersistenceModuleException, EntityAlreadyExistsException, InvalidArgumentException {
		checkConfigrableArgument(payor);
		Payor results = null;
		try {
			results = (Payor) getAnnuityService(payor).createPayor(payor);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		RunningServerInfo.getInstance().setRunningServerInfo(results);
		return results;	
	}

	public PayoutValueObject createPayout(PayoutValueObject payoutVO) throws ServerBusinessModuleException, ServerPersistenceModuleException, EntityAlreadyExistsException, InvalidArgumentException {
		checkPayoutValueObject(payoutVO);		
		Payout results = null;
		try {
			results = (Payout) getAnnuityService(payoutVO.getPayout()).createPayout(payoutVO.getPayout());	
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}		
		RunningServerInfo.getInstance().setRunningServerInfo(results);
		PayoutValueObject resObject = new PayoutValueObject();
		resObject.setPayout(results);	
		return resObject;

	}

	public void deleteAnnuity(AnnuityValueObject annuityVO) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		checkAnnuityValueObject(annuityVO);
		try {
			getAnnuityService(annuityVO.getAnnuity()).deleteAnnuity(annuityVO.getAnnuity());
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}			
	}

	public void deleteAnnuityHolder(AnnuityHolder annHolder) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {		
		checkConfigrableArgument(annHolder);
		try {
			getAnnuityService(annHolder).deleteAnnuityHolder(annHolder);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}	
	}

	public void deleteContact(Contact contact) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		checkConfigrableArgument(contact);
		try {
			getAnnuityService(contact).deleteContact(contact);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
	}

	public void deletePayor(Payor payor) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		checkConfigrableArgument(payor);
		try {
			getAnnuityService(payor).deletePayor(payor);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
	}

	public void deletePayout(PayoutValueObject payoutVO) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		checkPayoutValueObject(payoutVO);	
		try {			
			getAnnuityService(payoutVO.getPayout()).deletePayout(payoutVO.getPayout());
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
	}

	public void deleteRider(Rider rider) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		checkConfigrableArgument(rider);
		try {
			getAnnuityService(rider).deleteRider(rider);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}		
	}

	public AnnuityValueObject findAnnuityById(AnnuityValueObject annuityVO) throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		checkAnnuityValueObject(annuityVO);
		AnnuityValueObject results =null;
		try {
			IAnnuity ann = getAnnuityService(annuityVO.getAnnuity()).findAnnuityById(annuityVO.getAnnuity());
			RunningServerInfo.getInstance().setRunningServerInfo(ann);
			results = new AnnuityValueObject();
			results.setAnnuity(ann);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		return results;	

	}

	public AnnuityHolder findAnnuityHolder(AnnuityValueObject annuityVO) throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		checkAnnuityValueObject(annuityVO);
		AnnuityHolder results = null;
		try {
			results = (AnnuityHolder) getAnnuityService(annuityVO.getAnnuity()).findAnnuityHolder(annuityVO.getAnnuity());
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		RunningServerInfo.getInstance().setRunningServerInfo(results);
		return results;	

	}

	public Contact findContactById(Contact contact) throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
	 	
		checkConfigrableArgument(contact);
		Contact results = null;
		try {
			results = (Contact) getAnnuityService(contact).findContactById(contact);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		RunningServerInfo.getInstance().setRunningServerInfo(results);
		return results;
			
	}

	public List<AnnuityValueObject> findHolderAnnuities(AnnuityHolder annuityHolder) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		checkConfigrableArgument(annuityHolder);
		List<AnnuityValueObject> results = new ArrayList<AnnuityValueObject>();
		try {
			List<IAnnuity> annuities = getAnnuityService(annuityHolder).findHolderAnnuities(annuityHolder);
			for (IAnnuity annuity : annuities) {
				RunningServerInfo.getInstance().setRunningServerInfo(annuity);
				AnnuityValueObject annVo = new AnnuityValueObject();
				annVo.setAnnuity(annuity);
				results.add(annVo);
			}
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		return results;	

	}

	public AnnuityHolder findHolderById(AnnuityHolder annuityHolder) throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		checkConfigrableArgument(annuityHolder);
		AnnuityHolder results = null;
		try {
			results = (AnnuityHolder) getAnnuityService(annuityHolder).findHolderById(annuityHolder);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		RunningServerInfo.getInstance().setRunningServerInfo(results);
		return results;	

	}

	public List<AnnuityValueObject> findPayorAnnuities(Payor payor) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		checkConfigrableArgument(payor);
		List<AnnuityValueObject> results = new ArrayList<AnnuityValueObject>();
		try {
			List<IAnnuity> annuities = getAnnuityService(payor).findPayorAnnuities(payor);
			for (IAnnuity annuity : annuities) {
				RunningServerInfo.getInstance().setRunningServerInfo(annuity);
				AnnuityValueObject annVo = new AnnuityValueObject();
				annVo.setAnnuity(annuity);
				results.add(annVo);
			}
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		return results;	

	}

	public Payor findPayorById(Payor payor) throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		checkConfigrableArgument(payor);
		Payor results = null;
		try {
			results = (Payor) getAnnuityService(payor).findPayorById(payor);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		RunningServerInfo.getInstance().setRunningServerInfo(results);
		return results;		
	}

	public PayoutValueObject findPayoutById(PayoutValueObject payoutVO) throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		checkPayoutValueObject(payoutVO);
		Payout results = null;
		try {
			results = (Payout) getAnnuityService(payoutVO.getPayout()).findPayoutById(payoutVO.getPayout());
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		RunningServerInfo.getInstance().setRunningServerInfo(results);
		PayoutValueObject pvoResult = new PayoutValueObject();
		pvoResult.setPayout(results);
		return pvoResult;	
	}

	public Rider findRiderById(Rider rider) throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		checkConfigrableArgument(rider);
		Rider results = null;
		try {
			results = (Rider) getAnnuityService(rider).findRiderById(rider);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		RunningServerInfo.getInstance().setRunningServerInfo(results);
		return results;	
	}

	public AnnuityValueObject updateAnnuity(AnnuityValueObject annuityVO) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		checkAnnuityValueObject(annuityVO);
		IAnnuity annuity = annuityVO.getAnnuity();
		//Logger logger = getLogger(annuity);		
		try {
			annuity = (Annuity) getAnnuityService(annuity).updateAnnuity(annuity);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);			
		}
		RunningServerInfo.getInstance().setRunningServerInfo(annuity);
		AnnuityValueObject result = new AnnuityValueObject();
		result.setAnnuity(annuity);		
		return result;		
	}

	public AnnuityHolder updateAnnuityHolder(AnnuityHolder annHolder) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		checkConfigrableArgument(annHolder);
		AnnuityHolder results = null;
		try {
			results = (AnnuityHolder) getAnnuityService(annHolder).updateAnnuityHolder(annHolder);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		RunningServerInfo.getInstance().setRunningServerInfo(results);
		return results;
	}

	public Contact updateContact(Contact contact) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		checkConfigrableArgument(contact);
		Contact results = null;
		try {
			results = (Contact) getAnnuityService(contact).updateContact(contact);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		RunningServerInfo.getInstance().setRunningServerInfo(results);
		return results;
	}

	public Payor updatePayor(Payor payor) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		checkConfigrableArgument(payor);
		Payor results = null;
		try {
			results = (Payor) getAnnuityService(payor).updatePayor(payor);
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		RunningServerInfo.getInstance().setRunningServerInfo(results);
		return results;
	}

	public PayoutValueObject updatePayout(PayoutValueObject payoutVO) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		checkPayoutValueObject(payoutVO);
		Payout results = null;
		try {
			results = (Payout) getAnnuityService(payoutVO.getPayout()).updatePayout(payoutVO.getPayout());
		} catch (ServerInternalErrorException e) {
			convertServerException(e);
		}
		RunningServerInfo.getInstance().setRunningServerInfo(results);
		PayoutValueObject pvoResult = new PayoutValueObject();
		pvoResult.setPayout(results);
		return pvoResult;
	}
	
	private void checkConfigrableArgument(Configrable<String, String> configrable) throws InvalidArgumentException{		
		if (configrable == null) {
			throw new InvalidArgumentException("The input parameter is invalid.  It null.");
		}
		if (configrable.getConfiguration() == null){
			throw new InvalidArgumentException("The input parameter has null configuration. It is invalid.");
		}
	}
	
	private void checkAnnuityValueObject(AnnuityValueObject annuityVO) throws InvalidArgumentException {
		if (annuityVO == null) {
			throw new InvalidArgumentException("The AnnuityValueObject parameter is invalid.  It is null");
		}
		if (annuityVO.getAnnuity() == null) {
			throw new InvalidArgumentException("The AnnuityValueObject contains no valid Annuity");
		}
		checkConfigrableArgument(annuityVO.getAnnuity());
	}
	
	private void checkPayoutValueObject(PayoutValueObject payoutVO) throws InvalidArgumentException {
		if (payoutVO == null) {
			throw new InvalidArgumentException("The PayoutValueObject parameter is invalid.  It is null");
		}
		if (payoutVO.getPayout() == null) {
			throw new InvalidArgumentException("The PayoutValueObject contains no valid Payout");
		}
		checkConfigrableArgument(payoutVO.getPayout());
	}		
		

	private AcmeLogger getLogger(Configrable<String, String> configrable) {
		return AnnuityLoggerFactory.getAcmeServerLogger(configrable, getClass().getName());
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
	
	protected IAnnuityService getAnnuityService(Configrable<String, String> configrable) throws ServerBusinessModuleException {		
		try {
			//return EJB3ServiceLookup.getAnnuityEJB3Service(null, null,configrable, getLogger(configrable));
			return new AnnuityBusinessServiceLookup().getAnnuityAnnuityService(configrable);
		} catch (Exception e) {
			AcmeLogger logger = getLogger(configrable);
			logger.info("Annuity Service lookup returned the error:" +e.getMessage());			
			throw new ServerBusinessModuleException(e.getMessage(), e);
		} 
	}

}

