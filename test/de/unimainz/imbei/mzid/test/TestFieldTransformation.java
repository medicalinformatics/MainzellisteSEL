package de.unimainz.imbei.mzid.test;

import static org.junit.Assert.*;
import de.unimainz.imbei.mzid.*;

import org.junit.Before;
import org.junit.Test;

@Deprecated
public class TestFieldTransformation {

	private FieldTransformation transformation;
	
	@Before
	public void setUp() throws Exception {
		this.transformation = new FieldTransformation();
	}

	@Test
	public void testNormalizeString() {
		String testCases[] = {"Müller", "Böhlke", "Sänger",
				"Ängerich", "Ölberg", "Üdersdorf", "Spaß",
				"  Mähdrescher-'"};
		String expected[] = {"MUELLER", "BOEHLKE", "SAENGER",
				"AENGERICH", "OELBERG", "UEDERSDORF", "SPASS",
				"MAEHDRESCHER"};
		
		for (int i = 0; i < testCases.length; i++)
		{
			PlainTextField field = new PlainTextField(testCases[i]);
			PlainTextField normalized = transformation.normalizeString(field);
			assertEquals(expected[i], normalized.getValue());
		}
	}

}
