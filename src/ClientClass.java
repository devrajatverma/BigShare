import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientClass {

	String host;
	File file;

	public void send() {
		Socket socket = null;
		OutputStream out = null;
		DataOutputStream d = null;
		InputStream in = null;
		String filename = null;
		double fileLength = 0;

		try {
			try {
				socket = new Socket(host, 8888);
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

			filename = file.getName();
			fileLength = file.length();
			// Sending Name and size of the file

			d = new DataOutputStream(out);
			try {
				d.writeUTF(filename);
				d.writeDouble(fileLength);
			} catch (IOException e) {
				System.out.println("Error while writing filename and size");
			}

			try {
				in = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				System.out.println("File Not Found");
			}

			byte[] bytes = new byte[16000]; // 16 mb Buffer
			double status = 0L;
			int count = 0;
			try {
				while ((count = in.read(bytes)) > 0) {
					out.write(bytes, 0, count);
					status += count;
					UI.indicatorC = UI.barC = status / fileLength;
				}
			} catch (IOException e) {
				System.out.println("Error during Reading form socket/Writing writing to file in client class");
			}

		} finally {
			UI.loopControlSend = false;
			UI.barC = 1.0;
			UI.indicatorC = 1.0;

			if (socket != null)
				try {
					socket.close();
				} catch (IOException e) {
					System.out.println("IOException during closing client socket");
				}

			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					System.out.println("IOException during cloasing out stream");
				}
			if (d != null)
				try {
					d.close();
				} catch (IOException e1) {
					System.out.println("Error while closing DataOutputStream");
				}

			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					System.out.println("IOException during closing in stream");
				}

			if (file.getName().endsWith(".zip"))
				file.delete();

		}
	}
}