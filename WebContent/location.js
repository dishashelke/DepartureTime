/**
 * Validations on latitude and longitude
 */
function validateForm() {
	var latitude = document.forms["mainform"]["latitude"].value;
	var longitude = document.forms["mainform"]["longitude"].value;

	if ((latitude == null || latitude == "")
			&& (longitude == null || longitude == "")) {
		alert("Latitude and Longitude must be filled out.\n Click on \"Get Location\" to get current location.");
		return false;
	} else if (latitude == null || latitude == "") {
		alert("Latitude must be filled out.");
		return false;
	} else if (longitude == null || longitude == "") {
		alert("Longitude must be filled out.");
		return false;
	} else if (isNaN(latitude) || latitude < -90 || latitude > 90) {
		alert("Invalid Latitude.");
		return false;
	} else if (isNaN(longitude) || longitude < -180 || longitude > 180) {
		alert("Invalid Longitude.");
		return false;
	}
}
/**
 * Find the current Location of the user
 */
function getLocation() {
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(showPosition);
	} else {
		var message = document.getElementById("message");
		message.innerHTML = "Geolocation is not supported by this browser.";
	}
}

function showPosition(position) {
	var latitude = document.getElementById("latitude");
	var longitude = document.getElementById("longitude");
	latitude.value = position.coords.latitude;
	longitude.value = position.coords.longitude;
}
