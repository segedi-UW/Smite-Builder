import javafx.scene.image.Image;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.util.Hashtable;
import java.util.LinkedList;


public class ItemUtil{

	private static final String FILENAME = "finalItems.csv";
	private static final ObservableList<Item> ITEMS = createObservableList();
	private static final Hashtable<String, Item> ITEM_LOOKUP = createItemLookup();
	private static final Hashtable<Item, Image> IMAGE_LOOKUP = createImageLookup();

	private static ObservableList<Item> createObservableList() {
		LinkedList<Item> list = ItemParser.parseCsvToList(FILENAME);
		return FXCollections.observableList(list);
	}

	private static Hashtable<String, Item> createItemLookup() {
		final int SIZE = ITEMS.size() * 2 + 1;
		Hashtable<String, Item> table = new Hashtable<>(SIZE);

		for (Item item : ITEMS) {
			table.put(item.NAME, item);
		}

		return table;
	}

	private static Hashtable<Item, Image> createImageLookup() {
		ImageParser parser = new ImageParser();
		return parser.parseResourcesForItemjpg(ITEMS);
	}

	private ItemUtil() {}

	public static ObservableList<Item> getObservableItemList() {
		return ITEMS;
	}

	public static Item lookupItem(String name) {
		return ITEM_LOOKUP.get(name);
	}

	public static Image lookupImage(Item item) {
		return IMAGE_LOOKUP.get(item);
	}
}
