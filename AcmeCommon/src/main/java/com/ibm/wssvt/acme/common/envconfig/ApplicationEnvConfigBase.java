package com.ibm.wssvt.acme.common.envconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.wssvt.acme.common.adapter.IServerAdapter;
import com.ibm.wssvt.acme.common.bean.IBeansFactory;
import com.ibm.wssvt.acme.common.envconfig.exception.ConfigurationException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitStack;
import com.ibm.wssvt.acme.common.executionunit.IExecutionUnit;
import com.ibm.wssvt.acme.common.executionunit.IExecutionUnitStack;
import com.ibm.wssvt.acme.common.executionunit.ISingleRunExecutionUnit;
import com.ibm.wssvt.acme.common.executionunit.IStackableExecutionUnit;


public class ApplicationEnvConfigBase implements IApplicationEnvConfig {	
	private static ApplicationEnvConfigBase instance =null;
	private AcmeEnvConfigLoader configLoader;
	private Map<String, IServerAdapter> adapters = new HashMap<String, IServerAdapter>();
	private List<IExecutionUnit> executionUnits = new ArrayList<IExecutionUnit>();
	private List<IExecutionUnitStack> executionUnitStacks = new ArrayList<IExecutionUnitStack>();
	private Map<String, ExceptionHandlerConfiguration> exceptionHandlers = new HashMap<String, ExceptionHandlerConfiguration>();
	private ClientConfiguration clientConfiguration = new ClientConfiguration();
		
	public static  ApplicationEnvConfigBase init(String fileName) throws ConfigurationException {
		instance = new  ApplicationEnvConfigBase(fileName);		
		return instance;		
	}

	public static synchronized ApplicationEnvConfigBase getInstance() throws ConfigurationException {
		if (instance == null) {
			throw new ConfigurationException ("ApplicationEnvConfigBase Loader has not been initialized.  " +
					"Caller must use ApplicationEnvConfigBase.init(String fileName) first " +
					"before calling the getInstance() or any other method.");
		}
		return instance;
	}	
	
	
	
	private ApplicationEnvConfigBase(String fileName) throws ConfigurationException {
		configLoader = AcmeEnvConfigLoader.init(fileName);		
		setClientConfigurations();
		setAdapters();
		setExecutionUnits();		
		setExceptionHandlers();
		setExecutionUnitStacks();
	}
	
	
	private void setExecutionUnitStacks() throws ConfigurationException {
		for (ExecutionUnitStackConfiguration stackConfig : configLoader.getExecutionUnitStackList()) {			
			for (int i = 0; i< stackConfig.getRepeat() ; i++){
				IExecutionUnitStack stack = new ExecutionUnitStack();
				stack.setDescription(stackConfig.getDescription());
				// - copy the configs one at a time - dont use the map b/c that will be shallow copy, and we need deep copy.
				for (String key :stackConfig.getConfiguration().getParameters().keySet()){
					stack.getConfiguration().addParameter(key, stackConfig.getConfiguration().getParameterValue(key));	
				}
				List<IExecutionUnit> stackEUs = new ArrayList<IExecutionUnit>();
				for (ExecutionUnitConfiguration euConfig: stackConfig.getStackExecutionUnitsList()){
					for (int j=0; j<euConfig.getRepeat(); j++){
						IExecutionUnit eu;
						try {
							eu = buildExecutionUnit(euConfig);
							if (!(eu instanceof IStackableExecutionUnit)){
								throw new ConfigurationException("Configuration Error.  The Execution Unit Stack has an Execution Unit that is not a type of IStackableExecutionUnit"
										+ ".  Stack description is: " + stackConfig.getDescription()
										+ ".  Execution Unit Description is: " + eu.getDescription()
										+ ".  You must correct this error first. (you must remove this ExecutionUnit from the ExecutionUnitStack)");
							}							
						} catch (IllegalAccessException e) {
							throw new ConfigurationException (e.getMessage(),e.getCause());
						} catch (InstantiationException e) {
							throw new ConfigurationException (e.getMessage(),e.getCause());
						} catch (ClassNotFoundException e) {
							throw new ConfigurationException (e.getMessage(),e.getCause());
						}
						stackEUs.add(eu);
					}
					
				}
				stack.setStackExecutionUnits(stackEUs);
				this.executionUnitStacks.add(stack);
			}			
		}
		
	}

	private void setClientConfigurations() throws ConfigurationException {
		this.clientConfiguration = configLoader.getClientConfiguration();
		if (this.clientConfiguration.getThreadCount()<=0){
			throw new ConfigurationException ("Client Configuration threadCount attribute is invalid. must be > 0");
		}
		if (this.clientConfiguration.getRunTime() < 0){
			throw new ConfigurationException ("Client Configuration runTime attribute is invalid. must be >= 0");
		}
		if (this.clientConfiguration.getId() == null || this.clientConfiguration.getId().trim().length() <1){
			this.clientConfiguration.setId("" + System.currentTimeMillis());
		}
	}

	private void setExceptionHandlers() {
		for (ExceptionHandlerConfiguration exConfig : configLoader.getExceptionHandlerList()) {			
			exceptionHandlers.put(exConfig.getExceptionClassName(), exConfig);
		}
		
	}
	
	private void setExecutionUnits() throws  ConfigurationException {
		for (ExecutionUnitConfiguration euConfig : configLoader.getExecutionUnitList()) {			
			for (int i = 0; i< euConfig.getRepeat() ; i++){
				IExecutionUnit executionUnit;
				try {
					executionUnit = buildExecutionUnit(euConfig);
					if (executionUnit instanceof ISingleRunExecutionUnit ){
						// for this , repeat must be 1 only.
						if (euConfig.getRepeat() > 1){
							throw new ConfigurationException("Invalid ExecutionUnit Configuration. Repeat value is not 1." +
									"  The Scenario is an instance of a SingleRunScenario." +
									"  Such scenarios must have repeat attribute set to 1." +
									"  Current value is set to: " + euConfig.getRepeat() +
									".  The scenario description is: " + euConfig.getDescription());
						}
					}				
					//executionUnit.getConfiguration().addParameter("internal.beansFactoryClass", euConfig.getBeansFactoryClass());
					if (executionUnit instanceof IStackableExecutionUnit){
						throw new ConfigurationException("Configuration Error.  Found an Execution Unit that is instance of IStackableExecutionUnit, but not part of an ExecutionUnitStack"						
								+ ".  Execution Unit Description is: " + executionUnit.getDescription()
								+ ".  You must correct this error first (either remove the execution unit, or add it to an ExecutionUnitStack)");
					}		
					executionUnits.add(executionUnit);
				} catch (IllegalAccessException e) {
					throw new ConfigurationException ("IllegalAccessException to: " + e.getMessage(),e.getCause());
				} catch (InstantiationException e) {
					throw new ConfigurationException ("InstantiationException for: " + e.getMessage(),e.getCause());
				} catch (ClassNotFoundException e) {
					throw new ConfigurationException ("ClassNotFoundException.  Missing class: " + e.getMessage(),e.getCause());
				}
			}
		}
		
		
	}

	
	private IExecutionUnit buildExecutionUnit(ExecutionUnitConfiguration euConfig) 
	throws IllegalAccessException, InstantiationException, ClassNotFoundException, ConfigurationException {		
		IServerAdapter iServerAdapter = adapters.get(euConfig.getAdapterId());
		if (iServerAdapter == null) {
			throw new ConfigurationException("invalid adapter id used for scenario with description: " 
					+ euConfig.getDescription());
		}
		IBeansFactory beansFactory = (IBeansFactory) Thread.currentThread().getContextClassLoader().loadClass(euConfig.getBeansFactoryClass()).newInstance();
		IExecutionUnit executionUnit = (IExecutionUnit) Thread.currentThread().getContextClassLoader().loadClass(euConfig.getClassName()).newInstance();		
		executionUnit.setServerAdapter(iServerAdapter);
		executionUnit.setBeansFactory(beansFactory);
		executionUnit.setDescription(euConfig.getDescription());
		executionUnit.setConfiguration(beansFactory.createParameterizable());				
		executionUnit.getConfiguration().addAllParams(euConfig.getConfiguration().getParameters());		
		executionUnit.getConfiguration().addParameter("internal.beansFactoryClass", euConfig.getBeansFactoryClass());
		return executionUnit;

	}
	
	private void setAdapters() throws ConfigurationException  {
		for (AdapterConfiguration adapterCfg : configLoader.getAdaptersList()) {			
			try{				
				IServerAdapter iServerAdapter = (IServerAdapter)Thread.currentThread().getContextClassLoader().loadClass(adapterCfg.getClassName()).newInstance();
				iServerAdapter.setConfiguration(adapterCfg.getConfiguration());
				iServerAdapter.setId(adapterCfg.getId());
				adapters.put(adapterCfg.getId(), iServerAdapter);
			}catch (IllegalArgumentException e) {
				throw new ConfigurationException ("Invalid adapter type at adapter id=" 
						+ adapterCfg.getId() + ".  Make sure the adapter confirms to " 
						+"class com.ibm.wssvt.acme.annuity.client.adapter.ServerAdapterType");
				
			} catch (IllegalAccessException e) {
				throw new ConfigurationException ("Failed to load ServerAdapter.  Error: " + e);
			} catch (InstantiationException e) {
				throw new ConfigurationException ("Failed to load ServerAdapter.  Error: " + e);
			} catch (ClassNotFoundException e) {
				throw new ConfigurationException ("Failed to load ServerAdapter.  Error: " + e);
			}
		}		
	}
	

	public Map<String,ExceptionHandlerConfiguration> getExceptionHandlers(){
		return this.exceptionHandlers;
	}
	
	public ClientConfiguration getClientConfiguration(){
		return this.clientConfiguration;
		
	}
	
	public int getExecutionUnitsCount() {
		return this.executionUnits.size();
	}

	public int getAdaptersCount() {
		return this.adapters.size();
	}
	
	private IExecutionUnit copyExecutionUnit(IExecutionUnit executionUnit, Map<String, IServerAdapter> localAdapersCacheMap) throws IllegalAccessException, InstantiationException{
		IExecutionUnit newEU = executionUnit.getClass().newInstance();
		newEU.setDescription(new String(executionUnit.getDescription()));				
		newEU.setBeansFactory(executionUnit.getBeansFactory().getClass().newInstance());
		newEU.setConfiguration(executionUnit.getBeansFactory().createParameterizable());				
		// - copy the configs one at a time - dont use the map b/c that will be shallow copy, and we need deep copy.
		for (String key :executionUnit.getConfiguration().getParameters().keySet()){
			newEU.getConfiguration().addParameter(key, executionUnit.getConfiguration().getParameterValue(key));	
		}

		// the adapter
		IServerAdapter mainAdapter = adapters.get(executionUnit.getServerAdapter().getId());
		IServerAdapter adapterCopy = localAdapersCacheMap.get(mainAdapter.getId());

		if (adapterCopy == null) {
			adapterCopy = mainAdapter.getClass().newInstance();
			for (String key :mainAdapter.getConfiguration().getParameters().keySet()){
				adapterCopy.getConfiguration().addParameter(key, mainAdapter.getConfiguration().getParameterValue(key));	
			}
			adapterCopy.setId(mainAdapter.getId());					
			localAdapersCacheMap.put(adapterCopy.getId(), adapterCopy);				
		}									
		newEU.setServerAdapter(adapterCopy);
		return newEU;
	}
	public synchronized List<IExecutionUnit> getExecutionUnits() throws ConfigurationException  {
		List<IExecutionUnit> results = new ArrayList<IExecutionUnit>(executionUnits.size());
		Map<String, IServerAdapter> localAdapersCacheMap = new HashMap<String, IServerAdapter>(adapters.size());
        		        
		for (IExecutionUnit executionUnit : executionUnits) {
			try {
				IExecutionUnit newEU = copyExecutionUnit(executionUnit, localAdapersCacheMap);
				results.add(newEU);
			} catch (IllegalAccessException e) {
	    		throw new ConfigurationException("Failed to make a deep copy of the scenarios list." +
	    				"  Make sure that the system has enough space on the drives.  Reported error: " + e.getMessage(), e);
			} catch (InstantiationException e) {
	    		throw new ConfigurationException("Failed to make a deep copy of the scenarios list." +
	    				"  Make sure that the system has enough space on the drives.  Reported error: " + e.getMessage(), e);
			}
		}
		return results;
	}

	@Override
	public synchronized List<IExecutionUnitStack> getExecutionUnitStacks()
			throws ConfigurationException {
		List<IExecutionUnitStack> results = new ArrayList<IExecutionUnitStack>(executionUnitStacks.size());		        		       
		for (IExecutionUnitStack euStack : executionUnitStacks) {
			try {
				IExecutionUnitStack stackCopy = new ExecutionUnitStack();
				stackCopy.setDescription(euStack.getDescription());
				// - copy the configs one at a time - dont use the map b/c that will be shallow copy, and we need deep copy.
				for (String key :euStack.getConfiguration().getParameters().keySet()){
					stackCopy.getConfiguration().addParameter(key, euStack.getConfiguration().getParameterValue(key));	
				}
				List<IExecutionUnit> stackEUs = new ArrayList<IExecutionUnit>(); 
				Map<String, IServerAdapter> localAdapersCacheMap = new HashMap<String, IServerAdapter>(adapters.size());
				for (IExecutionUnit eu: euStack.getStackExecutionUnits()){
					IExecutionUnit newEU = copyExecutionUnit(eu, localAdapersCacheMap);
					stackEUs.add(newEU);					
				}
				stackCopy.setStackExecutionUnits(stackEUs);
				results.add(stackCopy);
			} catch (IllegalAccessException e) {
	    		throw new ConfigurationException("Failed to make a deep copy of the scenarios list." +
	    				"  Make sure that the system has enough space on the drives.  Reported error: " + e.getMessage(), e);
			} catch (InstantiationException e) {
	    		throw new ConfigurationException("Failed to make a deep copy of the scenarios list." +
	    				"  Make sure that the system has enough space on the drives.  Reported error: " + e.getMessage(), e);
			}
		}
		return results;
	}

	@Override
	public int getExecutionUnitStacksCount() {
		return this.executionUnitStacks.size();
	}

	
	
}
