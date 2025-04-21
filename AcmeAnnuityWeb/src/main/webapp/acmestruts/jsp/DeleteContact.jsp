<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
  <title>Delete Contact</title>
</head>
<body>

Are you sure you want to delete Contact with id <s:label name="contactId"/> for Holder with ID <s:label name="holderId"/>

<s:form action="ManageContact" validate="false">
<s:hidden name="task"/>
<s:hidden name="holderId"/>
<s:hidden name="contactId"/>
<s:submit key="Yes - Delete" action="ManageContact_save"/>
<s:submit key="No - Return to Holder" action="ManageHolder_view"/>
</s:form>
</body>
</html>