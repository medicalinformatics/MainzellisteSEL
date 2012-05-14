package de.unimainz.imbei.mzid.test;

import de.unimainz.imbei.mzid.*;
import de.unimainz.imbei.mzid.exceptions.InvalidIDException;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class TestPIDGenerator { 	

	private static StringBuffer sigma = new StringBuffer("0123456789ACDEFGHJKLMNPQRTUVWXYZ");	
	private List<String> pidList = new ArrayList<String>();
	
	public TestPIDGenerator() throws IOException{
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("test/pidlist.txt")));
			String line;
			while((line = br.readLine()) != null){
				pidList.add(line);
			}
			br.close();
		} catch(IOException e) {
			throw new IOException("Reading of test pids failed: " + e.getMessage());
		}
	}

	@Test
	public void testPIDGenerator()  {
		
		/* Read test data: 500 Pids, generated with psx 1.2 */
		

		PIDGenerator gen = PIDGenerator.init(1, 2, 3, 0);
		
		ListIterator<String> pidIterator = pidList.listIterator();
		int i=0;
		String nextPid;
		try {
			for(; pidIterator.hasNext(); i++){
				nextPid = pidIterator.next();
				PID a = gen.getNext();
				assertEquals("PID Nr. " + i + " was different than expected,", 
						nextPid, a.toString());
				assertTrue(a + " was supposed to be a valid PID, but wasn't.", gen.verify(a.toString()));
			}
		} catch (InvalidIDException e) {
			fail("Generation of " + i+1 + "th PID failed.");
			return;
		}
	}
	
	@Test
	public void testCorrection() {

		ListIterator<String> pidIterator = pidList.listIterator();
		
		/* Teste Fälle, in denen ein Zeichen falsch ist */

		String correctPID;
		StringBuffer brokenPID;
		String correctedPID;
		PIDGenerator gen = PIDGenerator.init(1, 2, 3, 0);
		
		int charPos;
		
		for (int i = 0; i<8; i++)
		{
			correctPID = pidIterator.next();
			brokenPID = new StringBuffer(correctPID);
			 
			/* Fehler an Stelle i einfügen, ersetze Zeichen durch
			 * nächstes im Alphabet 
			 */
			charPos = sigma.indexOf(brokenPID.substring(i, i));
			brokenPID.setCharAt(i, sigma.charAt((charPos + 1) % sigma.length()));
			 
			assertFalse("Wrong PID " + brokenPID + " was verified.",  gen.verify(brokenPID.toString()));
			correctedPID = gen.correct(brokenPID.toString());
			 
			assertEquals("Correction of changed character failed: ", correctPID, correctedPID.toString());			 
		}
		
		/* Teste Fälle, in denen zwei benachbarte Zeichen vertauscht sind */
		char temp;
		for (int i = 0; i<7; i++)
		{
			correctPID = pidIterator.next();
			brokenPID = new StringBuffer(correctPID);
			 
			/* Tausche Position i und i + 1 */
			temp = brokenPID.charAt(i);
			brokenPID.setCharAt(i, brokenPID.charAt(i + 1));
			brokenPID.setCharAt(i + 1, temp);
	 
			/* Manchmal stimmen zwei Positionen des PIDs überein, dann ist die Vertauschung wirkungslos */
			if (brokenPID.charAt(i) != brokenPID.charAt(i + 1))
					assertFalse(gen.verify(brokenPID.toString()));
			correctedPID = gen.correct(brokenPID.toString());
			 
			assertEquals("Correction of transposed characters failed: ", correctPID, correctedPID.toString());			 
		}
		
	}

	@Test
	public void testVerify() {

		ListIterator<String> pidIterator = pidList.listIterator();
		PIDGenerator gen = PIDGenerator.init(1, 2, 3, 0);
		
		/* Verify correct PIDs */
		String thisPID;
		while (pidIterator.hasNext())
		{
			thisPID = pidIterator.next();
			assertTrue("Correct PID " + thisPID + " was not verified", gen.verify(thisPID));
		}
		
		/* The following test build on modifications of the
		 * correct PID "M02YH0JJ"
		 * Only non-correctable PIDs here, correctable are handled
		 * by testCorrection().
		 */
		String brokenPID;
		
		/* Incorrect through replacement of two characters */
		brokenPID = "M12YH0AJ";
		assertFalse("Incorrect PID " + brokenPID + " was verified.", gen.verify(brokenPID));

		/* Incorrect through transposition of two non-adjacent characters */
		brokenPID = "H02YM0JJ";
		assertFalse("Incorrect PID " + brokenPID + " was verified.", gen.verify(brokenPID));
		
		/* Check that PID.correct fails (returns null for incorrectable PID */
		brokenPID = "M12YH0AJ";
		assertNull("PID correction did not fail for incorrectable PID " + brokenPID, gen.correct(brokenPID));

		brokenPID = "H02YM0JJ";
		assertNull("PID correction did not fail for incorrectable PID " + brokenPID, gen.correct(brokenPID));
		
		/* Empty String */
		brokenPID = "";
		assertFalse("Incorrect PID " + brokenPID + " was verified.", gen.verify(brokenPID));
		assertNull("PID correction did not fail for empty PID " + brokenPID, gen.correct(brokenPID));
		
		/* Null String */
		brokenPID = null;
		assertFalse("Incorrect PID " + brokenPID + " was verified.", gen.verify(brokenPID));
		assertNull("PID correction did not fail for null " + brokenPID, gen.correct(brokenPID));

		/* Wrong number of characters */
		brokenPID = "H02YM0J";
		assertFalse("Incorrect PID " + brokenPID + " was verified.", gen.verify(brokenPID));
		assertNull("PID correction did not fail for wrong number of characters " + brokenPID, gen.correct(brokenPID));
	}
}
