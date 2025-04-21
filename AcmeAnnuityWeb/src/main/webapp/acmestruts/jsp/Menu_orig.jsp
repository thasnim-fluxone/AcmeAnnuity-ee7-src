<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Welcome</title>
    <link href="<s:url value="/css/examplecss"/>" rel="stylesheet"
          type="text/css"/>
</head>

<body>

<h1>Welcome to the ACME Struts Application</h1>
<h3>Server Config has been set. Here are the Commands</h3>
<ul>
    <li><a href="<s:url action="ManageHolder_input"/>">Create Holder</a></li>
    <li><a href="<s:url action="FindHolder"/>">Find Holder</a></li>       
</ul>

<!--  <li><a href="FindHolder.jsp">Find Holder</a></li> 
<li><a href="ContextFile.jsp">Context File</a></li>
<li><a href="<s:url action="ManageHolder_findh"/>">Find Holder</a></li>
<li><a href="<s:url action="ServerConfiguration_input"/>">Initialize Server Configurations.</a></li>
 -->

</body>
</html>