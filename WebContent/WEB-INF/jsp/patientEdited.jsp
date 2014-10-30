<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/static/css/patientenliste.css">

<title>Bearbeitung abgeschlosen</title>
</head>

<body>
	<div class="kopfzeile">
		<div class="logo">&nbsp;</div>
	</div>
	<div class="inhalt">
		<div class="formular">
			<div>&nbsp;</div>
			<h1>Bearbeitung abgeschlossen</h1>

			<p align="center">
				Die Änderungen an den Patientendaten wurden übernommen.
				Sie können nun zur aufrufenden Webseite zurückkehren.
			</p>
		</div>
		<div>&nbsp;</div>
	</div>
	<%@ include file="footer.jsp"%>
</body>
</html>