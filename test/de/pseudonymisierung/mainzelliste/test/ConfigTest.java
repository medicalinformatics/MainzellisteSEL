package de.pseudonymisierung.mainzelliste.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Pattern;

import org.junit.Test;
import static org.junit.Assert.*;

import com.sun.jersey.test.framework.JerseyTest;

import de.pseudonymisierung.mainzelliste.Config;

/**
 * Class tests for class de.pseudonymisierung.mainzelliste.Config
 */
public class ConfigTest extends JerseyTest {

	public ConfigTest() {
		super(TestUtilities.setUpTest());
	}

	/**
	 * Check that trailing whitespace is removed from configuration parameters.
	 */
	@Test
	public void testTrimProperties() {
		// First, check that parameter really has trailing whitespace in order
		// to
		// detect accidental trimming during
		// code cleanup.
		final String testParameter = "test.trimWhitespace";
		BufferedReader configFile = TestUtilities.getConfigfile();
		Pattern pattern = Pattern.compile("^\\s*" + testParameter + "\\s*=.*");
		String line;
		try {
			while ((line = configFile.readLine()) != null) {
				if (pattern.matcher(line).matches())
					break;
			}
			assertNotNull("Could not find test parameter for whitespace trimming in configuration file", line);
			String testParamValue = line.split("=\\s*")[1];
			assertTrue("Test parameter for whitespace trimming is empty or does not end with whitespace.",
					testParamValue.matches("\\S+\\s+"));
			// This is the actual test for whitespace trimming
			// 1. With method to get a single property
			assertEquals("Whitespace not trimmed from test parameter (Config#getProperty)", testParamValue.trim(),
					Config.instance.getProperty(testParameter));
			// 2. With method to get all properties
			assertEquals("Whitespace not trimmed form test parameter (Config#getProperties)", testParamValue.trim(),
					Config.instance.getProperties().get(testParamValue));

		} catch (IOException e) {
			fail("IO error while trying to read configuration file");
			e.printStackTrace();
		}
	}
}
