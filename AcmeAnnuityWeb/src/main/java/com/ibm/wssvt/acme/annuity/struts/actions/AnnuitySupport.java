package com.ibm.wssvt.acme.annuity.struts.actions;

import com.opensymphony.xwork2.ActionSupport;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityBeansFactory;
import com.ibm.wssvt.acme.annuity.common.bean.java.JavaBeansFactory;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.JAXBJPABeansFactory;
import com.ibm.wssvt.acme.annuity.common.bean.jpa.JPABeansFactory;
//import com.ibm.wssvt.acme.annuity.common.business.AnnuityBusinessServiceLookup;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.exception.ServerBusinessModuleException;
import com.ibm.wssvt.acme.annuity.common.servicelookup.jaxrs.JAXRSServiceLookup;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityServerConfig;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.bean.StringConfigrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
import com.ibm.wssvt.acme.common.log.AcmeLoggerFactory;


/**
 * Base Action class for the Acme package.
 */
public class AnnuitySupport extends ActionSupport {

	/**
	 * The Base Action class from which all the other classes inherit.
	 * Has the task field - to show what task in being performed.
	 * Also sets the global parameters.
	 */
	private static final long serialVersionUID = 1L;
	
	private String task = null;
	
	public String getTask() {
        return task;
    }
	public void setTask(String tType) {
        task =  tType;
    }
		
	public IAnnuityBeansFactory getBeansFactory(){		
		if (AnnuityServerConfig.getInstance() == null){
			throw new RuntimeException ("The server configuration is not initialized yet.");
		}
		String beansFactory = AnnuityServerConfig.getInstance().getConfigs().get("beansFactoryClass");
		if ("com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.JAXBJPABeansFactory".equals(beansFactory)){
			return new JAXBJPABeansFactory();
		}else if ("com.ibm.wssvt.acme.annuity.common.bean.jpa.JPABeansFactory".equals(beansFactory)){				
			return new JPABeansFactory();
		}else if ("com.ibm.wssvt.acme.annuity.common.bean.java.JavaBeansFactory".equals(beansFactory)){				
			return new JavaBeansFactory();
		}
		throw new RuntimeException ("The server configuration does not contain valid value for: beansFactoryClass "
				+ " Current value is: " + beansFactory);		
			
	}
	
	public IAnnuityService getNextService(Configrable<String, String> configrable) throws ServerBusinessModuleException{
		try {			
			return JAXRSServiceLookup.getAnnuityJAXRSService(configrable, getLogger(getClass().getName()));
			//return new AnnuityBusinessServiceLookup().getAnnuityAnnuityService(configrable);						
		} catch (Exception e) {							
			throw new ServerBusinessModuleException(e.getMessage(), e);
		} 

	}

	public void setConfigs(Configrable<String, String> configrable){
		if (AnnuityServerConfig.getInstance() == null){
			throw new RuntimeException ("The server configuration is not initialized yet.");			
		}
		configrable.getConfiguration().addAllParams(AnnuityServerConfig.getInstance().getConfigs());				
	}   

	public void setConfigsFromURL(String fileURL){
		if (AnnuityServerConfig.getInstance() == null) {
			AnnuityServerConfig.init(fileURL);
		}		
	}
	
	public AcmeLogger getLogger(String className){
		if (AnnuityServerConfig.getInstance() == null){
			throw new RuntimeException ("The server configuration is not initialized yet.");
		}
		Configrable<String, String> configs = new StringConfigrable();
		configs.getConfiguration().addAllParams(AnnuityServerConfig.getInstance().getConfigs());
		return AcmeLoggerFactory.getAcmeServerLogger(configs, className);
		
	}
}
