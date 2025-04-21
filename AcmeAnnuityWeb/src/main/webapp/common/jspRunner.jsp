<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="com.ibm.wssvt.acme.common.envconfig.ApplicationEnvConfigBase"%>
<%@page import="com.ibm.wssvt.acme.common.envconfig.exception.ConfigurationException"%>
<%@page import="com.ibm.wssvt.acme.common.client.AcmeJavaClient"%>
<%@page import="com.ibm.wssvt.acme.common.util.ClientContext"%>
<%@page import="com.ibm.wssvt.acme.common.stats.ExecutionStats"%>
<%@page import="com.ibm.wssvt.acme.common.client.IExecutionUnitRunner"%>
<%@page import="com.ibm.wssvt.acme.common.client.IReportedMessageVisitor"%>
<%@page import="com.ibm.wssvt.acme.common.stats.ExecutionStatsFormatter"%>
<%@page import="com.ibm.wssvt.acme.common.util.ClientType"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>ACME Annuity Web Scenario Runner</title>
</head>
<body>
Test run time:  <%=new Date()%>
<br>

<%!
private class JSPReportedMessageVisitor implements IReportedMessageVisitor {
		private JspWriter out;
		public JSPReportedMessageVisitor(JspWriter out){
			this.out = out;	
		}
		public void visit(IExecutionUnitRunner runner) {
			try{
				out.println(runner.getReportedMessage() + "<BR>");
			}catch (Throwable t) {
				t.printStackTrace();
			}			
		}
		
	}
%>
	
<%
	try{
		String fileName = request.getParameter("fileName");
		if (fileName == null || fileName.trim().length() ==0 ) {
			//out.println("<BR> JSP hosted on: " + RunningServerInfo.getInstance().getRunningServerInfo());
			out.println("<BR> JSP Web Execution FAILED.  You must provide configuration file URL.");
			out.println("<p> JSP Execution Completed.");
			return;
		} else {
			out.println("<BR>Running with configuration from file: " + fileName);
			//out.println("<BR> JSP hosted on: " + RunningServerInfo.getInstance().getRunningServerInfo());
		}

		ApplicationEnvConfigBase configLoader = ApplicationEnvConfigBase.init(fileName);
		configLoader.getClientConfiguration().setThreadCount(1);
		configLoader.getClientConfiguration().setRunTime(0);
		List executionUnits = configLoader.getExecutionUnits();

		if (executionUnits.size() <1) {
			out.println("<BR> JSP Web Execution FAILED.  There are no Execution Units to execute in this file.");
			out.println("<p> JSP Execution Completed.");
			return;
		} else{
			out.println("<BR> JSP Execution Starting ....");
			if (request.getParameter("resetExecutionStatsFlag") != null) {
				ExecutionStats.getInstance().reset();
				out.println("Execution Stats were reset.");
			} 
			AcmeJavaClient clnt = new AcmeJavaClient();
			ClientContext clientContext = new ClientContext("JSP", 0);
			clientContext.setRootLoggerName("com.ibm.wssvt.acme.annuity");
			clientContext.setClientType(ClientType.WEB);
			JSPReportedMessageVisitor jspReportedMessageVisitor = new JSPReportedMessageVisitor(out);
			clnt.executeClient(configLoader, clientContext, null, null, jspReportedMessageVisitor, 
				new ExecutionStatsFormatter(ExecutionStatsFormatter.HTML_NEW_LINE)); 
			out.println("<BR> Execution Completed.... Check the logs for additional information.");
		}		
		out.println("<p> JSP Execution Completed.");
	}catch (ConfigurationException e) {
		String msg = "JSP Web Execution FAILED.  Error in Configuration URL. Error is: " + e;
		System.out.println(msg);
		e.printStackTrace();
		out.println("<BR>" + msg);
		out.println("<p> JSP Execution Completed.");
	}catch (Throwable t) {
		String msg = "JSP Web Execution FAILED.  Unexpected error was reported. Error is: " + t;
		System.out.println(msg);
		t.printStackTrace();
		out.println("<BR>" + msg);
		out.println("<p> JSP Execution Completed.");
	}
%>

</body>
</html>