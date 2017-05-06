package stun;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Test {

	public static void main(String[] args) throws Exception {
		Discover h = new Discover();
		System.out.println(h.ip + "\n" + h.port);

		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter ip:");
		String ip = b.readLine();
		System.out.println("Enter port:");
		int port = Integer.parseInt(b.readLine());

		DatagramSocket soc = new DatagramSocket(8888);

		// Bouncer to make hole
		DatagramPacket p = new DatagramPacket(new byte[10], 10, InetAddress.getByName(ip), port);
		soc.send(p);

		p = new DatagramPacket(new byte[1000], 1000);

		soc.receive(p);
		System.out.println(new String(p.getData()));
		soc.close();

		ServerSocket ser = new ServerSocket(8888);
		Socket soc2 = ser.accept();
		InputStream in = soc2.getInputStream();
		System.out.println("Bytes readed: " + in.read());
		ser.close();

	}

}
