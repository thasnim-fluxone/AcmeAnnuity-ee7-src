package com.ibm.wssvt.acme.annuity.common.util;

import java.io.Serializable;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class EJBInputParamExaminerInterceptor implements Serializable{

	private static final long serialVersionUID = -2444926584505739316L;
	
	@AroundInvoke
	public Object CheckInputParamValue(InvocationContext ic) throws Exception{	
		Object result = null;		
		try {								
			Object[] params = ic.getParameters();		
			if (params == null) {
				System.out.println("INFO: " + getClass().getName() 
						+ " intercepted the method: " + ic.getMethod().getName()
						+ " that has a ic.getParameters() returned null - all params are null?");
			}else{
				for (Object param : params) {
					if (param == null){
						System.out.println("INFO: " + getClass().getName() 
								+ " intercepted the method: " + ic.getMethod().getName()
								+ " that has a null input parameter");
					}
				}
			}
			result = ic.proceed();
			return result;
		} finally {							
		}								
	}
	

}
