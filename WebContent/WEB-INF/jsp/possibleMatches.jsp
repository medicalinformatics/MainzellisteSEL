<%@page import="de.pseudonymisierung.mainzelliste.matcher.MatchResult.MatchResultType"%>
<%@page import="de.pseudonymisierung.mainzelliste.Field"%>
<%@page import="de.pseudonymisierung.mainzelliste.Patient"%>
<%@page import="java.util.LinkedList"%>
<%@page import="de.pseudonymisierung.mainzelliste.IDGeneratorFactory"%>
<%@page import="de.pseudonymisierung.mainzelliste.Config"%>
<%@page import="de.pseudonymisierung.mainzelliste.IDRequest"%>
<%@page import="java.util.List"%>
<%@page import="de.pseudonymisierung.mainzelliste.dto.Persistor"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String matchResultType = request.getParameter("matchResultType");
	MatchResultType showType;
	try { 
		showType = MatchResultType.valueOf(matchResultType);
	} catch (Exception e) {
		showType = MatchResultType.POSSIBLE_MATCH;
	}
	Double minWeight, maxWeight;
	try {
		minWeight = Double.parseDouble(request.getParameter("minWeight"));		
	} catch (Exception e) {
		minWeight = null;	
	}

	try {
		maxWeight = Double.parseDouble(request.getParameter("maxWeight"));		
	} catch (Exception e) {
		maxWeight = null;	
	}
// 	List<IDRequest> possibleMatches = Persistor.instance.getPossibleMatches();
 	List<IDRequest> possibleMatches = Persistor.instance.getIDIdRequests(showType, minWeight, maxWeight);	
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

<title>Mögliche Duplikate</title>
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
			<h1>Mögliche Duplikate</h1>
			<p>
			<form style="text-align: center" method="GET">
			Anzeigen: 
			<input type="radio" name="matchResultType" value="<%=MatchResultType.MATCH %>"
				<% if (showType==MatchResultType.MATCH) {%>checked="checked"<% } %>> Matche
			<input type="radio" name="matchResultType" value="<%=MatchResultType.POSSIBLE_MATCH %>"
				<% if (showType==MatchResultType.POSSIBLE_MATCH) {%>checked="checked"<% } %>> Unsichere Matche
			<input type="radio" name="matchResultType" value="<%=MatchResultType.NON_MATCH %>"
				<% if (showType==MatchResultType.NON_MATCH) {%>checked="checked"<% } %>> Non-Matche,
			Matchgewicht von <input type="text" name="minWeight" value="<%=minWeight==null ? "" : minWeight %>"size="4"> bis 
			<input type="text" name="maxWeight" value="<%=maxWeight==null ? "" : maxWeight %>" size="4">
			<input type="submit" value="OK">
			</form>
			</p>
			<table class="possible-matches">
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
						<td>Matchgewicht</td>
					</tr>
				</thead>
					<%
						int pairId = 1;
						for (IDRequest thisRequest : possibleMatches) {
							if (thisRequest.getMatchResult().getBestMatchedPatient() == null)
								continue;
							String newPID = thisRequest.getAssignedPatient().getId(idType)
									.getIdString();
							String possiblePID = thisRequest.getMatchResult()
									.getBestMatchedPatient().getId(idType).getIdString();
							double weight = thisRequest.getMatchResult()
									.getBestMatchedWeight();
					%>
				<tbody style="border: 1px solid;">
					<tr <% if(pairId % 2 == 1) { %>style="background-color:white"<% } %>>
						<td><a href="<%=request.getContextPath() %>/html/admin/editPatient?idType=<%=idType %>&idString=<%=newPID%>">
							<%=newPID %></a>
						</td>
						<%
					 	for (String fieldName : fieldNames) {
					 		Field thisField = thisRequest.getInputFields()
									.get(fieldName);
					 		boolean isDifferent = !(thisField == null || thisField.equals(thisRequest.getMatchResult().getBestMatchedPatient().getInputFields().get(fieldName)));
					 		String markDifferent = isDifferent ? " class=\"mark-duplicate\"" : ""; 
						 %>				
						<td<%=markDifferent %>><%=(thisField == null ? "" : thisField.getValue().toString()) %></td>
						<%
						}
						%>
						<td rowspan="2">
							<%=String.format("%.8f", weight) %>
						</td>
					</tr>
					<tr <% if(pairId % 2 == 1) { %>style="background-color:white"<% } %>>
						<td><a href="<%=request.getContextPath() %>/html/admin/editPatient?idType=<%=idType %>&idString=<%=possiblePID%>">
							<%=possiblePID%></a>
						</td>
						<%
						for (String fieldName : fieldNames) {
							Field thisField = thisRequest.getMatchResult().getBestMatchedPatient().getInputFields()
									.get(fieldName); 
					 		boolean isDifferent = !(thisField == null || thisField.equals(thisRequest.getInputFields().get(fieldName)));
					 		String markDifferent = isDifferent ? " class=\"mark-duplicate\"" : ""; 
						 %>				
						<td<%=markDifferent %>><%=(thisField == null ? "" : thisField.getValue().toString()) %></td>
						<%
							} %>
								</tr>
							<%
								pairId++;
								if (pairId <= possibleMatches.size()) {
								%>
						</tbody>
						<tbody style="border: 0px none">								
					<tr>
						<td class="separator" colspan="<%=fieldNames.size() + 2%>">&nbsp;
						</td>
					</tr>						
								<%
								} 
								%>
				</tbody>
						<%								
							}
						%>
					
			</table>

			<div>&nbsp;</div>
		</div>
	</div>
	<%@ include file="footer.jsp"%>
</body>
</html>