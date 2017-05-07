package future_development;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpTestReceiver {

	public static void main(String[] args) throws Exception {
		Discover h = new Discover();
		System.out.println(h.ip + "\n" + h.port);
		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter ip:");
		String ip = b.readLine();

		System.out.println("Enter port:");
		int port = Integer.parseInt(b.readLine());
		DatagramSocket soc = new DatagramSocket(8888);
		DatagramPacket ack = new DatagramPacket("ACK".getBytes(), 3, InetAddress.getByName(ip), port);

		// bouncer to make hole for packets incoming from sender
		soc.send(new DatagramPacket(new byte[2], 2, InetAddress.getByName(ip), port));
		// ------------------------------------------------------------------------------

		byte[] size = new byte[8];
		int filesize = 0;
		soc.receive(new DatagramPacket(size, size.length));
		// soc.send(ack);

		filesize = Integer.parseInt(size.toString());
		System.out.println("received file size" + filesize);
		soc.close();
	}

}
