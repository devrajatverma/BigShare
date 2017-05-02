
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import de.javawi.jstun.attribute.ChangeRequest;
import de.javawi.jstun.attribute.MappedAddress;
import de.javawi.jstun.attribute.MessageAttribute;
import de.javawi.jstun.attribute.MessageAttributeParsingException;
import de.javawi.jstun.header.MessageHeader;
import de.javawi.jstun.util.UtilityException;

public class StrunTcp {

	public static void main(String[] args)
			throws UnknownHostException, IOException, UtilityException, MessageAttributeParsingException {
		Socket s = new Socket("stun.services.mozilla.com", 3478);
		OutputStream out = s.getOutputStream();
		InputStream in = s.getInputStream();
		MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
		sendMH.generateTransactionID();

		// add an empty ChangeRequest attribute. Not required by the standard,
		// but JSTUN server requires it
		ChangeRequest changeRequest = new ChangeRequest();
		sendMH.addMessageAttribute(changeRequest);

		out.write(sendMH.getBytes());

		byte[] buf = new byte[1000];
		in.read(buf);

		MessageHeader receiveMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
		receiveMH.parseAttributes(buf);
		MappedAddress ma = (MappedAddress) receiveMH
				.getMessageAttribute(MessageAttribute.MessageAttributeType.MappedAddress);

		System.out.println(ma);
	}

}
