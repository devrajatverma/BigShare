package peerdiscovery;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Listner {

	public static String Listen() {
		DatagramSocket soc = null;
		try {
			soc = new DatagramSocket(8888);
			DatagramPacket p = new DatagramPacket(new byte[10], 10);
			soc.receive(p);
			soc.close();
			return p.getAddress().getHostAddress().toString();
		} catch (Exception e) {
			e.printStackTrace();
			soc.close();
		}
		return null;

	}

}
