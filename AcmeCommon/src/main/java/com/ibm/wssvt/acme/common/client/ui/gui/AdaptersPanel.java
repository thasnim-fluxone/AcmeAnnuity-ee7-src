package com.ibm.wssvt.acme.common.client.ui.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.ibm.wssvt.acme.common.client.ui.gui.model.AdapterConfigurationDecorator;
import com.ibm.wssvt.acme.common.client.ui.gui.model.ExecutionUnitConfigurationDecorator;

public class AdaptersPanel extends JPanel implements RefreshableAcmeGuiPanel{

	private static final long serialVersionUID = 1L;		
	private JComboBox cbTemplateAdapters = null;
	private JLabel lblServiceName = null;	
	private JScrollPane spSelectedAdapters = null;
	private JList lstConfiguredAdapters = null;
	private JButton btnAddAdapter = null;
	private JButton btnRemoveAdapter = null;
	private JButton btnConfigureAdapter = null;	
	private JLabel lblAdapterMessage = null;	
	private JButton btnDispalyAdapterTemplateProps = null;
	private JLabel lblSelectedAdapterId = null;
	
	
	public AdaptersPanel() {
		super();
		initialize();
	}

	
	private void initialize() {
		this.setName("Service Adapters");
		this.setSize(1076, 669);
		lblSelectedAdapterId = new JLabel();
		lblSelectedAdapterId.setBounds(new Rectangle(312, 95, 176, 22));
		lblSelectedAdapterId.setText("");
		lblSelectedAdapterId.setToolTipText("The selected adapter id");
		lblAdapterMessage = new JLabel();
		lblAdapterMessage.setBounds(new Rectangle(23, 541, 743, 16));
		lblAdapterMessage.setForeground(new Color(255, 51, 51));
		lblAdapterMessage.setText("");
		lblAdapterMessage.setToolTipText("look here for messages");
		lblServiceName = new JLabel();
		lblServiceName.setBounds(new Rectangle(19, 15, 105, 16));
		lblServiceName.setToolTipText("Select a Service (aka adapter)");
		lblServiceName.setText("Select a Service.");

		this.setLayout(null);		
		this.add(getCbTemplateAdapters(), null);
		this.add(lblServiceName, null);
		this.add(getSpSelectedAdapters(), null);
		this.add(getBtnAddAdapter(), null);
		this.add(getBtnRemoveAdapter(), null);
		this.add(lblAdapterMessage, null);
		this.add(getBtnConfigureAdapter(), null);
		this.add(getBtnDispalyAdapterTemplateProps(), null);
		this.add(lblSelectedAdapterId, null);			
		
		refresh();		
	}

	public void refresh() {
		ComboBoxModel cbModel = new DefaultComboBoxModel(AcmeGUI.getTemplateAdaptersDataModel());
		cbTemplateAdapters.setModel(cbModel);			
	
		DefaultListModel model = new DefaultListModel();
		lstConfiguredAdapters.setModel(model);
		for (int i = 0; i < AcmeGUI.getSelectedAdaptersDataModel().size(); i++) {
			AdapterConfigurationDecorator adapter = AcmeGUI.getSelectedAdaptersDataModel().get(i);
			model.addElement(adapter);						
		}
	}

	private JScrollPane getSpSelectedAdapters() {
		if (spSelectedAdapters == null) {
			spSelectedAdapters = new JScrollPane();
			spSelectedAdapters.setBounds(new Rectangle(22, 130, 744, 272));
			spSelectedAdapters.setViewportView(getLstConfiguredAdapters());
		}
		return spSelectedAdapters;
	}

	
	private JList getLstConfiguredAdapters() {
		if (lstConfiguredAdapters == null) {
			lstConfiguredAdapters = new JList();
			lstConfiguredAdapters.setModel(new DefaultListModel());
			lstConfiguredAdapters
					.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
						public void valueChanged(javax.swing.event.ListSelectionEvent e) {							
							AdapterConfigurationDecorator selected = (AdapterConfigurationDecorator) lstConfiguredAdapters.getSelectedValue();
							if (selected == null) return;
							lblSelectedAdapterId.setText("Adapter Id: " + selected.getId());
						}
					});
		}
		return lstConfiguredAdapters;
	}

	
	private JButton getBtnAddAdapter() {
		if (btnAddAdapter == null) {
			btnAddAdapter = new JButton();
			btnAddAdapter.setBounds(new Rectangle(24, 91, 248, 28));
			btnAddAdapter.setText("Add Service/Adapter");
			btnAddAdapter.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {					
					if (cbTemplateAdapters.getSelectedItem() == null){
						lblAdapterMessage.setText("Select a Valid Adapter First from the drop down");
						return;
					}
					AdapterConfigurationDecorator selectedItem = (AdapterConfigurationDecorator) cbTemplateAdapters.getSelectedItem();
					AdapterConfigurationDecorator copy = selectedItem.clone();
					copy.setId("" + (AcmeGUI.getSelectedAdaptersDataModel().size()+1));
					AcmeGUI.getSelectedAdaptersDataModel().add(copy);
					DefaultListModel model = (DefaultListModel) lstConfiguredAdapters.getModel();
					model.addElement(copy);
					lblAdapterMessage.setText("added OK: adapterId: " + copy.getId());
					lblAdapterMessage.setForeground(Color.BLACK);
				}
			});
		}
		return btnAddAdapter;
	}


	private JButton getBtnRemoveAdapter() {
		if (btnRemoveAdapter == null) {
			btnRemoveAdapter = new JButton();
			btnRemoveAdapter.setBounds(new Rectangle(544, 90, 221, 27));
			btnRemoveAdapter.setText("Remove Service/Adapter");
			btnRemoveAdapter.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (lstConfiguredAdapters.getSelectedValue() == null) {
						lblAdapterMessage.setText("select an adapter first from the list");
						lblAdapterMessage.setForeground(Color.RED);
						return;
					}
					AdapterConfigurationDecorator selectedElement = (AdapterConfigurationDecorator) lstConfiguredAdapters.getSelectedValue();
					for(ExecutionUnitConfigurationDecorator eu: AcmeGUI.getConfiguredExecutionUnitsDataModel()){
						if (eu.getAdapterId().equals(selectedElement.getId())){
							lblAdapterMessage.setText("This adapter is assigned to an EU. Remove the EU first: eu:" + eu.getDescription());
							lblAdapterMessage.setForeground(Color.RED);
							return;
						}
					}
					DefaultListModel model = (DefaultListModel) lstConfiguredAdapters.getModel();
					model.removeElement(selectedElement);
					AcmeGUI.getSelectedAdaptersDataModel().remove(selectedElement);					
					lblAdapterMessage.setText("Removed OK. adapterId: " + selectedElement.getId());
					lblAdapterMessage.setForeground(Color.BLACK);
				}
			});
		}
		return btnRemoveAdapter;
	}

	private JButton getBtnConfigureAdapter() {
		if (btnConfigureAdapter == null) {
			btnConfigureAdapter = new JButton();
			btnConfigureAdapter.setText("Configure Selected Service Params");
			btnConfigureAdapter.setBounds(new Rectangle(242, 416, 284, 28));
			btnConfigureAdapter.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {					
					AdapterConfigurationDecorator selected = (AdapterConfigurationDecorator) lstConfiguredAdapters.getSelectedValue();
					if (selected == null) {
						lblAdapterMessage.setText("Select an adapter from the list first.");
						lblAdapterMessage.setForeground(Color.RED);
						return;
					}
					lblAdapterMessage.setText("update params ...");
					lblAdapterMessage.setForeground(Color.BLACK);
					PropertiesDialog pd = new PropertiesDialog(selected);
					pd.setModal(true);
					pd.setVisible(true);
					lblAdapterMessage.setText("");
				}
				
			});
		}
		return btnConfigureAdapter;
	}

	private JButton getBtnDispalyAdapterTemplateProps() {
		if (btnDispalyAdapterTemplateProps == null) {
			btnDispalyAdapterTemplateProps = new JButton();
			btnDispalyAdapterTemplateProps.setBounds(new Rectangle(291, 44, 150, 32));
			btnDispalyAdapterTemplateProps.setText("Display Properties");
			btnDispalyAdapterTemplateProps
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {							
							AdapterConfigurationDecorator selected = (AdapterConfigurationDecorator) cbTemplateAdapters.getSelectedItem();
							if (selected == null) {
								return;
							}
							Set<String> keys = selected.getConfiguration().getParameters().keySet();
							String params ="";
							for (String key : keys) {
								params += key + "=" + selected.getConfiguration().getParameterValue(key)
								+"\n\n";
							}						
							JOptionPane.showMessageDialog(null, "Property Of the Service/Adapter: (you can change these once add/configure the service)\n\n" + params, 
									"Property Of the Service/Adapter", 
									JOptionPane.INFORMATION_MESSAGE);
						}
					});
		}
		return btnDispalyAdapterTemplateProps;
	}
	
	private JComboBox getCbTemplateAdapters() {
		if (cbTemplateAdapters == null) {
			cbTemplateAdapters = new JComboBox();
			cbTemplateAdapters.setToolTipText("A service adapter specifies the technology used to call the server.");
			cbTemplateAdapters.setBounds(new Rectangle(27, 48, 246, 29));
			cbTemplateAdapters.setName("");						
		}
		return cbTemplateAdapters;
	}
	
}
