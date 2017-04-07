import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerClass {
	File destpath;

	public void activate() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		InputStream in = null;
		OutputStream out = null;
		File receivedzip = null;

		try {
			try {
				serverSocket = new ServerSocket(8888);
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
			} catch (IOException e) {
				System.out.println("Can't get socket input stream. ");
			}

			// Getting File name and size
			String filename = null;
			double fileLength = 0;

			DataInputStream d = new DataInputStream(in);
			try {
				filename = d.readUTF();
				fileLength = d.readDouble();
			} catch (IOException e) {
				System.out.println("error while reading filename and size");
			}

			receivedzip = new File(destpath.getPath() + "\\" + filename);

			try {
				out = new FileOutputStream(receivedzip);
			} catch (FileNotFoundException e) {
				System.out.println("File not found. ");
			}

			byte[] bytes = new byte[16000]; // 16 mb buffer
			double status = 0D;
			int count = 0;
			try {
				while ((count = in.read(bytes)) > 0) {
					out.write(bytes, 0, count);
					status += count;
					UI.bar = status / fileLength;
					UI.indicator = status / fileLength;
				}
				if (receivedzip.getName().endsWith(".zip")) {
					UI.decompressflag = true;
					Compress.unzip(receivedzip.getPath(), destpath.getPath(), "");
					UI.decompressflag = false;
				}
			} catch (IOException e) {
				System.out.println("Error occured in while loop in reading from socket and writing to file");
			}

		} finally {
			UI.loopControlReceive = false;
			UI.bar = 1.0;
			UI.indicator = 1.0;

			if (socket != null)
				try {
					socket.close();
				} catch (IOException e) {
					System.out.println("Error while closing socket");
				}
			if (serverSocket != null)
				try {
					serverSocket.close();
				} catch (IOException e) {
					System.out.println("Error while closing ServerSocket");
				}
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					System.out.println("Eoor while closing out stream");
				}
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					System.out.println("Error while closing in socket stream");
				}

			if (receivedzip.getName().endsWith(".zip"))
				receivedzip.delete();

		}
	}

}