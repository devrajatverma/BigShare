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
	Socket socket;
	String host;
	OutputStream out;
	DataOutputStream d;
	InputStream in;
	File file;
	String filename;
	double fileLength;
	Thread t;

	public void compress(File sourceDirectory) {
		t = new Thread(() -> {
			Compress.zip(sourceDirectory.getPath(), sourceDirectory.getPath() + ".zip", "");
			file = new File(sourceDirectory.getPath() + ".zip");
		}, "Compressing");
		t.start();
	}

	public void send() {
		try {
			try {
				socket = new Socket(host, 2000);
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

			byte[] bytes = new byte[8192]; // 1mb Buffer
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

			double status = 0L;
			int count;
			try {
				while ((count = in.read(bytes)) > 0) {
					out.write(bytes, 0, count);
					status += count;
					UI.barC = status / fileLength;
					UI.indicatorC = status / fileLength;
				}
			} catch (IOException e) {
				System.out.println("Error during Reading form socket/Writing writing to file in client class");
			}

		} finally {
			UI.loopControlSend = false;

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