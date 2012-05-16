package de.unimainz.imbei.mzid.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.unimainz.imbei.mzid.CompoundField;
import de.unimainz.imbei.mzid.PlainTextField;
import de.unimainz.imbei.mzid.matcher.PSXFirstNameNormalizer;
import de.unimainz.imbei.mzid.matcher.PSXLastNameNormalizer;

public class TestPSXLastNameNormalizer {

	private String testcases[] =
		{
			"Müller-Lüdenscheidt",
			"Dr. Klöbner",
			"O'Brien",
			"Freiherr von und zu Guttenberg",
			"Guttenberg, Freiherr von und zu",
			"von Braunschweig-Wolfenbüttel "
			
		};
	
	private String expected[][] =
		{
			{ "MUELLER", "LUEDENSCHEIDT", "" },
			{ "KLOEBNER", "", "DR"},
			{ "BRIEN", "", "O" },
			{ "GUTTENBERG", "", "FREIHERR VON UND ZU"},			
			{ "GUTTENBERG", "", "FREIHERR VON UND ZU"},
			{ "BRAUNSCHWEIG", "WOLFENBUETTEL", "VON"}
		};
	
	@Test
	public void test() {
		PSXLastNameNormalizer lastNameNormalizer = new PSXLastNameNormalizer();
		CompoundField<PlainTextField> expectedField = new CompoundField<PlainTextField>(3);
		for (int i = 0; i < testcases.length; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				expectedField.setValueAt(j, new PlainTextField(expected[i][j]));
			}
			assertEquals(expectedField, lastNameNormalizer.transform(new PlainTextField(testcases[i])));
		}
	}

}
