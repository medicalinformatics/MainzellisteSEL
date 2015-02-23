<%@page import="de.pseudonymisierung.mainzelliste.Config"%>
<%@page import="java.util.ResourceBundle"%>
<% 
	ResourceBundle bundle = Config.instance.getResourceBunde(request);
	String contact = Config.instance.getProperty("contact");
%>
		<div class="kontakt_daten">
			<p><% if (contact != null && !contact.equals("")) { %>
				<%=bundle.getString("contact") %>: <%=contact %>
				<% } %>
			</p>
		</div>