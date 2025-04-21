package com.ibm.wssvt.acme.common.client.ui.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class StartupPanel extends JPanel {

	private static final long serialVersionUID = 1L;
		
	private JTextArea txtWelcomeMsg = null;	
	private JButton btnLoadFromTemplate = null;
	private JButton btnLoadFromFileAsATemplate = null;
	private JButton btnLoadFromFileAndConfigure = null;
	private JLabel lblStartupMessage = null;	
	
		
	public StartupPanel() {
		super();
		initialize();
	}

	
	private void initialize() {
		this.setName("Startup");
		this.setSize(1076, 669);	
		lblStartupMessage = new JLabel();
		lblStartupMessage.setBounds(new Rectangle(52, 505, 753, 16));
		lblStartupMessage.setText("");			
		this.setLayout(null);
		this.add(getTxtWelcomeMsg(), null);
		this.add(getBtnLoadFromTemplate(), null);
		this.add(getBtnLoadFromFileAsATemplate(), null);
		this.add(getBtnLoadFromFileAndConfigure(), null);
		this.add(lblStartupMessage, null);
	}
			
	private JTextArea getTxtWelcomeMsg() {
		if (txtWelcomeMsg == null) {
			txtWelcomeMsg = new JTextArea();
			txtWelcomeMsg.setBounds(new Rectangle(21, 14, 562, 71));
			txtWelcomeMsg.setEditable(false);
			txtWelcomeMsg.setText("Hi!\nThis tool can be used to generate the XML Configuration File."
					+"\nused by the Acme Annuity Application."
					+"\nUpdate each tab from the left to the right - and save your work at the end.");
		}
		return txtWelcomeMsg;
	}



	/**
	 * This method initializes btnLoadFromTemplate	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnLoadFromTemplate() {
		if (btnLoadFromTemplate == null) {
			btnLoadFromTemplate = new JButton();
			btnLoadFromTemplate.setBounds(new Rectangle(47, 121, 238, 34));
			btnLoadFromTemplate.setText("Load From Template");
			btnLoadFromTemplate.setToolTipText("Load the default template and configure.");
			btnLoadFromTemplate.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {					
					if (!AcmeGUI.loadTemplate()) {
						lblStartupMessage.setText("Failed to load!! Check the log");
						return;
					}					
					lblStartupMessage.setText("Template loaded OK!");
				}
			});
		}
		return btnLoadFromTemplate;
	}

	/**
	 * This method initializes btnLoadFromFileAsATemplate	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnLoadFromFileAsATemplate() {
		if (btnLoadFromFileAsATemplate == null) {
			btnLoadFromFileAsATemplate = new JButton();
			btnLoadFromFileAsATemplate.setBounds(new Rectangle(46, 168, 237, 33));
			btnLoadFromFileAsATemplate.setText("Load From My File As A Template");
			btnLoadFromFileAsATemplate.setToolTipText("Load an existing file and use as a custom template.");
			btnLoadFromFileAsATemplate
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {							
							File file = GuiUtils.getOpenFile();
							if (file == null) {
								lblStartupMessage.setText("User Cancel!");
								lblStartupMessage.setForeground(Color.BLUE);
								return;
							}
							
							try {
								URL u = new URL("file","",file.getAbsolutePath());
						        System.out.println(u.toString());								
								if (!AcmeGUI.loadUserFile(u.toString(), false)) {
									lblStartupMessage.setText("Failed to load!! File is not valid");									
									lblStartupMessage.setForeground(Color.RED);
									return;
								}
							} catch (MalformedURLException e1) {
								// TODO Auto-generated catch block
								lblStartupMessage.setText("Error: " + e1);
								lblStartupMessage.setForeground(Color.RED);
								e1.printStackTrace();
							}													
							lblStartupMessage.setText("file loaded OK!");
							lblStartupMessage.setForeground(Color.BLACK);
						}
					});
		}
		return btnLoadFromFileAsATemplate;
	}

	/**
	 * This method initializes btnLoadFromFileAndConfigure	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnLoadFromFileAndConfigure() {
		if (btnLoadFromFileAndConfigure == null) {
			btnLoadFromFileAndConfigure = new JButton();
			btnLoadFromFileAndConfigure.setBounds(new Rectangle(44, 213, 237, 36));
			btnLoadFromFileAndConfigure.setText("Load From My File And Configure");
			btnLoadFromFileAndConfigure.setToolTipText("Load an existing file and configure.");
			btnLoadFromFileAndConfigure
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							File file = GuiUtils.getOpenFile();
							if (file == null) {
								lblStartupMessage.setText("User Cancel!");
								lblStartupMessage.setForeground(Color.BLUE);
								return;
							}
							
							try {
								URL u = new URL("file","",file.getAbsolutePath());
						        //System.out.println(u.toString());
								if (!AcmeGUI.loadUserFile(u.toString(), true)) {
									lblStartupMessage.setText("Failed to load!! file is not valid");
									lblStartupMessage.setForeground(Color.RED);
									return;
								}

							} catch (MalformedURLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								lblStartupMessage.setText("Error: " + e1);
								lblStartupMessage.setForeground(Color.RED);
							}
							
							lblStartupMessage.setText("File loaded & Configured OK!");
							lblStartupMessage.setForeground(Color.BLACK);
						}
					});
		}
		return btnLoadFromFileAndConfigure;
	}

}  