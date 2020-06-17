import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.stream.IntStream;

public class GameUI
{
	static final int MIN_CELL_SIZE = 128;
	static final int BORDER_CELL_SIZE = 14;

	/** Root of view containing all elements for the game. */
	private final VBox root = new VBox();
	private final Label scoreLabel = new Label("0"); //Default score
	private final Pane board = new Pane();

	private int width;
	private int height;

	GameUI(int height, int width)
	{
		this.height = height;
		this.width = width;
		this.createGame(width, height);
	}

	private void createGame(int width, int height)
	{
		this.root.getStyleClass().add("game-root");
		this.root.setMinHeight(200);
		this.root.setMinWidth(200);
		createScore();
		createBoard(width, height);
	}

	private void createScore()
	{
		Label scoreNameLabel = new Label("Score");
		scoreNameLabel.getStyleClass().add("score-label");
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
		board.getStyleClass().add("board");
		board.setMaxWidth(MIN_CELL_SIZE * width);
		board.setMaxHeight(MIN_CELL_SIZE * height);

		IntStream.range(0, height).forEach(row ->
				IntStream.range(0, width).forEach(column -> board.getChildren().add(this.createEmptyTile(row, column))));

		this.root.getChildren().add(board);

//		ChangeListener<Number> changeListener = (observableValue, oldDouble, newDouble) -> {
//			System.out.println("Height: " + this.root.getHeight() + " Width: " + this.root.getWidth());
//			this.root.setPadding(new Insets(height, 0, 0, width));
//		};
//		this.root.widthProperty().addListener(changeListener);
//		this.root.heightProperty().addListener(changeListener);
	}

	private Rectangle createEmptyTile(int x, int y)
	{
		Rectangle rectangle = new Rectangle(MIN_CELL_SIZE, MIN_CELL_SIZE);
		rectangle.getStyleClass().add("empty-tile");
		rectangle.setX(x * MIN_CELL_SIZE);
		rectangle.setY(y * MIN_CELL_SIZE);

		return rectangle;
	}

	VBox getGameView()
	{
		return this.root;
	}

	Pane getBoard()
	{
		return this.board;
	}

	void addTile(Tile tile)
	{
		this.board.getChildren().add(tile);
		tile.setMinSize(MIN_CELL_SIZE - BORDER_CELL_SIZE, MIN_CELL_SIZE - BORDER_CELL_SIZE);
		tile.setPrefSize(MIN_CELL_SIZE - BORDER_CELL_SIZE, MIN_CELL_SIZE - BORDER_CELL_SIZE);
		tile.setMaxSize(MIN_CELL_SIZE - BORDER_CELL_SIZE, MIN_CELL_SIZE - BORDER_CELL_SIZE);
		tile.setLayoutX(MIN_CELL_SIZE * tile.getLocation().getX() + BORDER_CELL_SIZE / 2f);
		tile.setLayoutY(MIN_CELL_SIZE * tile.getLocation().getY() + BORDER_CELL_SIZE / 2f);
	}
}
