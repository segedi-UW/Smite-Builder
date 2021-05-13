import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.control.CheckBox;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import java.util.function.Predicate;
import javafx.scene.layout.BorderStroke ;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderWidths;
import javafx.scene.control.MultipleSelectionModel;
import javafx.beans.property.ObjectProperty;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.image.ImageView;
import java.util.Hashtable;
import java.util.LinkedList;

public class Gui extends Application {

	private final BorderPane pane;
	private final ListView<Item> itemSelection;
	private final ItemDisplayer itemDisplay;

	public static void main(String[] args) {
		Application.launch();
	}

	public static Border createSolidBorder(String webName) {
		final BorderStrokeStyle STYLE = BorderStrokeStyle.SOLID;
		final BorderWidths WIDTH = BorderStroke.MEDIUM;
		final Color COLOR = Color.web(webName);
		final BorderStroke STROKE = new BorderStroke(COLOR, STYLE, null, WIDTH);

		return new Border(STROKE);
	}

	private static ListView<Item> createItemSelection() {
		ObservableList<Item> items = ItemUtil.getObservableItemList();

		final int HEIGHT = 435;

		ListView<Item> itemSelection = new ListView<>(items);
		itemSelection.setCellFactory(callback -> {
			return new ItemCell();
		});
		itemSelection.setPrefHeight(HEIGHT);

		return itemSelection;
	}

	public Gui() {
		itemSelection = createItemSelection();
		itemDisplay = new ItemDisplayer();

		MultipleSelectionModel<Item> model = itemSelection.getSelectionModel();
		model.selectedItemProperty().addListener(itemDisplay);
		model.selectFirst();

		pane = new BorderPane();
		pane.setBackground(Background.EMPTY);
	}

	@Override
	public void start(Stage stage) {
		final String TITLE = "Smite Item Builder";
		stage.setTitle(TITLE);

		Scene scene = createScene();
		addRightPane();
		addCenterPane();

		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
	}

	private Scene createScene() {
		final String NAME = "MEDIUMTURQUOISE";
		final double ALPHA = 0.5;

		final double WIDTH = 1200;
		final double HEIGHT = 700;
		final Color COLOR = Color.web(NAME, ALPHA);

		return new Scene(pane, WIDTH, HEIGHT, COLOR);
	}

	private void addRightPane() {
		FilterBox filterBox = new FilterBox(itemSelection);

		VBox filterDisplay = filterBox.getDisplay();

		Label itemsLabel = new Label("Items");

		VBox itemBox = new VBox(itemsLabel, itemSelection);

		HBox itemArea = new HBox(itemBox, filterDisplay);

		VBox display = itemDisplay.getDisplay();

		VBox rightPanel = new VBox(itemArea, display);

		pane.setRight(rightPanel);
	}


	private void addCenterPane() {
		Builder builder = new Builder();
		pane.setCenter(builder.getDisplay());
	}

}
