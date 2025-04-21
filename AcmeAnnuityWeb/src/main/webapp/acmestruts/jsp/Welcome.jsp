<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Welcome</title>
 <!--     <link href="<s:url value="/css/examplecss"/>" rel="stylesheet"
          type="text/css"/> -->
<link rel="stylesheet" type="text/css"
	href="../../dojo/dijit/themes/dijit.css">
</head>

<body>

<h1>Welcome to the ACME Struts Application</h1>
<h3>Please specify the location of the Configuration File</h3>

<li><a href="<s:url action="ServerConfiguration_input"/>">Initialize Server Configurations.</a></li>

</body>
</html>