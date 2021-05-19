import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.LinkedList;

public class ItemParser {
	
	private static final int HASHTABLE_SIZE = 150;

	private static int lineNumber;

	public static LinkedList<Item> parseCsvToList(String filename) {
		try {
			return forEachToList(filename);
		} catch (IOException e) {
			throw new IllegalArgumentException("Error parseing csv file: " + e.getMessage());
		}
	}

	private static LinkedList<Item> forEachToList(String filename) throws IOException {
		lineNumber = 1;

		LinkedList<Item> items = new LinkedList<>();
		
		InputStream stream = ItemParser.class.getClassLoader().getResourceAsStream(filename);
		InputStreamReader streamReader = new InputStreamReader(stream);
		BufferedReader reader = new BufferedReader(streamReader);

		reader.lines().forEach((line) -> {
			Item item = parseCsvLine(line);
			items.add(item);
			lineNumber++;
		});
				
		return items;
	}

	public static Hashtable<String, Item> parseCsvToHashtable(String filename) {
		Hashtable<String, Item> itemTable = new Hashtable<>(HASHTABLE_SIZE);

		LinkedList<Item> items = parseCsvToList(filename);
		for (Item item : items) {
			itemTable.put(item.NAME, item);
		}

		return itemTable;
	}

	private static Item parseCsvLine(String line) {
		String[] split = splitItems(line);

		final int NAME_INDEX = 0;
		final int COST_INDEX = 1;
		final int START_INDEX = 2;

		Item item = new Item(split[NAME_INDEX], parseInt(split[COST_INDEX].trim()));

		for (int i = START_INDEX; i < split.length; i++) {
			item = parseStat(item, split[i]);
		}

		return item;
	}

	public static String[] splitItems(String line) {
		final char COMMA_CHAR = ',';
		final char QUOTE_CHAR = '"';

		boolean splitting = true;
		LinkedList<String> list = new LinkedList<>();
		final int LENGTH = line.length();
		int afterLastComma = 0;

		for (int i = 0; i < LENGTH && splitting; i++) {
			if (!splitting)
				break;
			char c = line.charAt(i);
			switch (c) {
				case COMMA_CHAR:
					list.add(line.substring(afterLastComma, i));
					afterLastComma = i + 1;
					break;
				case QUOTE_CHAR:
					splitting = false;
					break;
				default:
					// Do nothing
					break;
			}
		}

		list.add(line.substring(afterLastComma, LENGTH)); // Add the last section

		String[] array = new String[list.size()];

		return list.toArray(array);
	}

	private static Item parseStat(Item item, String string) {
		final String STAT_NAME_VALUE_REGEX = "=";
		final String QUOTATION_MARK = "\"";

		final int NAME_INDEX = 0;
		final int NUMBER_INDEX = 1;

		String[] split = string.split(STAT_NAME_VALUE_REGEX);

		if (split.length != 2)
			throw new IllegalArgumentException("Failed to parse item from: \"" + string + " on line " + lineNumber);

		String name = split[NAME_INDEX].trim();
		String value = split[NUMBER_INDEX].trim();

		if (value.startsWith(QUOTATION_MARK)) {
			item = addPassive(item, value);
		} else if (name.equals("starter")) {
			item = addStarter(item);
		} else {
			addStat(item, name, value);
		}

		return item;

	}

	private static void addStat(Item item, String name, String value) {
		Hashtable<String, Item.Stat> regexTable = Item.STAT_REGEX;
		Hashtable<Item.Stat, Integer> stats = item.STATS;

		Item.Stat stat = regexTable.get(name);

		if (stat == null) 
			throw new IllegalArgumentException("regex table failed to match: \"" + name + "\" on line " + lineNumber);

		Integer number = parseInt(value);
		stats.put(stat, number);
	}

	private static Item addStarter(Item item) {
		return Item.starterCopy(item);
	}

	private static Item addPassive(Item item , String string) {
		final int LAST_CHARACTER = string.length() - 1;

		Item newItem = Item.passiveCopy(item, string.substring(1, LAST_CHARACTER));
		return newItem;
	}

	private static int parseInt(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Unsuccessful parse of \"" + string + "\" on line " + lineNumber);
		}
	}
}
