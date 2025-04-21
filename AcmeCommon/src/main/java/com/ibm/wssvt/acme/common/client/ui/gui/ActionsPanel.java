package com.ibm.wssvt.acme.common.client.ui.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.bind.JAXBException;

import com.ibm.wssvt.acme.common.client.ui.gui.model.AdapterConfigurationDecorator;
import com.ibm.wssvt.acme.common.client.ui.gui.model.ExecutionUnitConfigurationDecorator;
import com.ibm.wssvt.acme.common.client.ui.gui.model.ParametarizableCopier;
import com.ibm.wssvt.acme.common.envconfig.AcmeEnvConfigLoader;
import com.ibm.wssvt.acme.common.envconfig.AdapterConfiguration;
import com.ibm.wssvt.acme.common.envconfig.EnvConfigStringParameterizable;
import com.ibm.wssvt.acme.common.envconfig.ExceptionHandlerConfiguration;
import com.ibm.wssvt.acme.common.envconfig.ExecutionUnitConfiguration;

public class ActionsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JButton btnSaveConfigFile = null;	
	private JLabel jLabel8 = null;
	private JTextField txtFileName = null;
	private JLabel lblActionsMessage = null;
	
	public ActionsPanel() {
		super();
		initialize();
	}
	
	private void initialize() {
		this.setName("Actions");
		this.setSize(1076, 669);
		lblActionsMessage = new JLabel();
		lblActionsMessage.setBounds(new Rectangle(36, 107, 596, 16));
		lblActionsMessage.setForeground(new Color(255, 51, 51));
		lblActionsMessage.setText("");
		jLabel8 = new JLabel();
		jLabel8.setBounds(new Rectangle(36, 30, 75, 16));
		jLabel8.setText("File Name:");		
		this.setLayout(null);
		this.add(getBtnSaveConfigFile(), null);
		this.add(jLabel8, null);
		this.add(getTxtFileName(), null);
		this.add(lblActionsMessage, null);		
	}
		
	private JTextField getTxtFileName() {
		if (txtFileName == null) {
			txtFileName = new JTextField();
			txtFileName.setBounds(new Rectangle(123, 28, 338, 20));
			txtFileName.setEditable(false);
		}
		return txtFileName;
	}

	/**
	 * This method initializes btnSaveConfigFile	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnSaveConfigFile() {
		if (btnSaveConfigFile == null) {
			btnSaveConfigFile = new JButton();
			btnSaveConfigFile.setBounds(new Rectangle(475, 26, 157, 39));
			btnSaveConfigFile.setText("Save");
			btnSaveConfigFile.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (AcmeGUI.getSelectedAdaptersDataModel().isEmpty()){
						lblActionsMessage.setText("No Services/Adapters are configured.  Did NOT save!");
						lblActionsMessage.setForeground(Color.RED);
						return;
					}
					if (AcmeGUI.getConfiguredExecutionUnitsDataModel().isEmpty()){
						lblActionsMessage.setText("No Executioin Units are configured.  Did NOT save!");
						lblActionsMessage.setForeground(Color.RED);
						return;
					}
					lblActionsMessage.setText("");
					File file = GuiUtils.getSaveFile();
					if (file != null) {
						txtFileName.setText(file.getAbsolutePath());
					}else{
						lblActionsMessage.setText("user cancel!");
						lblActionsMessage.setForeground(Color.BLUE);
			        	return;	
					}
					lblActionsMessage.setText("");
					EnvConfigStringParameterizable gparams = new EnvConfigStringParameterizable();
					
					ArrayList<AdapterConfiguration> adapters = new ArrayList<AdapterConfiguration>();
					ArrayList<ExecutionUnitConfiguration> executionUnits = new ArrayList<ExecutionUnitConfiguration>();
					ArrayList<ExceptionHandlerConfiguration> exceptionHandlers = new ArrayList<ExceptionHandlerConfiguration>();
					
					AcmeGUI.getConfiguredDocument().setGlobalConfigurationParams(gparams);
					AcmeGUI.getConfiguredDocument().setClientConfiguration(AcmeGUI.getConfiguredClientConfig());
					AcmeGUI.getConfiguredDocument().setAdapterConfigurationList(adapters);
					AcmeGUI.getConfiguredDocument().setExecutionUnitConfigurationList(executionUnits);
					AcmeGUI.getConfiguredDocument().setExceptionHandlerConfigurationList(exceptionHandlers);
					
					gparams.addAllParams(AcmeGUI.getConfiguredGlobalParams().getConfiguration().getParameters());
															
					for (AdapterConfigurationDecorator selectedAdapter : AcmeGUI.getSelectedAdaptersDataModel()) {
						AdapterConfiguration ac = new AdapterConfiguration();
						ac.setClassName(selectedAdapter.getClassName());
						ac.setId(selectedAdapter.getId());
						ac.setConfiguration(ParametarizableCopier.getCopy(selectedAdapter.getConfiguration(), new EnvConfigStringParameterizable()));
						adapters.add(ac);
					}
					
					
					for (ExecutionUnitConfigurationDecorator selectedEU: AcmeGUI.getConfiguredExecutionUnitsDataModel()){
						ExecutionUnitConfiguration eu = new ExecutionUnitConfiguration();
						eu.setAdapterId(selectedEU.getAdapterId());
						eu.setBeansFactoryClass(selectedEU.getBeansFactoryClass());
						eu.setClassName(selectedEU.getClassName());
						eu.setDescription(selectedEU.getDescription());
						eu.setRepeat(selectedEU.getRepeat());
						eu.setConfiguration(ParametarizableCopier.getCopy(selectedEU.getConfiguration(), new EnvConfigStringParameterizable()));
						executionUnits.add(eu);
					}
					
					for (ExceptionHandlerConfiguration selectedEH : AcmeGUI.getConfiguredExceptionHandlersDataModel()){
						ExceptionHandlerConfiguration eh = new ExceptionHandlerConfiguration();
						eh.setExceptionClassName(selectedEH.getExceptionClassName());
						eh.setHandlerClassName(selectedEH.getHandlerClassName());
						eh.setConfiguration(ParametarizableCopier.getCopy(selectedEH.getConfiguration(), new EnvConfigStringParameterizable()));
						exceptionHandlers.add(eh);
					}
					try {										        
						AcmeEnvConfigLoader.save(AcmeGUI.getConfiguredDocument(), txtFileName.getText());
						lblActionsMessage.setText("Saved OK. You may now use this config file with the Annuity Clients.");
						lblActionsMessage.setForeground(Color.BLACK);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						lblActionsMessage.setText("error: " + e1);
						lblActionsMessage.setForeground(Color.RED);
					} catch (JAXBException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						lblActionsMessage.setText("error: " + e1);
						lblActionsMessage.setForeground(Color.RED);
					}
				}				
			});
		}
		return btnSaveConfigFile;
	}
	
}
