package de.unimainz.imbei.mzid.matcher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import de.unimainz.imbei.mzid.Config;
import de.unimainz.imbei.mzid.Config.FieldType;
import de.unimainz.imbei.mzid.Patient;
import de.unimainz.imbei.mzid.exceptions.InternalErrorException;
import de.unimainz.imbei.mzid.matcher.MatchResult.MatchResultType;
import de.unimainz.imbei.mzid.Config;

public class EpilinkMatcher implements Matcher {

	private double threshold_match;
	private double threshold_non_match;
	private double error_rate;
	
	private Map<String, FieldComparator> comparators;
	private Map<String, Double> frequencies;
	/** Field weights */
	private Map<String, Double> weights;
	
	private double calculateWeight(Patient left, Patient right)
	{

		double weightSum = 0; // holds sum of field weights 
		double totalWeight = 0; // holds total weight
		for (String fieldName : weights.keySet())
		{
			double fieldWeight = weights.get(fieldName); 
			weightSum += fieldWeight;
			totalWeight += comparators.get(fieldName).compare(left, right) * fieldWeight;
		}
		totalWeight /= weightSum;
		return totalWeight;
	}
	
	public EpilinkMatcher(Properties props) throws InternalErrorException
	{
		// Get error rate (is needed for weight computation below)
		this.error_rate = Double.parseDouble(props.getProperty("matcher.epilink.error_rate"));			

		// Initialize internal maps
		this.comparators = new HashMap<String, FieldComparator>();
		this.frequencies = new HashMap<String, Double>();
		this.weights = new HashMap<String, Double>();
		
		// Get names of fields from config vars.*
		Pattern p = Pattern.compile("^field\\.(\\w+)\\.type");
		java.util.regex.Matcher m;

		// Build maps of comparators, frequencies and attribute weights from Properties
		for (Object key : props.keySet())
		{
			m = p.matcher((String) key);
			if (m.find()){
				String fieldName = m.group(1);
				String fieldCompStr = props.getProperty("field." + fieldName + ".comparator").trim();

				try {
					Class<FieldComparator> fieldCompClass = (Class<FieldComparator>) Class.forName("de.unimainz.imbei.mzid.matcher." + fieldCompStr);
					Constructor<FieldComparator> fieldCompConstr = fieldCompClass.getConstructor(String.class, String.class);
					FieldComparator fieldComp = fieldCompConstr.newInstance(fieldName, fieldName);
					comparators.put(fieldName, fieldComp);
				} catch (Exception e) {
					System.err.println(e.getMessage());
					throw new InternalErrorException();
				}

				// set frequency
				double frequency = Double.parseDouble(props.getProperty("matcher.epilink." + fieldName + ".frequency"));
				frequencies.put(fieldName, frequency);
				// calculate field weights
				// log_2 ((1 - e_i) / f_i)
				// all e_i have same value in this implementation
				double weight = (1 - error_rate) / frequency;
				weight = Math.log(weight) / Math.log(2);
				weights.put(fieldName, weight);
			}
		}
		// assert that Maps have the same keys
		assert(frequencies.keySet().equals(comparators.keySet()));
		assert(frequencies.keySet().equals(weights.keySet()));
		
		// load other config vars
		this.threshold_match = Double.parseDouble(props.getProperty("matcher.epilink.threshold_match"));
		this.threshold_non_match = Double.parseDouble(props.getProperty("matcher.epilink.threshold_non_match"));
	}
	
	@Override
	public MatchResult match(Patient a, Iterable<Patient> patientList) {
		
		Patient bestMatch = null;
		double bestWeight = Double.NEGATIVE_INFINITY;
		
		for (Patient b : patientList)
		{
			// assert that the persons have the same Fields 
			assert (a.getFields().keySet().equals(b.getFields().keySet()));
			double weight = calculateWeight(a, b);
			if (weight > bestWeight)
			{
				bestWeight = weight;
				bestMatch = b;
			}						
		}
	
		if (bestWeight >= threshold_match){
			return new MatchResult(MatchResultType.MATCH, bestMatch);			
		} else if (bestWeight < threshold_match && bestWeight > threshold_non_match) {
			return new MatchResult(MatchResultType.POSSIBLE_MATCH, bestMatch);
		} else {
			return new MatchResult(MatchResultType.NON_MATCH, null);
		}				
	}
	
	public static void main(String arg[])
	{
	}
}
