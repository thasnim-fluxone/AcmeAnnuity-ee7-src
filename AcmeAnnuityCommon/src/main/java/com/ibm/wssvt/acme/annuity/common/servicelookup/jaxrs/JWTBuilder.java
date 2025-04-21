package com.ibm.wssvt.acme.annuity.common.servicelookup.jaxrs;


import java.io.IOException;
import java.util.ArrayList;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import com.ibm.websphere.security.openidconnect.PropagationHelper;
import com.ibm.websphere.security.openidconnect.token.IdToken;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
import com.ibm.websphere.security.jwt.InvalidClaimException;
import com.ibm.websphere.security.jwt.JwtBuilder;


/* rhaque@us.ibm.com 10/26/2016
 * This code was added to support the propagation of a JWT Token and builder (RTC 222178 - Support for JWT propagation & JWT API)
 * This class is used to build the JWT token from the oauth token id using the JWT Builder Apis
 * For the case of ACME, since the header is in the format of a MultivaluedMap, the Oauth/JWT header 
 * had to be added to the header using the add method. We could not just add the header with a .header to the request
 * 
 */
public class JWTBuilder implements ClientRequestFilter {
	
	public JwtBuilder myJwtBuilder = null;
	private AcmeLogger logger;
	private String serverURL;
	private String issuerURL;
	
	public JWTBuilder(String serverURL, AcmeLogger logger, String issuerURL ){
		this.logger = logger;
		this.serverURL = serverURL;
		this.issuerURL = issuerURL;
	}
	
    public void filter(ClientRequestContext requestContext) throws IOException {
        
    	MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        
    	
    	//Get the idTokn from the Opaque Token obtained from the OP
    	IdToken myIdToken = PropagationHelper.getIdToken();
    	    	   	
	    //System.out.println("Access Token ID is: "+ myIdToken);
	    logger.fine("ID_Token from Oauth token is:" + myIdToken);  	    
	    
	    
	    String myJwtToken = null;
	    try{
	    	//create the JWT Token using the idToken
	    	myJwtToken = createJwtFromIdToken (myIdToken);	    	    	
	    	
	    	
	    	//add the JWT Token to the header	    	
	    	final String jwtAuthentication ="Bearer " + myJwtToken;
        	 headers.add("Authorization", jwtAuthentication);
	    	
	    }
	    catch (Exception e)
	    {
	    	throw new IOException(e);
	    }
    }
    
    /**
     * Builds the JWT Token from the id_token (of the opaque Oauth Token) using the JWT Builder Apis
     * @param idToken
     * @return
     * @throws Exception
     */
    
    public String createJwtFromIdToken (IdToken idToken)throws Exception {
        String jwtToken = null;        
        try {
            
        	
        	JwtBuilder jwtBuilder = JwtBuilder.create();
        	//System.out.println("JWTBuilder (default) is: "+ jwtBuilder);
            
        	//Audience is a list of Strings. Get the JAXRS server url from the config file, since that will be the audience
        	ArrayList<String> audience = new ArrayList<String>();
        	
        	audience.add(serverURL);
        	
        	//Create the JWT token - using the groupId and Subject from the Oauth token.
        	//However - we need to get the audience and the issuerUrl from the configuration 
            jwtBuilder.claim("groupIds", idToken.getClaim("groupIds"))
            .claim("aud", audience)          
            .issuer(issuerURL)
            .subject(idToken.getSubject());
                       
          //  logger.fine("JWT Token built Claims are: "+ jwtBuilder.getClaims().toJsonString());           
            
            jwtToken = jwtBuilder.buildJwt().compact();
            logger.fine("JWT Token built is: "+ jwtToken);          
            //System.out.println("JWT Token is: "+ jwtToken);
        } catch (InvalidClaimException e){        	
        	logger.warning("Invalid Claim for building the JWT. Exception is: " + e);
			e.printStackTrace();
        	throw new Exception(e);
        }       
        catch (Exception e){        	
        	logger.warning("Invalid Builder/JWT Exception while building the JWT. Exception is: " + e);
        	throw new Exception(e);
        }       
        return jwtToken;
    }
}