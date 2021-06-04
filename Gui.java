import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke ;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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
		final double HEIGHT = 760;
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
		display.setAlignment(Pos.BOTTOM_CENTER);

		VBox rightPanel = new VBox(itemArea, display);

		pane.setRight(rightPanel);
	}

	private void addCenterPane() {
		Builder builder = new Builder();
		pane.setCenter(builder.getDisplay());
	}

}
