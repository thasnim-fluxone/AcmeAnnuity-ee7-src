package com.ibm.wssvt.acme.annuity.common.util;


import java.io.Serializable;

import com.ibm.wssvt.acme.common.bean.Configrable;

public class AnnuityJMSMessage implements Serializable{
	private static final long serialVersionUID = 7756987146024613767L;
	private AnnuityAction action;
	private Configrable<String, String> input;
	private Object output;
		
	public AnnuityJMSMessage(AnnuityAction action,
			Configrable<String, String> input, Object output) {
		super();
		this.action = action;
		this.input = input;
		this.output = output;
	}
	
	public AnnuityJMSMessage() {
		super();
		// TODO Auto-generated constructor stub
	}
	public AnnuityAction getAction() {
		return action;
	}
	public void setAction(AnnuityAction action) {
		this.action = action;
	}
	public Configrable<String, String> getInput() {
		return input;
	}
	public void setInput(Configrable<String, String> input) {
		this.input = input;
	}
	public Object getOutput() {
		return output;
	}
	public void setOutput(Object output) {
		this.output = output;
	}
			
	
}
