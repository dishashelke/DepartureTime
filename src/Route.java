import java.util.List;

public class Route {
	private String routeName;
	private String routeCode;
	private List<String> directionCode;
	private String agencyName;
	private Agency agencies;

	public Route(String name, String code, List<String> directionCode,
			String agencyName, Agency agencies) {
		this.routeName = name;
		this.routeCode = code;
		this.directionCode = directionCode;
		this.agencyName = agencyName;
		this.agencies = agencies;
	}

	public String routeName() {
		return routeName;
	}

	public String routeCode() {
		return routeCode;
	}

	public List<String> directionCode() {
		return directionCode;
	}

	public String agencyName() {
		return agencyName;
	}

	public Agency agencies() {
		return agencies;
	}

	public String toString() {
		return "RouteName:" + routeName + "- RouteCode:" + "-directionCode:"
				+ directionCode + "-agencyName:" + agencyName;
	}
}
