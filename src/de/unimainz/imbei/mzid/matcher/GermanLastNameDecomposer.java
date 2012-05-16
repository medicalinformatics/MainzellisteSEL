package de.unimainz.imbei.mzid.matcher;

import java.util.Set;
import java.util.HashSet;

import de.unimainz.imbei.mzid.CompoundField;
import de.unimainz.imbei.mzid.PlainTextField;


/**
 * Decomposition of last name into components (3 by default),
 * with recognition of German components such as "von", "Freiherr" etc.
 * 
 * @author borg
 *
 */
public class GermanLastNameDecomposer implements FieldTransformer<PlainTextField, CompoundField<PlainTextField>>{
	
	private int nCcomponents = 3;

	/** Delimiters to recognize when decomposing Names as regular expression.
	 * 
	 */
	private String delimiters = "[ \\.:,;\\-']+";

	private static String nameParticles[] = {"AL", "AM", "AN", "AUF", 
		"D","DA", "DE", "DEL", "DELA", "DEM", "DEN", "DER", "DI", "DOS", "DR", "DU", 
		"EL", "EN", "ET", 
		"FREIFRAU", "FREIHERR", 
		"GRAEFIN", "GRAF", 
		"LA", "LE", 
		/*"MAC",*/ "MC", "MED", 
		"O", 
		"PD", "PROF", 
		"SR", 
		"UND", 
		"V", "VAN", "VO", "VOM", "VON", 
		"Y", 
		"ZU", "ZUM", "ZUR" };
	
	private Set<String> nameParticleSet;
	private int nComponents = 3;
	
	public GermanLastNameDecomposer()
	{
		this.nameParticleSet = new HashSet<String>();
		for (String particle : nameParticles)
		{
			nameParticleSet.add(particle);
		}
		
	}
	public CompoundField<PlainTextField> transform(PlainTextField input)
	{
		CompoundField<PlainTextField> output = new CompoundField<PlainTextField>(nComponents); 
		String substrings[] = input.getValue().split(delimiters);
		StringBuffer particles = new StringBuffer();
		StringBuffer otherComponents = new StringBuffer(); // collects all components > nComponents
		
		int i = 0;
		for (String thisSubstr : substrings)
		{
			// collect name particles ("von", "zu") separately
			if (nameParticleSet.contains(thisSubstr.toUpperCase()))
			{				
				if (particles.length() > 0) particles.append(" ");
				particles.append(thisSubstr);
				continue;
			}
			// Collect other components
			if (i < nComponents - 1)
			{
				output.setValueAt(i, new PlainTextField(thisSubstr));
				i++;
			} else {
				otherComponents.append(" ");
				otherComponents.append(thisSubstr);
			}
		}
		// fill remaining fields with empty Strings
		for (;i < nComponents - 1; i++)
			output.setValueAt(i, new PlainTextField(""));
		// add particles to last component
		if (otherComponents.length() > 0)
			otherComponents.append(" ");
		otherComponents.append(particles);
		output.setValueAt(nCcomponents - 1, new PlainTextField(otherComponents.toString()));
		return output;
		
	}
	
}
