import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.XML;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class StopDepartureTime {
	private final String TOKEN_511 = "e7a5b51c-895b-4011-bf72-008d9d305e5b";

	public Map<String, List<String>> get(List<Stop> stops) throws IOException {
		String xmlResult = null;
		Map<String, List<String>> departureTimes = new HashMap<String, List<String>>();
		for (Stop stop : stops) {
			String url = "http://services.my511.org/Transit2.0/GetNextDeparturesByStopCode.aspx?";
			url = url + "token=" + TOKEN_511 + "&stopcode=" + stop.stopCode();
			Connection connect = new Connection(url);
			xmlResult = connect.getData();
			departureTimes.putAll(parse(stop, xmlResult));// add to existing
															// map
		}
		return departureTimes;
	}

	private Map<String, List<String>> parse(Stop stop, String xmlLine) {
		Map<String, List<String>> departureTimes = new HashMap<String, List<String>>();
		String stopName = stop.stopName();
		String routeDirectionCode = stop.routeDirectionCode();
		String routeName = null;

		// converting xml to json
		JSONObject xmlJSONObj = XML.toJSONObject(xmlLine);
		int PRETTY_PRINT_INDENT_FACTOR = 4;
		String jsonLine = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);

		// parsing json object
		JsonElement jelement = new JsonParser().parse(jsonLine);
		JsonObject jobject = jelement.getAsJsonObject();
		jobject = jobject.getAsJsonObject("RTT");
		jobject = jobject.getAsJsonObject("AgencyList");

		// if predictions are not available
		if (jobject.has("Info")) {
			List<String> depatures = new ArrayList<String>();
			depatures.add("No Predictions Available");
			departureTimes.put(stopName, depatures);
			return departureTimes;
		}

		// if routeDirectionCode is not present
		if (routeDirectionCode == null || routeDirectionCode.isEmpty()) {
			return parseStopsNotHavingDirection(stop, jobject);
		}

		// if routeDirection is present
		jobject = jobject.getAsJsonObject("Agency");
		jobject = jobject.getAsJsonObject("RouteList");
		jelement = jobject.get("Route");
		if (jelement.isJsonArray()) {
			JsonArray routes = jelement.getAsJsonArray();
			for (int r = 0; r < routes.size(); r++) {
				jobject = routes.get(r).getAsJsonObject();

				jobject = jobject.getAsJsonObject("RouteDirectionList");
				jelement = jobject.get("RouteDirection");
				if (jelement.isJsonArray()) {
					JsonArray routeDirections = jelement.getAsJsonArray();
					for (int rD = 0; rD < routeDirections.size(); rD++) {
						jobject = routeDirections.get(rD).getAsJsonObject();
						routeName = jobject.getAsJsonPrimitive("Name")
								.toString();
						routeName = routeName.replaceAll("\"", "");
						List<String> depatures = parseStopList(jobject);
						if (!depatures.isEmpty())
							departureTimes.put(stop.agencyName() + ", "
									+ stopName + " => " + routeName, depatures);
					}
				} else if (jelement.isJsonObject()) {
					jobject = jelement.getAsJsonObject();
					List<String> depatures = parseStopList(jobject);
					routeName = jobject.getAsJsonPrimitive("Name").toString();
					routeName = routeName.replaceAll("\"", "");
					if (!depatures.isEmpty())
						departureTimes.put(stop.agencyName() + ", " + stopName
								+ " => " + routeName, depatures);
				}
			}
		} else if (jelement.isJsonObject()) {
			jobject = jelement.getAsJsonObject();
			jobject = jobject.getAsJsonObject("RouteDirectionList");
			jelement = jobject.get("RouteDirection");
			if (jelement.isJsonArray()) {
				JsonArray routeDirections = jelement.getAsJsonArray();
				for (int rD = 0; rD < routeDirections.size(); rD++) {
					jobject = routeDirections.get(rD).getAsJsonObject();
					routeName = jobject.getAsJsonPrimitive("Name").toString();
					routeName = routeName.replaceAll("\"", "");
					List<String> depatures = parseStopList(jobject);
					if (!depatures.isEmpty())
						departureTimes.put(stop.agencyName() + ", " + stopName
								+ " => " + routeName, depatures);
				}
			} else if (jelement.isJsonObject()) {
				jobject = jelement.getAsJsonObject();
				routeName = jobject.getAsJsonPrimitive("Name").toString();
				routeName = routeName.replaceAll("\"", "");
				List<String> depatures = parseStopList(jobject);
				if (!depatures.isEmpty())
					departureTimes.put(stop.agencyName() + ", " + stopName
							+ " => " + routeName, depatures);
			}
		}

		return departureTimes;
	}

	private Map<String, List<String>> parseStopsNotHavingDirection(Stop stop,
			JsonObject jobject) {
		Map<String, List<String>> departureTimes = new HashMap<String, List<String>>();
		String stopName = stop.stopName();
		String routeName = null;
		jobject = jobject.getAsJsonObject("Agency");
		jobject = jobject.getAsJsonObject("RouteList");
		JsonElement jelement = jobject.get("Route");
		if (jelement.isJsonArray()) {
			JsonArray routes = jelement.getAsJsonArray();
			for (int r = 0; r < routes.size(); r++) {
				jobject = routes.get(r).getAsJsonObject();
				routeName = jobject.getAsJsonPrimitive("Name").toString();
				routeName = routeName.replaceAll("\"", "");
				List<String> depatures = parseStopList(jobject);
				if (!depatures.isEmpty())
					departureTimes.put(stop.agencyName() + ", " + stopName
							+ " => " + routeName, depatures);
			}
		} else if (jelement.isJsonObject()) {
			jobject = jelement.getAsJsonObject();
			routeName = jobject.getAsJsonPrimitive("Name").toString();
			routeName = routeName.replaceAll("\"", "");
			List<String> depatures = parseStopList(jobject);
			if (!depatures.isEmpty())
				departureTimes.put(stop.agencyName() + ", " + stopName + " => "
						+ routeName, depatures);
		}

		return departureTimes;
	}

	private List<String> parseStopList(JsonObject jobject) {
		JsonElement jelement;
		List<String> depatures = new ArrayList<String>();
		jobject = jobject.getAsJsonObject("StopList");
		jobject = jobject.getAsJsonObject("Stop");
		jelement = jobject.get("DepartureTimeList");
		if (!jelement.isJsonPrimitive()) {
			jobject = jelement.getAsJsonObject();
			jelement = jobject.get("DepartureTime");
			if (!jelement.isJsonPrimitive()) {
				JsonArray times = jelement.getAsJsonArray();
				for (Object time : times) {
					depatures.add(time.toString());
				}
			} else if (jelement.isJsonObject()) {
				jobject = jelement.getAsJsonObject();
				depatures.add(jobject.toString());
			}
		} else {
			depatures.add("No predictions available.");
		}
		return depatures;
	}
}
