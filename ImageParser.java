import java.util.Collection;
import javafx.scene.image.Image;
import java.io.File;
import java.util.Hashtable;

public class ImageParser {

	public static String nameToFilename(String name, String extension) {
		final String SPACE = " ";
		final String DASH = "-";
		final String EMPTY_STRING = "";
		final String APOSTROPHE = "\'";
		final String QUOTE = "\"";

		name = name.toLowerCase();
		name = name.replace(SPACE, DASH);
		name = name.replace(QUOTE, EMPTY_STRING);
		name = name.replace(APOSTROPHE, EMPTY_STRING);
		name += "." + extension;
		return name;
	}

	public static String itemToFilename(Item item, String extension) {
		String name = item.NAME;
		return nameToFilename(name, extension);
	}

	public Hashtable<Item, Image> parseResourcesForItemjpg(Collection<Item> items) {
		final int SIZE = items.size() * 2 + 1;
		final double WIDTH = 126.0;
		final double HEIGHT = 126.0;
		final boolean PRESERVE = true;
		final boolean SMOOTH = true;
		final String EXTENSION = "jpg";

		Hashtable<Item, Image> table = new Hashtable<>(SIZE);

		for (Item item : items) {
			Image image = tryParseImage(item);
			table.put(item, image);
		}

		return table;
	}

	private Image tryParseImage(Item item) {
		try {
			parseImage(item);
		} catch (IllegalArgumentException e) {
			System.out.println("Could not load image for " + filename + ": " + e.getMessage());
		} catch (NullPointerException e) {
			System.out.println("Could not load image for " + filename + ": " + e.getMessage());
		}

	}

	private Image parseImage(Item item) {
		String filename = itemToFilename(item, EXTENSION);
		ImputStream stream = getClass().getResourceAsStream(filename);
		return new Image(stream, WIDTH, HEIGHT, PRESERVE, SMOOTH);
	}
}
