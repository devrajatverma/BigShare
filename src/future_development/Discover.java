package future_development;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import de.javawi.jstun.attribute.ChangeRequest;
import de.javawi.jstun.attribute.MappedAddress;
import de.javawi.jstun.attribute.MessageAttribute;
import de.javawi.jstun.header.MessageHeader;

public class Discover {
	public String ip;
	public int port;

	public Discover() throws Exception {
		MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
		// sendMH.generateTransactionID();

		// add an empty ChangeRequest attribute. Not required by the
		// standard,
		// but JSTUN server requires it

		ChangeRequest changeRequest = new ChangeRequest();
		sendMH.addMessageAttribute(changeRequest);
		byte[] data = sendMH.getBytes();

		DatagramSocket s = new DatagramSocket(8888);
		DatagramPacket p = new DatagramPacket(data, data.length, InetAddress.getByName("stun1.l.google.com"), 19302);
		s.send(p);

		DatagramPacket rp = new DatagramPacket(new byte[100], 100);
		s.receive(rp);
		MessageHeader receiveMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingResponse);
		// System.out.println(receiveMH.getTransactionID().toString() + "Size:"
		// + receiveMH.getTransactionID().length);
		receiveMH.parseAttributes(rp.getData());
		MappedAddress ma = (MappedAddress) receiveMH
				.getMessageAttribute(MessageAttribute.MessageAttributeType.MappedAddress);

		ip = ma.getAddress().toString();
		port = ma.getPort();
		s.close();
	}
}