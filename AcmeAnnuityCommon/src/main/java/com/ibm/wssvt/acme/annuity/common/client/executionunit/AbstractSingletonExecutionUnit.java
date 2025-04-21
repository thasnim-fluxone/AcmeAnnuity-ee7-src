package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityBeansFactory;
import com.ibm.wssvt.acme.annuity.common.client.adapter.ISingletonServerAdapter;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityLoggerFactory;
import com.ibm.wssvt.acme.common.executionunit.AbstractExecutionUnit;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
import com.ibm.wssvt.acme.common.log.AcmeLoggerConfig;
import com.ibm.wssvt.acme.common.util.StringUtils;

/**
 * $Rev: 773 $
 * $Date: 2007-09-13 11:32:04 -0500 (Thu, 13 Sep 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public abstract class AbstractSingletonExecutionUnit extends AbstractExecutionUnit {
	
	private static final long serialVersionUID = 1856315791007242892L;

	public ISingletonServerAdapter getServerAdapter() {
		return (ISingletonServerAdapter)super.getServerAdapter();
	}

	public void setServerAdapter(ISingletonServerAdapter adapter) {
		super.setServerAdapter(adapter);
	}
				
	public int getParameterValueInt(String key) throws InvalidExecutionUnitParameterException {
		String value =getConfiguration().getParameterValue(key);			
		try {
			return StringUtils.toInt(value);
		} catch (NumberFormatException e) {
			throw new InvalidExecutionUnitParameterException("The parameter: " 
					+ key + " is not an integer for scenario with description: " + getDescription()
					+ " Additional Error: " + e);
		}
	}
	
	public boolean getParameterValueBoolean(String key) throws InvalidExecutionUnitParameterException {
		String value =getConfiguration().getParameterValue(key);			
		if (value == null) {
			throw new InvalidExecutionUnitParameterException ("The parameter: " 
					+ key + " is null or not defined for the for scenario with description: " + getDescription());
		}
		if ("1".equals(value) || "true".equalsIgnoreCase(value)) {
			return true;
		}
		if ("0".equals(value) || "false".equalsIgnoreCase(value)) {
			return false;
		}
		throw new InvalidExecutionUnitParameterException ("The parameter: " 
			+ key + " is not a valid boolean value.  Valid boolean values are 0, 1, true or false" +
			".  It is defined for the for scenario with description: " + getDescription()
			+ " Current value is: " + value);
		
	}
	
	public  <T extends Enum<T>> T  getParameterValueEnum(Class<T> enumType, String key) 
		throws InvalidExecutionUnitParameterException {		
		String value = getConfiguration().getParameterValue(key);
		try{
			
			if (value == null || value.trim().length()<1) {
				throw new InvalidExecutionUnitParameterException("The parameter: " 
						+ key + " is null or empty for Scenario with description: " + getDescription());
			}
			return StringUtils.toEnum(enumType, value);
		}catch (IllegalArgumentException e) {
			throw new InvalidExecutionUnitParameterException("The parameter: " 
					+ key + " with value: " + value + " is not a valid value"
					+ " Scenario with description: " + getDescription() + " Additional Error: " + e);
		}
						
	}
	
	public int getRandomInteger(int lowerLimit, int upperLimit){
		int n = upperLimit - lowerLimit + 1;
        int i = new Random(UUID.randomUUID().hashCode()).nextInt(Math.abs(n)) ;
        return lowerLimit + i;
	}
	
	public <T extends Enum<T>> T  getRandomEnum(Class<T> enumType) {
		 T[] t = enumType.getEnumConstants();
		 int i = getRandomInteger(0, (t.length-1));
		 return t[i];		 
	 }
	 
	
	public IAnnuityBeansFactory getAnnuityBeansFactory(){
		return (IAnnuityBeansFactory) super.getBeansFactory();
	}
	
	protected AcmeLogger getLogger(String loggerName){				
		Logger root = Logger.getLogger(getClientContext().getPrefixedRootLoggerName());		
		AcmeLoggerConfig.setClientFileNamePattern("",
				getClientContext().getClientId(), getClientContext().getThreadId(), this);
		Logger theLogger = AnnuityLoggerFactory.getClientLogger(root, this, getClientContext().getLoggerPrefix()+ loggerName);
		AcmeLogger al = new AcmeLogger(theLogger);
		return al;
	}


}