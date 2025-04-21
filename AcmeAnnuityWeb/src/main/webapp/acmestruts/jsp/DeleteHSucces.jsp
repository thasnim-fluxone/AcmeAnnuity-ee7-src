<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
  <title>Delete Holder Success</title>
</head>
<body>

Holder with ID <s:label name="holderId"/> (and its contact) has been deleted successfully.

<s:form action="ManageHolder" validate="false">
<s:submit action="Menu" key="Main Menu" onclick="form.onsubmit=null"/>
</s:form>
</body>
</html>