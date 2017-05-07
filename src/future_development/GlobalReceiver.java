package future_development;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class GlobalReceiver {

	public static void main(String[] args) throws Exception {
		Discover h = new Discover();
		System.out.println(h.ip + "\n" + h.port);
		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter ip:");
		String ip = b.readLine();
		System.out.println("Enter port:");
		int port = Integer.parseInt(b.readLine());

		DatagramSocket s = new DatagramSocket(8888);
		byte[] data = "hello".getBytes();
		DatagramPacket p = new DatagramPacket(data, data.length, InetAddress.getByName(ip), port);
		s.send(p);
		s.close();
	}

}
