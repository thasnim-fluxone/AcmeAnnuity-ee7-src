package com.ibm.wssvt.acme.annuity.business.impl.jaxrs.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

@Provider
@Consumes( { MediaType.APPLICATION_JSON, "text/json" })
@Produces( { MediaType.APPLICATION_JSON, "text/json" })
public class MyJacksonDelegatorProvider implements MessageBodyReader<Object>,
		MessageBodyWriter<Object> {

	JacksonJaxbJsonProvider jacksonProvider;

	public MyJacksonDelegatorProvider() {
		ObjectMapper mapper = new ObjectMapper();
		//comment out the following because of defect 160064 & 151630
		//Rumana uncommenting on 3/4/22
		mapper.enableDefaultTyping();
		mapper.getSerializationConfig().setSerializationInclusion(
				Inclusion.NON_NULL);
		AnnotationIntrospector pair = new AnnotationIntrospector.Pair(
				new JaxbAnnotationIntrospector(),
				new JacksonAnnotationIntrospector());
		mapper.getDeserializationConfig().setAnnotationIntrospector(pair);
		mapper.getSerializationConfig().setAnnotationIntrospector(pair);
		this.jacksonProvider = new JacksonJaxbJsonProvider();
		jacksonProvider.setMapper(mapper);
	}

	public long getSize(Object t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return jacksonProvider.getSize(t, type, genericType, annotations,
				mediaType);
	}

	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return jacksonProvider.isWriteable(type, genericType, annotations,
				mediaType);
	}

	public void writeTo(Object t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException {
		jacksonProvider.writeTo(t, type, genericType, annotations, mediaType,
				httpHeaders, entityStream);
	}

	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return jacksonProvider.isReadable(type, genericType, annotations,
				mediaType);
	}

	public Object readFrom(Class<Object> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException {
		return jacksonProvider.readFrom(type, genericType, annotations,
				mediaType, httpHeaders, entityStream);
	}

}
