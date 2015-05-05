import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.json.XML;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RouteAgency {
	private Map<String, List<Route>> routeAgency;
	private Agency agencies;

	private final String TOKEN_511 = "e7a5b51c-895b-4011-bf72-008d9d305e5b";

	public RouteAgency(Agency agencies) {
		routeAgency = new HashMap<String, List<Route>>();
		this.agencies = agencies;
		findRouteForAllAgencies();
	}

	public List<Route> getRoutesForAgency(String agency) {
		return routeAgency.get(agency);
	}

	public List<Route> getAllRoutes() {
		List<Route> list = new ArrayList<Route>();
		for (List<Route> routes : routeAgency.values()) {
			list.addAll(routes);
		}
		return list;
	}

	private List<Route> findRouteForAgency(String agencyName) {
		List<Route> routes = null;
		String xmlData = getRoutesXML(agencyName);
		routes = parseRoutesXML(agencyName, xmlData);
		return routes;
	}

	private void findRouteForAllAgencies() {
		Set<String> listOfAgencies = agencies.agencies();
		for (String agency : listOfAgencies) {
			routeAgency.put(agency, findRouteForAgency(agency));
		}
	}

	private String getRoutesXML(String agencyName) {
		String url = "http://services.my511.org/Transit2.0/GetRoutesForAgency.aspx?";
		url = url + "token=" + TOKEN_511 + "&agencyName=" + agencyName;

		Connection connect = new Connection(url);
		String xmlResult = null;
		try {
			xmlResult = connect.getData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return xmlResult;
	}

	private List<Route> parseRoutesXML(String agencyName, String xmlData) {
		
		// converting xml to json
		JSONObject xmlJSONObj = XML.toJSONObject(xmlData);
		int PRETTY_PRINT_INDENT_FACTOR = 4;
		String jsonLine = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);

		List<Route> routes = new ArrayList<Route>();

		JsonElement jelement = new JsonParser().parse(jsonLine);
		JsonObject jobject = jelement.getAsJsonObject();
		jobject = jobject.getAsJsonObject("RTT");
		jobject = jobject.getAsJsonObject("AgencyList");
		jobject = jobject.getAsJsonObject("Agency");
		jobject = jobject.getAsJsonObject("RouteList");
		JsonArray jarray = jobject.getAsJsonArray("Route");
		for (int i = 0; i < jarray.size(); i++) {
			List<String> directionCode = new ArrayList<String>();
			jobject = jarray.get(i).getAsJsonObject();
			String routeName = jobject.get("Name").toString();
			String routeCode = jobject.get("Code").toString();
			routeName = routeName.replaceAll("\"", "");
			routeCode = routeCode.replaceAll("\"", "");
			if (jobject.has("RouteDirectionList")) {
				jobject = jobject.getAsJsonObject("RouteDirectionList");
				jelement = jobject.get("RouteDirection");
				if (jelement.isJsonArray()) {
					JsonArray routeDirections = jelement.getAsJsonArray();
					for (int rD = 0; rD < routeDirections.size(); rD++) {
						jobject = routeDirections.get(rD).getAsJsonObject();
						String routeDirectionCode = jobject.getAsJsonPrimitive(
								"Code").toString();
						routeDirectionCode = routeDirectionCode.replaceAll("\"", "");
						directionCode.add(routeDirectionCode);
					}
				} else if (jelement.isJsonObject()) {
					jobject = jelement.getAsJsonObject();
					String routeDirectionCode = jobject.getAsJsonPrimitive(
							"Code").toString();
					routeDirectionCode = routeDirectionCode.replaceAll("\"", "");
					directionCode.add(routeDirectionCode);
				}
				routes.add(new Route(routeName, routeCode, directionCode,
						agencyName, agencies));
			}
			else{
				routes.add(new Route(routeName, routeCode, directionCode,
						agencyName, agencies));
			}
		}

		return routes;
	}
}
