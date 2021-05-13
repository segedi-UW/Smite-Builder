import java.util.Hashtable;
import java.lang.StringBuilder;
public class Item implements Comparable<Item> {

	public static final Hashtable<String, Stat> STAT_REGEX = createRegexTable();
	public static final Hashtable<String, Stat> STAT_ALIAS_REGEX = createAliasRegexTable();

	public final Hashtable<Stat, Integer> STATS;
	public final String FULL_ITEM;
	public final String NAME;
	public final int COST;
	public final String PASSIVE;
	public final boolean IS_STARTER;

	public enum Stat {
		HEALTH("health", "Health", "Health"), MANA("mana", "Mana", "Mana"), PENETRATION_PHYSICAL("penetrationP", "Physical Penetration", "Pen Phys"), PENETRATION_MAGICAL("penetrationM", "Magical Penetration", "Pen Mag"),
		PENETRATION_PHYSICAL_PERCENT("penetrationP%", "Physical Penetration (%)", "%Pen Phys"), PENETRATION_MAGICAL_PERCENT("penetrationM%", "Magical Penetration (%)", "%Pen Mag"),
		POWER_PHYSICAL("powerP", "Physical Power", "Pow Phys"), POWER_MAGICAL("powerM", "Magical Power", "Pow Mag"), SPEED("speed", "Movement Speed (%)", "Move Spd"), ATTACK_SPEED("attackSpeed", "Attack Speed (%)", "Atk Spd"), BASIC_DAMAGE("basic", "Basic Attack Damage", "Basic Dmg"), 
		MITIGATION("mitigation", "Mitigation", "Mitigation"), COOLDOWN_REDUCTION("cdr", "Cooldown Reduction", "CDR"), CROWD_CONTROL_REDUCTION("ccr", "Crowd Control Reduction", "CCR"), CRITICAL_CHANCE("crit", "Critical Strike Chance", "Crit"),
		LIFESTEAL_PHYSICAL("lifestealP", "Physical Lifesteal", "Lifesteal Phys"), LIFESTEAL_MAGICAL("lifestealM", "Magical Lifesteal", "Lifesteal Mag"), HP5("hp5", "Hp5", "Hp5"), MP5("mp5", "Mp5", "Mp5"),
		PROTECTION_PHYSICAL("protectionP", "Physical Protection", "Prot Phys"), PROTECTION_MAGICAL("protectionM", "Magical Protection", "Prot Mag"); 


		public final String REGEX;
		public final String NAME;
		public final String ALIAS;

		private Stat(String regex, String name, String alias) {
			REGEX = regex;
			NAME = name;
			ALIAS = alias;
		}
	}

	public static Item passiveCopy(Item item, String passive) {
		return new Item(item, passive);
	}

	public static Item starterCopy(Item item) {
		return new Item(item, true);
	}

	public Item(String name, int cost, String passive) {
		NAME = name;
		COST = cost;
		STATS = new Hashtable<>(8);
		PASSIVE = passive == null ? "" : passive;
		IS_STARTER = false;
		FULL_ITEM = createFullString();
	}

	public Item(String name, int cost) {
		this(name, cost, "");
	}

	private Item(Item item, String passive) {
		NAME = item.NAME;
		COST = item.COST;
		STATS = item.STATS;
		IS_STARTER = item.IS_STARTER;
		PASSIVE = passive == null ? "" : passive;
		FULL_ITEM = createFullString();
	}

	private Item(Item item, boolean isStarter) {
		NAME = item.NAME;
		COST = item.COST;
		STATS = item.STATS;
		PASSIVE = item.PASSIVE;
		IS_STARTER = isStarter;
		FULL_ITEM = createFullString();
	}

	private static Hashtable<String, Stat> createRegexTable() {
		final Stat[] stats = Stat.values();

		final int HASHTABLE_SIZE = (stats.length * 2) + 1;
		Hashtable<String, Stat> hashtable = new Hashtable<>(HASHTABLE_SIZE);

		for (int i = 0; i < stats.length; i++) {
			Stat stat = stats[i];
			hashtable.put(stat.REGEX, stat);
		}

		return hashtable;
	}

	private static Hashtable<String, Stat> createAliasRegexTable() {
		final Stat[] stats = Stat.values();

		final int HASHTABLE_SIZE = (stats.length * 2) + 1;
		Hashtable<String, Stat> hashtable = new Hashtable<>(HASHTABLE_SIZE);

		for (int i = 0; i < stats.length; i++) {
			Stat stat = stats[i];
			hashtable.put(stat.ALIAS, stat);
		}

		return hashtable;
	}

	private String createFullString() {
		final String START = NAME + " (" + COST + " G)    ";
		final int WRAP = 65;

		StringBuilder builder = new StringBuilder();

		builder.append(START);

		Stat[] statValues = Stat.values();

		boolean first = true;

		for (int i = 0; i < statValues.length; i++) {
			Integer value = STATS.get(statValues[i]);
			if (value != null) {
				if (!first)
					builder.append(", ");
				builder.append(statValues[i].NAME + ": " + value);
				first = false;
			}
		}

		builder.append("\n\t" + wrapAt(PASSIVE, WRAP) + "\n");

		return builder.toString();
	}

	@Override
	public String toString() {
		return NAME + " (" + COST + " G)";
	}

	private String wrapAt(String text, int newLineIndex) {
		final int INCREMENT = newLineIndex;
		StringBuilder builder = new StringBuilder(text);
		while (newLineIndex < builder.length()) {
			int nextSpace = getNextSpaceIndex(builder.toString(), newLineIndex);
			if (nextSpace != -1)
				builder.insert(nextSpace, "\n\t");
			newLineIndex += INCREMENT;
		}
		return builder.toString();
	}

	private int getNextSpaceIndex(String text, int startIndex) {
		for (int i = startIndex; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == ' ') {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int compareTo(Item item) {
		if (COST > item.COST)
			return 1;
		else if (COST < item.COST)
			return -1;
		return 0;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Item) {
			Item item = (Item) object;
			if (NAME.equals(item.NAME))
				return true;
		}
		return false;
	}
}
