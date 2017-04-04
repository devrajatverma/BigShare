import java.io.File;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
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

	@Override
	public void start(Stage stage) {
		ProgressBar progressBar = new ProgressBar();
		ProgressIndicator progressIndicator = new ProgressIndicator();
		stage.setTitle("BIG SHARE");
		ClientClass client = new ClientClass();

		Scene home;
		FlowPane rootHome = new FlowPane(10, 10);
		rootHome.setAlignment(Pos.CENTER);
		home = new Scene(rootHome, 555, 270);

		FlowPane rootSend = new FlowPane(10, 10);
		rootSend.setAlignment(Pos.CENTER);
		Scene send = new Scene(rootSend, 400, 400);

		FlowPane rootReceive = new FlowPane();
		rootReceive.setAlignment(Pos.CENTER);
		Scene receive = new Scene(rootReceive, 400, 400);
		InnerShadow innerShadow = new InnerShadow(8.0, Color.AQUA);
		// -----------------home------------------------

		Button btnSend = new Button("Send Mode", new ImageView("send.png"));
		btnSend.setEffect(innerShadow);
		btnSend.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		rootHome.getChildren().addAll(btnSend);

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
		});
		rootHome.getChildren().add(btnReceive);

		// ------------send-----------
		Text instruction = new Text("Either Chose file OR directory");
		instruction.setFont(new Font(25));
		Label labelPath = new Label();
		Button browseDir = new Button("Browse Directory to be sent");
		Button browseFile = new Button("Browse File to be sent");
		browseFile.setOnAction((ae) -> {
			FileChooser fileChooser = new FileChooser();
			client.file = fileChooser.showOpenDialog(stage);
			labelPath.setText(client.file.getPath());
			browseDir.setDisable(true);
		});

		browseDir.setOnAction((ae) -> {
			DirectoryChooser directorychoser = new DirectoryChooser();

			File tempfile = directorychoser.showDialog(stage);
			labelPath.setText("COMPRESSING " + tempfile.getPath());
			Compress.zip(tempfile.getPath(), tempfile.getPath() + ".zip", "");
			labelPath.setText("COMPRESSING DONE");
			tempfile = new File(tempfile.getPath() + ".zip");
			client.file = tempfile;
			System.out.println(client.file.getPath());
			client.filename = tempfile.getName();
			System.out.println(client.filename);
			client.fileLength = tempfile.length();
			System.out.println(client.fileLength);

			browseFile.setDisable(true);
		});

		Button btnSendNow = new Button("Send");
		btnSendNow.setOnAction((ae) -> {
			client.send(progressBar, progressIndicator);
		});

		Separator separator = new Separator();
		separator.autosize();

		rootSend.getChildren().addAll(instruction, labelPath, separator, browseFile, browseDir, btnSendNow, progressBar,
				progressIndicator);
		// ------------receive---------
		stage.setScene(home);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
