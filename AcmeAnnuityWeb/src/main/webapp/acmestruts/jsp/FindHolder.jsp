<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="../../dojo/dijit/themes/dijit.css">
<link rel="stylesheet" type="text/css"
	href="../../dojo/dijit/themes/claro/claro.css">
<title>Find Holder by ID</title>
<script type="text/javascript"
	data-dojo-config="isDebug: false, async: true, parseOnLoad: true"
	src="../../dojo/dojo/dojo.js"></script>
<script type="text/javascript">
require(
// Set of module identifiers
[ "dojo", "dojo/parser", "dijit/form/Button", "dijit/form/Form", "dijit/layout/BorderContainer", "dijit/layout/ContentPane" ],
// Callback function, invoked on dependencies evaluation results
function(dojo) {
	dojo.ready(function() {

	});
});
</script>
</head>
<body class="claro">


<s:form action="ManageHolder_find" validate="false">
<s:actionerror />

<s:textfield label="Holder ID" name="holderId"/>

<s:hidden name="holderID"/>

<s:submit key="Find" action="ManageHolder_find"/>

</s:form>
	
	<div data-dojo-type="dijit.form.Form"></div>
	<div id="BorderContainer" style="height: 500px; width: 500px"
		data-dojo-type="dijit.layout.BorderContainer"
		data-dojo-props="design:'headline'">
		<div data-dojo-type="dijit.layout.ContentPane"
			data-dojo-props="region:'top'">
			<div data-dojo-type="dijit.form.Button"
				data-dojo-props="label:'Find Holder'"></div>
		</div>
		<div data-dojo-type="dijit.layout.ContentPane"
			data-dojo-props="region:'left'"></div>
		<div data-dojo-type="dijit.layout.ContentPane"
			data-dojo-props="region:'right'"></div>
		<div data-dojo-type="dijit.layout.ContentPane"
			data-dojo-props="region:'bottom'"></div>
	</div>

	</body>
</html>