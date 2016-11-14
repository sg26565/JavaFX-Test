package login;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Login extends Application {
	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {
		final GridPane root = new GridPane();
		root.setAlignment(Pos.CENTER);
		root.setHgap(10);
		root.setVgap(10);
		root.setPadding(new Insets(25, 25, 10, 25));

		final Text scenetitle = new Text("Welcome");
		scenetitle.setId("welcome-text");
		root.add(scenetitle, 0, 0, 2, 1);

		final Label userName = new Label("User Name:");
		root.add(userName, 0, 1);

		final TextField userTextField = new TextField();
		root.add(userTextField, 1, 1);

		final Label pw = new Label("Password:");
		root.add(pw, 0, 2);

		final PasswordField pwBox = new PasswordField();
		root.add(pwBox, 1, 2);

		final Button btn = new Button("Sign in");
		final HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		root.add(hbBtn, 1, 4);

		final Text msg = new Text();
		msg.setId("message");
		root.add(msg, 0, 6, 2, 1);

		btn.setOnAction(e -> {
			msg.setText("Sign in button pressed");
		});

		final Scene scene = new Scene(root);

		stage.setTitle("JavaFX Welcome");
		stage.setScene(scene);
		scene.getStylesheets().add(Login.class.getResource("login.css").toExternalForm());
		stage.show();
	}
}
