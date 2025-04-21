package com.ibm.wssvt.acme.annuity.business.impl.ejb30;
/*

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import com.ibm.wssvt.acme.annuity.common.util.AnnuityLoggerFactory;
import com.ibm.wssvt.acme.annuity.common.util.RunningServerInfo;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
  //    /** javadoc starts
 * $Rev: 621 $
 * $Date: 2007-08-03 17:51:36 -0500 (Fri, 03 Aug 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 //     javadoc ends
public class EJBRunningServerInfoInterceptor implements Serializable{
	private static final long serialVersionUID = -813019909465130674L;

	@SuppressWarnings("unchecked")
	@AroundInvoke
	public Object methodLogAndTime(InvocationContext ic) throws Exception{		
		Object result = null;
		try {					
			Configrable<String, String> configrable = null;
			Object[] params = ic.getParameters();
			for (Object param : params) {
				if (param instanceof Configrable){
					configrable = (Configrable<String, String>) param;
					break;
				}
			}
			if (configrable == null){
				Logger logger = Logger.getLogger(getClass().getName());
				logger.warning("The method call did not conatin any configrable paramenter."
						+ "Method is: " + ic.getMethod().getName()
						+ ". Its is expected that EACH method must contain at least one argument of type Configrable."
						+ ".  Using default logger: " + logger.getName());			
			}else{
				AcmeLogger logger = AnnuityLoggerFactory.getAcmeServerLogger(configrable, getClass().getName());		
				if (logger.getLogger().isLoggable(Level.FINE)){
					logger.fine("entering: " + getClass().getName() + " for method: " + ic.getMethod().getName());
					logger.fine("params: ");
					for (Object param : params) {
						logger.fine("param: " + param.toString());
					}
					
				}					
			}
			result = ic.proceed();
			return result;
		} finally {		

			if (result != null && result instanceof Configrable){			
				Configrable<String, String> config = (Configrable<String, String>) result;
				RunningServerInfo.getInstance().setRunningServerInfo(config);							
			}			
		}								
	}

}
*/
