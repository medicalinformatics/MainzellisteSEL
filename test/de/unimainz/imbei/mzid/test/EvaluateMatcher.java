package de.unimainz.imbei.mzid.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import de.unimainz.imbei.mzid.*;
import de.unimainz.imbei.mzid.matcher.*;

public class EvaluateMatcher {

	private static LinkedList<Patient> readPatients(String fileName)
	{
		LinkedList<Patient> result = new LinkedList<Patient>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName)));
			String line;
			line = br.readLine();
			String fieldNames[] = line.split(";");
			/* read columns */
			while ((line = br.readLine()) != null) {
				HashMap<String, Field<?>> fields = new HashMap<String, Field<?>>();
				String fieldValues[] = line.split(";");
				for (int i = 0; i < fieldValues.length; i++)
				{
					fields.put(fieldNames[i], new PlainTextField(fieldValues[i]));
				}
				Patient thisPatient = new Patient();
				thisPatient.setFields(fields);
				result.add(thisPatient);
			}
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
		return result;
	}

	public static void main(String args[]) {
		EpilinkMatcher matcher = (EpilinkMatcher) Config.instance.getMatcher();
		RecordTransformer transformer = Config.instance.getRecordTransformer();
		LinkedList<Patient> registryRaw = readPatients("test/registry.txt");
		LinkedList<Patient> cohortRaw = readPatients("test/cohort.txt");
		int nPairs = registryRaw.size() * cohortRaw.size(); 
		JFrame frame = new JFrame();
		JProgressBar progressBar = new JProgressBar(0, nPairs);
		progressBar.setValue(0);
		frame.add(progressBar);
		frame.setSize(500, 200);
		frame.setVisible(true);
		LinkedList<Boolean> isMatch = new LinkedList<Boolean>();
		LinkedList<Double> weight = new LinkedList<Double>();
		
		LinkedList<Patient> registry = new LinkedList<Patient>(); 		
		LinkedList<Patient> cohort = new LinkedList<Patient>(); 		

		for (Patient p : cohortRaw)
			cohort.add(transformer.transform(p));
		
		for (Patient p : registryRaw)
			registry.add(transformer.transform(p));

		// Die beiden Dateien enthalten besonders interessante (schwierige) Paare von Patienten,
		// sie werden parallel abgearbeitet, anstatt das Kreuzprodukt zu bilden
		Iterator<Patient> it1 = cohort.iterator();
		//Iterator<Patient> it2 = registry.iterator();
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("test/weights.txt", false)));
			bw.write("id1;id2;weight;is_match\n");
			while(it1.hasNext())
			{
				Patient p1 = it1.next();
				Iterator<Patient> it2 = registry.iterator();
				while (it2.hasNext())
				{
					Patient p2 = it2.next();
//			while(it1.hasNext() && it2.hasNext())
//			{
//				Patient p1 = it1.next();
//				Patient p2 = it2.next();
					double thisWeight = matcher.calculateWeight(p1, p2);
					String id1 = p1.getFields().get("ID_GoldStandard").getValue().toString();
					String id2 = p2.getFields().get("ID_GoldStandard").getValue().toString();
					Boolean thisIsMatch = id1.equals(id2);
					if (thisWeight > 0.3) bw.write(id1 + ";" + id2 + ";" + thisWeight + ";" + thisIsMatch + "\n");
//					progressBar.setValue(progressBar.getValue() + registry.size());
				}
				progressBar.setValue(progressBar.getValue() + registry.size());
			}
			bw.close();
			frame.dispose();
			System.exit(0);
		} catch (IOException e)
		{
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
