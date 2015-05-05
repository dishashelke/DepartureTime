import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.XML;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class StopParser {
	private final String TOKEN_511 = "e7a5b51c-895b-4011-bf72-008d9d305e5b";

	private List<String> getData(Route route) {
		List<String> xmlResults = new ArrayList<String>();
		List<String> directionCodes = route.directionCode();
		if (directionCodes == null || directionCodes.isEmpty()) {
			String url = "http://services.my511.org/Transit2.0/GetStopsForRoute.aspx?";
			url = url + "token=" + TOKEN_511;
			url += "&routeIDF=" + route.agencyName() + "~" + route.routeCode();
			Connection connect = new Connection(url);
			String xmlResult = null;
			try {
				xmlResult = connect.getData();
			} catch (IOException e) {
				e.printStackTrace();
			}
			xmlResults.add(xmlResult);

		} else {
			for (String code : directionCodes) {
				String url = "http://services.my511.org/Transit2.0/GetStopsForRoute.aspx?";
				url = url + "token=" + TOKEN_511;
				url += "&routeIDF=" + route.agencyName() + "~"
						+ route.routeCode();
				url += "~" + code;
				Connection connect = new Connection(url);
				String xmlResult = null;
				try {
					xmlResult = connect.getData();
				} catch (IOException e) {
					e.printStackTrace();
				}
				xmlResults.add(xmlResult);
			}
		}
		return xmlResults;
	}

	public List<Stop> parse(Route route) {
		List<String> xmlResults = getData(route);
		List<Stop> stops = new ArrayList<Stop>();

		for (String xmlResult : xmlResults) {
			String routeDirectionCode = null;
			// converting xml to json
			JSONObject xmlJSONObj = XML.toJSONObject(xmlResult);
			int PRETTY_PRINT_INDENT_FACTOR = 4;
			String jsonLine = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);

			// parsing json object
			JsonElement jelement = new JsonParser().parse(jsonLine);
			JsonObject jobject = jelement.getAsJsonObject();
			jobject = jobject.getAsJsonObject("RTT");
			jobject = jobject.getAsJsonObject("AgencyList");
			jobject = jobject.getAsJsonObject("Agency");
			jobject = jobject.getAsJsonObject("RouteList");
			jobject = jobject.getAsJsonObject("Route");

			if (route.agencies().hasDiection(route.agencyName())) {
				jobject = jobject.getAsJsonObject("RouteDirectionList");
				jobject = jobject.getAsJsonObject("RouteDirection");
				routeDirectionCode = jobject.get("Code").toString();
				routeDirectionCode = routeDirectionCode.replaceAll("\"", "");
			}
			jobject = jobject.getAsJsonObject("StopList");
			jelement = jobject.get("Stop");
			if (jelement.isJsonArray()) {
				JsonArray stopsJson = jelement.getAsJsonArray();
				for (int i = 0; i < stopsJson.size(); i++) {
					jobject = stopsJson.get(i).getAsJsonObject();
					String stopName = jobject.get("name").toString();
					String stopCode = jobject.get("StopCode").toString();
					stopName = stopName.replaceAll("\"", "");
					stopCode = stopCode.replaceAll("\"", "");
					Stop stop = new Stop(stopName, stopCode, route.agencies(),
							route.agencyName(), route.routeName(),
							routeDirectionCode);
					stops.add(stop);
				}
			} else if (jelement.isJsonObject()) {
				jobject = jelement.getAsJsonObject();
				String stopName = jobject.get("name").toString();
				String stopCode = jobject.get("StopCode").toString();
				stopName = stopName.replaceAll("\"", "");
				stopCode = stopCode.replaceAll("\"", "");
				Stop stop = new Stop(stopName, stopCode, route.agencies(),
						route.agencyName(), route.routeName(),
						routeDirectionCode);
				stops.add(stop);
			}
		}
		return stops;
	}
}
