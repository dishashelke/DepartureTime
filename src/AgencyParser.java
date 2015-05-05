import java.io.IOException;

import org.json.JSONObject;
import org.json.XML;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AgencyParser {

	private final String TOKEN_511 = "e7a5b51c-895b-4011-bf72-008d9d305e5b";

	private String getData() {
		String url = "http://services.my511.org/Transit2.0/GetAgencies.aspx?";
		url = url + "token=" + TOKEN_511;

		Connection connect = new Connection(url);
		String xmlResult = null;
		try {
			xmlResult = connect.getData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return xmlResult;
	}

	public Agency parse() {
		String xmlData = getData();
		Agency agencies = new Agency();

		// converting xml to json
		JSONObject xmlJSONObj = XML.toJSONObject(xmlData);
		int PRETTY_PRINT_INDENT_FACTOR = 4;
		String jsonLine = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);

		// parsing json object
		JsonElement jelement = new JsonParser().parse(jsonLine);
		JsonObject jobject = jelement.getAsJsonObject();
		jobject = jobject.getAsJsonObject("RTT");
		jobject = jobject.getAsJsonObject("AgencyList");
		JsonArray jarray = jobject.getAsJsonArray("Agency");
		for (int i = 0; i < jarray.size(); i++) {
			jobject = jarray.get(i).getAsJsonObject();
			String agencyName = jobject.get("Name").toString();
			String hasDirection = jobject.get("HasDirection").toString();
			agencyName = agencyName.substring(1, agencyName.length() - 1);
			agencies.add(agencyName, hasDirection);
		}
		return agencies;
	}
}
