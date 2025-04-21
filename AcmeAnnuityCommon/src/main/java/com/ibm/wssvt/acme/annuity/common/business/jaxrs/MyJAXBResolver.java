package com.ibm.wssvt.acme.annuity.common.business.jaxrs;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

@Provider
public class MyJAXBResolver implements ContextResolver<JAXBContext> {

	@Override
	public JAXBContext getContext(Class arg0) {
		try {
			return JAXBContext.newInstance(arg0.getPackage().getName());
		} catch (JAXBException e) {
			return null;
		}
	}

}
