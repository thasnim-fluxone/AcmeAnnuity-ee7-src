package com.ibm.wssvt.acme.common.envconfig;


import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.envconfig.exception.ConfigurationException;

public  class AcmeEnvConfigLoader  {
	private static AcmeEnvConfigBean configBean;		
	private static AcmeEnvConfigLoader instance = null;	
	@SuppressWarnings("unused")
	private static String fileName;
	
	public AcmeEnvConfigBean getConfigBean() {
		return configBean;
	}
	private AcmeEnvConfigLoader(String fileName) throws ConfigurationException{
		if (fileName != null && fileName.trim().length() > 0 ) {
			AcmeEnvConfigLoader.fileName = fileName;
		}else{
			throw new ConfigurationException("Invalid File Name");
		}
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance("com.ibm.wssvt.acme.common.envconfig");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			URL url = new URL(fileName);
			configBean = (AcmeEnvConfigBean) unmarshaller.unmarshal(url);	
			
		} catch (JAXBException e) {
			throw new ConfigurationException("Unable to read the AcmeEnvConfig file: " + fileName
					+ "Error is: " + e);
		} catch (MalformedURLException e) {
			throw new ConfigurationException("Unable to read the AcmeEnvConfig file: " + fileName
					+ "Error is: " + e);
		}
	}
	
	public static boolean isInited() {
		return (instance != null);
	}
	
	public static synchronized AcmeEnvConfigLoader  init(String fileName) throws ConfigurationException {
		instance = null;
		instance = new AcmeEnvConfigLoader(fileName);
		return instance;
		
	}

	public static synchronized AcmeEnvConfigLoader getInstance() throws ConfigurationException {
		if (instance == null) {
			throw new ConfigurationException ("AcmeEnvConfig Loader has not been initialized.  Caller must use " +
			"AcmeEnvConfig.init(String fileName) first before calling the getInstance() " +
			"or any other method.");
		}
		return instance;
	}

	
	public ArrayList<AdapterConfiguration> getAdaptersList() {	
		ArrayList<AdapterConfiguration> resultList = new ArrayList<AdapterConfiguration>();
		if (configBean.getAdaptersConfigurationList() != null) {
			for (AdapterConfiguration adapterConfiguration : configBean.getAdaptersConfigurationList()) {					
				AdapterConfiguration entry = new AdapterConfiguration();
				entry.setClassName(adapterConfiguration.getClassName());				
				entry.setId(adapterConfiguration.getId());				
				copyConfigs(adapterConfiguration, entry);
				
				if (configBean.getGlobalConfigurationParams()!= null) {
					for (String key : configBean.getGlobalConfigurationParams().getParameters().keySet()) {					
						String valFromAdapterConfig = entry.getConfiguration().getParameterValue(key);
						if (valFromAdapterConfig == null ) {
							entry.getConfiguration().addParameter(key, configBean.getGlobalConfigurationParams().getParameterValue(key));
						}
					}				
				}
				resultList.add(entry);
			}
		}
		return resultList;
	}


	public ClientConfiguration getClientConfiguration() {		
		ClientConfiguration result = new ClientConfiguration();
		if (configBean.getClientConfiguration().getConfiguration() != null && configBean.getGlobalConfigurationParams() != null) {
			result.setId(configBean.getClientConfiguration().getId());
			result.setRunTime(configBean.getClientConfiguration().getRunTime());
			result.setThreadCount(configBean.getClientConfiguration().getThreadCount());
			result.setThreadExecDelay(configBean.getClientConfiguration().getThreadExecDelay());
			copyConfigs(configBean.getClientConfiguration(), result);
			
			for (String key : configBean.getGlobalConfigurationParams().getParameters().keySet()) {
				String valFromClientConfig = result.getConfiguration().getParameterValue(key);
				if (valFromClientConfig == null) {
					result.getConfiguration().addParameter(key, configBean.getGlobalConfigurationParams().getParameterValue(key));
				}
			}
		}	
		return result;
	}

	
	public ArrayList<ExceptionHandlerConfiguration> getExceptionHandlerList() {
		return configBean.getExceptionHandlerConfigurationList();
	}


	public ArrayList<ExecutionUnitConfiguration> getExecutionUnitList() {		
		return getExecutionUnitList(configBean.getExecutionUnitConfigurationList());
		/*
		ArrayList<ExecutionUnitConfiguration> resultList = new ArrayList<ExecutionUnitConfiguration>();
		if (configBean.getExecutionUnitConfigurationList()!= null) {								
			for (ExecutionUnitConfiguration euConfig : configBean.getExecutionUnitConfigurationList()) {
				ExecutionUnitConfiguration entry = new ExecutionUnitConfiguration();
				entry.setAdapterId(euConfig.getAdapterId());
				entry.setBeansFactoryClass(euConfig.getBeansFactoryClass());
				entry.setClassName(euConfig.getClassName());
				entry.setDescription(euConfig.getDescription());
				entry.setRepeat(euConfig.getRepeat());
				copyConfigs(euConfig, entry);
				
				if (configBean.getGlobalConfigurationParams() != null) {
					for (String key : configBean.getGlobalConfigurationParams().getParameters().keySet()) {						
						String valFromEU = entry.getConfiguration().getParameterValue(key);
						if (valFromEU == null) {
							entry.getConfiguration().addParameter(key, configBean.getGlobalConfigurationParams().getParameterValue(key));
						}
					}				
				}
				resultList.add(entry);	
			}				
		}
		return resultList;
		*/
	}
	
	private ArrayList<ExecutionUnitConfiguration> getExecutionUnitList(ArrayList<ExecutionUnitConfiguration> list) {		
		ArrayList<ExecutionUnitConfiguration> resultList = new ArrayList<ExecutionUnitConfiguration>();
		if (list != null) {								
			for (ExecutionUnitConfiguration euConfig : list) {
				ExecutionUnitConfiguration entry = new ExecutionUnitConfiguration();
				entry.setAdapterId(euConfig.getAdapterId());
				entry.setBeansFactoryClass(euConfig.getBeansFactoryClass());
				entry.setClassName(euConfig.getClassName());
				entry.setDescription(euConfig.getDescription());
				entry.setRepeat(euConfig.getRepeat());
				copyConfigs(euConfig, entry);
				
				if (configBean.getGlobalConfigurationParams() != null) {
					for (String key : configBean.getGlobalConfigurationParams().getParameters().keySet()) {						
						String valFromEU = entry.getConfiguration().getParameterValue(key);
						if (valFromEU == null) {
							entry.getConfiguration().addParameter(key, configBean.getGlobalConfigurationParams().getParameterValue(key));
						}
					}				
				}
				resultList.add(entry);	
			}				
		}
		return resultList;
	}
	
	public ArrayList<ExecutionUnitStackConfiguration> getExecutionUnitStackList() {		
		ArrayList<ExecutionUnitStackConfiguration> resultList = new ArrayList<ExecutionUnitStackConfiguration>();
		if (configBean.getExecutionUnitStackList()!= null) {								
			for (ExecutionUnitStackConfiguration stackConfig : configBean.getExecutionUnitStackList()) {
				ExecutionUnitStackConfiguration entry = new ExecutionUnitStackConfiguration();								
				entry.setDescription(stackConfig.getDescription());
				entry.setRepeat(stackConfig.getRepeat());
				copyConfigs(stackConfig, entry);
				
				if (configBean.getGlobalConfigurationParams() != null) {
					for (String key : configBean.getGlobalConfigurationParams().getParameters().keySet()) {						
						String valFromStack = entry.getConfiguration().getParameterValue(key);
						if (valFromStack == null) {
							entry.getConfiguration().addParameter(key, configBean.getGlobalConfigurationParams().getParameterValue(key));
						}
					}				
				}
				ArrayList<ExecutionUnitConfiguration> stackEUs = getExecutionUnitList(stackConfig.getStackExecutionUnitsList());				
				entry.setStackExecutionUnitsList(stackEUs);
				resultList.add(entry);
				
			}			
		}
		return resultList;
	}	
	public static void save(AcmeEnvConfigBean bean, String fileName) throws JAXBException, FileNotFoundException {
		JAXBContext jc = JAXBContext.newInstance("com.ibm.wssvt.acme.common.envconfig");
		Marshaller m =  jc.createMarshaller();		
		m.setProperty("jaxb.formatted.output", Boolean.TRUE);
		m.marshal(bean, new java.io.FileOutputStream(fileName));	
	}
	
	private void copyConfigs(Configrable<String, String>in, Configrable<String, String> out) {
		if (in!= null && in.getConfiguration()!=null && out!=null && out.getConfiguration()!=null){
			for (String key : in.getConfiguration().getParameters().keySet()) {
				out.getConfiguration().addParameter(key, in.getConfiguration().getParameterValue(key));
			}
		}
	}
	
	public static void main(String[] args) throws Exception{		
		try {
		
			JAXBContext jc = JAXBContext.newInstance("com.ibm.wssvt.acme.common.envconfig");
			Marshaller m = jc.createMarshaller();
			configBean = new AcmeEnvConfigBean();
			EnvConfigStringParameterizable globalConfigs = new EnvConfigStringParameterizable();
			globalConfigs.addParameter("globalConfigKey1", "value1");
			globalConfigs.addParameter("globalConfigKey2", "value2");
			configBean.setGlobalConfigurationParams(globalConfigs);
			
			ClientConfiguration cc = new ClientConfiguration();
			cc.setId("123");
			EnvConfigStringParameterizable configs = new EnvConfigStringParameterizable();
			configs.addParameter("testKey", "TestValue");
			configs.addParameter("Key2", "value2");
			cc.setConfiguration(configs);												
			configBean.setClientConfiguration(cc);
			
			AdapterConfiguration adapterConfiguration = new AdapterConfiguration();
			adapterConfiguration.setConfiguration(configs);
			adapterConfiguration.setId("1");
			adapterConfiguration.setClassName("EJB_ADAPTER");
			configBean.getAdaptersConfigurationList().add(adapterConfiguration);
			
			AdapterConfiguration adapterConfig2 = new AdapterConfiguration();
			adapterConfig2.setConfiguration(configs);
			adapterConfig2.setId("1");
			adapterConfig2.setClassName("EJB_ADAPTER");
			configBean.getAdaptersConfigurationList().add(adapterConfig2);
			
			ExecutionUnitConfiguration eu1 = new ExecutionUnitConfiguration();
			eu1.setAdapterId("1");
			eu1.setBeansFactoryClass("JAXB");
			eu1.setClassName("com.ibm.MyClass");
			eu1.setConfiguration(configs);
			eu1.setDescription("EU Description1");
			eu1.setRepeat(1);
			configBean.getExecutionUnitConfigurationList().add(eu1);
			
			ExecutionUnitConfiguration eu2 = new ExecutionUnitConfiguration();
			eu2.setAdapterId("1");
			eu2.setBeansFactoryClass("JAXB");
			eu2.setClassName("com.ibm.MyClass");
			eu2.setConfiguration(configs);
			eu2.setDescription("EU Description2");
			eu2.setRepeat(1);
			configBean.getExecutionUnitConfigurationList().add(eu2);
			
			ExceptionHandlerConfiguration ex1 = new ExceptionHandlerConfiguration();
			ex1.setHandlerClassName("COUNT");
			ex1.setExceptionClassName("com.FooException");
			ex1.setConfiguration(configs);
			configBean.getExceptionHandlerConfigurationList().add(ex1);
			
			ExceptionHandlerConfiguration ex2 = new ExceptionHandlerConfiguration();
			ex2.setHandlerClassName("COUNT");
			ex2.setExceptionClassName("com.FooException");
			ex2.setConfiguration(configs);
			configBean.getExceptionHandlerConfigurationList().add(ex2);			
			configBean.setGlobalConfigurationParams(configs);
			
			m.marshal(configBean, new java.io.FileOutputStream("c:\\temp\\AcmeEnvConfig.xml"));
			
			//configBean = null;
			
			Unmarshaller um =  jc.createUnmarshaller();
			configBean = (AcmeEnvConfigBean) um.unmarshal(new File("c:\\temp\\AcmeEnvConfig.xml"));
			configBean.getExecutionUnitConfigurationList();
			m.setProperty("jaxb.formatted.output", Boolean.TRUE);
			m.marshal(configBean, System.out);
			m.marshal(configBean, new java.io.FileOutputStream("c:\\temp\\AcmeEnvConfig.xml"));
			
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}	
	
}
