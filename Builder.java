import java.util.Hashtable;
import java.util.LinkedList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class Builder {

	private final VBox DISPLAY;
	private final ItemDisplayer displayer;
	private final TotalDisplay total;
	private final ObservableMap<Item, Hashtable<Item.Stat, Integer>> totals;

	private class BuildBox {

		private final Background HIGHLIGHT = createHighlight();
		private Item item;
		private ImageView display;
		private CheckBox build;
		private HBox cell;

		private Background createHighlight() {
			Color color = Color.DARKSEAGREEN;
			BackgroundFill fill = new BackgroundFill(color, null, null);
			Background highlight = new Background(fill);
			return highlight;
		}

		public BuildBox() {
			display = new ImageView();
			setupDisplay();

			build = new CheckBox("Build");
			setupBuild();
			Button clear = new Button("Clear");
			setupClear(clear);

			VBox actions = new VBox(build, clear);
			final double SPACING = 15.0;
			actions.setSpacing(SPACING);

			HBox displayWrapper = createImageBorder();

			cell = new HBox(displayWrapper, actions);
			setupCell();
		}

		private void setupDisplay() {
			final int SIZE = 60;
			display.setFitWidth(SIZE);
			display.setFitHeight(SIZE);
		}

		private void setupBuild() {
			final int BUILD_TOTAL = 6;
			build.setOnAction(event -> {
				if (build.isSelected()) {
					if (total.size() < BUILD_TOTAL)
					total.addItem(item);
				} else {
					total.removeItem(item);
				}
			});
		}

		private void setupClear(Button clear) {
			clear.setOnAction(event -> {
				if (build.isSelected())
					build.fire();
				setItem(null);
			});
		}

		private void setupCell() {
			final double SPACING = 10.0;
			cell.setSpacing(SPACING);
			setupDragDetected();
			setupDragOver();
			setupDragEntered();
			setupDragExited();
			setupDragDropped();
			setupDragDone();
			setupMouseClicked();
		}

		private void setupDragDetected() {
			cell.setOnDragDetected(event -> {
				if (item != null) {
					Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);

					/* put a string on dragboard */
					ClipboardContent content = new ClipboardContent();
					content.putString(item.NAME);
					db.setContent(content);
				}
				
				event.consume();
			});
		}
		

		private void setupDragOver() {
			cell.setOnDragOver(event -> {
				if (event.getDragboard().hasString())
					event.acceptTransferModes(TransferMode.ANY);
				event.consume();
			});
		}

		private void setupDragEntered() {
			cell.setOnDragEntered(event -> {
				if (event.getDragboard().hasString())
					cell.setBackground(HIGHLIGHT);
				event.consume();
			});
		}

		private void setupDragExited() {
			cell.setOnDragExited(event -> {
				cell.setBackground(null);
				event.consume();
			});
		}

		private void setupDragDropped() {
			cell.setOnDragDropped(event -> {
				boolean success = false;
				Dragboard db = event.getDragboard();
				if (db.hasString()) {
					String itemName = db.getString();
					Item item = ItemUtil.lookupItem(itemName);
					if (!totals.containsKey(item) || event.getTransferMode() == TransferMode.MOVE) {
						if (this.item != null) {
							ClipboardContent content = new ClipboardContent();
							content.putString(this.item.NAME);
							db.setContent(content);
						} else {
							db.setContent(null);
						}
						
						if (build.isSelected())
							build.fire();
						setItem(item);
						success = true;
					}
				}
				event.setDropCompleted(success);
				event.consume();
			});
		}
		

		private void setupDragDone() {
			cell.setOnDragDone(event -> {
				if (event.getTransferMode() == TransferMode.MOVE) {
					Dragboard db  = event.getDragboard();
					if (build.isSelected())
						build.fire();
					
					if (db.hasString()) {
						String itemName = db.getString();
						Item item = ItemUtil.lookupItem(itemName);
						
						setItem(item);
					} else {
						setItem(null);
					}
				}
				event.consume();
			});

		}
		
		
		private void setupMouseClicked() {
			cell.setOnMouseClicked(event -> {
				displayer.setItem(item);
			});
		}

		private HBox createImageBorder() {
			HBox borderBox = new HBox(display);

			Border border = Gui.createSolidBorder("GOLDENROD");
			borderBox.setBorder(border);

			return borderBox;
		}
		

		private void setItem(Item item) {
			if (this.item != null) {
				totals.remove(this.item);
				build.setSelected(false);
			}

			if (item != null) {
				totals.put(item, item.STATS);
				
				displayer.setItem(item);

				Image displayImage = ItemUtil.lookupImage(item);
				display.setImage(displayImage);
			} else
				display.setImage(null);
			
			this.item = item;
			build.setSelected(false);
		}
		

		public HBox getDisplay() {
			return cell;
		}
		

	}

	private class TotalDisplay {

		private VBox display;
		private StatTable statsTable;
		private ObservableList<Item> build;

		private class BuildCell extends ListCell<Item> {
			@Override
			public void updateItem(Item item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null) {
					Image image = ItemUtil.lookupImage(item);
					ImageView imageView = new ImageView(image);
					final int SIZE = 40;
					imageView.setFitHeight(SIZE);
					imageView.setFitWidth(SIZE);

					HBox cell = new HBox(imageView);
					cell.setAlignment(Pos.CENTER_LEFT);
					setGraphic(cell);
				} else
					setGraphic(null);
			}
		}

		public TotalDisplay() {
			LinkedList<Item> items = new LinkedList<>();
			build = FXCollections.observableList(items);

			statsTable = new StatTable(build);
			
			ListView<Item> buildView = new ListView<>(build);
			setupBuildView(buildView);

			Label statsLabel = createBoldLabel("Total Stats");
			Label buildLabel = createBoldLabel("Build");

			final int LEFT = 40;

			display = new VBox(statsLabel, statsTable.getDisplay(), buildLabel, buildView);
			VBox.setMargin(buildLabel, new Insets(0,0,0,LEFT));
			VBox.setMargin(buildView, new Insets(0,0,0,LEFT));
			VBox.setMargin(statsLabel, new Insets(0,0,0,LEFT));
			VBox.setMargin(statsTable.getDisplay(), new Insets(0,0,0,LEFT));
		}

		private void setupBuildView(ListView<Item> buildView) {
			final int WIDTH = 330;
			final int HEIGHT = 300;

			buildView.setOrientation(Orientation.HORIZONTAL);
			buildView.setPrefWidth(WIDTH);
			buildView.setPrefHeight(HEIGHT);
			buildView.setCellFactory(callback -> {
				return new BuildCell();
			});
		}


		public void addItem(Item item) {
			if (item != null)
				build.add(item);
		}

		public void removeItem(Item item) {
			if (item != null)
				build.remove(item);
		}

		public VBox getDisplay() {
			return display;
		}

		public int size() {
			return build.size();
		}
	}

	public Builder() {
		displayer = new ItemDisplayer();
		total = new TotalDisplay();
		Hashtable<Item, Hashtable<Item.Stat, Integer>> table = new Hashtable<>();
		totals = FXCollections.observableMap(table);

		VBox starter = createBuilderRow("Starter");
		VBox core = createBuilderRow("Core");
		VBox offense = createBuilderRow("Offense");
		VBox defense = createBuilderRow("Defense");
		VBox utility = createBuilderRow("Utility");

		HBox display = new HBox(displayer.getDisplay(), total.getDisplay());

		DISPLAY = new VBox(starter, core, offense, defense, utility, display);
	}

	private VBox createBuilderRow(String text) {
		final int LEFT = 5;
		final int BOXES = 4;
		final double SPACING = 80.0;

		HBox row = new HBox();
		row.setSpacing(SPACING);

		ObservableList<Node> children = row.getChildren();;

		for (int i = 0; i < BOXES; i++) {
			BuildBox box = new BuildBox();
			children.add(box.getDisplay());
		}

		Label label = createBoldLabel(text);

		Color color = Color.web("SLATEGRAY", 0.3);
		BackgroundFill fill = new BackgroundFill(color, null, null);
		Background background = new Background(fill);

		VBox labeledRow = new VBox(label, row);

		labeledRow.setBackground(background);

		final Insets inset = new Insets(0, 0, 0, LEFT);

		VBox.setMargin(label, inset);
		VBox.setMargin(row, inset);

		Border border = Gui.createSolidBorder("IVORY");
		labeledRow.setBorder(border);

		return labeledRow;
	}
	
	private Label createBoldLabel(String text) {
		Label label = new Label(text);
		label.setStyle("-fx-font-weight: bolder");
		return label;
	}

	public VBox getDisplay() {
		return DISPLAY;
	}
}
