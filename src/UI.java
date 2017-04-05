import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import javafx.application.Application;
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
	ProgressBar progressBarServer = null;
	ProgressIndicator progressIndicatorServer = null;

	@Override
	public void start(Stage stage) {
		ProgressBar progressBarClient = new ProgressBar();
		ProgressIndicator progressIndicatorClient = new ProgressIndicator();
		progressBarServer = new ProgressBar();
		progressIndicatorServer = new ProgressIndicator();
		stage.setTitle("BIG SHARE");
		ClientClass client = new ClientClass();
		ServerClass server = new ServerClass();

		Scene home;
		FlowPane rootHome = new FlowPane(10, 10);
		rootHome.setAlignment(Pos.CENTER);
		home = new Scene(rootHome, 555, 270);
		stage.setScene(home);
		stage.show();

		FlowPane rootSend = new FlowPane(10, 10);
		rootSend.setAlignment(Pos.TOP_CENTER);
		Scene send = new Scene(rootSend, 400, 200);

		FlowPane rootReceive = new FlowPane();
		rootReceive.setAlignment(Pos.TOP_CENTER);
		Scene receive = new Scene(rootReceive, 600, 400);
		InnerShadow innerShadow = new InnerShadow(8.0, Color.AQUA);
		Separator separatorSend = new Separator();
		separatorSend.setPrefWidth(400);
		Separator separatorReceive = new Separator();
		separatorReceive.setPrefWidth(600);
		// -----------------home------------------------

		Button btnSend = new Button("Send Mode", new ImageView("send.png"));
		btnSend.setEffect(innerShadow);
		btnSend.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		btnSend.setOnAction((ae) -> {
			stage.setScene(send);
			stage.show();
		});

		Button btnReceive = new Button("Receive Mode", new ImageView("receive.png"));
		btnReceive.setEffect(innerShadow);
		btnReceive.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		btnReceive.setOnAction((ae) -> {
			stage.setScene(receive);
			stage.show();
			server.activate(progressBarServer, progressIndicatorServer);
		});

		rootHome.getChildren().addAll(btnSend, btnReceive);

		// ------------send-----------
		TextField serverAddressTaker = new TextField("192.168.0.100");

		serverAddressTaker.setPrefSize(150, 0);

		Text instruction = new Text("Either Chose file OR directory");
		instruction.setFont(new Font(25));
		Label labelPath = new Label();
		Button browseDir = new Button("Browse Directory to be sent");
		Button browseFile = new Button("Browse File to be sent");
		browseFile.setOnAction((ae) -> {
			FileChooser fileChoser = new FileChooser();
			File tempfile = fileChoser.showOpenDialog(stage);

			if (tempfile != null) {
				labelPath.setText(tempfile.getAbsolutePath());
				client.file = tempfile;
				client.filename = tempfile.getName();
				client.fileLength = tempfile.length();
				browseDir.setDisable(true);
			}
		});

		browseDir.setOnAction((ae) -> {
			DirectoryChooser directorychoser = new DirectoryChooser();
			File tempfile = directorychoser.showDialog(stage);

			if (tempfile != null) {
				labelPath.setText("COMPRESSING " + tempfile.getPath());
				Compress.zip(tempfile.getPath(), tempfile.getPath() + ".zip", "");
				labelPath.setText("COMPRESSING DONE");
				tempfile = new File(tempfile.getPath() + ".zip");
				client.file = tempfile;
				client.filename = tempfile.getName();
				client.fileLength = tempfile.length();
				browseFile.setDisable(true);
			}
		});

		Button btnSendNow = new Button("Send");
		btnSendNow.setOnAction((ae) -> {
			client.host = serverAddressTaker.getText();
			client.send(progressBarClient, progressIndicatorClient);
		});

		Label labelServerAddress = new Label("Enter The Address of Server");

		rootSend.getChildren().addAll(labelServerAddress, serverAddressTaker, instruction, labelPath, browseFile,
				browseDir, btnSendNow, progressBarClient, progressIndicatorClient);

		// ------------receive-------------------------------------------

		Text info = null;
		Label labelDestinationPath = new Label();
		try {
			info = new Text("Local Address " + InetAddress.getLocalHost().getHostAddress().toString()
					+ "|| External Address " + server.getIp());
		} catch (Exception e) {
			e.printStackTrace();
		}
		info.setFont(new Font(20));

		Button btnDestPath = new Button("Browse Destination Directory");
		btnDestPath.setOnAction((ae) -> {
			DirectoryChooser destinationDirectoryChoser = new DirectoryChooser();
			server.destpath = destinationDirectoryChoser.showDialog(stage);
			labelDestinationPath.setText(server.destpath.getPath());
		});

		rootReceive.getChildren().addAll(info, separatorReceive, labelDestinationPath, btnDestPath, progressBarServer,
				progressIndicatorServer);
		// -------------------------------------------------------------
		stage.setOnCloseRequest((ae) -> {
			if (server.socket != null)
				try {
					server.socket.close();
				} catch (IOException e) {
					System.out.println("Error while closing socket");
				}
			if (server.serverSocket != null)
				try {
					server.serverSocket.close();
				} catch (IOException e) {
					System.out.println("Error while closing ServerSocket");
				}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
