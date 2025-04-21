package com.ibm.wssvt.acme.common.client.ui.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.ibm.wssvt.acme.common.client.ui.gui.model.ExceptionHandlerConfigurationDecorator;

public class ExceptionHandlerPanel extends JPanel implements RefreshableAcmeGuiPanel{

	private static final long serialVersionUID = 1L;
	private JList lstTemplateExceptionHandlers = null;
	private JScrollPane spTemplateExceptionHandlers = null;
	private JLabel jLabel3 = null;
	private JTextField txtExceptionClassName = null;
	private JLabel jLabel4 = null;
	private JScrollPane spExceptionClassName = null;
	private JScrollPane spConfiguredExceptionHandlers = null;
	private JList lstConfiguredExceptionHandlers = null;
	private JButton btnAddExHandler = null;
	private JButton btnRemove = null;
	private JButton btnConfigureExHandler = null;
	private JLabel lblExHandlerMessage = null;
	

	public ExceptionHandlerPanel() {
		super();
		initialize();
	}

	private void initialize() {
		this.setName("Exception Handlers");
		this.setSize(1076, 669);
		lblExHandlerMessage = new JLabel();
		lblExHandlerMessage.setBounds(new Rectangle(33, 469, 663, 16));
		lblExHandlerMessage.setForeground(new Color(255, 51, 51));
		lblExHandlerMessage.setText("");
		jLabel4 = new JLabel();
		jLabel4.setBounds(new Rectangle(17, 104, 128, 16));
		jLabel4.setText("Available Handlers");
		jLabel3 = new JLabel();
		jLabel3.setBounds(new Rectangle(10, 36, 111, 16));
		jLabel3.setText("Exception Class");		
		this.setLayout(null);
		this.add(getSpTemplateExceptionHandlers(), null);
		this.add(jLabel3, null);
		this.add(jLabel4, null);
		this.add(getSpExceptionClassName(), null);
		this.add(getSpConfiguredExceptionHandlers(), null);
		this.add(getBtnAddExHandler(), null);
		this.add(getBtnRemove(), null);
		this.add(getBtnConfigureExHandler(), null);
		this.add(lblExHandlerMessage, null);			
	
		refresh();
	}

	public void refresh() {
		DefaultListModel model = new DefaultListModel();
		lstTemplateExceptionHandlers.setModel(model);		
		for (Iterator<ExceptionHandlerConfigurationDecorator> i = AcmeGUI.getTemplateExceptionHandlersDataModel().iterator(); i.hasNext();) {
			model.addElement(i.next());			
		}
		
		model = new DefaultListModel();
		lstConfiguredExceptionHandlers.setModel(model);
		for (int i=0; i<AcmeGUI.getConfiguredExceptionHandlersDataModel().size(); i++){			
			model.addElement(AcmeGUI.getConfiguredExceptionHandlersDataModel().get(i));
		}
	}
	private JList getLstTemplateExceptionHandlers() {
		if (lstTemplateExceptionHandlers == null) {
			lstTemplateExceptionHandlers = new JList();	
			lstTemplateExceptionHandlers.setToolTipText("An exception handler specifies what action to take when an exception of a particular type is encountered.");
			lstTemplateExceptionHandlers
					.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
						public void valueChanged(javax.swing.event.ListSelectionEvent e) {							
							ExceptionHandlerConfigurationDecorator eh = (ExceptionHandlerConfigurationDecorator) lstTemplateExceptionHandlers.getSelectedValue();
							if (eh == null) {
								return;
							}
							txtExceptionClassName.setText(eh.getExceptionClassName());
						}
					});
					
		}
		return lstTemplateExceptionHandlers;
	}

	private JScrollPane getSpTemplateExceptionHandlers() {
		if (spTemplateExceptionHandlers == null) {
			spTemplateExceptionHandlers = new JScrollPane();
			spTemplateExceptionHandlers.setBounds(new Rectangle(31, 134, 207, 316));
			spTemplateExceptionHandlers.setViewportView(getLstTemplateExceptionHandlers());
		}
		return spTemplateExceptionHandlers;
	}

	private JTextField getTxtExceptionClassName() {
		if (txtExceptionClassName == null) {
			txtExceptionClassName = new JTextField();
			txtExceptionClassName.setToolTipText("This is the exception class (including package name) for which the selected handler will be used.  A wild card (*) may be used for class or package.");
		}
		return txtExceptionClassName;
	}

	private JScrollPane getSpExceptionClassName() {
		if (spExceptionClassName == null) {
			spExceptionClassName = new JScrollPane();
			spExceptionClassName.setBounds(new Rectangle(20, 56, 409, 37));
			spExceptionClassName.setViewportView(getTxtExceptionClassName());
		}
		return spExceptionClassName;
	}

	
	private JScrollPane getSpConfiguredExceptionHandlers() {
		if (spConfiguredExceptionHandlers == null) {
			spConfiguredExceptionHandlers = new JScrollPane();
			spConfiguredExceptionHandlers.setBounds(new Rectangle(481, 60, 216, 347));
			spConfiguredExceptionHandlers.setViewportView(getLstConfiguredExceptionHandlers());
		}
		return spConfiguredExceptionHandlers;
	}
	private JList getLstConfiguredExceptionHandlers() {
		if (lstConfiguredExceptionHandlers == null) {
			lstConfiguredExceptionHandlers = new JList();
		}
		return lstConfiguredExceptionHandlers;
	}

	private JButton getBtnAddExHandler() {
		if (btnAddExHandler == null) {
			btnAddExHandler = new JButton();
			btnAddExHandler.setBounds(new Rectangle(301, 154, 119, 26));
			btnAddExHandler.setText("Add --->");
		
			btnAddExHandler.addActionListener(new java.awt.event.ActionListener() {				
				public void actionPerformed(java.awt.event.ActionEvent e) {					
					ExceptionHandlerConfigurationDecorator eh = (ExceptionHandlerConfigurationDecorator) lstTemplateExceptionHandlers.getSelectedValue();
					if (eh == null) {
						lblExHandlerMessage.setText("Select an Exception Handler from the left first");
						lblExHandlerMessage.setForeground(Color.RED);
						return;
					}
					if (txtExceptionClassName.getText().trim().length() <5) {
						lblExHandlerMessage.setText("provide at least 5 chars for the exception class name (like com.*)");
						lblExHandlerMessage.setForeground(Color.RED);
						return;
					}
					ExceptionHandlerConfigurationDecorator copy = eh.clone();				
					copy.setExceptionClassName(txtExceptionClassName.getText());
					AcmeGUI.getConfiguredExceptionHandlersDataModel().add(copy);
					((DefaultListModel)lstConfiguredExceptionHandlers.getModel()).addElement(copy);
					lblExHandlerMessage.setText("Added OK");
					lblExHandlerMessage.setForeground(Color.BLACK);
				}
			});
			
		}
		return btnAddExHandler;
	}

	private JButton getBtnRemove() {
		if (btnRemove == null) {
			btnRemove = new JButton();
			btnRemove.setBounds(new Rectangle(304, 216, 116, 29));
			btnRemove.setText("<--- Remove");			
			btnRemove.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {					
					ExceptionHandlerConfigurationDecorator eh = (ExceptionHandlerConfigurationDecorator) lstConfiguredExceptionHandlers.getSelectedValue();					
					int idx = lstConfiguredExceptionHandlers.getSelectedIndex();
					if (eh == null){
						lblExHandlerMessage.setText("Select an Exception Handler from the Right to remove");
						lblExHandlerMessage.setForeground(Color.RED);
						return;
					}
					AcmeGUI.getConfiguredExceptionHandlersDataModel().remove(idx);					
					((DefaultListModel) lstConfiguredExceptionHandlers.getModel()).remove(idx);
					lblExHandlerMessage.setText("Removed OK.");
					lblExHandlerMessage.setForeground(Color.BLACK);
				}
			});
			
		}
		return btnRemove;
	}

	private JButton getBtnConfigureExHandler() {
		if (btnConfigureExHandler == null) {
			btnConfigureExHandler = new JButton();
			btnConfigureExHandler.setBounds(new Rectangle(519, 419, 137, 28));
			btnConfigureExHandler.setText("Configure Params");
			btnConfigureExHandler.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ExceptionHandlerConfigurationDecorator eh = (ExceptionHandlerConfigurationDecorator) lstConfiguredExceptionHandlers.getSelectedValue();
					if (eh == null) {
						lblExHandlerMessage.setText("Select a value from  the right to configure");
						lblExHandlerMessage.setForeground(Color.RED);
						return;
					}
					lblExHandlerMessage.setText("update params...");
					lblExHandlerMessage.setForeground(Color.BLACK);
					PropertiesDialog pd = new PropertiesDialog(eh);
					pd.setModal(true);
					pd.setVisible(true);
					lblExHandlerMessage.setText("");
					
				}
			});
		}
		return btnConfigureExHandler;
	}

}  
