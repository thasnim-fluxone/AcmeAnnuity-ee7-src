package com.ibm.wssvt.acme.common.client.ui.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.ibm.wssvt.acme.common.client.ui.gui.model.AdapterConfigurationDecorator;
import com.ibm.wssvt.acme.common.client.ui.gui.model.ExecutionUnitConfigurationDecorator;
import com.ibm.wssvt.acme.common.client.ui.gui.model.IAdapterBeansFactoryMap;

public class ExecutionUnitPanel extends JPanel implements RefreshableAcmeGuiPanel{

	private static final long serialVersionUID = 1L;


	private JComboBox cbConfiguredAdapters = null;
	private JList lstTemplateExecutionUnits = null;
	private JComboBox cbBeansFactory = null;
	private JButton btnAddEU = null;
	private JButton btnRemoveEU = null;
	private JList lstConfiguredExecutionUnits = null;
	private JScrollPane spConfiguredExecutionUnits = null;
	private JButton btnConfigureEU = null;
	private JLabel lblExecutionUnitMessage = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;	
	private JLabel jLabel6 = null;
	private JTextField txtEURepeat = null;
	private JLabel jLabel7 = null;
	private JTextField txtEUDescription = null;

	private JLabel lblAdapterId = null;
	
	
		
	/**
	 * This is the default constructor
	 */
	public ExecutionUnitPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setName("Execution Units");
		this.setSize(1076, 669);
		lblAdapterId = new JLabel();
		lblAdapterId.setBounds(new Rectangle(256, 81, 227, 16));
		lblAdapterId.setText("");
		jLabel7 = new JLabel();
		jLabel7.setBounds(new Rectangle(17, 108, 80, 16));
		jLabel7.setText("Description:");
		jLabel6 = new JLabel();
		jLabel6.setBounds(new Rectangle(17, 82, 48, 16));
		jLabel6.setText("Repeat");
		jLabel2 = new JLabel();
		jLabel2.setBounds(new Rectangle(30, 136, 220, 16));
		jLabel2.setText("Available Execution Units");
		jLabel1 = new JLabel();
		jLabel1.setBounds(new Rectangle(254, 19, 225, 16));
		jLabel1.setText("Available Beans Factory for Adapter");
		jLabel = new JLabel();
		jLabel.setBounds(new Rectangle(9, 22, 208, 16));
		jLabel.setText("Configured Services/ Adapters");
		lblExecutionUnitMessage = new JLabel();
		lblExecutionUnitMessage.setBounds(new Rectangle(26, 555, 839, 16));
		lblExecutionUnitMessage.setForeground(new Color(255, 51, 51));
		lblExecutionUnitMessage.setText("");
		this.setLayout(null);
		this.add(getCbConfiguredAdapters(), null);
		this.add(getLstTemplateExecutionUnits(), null);
		this.add(getCbBeansFactory(), null);
		this.add(getBtnAddEU(), null);
		this.add(getBtnRemoveEU(), null);
		this.add(getSpConfiguredExecutionUnits(), null);
		this.add(getBtnConfigureEU(), null);
		this.add(lblExecutionUnitMessage, null);
		this.add(jLabel, null);
		this.add(jLabel1, null);
		this.add(jLabel2, null);			
		this.add(jLabel6, null);
		this.add(getTxtEURepeat(), null);
		this.add(jLabel7, null);
		this.add(getTxtEUDescription(), null);
		this.add(lblAdapterId, null);
		
		refresh();
	}

	public void refresh() {
		DefaultComboBoxModel cbModel = new DefaultComboBoxModel(AcmeGUI.getSelectedAdaptersDataModel());
		cbConfiguredAdapters.setModel(cbModel);
		if (cbModel.getSize()>0){
			cbConfiguredAdapters.setSelectedIndex(0);
		}
		AdapterConfigurationDecorator selectedAdapter = (AdapterConfigurationDecorator)cbConfiguredAdapters.getSelectedItem();
		if (selectedAdapter != null){
			DefaultComboBoxModel model = new DefaultComboBoxModel(
					AcmeGUI.getAdapterBeansFactoryMap().getSupportedBeansFactories(selectedAdapter));							
			cbBeansFactory.setModel(model);
			lblAdapterId.setText("AdapterId: " + selectedAdapter.getId());
			lblAdapterId.setForeground(Color.BLACK);
		}
		
		DefaultListModel model = new DefaultListModel();
		for (Iterator<ExecutionUnitConfigurationDecorator> i = AcmeGUI.getTemplateExecutionUnitsDataModel().iterator(); i.hasNext();) {
			ExecutionUnitConfigurationDecorator euConfig = (ExecutionUnitConfigurationDecorator) i.next();				
			model.addElement(euConfig);						
		}
		lstTemplateExecutionUnits.setModel(model);		
		model = new DefaultListModel();
		lstConfiguredExecutionUnits.setModel(model);		
		for (int i = 0; i < AcmeGUI.getConfiguredExecutionUnitsDataModel().size(); i++) {
			model.addElement(AcmeGUI.getConfiguredExecutionUnitsDataModel().get(i));
			
		}
	}


	private JComboBox getCbConfiguredAdapters() {
		if (cbConfiguredAdapters == null) {
			cbConfiguredAdapters = new JComboBox();
			cbConfiguredAdapters.setBounds(new Rectangle(10, 44, 210, 25));
			
			cbConfiguredAdapters.addFocusListener(new java.awt.event.FocusAdapter() {				
				public void focusGained(java.awt.event.FocusEvent e) {										
					cbConfiguredAdapters.setModel(new DefaultComboBoxModel(AcmeGUI.getSelectedAdaptersDataModel()));
					AdapterConfigurationDecorator selected = (AdapterConfigurationDecorator ) cbConfiguredAdapters.getSelectedItem();
					if (selected == null) {						
						return;
					}
					DefaultComboBoxModel model = new DefaultComboBoxModel(AcmeGUI.getAdapterBeansFactoryMap().getSupportedBeansFactories(selected));					
					cbBeansFactory.setModel(model);
					lblAdapterId.setText("AdapterId: " + selected.getId());
					lblAdapterId.setForeground(Color.BLACK);
				}
			});
			
			cbConfiguredAdapters.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					AdapterConfigurationDecorator selected = (AdapterConfigurationDecorator ) cbConfiguredAdapters.getSelectedItem();
					if (selected == null) {
						lblExecutionUnitMessage.setText("You MUST selecte an adapter first!!");
						lblExecutionUnitMessage.setForeground(Color.RED);
						return;
					}
					DefaultComboBoxModel model = new DefaultComboBoxModel(AcmeGUI.getAdapterBeansFactoryMap().getSupportedBeansFactories(selected));							
					cbBeansFactory.setModel(model);
					lblAdapterId.setText("AdapterId: " + selected.getId());
					lblExecutionUnitMessage.setForeground(Color.BLACK);
				}
			});
		}
		return cbConfiguredAdapters;
	}

	/**
	 * This method initializes lstTemplateExecutionUnits	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getLstTemplateExecutionUnits() {
		if (lstTemplateExecutionUnits == null) {
			lstTemplateExecutionUnits = new JList();
			lstTemplateExecutionUnits.setToolTipText("An execution unit is a series of operations to perform using a service adapter");
			lstTemplateExecutionUnits.setBounds(new Rectangle(28, 159, 283, 367));			
			lstTemplateExecutionUnits
					.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
						public void valueChanged(javax.swing.event.ListSelectionEvent e) {
							if (-1 == lstTemplateExecutionUnits.getSelectedIndex()){
								return;
							}
							ExecutionUnitConfigurationDecorator eu = (ExecutionUnitConfigurationDecorator)( (DefaultListModel) lstTemplateExecutionUnits.getModel()).get(lstTemplateExecutionUnits.getSelectedIndex());
							txtEURepeat.setText("" + eu.getRepeat());
							txtEUDescription.setText(eu.getDescription());
						}
					});
		}
		return lstTemplateExecutionUnits;
	}

	/**
	 * This method initializes cbBeansFactory	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getCbBeansFactory() {
		if (cbBeansFactory == null) {
			cbBeansFactory = new JComboBox();
			cbBeansFactory.setBounds(new Rectangle(256, 41, 185, 25));
		}
		return cbBeansFactory;
	}

	/**
	 * This method initializes btnAddEU	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnAddEU() {
		if (btnAddEU == null) {
			btnAddEU = new JButton();
			btnAddEU.setBounds(new Rectangle(335, 192, 107, 31));
			btnAddEU.setText("Add --->");
			btnAddEU.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					AdapterConfigurationDecorator adapter = (AdapterConfigurationDecorator) ((DefaultComboBoxModel) cbConfiguredAdapters.getModel()).getSelectedItem();
					if (adapter  == null){
						lblExecutionUnitMessage.setText("Update adapters selection first!");
						lblExecutionUnitMessage.setForeground(Color.RED);
						return;
					}
					ExecutionUnitConfigurationDecorator eu = (ExecutionUnitConfigurationDecorator) lstTemplateExecutionUnits.getSelectedValue();
					if (eu== null){
						lblExecutionUnitMessage.setText("select an EU from the left to add.");
						lblExecutionUnitMessage.setForeground(Color.RED);
						return;
					}					
					ExecutionUnitConfigurationDecorator copy = eu.clone();
					copy.setAdapterId(adapter.getId());
					IAdapterBeansFactoryMap beansFacttory = (IAdapterBeansFactoryMap)
						((DefaultComboBoxModel) cbBeansFactory.getModel()).getSelectedItem();
					copy.setBeansFactoryClass(beansFacttory.getName());
					try{
						copy.setRepeat(Integer.parseInt(txtEURepeat.getText()));
					}catch (Exception ex){
						lblExecutionUnitMessage.setText("repeat must be a valid number");
						lblExecutionUnitMessage.setForeground(Color.RED);
					}
					copy.setDescription(txtEUDescription.getText());
					AcmeGUI.getConfiguredExecutionUnitsDataModel().add(copy);
					((DefaultListModel)lstConfiguredExecutionUnits.getModel()).addElement(copy);	
					lblExecutionUnitMessage.setText("Added OK");
					lblExecutionUnitMessage.setForeground(Color.BLACK);
				}
			});
		}
		return btnAddEU;
	}

	/**
	 * This method initializes btnRemoveEU	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnRemoveEU() {
		if (btnRemoveEU == null) {
			btnRemoveEU = new JButton();
			btnRemoveEU.setBounds(new Rectangle(335, 272, 111, 29));
			btnRemoveEU.setText("<--- Remove");
			btnRemoveEU.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ExecutionUnitConfigurationDecorator eu = (ExecutionUnitConfigurationDecorator) lstConfiguredExecutionUnits.getSelectedValue();
					int idx = lstConfiguredExecutionUnits.getSelectedIndex();
					if (eu == null){
						lblExecutionUnitMessage.setText("Select an EU from the Right to remove");
						lblExecutionUnitMessage.setForeground(Color.RED);
						return;
					}
					AcmeGUI.getConfiguredExecutionUnitsDataModel().remove(idx);					
					((DefaultListModel) lstConfiguredExecutionUnits.getModel()).remove(idx);
					lblExecutionUnitMessage.setText("Removed OK");
					lblExecutionUnitMessage.setForeground(Color.BLACK);
				}
			});
		}
		return btnRemoveEU;
	}

	/**
	 * This method initializes lstConfiguredExecutionUnits	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getLstConfiguredExecutionUnits() {
		if (lstConfiguredExecutionUnits == null) {
			lstConfiguredExecutionUnits = new JList();
			DefaultListModel model = new DefaultListModel();
			lstConfiguredExecutionUnits.setModel(model);
		}
		return lstConfiguredExecutionUnits;
	}

	/**
	 * This method initializes spConfiguredExecutionUnits	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getSpConfiguredExecutionUnits() {
		if (spConfiguredExecutionUnits == null) {
			spConfiguredExecutionUnits = new JScrollPane();
			spConfiguredExecutionUnits.setBounds(new Rectangle(526, 20, 345, 467));
			spConfiguredExecutionUnits.setViewportView(getLstConfiguredExecutionUnits());
		}
		return spConfiguredExecutionUnits;
	}

	/**
	 * This method initializes btnConfigureEU	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnConfigureEU() {
		if (btnConfigureEU == null) {
			btnConfigureEU = new JButton();
			btnConfigureEU.setBounds(new Rectangle(632, 506, 147, 26));
			btnConfigureEU.setText("Configure Params");
			btnConfigureEU.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {					
					ExecutionUnitConfigurationDecorator eu = (ExecutionUnitConfigurationDecorator) lstConfiguredExecutionUnits.getSelectedValue();
					if (eu == null) {
						lblExecutionUnitMessage.setText("select an EU from the right to configure.");
						lblExecutionUnitMessage.setForeground(Color.RED);
						return;
					}
					lblExecutionUnitMessage.setText("configure params...");
					lblExecutionUnitMessage.setForeground(Color.BLACK);
					PropertiesDialog pd = new PropertiesDialog(eu);
					pd.setModal(true);
					pd.setVisible(true);					
					lblExecutionUnitMessage.setText("");
					lblExecutionUnitMessage.setForeground(Color.BLACK);
				}
			});
		}
		return btnConfigureEU;
	}
	private JTextField getTxtEURepeat() {
		if (txtEURepeat == null) {
			txtEURepeat = new JTextField();
			txtEURepeat.setToolTipText("This is the number of times to execute this EU before moving on to the next EU.");
			txtEURepeat.setBounds(new Rectangle(75, 82, 136, 20));
		}
		return txtEURepeat;
	}

	/**
	 * This method initializes txtEUDescription	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTxtEUDescription() {
		if (txtEUDescription == null) {
			txtEUDescription = new JTextField();
			txtEUDescription.setBounds(new Rectangle(111, 106, 311, 20));
		}
		return txtEUDescription;
	}
}  
