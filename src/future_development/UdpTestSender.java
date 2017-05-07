package future_development;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class UdpTestSender {

	public static void main(String[] args) throws Exception {
		Discover h = new Discover();
		System.out.println(h.ip + "\n" + h.port);

		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter ip:");
		String ip = b.readLine();
		System.out.println("Enter port:");
		int port = Integer.parseInt(b.readLine());

		DatagramSocket soc = new DatagramSocket(8888);
		soc.setSoTimeout(400);

		int reads = 0, offset = 0;
		byte[] buffer = new byte[500];
		DatagramPacket pout = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), port);
		DatagramPacket pin = new DatagramPacket(new byte[10], 10); // to receive
																	// ACK
		File f = new File("C:/Users/RAJAT VERMA/Desktop/diff.png");
		long filesize = f.length();

		final ByteBuffer buf = ByteBuffer.allocate(8); // for 2 ints, an int is
														// 4 bytes long
		buf.putLong(filesize);
		buf.rewind();
		DatagramPacket packet = new DatagramPacket(buf.array(), buf.limit(), InetAddress.getByName(ip), port);
		// sending file size
		while (true) {
			soc.send(packet);
			soc.receive(pin);
			if (pin.getData().toString() == "ACK")
				break;
		}

		// FileInputStream fis = new FileInputStream(f);
		// while (true) {
		// reads = fis.read(buffer);
		// offset += reads;
		// if (reads < 500)
		// pout = new DatagramPacket(buffer, reads, InetAddress.getByName(ip),
		// port);
		//
		// while (pin.getData().toString() != "ACK") {
		// soc.send(pout);
		// soc.receive(pin);
		// }
		//
		// if (offset == filesize)
		// break;
		// }
		//
		// fis.close();
		soc.close();

	}

}
