import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import de.javawi.jstun.attribute.MappedAddress;
import de.javawi.jstun.attribute.MessageAttributeParsingException;
import de.javawi.jstun.util.UtilityException;

public class Test {

	public static void main(String[] args) throws IOException, MessageAttributeParsingException, UtilityException {
		UDPHole h = new UDPHole();
		MappedAddress ma = null;
		try {
			ma = h.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(InetAddress.getLocalHost() + " " + h.s.getLocalPort());
		System.out.println(ma);

		DatagramPacket p = new DatagramPacket(new byte[1000], 1000);
		h.s.receive(p);

		System.out.println(p.toString());

		h.s.close();
	}

}
