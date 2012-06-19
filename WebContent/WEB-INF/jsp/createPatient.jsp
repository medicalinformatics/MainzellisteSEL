<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>

<!-- crypto-js einbinden TODO: In Projekt integrieren(?) -->
<script type="text/javascript" src="http://crypto-js.googlecode.com/files/2.5.3-crypto-sha1.js"></script>
<script type="text/javascript" src="http://crypto-js.googlecode.com/files/2.5.3-crypto-md5.js"></script>
<!-- JQuery -->
<script type="text/javascript" src="http://code.jquery.com/jquery-1.7.2.js"></script>
<script type="text/javascript" src="/mzid/static/jslib/bloomFilter-js/bloomFilter.js"></script>
<script type="text/javascript">

/* TODO: Normalisierung von Umlauten, Namenskomponenten, ...
 * Vorerst nur in Großbuchstaben konvertieren.
 */
function normalize(x)
{
  return x.toUpperCase();
}


function createBloomFilter(form, fields)
{
	/* Config-Variablen für Bloom-Filter */
	var n = 2;
	var l = 500;
	var k = 15;
	
	for	(var i=0; i<fields.length; i++)
	{
		fieldName = $(fields[i]).attr('id');
		value = $(fields[i]).val();
		value = normalize(value);
		bloom = nGramBloomFilter(value, n, k, l);
		orgFld = jQuery('<input type="hidden", id="org_' + fieldName + '" value="' + val + '">');
		$(form).append(orgFld);
		$(fields[i]).val(JSON.stringify(bloom));
		
	}
}

function validateForm()
{
	requiredFields = ['#vorname', '#nachname', '#geburtstag', '#geburtsmonat', '#geburtsjahr'];
	for (i = 0; i < requiredFields.length; i++) {
		if ($(requiredFields[i]).val().length == 0) {
			$(requiredFields[i]).focus();
			alert('Bitte füllen Sie alle Pflichtfelder aus!');
			return false;
		}
	}
	
	// Geburtsjahr prüfen
	if ($('#geburtsjahr').val().length != 4)
	{
		alert('Bitte geben Sie ein vierstelliges Geburtsjahr ein!');
		return false;
	}
	var geburtstag = parseInt($('#geburtstag').val(), 10);
	var geburtsmonat = parseInt($('#geburtsmonat').val(), 10);
	var geburtsjahr = parseInt($('#geburtsjahr').val(), 10);

	switch (geburtsmonat) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			if (geburtstag > 31) 
			{
				alert("Das eingegebene Datum ist ungültig!");
				return false;
			} else {
				break;
			}
		case 4:
		case 6:
		case 9:
		case 11:
			if (geburtstag > 30) {
				alert("Das eingegebene Datum ist ungültig!");
				return false; 
			} else {
				break;
			}
		case 2:
			if (((geburtsjahr % 400 == 0) || (geburtsjahr % 4 == 0 && geburtsjahr % 100 != 0))
					&& geburtstag <= 29) 
				break;
			else if (geburtstag <= 28) 
				break; 
			else {
				alert("Das eingegebene Datum ist ungültig!");
				return false;
			}
		default :
			alert("Das eingegebene Datum ist ungültig!");
			return false;
	}
	
	// Prüfen, ob Geburtsname verschieden von Nachnamen ist
	
	if ($('#nachname').val() == $('#geburtsname').val()) {
		alert('Bitte geben Sie den Geburtsnamen nur an, ' +
			'wenn er sich vom aktuellen Nachnamen unterscheidet!');
		return false;		
	}
	
	return true;	
}
</script>

<body>
<h1>Patienten anlegen</h1>
<h2>Hinweise zur Eingabe</h2>
<p>
	Die Patientenliste überprüft, ob der eingegebene Patient bereits existiert oder ob er
	neu angelegt werden muss. Sie können dabei helfen, diese Entscheidung zu treffen, indem 
	Sie folgende Punkte beachten:
	<ul>
		<li> Geben Sie alle Ihnen bekannten Vornamen, getrennt durch Leerzeichen, an. </li>
		<li> Achten Sie bei Doppelnamen darauf, ob sie mit Bindestrich oder 
			zusammen geschrieben werden (z.B. "Annalena" oder "Anna-Lena").
		<li> Geben Sie den Geburtsnamen an, falls er vom aktuellen Nachnamen abweicht (z.B. bei 
 			Namenswechsel durch Heirat) </li>
	</ul> 
</p>
	<form action="/mzid/patients?tokenId=${it.tokenId}&callback=${it.callback}" method="post" id="form_person"
		onsubmit="return validateForm();">
		<label for="vorname">Vorname</label>
		<input name="vorname" id="vorname"/>
		<br/>
		<label for="nachname">Nachname</label>
		<input name="nachname" id="nachname"/>
		<br/>
		<label for="geburtsname">Geburtsname (falls abweichend)</label>
		<input name="geburtsname" id="geburtsname"/>
		<br/>
		<label for="geburtstag">Geburtsdatum (TT MM JJJJ)</label>
		<input name="geburtstag" id="geburtstag"/>
		<input name="geburtsmonat" id="geburtsmonat"/>
		<input name="geburtsjahr" id="geburtsjahr"/>
		<br/>
		<input type="submit" value="Abschicken!">
	</form>
</body>
</html>