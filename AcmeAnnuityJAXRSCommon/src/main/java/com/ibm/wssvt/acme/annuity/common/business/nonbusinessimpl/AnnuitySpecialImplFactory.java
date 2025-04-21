package com.ibm.wssvt.acme.annuity.common.business.nonbusinessimpl;

import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.exception.InvalidConfigurationException;

public class AnnuitySpecialImplFactory {

	public IAnnuityService getSpecialAnnuityService(Configrable<String, String> configrable) throws InvalidConfigurationException{
		String specialService = configrable.getConfiguration().getParameterValue("specialServiceName");
		if ("ExceptionReturnService".equalsIgnoreCase(specialService)) {
			return new AnnuityExceptionReturnService();
		}else if ("MemoryManagementService".equalsIgnoreCase(specialService)) {
			return new AnnuityMemoryManagementService();
		}else if ("QuickReturnService".equalsIgnoreCase(specialService)) {
			return new AnnuityQuickReturnService();
		}else if (specialService != null && specialService.endsWith(".class")){
			try {
				IAnnuityService service  = (IAnnuityService) Thread.currentThread().getContextClassLoader().loadClass(specialService).newInstance();
				return service;
			} catch (Exception e) {
				throw new InvalidConfigurationException("Failed to load the specialServiceName value of: " + specialService
						+ "The error is: " + e.getMessage(), e);
			}						
		}
		throw new InvalidConfigurationException("Annuity Special Service name is NOT Valid.  Received value:  " + specialService);
	}
}
