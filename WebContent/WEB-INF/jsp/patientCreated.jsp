<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.Map" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>

<body>
	<p>
		ID: ${it.id}
	</p>
	<%
		Map<String, Object> map = (Map<String,Object>)request.getAttribute("it");
		Boolean tentative = (Boolean) map.get("tentative");
		if (tentative)
		{
	%>
		<p>
			Zu den eingegebenen Daten wurde ein ähnlicher Patient gefunden, der nicht
			mit hinreichender Sicherheit zugeordnet werden kann. Der angezeigte PID
			kann zwar verwendet werden, ist aber als vorläufig zu betrachten. Zukünftige
			Abfragen mit den gleichen Daten werden möglicherweise einen anderen PID liefern.
		<p>
	<%
		}
	%>
</body>
</html>