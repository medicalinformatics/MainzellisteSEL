package de.unimainz.imbei.mzid.matcher;

import de.unimainz.imbei.mzid.Field;

public interface FieldTransformer<IN extends Field<?>, OUT extends Field<?>>{

	public OUT transform(IN input);
	
	public Class<IN> getInputClass();
	public Class<OUT> getOutputClass();
}
