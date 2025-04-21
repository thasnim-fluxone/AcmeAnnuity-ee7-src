<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
  <title>Create or Update a Holder</title>
  <s:head/> 
</head>
<body>

<s:form action="ManageHolder" validate="false">
	<s:hidden name="task"/>
    <s:if test="task == 'CREATE'">
        <s:textfield key="holderId"/>
    </s:if>            
    <s:else>
       <s:label key="holderId"/>
        <s:hidden name="holderId"/>
        <s:hidden name="contactId"/>
    </s:else>   
  	
  	<s:textfield key="holderFirstName"/>
  	<s:textfield key="holderLastName"/>  
  	<s:textfield key="holderGovernmentId"/>
  	 	
  	<s:select key="holderCategory" headerKey="1" list="{'METAL','BRONZE','SILVER','GOLD'}" />
  	  	  	
  	<s:hidden name="holderVersion"/>
  	<s:hidden name="holderDateOfBirth"/>
  	

 	<s:if test="task == 'CREATE'">
    	<s:submit key="Create" action="ManageHolder_save"/>
    	<s:submit key="AutoFill" action="ManageHolder_autoFill"/>
  	</s:if>
  	<s:else>
  		<s:submit key="Update" action="ManageHolder_save"/>
  	</s:else> 
  
	<s:submit action="Menu" key="Cancel" onclick="form.onsubmit=null"/>

</s:form>
</body>
</html>
