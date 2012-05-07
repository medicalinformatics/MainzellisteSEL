package de.unimainz.imbei.mzid;

/** Represents a pair of Strings. Used for implementation
 * of 2D maps as Map<StringPair, ?>.
 * @author borg
 *
 */
public class StringPair { // TODO: Pairs are evil!
	private String str1;
	private String str2;
	
	public StringPair(String str1, String str2) {
		this.str1 = str1;
		this.str2 = str2;
	}

	@Override
	public int hashCode()
	{
		String hashStr = str1 + "," + str2;
		return hashStr.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof StringPair))
			return false;
		
		StringPair other = (StringPair)obj;
		return str1.equals(other.str1) && str2.equals(other.str2);
	}
}
