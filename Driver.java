import java.util.Hashtable;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Enumeration;
import java.lang.StringBuilder;

public class Driver {

	private final static Hashtable<String, Command> COMMAND_REGEX = createCommandRegex();

	private static Hashtable<String, Item> items;
	private static Hashtable<String, Item> activeItems;
	private static Hashtable<String, Item> filteredItems;
	private static Hashtable<Item.Stat, Item.Stat> filterStats;

	private static Scanner scan;

	private interface InputWorker {
		public void readInput(String input);
	}

	private static class Prompter {

		private boolean running;
		private InputWorker worker;
		private String exitString;
		private String prompt;

		public Prompter(InputWorker worker) {
			this.worker = worker;
			exitString = "q";
			prompt = "Enter input: ";
		}

		public Prompter() {
			this(null);
		}

		public void loopPrompt() {
			running = true;
			while(running) {
				prompt();
			} 
		}

		public void prompt() {
			System.out.print("Enter \"" + exitString + "\" to quit\n" + prompt);
			String input = scan.nextLine();
			System.out.println();

			if (input.equals(exitString))
				stop();
			else
				worker.readInput(input);
		}

		public void stop() {
			running = false;
		}

		public void setPrompt(String prompt) {
			this.prompt = prompt;
		}

		public void setInputWorker(InputWorker worker) {
			this.worker = worker;
		}

		public void setExitString(String exitString) {
			this.exitString = exitString;
		}
	}

	private enum Command {
		LIST("l"), LIST_FILTERED("lf"), SEARCH("s"), FILTER("f"), CLEAR_FILTER("cf"), HELP("h");

		public final String ALIAS;

		private Command(String alias) {
			ALIAS = alias;
		}
	}

	private static Hashtable<String, Command> createCommandRegex() {

		final Command[] list = Command.values();
		final int HASHTABLE_SIZE = list.length * 2;

		Hashtable<String, Command> table = new Hashtable<>(HASHTABLE_SIZE);

		for (int i = 0; i < list.length; i++) {
			Command command = list[i];
			table.put(command.ALIAS, command);
		}

		return table;
	}

	public static void main(String[] args) {
		initialize();
		start();
	}

	private static void initialize() {
		final String FILENAME = "finalItems.csv";

		ItemParser parser = new ItemParser();
		items = parser.parseCsv(FILENAME);

		activeItems = new Hashtable<>(items);
		filteredItems = new Hashtable<>(items);
		filterStats = new Hashtable<>(15);

		scan = new Scanner(System.in);
	}

	private static void start() {
		final String prompt = "Enter (l)ist, (f)ilter, list filtered (lf), clear filter (cf), (s)earch, (h)elp: " ;
		final String message = "Welcome to Smite Item Evaluator and Builder!";

		System.out.println(message);

		Prompter prompter = new Prompter((input) -> {
			readCommand(input);
		});
		prompter.setExitString("e");
		prompter.setPrompt(prompt);
		prompter.loopPrompt();
	}

	private static void readCommand(String input) {
		Command command = COMMAND_REGEX.get(input);
		if (command == null) {
			final String MESSAGE = "The command \"" + input + "\" does not exist. Use (h) for help";
			System.out.println(MESSAGE);
		} else {
			fireCommand(command);
		}
	}

	private static void fireCommand(Command command) {
		switch (command) {
			case LIST:
				printList(items, "Printing all items:");
				break;
			case LIST_FILTERED:
				printList(filteredItems, "Printing filtered items:\nSelected Stats: " + filterStats.values() + "\n");
				break;
			case CLEAR_FILTER:
				filterStats.clear();
				filterItems();
				break;
			case SEARCH:
				promptSearchForItem();
				break;
			case FILTER:
				promptFilterItems();
				break;
			case HELP:
				showHelp();
				break;
		}
	}

	private static void printList(Hashtable<String, Item> table, String message) {
		final String SEPARATOR = "*****************************";
		System.out.println(message + "\n" + SEPARATOR + "\n");
		list(table);
		System.out.println("\n" + SEPARATOR + "\n");
	}

	private static void list(Hashtable<String, Item> table) {
		StringBuilder builder = new StringBuilder();

		ArrayList<Item> items = new ArrayList<>(table.values());
		items.sort(null);
		for (Item item : items) {
			builder.append(item + "\n");
		}
		System.out.println(builder.toString());
	}

	private static void searchForItem(String input) {
		Item item = items.get(input);
		if (item == null)
			System.out.println("Item not found: " + input);
		else {
			final String SEPARATOR = "******************";
			System.out.println(SEPARATOR + "\n" + item + "\n" + SEPARATOR + "\n");
		}
	}

	private static void promptSearchForItem() {
		String prompt = "Enter name of item: ";
		Prompter prompter = new Prompter((input) -> {
			searchForItem(input);
		});
		prompter.setPrompt(prompt);
		prompter.loopPrompt();
	}

	private static void promptFilterItems() {

		String prompt = "Filter keys: " + keysToString(Item.STAT_ALIAS_REGEX) + "\nEnter key to filter by: ";
	
		Prompter prompter = new Prompter();
		prompter.setInputWorker((input) -> {
			editFilterKeys(input);
			prompter.setPrompt("Filter keys: " + keysToString(Item.STAT_ALIAS_REGEX) + "\nEnter key to filter by: ");
			filterItems();
		});
		prompter.setExitString("d");
		prompter.setPrompt(prompt);
		prompter.loopPrompt();
	}

	private static String keysToString(Hashtable<String, Item.Stat> table) {
		StringBuilder builder = new StringBuilder();
		Enumeration<String> keys = table.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			builder.append(key);
			boolean inTable = filterStats.containsKey(table.get(key)); 
			String contains = inTable ? "T" : "F";
			builder.append(" (" + contains + ")");
			builder.append(keys.hasMoreElements() ? ", " : "");
		}

		return builder.toString();
	}

	private static void editFilterKeys(String input) {
		Hashtable<String, Item.Stat> table = Item.STAT_ALIAS_REGEX;

		Item.Stat stat = table.get(input);
		if (stat == null)
			System.out.println("The alias \"" + input + "\" does not exist.");
		else {
			if (filterStats.contains(stat)) {
				filterStats.remove(stat);
				System.out.println("Removed " + stat + " to filter Keys.");
			} else {
				filterStats.put(stat, stat);
				System.out.println("Added " + stat + " to filter Keys.");
			}
		}
	}

	private static void filterItems() {
		filteredItems = new Hashtable<>(items);

		ArrayList<Item> items = new ArrayList<>(filteredItems.values());

		for (Item.Stat stat : filterStats.values()) {
			for (int i = 0; i < items.size(); i++) { // Need to keep rechecking size
				Item item = items.get(i);
				if (!item.STATS.containsKey(stat))
					filteredItems.remove(item.NAME);
			}
		}
	}

	private static void showHelp() {
		System.out.println("Sorry nothing helpful here yet...");
	}

}
