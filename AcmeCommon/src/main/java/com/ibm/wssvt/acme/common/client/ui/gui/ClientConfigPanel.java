package com.ibm.wssvt.acme.common.client.ui.gui;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ClientConfigPanel extends JPanel implements RefreshableAcmeGuiPanel{

	private static final long serialVersionUID = 1L;
	private JLabel lblClientConfigPrompt = null;
	private JLabel lblThreadCount = null;
	private JTextField txtThreadCount = null;
	private JLabel lblRuntime = null;
	private JLabel lblThreadExecDelay = null;
	private JTextField txtRuntime = null;
	private JTextField txtThreadExecDelay = null;
	private JButton btnClientEnvConfigConfiguration = null;
	private JLabel jLabel5 = null;
	private JTextField txtClientId = null;
	private JLabel lblClientConfigMsg = null;
	private JButton btnSetClientConfig = null;
	
	

	
	/**
	 * This is the default constructor
	 */
	public ClientConfigPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setName("Client Config");
		this.setSize(1076, 669);
		lblClientConfigMsg = new JLabel();
		lblClientConfigMsg.setBounds(new Rectangle(21, 156, 511, 16));
		lblClientConfigMsg.setForeground(new Color(255, 51, 51));			
		lblClientConfigMsg.setText("");
		jLabel5 = new JLabel();
		jLabel5.setBounds(new Rectangle(19, 47, 143, 16));
		jLabel5.setText("Client ID (optional)");
		jLabel5.setToolTipText("The ID for the client.  It is recommended that this is left blank.");
		lblThreadExecDelay = new JLabel();
		lblThreadExecDelay.setBounds(new Rectangle(14, 131, 153, 16));
		lblThreadExecDelay.setText("Thread Execution Delay:");
		lblThreadExecDelay.setToolTipText("Time to wait (ms) before starting the next client thread.");
		lblRuntime = new JLabel();
		lblRuntime.setBounds(new Rectangle(13, 102, 152, 16));
		lblRuntime.setText("Minimum Runtime:");
		lblRuntime.setToolTipText("The amount of time (ms) after which client threads quit looping.");
		lblThreadCount = new JLabel();
		lblThreadCount.setBounds(new Rectangle(17, 76, 147, 16));
		lblThreadCount.setText("Thread Count");
		lblThreadCount.setToolTipText("The number of client threads to execute.");
		lblClientConfigPrompt = new JLabel();
		lblClientConfigPrompt.setBounds(new Rectangle(21, 14, 205, 16));
		lblClientConfigPrompt.setText("Provide the Client Config Data");
		
		this.setLayout(null);
		this.add(lblClientConfigPrompt, null);
		this.add(lblThreadCount, null);
		this.add(getTxtThreadCount(), null);
		this.add(lblRuntime, null);
		this.add(lblThreadExecDelay, null);
		this.add(getTxtRuntime(), null);
		this.add(getTxtThreadExecDelay(), null);
		this.add(getBtnClientEnvConfigConfiguration(), null);
		this.add(jLabel5, null);
		this.add(getTxtClientId(), null);
		this.add(lblClientConfigMsg, null);
		this.add(getBtnSetClientConfig(), null);
		
		refresh();		
	}
	
	public void refresh() {
		txtClientId.setText(AcmeGUI.getConfiguredClientConfig().getId());
		txtThreadCount.setText("" + AcmeGUI.getConfiguredClientConfig().getThreadCount());
		txtRuntime.setText("" + AcmeGUI.getConfiguredClientConfig().getRunTime());	
		txtThreadExecDelay.setText("" + AcmeGUI.getConfiguredClientConfig().getThreadExecDelay());		
		
	}
	
	private JTextField getTxtThreadCount() {
		if (txtThreadCount == null) {
			txtThreadCount = new JTextField();
			txtThreadCount.setBounds(new Rectangle(181, 74, 167, 20));
		}
		return txtThreadCount;
	}


	private JTextField getTxtRuntime() {
		if (txtRuntime == null) {
			txtRuntime = new JTextField();
			txtRuntime.setBounds(new Rectangle(181, 104, 165, 20));
		}
		return txtRuntime;
	}

	private JTextField getTxtThreadExecDelay() {
		if (txtThreadExecDelay == null) {
			txtThreadExecDelay = new JTextField();
			txtThreadExecDelay.setBounds(new Rectangle(181, 130, 165, 20));
		}
		return txtThreadExecDelay;
	}

	
	private JButton getBtnClientEnvConfigConfiguration() {
		if (btnClientEnvConfigConfiguration == null) {
			btnClientEnvConfigConfiguration = new JButton();
			btnClientEnvConfigConfiguration.setBounds(new Rectangle(21, 180, 150, 28));
			btnClientEnvConfigConfiguration.setText("Configure Params");
			btnClientEnvConfigConfiguration
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							if (checkClientConfigElementData()){		
								PropertiesDialog pd = new PropertiesDialog(AcmeGUI.getConfiguredClientConfig());
								pd.setVisible(true);							
								lblClientConfigMsg.setText("");
							}
						}
					});
		}
		return btnClientEnvConfigConfiguration;
	}

	
	private JTextField getTxtClientId() {
		if (txtClientId == null) {
			txtClientId = new JTextField();
			txtClientId.setBounds(new Rectangle(180, 46, 166, 20));
		}
		return txtClientId;
	}

	private boolean checkClientConfigElementData(){
		AcmeGUI.getConfiguredClientConfig().setId(txtClientId.getText());
		try{
			AcmeGUI.getConfiguredClientConfig().setRunTime(Long.parseLong(txtRuntime.getText()));
			if (AcmeGUI.getConfiguredClientConfig().getRunTime() <1) throw new Exception();
		}catch (Exception ex) {
			lblClientConfigMsg.setText("run time must be a valid number and greather than or equal 1");
			lblClientConfigMsg.setForeground(Color.RED);
			return false;
		}
		try{
			AcmeGUI.getConfiguredClientConfig().setThreadCount(Integer.parseInt(txtThreadCount.getText()));
			if (AcmeGUI.getConfiguredClientConfig().getThreadCount()<1) throw new Exception();
		}catch (Exception ex) {
			lblClientConfigMsg.setText("thread count must be a valid number greather than or equal  1");
			lblClientConfigMsg.setForeground(Color.RED);
			return false;
		}
		try{
			AcmeGUI.getConfiguredClientConfig().setThreadExecDelay(Long.parseLong(txtThreadExecDelay.getText()));
			if (AcmeGUI.getConfiguredClientConfig().getThreadExecDelay() <0)  throw new Exception();
		}catch (Exception ex){
			lblClientConfigMsg.setText("Thread Exec delay must be a valid number. 0 or more.");
			lblClientConfigMsg.setForeground(Color.RED);
			return false;
		}
		return true;
		
	}
	
	private JButton getBtnSetClientConfig() {
		if (btnSetClientConfig == null) {
			btnSetClientConfig = new JButton();
			btnSetClientConfig.setBounds(new Rectangle(192, 180, 126, 28));
			btnSetClientConfig.setText("Set");
			btnSetClientConfig.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (checkClientConfigElementData()){					
						lblClientConfigMsg.setText("set OK!");
						lblClientConfigMsg.setForeground(Color.BLACK);
					}
				}
			});
		}
		return btnSetClientConfig;
	}

}  
