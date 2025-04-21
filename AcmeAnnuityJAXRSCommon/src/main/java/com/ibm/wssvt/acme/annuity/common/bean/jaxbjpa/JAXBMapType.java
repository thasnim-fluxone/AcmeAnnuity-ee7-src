package com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa;

import java.util.ArrayList; 
import java.util.List;   

public class JAXBMapType {      
	private List<JAXBMapEntryType> entry = new ArrayList<JAXBMapEntryType>();   
	
	public List<JAXBMapEntryType> getEntry() {
        return entry;
    }

	public void setEntry(List<JAXBMapEntryType> entry) {
		this.entry = entry;
	}
}