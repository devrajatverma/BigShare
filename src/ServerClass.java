import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerClass {
	static ServerSocket serverSocket = null;
	static Socket socket = null;
	static InputStream in = null;
	static OutputStream out = null;
	static String filename = null;
	static long fileLength = 0L;
	static byte[] bytes = new byte[8192]; // 1 mb buffer
	static long status = 0L;

	public static void main(String[] args) throws IOException {

		try {
			serverSocket = new ServerSocket(2001);
		} catch (IOException ex) {
			System.out.println("Can't setup server on this port number. ");
		}

		try {
			socket = serverSocket.accept();
		} catch (IOException ex) {
			System.out.println("Can't accept client connection. ");
		}

		try {
			in = socket.getInputStream();
		} catch (IOException ex) {
			System.out.println("Can't get socket input stream. ");
		}

		// Getting File name and size

		DataInputStream d = new DataInputStream(in);
		filename = d.readUTF();
		fileLength = d.readLong();

		System.out.println(filename + "\n" + fileLength); // Test

		try {
			out = new FileOutputStream("C:\\Users\\RAJAT VERMA\\Desktop\\" + filename + "Received");
		} catch (FileNotFoundException ex) {
			System.out.println("File not found. ");
		}

		int count;
		while ((count = in.read(bytes)) > 0) {
			out.write(bytes, 0, count);
			status += count;
			System.out.println((status / fileLength) * 100 + "%" + " Received");
		}

		if (out != null)
			out.close();
		if (in != null)
			in.close();
		if (socket != null)
			socket.close();
		if (serverSocket != null)
			serverSocket.close();
	}
}
