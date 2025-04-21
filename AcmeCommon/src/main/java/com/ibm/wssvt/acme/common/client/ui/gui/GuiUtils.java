package com.ibm.wssvt.acme.common.client.ui.gui;

import java.awt.Color;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JLabel;

public class GuiUtils {
	
	public static void setErrorMessage(JLabel lbl, String msg) {
		lbl.setForeground(new Color(255, 51, 51));
		lbl.setText(msg);	
	}

	public static void setInfoMessage(JLabel lbl, String msg) {
		lbl.setForeground(new Color(51, 51, 51));
		lbl.setText(msg);	
	}
	
	public static File getOpenFile(){
		JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fileChooser.getSelectedFile();
          return selectedFile;		
        }else{
        	return null;
        }
	}
	
	public static File getSaveFile(){
		JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fileChooser.getSelectedFile();
          return selectedFile;		
        }else{
        	return null;
        }
	}
}
