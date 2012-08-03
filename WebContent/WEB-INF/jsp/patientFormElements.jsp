<!-- Form elements for patient data, to be included in other pages -->

					<%@page import="java.util.Calendar"%>
					<%@ page import="java.util.Map" %>					
					<%
						Map<String, Object> map = (Map<String,Object>)request.getAttribute("it");
						Map<String, Object> fields = (Map<String,Object>) map.get("fields");
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
										value="${it.fields.vorname}"/></td>
								</tr>
								<tr>
									<td><label for="nachname">Nachname : </label></td>
									<td><input type="text" id="nachname" name="nachname" size="50" placeholder="Müller-Schulze"
									value="${it.fields.nachname}"/></td>
								</tr>
								<tr>
									<td><label for="geburtsname">Geburtsname : </label></td>
									<td><input type="text" id="geburtsname" name="geburtsname" size="50" placeholder="Schulze"
									value="${it.fields.geburtsname}"/><small> (falls abweichend)</small></td>
								</tr>
								<tr>
									<td><small>&nbsp;</small></td>
								</tr>
								<tr>
									<td><label for="geburtsdatum">Geburtsdatum :</label></td>
									<td class="geburtsdatum" name="geburtsdatum">
										<div>
											<select class="geburtstag" name="geburtstag" id ="geburtstag">
												<option value="-1">Tag:</option>
												<%												
												for (int i=1; i <= 31; i++)
												{
												%>									
												<option value="<%= i%>"
													<% if (fields != null && i==Integer.parseInt(fields.get("geburtstag").toString())) { %>
														selected="selected"
													<% } %>>
													<%= String.format("%02d", i)%>
													</option>
												<%
												}
												%>
											</select>
											<select class="geburtsmonat" name="geburtsmonat" id="geburtsmonat"
												value="${it.fields.geburtsmonat}">
												<option value="-1">Monat:</option>
												<%
												String months[] = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"}; 
												for (int i=1; i <= 12; i++)
												{
												%>									
												<option value="<%= i%>"
													<% if (fields != null && i==Integer.parseInt(fields.get("geburtsmonat").toString())) { %>
														selected="selected"
													<% } %>>
													<%= months[i-1]%>
													</option>
												<%
												}
												%>
											</select>
											<select class="geburtsjahr" name="geburtsjahr" id="geburtsjahr">
												<option value="-1">Jahr:</option>
												<%			
												int currentYear = Calendar.getInstance().get(Calendar.YEAR);

												for (int i=currentYear; i >= currentYear - 130; i--)
												{
												%>									
												<option value="<%= i%>"
													<% if (fields != null && i==Integer.parseInt(fields.get("geburtsjahr").toString())) { %>
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
										<input type="text" id="plz" name="plz" size="5" maxlength="5" placeholder="PLZ"
											value="${it.fields.plz}"/>
										<input type="text" id="ort" name="ort" size="40" placeholder="Ort"
											value="${it.fields.ort}"/>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>
										<input class="submit_anlegen" type="submit" name="anlegen" value=" Anlegen "/>
									</td>
								</tr>
							</tbody>
						</table>
				</fieldset>
