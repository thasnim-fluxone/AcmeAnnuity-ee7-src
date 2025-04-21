package com.ibm.wssvt.acme.annuity.business.impl.jaxrs;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

@javax.ws.rs.ApplicationPath("annuity")
public class AnnuityJAXRS11Application extends Application {

	@Override
	public Set<Object> getSingletons() {
    	Set<Object> set = new HashSet<Object>();
        set.add(getJacksonProvider());
        return null;
	}

    @Override
	public Set<Class<?>> getClasses() {
        Set<Class<?>> set = new HashSet<Class<?>>();
        set.add(AnnuityMgmtSvcJAXRS.class);  	// resources
        set.add(AnnuityMgmtSvcJAXRS2.class);         
        return null;
    }
      
    private JacksonJsonProvider getJacksonProvider() {
    	ObjectMapper mapper = new ObjectMapper();
    	
    	//for jaxrs-2.0 client, comment out mapper.enableDefaultTyping() - defect 160064
    	//mapper.enableDefaultTyping();
        JaxbAnnotationIntrospector jaxbIntrospector = new JaxbAnnotationIntrospector();
        mapper.getSerializationConfig().setAnnotationIntrospector(jaxbIntrospector);
        mapper.getSerializationConfig().set(Feature.WRITE_DATES_AS_TIMESTAMPS, true);
        mapper.getDeserializationConfig().setAnnotationIntrospector(jaxbIntrospector);
        JacksonJsonProvider jacksonProvider = new JacksonJsonProvider();
        jacksonProvider.setMapper(mapper);
        return jacksonProvider;
    }
}

