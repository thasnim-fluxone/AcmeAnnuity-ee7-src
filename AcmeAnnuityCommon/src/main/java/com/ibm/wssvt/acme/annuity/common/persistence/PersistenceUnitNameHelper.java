package com.ibm.wssvt.acme.annuity.common.persistence;

import com.ibm.wssvt.acme.common.bean.Configrable;

public class PersistenceUnitNameHelper {

	private static final String PERSISTENCE_UNIT_NAME = "persistenceUnitName";

	public static void setPersistenceUnitName(Configrable<String, String> configrable, JPAPersistenceUnitName persistenceUnitName) {
		if (configrable == null) {
			return;
		}
		String currentPUName = configrable.getConfiguration().getParameterValue(PERSISTENCE_UNIT_NAME);
		if (currentPUName != null && currentPUName.trim().length() >0) {
			// name alerady provided, do not override.
			return;
		}
		
		if (persistenceUnitName == null) {
			// try best guess for backword compatability
			String beansFactoryName = configrable.getConfiguration().getParameterValue("internal.beansFactoryClass");
			String persistenceType = configrable.getConfiguration().getParameterValue("persistenceType");
			if (beansFactoryName == null || persistenceType == null) {
				configrable.getConfiguration().addParameter(PERSISTENCE_UNIT_NAME, "");
				return;
			}
			
			if (AnnuityPersistenceType.JPA_SA.toString().equals(persistenceType)){
				if (beansFactoryName.contains("JAXBJPABeansFactory")) {
					configrable.getConfiguration().addParameter(PERSISTENCE_UNIT_NAME, JPAPersistenceUnitName.AnnuitySAJAXBJPA.toString());
				}else if (beansFactoryName.contains("JPABeansFactory")) {
					configrable.getConfiguration().addParameter(PERSISTENCE_UNIT_NAME, JPAPersistenceUnitName.AnnuitySAJPAOnly.toString());
				}else if (beansFactoryName.contains("JavaBeansFactory")) {
					configrable.getConfiguration().addParameter(PERSISTENCE_UNIT_NAME, ""); // unknown. tester must have prvided it, cannot guess.
				}					
			}else if (AnnuityPersistenceType.JPA_EJB.toString().equals(persistenceType)){
				if (beansFactoryName.contains("JAXBJPABeansFactory")) {
					configrable.getConfiguration().addParameter(PERSISTENCE_UNIT_NAME, JPAPersistenceUnitName.AnnuityDSJAXBJPA.toString());
				}else if (beansFactoryName.contains("JPABeansFactory")) {
					configrable.getConfiguration().addParameter(PERSISTENCE_UNIT_NAME, JPAPersistenceUnitName.AnnuityDSJPAOnly.toString());
				}else if (beansFactoryName.contains("JavaBeansFactory")) {
					configrable.getConfiguration().addParameter(PERSISTENCE_UNIT_NAME, ""); // unknown. tester must have prvided it, cannot guess.
				}				
			}			
			
		}else{
			configrable.getConfiguration().addParameter(PERSISTENCE_UNIT_NAME, persistenceUnitName.toString());
		}
		
	}
}
