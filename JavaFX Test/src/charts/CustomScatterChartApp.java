package charts;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.BorderPane;
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

		// final TableView<Data<Number, Number>> tableView = new TableView<>();
		// final TableColumn<Data<Number, Number>, Number> xColumn = new
		// TableColumn<>("X Value");
		// final TableColumn<Data<Number, Number>, Number> yColumn = new
		// TableColumn<>("Y Value");
		// xColumn.setCellValueFactory(new PropertyValueFactory<>("XValue"));
		// yColumn.setCellValueFactory(new PropertyValueFactory<>("YValue"));
		// tableView.getColumns().add(xColumn);
		// tableView.getColumns().add(yColumn);
		// tableView.setItems(chart.getData().get(0).getData());
		// tableView.setMaxHeight(150);
		// tableView.setPadding(new Insets(10, 10, 10, 10));

		final BorderPane pane = new BorderPane(chart);
		// pane.setBottom(tableView);

		final Scene scene = new Scene(pane);

		stage.setTitle("Custom Scatter Chart Sample");
		stage.setScene(scene);
		stage.show();
	}
}
