package com.ibm.wssvt.acme.common.client.ui.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.wssvt.acme.common.bean.StringConfigrable;
import com.ibm.wssvt.acme.common.client.ui.gui.model.AdapterConfigurationDecorator;
import com.ibm.wssvt.acme.common.client.ui.gui.model.ExceptionHandlerConfigurationDecorator;
import com.ibm.wssvt.acme.common.client.ui.gui.model.ExecutionUnitConfigurationDecorator;
import com.ibm.wssvt.acme.common.client.ui.gui.model.IAdapterBeansFactoryMap;
import com.ibm.wssvt.acme.common.client.ui.gui.model.ParametarizableCopier;
import com.ibm.wssvt.acme.common.envconfig.AcmeEnvConfigBean;
import com.ibm.wssvt.acme.common.envconfig.AcmeEnvConfigLoader;
import com.ibm.wssvt.acme.common.envconfig.AdapterConfiguration;
import com.ibm.wssvt.acme.common.envconfig.ClientConfiguration;
import com.ibm.wssvt.acme.common.envconfig.EnvConfigStringParameterizable;
import com.ibm.wssvt.acme.common.envconfig.ExceptionHandlerConfiguration;
import com.ibm.wssvt.acme.common.envconfig.ExecutionUnitConfiguration;

public class AcmeGUI {
	
	private static AcmeEnvConfigBean templateDocument = new AcmeEnvConfigBean();
	private static AcmeEnvConfigBean configuredDocument = new AcmeEnvConfigBean();
	
	private static StringConfigrable templateGlobalParams = new StringConfigrable();
	private static StringConfigrable configuredGlobalParams = new StringConfigrable();
	private static ClientConfiguration templateClientConfig = new ClientConfiguration();
	private static ClientConfiguration configuredClientConfig = new ClientConfiguration();
	private static Vector<AdapterConfigurationDecorator> templateAdaptersDataModel = new Vector<AdapterConfigurationDecorator>();
	private static Vector<AdapterConfigurationDecorator> configuredAdaptersDataModel = new Vector<AdapterConfigurationDecorator>();
	private static Vector<ExecutionUnitConfigurationDecorator> templateExecutionUnitsDataModel = new Vector<ExecutionUnitConfigurationDecorator>();
	private static Vector<ExecutionUnitConfigurationDecorator> configuredExecutionUnitsDataModel = new Vector<ExecutionUnitConfigurationDecorator>();
	private static Vector<ExceptionHandlerConfigurationDecorator> templateExceptionHandlersDataModel = new Vector<ExceptionHandlerConfigurationDecorator>();
	private static Vector<ExceptionHandlerConfigurationDecorator> configuredExceptionHandlersDataModel = new Vector<ExceptionHandlerConfigurationDecorator>();
	
	private static IAdapterBeansFactoryMap adapterBeansFactoryMap = null;
	

	
	public static IAdapterBeansFactoryMap getAdapterBeansFactoryMap() {
		return adapterBeansFactoryMap;
	}

	public static void setAdapterBeansFactoryMap(
			IAdapterBeansFactoryMap adapterBeansFactoryMap) {
		AcmeGUI.adapterBeansFactoryMap = adapterBeansFactoryMap;
	}

	public static void reset(){
		setTemplateGlobalParams(new StringConfigrable());
		setConfiguredGlobalParams(new StringConfigrable());
		
		setTemplateClientConfig(new ClientConfiguration());
		setConfiguredClientConfig(new ClientConfiguration());
		
		setTemplateAdaptersDataModel(new Vector<AdapterConfigurationDecorator>());
		setSelectedAdaptersDataModel(new Vector<AdapterConfigurationDecorator>());
		
		setTemplateExecutionUnitsDataModel(new Vector<ExecutionUnitConfigurationDecorator>());
		setConfiguredExecutionUnitsDataModel(new Vector<ExecutionUnitConfigurationDecorator>());
		
		setTemplateExceptionHandlersDataModel(new Vector<ExceptionHandlerConfigurationDecorator>());
		setConfiguredExceptionHandlersDataModel(new Vector<ExceptionHandlerConfigurationDecorator>());
				
		setConfiguredDocument(new AcmeEnvConfigBean());
		
	}
	
	public static void launchApp(final List<JPanel> panels, IAdapterBeansFactoryMap adapterBeansFactoryMap){		
		if (adapterBeansFactoryMap == null) {
			throw new IllegalArgumentException("The object adapterBeansFactoryMap is null");
		}
		setAdapterBeansFactoryMap(adapterBeansFactoryMap);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (panels == null){
					List<JPanel> acmeCommonOnlyPanels = new ArrayList<JPanel>();
					acmeCommonOnlyPanels.add(ACMECommonPanels.STARTUP_PANEL.getPanel());
					acmeCommonOnlyPanels.add(ACMECommonPanels.CLIENT_CONFIG_PANEL.getPanel());
					acmeCommonOnlyPanels.add(ACMECommonPanels.ADAPTERS_PANEL.getPanel());
					acmeCommonOnlyPanels.add(ACMECommonPanels.EXECUTION_UNITS_PANEL.getPanel());
					acmeCommonOnlyPanels.add(ACMECommonPanels.EXCEPTION_HANDLERS_PANEL.getPanel());
					acmeCommonOnlyPanels.add(ACMECommonPanels.GLOBAL_PARAMS_PANEL.getPanel());
					acmeCommonOnlyPanels.add(ACMECommonPanels.ACTIONS_PANEL.getPanel());
					MainFrame mainFrame = new MainFrame(acmeCommonOnlyPanels);
					mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					mainFrame.setVisible(true);	
				}else{
					MainFrame mainFrame = new MainFrame(panels);
					mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					mainFrame.setVisible(true);
				}
				
			}
		});
	}
	
	/*public static void main(String[] args) {		
		init();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainFrame mainFrame = new MainFrame();
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.setVisible(true);
			}
		});		
	}
	*/
	private static boolean loadXML(String fileName, boolean config) {		
		try {
			AcmeEnvConfigLoader.init(fileName);
			setTemplateDocument(AcmeEnvConfigLoader.getInstance().getConfigBean());
		} catch (Exception e) {			
			e.printStackTrace();
			return false;
		}
		reset();
		getTemplateGlobalParams().setConfiguration(getTemplateDocument().getGlobalConfigurationParams());
		getConfiguredGlobalParams().setConfiguration(ParametarizableCopier.getCopy(getTemplateGlobalParams().getConfiguration(), new EnvConfigStringParameterizable()));

		// copy xml to template
		getTemplateClientConfig().setId(getTemplateDocument().getClientConfiguration().getId());
		getTemplateClientConfig().setRunTime(getTemplateDocument().getClientConfiguration().getRunTime());
		getTemplateClientConfig().setThreadCount(getTemplateDocument().getClientConfiguration().getThreadCount());
		getTemplateClientConfig().setThreadExecDelay(getTemplateDocument().getClientConfiguration().getThreadExecDelay());
		getTemplateClientConfig().setConfiguration(getTemplateDocument().getClientConfiguration().getConfiguration());

		// copy from template to configured.
		getConfiguredClientConfig().setId(getTemplateClientConfig().getId());
		getConfiguredClientConfig().setRunTime(getTemplateClientConfig().getRunTime());
		getConfiguredClientConfig().setThreadCount(getTemplateClientConfig().getThreadCount());
		getConfiguredClientConfig().setThreadExecDelay(getTemplateClientConfig().getThreadExecDelay());
		getConfiguredClientConfig().setConfiguration(getTemplateClientConfig().getConfiguration());
		
		for (AdapterConfiguration ac : getTemplateDocument().getAdaptersConfigurationList()) {
			AdapterConfigurationDecorator ace = new AdapterConfigurationDecorator(ac);				
			getTemplateAdaptersDataModel().add(ace);
			if(config) getSelectedAdaptersDataModel().add(ace.clone());
		}
		for (ExecutionUnitConfiguration euConfig: getTemplateDocument().getExecutionUnitConfigurationList()){
			ExecutionUnitConfigurationDecorator euce = new ExecutionUnitConfigurationDecorator(euConfig);				
			getTemplateExecutionUnitsDataModel().add(euce);
			if(config) getConfiguredExecutionUnitsDataModel().add(euce.clone());
		}
		for (ExceptionHandlerConfiguration eh: getTemplateDocument().getExceptionHandlerConfigurationList()){
			ExceptionHandlerConfigurationDecorator ehce = new ExceptionHandlerConfigurationDecorator(eh);
			getTemplateExceptionHandlersDataModel().add(ehce);
			if(config) getConfiguredExceptionHandlersDataModel().add(ehce.clone());
		}					
		return true;

	}
	
	public static boolean loadUserFile(String fileName, boolean config){				
		return loadXML(fileName, config);		
	}
	
	public static boolean  loadTemplate(){
			URL url = AcmeGUI.class.getClassLoader().getResource("ACMEClientGUITemplate.xml");
			if (url == null){
				System.out.println("Unable to find the Template!!");
				return false;
			}else{
				System.out.println("loading the template: " + url);
				return loadXML(url.toString(), false);	
			}
								
	}
	
	public static void setTemplateGlobalParams(StringConfigrable templateGlobalParams) {
		AcmeGUI.templateGlobalParams = templateGlobalParams;
	}
	public static StringConfigrable getTemplateGlobalParams() {
		return templateGlobalParams;
	}
	public static void setConfiguredGlobalParams(StringConfigrable updatedGlobalParams) {
		AcmeGUI.configuredGlobalParams = updatedGlobalParams;
	}
	public static StringConfigrable getConfiguredGlobalParams() {
		return configuredGlobalParams;
	}
	public static void setTemplateClientConfig(ClientConfiguration templateClientConfig) {
		AcmeGUI.templateClientConfig = templateClientConfig;
	}
	public static ClientConfiguration getTemplateClientConfig() {
		return templateClientConfig;
	}
	public static void setConfiguredClientConfig(ClientConfiguration updatedClientConfig) {
		AcmeGUI.configuredClientConfig = updatedClientConfig;
	}
	public static ClientConfiguration getConfiguredClientConfig() {
		return configuredClientConfig;
	}
	public static void setTemplateAdaptersDataModel(
			Vector<AdapterConfigurationDecorator> templateAdaptersDataModel) {
		AcmeGUI.templateAdaptersDataModel = templateAdaptersDataModel;
	}
	public static Vector<AdapterConfigurationDecorator> getTemplateAdaptersDataModel() {
		return templateAdaptersDataModel;
	}
	public static void setSelectedAdaptersDataModel(
			Vector<AdapterConfigurationDecorator> selectedAdaptersDataModel) {
		AcmeGUI.configuredAdaptersDataModel = selectedAdaptersDataModel;
	}
	public static Vector<AdapterConfigurationDecorator> getSelectedAdaptersDataModel() {
		return configuredAdaptersDataModel;
	}
	public static void setTemplateExecutionUnitsDataModel(
			Vector<ExecutionUnitConfigurationDecorator> templateExecutionUnitsDataModel) {
		AcmeGUI.templateExecutionUnitsDataModel = templateExecutionUnitsDataModel;
	}
	public static Vector<ExecutionUnitConfigurationDecorator> getTemplateExecutionUnitsDataModel() {
		return templateExecutionUnitsDataModel;
	}
	public static void setConfiguredExecutionUnitsDataModel(
			Vector<ExecutionUnitConfigurationDecorator> configuredExecutionUnitsDataModel) {
		AcmeGUI.configuredExecutionUnitsDataModel = configuredExecutionUnitsDataModel;
	}
	public static Vector<ExecutionUnitConfigurationDecorator> getConfiguredExecutionUnitsDataModel() {
		return configuredExecutionUnitsDataModel;
	}
	public static void setTemplateExceptionHandlersDataModel(
			Vector<ExceptionHandlerConfigurationDecorator> templateExceptionHandlersDataModel) {
		AcmeGUI.templateExceptionHandlersDataModel = templateExceptionHandlersDataModel;
	}
	public static Vector<ExceptionHandlerConfigurationDecorator> getTemplateExceptionHandlersDataModel() {
		return templateExceptionHandlersDataModel;
	}
	public static void setConfiguredExceptionHandlersDataModel(
			Vector<ExceptionHandlerConfigurationDecorator> configuredExceptionHandlersDataModel) {
		AcmeGUI.configuredExceptionHandlersDataModel = configuredExceptionHandlersDataModel;
	}
	public static Vector<ExceptionHandlerConfigurationDecorator> getConfiguredExceptionHandlersDataModel() {
		return configuredExceptionHandlersDataModel;
	}
	public static void setTemplateDocument(AcmeEnvConfigBean templateDocument) {
		AcmeGUI.templateDocument = templateDocument;
	}
	public static AcmeEnvConfigBean getTemplateDocument() {
		return templateDocument;
	}
	public static void setConfiguredDocument(AcmeEnvConfigBean configuredDocument) {
		AcmeGUI.configuredDocument = configuredDocument;
	}
	public static AcmeEnvConfigBean getConfiguredDocument() {
		return configuredDocument;
	}
}
