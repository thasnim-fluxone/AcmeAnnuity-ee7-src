<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
  <title>Contact Details</title>
</head>
<body>

<s:if test="task == 'DELETE'">
        Contact for Holder with ID <s:label name="holderId"/> has been deleted successfully.
</s:if>
    
    
<s:else>
<p>
   Here are the Contact details:
</p>
	<ul>
    	<li><s:label key="holderId"/></li>
    	<li><s:label key="contactId"/></li>
    	<li><s:text name="contact.phone"/> : <s:property value="contact.getPhone()" /></li>
    	<li><s:text name="contact.email"/> : <s:property value="contact.getEmail()" /></li>
    	<li><s:text name="contact.addrs1"/> : <s:property value="contact.getAddress().getLine1()" /></li>    
		<li><s:text name="contact.version"/> : <s:property value="contact.getVersion()" /></li>
	</ul>
</s:else>
 
<s:form validate="false">

<s:hidden name="task"/>
<s:hidden name="holderId"/>
<s:hidden name="contactId"/>

<s:submit key="Return  to Holder" action="ManageHolder_view"/> 

<s:submit action="Menu" key="Menu" onclick="form.onsubmit=null"/>

</s:form>

</body>
</html>


<!--  
<ul>
    <li><s:text name="holderId"/> : <s:property value="holderId" /></li>
    <li><s:text name="contactId"/> : <s:property value="contactId" /></li>
    <li><s:text name="contact.phone"/> : <s:property value="contact.getPhone()" /></li>
    <li><s:text name="contact.email"/> : <s:property value="contact.getEmail()" /></li>
    <li><s:text name="contact.addrs1"/> : <s:property value="contact.getAddress().getLine1()" /></li>
</ul>
<s:hidden name="contactPhone"/>
<s:hidden name="contactEmail"/>

<s:submit key="Update" action="ManageContact_update"/>
-->