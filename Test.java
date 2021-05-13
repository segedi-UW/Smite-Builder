import java.util.Arrays;
import javafx.scene.image.Image;
import java.io.InputStream;

public class Test {

	public static void main(String[] args) {
		Test test = new Test();
		test.testAll();
		System.out.println("Passed All Tests!");
	}

	public void testAll() {
		testSplitItems();
		testImageParser();
		testResourceLoad();
	}

	public void testSplitItems() {
		ItemParser parser = new ItemParser();
		final String test = "Hello, my name is, AJ, God, why so many commas...,\"the, mans\"";
		final String[] expected = {"Hello", " my name is", " AJ", " God", " why so many commas...", "\"the, mans\""};
		String[] actual = parser.splitItems(test);
		testArrayEquals(actual, expected);
	}

	public void testImageParser() {
		testNameToFilename();
	}

	public void testNameToFilename() {
		String[] names = {"soul eater", "void shield", "sentinel's embrace"};
		String[] expected = {"soul-eater.jpg", "void-shield.jpg", "sentinels-embrace.jpg"};
		String[] actual = new String[expected.length];

		ImageParser parser = new ImageParser();

		final String EXTENSION = "jpg";

		for (int i = 0; i < actual.length; i++) {
			actual[i] = parser.nameToFilename(names[i], EXTENSION);
		}

		testArrayEquals(actual, expected);
	}

	public void testResourceLoad() {
		final String EXTENSION = "jpg";
		String[] names = {"soul-eater.jpg", "void-shield.jpg", "sentinels-embrace.jpg"};
		for (int i = 0; i < names.length; i++) {
			String filename = names[i];
			InputStream stream = getClass().getResourceAsStream(filename);
			Image image = new Image(stream);
		}
	}

	private <T> void testArrayEquals(T[] actual, T[] expected) {
		if (!Arrays.equals(actual, expected))
			failExpectedActual(Arrays.toString(expected), Arrays.toString(actual));
	}
	
	private void fail(String message) {
		throw new IllegalStateException("Test failed: " + message);
	}

	private void failExpectedActual(String expected, String actual) {
		fail("Expected did not match actual.\nExpected: " + expected + "\nActual:   " + actual);
	}
}
