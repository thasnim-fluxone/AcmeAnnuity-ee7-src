package com.ibm.wssvt.acme.annuity.common.persistence.impl.jpasa;

import java.util.List;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
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
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.annuity.common.persistence.PolicyPersistence;
import com.ibm.wssvt.acme.common.exception.ExceptionFormatter;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class PolicyPersistenceJPASAImpl<T> extends BasicPersistenceJPASAImpl<T> implements PolicyPersistence<IPersisteble<T>, String, String> {

	private AcmeLogger logger;
	public PolicyPersistenceJPASAImpl(AcmeLogger logger){
		super(logger);
		this.logger = logger;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<IPolicy> getHolderPolicies(IAnnuityHolder holder) throws InvalidArgumentException, ServerPersistenceModuleException {
		logger.fine("getHolderPolicies");
		if (holder == null) {
			logger.fine("Invalid annuity holder - its null, returning Exception" + logger.getAllLogs());
			throw new InvalidArgumentException("Invalid Annuity Holder argument");
		}
		EntityManagerFactory factory = null;
		EntityManager em = null;
		String queryType = holder.getConfiguration().getParameterValue("queryType");
		try {
			factory = super.getEntityManagerFactory(holder);
			em = factory.createEntityManager();
			Query query;
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
				query.setParameter("holderId", holder.getId());
			}
			List l = (List<IPolicy>) query.getResultList();
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
	
	@SuppressWarnings("unchecked")
	public List<IBeneficiary> getHolderBeneficiaries(IAnnuityHolder holder) throws InvalidArgumentException, ServerPersistenceModuleException {
		logger.fine("getHolderBeneficiaries");
		if(holder == null) {
			logger.fine("Invalid anuity holder - its null, returning Exception " + logger.getAllLogs());
			throw new InvalidArgumentException("Invalid Annuity Holder argument");
		}
		EntityManagerFactory factory = null;
		EntityManager em = null;
		String queryType = holder.getConfiguration().getParameterValue("queryType");
		try {
			factory = super.getEntityManagerFactory(holder);
			em = factory.createEntityManager();
			Query query;
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
				query.setParameter("holderId", holder.getId());
			}
			List l = (List<IBeneficiary>) query.getResultList();
			return l;
		} catch (PersistenceException e) {
			String msg = "Persistence Module returned an error - see error message for details. Error: "
				+ ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.log(Level.WARNING, msg, e);
			throw new ServerPersistenceModuleException(msg);
		} catch (Throwable t) {
			String msg = "Encountered a non-declered exception. Error: "
				+ ExceptionFormatter.deepFormatToString(t) + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, t);
			throw new ServerPersistenceModuleException(msg);
		} finally {
			logger.fine("- close em");
			if (em != null) {
				em.close();
			}
		}

	}
	
	@SuppressWarnings("unchecked")
	public List<String> getHolderBeneficiaryContactEmails(IAnnuityHolder holder) throws InvalidArgumentException, ServerPersistenceModuleException {
		logger.fine("getHolderBeneficiaryContactEmails");
		if(holder == null) {
			logger.fine("Invalid anuity holder - its null, returning Exception " + logger.getAllLogs());
			throw new InvalidArgumentException("Invalid Annuity Holder argument");
		}
		EntityManagerFactory factory = null;
		EntityManager em = null;
		String queryType = holder.getConfiguration().getParameterValue("queryType");
		try {
			factory = super.getEntityManagerFactory(holder);
			em = factory.createEntityManager();
			Query query;
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
				query.setParameter("holderId", holder.getId());
			}
			List l = (List<String>) query.getResultList();
			return l;
		} catch (PersistenceException e) {
			String msg = "Persistence Module returned an error - see error message for details. Error: "
				+ ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.log(Level.WARNING, msg, e);
			throw new ServerPersistenceModuleException(msg);
		} catch (Throwable t) {
			String msg = "Encountered a non-declered exception. Error: "
				+ ExceptionFormatter.deepFormatToString(t) + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, t);
			throw new ServerPersistenceModuleException(msg);
		} finally {
			logger.fine("- close em");
			if (em != null) {
				em.close();
			}
		}

	}


	@SuppressWarnings("unchecked")
	public IFund getFundIndexRateFromFundName(IAnnuityHolder holder, String fundName) throws InvalidArgumentException, ServerPersistenceModuleException {
		logger.fine("getFundIndexRateFromFundName");
		if(holder == null) {
			logger.fine("Invalid anuity holder - its null, returning Exception " + logger.getAllLogs());
			throw new InvalidArgumentException("Invalid Annuity Holder argument");
		}
		EntityManagerFactory factory = null;
		EntityManager em = null;
		String queryType = holder.getConfiguration().getParameterValue("queryType");
		try {
			factory = super.getEntityManagerFactory(holder);
			em = factory.createEntityManager();
			Query query;
			if(queryType != null && queryType.toLowerCase().equals("criteria")) {
				logger.fine("Performing criteria query for getFundIndexRateFromFundName");
				String policyClassName = holder.getClass().getPackage().getName() + ".Policy";
				logger.finest("Policy implementation: " + policyClassName);
				CriteriaBuilder qb = em.getCriteriaBuilder();
				CriteriaQuery cq = qb.createQuery();
				Root<IPolicy> policy = cq.from(Class.forName(policyClassName));
				MapJoin<IPolicy, Integer, IFund> fund = policy.joinMap("Funds");
				cq.select(fund).where(qb.equal(fund.get("fundName"), fundName), qb.equal(policy.get("annuityHolderId"), holder.getId()));
				query = em.createQuery(cq);
			} else {
				logger.fine("Performing JPQL query for getFundIndexRateFromFundName");
				query = em.createNamedQuery("GetFundIndexRateFromFundName");
				query.setParameter("holderId", holder.getId());
				query.setParameter("fName", fundName);
			}
			//List l = (List<String>) query.getResultList();
			Object o = query.getSingleResult();
			return (IFund) o;
			//return l;
		} catch (PersistenceException e) {
			String msg = "Persistence Module returned an error - see error message for details. Error: "
				+ ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.log(Level.WARNING, msg, e);
			throw new ServerPersistenceModuleException(msg);
		} catch (Throwable t) {
			String msg = "Encountered a non-declered exception. Error: "
				+ ExceptionFormatter.deepFormatToString(t) + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, t);
			throw new ServerPersistenceModuleException(msg);
		} finally {
			logger.fine("- close em");
			if (em != null) {
				em.close();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<IFund> getFundsInState(IAnnuityHolder holder, String state) throws InvalidArgumentException, ServerPersistenceModuleException {
		logger.fine("getFundsInState");
		if(holder == null) {
			logger.fine("Invalid anuity holder - its null, returning Exception " + logger.getAllLogs());
			throw new InvalidArgumentException("Invalid Annuity Holder argument");
		}
		EntityManagerFactory factory = null;
		EntityManager em = null;
		String queryType = holder.getConfiguration().getParameterValue("queryType");
		try {
			factory = super.getEntityManagerFactory(holder);
			em = factory.createEntityManager();
			Query query;
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
				//cq.select(fund).where(qb.equal(fund.get("theAddress").get("state"), state), qb.equal(policy.get("annuityHolderId"), holder.getId()));
				query = em.createQuery(cq);
			} else {
				logger.fine("Performing JPQL query for getFundsInState");
				query = em.createNamedQuery("GetHolderFundsInState");
				query.setParameter("holderId", holder.getId());
				query.setParameter("state", state);
			}
			List l = (List<IFund>) query.getResultList();
			return l;
		} catch (PersistenceException e) {
			String msg = "Persistence Module returned an error - see error message for details. Error: "
				+ ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.log(Level.WARNING, msg, e);
			throw new ServerPersistenceModuleException(msg);
		} catch (Throwable t) {
			String msg = "Encountered a non-declered exception. Error: "
				+ ExceptionFormatter.deepFormatToString(t) + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, t);
			throw new ServerPersistenceModuleException(msg);
		} finally {
			logger.fine("- close em");
			if (em != null) {
				em.close();
			}
		}
		
	}

	@SuppressWarnings("unchecked")
	public List<IPersisteble<T>> customQuery(IAnnuityHolder holder)
	throws InvalidArgumentException, ServerPersistenceModuleException {
		EntityManagerFactory factory = null;
		EntityManager em = null;
		String rootClass = holder.getConfiguration().getParameterValue("queryRoot");
		try {
			factory = super.getEntityManagerFactory(holder);
			em = factory.createEntityManager();
			logger.fine("Performing custom criteria query");
			String rootClassName = holder.getClass().getPackage().getName() + "." + rootClass;
			logger.finest("Root class: " + rootClassName);
			CriteriaBuilder qb = em.getCriteriaBuilder();
			CriteriaQuery cq = qb.createQuery();
			Root<AnnuityPersistebleObject> root = cq.from(Class.forName(rootClassName));
			cq.select(root).where(qb.equal(root.get("annuityHolderId"), holder.getId()));
			Query query = em.createQuery(cq);
			List l = (List<IPersisteble<T>>) query.getResultList();
			return l;
		} catch (PersistenceException e) {
			String msg = "Persistence Module returned an error - see error message for details. Error: "
				+ ExceptionFormatter.deepFormatToString(e) + logger.getAllLogs();
			logger.log(Level.WARNING, msg, e);
			throw new ServerPersistenceModuleException(msg);
		} catch (Throwable t) {
			String msg = "Encountered a non-declered exception. Error: "
				+ ExceptionFormatter.deepFormatToString(t) + logger.getAllLogs();
			logger.log(Level.SEVERE, msg, t);
			throw new ServerPersistenceModuleException(msg);
		} finally {
			logger.fine("- close em");
			if (em != null) {
				em.close();
			}
		}
	}	
	
}
