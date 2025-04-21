package com.ibm.wssvt.acme.annuity.common.servicelookup.jaxrs;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.security.auth.Subject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import com.ibm.websphere.security.openidconnect.PropagationHelper;
import com.ibm.websphere.security.openidconnect.token.IdToken;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
import com.ibm.websphere.security.jwt.Claims;
import com.ibm.websphere.security.jwt.InvalidClaimException;
import com.ibm.websphere.security.jwt.JwtBuilder;
import com.ibm.websphere.security.jwt.JwtToken;
import com.ibm.websphere.security.social.UserProfile;
import com.ibm.websphere.security.social.UserProfileManager;
import com.ibm.websphere.security.auth.WSSubject;


/* rhaque@us.ibm.com 10/26/2016
 * This code was added to support the propagation of a JWT Token and builder (RTC 222178 - Support for JWT propagation & JWT API)
 * This class is used to build the JWT token from the oauth token id using the JWT Builder Apis
 * For the case of ACME, since the header is in the format of a MultivaluedMap, the Oauth/JWT header 
 * had to be added to the header using the add method. We could not just add the header with a .header to the request
 * This code gets the JWT Token from the SocialMedia login to create a new JWT using the Builder API.
 */
public class JWTSocialMedia implements ClientRequestFilter {
	
	public JwtBuilder myJwtBuilder = null;
	private AcmeLogger logger;
	//private String serverURL;
	//private String issuerURL;
	
	public JWTSocialMedia(AcmeLogger logger ){
		this.logger = logger;
	//	this.serverURL = serverURL;
	//	this.issuerURL = issuerURL;
	}
	
    public void filter(ClientRequestContext requestContext) throws IOException {
        
    	MultivaluedMap<String, Object> headers = requestContext.getHeaders();
            	
	    String myJwtToken = null;
	    try{
	    	//create the JWT Token from the userProfile
	    	myJwtToken = createJwtFromUserProfile();	    	    	
	    	
	    	
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
     * Get the JWT Token from the claims of the JWT token from the OP
     * @param idToken
     * @return
     * @throws Exception
     */
    
    public String createJwtFromUserProfile ()throws Exception {
        String jwtTokenString = null;        
        try {
            
        	        	
        	UserProfile userProfile = UserProfileManager.getUserProfile(); //get user profile data from social media
        	jwtTokenString = userProfile.getAccessToken();      	
        	logger.fine("JWT Access Token from UserProfile is: "+ jwtTokenString);          
        }       
        catch (Exception e){        	
        	logger.warning("Invalid Exception while getting the JWT Access Token from Social Media. Exception is: " + e);
        	throw new Exception(e);
        }       
        return jwtTokenString;
    }
}