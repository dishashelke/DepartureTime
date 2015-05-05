import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Agency {
	private Map<String, String> agencies;

	public Agency() {
		agencies = new HashMap<String, String>();
	}

	public Agency(Map<String, String> agencies) {
		this.agencies = agencies;
	}

	public void add(String agencyName, String hasDirection) {
		agencies.put(agencyName, hasDirection);
	}

	public boolean hasDiection(String agency) {
		return Boolean.parseBoolean(agencies.get(agency));
	}

	public Set<String> agencies() {
		return agencies.keySet();
	}

}
