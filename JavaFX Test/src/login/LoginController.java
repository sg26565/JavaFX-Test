package login;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class LoginController {
	@FXML
	private PasswordField passwordField;

	@FXML
	private Text actiontarget;

	@FXML
	TextField usernameField;

	@FXML
	private void handleSubmitButtonAction() {
		actiontarget.setText(String.format("Username: %s, Password: %s", usernameField.getText(), passwordField.getText()));
	}
}
