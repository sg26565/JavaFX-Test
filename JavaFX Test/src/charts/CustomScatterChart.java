package charts;

import static java.lang.Math.max;
import static java.lang.Math.min;

import de.treichels.math.Matrix;
import de.treichels.math.PolynomalFunction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
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
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class CustomScatterChart extends ScatterChart<Number, Number> {
	private final MenuItem add = new MenuItem("Add Point");
	private final MenuItem del = new MenuItem("Delete Point");
	private final ContextMenu contextMenu = new ContextMenu(add, del);
	private final ObservableList<Data<Number, Number>> dataList = FXCollections.<Data<Number, Number>>observableArrayList();
	private final Tooltip tooltip = new Tooltip();
	private double clickedX;
	private double clickedY;
	private Node clickedSymbol;
	private PolynomalFunction[] functions;

	public CustomScatterChart(final NumberAxis xAxis, final NumberAxis yAxis) {
		super(xAxis, yAxis);

		setMinSize(500, 500);
		setPrefSize(500, 500);

		// add series
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
			dataList.removeIf(d -> ((Group) d.getNode()).getChildren().get(0) == clickedSymbol);
		});
	}

	public void addDataPoint(final double x, final double y) {
		addDataPoint(dataList.size(), x, y);
	}

	public void addDataPoint(final int index, final double x, final double y) {
		dataList.add(index, createDataPoint(x, y));
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

		dataPoint.setNode(new Group(circle));
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

		// create polynomals
		final int n = dataList.size();
		functions = new PolynomalFunction[n - 1];
		for (int i = 0; i < n - 1; i++) {
			// ax^3 + bx^2 + cx + d
			final PolynomalFunction f = new PolynomalFunction(3);
			functions[i] = f;

			// di = yi
			final double di = dataList.get(i).getYValue().doubleValue();
			f.getCoefficients()[3] = di;
		}

		// create coefficent matrix
		final int rows = n - 2;
		final int columns = n - 1;
		final Matrix matrix = new Matrix(rows, columns);

		// fill and solve matrix only for 3 point or more
		if (rows > 0) {
			for (int i = 1; i < n - 1; i++) {
				final double[] row = matrix.getData()[i - 1];

				final Data<Number, Number> dataim1 = dataList.get(i - 1);
				final Data<Number, Number> datai = dataList.get(i);
				final Data<Number, Number> dataip1 = dataList.get(i + 1);

				final double xim1 = dataim1.getXValue().doubleValue();
				final double xi = datai.getXValue().doubleValue();
				final double xip1 = dataip1.getXValue().doubleValue();

				final double yim1 = dataim1.getYValue().doubleValue();
				final double yi = datai.getYValue().doubleValue();
				final double yip1 = dataip1.getYValue().doubleValue();

				if (i > 1) {
					row[i - 2] = xi - xim1;
				}

				row[i - 1] = 2 * (xip1 - xim1);

				if (i < n - 2) {
					row[i] = xip1 - xi;
				}

				row[columns - 1] = 3 * ((yip1 - yi) / (xip1 - xi) - (yi - yim1) / (xi - xim1));
			}

			// System.out.println(matrix);
			matrix.solve();
			// System.out.println(matrix);
		}

		// get bi from solved matrix
		for (int i = 0; i < n - 1; i++) {
			final PolynomalFunction f = functions[i];

			final double bi = i == 0 ? 0 : matrix.get(i - 1, columns - 1);

			f.getCoefficients()[1] = bi;
		}

		// calculate ai and ci
		for (int i = 0; i < n - 1; i++) {
			final Data<Number, Number> datai = dataList.get(i);
			final Data<Number, Number> dataip1 = dataList.get(i + 1);

			final double xi = datai.getXValue().doubleValue();
			final double xip1 = dataip1.getXValue().doubleValue();

			final double yi = datai.getYValue().doubleValue();
			final double yip1 = dataip1.getYValue().doubleValue();

			final PolynomalFunction fi = functions[i];

			final double bi = fi.getCoefficients()[1];
			final double bip1;
			if (i < n - 2) {
				final PolynomalFunction fip1 = functions[i + 1];
				bip1 = fip1.getCoefficients()[1];
			} else {
				bip1 = 0;
			}

			final double ai = (bip1 - bi) / 3 / (xip1 - xi);
			final double ci = (yip1 - yi) / (xip1 - xi) - (bip1 - bi) * (xip1 - xi) / 3 - bi * (xip1 - xi);
			fi.getCoefficients()[0] = ai;
			fi.getCoefficients()[2] = ci;
		}

		// for (int i = 0; i < n - 1; i++) {
		// System.out.printf("f[%d]=%s\n", i, functions[i]);
		// }

		// update symbol positions
		Number x0 = null;
		double fromX = 0;
		double fromY = 0;

		for (int i = 0; i < dataList.size(); i++) {
			final Data<Number, Number> dataPoint = dataList.get(i);

			final ObservableList<Node> nodes = ((Group) dataPoint.getNode()).getChildren();
			nodes.remove(1, nodes.size());
			final Circle circle = (Circle) nodes.get(0);

			final Number x1 = dataPoint.getXValue();
			final Number y1 = dataPoint.getYValue();
			final double toX = getDisplayPosition(x1, xAxis);
			final double toY = getDisplayPosition(y1, yAxis);
			final double radius = 2 + min(xAxis.getWidth(), yAxis.getHeight()) / 200;

			// update circle
			circle.setRadius(radius);
			circle.setCenterX(toX);
			circle.setCenterY(toY);
			circle.toFront();

			if (i > 0) {
				// draw line
				final Line line = new Line();
				line.setStartX(fromX);
				line.setStartY(fromY);
				line.setEndX(toX);
				line.setEndY(toY);
				line.setStroke(Color.LIGHTGREY);
				line.getStrokeDashArray().addAll(10d, 8d, 2d, 8d);
				line.toBack();
				nodes.add(line);

				// draw spline
				final PolynomalFunction f = functions[i - 1];
				final Path path = new Path();
				final int range = x1.intValue() - x0.intValue();
				for (int x = 0; x <= range; x++) {
					final double y = f.evaluate(x);
					final double toX1 = getDisplayPosition(x + x0.intValue(), xAxis);
					final double toY1 = getDisplayPosition(y, yAxis);

					if (x == 0) {
						path.getElements().add(new MoveTo(toX1, toY1));
					} else {
						path.getElements().add(new LineTo(toX1, toY1));
					}
				}
				path.setStroke(Color.BLUE);
				path.toBack();
				nodes.add(path);
			}

			// save current values for next loop
			x0 = x1;
			fromX = toX;
			fromY = toY;
		}
	}
}
