<%@page import="de.pseudonymisierung.mainzelliste.Config"%>
<%@page import="java.util.ResourceBundle"%>
<% 
	ResourceBundle bundle = Config.instance.getResourceBunde(request);
	String operator = Config.instance.getProperty("operator.contact");
%>
		<div class="kontakt_daten">
			<p><% if (operator != null && !operator.equals("")) { %>
				<%=bundle.getString("operator") %>: <%=operator %>
				<% } %>
			</p>
			<p>
				<%=bundle.getString("mainzellisteInfo") %>&nbsp;<a href="http://www.mainzelliste.de">http://www.mainzelliste.de</a>.
			</p>
		</div>