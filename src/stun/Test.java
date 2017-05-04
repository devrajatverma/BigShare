package stun;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Test {

	public static void main(String[] args) throws Exception {
		UDPHole h = new UDPHole();
		System.out.println(h.ip + "\n" + h.port);

		DatagramSocket soc = new DatagramSocket(8888);
		soc.setReuseAddress(true);

		DatagramPacket p = new DatagramPacket(new byte[1000], 1000);
		soc.receive(p);
		System.out.println(new String(p.getData()));
		soc.close();
	}

}
