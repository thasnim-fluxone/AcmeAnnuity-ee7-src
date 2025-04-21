package com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa;

import java.util.HashMap; 
import java.util.Map; 
import java.util.Map.Entry; 
import javax.xml.bind.annotation.adapters.XmlAdapter; 

public final class JAXBMapAdapter extends XmlAdapter<JAXBMapType,Map<String, String>> { 

@Override
public JAXBMapType marshal(Map<String, String> arg0) throws Exception { 
	JAXBMapType jaxbMapType = new JAXBMapType(); 
	for(Entry<String, String> entry : arg0.entrySet()) { 
		JAXBMapEntryType jaxbMapEntryType = 
			new JAXBMapEntryType(); 
		jaxbMapEntryType.setKey(entry.getKey()); 
		jaxbMapEntryType.setValue(entry.getValue()); 
		jaxbMapType.getEntry().add(jaxbMapEntryType); 
		} 
	return jaxbMapType; 
} 
@Override
public Map<String, String> unmarshal(JAXBMapType arg0) throws Exception 
{ 
	HashMap<String, String> hashMap = new HashMap<String, String>(); 
	for(JAXBMapEntryType jaxbEntryType : arg0.getEntry()) { 
		hashMap.put(jaxbEntryType.getKey(), jaxbEntryType.getValue()); 
	} 
	return hashMap; 
	} 
}
