<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
  <title>Create a Contact</title>
</head>
<body>

Create a Contact for Holder: <s:label name="holderId"/>

<s:form action="ManageContact" validate="false">
<s:hidden name="task"/>
<s:hidden name="holderId"/>
    <s:if test="task == 'CREATE'">
        <s:textfield key="contactId"/>
    </s:if>
    <s:else>
      <p>   <s:label key="contactId"/></p>
        	<s:hidden name="contactId"/>
        	<s:hidden name="contactVersion"/>
    </s:else>
 
  <s:textfield key="contactPhone"/>
  <s:textfield key="contactEmail"/>
  
  <s:if test="task == 'CREATE'">
        <s:submit key="Create" action="ManageContact_save"/>
        <s:submit key="AutoFill" action="ManageContact_autoFill"/>
  </s:if>
  <s:else>
      <s:submit key="Update" action="ManageContact_save"/>        
  </s:else>
  <s:submit action="Menu" key="Cancel"
                    onclick="form.onsubmit=null"/>
</s:form>
</body>
</html>
