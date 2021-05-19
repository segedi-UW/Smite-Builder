import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.input.ClipboardContent;

public class ItemCell extends ListCell<Item> {

	@Override
	public void updateItem(Item item, boolean empty) {
		super.updateItem(item, empty);
		if (item != null) {
			Image image = ItemUtil.lookupImage(item);
			ImageView imageView = new ImageView(image);
			final int SIZE = 40;
			imageView.setFitHeight(SIZE);
			imageView.setFitWidth(SIZE);

			Label label = new Label(item.toString());
			HBox cell = new HBox(imageView, label);
			final int LEFT = 5;
			HBox.setMargin(label, new Insets(0, 0, 0, LEFT));
			cell.setAlignment(Pos.CENTER_LEFT);
			setGraphic(cell);
			setOnDragDetected(event -> {
				Dragboard db = startDragAndDrop(TransferMode.COPY);

				/* put a string on dragboard */
				ClipboardContent content = new ClipboardContent();
				content.putString(item.NAME);
				db.setContent(content);

				event.consume();
			});
		} else
			setGraphic(null);
	}
}
