import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
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
		GameController gameController = new GameController(4, 4);
		VBox pane = gameController.getGame();

		Scene scene = new Scene(pane);
		scene.getStylesheets().addAll(getClass().getResource("2048.css").toExternalForm());

		primaryStage.setScene(scene);
		primaryStage.show();
		pane.requestFocus(); //Key events won't work unless focus is requested.
	}
}
