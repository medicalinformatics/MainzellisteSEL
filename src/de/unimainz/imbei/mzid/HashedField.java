package de.unimainz.imbei.mzid;

import java.util.BitSet;

public class HashedField extends Field<BitSet>{
	private BitSet value;
	
	public HashedField(BitSet b) {
		super(b);
	}
	
	/** Constructor that accepts a String of 0s and 1s. */
	public HashedField(String b)
	{
		BitSet bs = new BitSet(b.length());
		for (int i = 0; i < b.length(); i++)
		{
			switch (b.charAt(i))
			{
			case '1' :
				bs.set(i);
			case '0' :
				break;
			default : // illegal value
				return;
			}
		}
		this.value = bs;
	}
	
	@Override
	public BitSet getValue() {
		return value;
	}
	
	@Override
	public void setValue(BitSet hash) {
		this.value = hash;
	}
}
