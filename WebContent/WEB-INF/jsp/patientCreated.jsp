<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.Map" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/static/css/patientenliste.css">

<title>Ergebnis</title>
</head>

<body>
<body>
		<div class="kopfzeile">
			<div class="logo">&nbsp;</div>
		</div>
		<div class="inhalt">
			<div class="formular">
				<div>&nbsp;</div>
				<h1>Ergebnis</h1>

	<p>
		Ihr angeforderter PID lautet <tt><big>${it.id}</big></tt>. Bitte übernehmen Sie ihn in Ihre Unterlagen.
	</p>
	<%
		Map<String, Object> map = (Map<String,Object>)request.getAttribute("it");
		boolean tentative = ((Boolean) map.get("tentative"));
		if (tentative)
		{
	%>
		<p>
			Zu den eingegebenen Daten wurde ein ähnlicher Patient gefunden, der nicht
			mit hinreichender Sicherheit zugeordnet werden kann. Der angezeigte PID
			ist als vorläufig zu betrachten. Das bedeutet, dass der PID zwar verwendet 
			werden kann, aber zukünftige Abfragen mit den gleichen Daten können einen 
			anderen	PID liefern.
		<p>
		<div>
			<form>
				<input type="button" value="Fenster schließen" onClick="window.close()">
			</form>
		</div>
	<%
		}
	%>
		<div>&nbsp;</div>
	</div>
	<%@ include file="footer.jsp" %>
</body>
</html>