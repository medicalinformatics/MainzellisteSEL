package de.unimainz.imbei.mzid.test;

import org.codehaus.jettison.json.JSONObject;

public class JSONTest {

	public static void main(String args[]) {
		try {
			JSONObject object = new JSONObject();
			JSONObject inner = new JSONObject();
			
			inner.put("inner_key", "inner_value");
			object.put("outer key", "outer value");
			object.put("key for inner", inner);
			System.out.println(object.toString());
		} catch (Exception e) {
			System.err.println(e.getStackTrace());
		}
	}
}
