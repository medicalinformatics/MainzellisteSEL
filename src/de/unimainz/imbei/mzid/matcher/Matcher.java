package de.unimainz.imbei.mzid.matcher;

import java.util.Iterator;
import java.util.Vector;

import de.unimainz.imbei.mzid.Patient;

/**
 * This class is reponsible for comparing a given patient to those present in the local database.
 * 
 * @author Martin
 *
 */
public interface Matcher {

	public MatchResult match(Patient a, Iterable<Patient> patientList);

}
