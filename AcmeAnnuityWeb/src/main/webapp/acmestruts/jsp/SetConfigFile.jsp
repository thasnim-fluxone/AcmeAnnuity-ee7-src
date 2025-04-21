<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
	<head>
  		<title>Initialize Server Configuration</title>
	</head>
	<body>
		<s:form action="ServerConfiguration" validate="false">
			<s:textfield label="Config File URL" name="configFile"/>
			<s:submit value="Set Server Configuration" action="ServerConfiguration_init"/>
		</s:form>
	</body>
</html>