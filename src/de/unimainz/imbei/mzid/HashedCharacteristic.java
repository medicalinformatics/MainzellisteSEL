package de.unimainz.imbei.mzid;

import java.util.BitSet;

public class HashedCharacteristic extends Characteristic<BitSet>{
	private BitSet value;
	
	public HashedCharacteristic(BitSet b) {
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
