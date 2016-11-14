package charts;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CustomChartApp extends Application {
	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {
		final Scene scene = new Scene(new CustomChart(), 800, 600);

		stage.setTitle("Custom Chart Sample");
		stage.setScene(scene);
		stage.show();

	}
}
