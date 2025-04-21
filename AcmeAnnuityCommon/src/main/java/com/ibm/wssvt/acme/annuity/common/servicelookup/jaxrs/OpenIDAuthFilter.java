package com.ibm.wssvt.acme.annuity.common.servicelookup.jaxrs;


import java.io.IOException;



import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import com.ibm.websphere.security.openidconnect.PropagationHelper;


/* rhaque@us.ibm.com 09/09/2016
 * This code was added to support the propagation of a Oauth token from a JAXRS client to server (RTC 184646) - which can also be of type
 * JWT (RTC 207804).
 * For the case of ACME, since the header is in the format of a MultivaluedMap, the Oauth/JWT header 
 * had to be added to the header using the add method. We could not just add the header with a .header to the request
 * 
 */
public class OpenIDAuthFilter implements ClientRequestFilter {
		
    public void filter(ClientRequestContext requestContext) throws IOException {
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        
	    String myAccessToken = PropagationHelper.getAccessToken();
	           
        final String samlAuthentication ="Bearer " + myAccessToken;
        	 headers.add("Authorization", samlAuthentication);
    }
}
