package com.ibm.wssvt.acme.annuity.persistence.impl.ejbjpa;
/*
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
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneContact;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneficiary;
import com.ibm.wssvt.acme.annuity.common.bean.IFund;
import com.ibm.wssvt.acme.annuity.common.bean.IPersisteble;
import com.ibm.wssvt.acme.annuity.common.bean.IPolicy;
import com.ibm.wssvt.acme.annuity.common.bean.jpa.AnnuityPersistebleObject;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.annuity.common.persistence.JPAPersistenceUnitName;
import com.ibm.wssvt.acme.annuity.common.persistence.PersistenceUnitNameHelper;
import com.ibm.wssvt.acme.annuity.common.persistence.PolicyPersistence;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityLoggerFactory;
//import com.ibm.wssvt.acme.annuity.common.util.EJBInputParamExaminerInterceptor;
//import com.ibm.wssvt.acme.annuity.common.util.EJBLoggingInterceptor;
//import com.ibm.wssvt.acme.annuity.common.util.RunningServerInfo;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.exception.ExceptionFormatter;
import com.ibm.wssvt.acme.common.log.AcmeLogger;


 * $Rev: 705 $
 * $Date: 2007-08-23 16:53:38 -0500 (Thu, 23 Aug 2007) $
 * $Author: gli $
 * $LastChangedBy: gli $
 

@Stateless
@Local(PolicyPersistenceJPALocal.class)
//@Interceptors({EJBInputParamExaminerInterceptor.class, EJBLoggingInterceptor.class})
public class PolicyPersistenceJPASessionBean implements PolicyPersistence<IPersisteble<?>, String, String>{
	@PersistenceUnit(unitName="PolicyDSJPAOnly")
	private EntityManagerFactory entityManagerJPAFactory;	
	
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
	public List<IPolicy> getHolderPolicies(IAnnuityHolder holder) throws InvalidArgumentException, ServerPersistenceModuleException {
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(holder, getClass().getName());		

		logger.fine("getHolderAnnuities. Holder info: id: " + holder.getId());
		if (holder == null) {
			String msg = "Invalid annuity holder - its null, returning Exception"
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" + logger.getAllLogs();
			logger.fine(msg);				
			throw new InvalidArgumentException("Invalid Annuity Holder argument");
		}
		String queryType = holder.getConfiguration().getParameterValue("queryType");
		EntityManager em = null;
		Query query;
		try{		 						
			em = getEntityManager(holder);
			if(queryType != null && queryType.toLowerCase().equals("criteria")) {
				logger.fine("Performing criteria query for getHolderPolicies");
				String policyClassName = holder.getClass().getPackage().getName() + ".Policy";
				logger.finest("Policy implementation: " + policyClassName);
				CriteriaBuilder qb = em.getCriteriaBuilder();
				CriteriaQuery cq = qb.createQuery();
				Root<IPolicy> p = cq.from(Class.forName(policyClassName));
				cq.select(p).where(qb.equal(p.get("annuityHolderId"), holder.getId()));
				query = em.createQuery(cq);
			} else {
				logger.fine("Performing JPQL query for getHolderPolicies");
				query = em.createNamedQuery("GetHolderPolicies");
				logger.fine("after get querey: " + query);
				query.setParameter("holderId", holder.getId());
			}
			List<IPolicy> polList = query.getResultList();
			logger.fine("List that was returned " + polList);
			return polList;
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
	
	@SuppressWarnings("unchecked")
	public List<IBeneficiary> getHolderBeneficiaries(IAnnuityHolder holder) throws InvalidArgumentException, ServerPersistenceModuleException {
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(holder, getClass().getName());

		logger.fine("getHolderBeneficiaries. Holder info: id: " + holder.getId());
		if (holder == null) {
			String msg = "Invalid annuity holder - its null, returning Exception"
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" + logger.getAllLogs();
			logger.fine(msg);
			throw new InvalidArgumentException("Invalid Annuity Holder argument");
		}
		String queryType = holder.getConfiguration().getParameterValue("queryType");
		EntityManager em = null;
		Query query;
		try{
			em = getEntityManager(holder);
			if(queryType != null && queryType.toLowerCase().equals("criteria")) {
				logger.fine("Performing criteria query for getHolderBeneficiaries");
				String beneficiaryClassName = holder.getClass().getPackage().getName() + ".Beneficiary";
				logger.finest("Beneficiary implementation: " + beneficiaryClassName);
				CriteriaBuilder qb = em.getCriteriaBuilder();
				CriteriaQuery cq = qb.createQuery();
				Root<IBeneficiary> p = cq.from(Class.forName(beneficiaryClassName));
				cq.select(p).where(qb.equal(p.get("annuityHolderId"), holder.getId()));
				query = em.createQuery(cq);
			} else {
				logger.fine("Performing JPQL query for getHolderBeneficiaries");
				query = em.createNamedQuery("GetHolderBeneficiaries");
				logger.fine("after get querey: " + query);
				query.setParameter("holderId", holder.getId());
			}
			List<IBeneficiary> benList = query.getResultList();
			logger.fine("List that was returned " + benList);
			return benList;
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
	
	@SuppressWarnings("unchecked")
	public List<String> getHolderBeneficiaryContactEmails(IAnnuityHolder holder) throws InvalidArgumentException, ServerPersistenceModuleException {
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(holder, getClass().getName());

		logger.fine("getHolderBeneficiaryContactEmails. Holder info: id: " + holder.getId());
		if (holder == null) {
			String msg = "Invalid annuity holder - its null, returning Exception"
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" + logger.getAllLogs();
			logger.fine(msg);
			throw new InvalidArgumentException("Invalid Annuity Holder argument");
		}
		String queryType = holder.getConfiguration().getParameterValue("queryType");
		EntityManager em = null;
		Query query;
		try{		 						
			em = getEntityManager(holder);
			if(queryType != null && queryType.toLowerCase().equals("criteria")) {
				logger.fine("Performing criteria query for getHolderBeneficiaryContactEmails");
				String beneContactClassName = holder.getClass().getPackage().getName() + ".BeneContact";
				logger.finest("BeneContact implementation: " + beneContactClassName);
				CriteriaBuilder qb = em.getCriteriaBuilder();
				CriteriaQuery cq = qb.createQuery();
				Root<IBeneContact> p = cq.from(Class.forName(beneContactClassName));
				cq.select(p.get("email")).where(qb.equal(p.get("beneficiary").get("annuityHolderId"), holder.getId())).orderBy(qb.asc(p.get("email")));
				List<Order> sorting = cq.getOrderList();
				sorting.add(0, qb.asc(p.get("id").get("contactType")));
				cq.orderBy(sorting);
				query = em.createQuery(cq);
			} else {
				logger.fine("Performing JPQL query for getHolderBeneficiaryContactEmails");
				query = em.createNamedQuery("GetHolderBeneficiaryContactEmails");
				logger.fine("after get querey: " + query);
				query.setParameter("holderId", holder.getId());
			}
			List<String> emailList = query.getResultList();
			logger.fine("List that was returned " + emailList);
			return emailList;
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
	
	@SuppressWarnings("unchecked")
	public IFund getFundIndexRateFromFundName(IAnnuityHolder holder, String fundName) throws InvalidArgumentException, ServerPersistenceModuleException {
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(holder, getClass().getName());		

		logger.fine("getFundIndexRateFromFundName. Holder info: id: " + holder.getId());
		if (holder == null) {
			String msg = "Invalid annuity holder - its null, returning Exception"
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" + logger.getAllLogs();
			logger.fine(msg);				
			throw new InvalidArgumentException("Invalid Annuity Holder argument");
		}	
		String queryType = holder.getConfiguration().getParameterValue("queryType");
		EntityManager em = null;
		Query query;
		try{		 						
			em = getEntityManager(holder);
			if(queryType != null && queryType.toLowerCase().equals("criteria")) {
				logger.fine("Performing criteria query for getFundIndexRateFromFundName");
				String policyClassName = holder.getClass().getPackage().getName() + ".Policy";
				logger.finest("BeneContact implementation: " + policyClassName);
				CriteriaBuilder qb = em.getCriteriaBuilder();
				CriteriaQuery cq = qb.createQuery();
				Root<IPolicy> policy = cq.from(Class.forName(policyClassName));
				MapJoin<IPolicy, Integer, IFund> fund = policy.joinMap("Funds");
				cq.select(fund).where(qb.equal(fund.get("fundName"), fundName), qb.equal(policy.get("annuityHolderId"), holder.getId()));
				query = em.createQuery(cq);
			} else {
				logger.fine("Performing JPQL query for getFundIndexRateFromFundName");
				query = em.createNamedQuery("GetFundIndexRateFromFundName");
				logger.fine("after get querey: " + query);
				query.setParameter("holderId", holder.getId());	
				query.setParameter("fName", fundName);
			}

			Object o = query.getSingleResult();			
			logger.fine("Object that was returned " + o);
			return (IFund) o;		

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
	
	@SuppressWarnings("unchecked")
	public List<IFund> getFundsInState(IAnnuityHolder holder, String state) throws InvalidArgumentException, ServerPersistenceModuleException {
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(holder, getClass().getName());		

		logger.fine("getFundsInState. Holder info: id: " + holder.getId());
		if (holder == null) {
			String msg = "Invalid annuity holder - its null, returning Exception"
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" + logger.getAllLogs();
			logger.fine(msg);				
			throw new InvalidArgumentException("Invalid Annuity Holder argument");
		}	
		String queryType = holder.getConfiguration().getParameterValue("queryType");
		EntityManager em = null;
		Query query;
		try{
			em = getEntityManager(holder);
			if(queryType != null && queryType.toLowerCase().equals("criteria")) {
				logger.fine("Performing criteria query for getFundsInState");
				String policyClassName = holder.getClass().getPackage().getName() + ".Policy";
				logger.finest("Policy implementation: " + policyClassName);
				CriteriaBuilder qb = em.getCriteriaBuilder();
				CriteriaQuery cq = qb.createQuery();
				Root<IPolicy> policy = cq.from(Class.forName(policyClassName));
				MapJoin<IPolicy, Integer, IFund> fund = policy.joinMap("Funds");
				cq.where(qb.equal(policy.get("annuityHolderId"), holder.getId()));
				cq.where(cq.getRestriction(), qb.equal(fund.get("theAddress").get("state"), state)).select(fund);
				query = em.createQuery(cq);
			} else {
				logger.fine("Performing JPQL query for getFundsInState");
				query = em.createNamedQuery("GetHolderFundsInState");
				logger.fine("after get querey: " + query);
				query.setParameter("holderId", holder.getId());	
				query.setParameter("state", state);
			}
			List<IFund> fundList = query.getResultList();
			logger.fine("List that was returned " + fundList);
			return fundList;
					
			
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

	@SuppressWarnings("unchecked")
	public List<IPersisteble<?>> customQuery(IAnnuityHolder holder)
	throws InvalidArgumentException, ServerPersistenceModuleException {
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(holder, getClass().getName());

		logger.fine("getHolderBeneficiaryContactEmails. Holder info: id: " + holder.getId());
		if (holder == null) {
			String msg = "Invalid annuity holder - its null, returning Exception"
				+ " server: " + "RunningServerInfo.getInstance().getRunningServerInfo()" + logger.getAllLogs();
			logger.fine(msg);
			throw new InvalidArgumentException("Invalid Annuity Holder argument");
		}
		String rootClass = holder.getConfiguration().getParameterValue("queryRoot");
		EntityManager em = null;
		try{
			em = getEntityManager(holder);
			logger.fine("Performing custom query");
			String rootClassName = holder.getClass().getPackage().getName() + "." + rootClass;
			logger.finest("Root class: " + rootClassName);
			CriteriaBuilder qb = em.getCriteriaBuilder();
			CriteriaQuery cq = qb.createQuery();
			Root<AnnuityPersistebleObject> root = cq.from(Class.forName(rootClassName));
			cq.select(root).where(qb.equal(root.get("annuityHolderId"), holder.getId()));
			Query query = em.createQuery(cq);
			List l = (List<IPersisteble<?>>) query.getResultList();
			return l;
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

	private EntityManager getEntityManager(Configrable<String, String> configrable) throws InvalidArgumentException{
		AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(configrable, getClass().getName());		
		
		PersistenceUnitNameHelper.setPersistenceUnitName(configrable, null);
		String persistenceUnitName = configrable.getConfiguration().getParameterValue("persistenceUnitName");
		logger.fine("persistenceUnitName: " + persistenceUnitName);
		//if (JPAPersistenceUnitName.AnnuityDSJAXBJPA.toString().equals(persistenceUnitName)) {
			//logger.fine("JAXBJPA - PU");
			//return entityManagerJAXBJPAFactory.createEntityManager();
		//}else
			if (JPAPersistenceUnitName.PolicyDSJPAOnly.toString().equals(persistenceUnitName)) {
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
*/
