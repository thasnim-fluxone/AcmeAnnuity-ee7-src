package com.ibm.wssvt.acme.annuity.common.persistence.impl.jpasa;

import java.util.logging.Level;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;

import com.ibm.wssvt.acme.annuity.common.bean.IPersisteble;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.annuity.common.persistence.BasicPersistence;
import com.ibm.wssvt.acme.annuity.common.persistence.JPAPersistenceUnitName;
import com.ibm.wssvt.acme.annuity.common.persistence.PersistenceUnitNameHelper;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.exception.ExceptionFormatter;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
/**
 * $Rev: 744 $
 * $Date: 2007-09-07 09:51:07 -0500 (Fri, 07 Sep 2007) $
 * $Author: rhaque $
 * $LastChangedBy: rhaque $
 */
public abstract class BasicPersistenceJPASAImpl<T> implements BasicPersistence<IPersisteble<T>, String, String>{
	
	private AcmeLogger logger ; //Logger.getLogger(PersistenceJPASAImpl.class.getName());
	private static EntityManagerFactory cachedFactory = null;
	
	public BasicPersistenceJPASAImpl(AcmeLogger logger){
		this.logger = logger;
	}
	
	public IPersisteble<T> createObject(IPersisteble<T> persistebleObject) throws ServerPersistenceModuleException, EntityAlreadyExistsException, InvalidArgumentException {
		EntityManager em = null;
		try{						
			logger.fine("Entering the create Object - jpa-SA -getting factory");
			logger.fine("Create Object's id is " + persistebleObject.getId());
			cachedFactory = getEntityManagerFactory(persistebleObject); 			
			logger.fine("- jpa-SA -getting em");
			em = cachedFactory.createEntityManager();
			logger.fine("- begin tx");
			em.getTransaction().begin();
			logger.fine("- persist");
			em.persist(persistebleObject);
			logger.fine("- commit");
			em.getTransaction().commit();	
			return persistebleObject;
		}catch (EntityExistsException e) {
			rollBackTransaction(em);
			String msg = "Entity already exists.  Error: " + ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.info(msg);			
			throw new EntityAlreadyExistsException("Attempt to save an object with same primary key. " + msg);									
		}catch (IllegalStateException e) {
			rollBackTransaction(em);
			String msg = "Illegal State Exception.  Check the code logic.  Error: " 
				+ ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			throw new ServerPersistenceModuleException("Illegal State Exception. Check Business Logic." + msg);
		}catch (IllegalArgumentException e) {
			rollBackTransaction(em);
			String msg = "Object is not a JPA Object.  Error: " + ExceptionFormatter.deepFormatToString(e)  + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			throw new InvalidArgumentException("Object is not a JPA Object. Check Code Logic. Error: " + msg);
		}catch (TransactionRequiredException e){
			rollBackTransaction(em);
			String msg = "Business Logic was expecting a transaction.  Error: " 
				+ ExceptionFormatter.deepFormatToString(e)  + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			throw new ServerPersistenceModuleException("Business Logic was expecting a transaction. Check Business Logic."
					+ ".  Error: " + msg);
		}catch (PersistenceException e) {
			rollBackTransaction(em);
			String msg = "Persistence Module returned an error - see error message for details. Error: " 
				+ ExceptionFormatter.deepFormatToString(e)  + logger.getAllLogs();
			logger.log(Level.WARNING, msg, e);
			throw new ServerPersistenceModuleException(msg);
		}catch (Throwable t){
			rollBackTransaction(em);
			String msg= "Non-decleraed exeception.  Error: " + ExceptionFormatter.deepFormatToString(t)  + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, t);
			throw new ServerPersistenceModuleException(msg);
		}finally {
			logger.fine("- close em");
			if (em != null){
				em.close();
			}			
		}
		
	}	

	public <C> void deleteObject(Class<C> object, Object id, Configrable<String, String> configrable) throws ServerPersistenceModuleException, InvalidArgumentException {				
		EntityManager em = null;
		try{
			logger.fine("Entering the delete Object - jpa-SA -getting factory");
			cachedFactory = getEntityManagerFactory(configrable); 
			
			logger.fine("- jpa-SA -getting em");
			em = cachedFactory.createEntityManager();
			logger.fine("- begin tx");
			em.getTransaction().begin();
			logger.fine("- find ");
			C obj = em.find(object, id);
			logger.fine("- delete ");
			em.remove(obj);			
			logger.fine("- commit");
			em.getTransaction().commit();		
		}catch (IllegalStateException e) {
			rollBackTransaction(em);
			String msg = "Illegal State Exception.  Check the code logic.  Error: " 
				+ ExceptionFormatter.deepFormatToString(e)  + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			throw new ServerPersistenceModuleException("Illegal State Exception. Check Business Logic." + msg);
		}catch (IllegalArgumentException e) {
			rollBackTransaction(em);
			String msg = "Object is not a JPA Object.  Error: " + ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			throw new InvalidArgumentException("Object is not a JPA Object. Check Code Logic. Error: " + msg);
		}catch (TransactionRequiredException e){
			rollBackTransaction(em);
			String msg = "Business Logic was expecting a transaction.  Error: " 
				+ ExceptionFormatter.deepFormatToString(e)  + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			throw new ServerPersistenceModuleException("Business Logic was expecting a transaction. Check Business Logic."
					+ ".  Error: " + msg);
		}catch (PersistenceException e) {
			rollBackTransaction(em);
			String msg = "Persistence Module returned an error - see error message for details. Error: " 
				+ ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.log(Level.WARNING, msg, e);
			throw new ServerPersistenceModuleException(msg);

		}catch (Throwable t){
			rollBackTransaction(em);
			String msg= "Non-decleraed exeception.  Error: " + ExceptionFormatter.deepFormatToString(t) + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, t);
			throw new ServerPersistenceModuleException(msg);
		}finally {
			logger.fine("- close em");
			if (em != null){
				em.close();
			}	
		}
		
	}

	public <C> C readObject(Class<C> entClass, Object entId, Configrable<String, String> configrable) throws EntityNotFoundException, ServerPersistenceModuleException, InvalidArgumentException {
		EntityManager em = null;
		C persistable = null;
		try{
			logger.fine("Entering the read Object - jpa-SA -getting factory");
			cachedFactory = getEntityManagerFactory(configrable);
			logger.fine("- jpa-SA -getting em");
			em = cachedFactory.createEntityManager();
			persistable = em.find(entClass, entId);
			if (persistable == null) {
				logger.fine("read object returned null.  the object does not exist.  Class: " + 
						entClass + " id: " + entId + " returning an EntityNotFoundException");
				throw new EntityNotFoundException("Failed to find the object with " 
						+ entClass + " with id:" + entId + logger.getAllLogs());

			}
		}catch (IllegalStateException e) {
			String msg = "Illegal State Closed.  Error: " + ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();			
			logger.log(Level.SEVERE, msg, e);
			throw new ServerPersistenceModuleException(msg);			
		}catch (IllegalArgumentException e) {			
			String msg = "Object is not a JPA Object.  Check the code logic. Error: " 
				+ ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			throw new InvalidArgumentException(msg); 
		}catch (EntityNotFoundException e) {
			throw e;
		}catch (PersistenceException e) {
			String msg = "Persistence Module returned an error - see error message for details. Error: " 
				+ ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.log(Level.WARNING, msg, e);
			throw new ServerPersistenceModuleException(msg);
		}catch (Throwable t){			
			String msg = "Encountered a non-declered exception. Error: " + 
				ExceptionFormatter.deepFormatToString(t) + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, t);
			throw new ServerPersistenceModuleException(msg);
		}finally {
			logger.fine("- close em");
			if (em != null){
				em.close();
			}
		}
		return persistable;
	}

	public IPersisteble<T> updateObject(IPersisteble<T> persisteble) throws ServerPersistenceModuleException, InvalidArgumentException {
		EntityManager em = null;
		try{
			logger.fine("Entering the update Object - jpa-SA -getting factory - new");
			cachedFactory = getEntityManagerFactory(persisteble);
			logger.fine("- jpa-SA -getting em");
			em = cachedFactory.createEntityManager();
			logger.fine("- begin tx");
			em.getTransaction().begin();
			logger.fine("- delete ");
			persisteble = em.merge(persisteble);
			logger.fine("- commit");
			em.getTransaction().commit();
			return persisteble;
		}catch (IllegalStateException e) {
			rollBackTransaction(em);
			String msg = "Illegal State Exception.  Check the code logic.  Error: " 
				+ ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			throw new ServerPersistenceModuleException("Illegal State Exception. Check Business Logic." + msg);
		}catch (IllegalArgumentException e) {
			rollBackTransaction(em);
			String msg = "Object is not a JPA Object.  Error: " + ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			throw new InvalidArgumentException("Object is not a JPA Object. Check Code Logic. Error: " + msg);
		}catch (TransactionRequiredException e){
			rollBackTransaction(em);
			String msg = "Business Logic was expecting a transaction.  Error: " 
				+ ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			throw new ServerPersistenceModuleException("Business Logic was expecting a transaction. Check Business Logic."
					+ ".  Error: " + msg);
		}catch (PersistenceException e) {
			rollBackTransaction(em);
			String msg = "Persistence Module returned an error - see error message for details. Error: " 
				+ ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.log(Level.WARNING, msg, e);
			throw new ServerPersistenceModuleException(msg);
		}catch (Throwable t){
			rollBackTransaction(em);
			String msg= "Non-decleraed exeception.  Error: " + ExceptionFormatter.deepFormatToString(t) + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, t);
			throw new ServerPersistenceModuleException(msg);
		}finally {
			logger.fine("- close em");
			if (em != null){
				em.close();
			}			
		}
		
	}

/*	
	@SuppressWarnings("unchecked")
	public List<IAnnuity> getHolderAnnuities(IAnnuityHolder holder) throws InvalidArgumentException, ServerPersistenceModuleException {
		logger.fine("getHolderAnnuities");
		if (holder == null) {
			logger.fine("Invalid annuity holder - its null, returning Exception" + logger.getAllLogs());
			throw new InvalidArgumentException("Invalid Annuity Holder argument");
		}
		EntityManagerFactory factory = null;
		EntityManager em = null;
		try{		 						
			factory = getEntityManagerFactory(holder); 					
			em = factory.createEntityManager();
			Query query = em.createNamedQuery("GetHolderAnnuities");
			query.setParameter("holderId", holder.getId());
			List l = (List<IAnnuity>) query.getResultList();
			return l;
		}catch (PersistenceException e) {
			String msg = "Persistence Module returned an error - see error message for details. Error: " 
				+ ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.log(Level.WARNING, msg, e);
			throw new ServerPersistenceModuleException(msg);
		}catch (Throwable t) {
			String msg = "Encountered a non-declered exception. Error: " + 
			ExceptionFormatter.deepFormatToString(t) + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, t);
			throw new ServerPersistenceModuleException(msg);
		}finally {
			logger.fine("- close em");
			if (em != null){
				em.close();
			}	
		}
		
	}
	
	public IAnnuityHolder getAnnuityHolder(IAnnuity annuity) throws EntityNotFoundException, ServerPersistenceModuleException, InvalidArgumentException {
		logger.fine("getAnnuityHolder");
		if (annuity== null) {
			logger.fine("Invalid annuity - its null, returning Exception" + logger.getAllLogs());
			throw new InvalidArgumentException("Invalid Annuity argument");
		}
		try{
			PersistenceUnitNameHelper.setPersistenceUnitName(annuity, null);			
			String persistenceUnitName = annuity.getConfiguration().getParameterValue("persistenceUnitName");
			logger.fine("persistenceUnitName: " + persistenceUnitName);
			if (JPAPersistenceUnitName.AnnuitySAJAXBJPA.toString().equals(persistenceUnitName)) {
				return readObject(com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityHolder.class, 
						annuity.getAnnuityHolderId(), annuity);
			}else if (JPAPersistenceUnitName.AnnuitySAJPAOnly.toString().equals(persistenceUnitName)) {
				return readObject(com.ibm.wssvt.acme.annuity.common.bean.jpa.AnnuityHolder.class, annuity.getAnnuityHolderId(), annuity);
			}else{
				throw new InvalidArgumentException("The parameter persistenceUnitName was not set.  Please provide a valid value! Failed to getAnnuityHolder"
						+ logger.getAllLogs());
			}
						
						
		}catch (Throwable e){
			String msg = "Encountered a non-declered exception. Error: " + 
			ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, e);
			throw new ServerPersistenceModuleException(msg);
		}
	}

	@SuppressWarnings("unchecked")
	public List<IAnnuity> getPayorAnnuities(IPayor payor) 
		throws InvalidArgumentException, ServerPersistenceModuleException {				
		logger.fine("getPayorAnnuities");
		if (payor == null) {
			logger.fine("Invalid payor - its null, returning Exception" + logger.getAllLogs());
			throw new InvalidArgumentException("Invalid Payor argument -its null.");
		}
		EntityManager em = null;
		try{		 						
			cachedFactory = getEntityManagerFactory(payor);							
			em = cachedFactory.createEntityManager();					
			Query query = em.createNamedQuery("GetPayorAnnuities");
			query.setParameter(1, payor.getId());
			List<IAnnuity> annuities = (List<IAnnuity>) query.getResultList();

			//load annuity eager relationships.
			for (IAnnuity annuity : annuities) {				
				annuity.getPayouts();				
			}			
			return annuities;
		}catch (PersistenceException e) {
			String msg = "Persistence Module returned an error - see error message for details. Error: " 
				+ ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.log(Level.WARNING, msg, e);
			throw new ServerPersistenceModuleException(msg);

		}catch (Throwable t) {
			String msg = "Encountered a non-declered exception. Error: " + 
			ExceptionFormatter.deepFormatToString(t) + logger.getAllLogs(); 
			logger.log(Level.SEVERE, msg, t);
			throw new ServerPersistenceModuleException(msg);
		}finally {
			logger.fine("- close em");
			if (em != null){
				em.close();
			}			
		}
		
	}
	
	*/
	protected synchronized EntityManagerFactory getEntityManagerFactory(Configrable<String, String> configrable) throws InvalidArgumentException{
		String refreshEmf = configrable.getConfiguration().getParameterValue("refreshJPAEMF");
		logger.fine("refreshJPAEMF value is:" + refreshEmf + (" null means false"));
		logger.fine("cachedFactory is: " + cachedFactory );
		if (cachedFactory == null || "true".equalsIgnoreCase(refreshEmf) || !(cachedFactory.isOpen())){			
			if (cachedFactory != null && cachedFactory.isOpen()) {  // i.e refresh request
				logger.fine("cachedFactory is not null, we need to close it first.  Issuing close()");
				cachedFactory.close();
			}

			PersistenceUnitNameHelper.setPersistenceUnitName(configrable, null);
			String persistenceUnitName = configrable.getConfiguration().getParameterValue("persistenceUnitName");
			logger.fine("persistenceUnitName: " + persistenceUnitName);
			if (JPAPersistenceUnitName.AnnuitySAJAXBJPA.toString().equals(persistenceUnitName)) {
				logger.fine("using AnnuitySAJAXBJPA factory");
				cachedFactory = Persistence.createEntityManagerFactory("AnnuitySAJAXBJPA", 
						configrable.getConfiguration().getParameters());
			}else if (JPAPersistenceUnitName.AnnuitySAJPAOnly.toString().equals(persistenceUnitName)) {
				logger.fine("using AnnuitySAJPAOnly factory");
				cachedFactory = Persistence.createEntityManagerFactory("AnnuitySAJPAOnly", 
						configrable.getConfiguration().getParameters());				
			}else if (JPAPersistenceUnitName.PolicySAJPAOnly.toString().equals(persistenceUnitName)) {
				logger.fine("using PolicySAJPAOnly factory");
				cachedFactory = Persistence.createEntityManagerFactory("PolicySAJPAOnly", 
						configrable.getConfiguration().getParameters());				
			}
			
			else{
				throw new InvalidArgumentException("The parameter persistenceUnitName was not set.  Please provide a valid value! " + logger.getAllLogs());
			}
		}
		return cachedFactory;
	}
	

	private void rollBackTransaction(EntityManager em) {
		if (em == null) return;
		EntityTransaction toRollBack = em.getTransaction();
		if(toRollBack != null && toRollBack.isActive()) {
			try {
				toRollBack.rollback();
			}catch(Exception ignoreMe) { }
		}
	}
}