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
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class CustomScatterChart extends ScatterChart<Number, Number> {
	private final MenuItem add = new MenuItem("Add Point");
	private final MenuItem del = new MenuItem("Delete Point");
	private final ContextMenu contextMenu = new ContextMenu(add, del);
	private final ObservableList<Data<Number, Number>> dataList = FXCollections.<Data<Number, Number>>observableArrayList();
	private final ObservableList<Data<Number, Number>> curveList = FXCollections.<Data<Number, Number>>observableArrayList();
	private final Tooltip tooltip = new Tooltip();
	private double clickedX;
	private double clickedY;
	private Node clickedSymbol;

	public CustomScatterChart(final NumberAxis xAxis, final NumberAxis yAxis) {
		super(xAxis, yAxis);

		setMinSize(500, 500);
		setPrefSize(500, 500);

		// add series
		getData().add(new Series<>("Curve", curveList));
		getData().add(new Series<>("Data Points", dataList));

		/*** Event Handler ***/
		// show add context menu and save clicked coordinates for later use
		addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				clickedX = getDataValue(e.getX() - yAxis.getWidth() - getInsets().getLeft() - getPadding().getLeft() - 6, xAxis);
				clickedY = getDataValue(e.getY() - getInsets().getTop() - getPadding().getTop(), yAxis);
				add.setVisible(true);
				del.setVisible(false);
				contextMenu.show(this, e.getScreenX(), e.getScreenY());
				e.consume();
			}
		});

		// add action - add new data point on last clicked coordinates
		add.setOnAction(e -> {
			final int index = dataList.stream().filter(d -> d.getXValue().doubleValue() > clickedX).findFirst().map(d -> dataList.indexOf(d)).orElse(0);
			addDataPoint(index, clickedX, clickedY);
		});

		// delete action - remove data point of last clicked circle
		del.setOnAction(e -> {
			dataList.removeIf(d -> d.getNode() == clickedSymbol);
			curveList.remove(0);
		});
	}

	public void addDataPoint(final double x, final double y) {
		addDataPoint(dataList.size(), x, y);
	}

	public void addDataPoint(final int index, final double x, final double y) {
		dataList.add(index, createDataPoint(x, y));
		final Data<Number, Number> curvePoint = new Data<>(x, y);
		curvePoint.setNode(new Line());
		curveList.add(curvePoint);
	}

	private Data<Number, Number> createDataPoint(final double x, final double y) {
		// create data point
		final Data<Number, Number> dataPoint = new Data<>(x, y);

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
				add.setVisible(false);
				del.setVisible(true);
				del.setDisable(dataPoint.getXValue().doubleValue() == xAxis.getLowerBound() || dataPoint.getXValue().doubleValue() == xAxis.getUpperBound());
				clickedSymbol = (Node) e.getSource();
				contextMenu.show(circle, e.getScreenX(), e.getScreenY());
				e.consume();
			}
		});

		// turn circle orange when hovering over it
		circle.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> circle.setFill(Color.ORANGE));

		// turn circle back to transparent when exiting
		circle.addEventHandler(MouseEvent.MOUSE_EXITED, e -> circle.setFill(Color.TRANSPARENT));

		// show tooltip
		circle.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				contextMenu.hide();
				tooltip.setText(String.format("%d, %d", dataPoint.getXValue().intValue(), dataPoint.getYValue().intValue()));
				tooltip.show(circle, e.getScreenX() + 10, e.getScreenY() + 10);
			}
		});

		// hide tooltip
		circle.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> tooltip.hide());

		// update data value & round to integers while dragging
		circle.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				final NumberAxis xAxis = (NumberAxis) getXAxis();
				final NumberAxis yAxis = (NumberAxis) getYAxis();
				final int maxIndex = dataList.size() - 1;
				final int index = dataList.indexOf(dataPoint);
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

				dataPoint.setXValue(newX);
				dataPoint.setYValue(newY);

				// update tooltip
				tooltip.setText(String.format("%d, %d", dataPoint.getXValue().intValue(), dataPoint.getYValue().intValue()));
				tooltip.setAnchorX(e.getScreenX() + 10);
				tooltip.setAnchorY(e.getScreenY() + 10);
			}
		});

		dataPoint.setNode(circle);
		return dataPoint;
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
		for (int i = 0; i < dataList.size(); i++) {
			final Data<Number, Number> dataPoint = dataList.get(i);

			final Circle circle = (Circle) dataPoint.getNode();
			final double toX = getDisplayPosition(dataPoint.getXValue(), xAxis);
			final double toY = getDisplayPosition(dataPoint.getYValue(), yAxis);
			final double radius = 2 + min(xAxis.getWidth(), yAxis.getHeight()) / 200;

			// update circle
			circle.setRadius(radius);
			circle.setCenterX(toX);
			circle.setCenterY(toY);
			circle.toFront();

			// create curve
			if (i > 0) {
				final Data<Number, Number> lastDataPoint = dataList.get(i - 1);
				final Data<Number, Number> curvePoint = curveList.get(i - 1);

				curvePoint.setXValue(dataPoint.getXValue());
				curvePoint.setYValue(dataPoint.getYValue());

				final Line line = (Line) curvePoint.getNode();
				line.setStartX(getDisplayPosition(lastDataPoint.getXValue(), xAxis));
				line.setStartY(getDisplayPosition(lastDataPoint.getYValue(), yAxis));
				line.setEndX(toX);
				line.setEndY(toY);
			}
		}
	}
}
