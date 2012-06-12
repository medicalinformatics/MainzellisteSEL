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

</script>

<body>
	<form action="/mzid/patients?tokenId=${it.tokenId}" method="post" id="form_person">
		<label for="vorname">Vorname</label>
		<input name="vorname" id="vorname"/>
		<br/>
		<label for="nachname">Nachname</label>
		<input name="nachname" id="nachname"/>
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