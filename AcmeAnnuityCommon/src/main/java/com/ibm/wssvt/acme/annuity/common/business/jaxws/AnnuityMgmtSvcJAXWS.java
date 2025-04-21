package com.ibm.wssvt.acme.annuity.common.business.jaxws;



import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityValueObject;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Contact;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Payor;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.PayoutValueObject;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Rider;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerBusinessModuleException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;


public interface AnnuityMgmtSvcJAXWS {
	
	public AnnuityValueObject createAnnuity(AnnuityValueObject annuityVO) 
		throws ServerBusinessModuleException, ServerPersistenceModuleException, EntityAlreadyExistsException, InvalidArgumentException;
	public AnnuityValueObject updateAnnuity(AnnuityValueObject annuityVO) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;
	public void deleteAnnuity(AnnuityValueObject annuityVO) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;	
	
	public AnnuityHolder createAnnuityHolder(AnnuityHolder annHolder) 
		throws ServerBusinessModuleException, ServerPersistenceModuleException, EntityAlreadyExistsException, InvalidArgumentException;
	public AnnuityHolder updateAnnuityHolder(AnnuityHolder annHolder)
		throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;
	public void deleteAnnuityHolder(AnnuityHolder annHolder)
		throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;

	
	public Payor createPayor(Payor payor)
		throws ServerBusinessModuleException, ServerPersistenceModuleException, EntityAlreadyExistsException, InvalidArgumentException;
	public Payor updatePayor(Payor payor)
		throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;
	public void deletePayor(Payor payor) 
		throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;


	public Contact createContact(Contact contact)
		throws ServerBusinessModuleException, ServerPersistenceModuleException, EntityAlreadyExistsException, InvalidArgumentException;
	public Contact updateContact(Contact contact)
		throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;
	public void deleteContact(Contact contact) 
		throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;
	
	public PayoutValueObject createPayout(PayoutValueObject payoutVO)
		throws ServerBusinessModuleException, ServerPersistenceModuleException, EntityAlreadyExistsException, InvalidArgumentException;	
	public PayoutValueObject updatePayout(PayoutValueObject payoutVO)
		throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;
	public void deletePayout(PayoutValueObject payoutVO) 
		throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;
	
	public void deleteRider(Rider rider) 
		throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;
	
	
	public AnnuityValueObject findAnnuityById(AnnuityValueObject annuityVO) 
		throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;		
	public AnnuityHolder findHolderById(AnnuityHolder annuityHolder)
		throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;
	public Contact findContactById(Contact contact)
		throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;
	
	public List<AnnuityValueObject> findHolderAnnuities(AnnuityHolder annuityHolder)
		throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;
	public AnnuityHolder findAnnuityHolder(AnnuityValueObject annuityVO) 	
		throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;
	
	public List<AnnuityValueObject> findPayorAnnuities(Payor payor)
		throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;
	
	public Payor findPayorById(Payor payor)
		throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;
	
	public PayoutValueObject findPayoutById(PayoutValueObject payoutVO)
		throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;
	
	public Rider findRiderById(Rider rider)
		throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException;
	
}
