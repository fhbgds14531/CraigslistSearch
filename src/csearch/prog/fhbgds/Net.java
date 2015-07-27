package csearch.prog.fhbgds;

import java.io.IOException;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Net {

	/**
	 * Exists solely for error handling. See Net.getZip for more info
	 */
	public static String[] getLocationFromZipcode(String zip){
		try {
			return Net.getZip(zip);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Gets location info from http://ziptasticapi.com
	 * @param zip User input zipcode
	 * @return {@link String}[] containing the City, State, and Country the zip is referencing
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String[] getZip(String zip) throws ClientProtocolException, IOException{
		String resString = Request.Get("http://ziptasticapi.com/" + zip).execute().returnContent().asString();

		JSONObject o = (JSONObject) JSONValue.parse(resString);
		String city = (String) o.get("city");
		String state = (String) o.get("state");
		String country = (String) o.get("country");
		
		city = city.toLowerCase(Locale.US);
		
		String[] location = {city, state, country};
		
		return location;
	}
	
}
