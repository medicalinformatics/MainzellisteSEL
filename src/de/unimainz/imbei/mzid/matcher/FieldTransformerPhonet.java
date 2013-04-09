package de.unimainz.imbei.mzid.matcher;

import java.util.Properties;
import java.util.Vector;

import de.unimainz.imbei.mzid.CompoundField;
import de.unimainz.imbei.mzid.Config;
import de.unimainz.imbei.mzid.Phonet;
import de.unimainz.imbei.mzid.PhonetRules;
import de.unimainz.imbei.mzid.PlainTextField;

/**
 * FieldTransformerPhonet is the transformation of one PlainTextField into another...
 *  
 * @author warnecke
 *
 */
public class FieldTransformerPhonet extends FieldTransformer<PlainTextField, PlainTextField>{

	private static final int FIRST_RULES = 0;

	@Override
	public PlainTextField transform(PlainTextField input) {
		// TODO Auto-generated method stub
		
		char[] result = new char[255];
	    String str = input.getValue();
	    int retCode = Phonet.phonet(str, result, 255, FIRST_RULES);

	    String resultString = new String(result);
	    int resIndex = resultString.indexOf('\0');
	        
	    return new PlainTextField(new String(resultString.substring(0, resIndex)));
	        
	}

	@Override
	public Class getInputClass() {
		// TODO Doku
		return PlainTextField.class;
	}

	@Override
	public Class getOutputClass() {
		// TODO Doku
		return PlainTextField.class;
	}
	
	// TODO Doku
	@Override
	public CompoundField<PlainTextField> transform(CompoundField<PlainTextField> input)
	{
		Vector<PlainTextField> outFields = new Vector<PlainTextField>(input.getSize());
		for (PlainTextField thisField : input.getValue())
		{
			outFields.add(this.transform(thisField));
		}
		CompoundField<PlainTextField> result = new CompoundField<PlainTextField>(outFields);
		return result;
	}
	
	
	// TODO Doku
	public void LoadRules()
	{
		Properties props = Config.instance.getProperties();
		if (props.containsKey("path_phonet"))
		{
			Phonet.loadPhonetRules(Config.instance.getProperty("path_phonet"));
		}
		else 
		{
			Phonet.setPhonetRules(PhonetRules.phonetRulesGerman);
		}
		
	}

}
