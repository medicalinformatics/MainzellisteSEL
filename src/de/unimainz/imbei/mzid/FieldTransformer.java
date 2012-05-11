package de.unimainz.imbei.mzid;

public interface FieldTransformer<IN extends Field, OUT extends Field>{

	public OUT transform(IN input);
}
