import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.stream.IntStream;

public class GameUI
{
	static final int MIN_CELL_SIZE = 128;
	static final int BORDER_CELL_SIZE = 14;

	/** Root of view containing all elements for the game. */
	private final StackPane root = new StackPane();
	private final VBox gameRoot = new VBox();
	private final Label scoreLabel = new Label("0"); //Default score
	private final Pane board = new Pane();
	VBox overlay = new VBox();

	GameUI()
	{
		this.createGame();
	}

	private void createGame()
	{
		this.root.getStyleClass().add("game-root");
		this.root.setMinHeight(200);
		this.root.setMinWidth(200);
		this.root.getChildren().add(gameRoot);
		this.root.setAlignment(Pos.CENTER);
		this.gameRoot.setAlignment(Pos.CENTER);
		createScore();
		createBoard();
	}

	private void createScore()
	{
		Label scoreNameLabel = new Label("Score");
		scoreNameLabel.getStyleClass().add("score-label");
		scoreNameLabel.setPadding(new Insets(0, 10, 0, 0));
		scoreLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderStroke.THIN)));
		scoreLabel.setMinHeight(20);
		scoreLabel.setMinWidth(20);
		scoreLabel.setAlignment(Pos.CENTER);
		scoreLabel.setPadding(new Insets(2, 10, 2, 10));
		scoreLabel.getStyleClass().add("score-label");

		HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER);
		hBox.setPadding(new Insets(20));

		hBox.getChildren().addAll(scoreNameLabel, scoreLabel);

		this.gameRoot.getChildren().addAll(hBox);
	}

	private void createBoard()
	{
		board.getStyleClass().add("board");
		board.setMaxWidth(MIN_CELL_SIZE * 4);
		board.setMaxHeight(MIN_CELL_SIZE * 4);

		IntStream.range(0, 4).forEach(row ->
				IntStream.range(0, 4).forEach(column -> board.getChildren().add(this.createEmptyTile(row, column))));

		this.gameRoot.getChildren().add(board);
	}

	private Rectangle createEmptyTile(int x, int y)
	{
		Rectangle rectangle = new Rectangle(MIN_CELL_SIZE, MIN_CELL_SIZE);
		rectangle.getStyleClass().add("empty-tile");
		rectangle.setX(x * MIN_CELL_SIZE);
		rectangle.setY(y * MIN_CELL_SIZE);

		return rectangle;
	}

	StackPane getGameView()
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

	public void showOverlay(String label, Button... buttons)
	{
		overlay.setBackground(new Background(new BackgroundFill(Color.web("#f9f6f2", 0.5), null, null)));

		overlay.getChildren().addAll(new Label(label));
		overlay.getChildren().addAll(Arrays.asList(buttons));
		overlay.setAlignment(Pos.CENTER);

		this.root.getChildren().add(overlay);
		overlay.toFront();
	}

	public void resetBoard()
	{
		removeOverlay();
		this.board.getChildren().removeIf(e -> e instanceof Tile);
		this.updateScore(0);
	}

	public void removeOverlay()
	{
		this.root.getChildren().remove(overlay);
		this.overlay.getChildren().clear();
		this.root.requestFocus();
	}

	public void updateScore(int score)
	{
		this.scoreLabel.setText(String.valueOf(score));
	}
}
