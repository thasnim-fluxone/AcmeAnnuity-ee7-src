<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.rmi.PortableRemoteObject"%>
<%@page import="com.ibm.wssvt.acme.annuity.common.business.ejb30.AnnuityMgmtSvcEJB30"%>
<%@page import="com.ibm.wssvt.acme.annuity.common.business.ejb30.AnnuityMgmtSvcEJB30Local"%>
<%@page import="com.ibm.wssvt.acme.annuity.common.business.ejbws.ejb3jaxws.AnnuityMgmtSvcEJB30JAXWS"%>
<%@page import="com.ibm.wssvt.acme.annuity.common.business.ejbws.ejb3jaxws.AnnuityMgmtSvcEJB30JAXWSLocal"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Annuity Remote EJB Test: CRUD Annuity Test</title>
</head>
<body>
Test run time:  <%= new java.util.Date() %>
<br> 
<%
	try{
		InitialContext ic = new InitialContext();
		out.print("<BR>Attempt to get Pure EJB3 remote object from JNDI lookup");
		Object ref = ic.lookup("ejb/ACMEAnnuity/AcmeAnnuityEJB3.jar/AnnuityMgmtSvcEJB30Bean#com.ibm.wssvt.acme.annuity.common.business.ejb30.AnnuityMgmtSvcEJB30");
		AnnuityMgmtSvcEJB30 remote = (AnnuityMgmtSvcEJB30) PortableRemoteObject.narrow(ref, AnnuityMgmtSvcEJB30.class);
		out.print("<BR>Pure EJB3 remote object from lookup: " + remote);
		out.println("<BR>Found Successfully<p>");
	/*
		out.print("<BR>Attempt to get Pure EJB3 local object from JNDI lookup");		
		AnnuityMgmtSvcEJB30Local local = 
			(AnnuityMgmtSvcEJB30Local) ic.lookup("ejblocal:ACMEAnnuity/AcmeAnnuityEJB3.jar/AnnuityMgmtSvcEJB30Bean#com.ibm.wssvt.acme.annuity.common.business.ejb30.AnnuityMgmtSvcEJB30Local");
		out.print("<BR>Pure EJB3 local object from lookup: " + local);		
		out.println("<BR>Found Successfully<p>");

		out.print("<BR>Attempt to get EJB3JAXWS remote object from JNDI lookup");		
		Object ref3 = ic.lookup("ejb/ACMEAnnuityEJBWSes/AcmeAnnuityEJB3JAXWS.jar/AnnuityMgmtSvcEJB30JAXWSBean#com.ibm.wssvt.acme.annuity.common.business.ejbws.ejb3jaxws.AnnuityMgmtSvcEJB30JAXWS");
		AnnuityMgmtSvcEJB30JAXWS annuityMgmtSvcEJB30JAXWS = (AnnuityMgmtSvcEJB30JAXWS) PortableRemoteObject.narrow(ref3, AnnuityMgmtSvcEJB30JAXWS.class);
		out.print("<BR>EJB3JAXWS remote object from lookup: " + annuityMgmtSvcEJB30JAXWS);		
		out.println("<BR>Found Successfully<p>");
		*/
	/*
		out.print("<BR>Attempt to get EJB3JAXWS local object from JNDI lookup");		
		AnnuityMgmtSvcEJB30JAXWSLocal annuityMgmtSvcEJB30JAXWSLocal = 
			(AnnuityMgmtSvcEJB30JAXWSLocal) ic.lookup("ejblocal:ACMEAnnuityEJBWSes/AcmeAnnuityEJB3JAXWS.jar/AnnuityMgmtSvcEJB30JAXWSBean#com.ibm.wssvt.acme.annuity.common.business.ejbws.ejb3jaxws.AnnuityMgmtSvcEJB30JAXWSLocal");
		out.print("<BR>EJB3JAXWS local object from lookup: " + annuityMgmtSvcEJB30JAXWSLocal);						
		out.println("<BR>Found Successfully<p>");
	
		out.print("<BR>Attempt to get EJB3JAXRPC remote object from JNDI lookup");		
		Object ref2 = ic.lookup("ejb/ACMEAnnuityEJBWSes/AcmeAnnuityEJB3JAXRPC.jar/AnnuityMgmtSvcEJB30JAXRPCBean#com.ibm.wssvt.acme.annuity.common.business.ejbws.ejb3jaxrpc.AnnuityMgmtSvcEJB30JAXRPC");
		AnnuityMgmtSvcEJB30JAXRPC annuityMgmtSvcEJB30JAXRPC = (AnnuityMgmtSvcEJB30JAXRPC) PortableRemoteObject.narrow(ref2, AnnuityMgmtSvcEJB30JAXRPC.class);
		out.print("<BR>EJB3JAXRPC remote object from lookup: " + annuityMgmtSvcEJB30JAXRPC);		
		out.println("<BR>Found Successfully<p>");
		*/
	/*
		out.print("<BR>Attempt to get EJB3JAXRPC local object from JNDI lookup");		
		AnnuityMgmtSvcEJB30JAXRPCLocal annuityMgmtSvcEJB30JAXRPCLocal = 
			(AnnuityMgmtSvcEJB30JAXRPCLocal) ic.lookup("ejblocal:ACMEAnnuity/AcmeAnnuityEJB3JAXRPC.jar/AnnuityMgmtSvcEJB30JAXRPCBean#com.ibm.wssvt.acme.annuity.common.business.ejbws.ejb3jaxrpc.AnnuityMgmtSvcEJB30JAXRPCLocal");
		out.print("<BR>EJB3JAXRPC local object from lookup: " + annuityMgmtSvcEJB30JAXRPCLocal);						
		out.println("<BR>Found Successfully<p>");
	*/
		
			
	}catch(Throwable t){
		out.println("<BR>** ERROR **<p>"); 
		out.println(t.getMessage());
	}		
%>
Test End time:  <%= new java.util.Date() %>
</body>
</html>