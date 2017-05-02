import java.io.DataInputStream;
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
	final int port = 8888;
	String host;
	File file;

	public void send() {
		Socket socket = null;
		InputStream in = null;
		OutputStream out = null;
		DataOutputStream dout = null;
		DataInputStream din = null;
		String filename = null;
		long fileLength = 0;
		long offset = 0;

		try {
			try {
				socket = new Socket(host, port);
			} catch (UnknownHostException e) {
				System.out.println("UnknownHostException Occured");
			} catch (IOException e) {
				System.out.println("IOException Occured during getting Socket");
			}
			try {
				in = socket.getInputStream();
			} catch (IOException e) {
				System.out.println("Problem in getting inputStream");
			}
			// Reading Offset
			din = new DataInputStream(in);
			try {
				offset = din.readLong();
			} catch (IOException e1) {
				System.out.println("Error in Getting offset");
			}

			try {
				out = socket.getOutputStream();
			} catch (IOException e) {
				System.out.println("Problem in getting outputStream");
			}
			dout = new DataOutputStream(out);

			filename = file.getName();
			fileLength = file.length();

			// Sending Name and size of the file
			try {
				dout.writeUTF(filename);
				dout.writeLong(fileLength);
			} catch (IOException e) {
				System.out.println("Error while writing filename and size");
			}

			try {
				in = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				System.out.println("File Not Found");
			}

			try {
				in.skip(offset);
			} catch (IOException e1) {
				System.out.println("Error while skipping already send data");
			}

			byte[] bytes = new byte[16000]; // 16 mb Buffer

			int reads = 0;
			try {
				while ((reads = in.read(bytes)) > 0) {
					out.write(bytes, 0, reads);
					offset += reads;
					UI.indicatorC = UI.barC = (double) offset / (double) fileLength;
				}
			} catch (IOException e) {
				System.out.println("Error during Reading form socket/Writing writing to file in client class");
			}

		} finally {
			UI.barC = 1.0;
			UI.indicatorC = 1.0;
			UI.loopControlSend = false;

			if (socket != null)
				try {
					socket.close();
				} catch (IOException e) {
					System.out.println("IOException during closing client socket");
				}

			if (din != null)
				try {
					din.close();
				} catch (IOException e1) {
					System.out.println("Error while closing DataOutputStream");
				}

			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					System.out.println("IOException during closing in stream");
				}

			if (dout != null)
				try {
					dout.close();
				} catch (IOException e1) {
					System.out.println("Error while closing DataOutputStream");
				}

			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					System.out.println("IOException during cloasing out stream");
				}

			if (file.getName().endsWith(".zip") && offset == fileLength)
				file.delete();
		}
	}
}