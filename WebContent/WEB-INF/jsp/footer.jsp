<%@page import="de.pseudonymisierung.mainzelliste.Config"%>
<%
	String contact = Config.instance.getProperty("contact");
%>
		<div class="kontakt_daten">
			<p><% if (contact != null && !contact.equals("")) { %>
				Kontakt: <%=contact %>
				<% } %>
			</p>
		</div>