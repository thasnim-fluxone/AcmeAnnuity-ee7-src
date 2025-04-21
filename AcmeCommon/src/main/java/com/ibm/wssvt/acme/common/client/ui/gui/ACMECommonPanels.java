package com.ibm.wssvt.acme.common.client.ui.gui;

import javax.swing.JPanel;

public enum ACMECommonPanels {
	STARTUP_PANEL (new StartupPanel()),
	CLIENT_CONFIG_PANEL (new ClientConfigPanel()),
	ADAPTERS_PANEL (new AdaptersPanel()),
	EXECUTION_UNITS_PANEL (new ExecutionUnitPanel()),
	EXCEPTION_HANDLERS_PANEL (new ExceptionHandlerPanel()),
	GLOBAL_PARAMS_PANEL (new GlobalParamsPanel()),
	ACTIONS_PANEL (new ActionsPanel());
	
	private JPanel panel;
	ACMECommonPanels(JPanel panel){
		this.panel = panel;
	}
	
	public JPanel getPanel(){
		return this.panel;
	}
}
