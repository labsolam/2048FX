import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
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
		StackPane pane = gameController.getGame();

		Scene scene = new Scene(pane);
		scene.getStylesheets().addAll(getClass().getResource("2048.css").toExternalForm());

		primaryStage.setScene(scene);
		primaryStage.show();

		pane.requestFocus(); //Key events won't work unless focus is requested.
	}
}
