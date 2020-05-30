import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.stream.IntStream;

public class GameUI
{
	private static final int CELL_SIZE = 128;

	/** Root of view containing all elements for the game. */
	private final VBox root = new VBox();
	private final Label scoreLabel = new Label("0"); //Default score

	GameUI(int height, int width)
	{
		this.createGame(width, height);
	}

	private void createGame(int width, int height)
	{
		this.root.getStyleClass().add("game-root");
		createScore();
		createBoard(width, height);
	}

	private void createScore()
	{
		Label scoreNameLabel = new Label("Score");
		scoreLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderStroke.THIN)));
		scoreLabel.setMinHeight(20);
		scoreLabel.setMinWidth(20);

		HBox hBox = new HBox();
		hBox.setAlignment(Pos.BASELINE_RIGHT);

		hBox.getChildren().addAll(scoreNameLabel, scoreLabel);

		this.root.getChildren().addAll(hBox);
	}

	private void createBoard(int width, int height)
	{
		Pane pane = new Pane();
		pane.getStyleClass().add("board");

		IntStream.range(0, 4).forEach(row ->
				IntStream.range(0, 4).forEach(column -> pane.getChildren().add(this.createEmptyTile(row, column))));

		this.root.getChildren().add(pane);
	}

	private Rectangle createEmptyTile(int x, int y)
	{
		Rectangle rectangle = new Rectangle(CELL_SIZE, CELL_SIZE);
		rectangle.getStyleClass().add("empty-tile");
		rectangle.setX(x * CELL_SIZE + 2);
		rectangle.setY(y * CELL_SIZE + 2);

		return rectangle;
	}

	private void showStartOverlay()
	{

	}

	VBox getGameView()
	{
		return this.root;
	}

}
