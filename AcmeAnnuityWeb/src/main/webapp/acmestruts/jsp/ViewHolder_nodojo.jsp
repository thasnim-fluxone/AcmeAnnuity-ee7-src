<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
  <title>Holder details</title>
</head>
<body>
<p>
    Here are the Holder details:
</p>


<ul>	
	<li><s:label key="holderId"/></li>
    <li><s:text name="holderFirstName"/> : <s:property value="holder.getFirstName()" /></li>
    <li><s:text name="holderLastName"/> : <s:property value="holder.getLastName()" /></li>
    <li><s:text name="Date of Birth"/> : <s:property value="holder.getDateOfBirth()" /></li>
    <li><s:text name="Government ID"/> : <s:property value="holder.getGovernmentId()" /></li>
    <li><s:text name="Category"/> : <s:property value="holder.getCategory()" /></li>
    <li><s:text name="Version"/> : <s:property value="holder.getVersion()" /></li>    
</ul>

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

</body>
</html>