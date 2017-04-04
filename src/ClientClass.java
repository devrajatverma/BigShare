import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

public class ClientClass {
	Socket socket;
	String host;
	OutputStream out;
	InputStream in;
	File file = null;
	String filename = null;
	long fileLength = 0L;

	public void send(ProgressBar bar, ProgressIndicator indicator) {
		host = "127.0.0.1";
		out = null;
		in = null;

		try {
			socket = new Socket(host, 2001);
		} catch (UnknownHostException e) {
			System.out.println("UnknownHostException Occured");
		} catch (IOException e) {
			System.out.println("IOException Occured");
		}
		try {
			out = socket.getOutputStream();
		} catch (IOException e) {
			System.out.println("Problem in getting outputStream");
		}

		double status = 0L;
		byte[] bytes = new byte[8192]; // 1mb Buffer

		// Sending Name and size of the file

		DataOutputStream d = new DataOutputStream(out);
		try {
			d.writeUTF(filename);
		} catch (IOException e) {
			System.out.println("IOException in d.writeUTF(filename)");
		}
		try {
			d.writeLong(fileLength);
		} catch (IOException e) {
			System.out.println("IOException in d.writeLong(fileLength)");
		}

		try {
			in = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found");
		}

		int count = 0;
		try {
			while ((count = in.read(bytes)) > 0) {
				out.write(bytes, 0, count);
				status += count;
				bar.setProgress((status / fileLength) * 100D);
				indicator.setProgress((status / fileLength) * 100D);
			}
		} catch (IOException e) {
			System.out.println("IOException during Reading/Writing");
		}
		if (out != null)
			try {
				out.close();
			} catch (IOException e) {
				System.out.println("IOException during cloasing out stream");
			}
		if (in != null)
			try {
				in.close();
			} catch (IOException e) {
				System.out.println("IOException during closing in stream");
			}
		if (socket != null)
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("IOException during closing client socket");
			}
	}
}