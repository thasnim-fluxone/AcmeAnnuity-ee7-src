<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
  <link rel="stylesheet" type="text/css"
	href="../../dojo/dijit/themes/dijit.css">
<link rel="stylesheet" type="text/css"
	href="../../dojo/dijit/themes/claro/claro.css"> 
<title>Holder details</title>
<script type="text/javascript"
	djconfig="isDebug: false, parseOnLoad: true"
	src="../../dojo/dojo/dojo.js"></script>
<script type="text/javascript">
dojo.require("dojo.parser");
dojo.require("dijit.layout.BorderContainer");
dojo.require("dijit.layout.ContentPane");
</script>
</head>
<body class="claro">





	<div dojotype="dijit.layout.BorderContainer" id="BorderContainer"
		design="headline"  style="height: 500px; width: 500px">
		<div dojotype="dijit.layout.ContentPane" region="top">
		<h3>
    		Here are the Holder details:
		</h3>
		</div>
		<div dojotype="dijit.layout.ContentPane" region="center">
		<ul>	
		<li><s:label key="holderId"/></li>
    	<li><s:text name="holderFirstName"/> : <s:property value="holder.getFirstName()" /></li>
    	<li><s:text name="holderLastName"/> : <s:property value="holder.getLastName()" /></li>
    	<li><s:text name="Date of Birth"/> : <s:property value="holder.getDateOfBirth()" /></li>
    	<li><s:text name="Government ID"/> : <s:property value="holder.getGovernmentId()" /></li>
    	<li><s:text name="Category"/> : <s:property value="holder.getCategory()" /></li>
    	<li><s:text name="Version"/> : <s:property value="holder.getVersion()" /></li>    
		</ul>
		</div>
		<div dojotype="dijit.layout.ContentPane" region="bottom">
		<s:form validate="false">

<s:hidden name="holderId"/>
<s:hidden name="contactId"/>

<s:submit key="Update Holder" action="ManageHolder_update"/>
<s:submit key="Delete Holder" action="ManageHolder_delete"/>
 
<s:if test="contactId == ''">        
	<s:submit key="Create Contact" action="ManageContact_input"/>
</s:if>
  
<s:else>
	<s:submit key="View Contact" action="ManageContact_view"/>
	<s:submit key="Update Contact" action="ManageContact_update"/>
	<s:submit key="Delete Contact" action="ManageContact_delete"/>
</s:else>

<s:submit action="Menu" key="Menu" onclick="form.onsubmit=null"/>        
</s:form>
		
		</div>
	</div>
	
</body>
</html>