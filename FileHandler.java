import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Stream;

import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;

public class FileHandler {
	
	private static final String FILE_EXTENSION = ".sbf";

	public static void promptLoadFile() {
		FileChooser chooser = getDefaultFileChooser();
		File file = chooser.showOpenDialog(getDefaultWindow());
		if (file != null) {
			loadFile(file);
		}
	}
	
	private static void loadFile(File file) {
		try (Stream<String> stream = Files.lines(file.toPath())) {
			Builder builder = Gui.getBuilder();
			ArrayList<Builder.BuildBox> boxes = builder.getBuildBoxes();
			ArrayList<Item> items = new ArrayList<>();
			stream.forEach(line -> {
				if (line.isBlank())
					items.add(null);
				Item item = ItemUtil.lookupItem(line);
				items.add(item);
			});
			for (int i = 0; i < boxes.size(); i++) {
				Builder.BuildBox box = boxes.get(i);
				Item item = items.get(i);
				if (item != null)
					box.setItem(item);
			}
			Gui.setFilename(file.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveBuild() {
		Builder builder = Gui.getBuilder();
		ArrayList<Builder.BuildBox> boxes = builder.getBuildBoxes();
		FileChooser chooser = getDefaultFileChooser();
		File file = chooser.showSaveDialog(getDefaultWindow());
		if (file != null) {
			writeBuildToFile(boxes, file);
			Gui.setFilename(file.getName());
		}
	}
	
	private static void writeBuildToFile(ArrayList<Builder.BuildBox> boxes, File file) {
		try (FileWriter writer = new FileWriter(file)){
			for (Builder.BuildBox box : boxes) {
				Item item = box.getItem();
				String line = (item != null ? item.NAME : "") + "\n";
				writer.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static FileChooser getDefaultFileChooser() {
		final String title = "Smite Builder File Selection";
		FileChooser chooser = new FileChooser();
		chooser.setTitle(title);
		ObservableList<ExtensionFilter> filters = chooser.getExtensionFilters();
		filters.add(new ExtensionFilter("Smite Builder Files", "*" + FILE_EXTENSION));
		return chooser;
	}
	
	private static Window getDefaultWindow() {
		ObservableList<Window> windows = Stage.getWindows();
		return windows.get(0);
	}
}