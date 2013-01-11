/**
 * 
 */
package de.unimainz.imbei.mzid;

import java.util.Properties;

/**
 * Simple ID generator that outputs consecutive integers. Mainly for testing purposes.
 * @author borg
 *
 */
public class SimpleIDGenerator implements IDGenerator<IntegerID> {

	int counter;
	IDGeneratorMemory mem;
	String idType;
	
	@Override
	public void init(IDGeneratorMemory mem, String idType, Properties props) {
		this.mem = mem;

		String memCounter = mem.get("counter");
		if(memCounter == null) memCounter = "0";
		this.counter = Integer.parseInt(memCounter);

		this.idType = idType;
	}

	@Override
	public synchronized IntegerID getNext() {
		IntegerID newID = new IntegerID(Integer.toString(this.counter + 1), idType);
		this.counter++;
		this.mem.set("counter", Integer.toString(this.counter));
		this.mem.commit();
		return newID;
	}

	@Override
	public IntegerID buildId(String id) {
		return new IntegerID(id, this.idType);
	}

	@Override
	public boolean verify(String id) {
		try {
			Integer.parseInt(id);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	@Override
	public String correct(String PIDString) {
		try {
			Integer.parseInt(PIDString);
		} catch (NumberFormatException e) {
			return null;
		}
		return PIDString;
	}

	@Override
	public String getIdType() {
		return idType;
	}

}
