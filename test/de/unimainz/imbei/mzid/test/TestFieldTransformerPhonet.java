package de.unimainz.imbei.mzid.test;


import static org.junit.Assert.*;
import de.unimainz.imbei.mzid.*;


import org.junit.Before;
import org.junit.Test;

/**
 * TestFieldTransformerPhonet is the Test of the transformation of one PlainTextField into another...
 *  
 * @author warnecke
 *
 */
public class TestFieldTransformerPhonet {

	private FieldTransformerPhonet transformation;

	
	@Before
	public void setUp() throws Exception {
		this.transformation = new FieldTransformerPhonet();
	}
	
	
	@Test
	public void testPhonet() {
		String testCases[] = {"Mayer", "Meyer", "Sänger",
				"Ängerich", "Ölberg", "Üdersdorf", "Spaß",
				"Mähdrescher"};
		String expected[] = {"MEIA", "MEIA", "SENGA",
				"ENGERICH", "ÖLBERK", "ÜDERSDORF", "SPAS",
				"MEDRESHA"};
		
		for (int i = 0; i < testCases.length; i++)
		{
			PlainTextField field = new PlainTextField(testCases[i]);
			PlainTextField normalized = transformation.transform(field);
			assertEquals(expected[i], normalized.getValue());
		}
	}

}
