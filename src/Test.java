import java.net.DatagramPacket;

public class Test {

	public static void main(String[] args) throws Exception {
		UDPHole h = new UDPHole();
		System.out.println(h.ip + "\n" + h.port);

		DatagramPacket p = new DatagramPacket(new byte[1000], 1000);
		h.s.receive(p);
		System.out.println(p.getData().toString());
	}

}
