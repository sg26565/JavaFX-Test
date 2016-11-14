package charts;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class LineChartSample extends Application {
	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {
		stage.setTitle("Line Chart Sample");

		// defining the axes
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Number of Month");

		// creating the chart
		final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

		lineChart.setTitle("Stock Monitoring, 2010");

		// defining a series
		final Series<Number, Number> series = new Series<>();
		series.setName("My portfolio");

		// populating the series with data
		series.getData().add(new Data<>(1, 23));
		series.getData().add(new Data<>(2, 14));
		series.getData().add(new Data<>(3, 15));
		series.getData().add(new Data<>(4, 24));
		series.getData().add(new Data<>(5, 34));
		series.getData().add(new Data<>(6, 36));
		series.getData().add(new Data<>(7, 22));
		series.getData().add(new Data<>(8, 45));
		series.getData().add(new Data<>(9, 43));
		series.getData().add(new Data<>(10, 17));
		series.getData().add(new Data<>(11, 29));
		series.getData().add(new Data<>(12, 25));

		final Scene scene = new Scene(lineChart, 800, 600);
		lineChart.getData().add(series);

		for (final Data<Number, Number> data : series.getData()) {
			final Node node = data.getNode();
			if (node != null) {
				node.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> System.out.println(event));
			}
		}

		stage.setScene(scene);
		stage.show();
	}
}
