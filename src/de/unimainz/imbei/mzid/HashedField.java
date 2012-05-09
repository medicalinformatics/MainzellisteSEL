package de.unimainz.imbei.mzid;

import java.util.BitSet;

public class HashedField extends Field<BitSet>{
	private BitSet value;
	
	public HashedField(BitSet b) {
		super(b);
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
