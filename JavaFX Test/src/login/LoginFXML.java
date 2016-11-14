package login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginFXML extends Application {
	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {
		stage.setTitle("FXML Welcome");
		stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("login.fxml"))));
		stage.show();
	}
}
