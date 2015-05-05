public class Stop {
	private String stopName;
	private String stopCode;
	private Agency agencies;
	private String agencyName;
	private String routeName;
	private String routeDirectionCode;

	public Stop() {
		this.stopName = null;
		this.stopCode = null;
		this.agencies = new Agency();
		this.agencyName = null;
		this.routeName = null;
		this.routeDirectionCode = null;
	}
	public Stop(String stopName, String stopCode, Agency agencies, String agencyName, String routeName, String routeDirectionCode){
		this.stopName = stopName;
		this.stopCode = stopCode;
		this.agencies = agencies;
		this.agencyName = agencyName;
		this.routeName = routeName;
		this.routeDirectionCode = routeDirectionCode;
	}

	public String stopName() {
		return stopName;
	}

	public String stopCode() {
		return stopCode;
	}

	public Agency agencies() {
		return agencies;
	}

	public String agencyName() {
		return agencyName;
	}

	public String routeName() {
		return routeName;
	}
	public String routeDirectionCode() {
		return routeDirectionCode;
	}
	public String toString() {
		return "StopName:"+stopName+", RouteName:"+routeName+", RouteDirection:"+routeDirectionCode+", Agency:"+agencyName;
	}
}
