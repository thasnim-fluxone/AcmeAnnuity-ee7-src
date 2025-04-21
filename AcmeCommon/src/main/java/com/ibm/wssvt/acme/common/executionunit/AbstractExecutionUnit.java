package com.ibm.wssvt.acme.common.executionunit;

import java.util.ArrayList;
import java.util.List;

import com.ibm.wssvt.acme.common.adapter.IServerAdapter;
import com.ibm.wssvt.acme.common.bean.IBeansFactory;
import com.ibm.wssvt.acme.common.bean.Parameterizable;
import com.ibm.wssvt.acme.common.util.ClientContext;

/**
 * $Rev: 773 $
 * $Date: 2007-09-13 11:32:04 -0500 (Thu, 13 Sep 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public abstract class AbstractExecutionUnit implements IExecutionUnit {
	private static final long serialVersionUID = 8688712524892475458L;
	private String description;
	private IServerAdapter adapter;
	private IBeansFactory beansFactory;
	// this must be set by the Config Loader
	private Parameterizable<String, String> euConfig = null; 
	private List<IExecutionUnitEventListener> listeners = new ArrayList<IExecutionUnitEventListener>();
	private IExecutionUnitEvent executionUnitEvent = new ExecutionUnitEvent();	
	private ClientContext clientContext;
	
	public AbstractExecutionUnit() {
		this.executionUnitEvent.setExecutionUnit(this);
	}
	
	public synchronized void addEventListener(IExecutionUnitEventListener listener) {
		this.listeners.add(listener);	
	}

	public synchronized boolean removeEventListener(IExecutionUnitEventListener listener) {
		return this.listeners.remove(listener);
		
	}
	
	public IServerAdapter getServerAdapter() {
		return this.adapter;
	}

	public void setServerAdapter(IServerAdapter adapter) {
		this.adapter = adapter;
	}
			

	public String getDescription() {
		return this.description;		
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public synchronized void fireToListeners(){
		for(IExecutionUnitEventListener listener: listeners){
			listener.processExecutionUnitEvent(this.executionUnitEvent);
		}
		this.executionUnitEvent.clearAll();
	}
	
	public synchronized void clearListeners(){
		this.listeners.clear();
	}
	
	public IExecutionUnitEvent getExecutionUnitEvent() {
		return executionUnitEvent;
	}

	public void setExecutionUnitEvent(IExecutionUnitEvent executionUnitEvent) {
		this.executionUnitEvent = executionUnitEvent;
	}
	public synchronized void clearEvents(){
		this.executionUnitEvent.clearAll();		
		
	}
			
	public Parameterizable<String, String> getConfiguration() {
		return this.euConfig;
	}
	
	public void setConfiguration(Parameterizable<String, String> config) {
		this.euConfig = config;				
	}
	
	public ClientContext getClientContext() {
		return this.clientContext;
	}

	public void setClientContext(ClientContext clientContext) {
		this.clientContext = clientContext;
		
	}

	public IBeansFactory getBeansFactory() {
		return beansFactory;
	}

	public void setBeansFactory(IBeansFactory beansFactory) {
		this.beansFactory = beansFactory;
	}
		


}