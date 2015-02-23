<%@page import="de.pseudonymisierung.mainzelliste.Field"%>
<%@page import="de.pseudonymisierung.mainzelliste.ID"%>
<%@page import="de.pseudonymisierung.mainzelliste.Config"%>
<%@page import="java.util.LinkedList"%>
<%@page import="de.pseudonymisierung.mainzelliste.IDGeneratorFactory"%>
<%@page import="de.pseudonymisierung.mainzelliste.Patient"%>
<%@page import="java.util.List"%>
<%@page import="de.pseudonymisierung.mainzelliste.dto.Persistor"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	List<Patient> patients = Persistor.instance.getPatients(request.getParameter("searchString"));
	String idType = IDGeneratorFactory.instance.getDefaultIDType();
	List<String> fieldNames = new LinkedList<String>(
			Config.instance.getFieldKeys());
%>
<%!

	public String makeTableCell(String content) {
		return "<td>" + content + "</td>";
	}%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/static/css/patientenliste.css">

<title>Patientenliste</title>
<style type="text/css">
td {
	border: 1px solid black;
	padding-bottom: 2pt;
	padding-top: 2pt;
	padding-left: 4pt;
	padding-right: 4pt;
}
</style>
</head>

<body>
	<div class="kopfzeile">
		<div class="logo">&nbsp;</div>
	</div>
	<div class="inhalt">
		<div class="formular">
			<div>&nbsp;</div>
			<h1>Patientenliste</h1>
			<p>
				<form style="text-align: center" method="GET">
					Suchbegriff: <input type="text" name="searchString">
					<input type="submit" value="Suchen">
				</form>
			</p>
			<table class="possible-matches" style="max-width: 90%">
				<thead style="border-bottom: 2px solid;">
					<tr>
						<td><%=idType%></td>
						<%
							for (String fieldName : fieldNames) {
						%>
						<td><%=fieldName%></td>
						<%
							}
						%>
					</tr>
				</thead>
				<tbody style="border: 1px solid;">
					<%
						int pairId = 1;
						for (Patient p : patients) {
							ID thisId = p.getId(idType);
					%>
					<tr style="background-color: white;">
						<td><a href="<%=request.getContextPath() %>/html/admin/editPatient?idType=<%=thisId.getType() %>&idString=<%=thisId.getIdString()%>">
							<%=thisId.getIdString() %></a>
						</td>
						<%
					 	for (String fieldName : fieldNames) {
					 		Field thisField = p.getInputFields().get(fieldName);
						 %>				
						<td>
							<%=(thisField == null ? "" : thisField.getValue().toString()) %>
						</td>
						<%
						}
						%>
					</tr>
				</tbody>
						<%								
							}
						%>
					
			</table>

			<div>&nbsp;</div>
		</div>
	</div>
	<jsp:include page="footer.jsp" />
</body>
</html>