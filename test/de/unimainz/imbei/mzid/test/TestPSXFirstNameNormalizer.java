package de.unimainz.imbei.mzid.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.unimainz.imbei.mzid.CompoundField;
import de.unimainz.imbei.mzid.PSXFirstNameNormalizer;
import de.unimainz.imbei.mzid.PlainTextField;

public class TestPSXFirstNameNormalizer {

	private String testcases[] =
		{
			"  Sören Jürgen'",
			"'Hänschen-Klein'",
			"Karl Theodor Maria Nikolaus Johann Jacob Philipp Franz Joseph Sylvester"
		};
	
	private String expected[][] =
		{
			{ "SOEREN", "JUERGEN", "" },
			{ "HAENSCHEN", "KLEIN", ""},
			{ "KARL", "THEODOR", "MARIA NIKOLAUS JOHANN JACOB PHILIPP FRANZ JOSEPH SYLVESTER"}			
		};
	
	@Test
	public void test() {
		PSXFirstNameNormalizer firstNameNormalizer = new PSXFirstNameNormalizer();
		CompoundField<PlainTextField> expectedField = new CompoundField<PlainTextField>(3);
		for (int i = 0; i < testcases.length; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				expectedField.setValueAt(j, new PlainTextField(expected[i][j]));
			}
			assertEquals(expectedField, firstNameNormalizer.transform(new PlainTextField(testcases[i])));
		}
	}

}
