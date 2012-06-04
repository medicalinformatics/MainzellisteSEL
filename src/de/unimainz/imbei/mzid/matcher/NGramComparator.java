package de.unimainz.imbei.mzid.matcher;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import de.unimainz.imbei.mzid.Field;
import de.unimainz.imbei.mzid.PlainTextField;
import de.unimainz.imbei.mzid.Patient;

public class NGramComparator extends FieldComparator {

	private int nGramLength = 2;

	private Set<String> getNGrams(String input){
		// initialize Buffer to hold input and padding 
		// (nGramLength - 1 spaces on each side)
		StringBuffer buffer = new StringBuffer(input.length() + 2 * (nGramLength - 1));
		// Add leading padding
		for (int i = 0; i < nGramLength - 1; i++)
			buffer.append(" ");
		// add input string
		buffer.append(input);
		// add leading padding
		for (int i = 0; i < nGramLength - 1; i++)
			buffer.append(" ");

		HashSet<String> output = new HashSet<String>(buffer.length() - nGramLength + 1);
		for (int i = 0; i <= buffer.length() - nGramLength; i++)
		{
			output.add(buffer.substring(i, i + nGramLength));
		}
		return output;
	}

	public NGramComparator (String fieldLeft, String fieldRight)
	{
		super(fieldLeft, fieldRight);
	}

	@Override
	public double compare(Patient patientLeft, Patient patientRight) {
		Field<?> cLeft = patientLeft.getFields().get(fieldLeft);
		Field<?> cRight = patientRight.getFields().get(fieldRight);
		assert (cLeft instanceof PlainTextField);
		assert (cRight instanceof PlainTextField);
		
		Set<String> nGramsLeft = getNGrams((String) cLeft.getValue());
		Set<String> nGramsRight = getNGrams((String) cRight.getValue());
		
		int nLeft = nGramsLeft.size();
		int nRight = nGramsRight.size();
		nGramsLeft.retainAll(nGramsRight);
		int nCommon = nGramsLeft.size();
		
		return 2 * nCommon / (nLeft + nRight);
	}

}
