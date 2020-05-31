import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage)
	{
		GameController gameController = new GameController();
		VBox pane = gameController.getGame();

		ChangeListener<Number> changeListener = (observableValue, oldDouble, newDouble) -> {
			double height = 0;//pane.getHeight();
			double width = 0;//pane.getWidth();
			pane.setPadding(new Insets(height, 0, 0, width));
		};

		pane.widthProperty().addListener(changeListener);
		pane.heightProperty().addListener(changeListener);

		Scene scene = new Scene(pane);
		scene.getStylesheets().addAll(getClass().getResource("2048.css").toExternalForm());

		primaryStage.setMinHeight(200);
		primaryStage.setMinWidth(200);
		primaryStage.setScene(scene);
//		primaryStage.setMaximized(true);
		primaryStage.show();
	}
}
