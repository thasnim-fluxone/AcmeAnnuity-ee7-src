package com.ibm.wssvt.acme.common.client.ui.gui;

import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ibm.wssvt.acme.common.bean.StringConfigrable;

public class GlobalParamsPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private JButton btnUpdateGlobalParams = null;	
	private JLabel jLabel9 = null;	
	private JButton btnUpdateClientLoggingParams = null;
	private JButton btnUpdateServerLoggingParams = null;
	/**
	 * This is the default constructor
	 */
	public GlobalParamsPanel() {
		super();
		initialize();	
	
	}
	
	private void initialize() {
		this.setName("Global Params");
		this.setSize(427, 424);
		jLabel9 = new JLabel();
		jLabel9.setBounds(new Rectangle(33, 12, 180, 16));
		jLabel9.setText("Modify the Global Params");	
		this.setLayout(null);
		this.add(getBtnUpdateGlobalParams(), null);
		this.add(jLabel9, null);
		this.add(getBtnUpdateClientLoggingParams(), null);
		this.add(getBtnUpdateServerLoggingParams(), null);
		
	}
	

	private JButton getBtnUpdateGlobalParams() {
		if (btnUpdateGlobalParams == null) {
			btnUpdateGlobalParams = new JButton();
			btnUpdateGlobalParams.setToolTipText("Edit parameters to be used globally by the client.");
			btnUpdateGlobalParams.setBounds(new Rectangle(52, 128, 223, 27));
			btnUpdateGlobalParams.setText("Update All Global Params");
			btnUpdateGlobalParams.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					PropertiesDialog pd = new PropertiesDialog(AcmeGUI.getConfiguredGlobalParams());
					pd.setVisible(true);	
				}
			});
		}
		return btnUpdateGlobalParams;
	}


	private JButton getBtnUpdateClientLoggingParams() {
		if (btnUpdateClientLoggingParams == null) {
			btnUpdateClientLoggingParams = new JButton();
			btnUpdateClientLoggingParams.setToolTipText("Edit parameters related to client side logging.");
			btnUpdateClientLoggingParams.setBounds(new Rectangle(52, 42, 219, 29));
			btnUpdateClientLoggingParams.setText("Update Client Logging Params");
			btnUpdateClientLoggingParams
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {							
							StringConfigrable logsParams = new StringConfigrable();
							logsParams.getConfiguration().addParameter("client.log.refresh" , 
									AcmeGUI.getConfiguredGlobalParams().getConfiguration().getParameterValue("client.log.refresh"));
							logsParams.getConfiguration().addParameter("client.log.useParent" , 
									AcmeGUI.getConfiguredGlobalParams().getConfiguration().getParameterValue("client.log.useParent"));
							logsParams.getConfiguration().addParameter("client.log.maxBytes",  
									AcmeGUI.getConfiguredGlobalParams().getConfiguration().getParameterValue("client.log.maxBytes"));
							logsParams.getConfiguration().addParameter("client.log.maxCount",  
									AcmeGUI.getConfiguredGlobalParams().getConfiguration().getParameterValue("client.log.maxCount"));
							logsParams.getConfiguration().addParameter("client.log.format",  
									AcmeGUI.getConfiguredGlobalParams().getConfiguration().getParameterValue("client.log.format"));
							logsParams.getConfiguration().addParameter("client.log.level",  
									AcmeGUI.getConfiguredGlobalParams().getConfiguration().getParameterValue("client.log.level"));
							logsParams.getConfiguration().addParameter("client.log.filePath",  
									AcmeGUI.getConfiguredGlobalParams().getConfiguration().getParameterValue("client.log.filePath"));
							logsParams.getConfiguration().addParameter("client.log.filePattern",  
									AcmeGUI.getConfiguredGlobalParams().getConfiguration().getParameterValue("client.log.filePattern"));
							PropertiesDialog pd = new PropertiesDialog(logsParams);
							// make sure we block until done
							pd.setModal(true);
							pd.setVisible(true);							
							AcmeGUI.getConfiguredGlobalParams().getConfiguration().addAllParams(pd.getConfigrable().getConfiguration().getParameters());
						}
					});
		}
		return btnUpdateClientLoggingParams;
	}

	/**
	 * This method initializes btnUpdateServerLoggingParams	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnUpdateServerLoggingParams() {
		if (btnUpdateServerLoggingParams == null) {
			btnUpdateServerLoggingParams = new JButton();
			btnUpdateServerLoggingParams.setToolTipText("Edit parameters related to server side logging.");
			btnUpdateServerLoggingParams.setBounds(new Rectangle(53, 80, 219, 32));
			btnUpdateServerLoggingParams.setText("Update Server Logging Params");
			btnUpdateServerLoggingParams
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							StringConfigrable logsParams = new StringConfigrable();
							logsParams.getConfiguration().addParameter("server.log.refresh" , 
									AcmeGUI.getConfiguredGlobalParams().getConfiguration().getParameterValue("server.log.refresh"));
							logsParams.getConfiguration().addParameter("server.log.useParent" , 
									AcmeGUI.getConfiguredGlobalParams().getConfiguration().getParameterValue("server.log.useParent"));
							logsParams.getConfiguration().addParameter("server.log.maxBytes",  
									AcmeGUI.getConfiguredGlobalParams().getConfiguration().getParameterValue("server.log.maxBytes"));
							logsParams.getConfiguration().addParameter("server.log.maxCount",  
									AcmeGUI.getConfiguredGlobalParams().getConfiguration().getParameterValue("server.log.maxCount"));
							logsParams.getConfiguration().addParameter("server.log.format",  
									AcmeGUI.getConfiguredGlobalParams().getConfiguration().getParameterValue("server.log.format"));
							logsParams.getConfiguration().addParameter("server.log.level",  
									AcmeGUI.getConfiguredGlobalParams().getConfiguration().getParameterValue("server.log.level"));							
							logsParams.getConfiguration().addParameter("server.log.fileNamePattern",  
									AcmeGUI.getConfiguredGlobalParams().getConfiguration().getParameterValue("server.log.fileNamePattern"));
							PropertiesDialog pd = new PropertiesDialog(logsParams);
							// make sure we block until done
							pd.setModal(true);
							pd.setVisible(true);							
							AcmeGUI.getConfiguredGlobalParams().getConfiguration().addAllParams(pd.getConfigrable().getConfiguration().getParameters());
						}
					});
		}
		return btnUpdateServerLoggingParams;
	}


}  //  @jve:decl-index=0:visual-constraint="10,10"
