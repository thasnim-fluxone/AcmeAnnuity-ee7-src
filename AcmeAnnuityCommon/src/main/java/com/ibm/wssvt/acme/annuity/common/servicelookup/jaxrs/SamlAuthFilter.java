package com.ibm.wssvt.acme.annuity.common.servicelookup.jaxrs;


import java.io.IOException;



import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import com.ibm.websphere.security.saml2.PropagationHelper;

/* rhaque@us.ibm.com 08/31/2016
 * This code was added to support the propagation of a SAML token from a JAXRS client to server.
 * For the case of ACME, since the header is in the format of a MultivaluedMap, the SAML header 
 * had to be added to the header using the add method. We could not just add the header with a .header to the request
 * 
 */
public class SamlAuthFilter implements ClientRequestFilter {

    public void filter(ClientRequestContext requestContext) throws IOException {
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        
	    String mySAML = PropagationHelper.getEncodedSaml20Token(true);
        
        final String samlAuthentication ="SAML " + mySAML;
        	 headers.add("Authorization", samlAuthentication);
    }
}
