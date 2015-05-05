import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class NearbyPlaces {
	private int radius;
	private static final String SEARCH_TYPE = "transit_station";
	private static final String GOOGLE_NEARBY_SEARCH_API_KEY = "AIzaSyCCEeRCIH8qx9u48ijSPqJANVTw0r3yZk8";

	public NearbyPlaces() {
		this(1000);
	}

	public NearbyPlaces(int radius) {
		this.radius = radius;
	}

	public Set<String> get(double latitude, double longitude)
			throws IOException {
		String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
		url += "location=" + latitude + "," + longitude + "&radius=" + radius
				+ "&types=" + SEARCH_TYPE;
		url += "&key=" + GOOGLE_NEARBY_SEARCH_API_KEY;

		Connection connect = new Connection(url);
		String result = connect.getData();

		Set<String> stopNames = parseJson(result);
		return stopNames;
	}

	private Set<String> parseJson(String jsonLine) {
		Set<String> stopNames = new HashSet<String>();
		JsonElement jelement = new JsonParser().parse(jsonLine);
		JsonObject jobject = jelement.getAsJsonObject();
		JsonArray jarray = jobject.getAsJsonArray("results");
		for (int i = 0; i < jarray.size(); i++) {
			jobject = jarray.get(i).getAsJsonObject();
			String stop = jobject.get("name").toString();
			stop = stop.substring(1, stop.length() - 1);
			stopNames.add(stop);
		}
		return stopNames;
	}

}
