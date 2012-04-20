<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<form action="/mzid/mzid/persons" method="post">
		<label for="id">PID</label>
		<input name="id" id="id"/>
		<br/>
		<label for="vorname">Vorname</label>
		<input name="vorname" id="vorname"/>
		<br/>
		<label for="nachname">Nachname</label>
		<input name="nachname" id="nachname"/>
		<br/>
		<input type="submit" value="Abschicken!">
	</form>
</body>
</html>