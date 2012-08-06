<!-- Form elements for patient data, to be included in other pages -->

					<%@page import="java.util.Calendar"%>
					<%@ page import="java.util.Map" %>					
					<%
						Map<String, Object> map = (Map<String,Object>)request.getAttribute("it");
					%>
					<h3>Stammdaten</h3>
					<fieldset class="patienten_daten">
						<div>&nbsp;</div>
						<div>&nbsp;</div>
						<table class="daten_tabelle">
							<tbody>
								<tr>
									<td><label for="vorname">Vorname : </label></td>
									<td><input type="text" id="vorname" name="vorname" size="50" placeholder="Anne-Marie Luise"
										value="${it.vorname}" <% if (map.containsKey("disabled")) { %> disabled="disabled" <% } %>/></td>
								</tr>
								<tr>
									<td><label for="nachname">Nachname : </label></td>
									<td><input type="text" id="nachname" name="nachname" size="50" placeholder="Müller-Schulze"
									value="${it.nachname}" <% if (map.containsKey("disabled")) { %> disabled="disabled" <% } %>/></td>
								</tr>
								<tr>
									<td><label for="geburtsname">Geburtsname : </label></td>
									<td><input type="text" id="geburtsname" name="geburtsname" size="50" 
									value="${it.geburtsname}" 
									<% if (map.containsKey("disabled")) { %> disabled="disabled" <% } else { %> placeholder="Schulze" <% } %>/><small> (falls abweichend)</small></td>
								</tr>
								<tr>
									<td><small>&nbsp;</small></td>
								</tr>
								<tr>
									<td><label for="geburtsdatum">Geburtsdatum :</label></td>
									<td class="geburtsdatum" name="geburtsdatum">
										<div>
											<select class="geburtstag" name="geburtstag" id ="geburtstag"
												<% if (map.containsKey("disabled")) { %> disabled="disabled" <% } %>>
												<option value="-1">Tag:</option>
												<%												
												for (int i=1; i <= 31; i++)
												{
												%>									
												<option value="<%= i%>"
													<% if (map != null && 
														map.get("geburtstag") != null &&
														i == Integer.parseInt(map.get("geburtstag").toString())) { %>
														selected="selected"
													<% } %>>
													<%= String.format("%02d", i)%>
													</option>
												<%
												}
												%>
											</select>
											<select class="geburtsmonat" name="geburtsmonat" id="geburtsmonat"
												<% if (map.containsKey("disabled")) { %> disabled="disabled" <% } %>>
												<option value="-1">Monat:</option>
												<%
												String months[] = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"}; 
												for (int i=1; i <= 12; i++)
												{
												%>									
												<option value="<%= i%>"
													<% if (map != null && 
															map.get("geburtsmonat") != null &&
															i==Integer.parseInt(map.get("geburtsmonat").toString())) { %>
														selected="selected"
													<% } %>>
													<%= months[i-1]%>
													</option>
												<%
												}
												%>
											</select>
											<select class="geburtsjahr" name="geburtsjahr" id="geburtsjahr"
												<% if (map.containsKey("disabled")) { %> disabled="disabled" <% } %>>
												<option value="-1">Jahr:</option>
												<%			
												int currentYear = Calendar.getInstance().get(Calendar.YEAR);

												for (int i=currentYear; i >= currentYear - 130; i--)
												{
												%>									
												<option value="<%= i%>"
													<% if (map != null && 
															map.get("geburtsjahr") != null &&
															i==Integer.parseInt(map.get("geburtsjahr").toString())) { %>
														selected="selected"
													<% } %>>
													<%= String.format("%04d", i)%>
													</option>
												<%
												}
												%>
											</select>
										</div>
									</td>
								</tr>
								<tr>
									<td><label for="plz">Wohnort : </label></td>
									<td>
										<input type="text" id="plz" name="plz" size="5" maxlength="5" 
											value="${it.plz}" <% if (map.containsKey("disabled")) { %> disabled="disabled" <% } else { %>
											placeholder="PLZ" <% } %>/>
										<input type="text" id="ort" name="ort" size="40" 
											value="${it.ort}" <% if (map.containsKey("disabled")) { %> disabled="disabled" <% } else {%>
											placeholder="Ort" <% } %>/>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
								</tr>
							</tbody>
						</table>
				</fieldset>
