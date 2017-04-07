import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
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
	static double bar = 0, indicator = 0;
	static double barC = 0, indicatorC = 0;
	static Boolean loopControlSend = true, loopControlReceive = true, decompressflag = false;

	@Override
	public void start(Stage stage) {
		Platform.setImplicitExit(true);
		stage.setOnCloseRequest((ae) -> {
			Platform.exit();
			System.exit(0);
		});

		ProgressBar progressBarClient = new ProgressBar();
		progressBarClient.setPrefSize(300, 25);
		ProgressIndicator progressIndicatorClient = new ProgressIndicator();
		progressIndicatorClient.setPrefSize(90, 90);
		ProgressBar progressBarServer = new ProgressBar();
		progressBarServer.setPrefSize(350, 30);
		ProgressIndicator progressIndicatorServer = new ProgressIndicator();
		progressIndicatorServer.setPrefSize(90, 90);

		stage.setTitle("BIG SHARE");

		ClientClass client = new ClientClass();
		ServerClass server = new ServerClass();

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
		Scene receive = new Scene(rootReceive, 580, 440);
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
			loop1.setPriority(2);
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
			loop2.setPriority(2);
			loop2.start();

			new Thread(() -> server.activate(), "activate").start();

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
		labelPath.setFont(new Font(14));
		labelPath.setPrefSize(520, 0);
		Button browseFile = new Button("Browse File to be sent", new ImageView("file.png"));
		browseFile.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		Button browseDir = new Button("Browse Directory to be sent", new ImageView("Directory.png"));
		browseDir.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		browseFile.setOnAction((ae) -> {
			FileChooser fileChoser = new FileChooser();
			File tempfile = fileChoser.showOpenDialog(stage);

			if (tempfile != null) {
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
				labelPath.setText("COMPRESSING " + sourceDirectory.getPath());
				client.compress(sourceDirectory);
				try {
					client.t.join();
				} catch (InterruptedException e) {
					System.out.println("Interrupted browseDir Waiting for Compress to Complete.");
				}
				labelPath.setText("COMPRESSING DONE " + client.file.getPath());
			}
		});

		Button btnSendNow = new Button("Send", new ImageView("Send1.png"));
		btnSendNow.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		btnSendNow.setOnAction((ae) -> {
			client.host = serverAddressTaker.getText();
			new Thread(() -> {
				client.send();
			}, "Client.send()").start();
		});

		Label labelServerAddress = new Label("Enter The Address of Server");
		labelServerAddress.setFont(new Font(15));

		rootSend.getChildren().addAll(labelServerAddress, serverAddressTaker, separatorSend, instruction, labelPath,
				browseFile, browseDir, btnSendNow, progressBarClient, progressIndicatorClient);

		// ------------receive-------------------------------------------

		Text info = null;
		Label labelDestinationPath = new Label();
		labelDestinationPath.setFont(new Font(15));
		labelDestinationPath.setPrefSize(580, 40);
		Label decompressing = new Label("Decompressing...");
		decompressing.setFont(new Font(15));
		decompressing.setPrefSize(580, 40);
		decompressing.setVisible(decompressflag);

		try {
			info = new Text("Local Address " + InetAddress.getLocalHost().getHostAddress().toString()
					+ " || Global Address " + getIp());
		} catch (Exception e) {
			e.printStackTrace();
		}
		info.setFont(new Font(20));

		Button btnDestPath = new Button("Browse Destination Directory", new ImageView("search.png"));
		btnDestPath.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		btnDestPath.setPrefWidth(340);
		btnDestPath.setOnAction((ae) -> {
			DirectoryChooser destinationDirectoryChoser = new DirectoryChooser();
			server.destpath = destinationDirectoryChoser.showDialog(stage);
			labelDestinationPath.setText(server.destpath.getPath());
		});

		Label sep = new Label(" ");
		sep.setPrefWidth(580);
		sep.setVisible(false);
		rootReceive.getChildren().addAll(info, separatorReceive, labelDestinationPath, decompressing, btnDestPath, sep,
				progressBarServer, progressIndicatorServer);

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
}
