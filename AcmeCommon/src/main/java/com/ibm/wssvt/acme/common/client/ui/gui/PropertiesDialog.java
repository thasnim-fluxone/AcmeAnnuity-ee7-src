package com.ibm.wssvt.acme.common.client.ui.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.ibm.wssvt.acme.common.bean.Configrable;

public class PropertiesDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private Map<String, String> props = new HashMap<String, String>();
	private JScrollPane jScrollPane = null;
	private JPanel jPanel = null;
	private Configrable<String, String> configrable;
	
	public Configrable<String, String> getConfigrable() {
		return configrable;
	}

	public PropertiesDialog(Configrable<String, String> configrable){
		initialize();
		this.configrable = configrable;
		props = configrable.getConfiguration().getParameters();
		populate();
	}
	
	private void populate(){
		int x =0;
		int y=0;	
		jPanel.removeAll();		
		JLabel propNameLabel = new JLabel();
		propNameLabel.setText("Property Name");		
		GridBagConstraints propNameConst = new GridBagConstraints();
		propNameConst.fill =GridBagConstraints.VERTICAL;
		propNameConst.gridx = x;
		propNameConst.gridy = y;
		propNameConst.weightx= 1.0;
		jPanel.add(propNameLabel, propNameConst);
		x++;
		JLabel propValueLabel = new JLabel();
		propValueLabel.setText("Property Value");		
		GridBagConstraints propValueConst = new GridBagConstraints();
		propValueConst.fill =GridBagConstraints.VERTICAL;
		propValueConst.gridx = x;
		propValueConst.gridy = y;
		propValueConst.weightx= 1.0;
		jPanel.add(propValueLabel , propValueConst);
		y++;
		x=0;		
		for (String key : props.keySet()) {
			JLabel lbl = new JLabel();
			lbl.setText(key);
			lbl.setName(key);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill =GridBagConstraints.VERTICAL;
			gridBagConstraints.gridx = x;
			gridBagConstraints.gridy = y;
			gridBagConstraints.weightx= 1.0;
			jPanel.add(lbl, gridBagConstraints);
			x++;
			JTextField txt = new JTextField();
			txt.setText(props.get(key));
			txt.setName(key);
			txt.setColumns(35);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill =GridBagConstraints.VERTICAL;
			gridBagConstraints1.gridx = x;
			gridBagConstraints1.gridy = y;
			gridBagConstraints1.weightx= 1.0;
			jPanel.add(txt, gridBagConstraints1);			
								
			x++;
			JButton btnDelete = new JButton();
			btnDelete.setText("Delete");
			btnDelete.setName(key);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill =GridBagConstraints.VERTICAL;
			gridBagConstraints2.gridx = x;
			gridBagConstraints2.gridy = y;
			gridBagConstraints2.weightx= 1.0;
			jPanel.add(btnDelete, gridBagConstraints2);
			btnDelete.addActionListener(new ActionListener() {				
				public void actionPerformed(java.awt.event.ActionEvent e) {										
					String keyStr= ((JButton)e.getSource()).getName();
					props.remove(keyStr);																			
					populate();
					System.out.println(props);				
				}				
			});
			
			y++;
			x=0;			
		}
		
		
		x=0;
		// The DONE Button
		JButton btnDone = new JButton();
		btnDone.setText("Done");
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill =GridBagConstraints.BOTH;
		gridBagConstraints.gridx = x;
		gridBagConstraints.gridy = y;
		gridBagConstraints.weightx= 1.0;
		gridBagConstraints.gridwidth=1;
		jPanel.add(btnDone, gridBagConstraints);
		btnDone.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				Component[] comps2=jPanel.getComponents();
				for (int i = 0; i <comps2.length; i++) {
					if (comps2[i] instanceof JTextField){
						JTextField txt = (JTextField) comps2[i]; 
						String key = txt.getName();
						String value = txt.getText();
						props.put(key, value);			
					}
				}				
				setVisible(false);
			}
			
		});
		
		x++;
		JButton btnAdd = new JButton();
		btnAdd.setText("Add New Property");
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill =GridBagConstraints.VERTICAL;
		gridBagConstraints2.gridx = x;
		gridBagConstraints2.gridy = y;
		jPanel.add(btnAdd, gridBagConstraints2);
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				String key = JOptionPane.showInputDialog(null,
						  "What is the property NAME",
						  "Enter the property NAME",
						  JOptionPane.QUESTION_MESSAGE);
				String value="";
				if (key != null && key.trim().length() >0) {
					value= JOptionPane.showInputDialog(null,
							  "What is the property VALUE",
							  "Enter the property VALUE",
							  JOptionPane.QUESTION_MESSAGE);
				}else{
					JOptionPane.showMessageDialog(null, "Property Name is EMPTY. Nothing will be added ");
					return;
				}
				props.put(key, value);
				populate();				
			}
			
		});				
					
		
		Dimension d = new Dimension(700, 350);				
		this.setSize(d);
		jPanel.updateUI();
	}
		
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(500, 200);
		this.setTitle("Maintain Object Properties");
		this.setContentPane(getJContentPane());		
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.gridx = 0;								
			jContentPane = new JPanel();		
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getJScrollPane(), gridBagConstraints);
			
			
		}
		return jContentPane;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJPanel());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
		}
		return jPanel;
	}
	
}
