package charts;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.stage.Stage;

public class CustomScatterChartApp extends Application {
	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {
		final NumberAxis xAxis = new NumberAxis("X Axis", -100, 100, 10);
		final NumberAxis yAxis = new NumberAxis("Y Axis", -100, 100, 10);
		final CustomScatterChart chart = new CustomScatterChart(xAxis, yAxis);

		chart.addDataPoint(xAxis.getLowerBound(), yAxis.getLowerBound());
		chart.addDataPoint(0, 0);
		chart.addDataPoint(xAxis.getUpperBound(), yAxis.getUpperBound());

		chart.setLegendVisible(false);

		final Scene scene = new Scene(chart, 800, 600);

		stage.setTitle("Custom Scatter Chart Sample");
		stage.setScene(scene);
		stage.show();
	}
}
