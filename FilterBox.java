import javafx.scene.layout.VBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.Node;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import java.util.LinkedList;

public class FilterBox {

	private final LinkedList<Item.Stat> filters;
	private boolean filterStarters;
	private VBox display;
	private ListView<Item> selection;

	public FilterBox(ListView<Item> selection) {
		this.selection = selection;
		filters = new LinkedList<>();
		filterStarters = false;
		display = createFilterVBox();
	}

	public VBox getDisplay() {
		return display;
	}

	private VBox createFilterVBox() {
		VBox vbox = new VBox();

		Label filtersLabel = new Label("Filters");

		ObservableList<Node> children = vbox.getChildren();
		children.add(filtersLabel);

		Item.Stat[] stats = Item.Stat.values();

		for (int i = 0; i < stats.length; i++) {
			Item.Stat stat = stats[i];
			CheckBox filter = createFilterCheckBox(stat);
			children.add(filter);
		}

		String name = "Starter";
		String starterTooltip = "Filters Items that are starter items.";
		CheckBox starter = createCheckBox(name, starterTooltip);
		setStarterAction(starter);

		children.add(starter);

		return vbox;
	}

	private CheckBox createCheckBox(String name, String tooltip) {
		CheckBox check = new CheckBox(name);
		check.setTooltip(new Tooltip(tooltip));
		return check;
	}

	private CheckBox createFilterCheckBox(Item.Stat stat) {
		String tooltip = "Filters Items that have " + stat.NAME;
		return createFilterCheckBox(stat, tooltip);
	}

	private CheckBox createFilterCheckBox(Item.Stat stat, String tooltip) {
		CheckBox filter = new CheckBox(stat.ALIAS);
		setFilterAction(filter, stat);
		filter.setTooltip(new Tooltip(tooltip));
		return filter;
	}

	private void setFilterAction(CheckBox filter, Item.Stat stat) {
			filter.setOnAction(event -> {
				if (filter.isSelected())
					filters.add(stat);
				else
					filters.remove(stat);
				updateFilter();
			});
	}

	private void setStarterAction(CheckBox starter) {
		starter.setOnAction(event -> {
			filterStarters = !filterStarters;
			updateFilter();
		});
	}

	private void updateFilter() {
		ObservableList<Item> items = ItemUtil.getObservableItemList();
		if (filters.isEmpty() && !filterStarters) {
			selection.setItems(items);
		} else {
			FilteredList<Item> filter = items.filtered((item) -> {
				for (Item.Stat stat : filters) {
					if (!item.STATS.containsKey(stat))
						return false;
				}
				if (filterStarters && !item.IS_STARTER)
					return false;
				return true;
			});
			selection.setItems(filter);
		}
	}
}
