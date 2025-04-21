package com.ibm.wssvt.acme.common.adapter;

import java.util.ArrayList;
import java.util.List;

import com.ibm.wssvt.acme.common.bean.Parameterizable;
import com.ibm.wssvt.acme.common.bean.StringParameterizable;
import com.ibm.wssvt.acme.common.util.ClientContext;
/**
 * $Rev: 672 $
 * $Date: 2007-08-16 17:35:23 -0500 (Thu, 16 Aug 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public abstract class AbstractServerAdapter implements IServerAdapter{
	
	private static final long serialVersionUID = 5500690194439215059L;
	private Parameterizable<String, String> adapterConfig = new StringParameterizable();
	private ClientContext clientContext;
	private String id;
	private List<IServerAdapterEventListener> listeners = new ArrayList<IServerAdapterEventListener>();
	private IServerAdapterEvent serverAdapterEvent = new ServerAdapterEvent();
	
	public AbstractServerAdapter(){
		serverAdapterEvent.setServerAdapter(this);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public synchronized void fireToListeners(){
		for(IServerAdapterEventListener listener: listeners){
			listener.processServerAdapterEvent(this.serverAdapterEvent);
		}
		this.serverAdapterEvent.clearAll();
	}
	
	public synchronized void clearListeners(){
		this.listeners.clear();
	}
	
	public synchronized IServerAdapterEvent getServerAdapterEvent() {
		return this.serverAdapterEvent;
	}

	public synchronized void setServerAdapterEvent (IServerAdapterEvent event) {
		this.serverAdapterEvent = event;
	}
	public synchronized void clearEvents(){
		this.serverAdapterEvent.clearAll();		
		
	}
	
	public synchronized void addEventListener(IServerAdapterEventListener listener) {
		this.listeners.add(listener);	
	}

	public synchronized boolean removeEventListener(IServerAdapterEventListener listener) {
		return this.listeners.remove(listener);
		
	}
	
	public synchronized Parameterizable<String, String> getConfiguration() {
		return this.adapterConfig;
	}

	public  void setConfiguration(Parameterizable<String, String> config) {
		this.adapterConfig = config;
		
	}

	public synchronized ClientContext getClientContext() {
		return this.clientContext;
	}

	public synchronized void setClientContext(ClientContext clientContext) {
		this.clientContext = clientContext;
		
	}
		
}
