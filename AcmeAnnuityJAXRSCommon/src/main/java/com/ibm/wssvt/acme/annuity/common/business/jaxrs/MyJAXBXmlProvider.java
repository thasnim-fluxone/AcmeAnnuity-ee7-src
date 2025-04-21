package com.ibm.wssvt.acme.annuity.common.business.jaxrs;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBElement;

import org.apache.wink.common.internal.providers.entity.xml.JAXBXmlProvider;

/*
 * for now, I'm just extending the JAXBXmlProvider.  This probably won't work when WINK is
 * delivered as an OSGi bundle in WAS v8 due to OSGi protection of internal WINK classes.
 * At that time, we'll have to copy the code from JAXBXmlProvider into here, keeping the code below.
 */

@Provider
@Consumes( {MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD})
@Produces( {MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD})
public class MyJAXBXmlProvider extends JAXBXmlProvider {

	@Override
	public Object readFrom(Class<Object> arg0, Type arg1, Annotation[] arg2,
			MediaType arg3, MultivaluedMap<String, String> arg4,
			InputStream arg5) throws IOException, WebApplicationException {
		Object ret = super.readFrom(arg0, arg1, arg2, arg3, arg4, arg5);

		// It's possible the 'ret' object is still a JAXBElement, due to the presence of
		// creator method in ObjectFactory that returns JAXBElement, and the instantiation
		// of JAXBContext.newInstance(String) where String is the package name of the desired object.
		// This is probably an indicator that the JAXB objects and ObjectFactory have been
		// manipulated by-hand, and this is indeed the case with this SVT app.
		
		// Also, Wink in open source already has this fix:  see WINK-229
		
		if (ret instanceof JAXBElement) {
			return ((JAXBElement)ret).getValue();
		}
		return ret;
	}

}
