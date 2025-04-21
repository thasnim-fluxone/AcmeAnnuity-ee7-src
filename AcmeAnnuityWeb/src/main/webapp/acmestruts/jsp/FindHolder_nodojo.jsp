<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
  <title>Find Holder by ID</title>
</head>
<body>


<s:form action="ManageHolder_find" validate="false">
<s:actionerror />

<s:textfield label="Holder ID" name="holderId"/>

<s:hidden name="holderID"/>

<s:submit key="Find" action="ManageHolder_find"/>

</s:form>
</body>
</html>