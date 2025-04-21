<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>login.jsp</title>
</head>
<body>
<form method="POST" action="j_security_check">
<P></P>
<table border="0">
	<tbody>
		<TR>
			<TD width="164"><FONT size="4" face="Verdana"><B></B></FONT></TD>
			<TD width="112"><FONT size="4" face="Verdana"><B><FONT size="5">Log In</FONT></B></FONT></TD>
		</TR>
	</tbody>
</table>
<P><B> <BR>
ACME Annuity Web Execution - <BR>
Please enter admin role user name and password:</B><BR>
<BR>
</P>

<table border="0">
	<tbody>
		<TR>
			<TD width="134"><B>User name:</B></TD>
			<TD width="174"><INPUT type="text" name="j_username" size="28"></TD>
		</TR>
		<TR>
			<TD width="134"><B>Password:</B> </TD>
			<TD width="174"><INPUT type="password" name="j_password" size="28"></TD>
		</TR>
	</tbody>
</table>
<table border="0" width="370">
	<tbody>
		<TR>
			<TD height="70" width="279"></TD>
			<TD height="70" width="119"><INPUT type="submit" name="loginButton"
				value="Log on"></TD>
			<TD height="70" width="106"><INPUT type="reset" value="Cancel" name="cancelButton"></TD>
		</TR>
	</tbody>
</table>
</form>

</body>
</html>