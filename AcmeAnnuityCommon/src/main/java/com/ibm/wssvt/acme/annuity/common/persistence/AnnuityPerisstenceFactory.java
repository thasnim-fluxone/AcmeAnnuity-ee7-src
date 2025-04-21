package com.ibm.wssvt.acme.annuity.common.persistence;

import java.util.logging.Level;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.ibm.wssvt.acme.annuity.common.bean.IPersisteble;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.annuity.common.persistence.impl.jpasa.PersistenceJPASAImpl;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
import com.ibm.wssvt.acme.common.util.StringUtils;


/**
 * $Rev: 621 $
 * $Date: 2007-08-03 17:51:36 -0500 (Fri, 03 Aug 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class AnnuityPerisstenceFactory {
	 

   	@SuppressWarnings("unchecked")
	public static AnnuityPersistence<IPersisteble<String>, String, String> getAnnuityPersistence(Configrable<String, String> configrable, AcmeLogger logger) 
	throws ServerPersistenceModuleException{   		
		AnnuityPersistenceType type = getPersistenceType(configrable, logger);	
		logger.fine("using persistence type: " + type);
		if (AnnuityPersistenceType.JPA_SA.equals(type)){
			return new PersistenceJPASAImpl(logger); 
		
		}else if (AnnuityPersistenceType.JPA_EJB.equals(type)){			
			try {
				InitialContext ic = new InitialContext();				                                                                       
				Object ref = ic.lookup(configrable.getConfiguration().getParameterValue("jpaEjbLocalJndi"));					
				return (AnnuityPersistence)ref;
			} catch (NamingException e) {
				logger.log(Level.WARNING, "Failed to locate the JPA EJB Persience Implementaion." , e);
				throw new ServerPersistenceModuleException("Failed to locate the JPA EJB Persience Implementaion. Error: " + e);
			}catch(Throwable e) {
				logger.log(Level.WARNING, "Failed to locate the JPA EJB Persience Implementaion." , e);
				throw new ServerPersistenceModuleException("Failed to locate the JPA EJB Persience Implementaion. Error: " + e);
			}
		}else if (AnnuityPersistenceType.JPA_EJB_OPEN_JPA_EXT.equals(type)){			
			try {
				InitialContext ic = new InitialContext();				                                                                       
				Object ref = ic.lookup(configrable.getConfiguration().getParameterValue("jpaEjbOpenJpaExtLocalJndi"));					
				return (AnnuityPersistence)ref;
			} catch (NamingException e) {
				logger.log(Level.WARNING, "Failed to locate the JPA EJB Open JPA Ext Persience Implementaion." , e);
				throw new ServerPersistenceModuleException("Failed to locate the JPA EJB Open JPA Ext Persience Implementaion. Error: " + e);
			}catch(Throwable e) {
				logger.log(Level.WARNING, "Failed to locate the JPA EJB Open JPA Ext Persience Implementaion." , e);
				throw new ServerPersistenceModuleException("Failed to locate the JPA EJB Open JPA Ext Persience Implementaion. Error: " + e);
			}	
		}else{
			logger.info("Failed to locate the Persience Implementaion - " +
					"Could not find the type: " + configrable.getConfiguration().getParameterValue("persistenceType"));
			throw new ServerPersistenceModuleException("Failed to locate the Persience Implementaion - " +
					"Could not find the type: " + configrable.getConfiguration().getParameterValue("persistenceType"));
		}
		
	}

	private static AnnuityPersistenceType getPersistenceType(Configrable<String, String> configrable, AcmeLogger logger) throws ServerPersistenceModuleException {	
		AnnuityPersistenceType type;
		try{
			type = StringUtils.toEnum(AnnuityPersistenceType.class, 
					configrable.getConfiguration().getParameterValue("persistenceType"));				
		}catch (IllegalArgumentException e) {
			logger.warning("Failed to get the PersistenceType.  Configrable: " + configrable.getClass().getName() 
					+ "Configrable values: " + configrable.getConfiguration().getParameters().toString());
			throw new ServerPersistenceModuleException("Invalid Server Configuration.  Unable to find the Persistence " +
					"Implementaion for the persistence type: " 
					+ configrable.getConfiguration().getParameterValue("persistenceType"));
		}
		return type;
	}
}
