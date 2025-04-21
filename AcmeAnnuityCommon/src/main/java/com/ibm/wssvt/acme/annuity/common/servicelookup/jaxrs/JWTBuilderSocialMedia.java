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
public class JWTBuilderSocialMedia implements ClientRequestFilter {
	
	public JwtBuilder myJwtBuilder = null;
	private AcmeLogger logger;
	private String serverURL;
	private String issuerURL;
	
	public JWTBuilderSocialMedia(String serverURL, AcmeLogger logger, String issuerURL ){
		this.logger = logger;
		this.serverURL = serverURL;
		this.issuerURL = issuerURL;
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
     * Builds the JWT Token from the claims of the JWT token using the JWT Builder Apis
     * @param idToken
     * @return
     * @throws Exception
     */
    
    public String createJwtFromUserProfile ()throws Exception {
        String jwtTokenString = null;        
        try {
            
        	        	
        	UserProfile userProfile = UserProfileManager.getUserProfile(); //get user profile data from social media
                    	
        	//Get the claims from the userProfile
        	Claims myClaims = userProfile.getClaims();  



    /*                            
            Subject subject = WSSubject.getRunAsSubject();
            JwtToken jwtToken = null;
            Iterator<JwtToken> jwtTokenIterator = subject.getPrivateCredentials(JwtToken.class).iterator();
                if (jwtTokenIterator.hasNext()) {
                    jwtToken = jwtTokenIterator.next();
                }
        
            Claims myClaims = jwtToken.getClaims();
*/
            logger.fine("Claims from UserProfile are: "+ myClaims.toJsonString());
           
           /* 
            System.out.println( "Claims from UserProfile are: "+ myClaims.toJsonString());
            System.out.println( "Issuer is: " + myClaims.getIssuer());
            System.out.println( "Expiration is: " + myClaims.getExpiration());
            System.out.println( "Name is: " + myClaims.getClaim("name", String.class));
            System.out.println( "Email is: " + myClaims.getClaim("email", String.class));
            System.out.println( "Subject is: " + myClaims.getSubject());
            
            String userId= myClaims.getClaim("id", String.class);
            System.out.println( "Id is: " + userId);
            logger.fine("Userid from UserProfile is: "+ userId); 
       */
            
     
        	JwtBuilder jwtBuilder = JwtBuilder.create();
        	//System.out.println("JWTBuilder (default) is: "+ jwtBuilder);
            
        	//Audience is a list of Strings. Get the JAXRS server url from the config file, since that will be the audience
        	ArrayList<String> audience = new ArrayList<String>();
        	
        	audience.add(serverURL);
      	
        	//Create the jwtToken from the claims from the uniqueSecurityName
        	
        	jwtBuilder.subject(myClaims.getClaim("uniqueSecurityName", String.class))
        	.claim("aud", audience)
        	.issuer(issuerURL)
            .claim("uniqueSecurityName", myClaims.getClaim("uniqueSecurityName", String.class));
        	
        	
        	
        	jwtTokenString = jwtBuilder.buildJwt().compact();
            logger.fine("JWT Token built is: "+ jwtTokenString); 
            
           // logger.fine("JWT Token built Claims are: "+ (jwtToken.getClaims()).toJsonString());
           
           
        } catch (InvalidClaimException e){        	
        	logger.warning("Invalid Claim for building the JWT. Exception is: " + e);
			e.printStackTrace();
        	throw new Exception(e);
        }       
        catch (Exception e){        	
        	logger.warning("Invalid Builder/JWT Exception while building the JWT. Exception is: " + e);
        	throw new Exception(e);
        }       
        return jwtTokenString;
    }
}