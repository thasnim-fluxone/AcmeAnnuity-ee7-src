package com.ibm.wssvt.acme.annuity.common.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("unchecked")
public class ServerInjectedObjects {

	private static ServerInjectedObjects instance = null;
	private Map<String, Object> injObjects = Collections.synchronizedMap(new HashMap<String, Object>());
	private ServerInjectedObjects(){
		
	}
	
	public synchronized static ServerInjectedObjects getInstance(){
		if (instance == null) {
			instance = new ServerInjectedObjects();			
		}
		return instance;
	}
	
	public synchronized void addObject(String key, Object value){
		synchronized(injObjects){
			injObjects.put(key, value);
		}
	}
	
	public synchronized Object getObject(String key){
		synchronized(injObjects){
			return injObjects.get(key);
		}
	}
}
