package de.unimainz.imbei.mzid.matcher;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.codec.digest.DigestUtils;

import de.unimainz.imbei.mzid.HashedField;
import de.unimainz.imbei.mzid.Patient;
import de.unimainz.imbei.mzid.PlainTextField;

/**
 * BloomFilterTransformer and DiceFieldComparator implement the method presented by
 * Schnell et al (2009). BloomFilterTransformer splits a string enclosed in a PlainTextField
 * into n-grams 
 * @see <a href="http://www.biomedcentral.com/1472-6947/9/41">Rainer Schnell, Tobias Bachteler and Jörg Reiher:
 * Privacy-preserving record linkage using Bloom filters. BMC Medical Informatics and Decision Making 2009, 9:41</a>
 * @author borg
 *
 */
public class BloomFilterTransformer extends FieldTransformer<PlainTextField, HashedField> {

	public final static int hashLength = 500;
	private int nGramLength = 2;
	private int nHashFunctions = 15;
	
	/**
	 * Split the input string into n-grams of length nGramLength. The string
	 * is padded with nGramLength-1 spaces (trailing and leading).
	 * 
	 * For example, the input string "Java" yields the output n-grams 
	 * " J", "Ja", "av", "va", "a ".
	 * 
	 * @param input
	 * @return
	 */
	private Collection<String> getNGrams(String input){
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

		Vector<String> output = new Vector<String>(buffer.length() - nGramLength + 1);
		for (int i = 0; i <= buffer.length() - nGramLength; i++)
		{
			output.addElement(buffer.substring(i, i + nGramLength));
		}
		return output;
	}
	
	private int hash(String input, int index)
	{
		int hash1 = 0;
		int hash2 = 0;
		
		byte inputBytes[] = input.getBytes();
		byte md5[] = DigestUtils.md5(inputBytes);
		byte sha[] = DigestUtils.sha(inputBytes);
		
		// calculate significant Bytes of Hash
		int nSignBytes = (int) Math.ceil(Math.log(hashLength) / Math.log(256));

		// calculate combined Hash
		for (int byteInd = 0; byteInd < nSignBytes; byteInd++)
		{
			// byte is signed (-128 - 127), add 128 to get an unsigned value
		    hash1 += Math.pow(256, byteInd) * (md5[md5.length - 1 - byteInd] + 128);
		    hash2 += Math.pow(256, byteInd) * (sha[sha.length - 1 - byteInd] + 128);
		}
		
		return (hash1 + index * hash2) % hashLength;
	}
	

	@Override
	public HashedField transform(PlainTextField input)
	{	
		BitSet bitSet = new BitSet(hashLength);
		Collection<String> nGrams = getNGrams(input.getValue());
		for (String nGram : nGrams)
		{
			for (int i = 0; i < nHashFunctions; i++)
			{
				int hashRet = hash(nGram, i);
				bitSet.set(hashRet);
			}
		}
		
		HashedField output = new HashedField(bitSet);
		return output;
	}
	
	@Override
	public Class<PlainTextField> getInputClass()
	{
		return PlainTextField.class;
	}
	
	@Override
	public Class<HashedField> getOutputClass()
	{
		return HashedField.class;
	}

	public static void main(String args[])
	{
		BloomFilterTransformer transformer = new BloomFilterTransformer();
		PlainTextField testText1 = new PlainTextField("Andreas");
		Collection<String> nGrams = transformer.getNGrams(testText1.getValue());
		for (String str : nGrams)
			System.out.println(str);
		
		PlainTextField testText2 = new PlainTextField("Andreas");
				
		HashedField f1 = transformer.transform(testText1);
		HashedField f2 = transformer.transform(testText2);
		
		System.out.println(f1.toString());
		System.out.println(f2.toString());
		
		Patient p1 = new Patient();
		HashMap attr1 = new HashMap();
		attr1.put("vorname", f1);
		p1.setFields(attr1);
		
		Patient p2 = new Patient();
		HashMap attr2 = new HashMap();
		attr2.put("vorname", f2);
		p2.setFields(attr2);

		DiceFieldComparator comparator = new DiceFieldComparator("vorname", "vorname");
		
		System.out.println(comparator.compare(p1, p2));
		
	}
}
