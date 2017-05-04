package stun;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import de.javawi.jstun.attribute.MessageAttributeParsingException;
import de.javawi.jstun.util.UtilityException;

public class Test2 {

	public static void main(String[] args)
			throws UnknownHostException, IOException, MessageAttributeParsingException, UtilityException {
		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter ip:");
		String ip = b.readLine();
		System.out.println("Enter port:");
		int port = Integer.parseInt(b.readLine());

		DatagramSocket s = new DatagramSocket();
		byte[] data = "hello".getBytes();
		DatagramPacket p = new DatagramPacket(data, data.length, InetAddress.getByName(ip), port);

		s.send(p);

		s.close();

	}

}
