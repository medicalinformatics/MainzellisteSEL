package de.unimainz.imbei.mzid;

/** Represents a pair of Strings. Used for implementation
 * of 2D maps as Map<StringPair, ?>.
 * @author borg
 *
 */
public class StringPair {
	public StringPair(String str1, String str2) {
		super();
		this.str1 = str1;
		this.str2 = str2;
	}

	private String str1;
	private String str2;

	public int hashCode()
	{
		String hashStr = str1 + "," + str2;
		return hashStr.hashCode();
	}

}
