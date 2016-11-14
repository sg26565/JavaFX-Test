package charts;

import static java.lang.Math.max;
import static java.lang.Math.min;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CustomScatterChart extends ScatterChart<Number, Number> {
	private final MenuItem add = new MenuItem("Add Point");
	private final MenuItem del = new MenuItem("Delete Point");
	private final ContextMenu contextMenu1 = new ContextMenu(add);
	private final ContextMenu contextMenu2 = new ContextMenu(del);
	private final ObservableList<Data<Number, Number>> dataList = FXCollections.<Data<Number, Number>>observableArrayList();
	private double clickedX;
	private double clickedY;
	private Node clickedSymbol;

	public CustomScatterChart(final NumberAxis xAxis, final NumberAxis yAxis) {
		super(xAxis, yAxis);

		// add series
		getData().add(new Series<>("Data Points", dataList));
		getData().add(new Series<>("Curve", FXCollections.<Data<Number, Number>>observableArrayList()));

		/*** Event Handler ***/
		// show add context menu and save clicked coordinates for later use
		addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				clickedX = getDataValue(e.getX() - yAxis.getWidth() - getInsets().getLeft() - getPadding().getLeft() - 6, xAxis);
				clickedY = getDataValue(e.getY() - getInsets().getTop() - getPadding().getTop(), yAxis);
				contextMenu1.show(this, e.getScreenX(), e.getScreenY());
				e.consume();
			}
		});

		// add action - add new data point on last clicked coordinates
		add.setOnAction(e -> {
			final int index = dataList.stream().filter(d -> d.getXValue().doubleValue() > clickedX).findFirst().map(d -> dataList.indexOf(d)).orElse(0);
			dataList.add(index, createDatePoint(clickedX, clickedY));
		});

		// delete action - remove data point of last clicked circle
		del.setOnAction(e -> dataList.removeIf(d -> d.getNode() == clickedSymbol));
	}

	public void addDataPoint(final double x, final double y) {
		dataList.add(createDatePoint(x, y));
	}

	private Data<Number, Number> createDatePoint(final double x, final double y) {
		// create data point
		final Data<Number, Number> data = new Data<>(x, y);

		// create symbol
		final Circle circle = new Circle();
		circle.setFill(Color.TRANSPARENT);
		circle.setStroke(Color.RED);

		/*** Event Handler ***/
		// show delete context menu and save clicked symbol
		circle.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				final NumberAxis xAxis = (NumberAxis) getXAxis();
				// disable menu item for first and last data point
				del.setDisable(data.getXValue().doubleValue() == xAxis.getLowerBound() || data.getXValue().doubleValue() == xAxis.getUpperBound());
				clickedSymbol = (Node) e.getSource();
				contextMenu2.show(circle, e.getScreenX(), e.getScreenY());
				e.consume();
			}
		});

		// turn circle orange when hovering over it
		circle.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> circle.setFill(Color.ORANGE));

		// turn circle back to transparent when exiting
		circle.addEventHandler(MouseEvent.MOUSE_EXITED, e -> circle.setFill(Color.TRANSPARENT));

		// update data value & round to integers while dragging
		circle.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
			final NumberAxis xAxis = (NumberAxis) getXAxis();
			final NumberAxis yAxis = (NumberAxis) getYAxis();
			final int maxIndex = dataList.size() - 1;
			final int index = dataList.indexOf(data);
			double newX = getDataValue(e.getX(), xAxis);
			final double newY = getDataValue(e.getY(), yAxis);

			if (index == 0) {
				// first data point is fixed to left side
				newX = xAxis.getLowerBound();
			} else if (index == maxIndex) {
				// last data point is fixed to right side
				newX = xAxis.getUpperBound();
			} else {
				// preserve order - right of previous data point
				final double prevX = dataList.get(index - 1).getXValue().doubleValue();
				if (newX <= prevX) {
					newX = prevX + 1;
				}

				// preserve order - left of next data point
				final double nextX = dataList.get(index + 1).getXValue().doubleValue();
				if (newX >= nextX) {
					newX = nextX - 1;
				}
			}

			data.setXValue(newX);
			data.setYValue(newY);
		});

		data.setNode(circle);
		return data;
	}

	private double getDataValue(final double value, final NumberAxis axis) {
		return min(axis.getUpperBound(), max(axis.getLowerBound(), axis.getValueForDisplay(value).intValue()));
	}

	private double getDisplayPosition(final Number number, final NumberAxis axis) {
		return axis.getDisplayPosition(number);
	}

	@Override
	protected void layoutPlotChildren() {
		final NumberAxis xAxis = (NumberAxis) getXAxis();
		final NumberAxis yAxis = (NumberAxis) getYAxis();

		// update symbol positions
		for (final Series<Number, Number> series : getData()) {
			for (final Data<Number, Number> data : series.getData()) {
				final Node node = data.getNode();

				if (node instanceof Circle) {
					final Circle circle = (Circle) node;

					circle.setRadius(2 + min(xAxis.getWidth(), yAxis.getHeight()) / 200);
					circle.setCenterX(getDisplayPosition(data.getXValue(), xAxis));
					circle.setCenterY(getDisplayPosition(data.getYValue(), yAxis));
				}
			}
		}
	}
}
