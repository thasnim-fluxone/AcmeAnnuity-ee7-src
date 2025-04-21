package com.ibm.wssvt.acme.annuity.common.persistence;

import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
//import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
//import com.ibm.wssvt.acme.common.bean.Configrable;

/**
 * $Rev: 483 $
 * $Date: 2007-07-06 03:22:50 -0500 (Fri, 06 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
// Template O for Object
// Template C for Class
// Template K and V are the Configrable Pram and Value
public interface AnnuityPersistence<O, K, V> extends BasicPersistence<O, K, V>{
/*	
	O createObject(O object) 
		throws ServerPersistenceModuleException, InvalidArgumentException, EntityAlreadyExistsException;
	O updateObject(O object) 
		throws ServerPersistenceModuleException, InvalidArgumentException;
	<C> void deleteObject(Class<C> object, Object id, Configrable<K, V> configrable) 
		throws ServerPersistenceModuleException , InvalidArgumentException;	
	<C>C readObject(Class<C> entClass, Object entId, Configrable<K, V> configrable) 
		throws EntityNotFoundException, ServerPersistenceModuleException, InvalidArgumentException;
*/	
	List<IAnnuity> getHolderAnnuities(IAnnuityHolder holder) 
		throws InvalidArgumentException, ServerPersistenceModuleException;
	IAnnuityHolder getAnnuityHolder(IAnnuity annuity)
		throws EntityNotFoundException, ServerPersistenceModuleException, InvalidArgumentException;
	List<IAnnuity> getPayorAnnuities(IPayor payor) throws InvalidArgumentException, ServerPersistenceModuleException;
		
}
