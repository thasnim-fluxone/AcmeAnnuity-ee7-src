<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
  <title>Delete Holder</title>
</head>
<body>

Are you sure you want to delete Holder with ID <s:label name="holderId"/> as well as its contact?

<s:form action="ManageHolder" validate="false">
<s:hidden name="task"/>
<s:hidden name="holderId"/>
<s:hidden name="contactId"/>
<s:submit key="Yes - Delete" action="ManageHolder_deleteConfirm"/>
<s:submit key="No - Return to Holder" action="ManageHolder_view"/>
</s:form>
</body>
</html>