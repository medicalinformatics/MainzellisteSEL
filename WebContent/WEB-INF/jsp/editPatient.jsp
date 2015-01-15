<%@page import="org.codehaus.jettison.json.JSONObject"%>
<%@page import="de.pseudonymisierung.mainzelliste.ID"%>
<%@page import="de.pseudonymisierung.mainzelliste.Patient"%>
<%@page import="de.pseudonymisierung.mainzelliste.IDGeneratorFactory"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath() %>/static/css/patientenliste.css">

<title>Patienten bearbeiten</title>
</head>



<body>
	<div class="kopfzeile">
		<div class="logo">&nbsp;</div>
	</div>
	<div class="inhalt">
		<div>&nbsp;</div>
		<div class="formular">
<!-- 			<form method="post" id="form_person"> -->
			<form method="post" action="<%=request.getContextPath() %>/patients/tokenId/${it.tokenId}?_method=PUT" id="form_person">
				<h1>Patienten bearbeiten</h1>
				<%@ include file="patientFormElements.jsp" %>
				<div align="center">
					<td>&nbsp;</td>
				</div>
				<div align="center">
					<input type="submit" value="Speichern" />
				</div>
			</form>
		</div>
		<div>&nbsp;</div>
	</div>
	<%@include file="footer.jsp" %>
</body>
</html>
