package com.ibm.wssvt.acme.annuity.common.servicelookup.jaxrs;


import java.io.IOException;


//import java.io.UnsupportedEncodingException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.DatatypeConverter;

//import com.ibm.wssvt.acme.common.bean.Configrable;
//import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class BasicAuthFilter implements ClientRequestFilter {

    private final String usr;
    private final String pwd; 
    
    public BasicAuthFilter(String user, String password) {

        this.usr = user;
        this.pwd = password;
        
    }

    public void filter(ClientRequestContext requestContext) throws IOException {
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        
	 String token = this.usr + ":" + this.pwd;
	 
	 final String basicAuthentication ="Basic " + DatatypeConverter.printBase64Binary(token.getBytes("UTF-8"));
	 headers.add("Authorization", basicAuthentication);
	
    }
}
