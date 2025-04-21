package com.ibm.wssvt.acme.annuity.persistence.impl.ejbjpa;

import java.util.List;
import java.util.logging.Level;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
//import javax.interceptor.Interceptors;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPersisteble;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.annuity.common.persistence.AnnuityPersistence;
import com.ibm.wssvt.acme.annuity.common.persistence.JPAPersistenceUnitName;
import com.ibm.wssvt.acme.annuity.common.persistence.PersistenceUnitNameHelper;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityLoggerFactory;
//import com.ibm.wssvt.acme.annuity.common.util.EJBInputParamExaminerInterceptor;
//import com.ibm.wssvt.acme.annuity.common.util.EJBLoggingInterceptor;
//import com.ibm.wssvt.acme.annuity.common.util.RunningServerInfo;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.exception.ExceptionFormatter;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/**
 * $Rev: 705 $
 * $Date: 2007-08-23 16:53:38 -0500 (Thu, 23 Aug 2007) $
 * $Author: gli $
 * $LastChangedBy: gli $
 */

@Stateless
@Local(PersistenceJPALocal.class)
//@Interceptors({EJBInputParamExaminerInterceptor.class, EJBLoggingInterceptor.class})
public class PersistenceJPASessionBean implements AnnuityPersistence<IPersisteble<?>, String, String>{
	@PersistenceUnit(unitName="AnnuityDSJPAOnly")
	private EntityManagerFactory entityManagerJPAFactory;
	@PersistenceUnit(unitName="AnnuityDSJAXBJPA")
	private EntityManagerFactory entityManagerJAXBJPAFactory;
	
	@Resource SessionContext ejbContext;
	
	
	public <T>T readObject(Class<T> entClass, Object entId, Configrable<String, String> configrable) 
		throws EntityNotFoundException, ServerPersistenceModuleException, InvalidArgumentException {				
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(configrable, getClass().getName());
		logger.fine("entering read object method");
		logger.fine("Class: " + entClass);
		logger.finer("trying to find the object: " + entId);
		logger.finest("configrable: " +  configrable);
		
		T entity = null;		
		EntityManager em = null;
		try {				
			logger.fine("ReadObject= " +entId.toString());
			logger.fine("find annuity - id - " + entId);
			em = getEntityManager(configrable);			
			entity = em.find(entClass, entId);
			if (entity == null) {
				logger.fine("null return from find persisteble ");
				throw new EntityNotFoundException("Failed to find the object with " 
					+ entClass + " with id:" + entId + " Returning an EntityNotFoundException"  
					+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()"
					+ logger.getAllLogs());
			}
		}catch (IllegalStateException e) {
			String msg = "JPA Returned IllegalStateException.  Error: " 
				+ ExceptionFormatter.deepFormatToString(e) 
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();						
			logger.log(Level.SEVERE, msg, e);
			throw new ServerPersistenceModuleException(msg);			
		}catch (IllegalArgumentException e) {			
			String msg = "JPA Returned an IllegalArgumentException, is this a JPA Object? Class is: " + entClass 
				+ " Error: " 	+ ExceptionFormatter.deepFormatToString(e)			
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			throw new InvalidArgumentException(msg); 
		}catch (EntityNotFoundException e) {
			logger.fine("JPA returned EntityNotFoundException. Returning the same error - Error: " 
					+ ExceptionFormatter.deepFormatToString(e)
					+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
					+ ".  Internal Trace: " + logger.getAllLogs());			
			throw e;
		}catch (PersistenceException e) {
			String msg = "Persistence Module returned an error - This is a generic error, see error message for details. Error: " 
				+ ExceptionFormatter.deepFormatToString(e)  
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.WARNING, msg, e);
			throw new ServerPersistenceModuleException(msg);
		}catch (Throwable t){			
			String msg = "JPA Returned a Throwable or a non-declered exception. Error: " + 
				ExceptionFormatter.deepFormatToString(t) 
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, t);
			throw new ServerPersistenceModuleException(msg);
		} finally {
			if (em != null) {
				em.clear();
				em.close();
			}
		}
		
		return entity;					
	}

	public IPersisteble<?> createObject(IPersisteble<?> jpaPersisteble) 
		throws EntityAlreadyExistsException, ServerPersistenceModuleException, InvalidArgumentException{
		EntityManager em = null;
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(jpaPersisteble, getClass().getName());
		logger.fine("entering craeteObject()" );
		try {								
			em = getEntityManager(jpaPersisteble);
			logger.fine("createEntityManager jpaPersisteble is = " +jpaPersisteble.toString());
			logger.fine("jpaPersisteble class and Id are: " + jpaPersisteble.getClass().getName() + " id: " + jpaPersisteble.getId());
			em.persist(jpaPersisteble);
			logger.fine("after em.persist");
			em.joinTransaction();			
			logger.fine("after em.joinTransaction");
			em.flush();					
			logger.fine("after em.flush");
			return jpaPersisteble;
		}catch (EntityExistsException e) {
			String msg = "Entity already exists.  Error: " + ExceptionFormatter.deepFormatToString(e)
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.fine(msg);
			ejbContext.setRollbackOnly();
			throw new EntityAlreadyExistsException("JPA Returned EntityAlreadyExists Exception. Error: " + msg);									
		}catch (IllegalStateException e) {
			String msg = "JPA Returned IllegalStateException.  Error: " 
				+ ExceptionFormatter.deepFormatToString(e) 
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			ejbContext.setRollbackOnly();			
			throw new ServerPersistenceModuleException(msg);
		}catch (IllegalArgumentException e) {
			String msg = "JPA Returned an IllegalArgumentException, is this a JPA Object? Class is: " + jpaPersisteble.getClass().getName() 
				+ " Error: " 	+ ExceptionFormatter.deepFormatToString(e)			
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			ejbContext.setRollbackOnly();
			throw new InvalidArgumentException(msg);
		}catch (TransactionRequiredException e){
			String msg = "JPA Returned a TransactionRequiredException...Business Logic was expecting a transaction.  Error: " 
				+ ExceptionFormatter.deepFormatToString(e) 
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			ejbContext.setRollbackOnly();
		throw new ServerPersistenceModuleException(msg);
		}catch (PersistenceException e) {
			String msg = "Persistence Module returned an error - This is a generic error, see error message for details. Error: " 
				+ ExceptionFormatter.deepFormatToString(e)  
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.WARNING, msg, e);
			throw new ServerPersistenceModuleException(msg);
		}catch (Throwable t){
			String msg = "JPA Returned a Throwable or a non-declered exception. Error: " 
				+ ExceptionFormatter.deepFormatToString(t) 
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, t);
			ejbContext.setRollbackOnly();
			throw new ServerPersistenceModuleException(msg);
		} finally {
			if (em != null) {
				em.clear();
				em.close();
			}
		}		
	}

	public IPersisteble<?> updateObject(IPersisteble<?> jpaPersisteble) 
		throws ServerPersistenceModuleException, InvalidArgumentException {
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(jpaPersisteble, getClass().getName());
		EntityManager em = null;
		try {
			logger.fine("UpdateObject jpaPersisteble info: toString:" 
					+ jpaPersisteble.toString() + " class: " 
					+ jpaPersisteble.getClass().getName() + " id: " + jpaPersisteble.getId());
			em = getEntityManager(jpaPersisteble);			
			jpaPersisteble = em.merge(jpaPersisteble);
			logger.fine("after merge");
			em.joinTransaction();
			logger.fine("after joinTransaction");
			em.flush();
			logger.fine("after flush");
			return jpaPersisteble;
		}catch (IllegalStateException e) {
			String msg = "JPA Returned IllegalStateException.  Error: " 
				+ ExceptionFormatter.deepFormatToString(e) 
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();						
			logger.log(Level.SEVERE, msg, e);
			ejbContext.setRollbackOnly();			
			throw new ServerPersistenceModuleException("Illegal State Exception. Check Business Logic." + msg);
		}catch (IllegalArgumentException e) {
			String msg = "JPA Returned an Illegal Argument Exception, is this a JPA Object? Class is: " + jpaPersisteble.getClass().getName() 
				+ " Error: " 	+ ExceptionFormatter.deepFormatToString(e)			
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			ejbContext.setRollbackOnly();
			throw new InvalidArgumentException("Object is not a JPA Object. Check Code Logic. Error: " + msg);
		}catch (TransactionRequiredException e){
			String msg = "JPA Returned a TransactionRequiredException...Business Logic was expecting a transaction.  Error: " 
				+ ExceptionFormatter.deepFormatToString(e) 
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			ejbContext.setRollbackOnly();			
			throw new ServerPersistenceModuleException(msg);
		}catch (PersistenceException e) {
			String msg = "Persistence Module returned an error - This is a generic error, see error message for details. Error: " 
				+ ExceptionFormatter.deepFormatToString(e)  
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.WARNING, msg, e);			
			ejbContext.setRollbackOnly();
			throw new ServerPersistenceModuleException(msg);			
		}catch (Throwable t){
			String msg = "JPA Returned a Throwable or a non-declered exception. Error: " 
				+ ExceptionFormatter.deepFormatToString(t) 
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, t);
			ejbContext.setRollbackOnly();
			throw new ServerPersistenceModuleException(msg);
		} finally {
			if (em != null) {
				em.clear();
				em.close();
			}
		}
	}

	public <C> void deleteObject(Class<C> deleteObject, Object id, Configrable<String, String> configrable) 
		throws ServerPersistenceModuleException, InvalidArgumentException {
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(configrable, getClass().getName());
		EntityManager em = null;
		try {
			logger.fine("DeleteObject= class: " + deleteObject.getName() + " id: " + id);
			em = getEntityManager(configrable);
			logger.fine("find the object to delete");
			Object o = em.find(deleteObject, id);
			logger.fine("found object!");
			em.remove(o);
			logger.fine("after em.remove");
			em.joinTransaction();
			logger.fine("after em.join txn");
			em.flush();
			logger.fine("after flush");
		}catch (IllegalStateException e) {
			String msg = "JPA Returned IllegalStateException.  Error: " 
				+ ExceptionFormatter.deepFormatToString(e) 
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			ejbContext.setRollbackOnly();
			throw new ServerPersistenceModuleException(msg);
		}catch (IllegalArgumentException e) {
			String msg = "JPA Returned an IllegalArgumentException, is this a JPA Object? Class is: " + deleteObject.getClass().getName() 
				+ " Error: " 	+ ExceptionFormatter.deepFormatToString(e)			
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			ejbContext.setRollbackOnly();
			throw new InvalidArgumentException(msg);
		}catch (TransactionRequiredException e){
			String msg = "JPA Returned a TransactionRequiredException...Business Logic was expecting a transaction.  Error: " 
				+ ExceptionFormatter.deepFormatToString(e) 
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			ejbContext.setRollbackOnly();
			throw new ServerPersistenceModuleException(msg);
		}catch (PersistenceException e) {
			String msg = "Persistence Module returned an error - This is a generic error, see error message for details. Error: " 
				+ ExceptionFormatter.deepFormatToString(e)  
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.WARNING, msg, e);
			ejbContext.setRollbackOnly();
			throw new ServerPersistenceModuleException(msg);
		}catch (Throwable t){
			String msg = "JPA Returned a Throwable or a non-declered exception. Error: " 
				+ ExceptionFormatter.deepFormatToString(t) 
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, t);
			ejbContext.setRollbackOnly();
			throw new ServerPersistenceModuleException(msg);
		} finally {
			if (em != null) {
				em.clear();
				em.close();
			}
		}
		
	}

	
	@SuppressWarnings("unchecked")
	public List<IAnnuity> getHolderAnnuities(IAnnuityHolder holder) throws InvalidArgumentException, ServerPersistenceModuleException {
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(holder, getClass().getName());		
		
		if (holder == null) {
			String msg = "Invalid annuity holder - its null, returning Exception"
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" + logger.getAllLogs();
			logger.fine(msg);				
			throw new InvalidArgumentException("Invalid Annuity Holder argument");
		}	
		logger.fine("getHolderAnnuities. Holder info: id: " + holder.getId());
		
		EntityManager em = null;
		try{		 						
			em = getEntityManager(holder);
			logger.fine("get the query");
			Query query = em.createNamedQuery("GetHolderAnnuities");
			logger.fine("after get querey: " + query);
			query.setParameter("holderId", holder.getId());		
			List<IAnnuity> annList = query.getResultList();
			logger.fine("List that was returned " + annList);
			return annList;
		}catch (PersistenceException e) {
			String msg = "Persistence Module returned an error - This is a generic error, see error message for details. Error: " 
				+ ExceptionFormatter.deepFormatToString(e)  
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.WARNING, msg, e);
			ejbContext.setRollbackOnly();
			throw new ServerPersistenceModuleException(msg);
		}catch (Throwable t) {
			String msg = "JPA Returned a Throwable or a non-declered exception. Error: " 
				+ ExceptionFormatter.deepFormatToString(t) 
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			ejbContext.setRollbackOnly();
			logger.log(Level.SEVERE, msg, t);
			throw new ServerPersistenceModuleException(msg);
		} finally {
			if (em != null) {
				em.clear();
				em.close();
			}
		}
		
	}

	public IAnnuityHolder getAnnuityHolder(IAnnuity annuity) throws EntityNotFoundException, ServerPersistenceModuleException, InvalidArgumentException {
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(annuity, getClass().getName());		
		
		logger.fine("getAnnuityHolder");
		if (annuity== null) {
			logger.fine("Invalid annuity - its null, returning Exception" 
					+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" + logger.getAllLogs());
			throw new InvalidArgumentException("Invalid Annuity argument");
		}		
		try{
			PersistenceUnitNameHelper.setPersistenceUnitName(annuity, null);
			String persistenceUnitName = annuity.getConfiguration().getParameterValue("persistenceUnitName");
			logger.fine("persistenceUnitName: " + persistenceUnitName);
			if (JPAPersistenceUnitName.AnnuityDSJAXBJPA.toString().equals(persistenceUnitName)) {
				logger.fine("this is a jaxbjpa object");
				return readObject(com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityHolder.class, 
						annuity.getAnnuityHolderId(), annuity);				
			}else if (JPAPersistenceUnitName.AnnuityDSJPAOnly.toString().equals(persistenceUnitName)) {
				logger.fine("this is a jpa object");
				return readObject(com.ibm.wssvt.acme.annuity.common.bean.jpa.AnnuityHolder.class, annuity.getAnnuityHolderId(), annuity);	
			}else{
				String msg = "The parameter persistenceUnitName with value: " + persistenceUnitName
					+ " is not valid in this context.  Check your configuration settings for persistenceType and persistenceUnitName"
					+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
					+ ".  Internal Trace: " + logger.getAllLogs();
				logger.log(Level.SEVERE, msg);
				throw new InvalidArgumentException(msg);			
			}				
								
		}catch (Throwable t) {
			String msg = "JPA Returned a Throwable or a non-declered exception. Error: " 
				+ ExceptionFormatter.deepFormatToString(t) 
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, t);
			throw new ServerPersistenceModuleException(msg);
		}
	}

	@SuppressWarnings("unchecked")
	public List<IAnnuity> getPayorAnnuities(IPayor payor) 
		throws InvalidArgumentException, ServerPersistenceModuleException {				
		if (payor == null) {		
			throw new InvalidArgumentException("Invalid Payor argument -its null."
					+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()");
		}
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(payor, getClass().getName());		
		try{
			logger.fine("getPayorAnnuities: payor id: " + payor.getId());
			EntityManager em = getEntityManager(payor);					
			logger.fine("got em, now get query");
			Query query = em.createNamedQuery("GetPayorAnnuities");
			logger.fine("got query: " + query);
			query.setParameter(1, payor.getId());
			
			List<IAnnuity> annuities = (List<IAnnuity>) query.getResultList();
			logger.fine("return annuities list: " + annuities);
			logger.fine(" loading eager relastionships - ann->rider");
			//load annuity eager relationships.			
			for (IAnnuity annuity : annuities) {				
				annuity.getRiders();
			}			
			return annuities;
		}catch (PersistenceException e) {
			String msg = "Persistence Module returned an error - This is a generic error, see error message for details. Error: " 
				+ ExceptionFormatter.deepFormatToString(e)  
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.WARNING, msg, e);
			throw new ServerPersistenceModuleException(msg);
		}catch (Throwable t) {
			String msg = "JPA Returned a Throwable or a non-declered exception. Error: " 
				+ ExceptionFormatter.deepFormatToString(t) 
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, t);
			throw new ServerPersistenceModuleException(msg);
		}		
	}	
	
	private EntityManager getEntityManager(Configrable<String, String> configrable) throws InvalidArgumentException{
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(configrable, getClass().getName());		
		
		PersistenceUnitNameHelper.setPersistenceUnitName(configrable, null);
		String persistenceUnitName = configrable.getConfiguration().getParameterValue("persistenceUnitName");
		logger.fine("persistenceUnitName: " + persistenceUnitName);
		if (JPAPersistenceUnitName.AnnuityDSJAXBJPA.toString().equals(persistenceUnitName)) {
			logger.fine("JAXBJPA - PU");
			return entityManagerJAXBJPAFactory.createEntityManager();
		}else if (JPAPersistenceUnitName.AnnuityDSJPAOnly.toString().equals(persistenceUnitName)) {
			logger.fine("JPA - PU");
			return entityManagerJPAFactory.createEntityManager();	
		}else{
			String msg = "The parameter persistenceUnitName with value: " + persistenceUnitName
				+ " is not valid in this context.  Check your configuration settings for persistenceType and persistenceUnitName"
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" 
				+ ".  Internal Trace: " + logger.getAllLogs();
			logger.log(Level.SEVERE, msg);
			throw new InvalidArgumentException(msg);
		
		}				
	}
}
