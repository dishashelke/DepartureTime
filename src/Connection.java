import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Connection {
	private URL obj;

	public Connection(String url) {
		url = url.replaceAll(" ", "%20");
		try {
			obj = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public String getData() throws IOException {
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer buffer = new StringBuffer();

		while (true) {
			inputLine = in.readLine();
			if (inputLine == null)
				break;
			if (inputLine != null
					&& (!inputLine.contains("No Predictions Available")))
				buffer.append(inputLine);
		}
		in.close();
		return buffer.toString();
	}
}
