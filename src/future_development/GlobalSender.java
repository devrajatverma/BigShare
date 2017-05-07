package future_development;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class GlobalSender {

	public static void main(String[] args) throws Exception {

		Discover h = new Discover();
		System.out.println(h.ip + "\n" + h.port);
		DatagramSocket soc = new DatagramSocket(8888);
		// soc.setReuseAddress(true);
		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter ip:");
		String ip = b.readLine();

		System.out.println("Enter port:");
		int port = Integer.parseInt(b.readLine());

		// Bouncer to make hole
		DatagramPacket p = new DatagramPacket("hole".getBytes(), 4, InetAddress.getByName(ip), port);
		soc.send(p);

		System.out.println("Waiting to sender now");
		p = new DatagramPacket(new byte[100], 100);
		soc.receive(p);
		System.out.println("received");
		System.out.println(new String(p.getData()));
		soc.close();

	}

}
