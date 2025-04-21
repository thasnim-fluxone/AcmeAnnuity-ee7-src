<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<link rel="stylesheet" type="text/css"
	href="../../dojo/dijit/themes/dijit.css">
<link rel="stylesheet" type="text/css"
	href="../../dojo/dijit/themes/claro/claro.css">
<title>Welcome</title>
<script type="text/javascript"
	djconfig="isDebug: false, parseOnLoad: true"
	src="../../dojo/dojo/dojo.js"></script>
<script type="text/javascript">
dojo.require("dojo.parser");
dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.layout.BorderContainer");
</script>
</head>

<body class="claro">

<!--  <li><a href="FindHolder.jsp">Find Holder</a></li> 
<li><a href="ContextFile.jsp">Context File</a></li>
<li><a href="<s:url action="ManageHolder_findh"/>">Find Holder</a></li>
<li><a href="<s:url action="ServerConfiguration_input"/>">Initialize Server Configurations.</a></li>
 -->

	<div dojotype="dijit.layout.BorderContainer" id="BorderContainer"
		design="headline"
		style="height:500px; width: 500px;">
		<div dojotype="dijit.layout.ContentPane" region="top"><h1>Welcome to the ACME Struts Application</h1>
		<br>
		<h3>Server Config has been set. Here are the Commands</h3></div>
		<div dojotype="dijit.layout.ContentPane" region="center">
			<ul>
    			<li><a href="<s:url action="ManageHolder_input"/>">Create Holder</a></li>
    			<li><a href="<s:url action="FindHolder"/>">Find Holder</a></li>        
			</ul>
		</div>
	</div>
</body>
</html>