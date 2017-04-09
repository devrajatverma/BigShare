import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UI extends Application {
	// -----------UI Fields-----------------------------
	static double bar = 0, indicator = 0;
	static double barC = 0, indicatorC = 0;
	static Boolean loopControlSend = true, loopControlReceive = true;
	Label Incomming, labelDecompress;

	// ------server fields-------
	File destpath;
	File receivedzip;

	@Override
	public void start(Stage stage) {
		Platform.setImplicitExit(true);
		stage.setOnCloseRequest((ae) -> {
			Platform.exit();
			System.exit(0);
		});

		stage.setResizable(false);
		stage.setTitle("BIG SHARE By devrajatverma@gmail.com");
		ProgressBar progressBarClient = new ProgressBar();
		progressBarClient.setPrefSize(300, 30);
		ProgressIndicator progressIndicatorClient = new ProgressIndicator();
		progressIndicatorClient.setPrefSize(70, 70);
		ProgressBar progressBarServer = new ProgressBar();
		progressBarServer.setPrefSize(350, 30);
		ProgressIndicator progressIndicatorServer = new ProgressIndicator();
		progressIndicatorServer.setPrefSize(70, 70);

		ClientClass client = new ClientClass();

		FlowPane rootHome = new FlowPane(10, 10);
		rootHome.setAlignment(Pos.CENTER);
		Scene home = new Scene(rootHome, 560, 270);
		stage.setScene(home);
		stage.show();

		FlowPane rootSend = new FlowPane(10, 10);
		rootSend.setAlignment(Pos.TOP_CENTER);
		Scene send = new Scene(rootSend, 520, 330);
		Separator separatorSend = new Separator();
		separatorSend.setPrefWidth(480);

		FlowPane rootReceive = new FlowPane();
		rootReceive.setAlignment(Pos.TOP_CENTER);
		Scene receive = new Scene(rootReceive, 580, 470);
		Separator separatorReceive = new Separator();
		separatorReceive.setPrefWidth(560);

		InnerShadow innerShadow = new InnerShadow(8.0, Color.AQUA);

		// -----------------home------------------------

		Button btnSend = new Button("Send Mode", new ImageView("send.png"));
		btnSend.setEffect(innerShadow);
		btnSend.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		btnSend.setOnAction((ae) -> {
			Thread loop1 = new Thread(() -> {
				while (loopControlSend) {
					progressBarClient.setProgress(barC);
					progressIndicatorClient.setProgress(indicatorC);
				}
			}, "clientBar&IndicatorUpdator");
			loop1.start();

			stage.setScene(send);
			stage.show();
		});

		Button btnReceive = new Button("Receive Mode", new ImageView("receive.png"));
		btnReceive.setEffect(innerShadow);
		btnReceive.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		btnReceive.setOnAction((ae) -> {
			Thread loop2 = new Thread(() -> {
				while (loopControlReceive) {
					progressBarServer.setProgress(bar);
					progressIndicatorServer.setProgress(indicator);
				}
			}, "serverBar&IndicatorUpdator");
			loop2.start();

			new Thread(() -> server(), "activate").start();

			stage.setScene(receive);
			stage.show();

		});

		rootHome.getChildren().addAll(btnSend, btnReceive);

		// ------------send-----------
		TextField serverAddressTaker = new TextField("192.168.0.100");
		serverAddressTaker.setPromptText("Enter The Address");

		Text instruction = new Text("Either Chose File or Directory");
		instruction.setFont(new Font(30));
		Label labelPath = new Label();
		labelPath.setFont(new Font(20));
		labelPath.setPrefSize(520, 0);

		Button btnSendNow = new Button("Send", new ImageView("Send1.png"));
		btnSendNow.setDisable(true);
		btnSendNow.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		btnSendNow.setOnAction((ae) -> {
			client.host = serverAddressTaker.getText();
			new Thread(() -> {
				client.send();
			}, "Client.send()").start();
		});

		Button browseFile = new Button("Browse File to be sent", new ImageView("file.png"));
		browseFile.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		Button browseDir = new Button("Browse Directory to be sent", new ImageView("Directory.png"));
		browseDir.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		browseFile.setOnAction((ae) -> {
			FileChooser fileChoser = new FileChooser();
			File tempfile = fileChoser.showOpenDialog(stage);

			if (tempfile != null) {
				Platform.runLater(() -> btnSendNow.setDisable(false));
				labelPath.setText(tempfile.getAbsolutePath());
				client.file = tempfile;
				browseDir.setDisable(true);
			}
		});

		browseDir.setOnAction((ae) -> {
			DirectoryChooser directorychoser = new DirectoryChooser();
			File sourceDirectory = directorychoser.showDialog(stage);

			if (sourceDirectory != null) {
				browseFile.setDisable(true);
				labelPath.setText("COMPRESSING.... Please Be PETIENT");

				new Thread(() -> {
					Compress.zip(sourceDirectory.getPath(), sourceDirectory.getPath() + ".zip", "");
					Platform.runLater(() -> {
						labelPath.setText("Compressing Done...!!! Now Click On Send Icon");
						btnSendNow.setDisable(false);
					});
				}, "Compressing").start();

				client.file = new File(sourceDirectory.getPath() + ".zip");
			}
		});

		Label labelServerAddress = new Label("Enter The Address of SENDER: ");
		labelServerAddress.setFont(new Font(15));

		rootSend.getChildren().addAll(labelServerAddress, serverAddressTaker, separatorSend, instruction, labelPath,
				browseFile, browseDir, btnSendNow, progressBarClient, progressIndicatorClient);

		// ------------receive-------------------------------------------

		Text info = null;
		try {
			info = new Text("Local Address " + InetAddress.getLocalHost().getHostAddress().toString()
					+ " || Global Address " + getIp());
		} catch (Exception e) {
			e.printStackTrace();
		}
		info.setFont(new Font(20));

		Label labelDestinationPath = new Label();
		labelDestinationPath.setFont(new Font(14));
		labelDestinationPath.setPrefSize(580, 20);

		Label decompressing = new Label();
		decompressing.setFont(new Font(14));
		decompressing.setPrefSize(580, 20);

		Button btnDestPath = new Button("Browse Destination Directory", new ImageView("search.png"));
		btnDestPath.setContentDisplay(ContentDisplay.TOP);
		btnDestPath.setPrefWidth(340);
		btnDestPath.setOnAction((ae) -> {
			DirectoryChooser destinationDirectoryChoser = new DirectoryChooser();
			destpath = destinationDirectoryChoser.showDialog(stage);
			labelDestinationPath.setText(destpath.getPath());
		});

		Incomming = new Label();
		Incomming.setFont(new Font(25));
		Incomming.setPrefWidth(580);
		labelDecompress = new Label();
		labelDecompress.setFont(new Font(25));
		rootReceive.getChildren().addAll(info, separatorReceive, labelDestinationPath, decompressing, btnDestPath,
				Incomming, labelDecompress, progressBarServer, progressIndicatorServer);

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

	public static void main(String[] args) {
		launch(args);
	}

	public void server() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		InputStream in = null;
		OutputStream out = null;

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
			Platform.runLater(() -> Incomming.setText("Receiving: " + receivedzip.getName()));

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
					UI.indicator = UI.bar = status / fileLength;
				}
				if (receivedzip.getName().endsWith(".zip")) {
					Platform.runLater(() -> labelDecompress.setText("Decompressing Please Be Petient"));
					Compress.unzip(receivedzip.getPath(), destpath.getPath(), "");
					Platform.runLater(() -> labelDecompress.setText("Decompressing DONE"));
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
