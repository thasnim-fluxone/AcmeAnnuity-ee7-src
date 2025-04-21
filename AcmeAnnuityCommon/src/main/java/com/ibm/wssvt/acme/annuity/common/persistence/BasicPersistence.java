package com.ibm.wssvt.acme.annuity.common.persistence;

import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.common.bean.Configrable;

public interface BasicPersistence<O, K, V>{
	
	O createObject(O object) 
		throws ServerPersistenceModuleException, InvalidArgumentException, EntityAlreadyExistsException;
	O updateObject(O object) 
		throws ServerPersistenceModuleException, InvalidArgumentException;
	<C> void deleteObject(Class<C> object, Object id, Configrable<K, V> configrable) 
		throws ServerPersistenceModuleException , InvalidArgumentException;	
	<C>C readObject(Class<C> entClass, Object entId, Configrable<K, V> configrable) 
		throws EntityNotFoundException, ServerPersistenceModuleException, InvalidArgumentException;
}