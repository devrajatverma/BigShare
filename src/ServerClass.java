import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

public class ServerClass {

	File receivedzip;
	File destpath;

	public void activate(ProgressBar bar, ProgressIndicator indicator) {
		ServerSocket serverSocket = null;
		Socket socket = null;
		InputStream in = null;
		OutputStream out = null;

		try {
			try {
				serverSocket = new ServerSocket(2000);
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
			} catch (FileNotFoundException ex) {
				System.out.println("File not found. ");
			}

			byte[] bytes = new byte[8192]; // 1 mb buffer
			double status = 0D;
			int count;
			try {
				while ((count = in.read(bytes)) > 0) {
					out.write(bytes, 0, count);
					status += count;
					UI.bar = status / fileLength;
					UI.indicator = status / fileLength;
				}
				if (receivedzip.getName().endsWith(".zip")) {
					Compress.unzip(receivedzip.getPath(), destpath.getPath(), "");
				}
			} catch (IOException e) {
				System.out.println("Error occured in while loop in reading from socket and writing to file");
			}

		} finally {
			UI.loopControlReceive = false;

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

	public String getIp() throws Exception {
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			String ip = in.readLine();
			return ip;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}