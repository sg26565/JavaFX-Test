package binding;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Binding {
	private static class Data {
		private String foo;
		private String bar;
	}

	public static void main(final String[] args) {
		final Data d = new Data();
		final StringProperty p1 = new SimpleStringProperty(d, "foo", "initial foo");
		p1.addListener((p, o, n) -> System.out.printf("p1: %s -> %s\n", o, n));

		final StringProperty p2 = new SimpleStringProperty(d, "bar", "initial bar");
		p2.addListener((p, o, n) -> System.out.printf("p2: %s -> %s\n", o, n));

		System.out.printf("p1: \"%s\", p2: \"%s\"\n", p1, p2);

		Bindings.bindBidirectional(p1, p2);

		System.out.printf("p1: \"%s\", p2: \"%s\"\n", p1, p2);

		p1.set("foo");

		System.out.printf("p1: \"%s\", p2: \"%s\"\n", p1, p2);

		p2.set("bar");

		System.out.printf("p1: \"%s\", p2: \"%s\"\n", p1, p2);
	}
}
