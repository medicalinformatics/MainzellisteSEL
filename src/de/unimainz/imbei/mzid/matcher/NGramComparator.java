package de.unimainz.imbei.mzid.matcher;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import de.unimainz.imbei.mzid.PlainTextField;

public class NGramComparator extends FieldComparator<PlainTextField> {

	private int nGramLength = 2;

	private static Map<String, Set<String>> cacheNGrams = new HashMap<String, Set<String>>(50000);
	
	private Set<String> getNGrams(String input){
		Set<String> cacheResult = cacheNGrams.get(input);
		if (cacheResult != null) return Collections.unmodifiableSet(cacheResult);

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
		cacheNGrams.put(new String(input), output);
		return Collections.unmodifiableSet(output);
	}

	public NGramComparator (String fieldLeft, String fieldRight)
	{
		super(fieldLeft, fieldRight);
	}

	@Override
	public double compareBackend(PlainTextField fieldLeft, PlainTextField fieldRight) {
		assert (fieldLeft instanceof PlainTextField);
		assert (fieldRight instanceof PlainTextField);
		
		Set<String> nGramsLeft = getNGrams(fieldLeft.getValue());
		Set<String> nGramsRight = getNGrams(fieldRight.getValue());
		
		int nLeft = nGramsLeft.size();
		int nRight = nGramsRight.size();
		
		int nCommon = 0;
		Set<String> smaller;
		Set<String> larger;
		
		if (nLeft < nRight) {
			smaller = nGramsLeft;
			larger = nGramsRight;			
		} else {
			smaller = nGramsRight;
			larger = nGramsLeft;
		}
		
		for (String str : smaller) {
			if (larger.contains(str)) nCommon++;
		}
		
//		Set<String> intersection = new HashSet<String>(nGramsLeft);
//		intersection.retainAll(nGramsRight);
//		int nCommon = intersection.size();
		
		return 2.0 * nCommon / (nLeft + nRight);
	}

}
