package future_development;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class GetIP {
	public String getIp() {
		URL whatismyip = null;
		BufferedReader in = null;
		try {
			whatismyip = new URL("http://checkip.amazonaws.com");
			// http://ipecho.net/plain
			in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			String ip = in.readLine();
			return ip;
		} catch (Exception e) {
			return "No Internet";
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
