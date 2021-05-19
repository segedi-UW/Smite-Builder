import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Hashtable;
import javafx.scene.image.Image;

public class ImageParser {

	public static String nameToResourceName(String name, String extension) {
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

	public static String itemToResourceName(Item item, String extension) {
		String name = item.NAME;
		return nameToResourceName(name, extension);
	}

	public Hashtable<Item, Image> parseResourcesForItemjpg(Collection<Item> items) {
		final int SIZE = items.size() * 2 + 1;


		Hashtable<Item, Image> table = new Hashtable<>(SIZE);

		for (Item item : items) {
			Image image = tryParseImage(item);
			table.put(item, image);
		}

		return table;
	}

	private Image tryParseImage(Item item) {
		try {
			return parseImage(item);
		} catch (IllegalArgumentException e) {
			System.out.println("Could not load image for " + item + ": " + e.getMessage());
		} catch (NullPointerException e) {
			System.out.println("Could not load image for " + item + ": " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Failed to close stream. " + e.getMessage());
		}
		throw new IllegalArgumentException("Error loading images");
	}

	private Image parseImage(Item item) throws IOException {
		final double WIDTH = 126.0;
		final double HEIGHT = 126.0;
		final boolean PRESERVE = true;
		final boolean SMOOTH = true;
		final String EXTENSION = "jpg";
		
		final String filename = itemToResourceName(item, EXTENSION);
		InputStream stream = getClass().getClassLoader().getResourceAsStream(filename);
		Image image = new Image(stream, WIDTH, HEIGHT, PRESERVE, SMOOTH);
		
		stream.close();
		return image;
	}
}
