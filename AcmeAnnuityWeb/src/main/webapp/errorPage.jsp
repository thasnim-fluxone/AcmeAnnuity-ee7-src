<%@ page isErrorPage="true" language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.logging.*" %>
<%@ page session="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Error Page</title>
</head>
<body>
<p>
<B> ACME Annuity Web Execution - Error 
<BR>
The following error was detected:</B>
<BR><BR>

<B>Exception Class: </B>
<BR> <%= exception.getClass() %>

<BR><BR>
<B>Exception Message - via exception.getMessage()</B>
<BR> <%= exception.getMessage() %>

<BR><BR>
<B>Exception Cause - via exception.getCause()</B>
<BR> <%= exception.getCause() %>


<BR><BR>
<B>Notes:</B>
<BR>
If this is a login error:
<Br> * Please check the user Id and Password.
<BR> * Make sure that the user Id exists on your LDAP server AND should be defined on the application security role mapping.</p>
</body>
</html>