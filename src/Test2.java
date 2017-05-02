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

	public void init(byte[] id)
			throws UnknownHostException, IOException, MessageAttributeParsingException, UtilityException {
		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter ip:");
		String ip = b.readLine();
		System.out.println("Enter port:");
		int port = Integer.parseInt(b.readLine());
		System.out.println(ip + port);

		DatagramSocket s = new DatagramSocket();
		DatagramPacket p = new DatagramPacket(new byte[1000], 1000, InetAddress.getByName(ip), port);

		s.send(p);
		s.close();

	}

}
