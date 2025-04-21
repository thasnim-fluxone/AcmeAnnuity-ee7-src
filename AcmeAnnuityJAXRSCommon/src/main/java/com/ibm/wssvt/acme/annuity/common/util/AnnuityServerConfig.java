package com.ibm.wssvt.acme.annuity.common.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AnnuityServerConfig {

	private static AnnuityServerConfig instance ;
	private Map<String, String> configs = Collections.synchronizedMap(new HashMap<String, String>());
	public static final String PROPS_FILE = "AnnuityServer.properties";
	
	public Map<String, String> getConfigs() {
		return configs;
	}
	
	private AnnuityServerConfig(){
		
	}
	
	public static synchronized AnnuityServerConfig init(String fileURL){
		instance = new AnnuityServerConfig();							
		try {
			if (fileURL == null || fileURL.trim().length() ==0 ){
				instance = null;
				throw new IllegalArgumentException("file  url is null or empty.  current value is: " + fileURL);
			}
			
			URL url= new URL(fileURL);
			Properties props = new Properties();
			props.load(url.openStream());
			for (Object key: props.keySet()){
				String value= (String)props.get(key);
				if (value != null) value = value.trim();
				instance.getConfigs().put((String)key, value);
			}				
		} catch (MalformedURLException e) {			
			e.printStackTrace();
			instance =  null;			
			throw new IllegalArgumentException("Got MalformedURLException.  URL is not valid. " +
					"Current value is: " + fileURL + ".  Error: " + e, e);
		} catch (IOException e) {
			e.printStackTrace();
			instance = null;
			throw new IllegalArgumentException("Got IOException when accessing properties file.   " +
					"Current value is: " + fileURL + ".  Error: " + e, e);
		} 
				
		return instance;
	}
	
	public static synchronized AnnuityServerConfig getInstance(){
		if (instance == null){
			throw new IllegalArgumentException("getInstance called, but the object is not inited." +
					"You must call init() first.  Also, it maybe that init() was called, but it did not complete OK.");
		}
		return instance;
	}

}
