import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class StatTable implements ListChangeListener<Item> {
	
	ObservableList<Item> items;
	Hashtable<Item.Stat, Integer> total;
	Hashtable<Item.Stat, Label> labels;
	private GridPane display;

	public StatTable(ObservableList<Item> builtItems) {
		this.items = builtItems;
		builtItems.addListener(this);
		total = new Hashtable<>();
		labels = new Hashtable<>();
		initializeTable();
	}
	
	private void initializeTable() {
		display = new GridPane();
		final double GAP = 15.0;
		Color color = Color.IVORY;
		BackgroundFill fill = new BackgroundFill(color, null, null);
		Background background = new Background(fill);
		display.setBackground(background);
		display.setHgap(GAP);
		int labelColumn = 0;
		int valueColumn = 1;
		boolean shifted = false;
		int row = 0;
		Item.Stat[] stats = Item.Stat.values();
		for (int i = 0; i < stats.length; i++) {
			Item.Stat stat = stats[i];

			if (i > stats.length / 2 && !shifted) {
				labelColumn = 2;
				valueColumn = 3;
				row = 0;
				shifted = true;
			}
			total.put(stat, 0);
			
			Label label = new Label(stat.NAME);
			Label value = new Label("0");
			labels.put(stat, value);
			
			display.add(label, labelColumn, row);
			display.add(value, valueColumn, row);
			row++;
		}
	}
	
	@Override
	public void onChanged(Change<? extends Item> arg0) {
		resetTable();
		calculateStats();
		updateTable();
	}
	
	public GridPane getDisplay() {
		return display;
	}
	
	private void calculateStats() {
		for (Item item : items) {
			Hashtable<Item.Stat, Integer> stats = item.STATS;
			Enumeration<Item.Stat> keys = stats.keys();
			Iterator<Item.Stat> keyReader = keys.asIterator();
			keyReader.forEachRemaining((stat) -> {
				Integer value = stats.get(stat);
				if (value == null) throw new IllegalStateException("The value was null for the key: " + stat + ", in the table: " + stats);
				addStat(stat, value);
			});
		}
	}
	
	private void addStat(Item.Stat stat, int value) {
		int previous = total.get(stat);
		total.replace(stat, previous + value);
	}
	
	private void resetTable() {
		Item.Stat[] stats = Item.Stat.values();
		
		for (int i = 0; i < stats.length; i++) {
			Item.Stat stat = stats[i];
			total.replace(stat, 0);
		}
	}
	
	private void updateTable() {
		Item.Stat[] stats = Item.Stat.values();
		for (int i = 0; i < stats.length; i++) {
			Item.Stat stat = stats[i];
			Integer value = total.get(stat);
			Label label = labels.get(stat);
			label.setText("" + value);
		}
	}
	
}
