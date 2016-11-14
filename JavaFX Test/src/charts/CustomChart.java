package charts;

import static java.lang.Math.max;
import static java.lang.Math.min;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Chart;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CustomChart extends Chart {
	public class Data extends Circle {
		private int pointX;
		private int pointY;

		public Data(final int pointX, final int pointY) {
			setPointX(pointX);
			setPointY(pointY);
			setRadius(3.0d);
			setFill(Color.TRANSPARENT);
			setStroke(Color.RED);

			// turn circle orange when hovering over it
			addEventHandler(MouseEvent.MOUSE_ENTERED, e -> setStroke(Color.ORANGE));

			// turn circle back to red
			addEventHandler(MouseEvent.MOUSE_EXITED, e -> setStroke(Color.RED));

			// Update coordinates while dragging
			addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
				// move circle
				setCenterX(e.getX());
				setCenterY(e.getY());

				// translate to point coordinates
				setPointX(toPointX(e.getX()));
				setPointY(toPointY(e.getY()));
			});
		}

		public int getPointX() {
			return pointX;
		}

		public int getPointY() {
			return pointY;
		}

		public void setPointX(final int pointX) {
			// enforce limits
			this.pointX = min(max(pointX, -100), 100);
		}

		public void setPointY(final int pointY) {
			// enforce limits
			this.pointY = min(max(pointY, -100), 100);
		}
	}

	private double top;
	private double left;
	private double width;
	private double height;

	public CustomChart() {
		final ObservableList<Node> children = getChartChildren();

		children.add(new Data(-100, -100));
		children.add(new Data(0, 0));
		children.add(new Data(100, 100));
	}

	@Override
	protected void layoutChartChildren(final double top, final double left, final double width, final double height) {
		this.top = top;
		this.left = left;
		this.width = width;
		this.height = height;

		for (final Node node : getChartChildren()) {
			final Data d = (Data) node;
			// translate to scene coordinates
			d.setCenterX(toScreenX(d.getPointX()));
			d.setCenterY(toScreenY(d.getPointY()));
		}
	}

	private int toPointX(final double x) {
		return (int) ((x - left - width / 2.0d) * 200.0d / width);
	}

	private int toPointY(final double y) {
		return (int) ((y - top - height / 2.0d) * -200.0d / height);

	}

	private double toScreenX(final int x) {
		return left + width / 2.0d + width / 200.0d * x;
	}

	private double toScreenY(final int y) {
		return top + height / 2.0d - height / 200.0d * y;
	}
}
