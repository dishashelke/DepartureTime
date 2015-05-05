//ref to Main class

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/NearestStops")
public class NearestStops extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static List<Stop> stopsOf511 = null;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		double latitude = Double.parseDouble(request.getParameter("latitude"));
		double longitude = Double
				.parseDouble(request.getParameter("longitude"));
		int radius = 1000;
		
		// Nearbystops from google
		NearbyPlaces nearby = new NearbyPlaces(radius);
		Set<String> stopsOfGoogle = nearby.get(latitude, longitude);
		
		// Get all the Agencies
		if (stopsOf511 == null) {
			AgencyParser parser = new AgencyParser();
			Agency sfAgencies = parser.parse();
			// Get the routes of every Agency
			RouteAgency routeAgency = new RouteAgency(sfAgencies);
			// Get Stops for every agency
			StopParser stopParser = new StopParser();
			List<Route> routes = routeAgency.getAllRoutes();
			stopsOf511 = new ArrayList<Stop>();
			for (Route route : routes) {
				stopsOf511.addAll(stopParser.parse(route));
			}
		}
		
		//Get the stops matching with the stops provided by google
		List<Stop> nearByStops = getMatchingStops(stopsOfGoogle, stopsOf511);
		//Get the Agency name, Stop name, Route name and departure time for these matching stops
		StopDepartureTime stoptime = new StopDepartureTime();
		Map<String, List<String>> departureTimes = stoptime.get(nearByStops);
		
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();

		writer.print("<center>");
		writer.print("<br><br><b>SF Public Transportation</b><br><br>");
		
		// print departure times
		if (!departureTimes.isEmpty()) {
			writer.print("<table>");
			writer.print("<tr><th>Departure Timings of Nearby Stops</th></tr>");
			for (Map.Entry<String, List<String>> entry : departureTimes
					.entrySet()) {
				String key = entry.getKey();
				String time = "";
				for (String value : entry.getValue())
					time += value + ", ";
				writer.print("<tr><td>");
				writer.print(key + ": " + time.substring(0, time.length()));
				writer.print("</tr></td>");
			}
		}
		writer.print("</tr><a href = \"index.html\"> Back to main page </a></td>");
		writer.print("</table>");
		writer.println("</center>");
		
	}
	private List<Stop> getMatchingStops(Set<String> stopsOfGoogle, List<Stop> stopsOf511){
		List<Stop> nearByStops = new ArrayList<Stop>();
		StringUtility utility = new StringUtility();

		for (String stopOfGoogle : stopsOfGoogle) {
			for (Stop stopOf511 : stopsOf511) {
				String stopNameOf511 = stopOf511.stopName();
				int difference = utility.minDistance(stopOfGoogle,
						stopNameOf511);
				int length = Math.max(stopOfGoogle.length(),
						stopNameOf511.length());
				// if(stopNameOf511.contains(stopOfGoogle)){
				if (difference < Math.floor(length * 1 / 4)) {
					nearByStops.add(stopOf511);
					break;
				}
			}
		}
		return nearByStops;
	}
}
