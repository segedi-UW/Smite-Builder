import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
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

	private static final BorderPane pane = new BorderPane();
	private static final ListView<Item> itemSelection = createItemSelection();
	private static final ItemDisplayer itemDisplay = new ItemDisplayer();
	private static final Builder builder = new Builder();
	private static final Menu filename = new Menu("Current File: New File"); 
	private static String filenameText = "New File";

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

		MultipleSelectionModel<Item> model = itemSelection.getSelectionModel();
		model.selectedItemProperty().addListener(itemDisplay);
		model.selectFirst();

		pane.setBackground(Background.EMPTY);
	}

	@Override
	public void start(Stage stage) {
		final String TITLE = "Smite Item Builder";
		stage.setTitle(TITLE);

		Scene scene = createScene();
		addTopPane();
		addRightPane();
		addCenterPane();

		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
	}

	public static Builder getBuilder() {
		return builder;
	}
	
	public static void setFilename(String name) {
		filenameText = name;
		filename.setText("Current File: " + name);
	}
	
	private static Scene createScene() {
		final String NAME = "MEDIUMTURQUOISE";
		final double ALPHA = 0.5;

		final double WIDTH = 1200;
		final double HEIGHT = 760;
		final Color COLOR = Color.web(NAME, ALPHA);

		return new Scene(pane, WIDTH, HEIGHT, COLOR);
	}

	private static void addTopPane() {
		MenuItem load = new MenuItem("Load");
		load.setOnAction(event -> {
			askToSave(() -> {
				FileHandler.promptLoadFile();
			});
		});
		MenuItem newFile = new MenuItem("New");
		newFile.setOnAction(event -> {
			askToSave(() -> {
				ArrayList<Builder.BuildBox> boxes = builder.getBuildBoxes();
				for (Builder.BuildBox box : boxes) {
					box.setItem(null);
				}
			});
		});
		MenuItem save = new MenuItem("Save");
		save.setOnAction(event -> {
			FileHandler.saveBuild();			
		});
		
		Menu file = new Menu("File");
		ObservableList<MenuItem> items = file.getItems();
		
		items.addAll(load, newFile);
		
		ObservableList<MenuItem> filenameItems = filename.getItems();
		filenameItems.add(save);
		
		MenuBar bar = new MenuBar(file, filename);
		
		pane.setTop(bar);
	}
	
	private static void askToSave(Runnable runnable) {
		String message = "Would you like to save " + filenameText + "?";
		confirm(message, () -> {
			FileHandler.saveBuild();
			runnable.run();
		}, () -> {
			runnable.run();
		});
	}
	
	private static void confirm(String message, Runnable yes, Runnable no) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		ObservableList<ButtonType> types = alert.getButtonTypes();
		types.clear();
		types.add(ButtonType.NO);
		types.add(ButtonType.YES);
		types.add(ButtonType.CANCEL);
		
		alert.setTitle("Confirmation");
		alert.setHeaderText("Save File First?");
		alert.setContentText(message);
			alert.showAndWait().ifPresent(button -> {
				if (button == ButtonType.YES)
					yes.run();
				else if (button == ButtonType.NO)
					no.run();
			});
	}
	
	private static void addRightPane() {
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

	private static void addCenterPane() {
		pane.setCenter(builder.getDisplay());
	}

}
