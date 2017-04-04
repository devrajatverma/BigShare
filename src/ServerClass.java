import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

public class ServerClass implements Runnable {

	ServerSocket serverSocket = null;
	Socket socket = null;
	File receivedzip = null;
	File destpath = null;
	InputStream in = null;
	OutputStream out = null;
	String filename = null;
	long fileLength = 0L;
	long status = 0L;
	ProgressBar bar = null;
	ProgressIndicator indicator = null;

	@Override
	public void run() {
		try {
			try {
				serverSocket = new ServerSocket(2005);
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
			try {
				filename = d.readUTF();
				fileLength = d.readLong();
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
			int count;
			while ((count = in.read(bytes)) > 0) {
				out.write(bytes, 0, count);
				status += count;
				bar.setProgress((status / fileLength) * 100D);
				indicator.setProgress((status / fileLength) * 100D);
			}
			if (receivedzip.getName().endsWith(".zip")) {
				System.out.println("Decompressing...");
				Compress.unzip("C:\\Users\\RAJAT VERMA\\Desktop\\receiver\\" + filename,
						"C:\\Users\\RAJAT VERMA\\Desktop\\receiver\\", "");
			}

		} catch (IOException e) {
			System.out.println("Error occured in while loop in reading from socket and writing to file");
		} finally {
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