import javafx.animation.*;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameController
{
	private static final int GRID_HEIGHT = 4;
	private static final int GRID_WIDTH = 4;

	private final GameUI gameUI;
	private final Map<Location, Tile> tiles;

	private final List<Integer> boardWidth;
	private final List<Integer> boardHeight;

	private boolean tilesMoving = false;
	private boolean hasWon = false;

	private int score = 0;

	GameController()
	{
		this.tiles = new HashMap<>();
		initialiseBoard();
		this.boardWidth = IntStream.range(0, GRID_WIDTH).boxed().collect(Collectors.toList());
		this.boardHeight = IntStream.range(0, GRID_HEIGHT).boxed().collect(Collectors.toList());
		this.gameUI = new GameUI();
		this.addKeyHandlers();
		this.startGame();
	}

	private void initialiseBoard()
	{
		IntStream.range(0, GRID_HEIGHT).forEach(column ->
				IntStream.range(0, GRID_WIDTH).forEach(
						row -> tiles.put(new Location(column, row), null)
				)
		);
	}

	private void startGame()
	{
		//Generate 2 tiles to start
		this.generateRandomTile();
		this.generateRandomTile();
	}

	StackPane getGame()
	{
		return this.gameUI.getGameView();
	}

	private void generateRandomTile()
	{
		List<Location> emptyLocations = this.getEmptyLocations();
		Location newRandom = emptyLocations.get(new Random().nextInt(emptyLocations.size()));

		// 90% chance of 2
		int value = new Random().nextDouble() < 0.9d ? 2 : 4;
		Tile tile = new Tile(value, newRandom.getX(), newRandom.getY());

		this.tiles.put(tile.getLocation(), tile);
		this.gameUI.addTile(tile);

		animateAddingNewTile(tile).play();

		if  (isGameOver())
		{
			Button button = new Button("Try Again");
			button.setOnAction(event -> restartGame());

			this.gameUI.showOverlay("Game over", button);
		}
	}

	private boolean isGameOver()
	{
		return this.getEmptyLocations().size() == 0 && !movementsAvailable();
	}

	private List<Location> getEmptyLocations()
	{
		return this.tiles.entrySet()
				.stream()
				.filter(entry -> entry.getValue() == null)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	private void addKeyHandlers()
	{
		this.getGame().setOnKeyPressed(e ->
		{
			if (e.getCode().isArrowKey())
			{
				move(Direction.valueOf(e.getCode().name()));
			}
		});
	}

	private void move(Direction direction)
	{
		synchronized (tiles)
		{
			if (tilesMoving)
			{
				return;
			}
		}

		ParallelTransition parallelTransition = new ParallelTransition();
		List<Tile> tilesToRemove = new ArrayList<>();

		//Each move updates the score, but you can only use final variables in the lambda
		//I could use AtomicInteger, but  it's not necessary as this will never be parallel
		//Little trick to update the score using a final array
		final int[] moveScore = new int[1];

		int tilesMoved = this.boardNavigator(direction, (col, row) ->
				{
					Location currentLocation = new Location(col, row);
					Location furthestLocation = findFurthestCell(currentLocation, direction);

					if (!currentLocation.equals(furthestLocation))
					{
						Optional<Tile> optionalTile = getOptionalTile(furthestLocation);
						if (optionalTile.isPresent())
						{
							parallelTransition.getChildren()
									.add(animateMergeTiles(currentLocation, furthestLocation));
							tilesToRemove.add(this.tiles.get(currentLocation));
							this.tiles.replace(currentLocation, null);
							this.tiles.get(furthestLocation).merge();
							this.tiles.get(furthestLocation).toFront();
							moveScore[0] += this.tiles.get(furthestLocation).getValue();

							if (this.tiles.get(furthestLocation).getValue() == 2048 && !hasWon)
							{
								hasWon = true;

								Button keepGoing = new Button("Keep Going");
								keepGoing.setOnAction(e1 -> this.gameUI.removeOverlay());

								Button restart = new Button("Restart");
								restart.setOnAction(e2 -> restartGame());

								this.gameUI.showOverlay("You win!", keepGoing, restart);
							}
						}
						else
						{
							parallelTransition.getChildren().add(animateMoveTiles(currentLocation, furthestLocation));
							//Tile is empty, move
							Tile tile = this.tiles.replace(currentLocation, null);
							this.tiles.put(furthestLocation, tile);
						}
						return 1;
					}
					return 0;
				}
		);

		if (parallelTransition.getChildren().size() > 0)
		{
			parallelTransition.setOnFinished(e ->
			{
				score += moveScore[0];
				this.gameUI.updateScore(score);
				this.tiles.values().stream().filter(Objects::nonNull).forEach(Tile::unMerge);
				this.gameUI.getBoard().getChildren().removeAll(tilesToRemove);

				if (isGameOver())
				{
					Button button = new Button("Try Again");
					button.setOnAction(event -> restartGame());

					this.gameUI.showOverlay("Game over", button);
				}
				else if (this.getEmptyLocations().size() > 0 && tilesMoved > 0)
				{
					this.generateRandomTile();
				}

				synchronized (this.tiles)
				{
					this.tilesMoving = false;
				}
			});

			synchronized (this.tiles)
			{
				this.tilesMoving = true;
			}

			parallelTransition.play();
		}
	}

	private Location findFurthestCell(Location location, Direction direction)
	{
		Location furthestCell = location;
		Location offsetLocation = Location.offsetLocation(furthestCell, direction);

		while (isLocationValid(offsetLocation))
		{
			if (canCellMove(location, offsetLocation))
			{
				furthestCell = offsetLocation;
				offsetLocation = Location.offsetLocation(offsetLocation, direction);
			}
			else
			{
				break;
			}
		}

		return furthestCell;
	}

	private boolean isLocationValid(Location location)
	{
		return location.getX() >= 0 && location.getX() < GRID_WIDTH && location.getY() >= 0 && location.getY() < GRID_HEIGHT;
	}

	private boolean canCellMove(Location locationToMove, Location locationToMoveTo)
	{
		//Don't move empty tiles
		if (getOptionalTile(locationToMove).isEmpty())
		{
			return false;
		}

		if (getOptionalTile(locationToMoveTo).isEmpty())
		{
			return true;
		}

		return this.tiles.get(locationToMove).canMerge(this.tiles.get(locationToMoveTo));
	}

	private Optional<Tile> getOptionalTile(Location location)
	{
		return Optional.ofNullable(this.tiles.get(location));
	}

	private SequentialTransition animateMergeTiles(Location toMove, Location toMoveTo)
	{
		//First move the tile
		SequentialTransition sequentialTransition = new SequentialTransition();
		sequentialTransition.getChildren().add(animateMoveTiles(toMove, toMoveTo));

		//Scale the merge
		ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(80));
		scaleTransition.setNode(this.tiles.get(toMoveTo));
		scaleTransition.setAutoReverse(true);
		scaleTransition.setCycleCount(2);
		scaleTransition.setByX(0.1f);
		scaleTransition.setByY(0.1f);
		scaleTransition.setInterpolator(Interpolator.EASE_IN);

		sequentialTransition.getChildren().add(scaleTransition);

		return sequentialTransition;
	}

	private TranslateTransition animateMoveTiles(Location toMove, Location toMoveTo)
	{
		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(65));
		translateTransition.setNode(this.tiles.get(toMove));
		translateTransition.setByX((toMoveTo.getX() - toMove.getX()) * (GameUI.MIN_CELL_SIZE));
		translateTransition.setByY((toMoveTo.getY() - toMove.getY()) * (GameUI.MIN_CELL_SIZE));

		return translateTransition;
	}

	private ScaleTransition animateAddingNewTile(Tile tile)
	{
		ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(80), tile);
		scaleTransition.setFromX(0.5);
		scaleTransition.setFromY(0.5);
		scaleTransition.setToX(1);
		scaleTransition.setToY(1);
		scaleTransition.setCycleCount(1);

		return scaleTransition;
	}

	private boolean movementsAvailable()
	{
		int upMovesAvailable =
				this.boardNavigator(Direction.UP, (col, row) ->
				{
					Location currentLocation = new Location(col, row);
					Location furthestLocation = findFurthestCell(currentLocation, Direction.UP);

					return currentLocation.equals(furthestLocation) ? 0 : 1;
				});

		int leftMovesAvailable =
				this.boardNavigator(Direction.LEFT, (col, row) ->
				{
					Location currentLocation = new Location(col, row);
					Location furthestLocation = findFurthestCell(currentLocation, Direction.LEFT);

					return currentLocation.equals(furthestLocation) ? 0 : 1;
				});
		return upMovesAvailable + leftMovesAvailable > 0;
	}

	private int boardNavigator(Direction direction, IntBinaryOperator intBinaryOperator)
	{
		Comparator<Integer> comparator = direction == Direction.UP || direction == Direction.LEFT ?
				Comparator.naturalOrder() : Collections.reverseOrder();

		this.boardWidth.sort(comparator);
		this.boardHeight.sort(comparator);

		AtomicInteger movesAvailable = new AtomicInteger();

		this.boardWidth.forEach(col ->
				this.boardHeight.forEach(row ->
						movesAvailable.addAndGet(intBinaryOperator.applyAsInt(col, row))
				)
		);

		return movesAvailable.get();
	}

	void restartGame()
	{
		this.gameUI.resetBoard();
		this.hasWon = false;
		this.score = 0;
		this.tiles.clear();
		this.initialiseBoard();
		startGame();
	}
}
