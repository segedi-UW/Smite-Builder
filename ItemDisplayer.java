import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.image.Image;
import java.util.LinkedList;
import java.util.Hashtable;

public class ItemDisplayer implements ChangeListener<Item> {

	private Label name;
	private ListView<String> stats;
	private ImageView imageView;
	private TextArea passive;

	public ItemDisplayer() {
		final int STAT_HEIGHT = 120;
		final int PASSIVE_HEIGHT = 100;
		final double WIDTH = 375.0;
		final String FONT_SIZE = "20";
		final String FONT_WEIGHT = "bold";
		name = new Label();
		name.setStyle("-fx-font-size: " + FONT_SIZE + "; -fx-font-weight: " + FONT_WEIGHT);
		stats = new ListView<>();
		stats.setPrefHeight(STAT_HEIGHT);
		imageView = new ImageView();
		passive = new TextArea();
		passive.setPrefWidth(WIDTH);
		passive.setPrefHeight(PASSIVE_HEIGHT);
		passive.setEditable(false);
		passive.setWrapText(true);
	}

	public void setItem(Item item) {
		if (item == null)
			return;

		name.setText(item.NAME);
		setStats(item);
		passive.setText(item.PASSIVE);

		Image image = ItemUtil.lookupImage(item);
		imageView.setImage(image);
	}

	private void setStats(Item item) {
		ObservableList<String> sorted = getSortedObservableStats(item);
		stats.setItems(sorted);
	}

	private ObservableList<String> getSortedObservableStats(Item item) {
		LinkedList<String> stats = new LinkedList<>();
		Hashtable<Item.Stat, Integer> statTable = item.STATS;

		Item.Stat[] allStats = Item.Stat.values();

		for (Item.Stat stat : allStats) {
			if (statTable.containsKey(stat)) {
				String text = stat.NAME + ": " + statTable.get(stat);
				stats.add(text);
			}
		}

		return FXCollections.observableList(stats);
	}

	public VBox getDisplay() {
		final int RIGHT = 5;
		final int TOP = 5;

		HBox statsImage = new HBox(stats, imageView);

		VBox display = new VBox(name, statsImage, passive);
		VBox.setMargin(name, new Insets(TOP, 0, 0, 0));
		VBox.setMargin(passive, new Insets(0, RIGHT, 0, 0));
		display.setAlignment(Pos.CENTER);
		return display;
	}

	@Override
	public void changed(ObservableValue<? extends Item> observable, Item oldValue, Item newValue) {
		if (newValue != null)
			setItem(newValue);
	}
}
