package de.unimainz.imbei.mzid;

public interface ID {
	public IDGenerator<? extends ID> getFactory();
	
	public String getId();
	public void setId(String id);
}
