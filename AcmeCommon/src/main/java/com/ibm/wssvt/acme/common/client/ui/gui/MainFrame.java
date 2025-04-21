package com.ibm.wssvt.acme.common.client.ui.gui;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame {

	private List<JPanel> panels;
	private static final long serialVersionUID = 1L;
	private JTabbedPane jTabbedPane = null;
	
	public MainFrame(List<JPanel> panels) {
		super();
		this.panels = panels;
		initialize();
	}
	private void initialize() {
		this.setSize(1076, 669);
		this.setContentPane(getJTabbedPane());
		this.setTitle("Acme Annuity Client Configuration Manager");
	}
	
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {		
			jTabbedPane = new JTabbedPane();
			jTabbedPane.setName("Acme Tabs");
			jTabbedPane.setToolTipText("Update EACH Tab from Left to Right!");			
			if (panels != null){
				for (JPanel panel : panels) {
					jTabbedPane.addTab(panel.getName(), panel);
				}
			}
			/*
			jTabbedPane.addTab("Startup", null, startupPanel, null);
			jTabbedPane.addTab("Common Data", null, commonDataPanel, null);
			jTabbedPane.addTab("Client Config", null, clientConfigPanel, null);			
			jTabbedPane.addTab("Service Adapter", null, adaptersPanel, null);						
			jTabbedPane.addTab("Execution Unit", null, executionUnitPanel, null);
			jTabbedPane.addTab("Exception Handler", null, exceptionHandlerPanel, null);						
			jTabbedPane.addTab("Global Params", null, globalParamsPanel, null);
			jTabbedPane.addTab("Actions", null, actionsPanel, null);
			*/									
			jTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {					
					if (panels != null){						
						for (JPanel panel : panels) {
							if (panel instanceof RefreshableAcmeGuiPanel){
								((RefreshableAcmeGuiPanel) panel).refresh();
							}
						}
					}
				}
			});
		}		
		return jTabbedPane;
	}	
}
