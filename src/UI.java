import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import peerdiscovery.Echo;
import peerdiscovery.Listner;

public class UI extends Application {
	// -----------UI Fields-----------------------------
	static double bar = 0, indicator = 0;
	Label Incomming, labelDecompress;
	// sender fields------------------
	static double barSender = 0, indicatorSender = 0;
	static Label labelSenderStatus;
	// ------server fields-----------
	static int port = 8888;
	File destpath, receivedzip;

	@Override
	public void start(Stage stage) {

		Platform.setImplicitExit(true);
		stage.setOnCloseRequest((ae) -> {
			Platform.exit();
			System.exit(0);
		});

		stage.setResizable(false);
		stage.setTitle("BigShare By devrajatverma@gmail.com");
		ProgressBar progressBarSender = new ProgressBar();
		progressBarSender.setPrefSize(500, 30);
		ProgressIndicator progressIndicatorSender = new ProgressIndicator();
		progressIndicatorSender.setPrefSize(80, 80);
		ProgressBar progressBarReceiver = new ProgressBar();
		progressBarReceiver.setPrefSize(550, 30);
		ProgressIndicator progressIndicatorReceiver = new ProgressIndicator();
		progressIndicatorReceiver.setPrefSize(80, 80);

		Sender sender = new Sender();

		FlowPane rootHome = new FlowPane(5, 5);
		rootHome.setAlignment(Pos.CENTER);
		Scene home = new Scene(rootHome, 550, 290);
		stage.setScene(home);
		stage.show();

		FlowPane rootSend = new FlowPane(10, 10);
		rootSend.setAlignment(Pos.TOP_CENTER);
		Scene send = new Scene(rootSend, 520, 390);
		Separator separatorSend = new Separator();
		separatorSend.setPrefWidth(480);

		FlowPane rootReceive = new FlowPane(10, 10);
		rootReceive.setAlignment(Pos.TOP_CENTER);
		Scene receive = new Scene(rootReceive, 580, 480);
		Separator separatorReceive = new Separator();
		separatorReceive.setPrefWidth(560);

		InnerShadow innerShadow = new InnerShadow(8.0, Color.AQUA);

		// -----------------home------------------------
		// serverAddtessTaker Belongs to send scene but due to ref put here
		TextField serverAddressTaker = new TextField();
		serverAddressTaker.setFont(new Font(15));
		// serverAddressTaker.setAlignment(Pos.CENTER);
		// --------------------------------------------------------------------------
		Label instructionHome = new Label("Sender Should Proceed First.");
		instructionHome.setFont(new Font(18));
		instructionHome.setPrefWidth(560);
		instructionHome.setAlignment(Pos.CENTER);
		instructionHome.setTextFill(Color.web("#ff0000"));
		Button btnSend = new Button("Send Mode", new ImageView("send.png"));
		btnSend.setEffect(innerShadow);
		btnSend.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		btnSend.setOnAction((ae) -> {

			Thread t1 = new Thread(() -> {
				while (true) {
					progressBarSender.setProgress(barSender);
					progressIndicatorSender.setProgress(indicatorSender);
				}
			}, "clientBar&IndicatorUpdator");
			t1.setPriority(3);
			t1.setDaemon(true);
			t1.start();

			new Thread(() -> {
				serverAddressTaker.setText(Listner.Listen());

			}).start();

			stage.setScene(send);
			stage.show();
		});

		Button btnReceive = new Button("Receive Mode", new ImageView("receive.png"));
		btnReceive.setEffect(innerShadow);
		btnReceive.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		btnReceive.setOnAction((ae) -> {

			Thread t2 = new Thread(() -> {
				while (true) {
					progressBarReceiver.setProgress(bar);
					progressIndicatorReceiver.setProgress(indicator);
				}
			}, "serverBar&IndicatorUpdator");
			t2.setPriority(3);
			t2.setDaemon(true);
			t2.start();

			new Thread(() -> {
				Echo.BroadcastIp();
				while (true)
					receiver();

			}).start();

			stage.setScene(receive);
			stage.show();

		});

		rootHome.getChildren().addAll(instructionHome, btnSend, btnReceive);

		// ------------send---------------------------------------------------------------------

		Label labelServerAddress = new Label("Tell Receiver to Press echo if Following Box is Empty.");
		labelServerAddress.setFont(new Font(20));
		labelServerAddress.setAlignment(Pos.CENTER);

		Label labelPath = new Label();
		labelPath.setFont(new Font(20));
		labelPath.setPrefSize(520, 0);

		Button btnSendNow = new Button("Send", new ImageView("Send1.png"));
		btnSendNow.setDisable(true);
		btnSendNow.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		btnSendNow.setOnAction((ae) -> {
			sender.host = serverAddressTaker.getText();
			labelSenderStatus.setText("SENDING " + sender.file.getName());
			Platform.runLater(() -> btnSendNow.setDisable(true));
			new Thread(() -> {
				sender.send();
				Platform.runLater(() -> {
					labelSenderStatus.setText("SENT " + sender.file.getName());
					btnSendNow.setDisable(false);
				});
			}, "Client.send()").start();
		});

		Button browseFile = new Button("Browse File to be sent", new ImageView("file.png"));
		browseFile.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		browseFile.setOnAction((ae) -> {
			FileChooser fileChoser = new FileChooser();
			File tempfile = fileChoser.showOpenDialog(stage);

			if (tempfile != null) {
				btnSendNow.setDisable(false);
				labelPath.setText(tempfile.getAbsolutePath());
				sender.file = tempfile;

			}
		});

		Button browseDir = new Button("Browse Directory to be sent", new ImageView("Directory.png"));
		browseDir.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		browseDir.setOnAction((ae) -> {
			DirectoryChooser directorychoser = new DirectoryChooser();
			File sourceDirectory = directorychoser.showDialog(stage);

			if (sourceDirectory != null) {
				File zip = new File(sourceDirectory.getPath() + ".zip");

				if (!(zip.exists())) {
					new Thread(() -> {
						Platform.runLater(() -> {
							btnSendNow.setDisable(true);
							labelPath.setText("COMPRESSING.... Please Be PETIENT");
						});

						Compress.zip(sourceDirectory.getPath(), sourceDirectory.getPath() + ".zip", "");
						Platform.runLater(() -> {
							labelPath.setText("Compressing Done...!!! Now Click On Send Icon");
							btnSendNow.setDisable(false);
						});
					}, "Compressing").start();
				}

				sender.file = zip;
				btnSendNow.setDisable(false);
			}
		});

		labelSenderStatus = new Label();
		labelSenderStatus.setFont(new Font(15));
		labelSenderStatus.setPrefWidth(520);
		rootSend.getChildren().addAll(labelServerAddress, serverAddressTaker, separatorSend, labelPath, browseFile,
				browseDir, btnSendNow, labelSenderStatus, progressBarSender, progressIndicatorSender);

		// ------------receive-------------------------------------------

		// Text info = null;
		// info = new Text("Public Address: " + getIp());
		// info.setFont(new Font(20));

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
			if (destpath != null)
				labelDestinationPath.setText(destpath.getPath());
		});

		Button echo = new Button("ECHO", new ImageView("echo.png"));
		echo.setContentDisplay(ContentDisplay.TOP);
		echo.setOnAction((ae) -> Echo.BroadcastIp());

		Incomming = new Label();
		Incomming.setFont(new Font(25));
		Incomming.setPrefWidth(580);
		labelDecompress = new Label();
		labelDecompress.setFont(new Font(25));
		rootReceive.getChildren().addAll(labelDestinationPath, decompressing, btnDestPath, echo, Incomming,
				labelDecompress, progressBarReceiver, progressIndicatorReceiver);

	}

	public static void main(String[] args) {
		launch(args);
	}

	public void receiver() {
		String filename = null;
		long fileLength = 0;
		long offset = 0;
		ServerSocket serverSocket = null;
		Socket socket = null;
		InputStream in = null;
		OutputStream out = null;
		DataInputStream din = null;
		DataOutputStream dout = null;
		File temp = null;

		temp = new File(".tmp");
		if (temp.exists()) {
			try (DataInputStream d = new DataInputStream(new FileInputStream(temp))) {
				offset = d.readLong();
			} catch (FileNotFoundException e) {
				System.out.println("temp file not found");
			} catch (IOException e) {
				System.out.println("Error while reading offset");
			}
		}

		try {
			try {
				serverSocket = new ServerSocket(port);
			} catch (IOException ex) {
				System.out.println("Can't setup server on this port number. ");
			}
			try {
				socket = serverSocket.accept();
			} catch (IOException ex) {
				System.out.println("Can't accept client connection. ");
			}

			try {
				out = socket.getOutputStream();
			} catch (IOException e) {
				System.out.println("Error While getting output stream");
			}
			dout = new DataOutputStream(out);
			try {
				dout.writeLong(offset);
			} catch (IOException e) {
				System.out.println("error while writing offset");
			}

			try {
				in = socket.getInputStream();
			} catch (IOException e) {
				System.out.println("Can't get socket input stream. ");
			}

			// Getting File name and size

			din = new DataInputStream(in);
			try {
				filename = din.readUTF();
				fileLength = din.readLong();
			} catch (IOException e) {
				System.out.println("error while reading filename and size");
			}

			if (destpath == null) {
				destpath = new File("").getAbsoluteFile();
				receivedzip = new File(destpath.getPath() + "/" + filename);
			} else
				receivedzip = new File(destpath.getPath() + "/" + filename);

			Platform.runLater(() -> Incomming.setText("Receiving: " + receivedzip.getName()));

			try {
				out = new FileOutputStream(receivedzip, true);
			} catch (FileNotFoundException e) {
				System.out.println("File not found. ");
			}

			byte[] bytes = new byte[1400];
			int reads = 0;
			try {
				while ((reads = in.read(bytes)) > 0) {
					out.write(bytes, 0, reads);
					offset += reads;
					UI.indicator = UI.bar = (double) offset / (double) fileLength;
				}
				if (receivedzip.getName().endsWith(".zip") && offset == fileLength) {
					Platform.runLater(() -> {
						labelDecompress.setText("Decompressing Please Be Petient");
						Incomming.setText("Received: " + receivedzip.getName());
					});

					Compress.unzip(receivedzip.getPath(), destpath.getAbsolutePath(), "");

					Platform.runLater(() -> labelDecompress.setText("Decompressing DONE"));
				}

			} catch (IOException e) {
				try (DataOutputStream d = new DataOutputStream(new FileOutputStream(temp));) {
					d.writeLong(offset);
				} catch (FileNotFoundException e1) {
					System.out.println("temp not found to write offset");
				} catch (IOException e2) {
					System.out.println("ioexception duing writing offset");
				}
				System.out.println("Error occured in while loop in reading from socket and writing to file");
			}

			Platform.runLater(() -> Incomming.setText("Recived: " + receivedzip.getName()));

		} finally {
			UI.bar = 1;
			UI.indicator = 1;

			if (serverSocket != null)
				try {
					serverSocket.close();
				} catch (IOException e) {
					System.out.println("Error while closing ServerSocket");
				}

			if (socket != null)
				try {
					socket.close();
				} catch (IOException e) {
					System.out.println("Error while closing socket");
				}

			if (din != null) {
				try {
					din.close();
				} catch (IOException e) {
					System.out.println("Error while closing din");
				}
			}

			if (dout != null) {
				try {
					dout.close();
				} catch (IOException e) {
					System.out.println("Error while closing dout");
				}
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

			if (temp.exists() && offset == fileLength)
				temp.delete();

			if (receivedzip.getName().endsWith(".zip") && offset == fileLength) {
				receivedzip.delete();
				temp.delete();
			}

		}
	}
}
