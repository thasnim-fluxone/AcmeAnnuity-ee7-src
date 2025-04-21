/*
package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

 

public class ExecuteWebURLEU extends AbastractAnnuityExecutionUnit {
	private static final long serialVersionUID = 5882648036323517725L;
	private static final String URL_KEY ="url";
	private static final String SUCCESS_MSG_KEY ="successMessage";
	private static final String USE_SECURITY_KEY = "useSecurity";	
	private static final String USER_NAME_KEY = "userName";
	private static final String USER_PASSWORD_KEY ="userPassword";
	private static final String WEB_APP_NAME_KEY = "webAppName";
	
	private static final String BASIC_AUTH = "BASIC_AUTH";
	private static final String FORM_AUTH = "FORM_AUTH";
				
	private String url = null;
	private String successMsg = null;
	private AcmeLogger logger = null;
	
	private String useSecurity ="NONE";
	private String userName;
	private String userPassword;
	private URL urlObj; 	
	private String webAppName;
	private static Map<Long, HttpClient> clients = Collections.synchronizedMap(new HashMap<Long, HttpClient>());
	
	public void execute() {				
		logger = getLogger(getClass().getName());	
		GetMethod getMethod = null;
		try {			
			setEUParams();
			HttpClient client = getClient();
			getMethod = new GetMethod(url);
			int responseCode = client.executeMethod(getMethod);
			logger.fine("URL execution response code is: " + responseCode);
			String response = getMethod.getResponseBodyAsString();			
			logger.fine("response is: " + response);
			if (successMsg == null) {
				return;
			}			
			if (response.indexOf(successMsg) > -1) {
				logger.fine("Accessed the URL:" + url + " and found Success Criteria");
			}else{
				String msg = "Accessed the URL: " + url + 
					" but could not find the Success Msg criteria of: '" + successMsg +"'";
				logger.info(msg);
				logger.info("URL Data was: " + response);
				throw new ExecutionUnitVerificationException(msg);
			}
		} catch (Exception e) {
			getExecutionUnitEvent().addException(e);			
		}finally{
			if (getMethod != null) getMethod.releaseConnection();
		}
	}
	
	
	private void setEUParams() throws InvalidExecutionUnitParameterException {
		url = getConfiguration().getParameterValue(URL_KEY);		
		if (url == null || url.trim().length()==0) {
			String msg = "The URL value is invalid. Its null or empty";
			logger.info(msg);
			throw new InvalidExecutionUnitParameterException(msg);
		}
		logger.fine("url: " + url);
		
		try {
			urlObj = new URL(url);
		} catch (MalformedURLException e) {
			String msg = "the URL provided is not a valid URL. Current value is: " + url 
				+ " Got a MalformedURLException when attempted to create a URL object of this URL. Error is: " + e;
			throw new InvalidExecutionUnitParameterException(msg);
			
		}
		
		successMsg = getConfiguration().getParameterValue(SUCCESS_MSG_KEY);
		if (successMsg== null || successMsg.trim().length()==0) {
			logger.warning("success Message for this url is null. This EU will not validate the Web Execution!!!");
			successMsg = null;
		}
		logger.fine("successMsg: " + successMsg);
		
		useSecurity = getConfiguration().getParameterValue(USE_SECURITY_KEY);
		if (useSecurity != null && useSecurity.trim().length() >=0) {			
			if ("BASIC_AUTH".equalsIgnoreCase(useSecurity)
					|| "FORM_AUTH".equalsIgnoreCase(useSecurity)
					|| "NONE".equalsIgnoreCase(useSecurity)){
				userName = getConfiguration().getParameterValue(USER_NAME_KEY);					
				userPassword = getConfiguration().getParameterValue(USER_PASSWORD_KEY);				
				if (userName == null || userName.trim().length() ==0) {
					String msg = "Connection to web is set to use security, but the userName is null or empty.  Current value is: " + userName;
					logger.info(msg);
					throw new InvalidExecutionUnitParameterException(msg);
				}
				if (userPassword == null || userPassword.trim().length() ==0) {
					String msg = "Connection to web is set to use security, but the userPassword is null or empty.  Current value is: " + userPassword;
					logger.info(msg);
					throw new InvalidExecutionUnitParameterException(msg);
				}				
				if ("FORM_AUTH".equalsIgnoreCase(useSecurity)){
					webAppName = getConfiguration().getParameterValue(WEB_APP_NAME_KEY);
					if (webAppName == null || webAppName.trim().length() ==0) {
						String msg = "Connection to web is set to use FORM security, but the webAppName is null or empty.  Current value is: " + webAppName;
						logger.info(msg);
						throw new InvalidExecutionUnitParameterException(msg);
					}
					logger.fine("useSecurity: " + useSecurity);
					logger.fine("userName: " + userName);
					logger.fine("userPassword: " + userPassword);
					logger.fine("webAppName: " + webAppName);
				}
				if ("NONE".equalsIgnoreCase(useSecurity)){
					logger.info("the useSecurity is set to NONE.  Security will not be used.");	
				}
				
			}else{
				throw new InvalidExecutionUnitParameterException("The useSecurity key is not valid.  " +
						"valid values are BASIC_AUTH, FORM_AUTH, or NONE. Provided value is: " + useSecurity);
			}
			
		}
	}
			
	private synchronized HttpClient getClient(){
		HttpClient client = clients.get(Thread.currentThread().getId());
		if (client == null){
			logger.fine("starting a new client.");
			client = new HttpClient();
			clients.put(Thread.currentThread().getId(), client);
			if (BASIC_AUTH.equalsIgnoreCase(useSecurity)){
				logger.fine("using BASIC AUTH Security");
				loginWithBasicAuth(client);
			}
			if (FORM_AUTH.equalsIgnoreCase(useSecurity)){
				logger.fine("using FORM AUTH Security");
				loginWithFormAuth(client);
			}
		}
		return client;
	}

	
	private void loginWithFormAuth(HttpClient client) {		
		logger.fine("Setting the client for FORM AUTH Security.");
		NameValuePair[] data = new NameValuePair[3];
		data[0] = new org.apache.commons.httpclient.NameValuePair("j_username", userName);
		data[1] = new NameValuePair("j_password", userPassword);
		data[2] = new NameValuePair("Login", "login");		
		
		String loginURL = urlObj.getProtocol() + "://" + urlObj.getHost()+ ":" + urlObj.getPort() + "/" + webAppName + "/j_security_check"; 
		logger.fine("the POST url for the Form Security is: " + loginURL);
		
		PostMethod loginPost = new PostMethod(loginURL);
		loginPost.addParameters(data);	
		try {
			int responseCode = client.executeMethod(loginPost);		
			logger.fine("response code is: " + responseCode);			
		} catch (HttpException e) {
			String msg = "Unexpected HTTPException when executing the post method with url: " + loginURL + " Error: " +e;
			logger.info(msg);
			throw new RuntimeException(msg, e);			
		} catch (IOException e) {
			String msg = "Unexpected IOException when executing the post method with url: " + loginURL + " Error: " +e;
			logger.info(msg);
			throw new RuntimeException(msg, e);			
		}finally{
				loginPost.releaseConnection();
		}	
		logger.fine("client is ready with form security");
	}

	private void loginWithBasicAuth(HttpClient client)  {		
		logger.fine("Setting the client for BASIC AUTH Security.");
		client.getParams().setAuthenticationPreemptive(true);
		Credentials defaultcreds = new UsernamePasswordCredentials(userName, userPassword);			
		AuthScope authScope = new AuthScope(urlObj.getHost(), urlObj.getPort(), AuthScope.ANY_REALM);
		client.getState().setCredentials(authScope, defaultcreds);			
		List<String> authPrefs = new ArrayList<String>(1);			
		authPrefs.add(AuthPolicy.BASIC);
		client.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
		logger.fine("Client is set for BASIC AUTH Security.");
	}
}
*/
