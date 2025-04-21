package com.ibm.wssvt.acme.annuity.web;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

//import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceRef;
//import javax.xml.ws.WebServiceRef;

import com.ibm.wssvt.acme.annuity.common.business.ejb30.AnnuityMgmtSvcEJB30;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.AnnuityMgmtSvcEJB30Local;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.policy.PolicyMgmtSvcEJB30;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.policy.PolicyMgmtSvcEJB30Local;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.AnnuityMgmtSvcEJB30JAXWSImpl;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl.AnnuityMgmtSvcPojoJAXWSImpl;

//import com.ibm.wssvt.acme.annuity.common.client.jaxrpc.ejb2impl.AnnuityMgmtSvcEJB2XJAXRPCImpl;
//import com.ibm.wssvt.acme.annuity.common.client.jaxrpc.ejb3impl.AnnuityMgmtSvcEJB3JAXRPCImpl;
//import com.ibm.wssvt.acme.annuity.common.client.jaxrpc.pojoimpl.AnnuityMgmtSvcPojoJAXRPCImpl;
//import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.AnnuityMgmtSvcEJB30JAXWSImpl;
//import com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl.AnnuityMgmtSvcPojoJAXWSImpl;
//import com.ibm.wssvt.acme.annuity.common.util.RunningServerInfo;
import com.ibm.wssvt.acme.common.client.AcmeJavaClient;
import com.ibm.wssvt.acme.common.client.IExecutionUnitRunner;
import com.ibm.wssvt.acme.common.client.IReportedMessageVisitor;
import com.ibm.wssvt.acme.common.envconfig.ApplicationEnvConfigBase;
import com.ibm.wssvt.acme.common.envconfig.IApplicationEnvConfig;
import com.ibm.wssvt.acme.common.envconfig.exception.ConfigurationException;
import com.ibm.wssvt.acme.common.executionunit.IExecutionUnit;
import com.ibm.wssvt.acme.common.stats.ExecutionStats;
import com.ibm.wssvt.acme.common.stats.ExecutionStatsFormatter;
import com.ibm.wssvt.acme.common.util.ClientContext;
import com.ibm.wssvt.acme.common.util.ClientType;

public class AnnuityWebServlet extends HttpServlet{
	
	@EJB AnnuityMgmtSvcEJB30 ejb3Remote;
	@EJB AnnuityMgmtSvcEJB30Local ejb3Local;
	@EJB PolicyMgmtSvcEJB30Local ejb3PolicyLocal;
	@EJB PolicyMgmtSvcEJB30 ejb3PolicyRemote;
	
	@WebServiceRef (name="services/AnnuityMgmtSvcPojoJAXWSImpl",wsdlLocation="wsdl/AnnuityMgmtSvcPojoJAXWSImpl.wsdl")
	private static AnnuityMgmtSvcPojoJAXWSImpl pojoJaxws;
		
	//old way - didn't work for EJBWS security
	//@WebServiceRef(wsdlLocation="wsdl/AnnuityMgmtSvcEJB30JAXWSImpl.wsdl")
		
	@WebServiceRef(name="services/AnnuityMgmtSvcEJB30JAXWSImpl",wsdlLocation="wsdl/AnnuityMgmtSvcEJB30JAXWSImpl.wsdl")
	private static AnnuityMgmtSvcEJB30JAXWSImpl ejb3jaxws;

	
	//@Resource(name="services/AnnuityMgmtSvcPojoJAXRPCImpl")	
	//private static AnnuityMgmtSvcPojoJAXRPCImpl pojoJaxrpc;
	
	//@Resource(name="services/AnnuityMgmtSvcEJB3JAXRPCImpl")	
	//private static AnnuityMgmtSvcEJB3JAXRPCImpl ejb3Jaxrpc;	

	//note its 2x, not 2X - generated code!!
	//@Resource(name="services/AnnuityMgmtSvcEJB2xJAXRPCImpl")
	//private static AnnuityMgmtSvcEJB2XJAXRPCImpl ejb2Jaxrpc;
/*
 *These 2 jaxrpc to jaxws clients are disabled due to defect # 561655
	// jaxrpc2pojojaxws
	@Resource(name="services/AnnuityMgmtSvcPojoJAXWSImpl")	
	private static com.ibm.wssvt.acme.annuity.common.client.jaxrpc.jaxrpc2pojojaxwsimpl.AnnuityMgmtSvcPojoJAXWSImpl jaxrpc2PojoJAXWS;		
	
	//jaxrpc2EJB3jaxws
	@Resource(name="services/AnnuityMgmtSvcEJB30JAXWSImpl")	
	private static com.ibm.wssvt.acme.annuity.common.client.jaxrpc.jaxrpc2ejb3jaxwsimpl.AnnuityMgmtSvcEJB30JAXWSImpl jaxrpc2Ejb3JAXWS;
*/
	private static final long serialVersionUID = 1L;
	private Long startTime;

	public AnnuityWebServlet() {
		super();
	}   	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		doPost(request, response);		
	}  	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {				
		PrintWriter out = response.getWriter();
		out.println("<HTML>");						
		try{
			String fileName = request.getParameter("fileName");
			if (fileName == null || fileName.trim().length() ==0 ) {
				//out.println("<BR>Servlet hosted on: " + RunningServerInfo.getInstance().getRunningServerInfo());
				out.println("<BR>Servlet Web Execution FAILED. You must provide configuration file name.");				
				issueEndLinesHtml(out);
				return;
			} else {
				out.println("<BR>Running with configuration from file: " + fileName);
				//out.println("<BR>Servlet hosted on: " + RunningServerInfo.getInstance().getRunningServerInfo());
				startTime = System.currentTimeMillis();
			}
			
			IApplicationEnvConfig configLoader = ApplicationEnvConfigBase.init(fileName);
			List<IExecutionUnit> executionUnits = configLoader.getExecutionUnits();	
			if (executionUnits.size() <1) {
				out.println("<BR>Servlet Web Execution FAILED. There are no scenarios in this file to execute.");
				issueEndLinesHtml(out);
				return;
			} else{							
				out.println("<BR>Servlet Execution is Starting ...");
				if (request.getParameter("resetExecutionStatsFlag") != null) {
					ExecutionStats.getInstance().reset();
					out.println("<BR>Execution Stats were reset.");
				}
				AcmeJavaClient clnt = new AcmeJavaClient();
				ClientContext clientContext = new ClientContext("SERVLET", 0);	
				clientContext.getInjectedObjects().put("AnnuityMgmtSvcEJB30Local", ejb3Local);
				clientContext.getInjectedObjects().put("PolicyMgmtSvcEJB30Local", ejb3PolicyLocal);
				clientContext.getInjectedObjects().put("AnnuityMgmtSvcEJB30", ejb3Remote);
				clientContext.getInjectedObjects().put("PolicyMgmtSvcEJB30", ejb3PolicyRemote);				
				clientContext.getInjectedObjects().put("AnnuityMgmtSvcPojoJAXWSImpl", pojoJaxws);
				clientContext.getInjectedObjects().put("AnnuityMgmtSvcEJB30JAXWSImpl", ejb3jaxws);
				//clientContext.getInjectedObjects().put("AnnuityMgmtSvcPojoJAXRPCImpl", pojoJaxrpc);
				//clientContext.getInjectedObjects().put("AnnuityMgmtSvcEJB3JAXRPCImpl", ejb3Jaxrpc);
				//clientContext.getInjectedObjects().put("AnnuityMgmtSvcEJB2XJAXRPCImpl", ejb2Jaxrpc);
				/*  disabled due to defect 561655
				clientContext.getInjectedObjects().put("JAXRPC2PojoJAXWS_AnnuityMgmtSvcPojoJAXWSImpl", jaxrpc2PojoJAXWS);
				clientContext.getInjectedObjects().put("JAXRPC2EJB3JAXWS_AnnuityMgmtSvcPojoJAXWSImpl", jaxrpc2Ejb3JAXWS);
				*/
				configLoader.getClientConfiguration().setRunTime(0);
				configLoader.getClientConfiguration().setThreadCount(1);
				clientContext.setRootLoggerName("com.ibm.wssvt.acme.annuity");
				clientContext.setClientType(ClientType.WEB);
				ServletReportedMessageVisitor servletReportedMessageVisitor = new ServletReportedMessageVisitor(out);
				clnt.executeClient(configLoader, clientContext, null, null,
						servletReportedMessageVisitor, new ExecutionStatsFormatter(ExecutionStatsFormatter.HTML_NEW_LINE));							
			}			
			out.println("<BR>Execution Time: " + (System.currentTimeMillis() - startTime) + " ms.");					
		}catch (ConfigurationException e) {
			String msg = "<BR>Servlet Web Execution FAILED.  Error in Configuration. Error: " + e;
			System.out.println(msg);
			e.printStackTrace();
			out.println("" + msg);		
		}catch (Throwable t) {
			String msg = "<BR>Servlet Web Execution FAILED.  Unexpected error was reported in the AnnuityWebServlet.java. Error: " + t;
			System.out.println(msg);
			t.printStackTrace();
			out.println("" + msg);			
		}
		out.println("");
		issueEndLinesHtml(out);
	}
	
	private void issueEndLinesHtml(PrintWriter out) {
		out.println("<BR>Servlet Execution Completed.");
		out.println("</html>");
	}
	
	private class ServletReportedMessageVisitor implements IReportedMessageVisitor {
		private PrintWriter out;
		public ServletReportedMessageVisitor(PrintWriter out){
			this.out = out;	
		}
		public void visit(IExecutionUnitRunner runner) {
			out.println("<BR>" + runner.getReportedMessage());
			
		}
		
	}
}
