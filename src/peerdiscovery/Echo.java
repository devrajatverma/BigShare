package peerdiscovery;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Echo {

	public static void BroadcastIp() {
		byte[] hello = "hello".getBytes();
		DatagramSocket soc = null;
		try {
			soc = new DatagramSocket();
			soc.setBroadcast(true);
			DatagramPacket p = new DatagramPacket(hello, hello.length, InetAddress.getByName("255.255.255.255"), 8888);
			soc.send(p);
			soc.close();

		} catch (Exception e) {
			soc.close();
			e.printStackTrace();
		}

	}

}
